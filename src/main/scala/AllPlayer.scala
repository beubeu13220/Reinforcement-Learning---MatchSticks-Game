package rf.examples

object AllPlayer {

  /**
    * Case class Transition allow us to save the transition between state
    * Used it after when we want train a player with the value function
    * @param state :  The next state reach after play action
    * @param statePrev : The current state when play
    * @param action : the number of sticks picked
    * @param reward
    */
  case class Transition(state: java.lang.Integer, statePrev: Int, action: Int, reward: Int)

  /**
    * Case class Stats is used to store the game statistics
    * We used it in the Player object
    * @param nbWin
    * @param nbLoose
    * @param nbStart
    */
  case class Stats(nbWin: Int, nbLoose: Int, nbStart: Int) {
    def add(that: Stats): Stats = that match {
      case Stats(1, 0, 0) => Stats(nbWin + 1, nbLoose, nbStart)
      case Stats(0, 1, 0) => Stats(nbWin, nbLoose + 1, nbStart)
      case Stats(0, 0, 1) => Stats(nbWin, nbLoose, nbStart + 1)
      case Stats(_, _, _) => throw new IllegalArgumentException("Stats object hasn't the right format")
    }
  }

  /**
    * Helper function Transition
    * Used to store a Transition in a List Transition
    * At end of a game, we have a list Transition with each Transition is a step game
    * @param stateList : Transition List
    * @param reward : Tuple return by game object after play, which allow us to fill the Transition object
    * @return A new Transition List
    */
  def addTransition(stateList: List[Transition], reward: (Int, Int, Int)): List[Transition] = Transition(null, reward._1, reward._2, reward._3) :: stateList

  /**
    * We set the next step on the head of the Transition List
    * When we create the head we don't know the next step
    * So we must set it after and we used this function
    * @param stateList : Transition list
    * @param state : The next step, we want set it
    * @return Transition List
    */
  def setPrevState(stateList: List[Transition], state: java.lang.Integer): List[Transition] = stateList match {
    case Transition(null, statePrev, action, reward) :: xs => if(state==null) Transition(null, statePrev, action, 1) :: xs else Transition(state, statePrev, action, reward) :: xs
    case Transition(_, _, _, _) :: xs => stateList
    case Nil => Nil
  }

  /**
    * Abstract class Player
    * @param namePlayer : String name player
    * @param stateList : Value function which provide by the function getStateListGame, and uptdate durind the training step
    * @param statsList : a List with statistics like the number of game won, loose and starting
    * @param permittedAction : values list which contains the number of sticks that the player will able to pick
    */
  abstract class Player(val namePlayer: String, val stateList: Map[Int, Double], val statsList: Stats, permittedAction: List[Int]) {

    def play(currentState: Int): Int

    def isHuman: Boolean

    def setStats(newStats: Stats): Player

    def train(currentPlayerListState: List[Transition], valueFunction: (Int => Double, Transition) => (Int, Double)): Player

    def stateToString = {
      println("Fonction Value " + namePlayer + " : ")
      stateList.toList.sortBy(_._1).foreach(x => println("State : " + x._1 + " => " + x._2))
    }

    def statsToString = {
      println("Stats game " + namePlayer + " : ")
      println("Win : " + statsList.nbWin + " | Loose : " + statsList.nbLoose + " | Start : " + statsList.nbStart)
    }

  }


  /**
    * Stick player is an implementation of robot player
    * It can play randomly or according to its value function
    * To play with its value function (stateList) we must training it
    * @param namePlayer : String name player
    * @param stateList : Value function which provide by the function getStateListGame, and update during the training step
    * @param esp : epsilon value we used it in the epsilon greedy step
    * @param permittedAction : values list which contains the number of stick that the player will able to pick
    * @param statsList : a List with statistics like the number of game win, loose and starting
    */
  class StickPlayer(namePlayer: String, stateList: Map[Int, Double], val esp: Double, permittedAction: List[Int], statsList: Stats) extends Player(namePlayer, stateList, statsList, permittedAction) {

    /**
      * Create an object StickPlayer without the statsList parameter
      * @param namePlayer
      * @param stateList
      * @param esp
      * @param permittedAction
      * @return
      */
    def this(namePlayer: String, stateList: Map[Int, Double], esp: Double, permittedAction: List[Int]) = this(namePlayer, stateList, esp, permittedAction, Stats(0, 0, 0))

    def isHuman: Boolean = false

    /**
      * The function play allow to the players to choose the sticks number to pick by the current number of sticks
      * The player can plays randomly if a random number is greater than epsilon
      * Or he plays with the greedyStep function
      * @param currentState : Remaining sticks in the game
      * @return Number of sticks to pick
      */
    def play(currentState: Int): Int = {
      if (scala.util.Random.nextFloat > esp) greedyStep(currentState)
      else permittedAction(scala.util.Random.nextInt(permittedAction.length))
    }

    /**
      * Among the next state that we can reach by the rules, we choose an action which minimize the value function
      * @param currentState : Remaining sticks in the game
      * @return Number of sticks to pick
      */
    private def greedyStep(currentState: Int): Int = {
      if (currentState <= 1) 1
      else currentState - stateList.filterKeys(permittedAction.map(currentState - _).contains(_)).minBy(_._2)._1
    }

    /**
      * Allow us to save some statistic in the player memory
      * @param newStats : New Stats (nbWin, nbLoose, nbStart)
      * @return A new Stick Player
      */
    def setStats(newStats: Stats): StickPlayer = new StickPlayer(
      this.namePlayer,
      this.stateList,
      this.esp,
      this.permittedAction,
      this.statsList add newStats
    )

    /**
      * Update the value function Player
      * @param currentPlayerListState : Value function values get by a game
      * @param valueFunction : The value function which define the computing rules
      * @return A new stick player with the new stateList property
      */
    def train(currentPlayerListState: List[Transition], valueFunction: (Int => Double, Transition) => (Int, Double)): StickPlayer = new StickPlayer(
      this.namePlayer,
      currentPlayerListState.foldLeft(this.stateList){
        (listTransition,transition) => listTransition ++ Map(valueFunction(listTransition,transition))
      },
      this.esp,
      this.permittedAction,
      this.statsList
    )

    /**
      * Set the new epsilon value
      * @param rateEps : Factor that multiply the current epsilon value
      * @return A new stick player with the new epsilon property
      */
    def setEsp(rateEps : Double) : StickPlayer = new StickPlayer(
      this.namePlayer,
      this.stateList,
      (this.esp * rateEps) max 0.05,
      this.permittedAction,
      this.statsList
    )


  }

  /**
    * An object which allow us to play against an robot player
    * @param namePlayer : String name player
    * @param stateList : Value function which provide by the function getStateListGame, and update during the training step
    * @param permittedAction : values list which contains the number of sticks that the player will able to pick
    * @param statsList : a List with statistics like the number of game win, loose and starting
    */
  class HumanStickPlay(namePlayer: String, stateList: Map[Int, Double], permittedAction: List[Int], statsList: Stats) extends Player(namePlayer, stateList, statsList, permittedAction) {

    def this(namePlayer: String, stateList: Map[Int, Double], permittedAction: List[Int]) = this(namePlayer, stateList, permittedAction, Stats(0, 0, 0))

    /**
      * Ask to human player to choose a stick number
      * @param currentState Remaining sticks in the game
      * @return Number of sticks to pick
      */
    def play(currentState: Int): Int = {
      println("Play an action in the permitted action " + permittedAction.toString)
      scala.io.StdIn.readInt()
    }

    /**
      * Nothing to do
      * Only return the current player
      * The human player can't be training
     */
    def train(currentPlayerListState: List[Transition], valueFunction: (Int => Double, Transition) => (Int, Double)): HumanStickPlay = this

    def setStats(newStats: Stats): HumanStickPlay = new HumanStickPlay(
      this.namePlayer,
      this.stateList,
      this.permittedAction,
      this.statsList add newStats
    )

    def isHuman: Boolean = true


  }


}

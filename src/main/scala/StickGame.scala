package rf.examples


/**
  * The class StickGame allow us to create a game object
  * @param nbStick : The number of sticks in the game
  * @param permittedAction : The list of permitted action, so with List(1,2,3) we only pick 1 or 2 or 3 sticks at each step
  */
class StickGame(val nbStick: Int, val permittedAction: List[Int]) {

  // Mutable variable isn't a good solution, it would be better to use immutable variable
  var currentNbStick = nbStick

  def isTerminate: Boolean = if (currentNbStick <= 0) true else false

  def display = println("|" * currentNbStick)

  /**
    * Side effect function
    * We pick some sticks in the current game and update the currentNbStick variable
    * Also we return a tuple
    * @param actionValue : Int value in the permitted action list that the player want to pick
    * @return (Current sticks number in the game, sticks number picked, reward with the action)
    */
  def action(actionValue: Int): (java.lang.Integer, Int, Int) = {
    require(permittedAction.contains(actionValue), "The action value not contains in the permitted actions")

    currentNbStick -= actionValue

    if (currentNbStick <= 0) (null, actionValue, -1)
    else (currentNbStick, actionValue, 0)


  }

  /**
    * Helper function provide us the init value function that match with this game
    * A game with twelve sticks must give a value function with 12 steps
    * @param initValue : default int value of the value function
    * @return  Empty Value Function
    */
  def getStateListGame(initValue: Double = 0.0): Map[Int, Double] = {
    (1 to nbStick) map { x => x -> initValue } toMap
  }

  /**
    * Helper function
    * If we want to play more than one game with the same configuration
    * we can reset the game, especially the mutable variable currentNbStick
   */
  def resetGame = new StickGame(this.nbStick, this.permittedAction)


}

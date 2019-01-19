package rf.examples

import rf.examples.AllPlayer._

object Play {

  //learning rate
  val alpha: Double = 0.001

  /**
    * Function used to update the function value
    * V(St) = V(St) + alpha * ( V(St+1) - V(St) )
    * @param valueDict : Function value dict with old values
    * @param valueTransition : New values get by a game
    * @return A tuple State and new V(s) value
    */
  def valueFunctionPlay(valueDict: Int => Double, valueTransition: Transition): (Int, Double) = valueTransition match {
    case Transition(null, statePrev, _, reward) => (statePrev, valueDict(statePrev) + alpha * (reward - valueDict(statePrev)))
    case Transition(state, statePrev, _, _) => (statePrev, valueDict(statePrev) + alpha * (valueDict(state) - valueDict(statePrev)))
  }

  /**
    * A function allow us to play game with two robots players or with a robot player against human player
    * Also, the function provides option to run the training step after the game if we play with robot player
    * The function is able to play only one game
    * @param game : a Game instance
    * @param player1 : Player (StickPlayer/Human Player)
    * @param player2 : Player (StickPlayer/Human Player)
    * @param train : Boolean, True if we wan train the StickPlayer. Warning, if set it to True and a player is Human we raise an exception
    * @param verbose : If we want to print all step of game
    * @return Player1 and Player2 + their Transition List according to the party
    */
  def runPlay(game: StickGame, player1: Player, player2: Player, train: Boolean, verbose: Boolean): (Player, List[Transition], Player, List[Transition]) = {

    def runPlayAcc(currentPlayer: Player, currentPlayerListState: List[Transition], nextPlayer: Player, nextPlayerListState: List[Transition]): (Player, List[Transition], Player, List[Transition]) = {

      if (game.isTerminate) {

        if (verbose) println("Le player " + nextPlayer.namePlayer + " loose the game")

        if (train) {
          (currentPlayer.setStats(Stats(1, 0, 0)).train(currentPlayerListState, valueFunctionPlay),
            currentPlayerListState,
            nextPlayer.setStats(Stats(0, 1, 0)).train(nextPlayerListState, valueFunctionPlay),
            nextPlayerListState
          )
        } else {
          (currentPlayer.setStats(Stats(1, 0, 0)), currentPlayerListState, nextPlayer.setStats(Stats(0, 1, 0)), nextPlayerListState)
        }
      }
      else {

        if (verbose) game.display

        //Player take an action by the current action
        val currentNbStickBeforePlay = game.currentNbStick
        val currentAction = currentPlayer.play(currentNbStickBeforePlay)

        if (verbose) println("Le player " + currentPlayer.namePlayer + " play the action : " + currentAction)
        //Update the game
        val transitionAction = game.action(currentAction)

        runPlayAcc(nextPlayer,
          setPrevState(nextPlayerListState, transitionAction._1),
          currentPlayer,
          addTransition(currentPlayerListState, (currentNbStickBeforePlay, transitionAction._2, transitionAction._3))
        )
      }

    }


    runPlayAcc(player1.setStats(Stats(0, 0, 1)), Nil, player2, Nil)
  }

  /**
    * A Wrapper function on the runPlay function
    * Allow us to player lot of games, not only one
    * We used the function to simulate some experiences
    * @param nbIteration : Number of playing games
    * @param game : a Game instance
    * @param player1 : StickPlayer
    * @param player2 : StickPlayer
    * @param learningRate : A value to decrease the epsilon value, so decrease the exploration step
    * @param train : Boolean
    * @param verbose : If we want to print all step of game
    * @return
    */
  def iterationPlay(nbIteration: Int, game: StickGame, player1: StickPlayer, player2: StickPlayer, learningRate: Double, train: Boolean, verbose: Boolean): (StickPlayer, List[List[Transition]], StickPlayer, List[List[Transition]]) = {

    def wrapSetEpsRunPlay(ruleIteration: Boolean, p1: StickPlayer, p2: StickPlayer): (StickPlayer, StickPlayer) = {
      if (ruleIteration) (p1, p2)
      else (p1.setEsp(learningRate), p2.setEsp(learningRate))
    }

    def iteration(accIteration: Int, g: StickGame, p1: StickPlayer, p1History: List[List[Transition]], p2: StickPlayer, p2History: List[List[Transition]], random: Double = scala.util.Random.nextDouble): (StickPlayer, List[List[Transition]], StickPlayer, List[List[Transition]]) = {

      if (accIteration > nbIteration) (p1, p1History, p2, p2History)
      else {
        val (winPlayer: StickPlayer, winTransion, loosePlayer: StickPlayer, looseTransition) = {
          val (wrapP1, wrapP2): (StickPlayer, StickPlayer) = if (train) wrapSetEpsRunPlay(accIteration % 10 == 0, p1, p2) else (p1, p2)
          if (random < 0.5) runPlay(g, wrapP1, wrapP2, train, verbose)
          else runPlay(g, wrapP2, wrapP1, train, verbose)
        }
        iteration(accIteration + 1, g.resetGame, winPlayer, winTransion :: p1History, loosePlayer, looseTransition :: p2History)
      }

    }

    iteration(1, game, player1, Nil, player2, Nil)

  }


  def main(args: Array[String]): Unit = {

    ///java -jar reinforcementLearning-0.1.jar rf.examples.Play
    val game1 = new StickGame(12, List(1, 2, 3))

    val randomPlayer = new StickPlayer("PlayerRandom", game1.getStateListGame(), 1, game1.permittedAction)

    val humanPlayer = new HumanStickPlay("humanPlayer", game1.getStateListGame(), game1.permittedAction)

    //// training experience : 1 000 two players experiences with 10 000 iterations at each training
    /// On the 1 000 experiences we get the same state history (1,5,9)
    //var i = 0
    //val listL = scala.collection.mutable.ArrayBuffer.empty[List[Int]]
    //while(i < 1000){
    //  val (winExp1, _,looseExp1,_) = iterationPlay(
    //    10000,
    //    game1.resetGame,
    //    new StickPlayer("Player1", game1.getStateListGame(), 0.99, game1.permittedAction),
    //    new StickPlayer("Player2", game1.getStateListGame(), 0.99, game1.permittedAction),
    //    0.99,
    //    true,
    //    false
    //  )
    //  i+=1
    //  listL.append(winExp1.stateList.toList.sortBy(_._2).take(3).map(_._1))
    //}
    /// Result
    ///listL.groupBy(identity).map{case (k,v)=> (k,v.size)}
    //res36: scala.collection.immutable.Map[List[Int],Int] = Map(List(1, 5, 9) -> 1000)

    // Train two players
    val (lastWinPlayer, _, lastLoosePlayer, _) = iterationPlay(
      10000,
      game1.resetGame,
      new StickPlayer("Player1", game1.getStateListGame(), 0.99, game1.permittedAction),
      new StickPlayer("Player2", game1.getStateListGame(), 0.99, game1.permittedAction),
      0.99,
      true,
      false
    )

    //winExp1.stateList.toList.sortBy(_._2).take(3)
    //looseExp1.stateList.toList.sortBy(_._2).take(3)
    //histo1.map( _.last match { case Transition(_,_,action,_) => action } ).groupBy(identity).map{ case (k,v) => (k, v.length)}.toList
    //histo2.map( _.last match { case Transition(_,_,action,_) => action } ).groupBy(identity).map{ case (k,v) => (k, v.length)}.toList
    //histo1.drop(7000).map( _.last match { case Transition(_,_,action,_) => action } ).groupBy(identity).map{ case (k,v) => (k, v.length)}.toList
    //histo2.drop(7000).map( _.last match { case Transition(_,_,action,_) => action } ).groupBy(identity).map{ case (k,v) => (k, v.length)}.toList


    //val lastWinPlayerStats = lastWinPlayer.statsList
    //
    val (player4, _, player3, _) = iterationPlay(100, game1.resetGame, lastWinPlayer, randomPlayer, 1, false, false)

    if (player3.namePlayer == "PlayerRandom") println("success percentage of robot player against random player : " + 1.-(player3.statsList.nbWin / 100.0) + " %")
    else println("success percentage of robot player against random player: " + 1.-(player4.statsList.nbWin / 100.0) + " %")

    //// TO DO : Faire les tests sur Play, Faire les commentaires, Faire le git avec readme, regarder l'erreur liée au 10

    runPlay(game1.resetGame,player4, humanPlayer,false, true)

  }

}


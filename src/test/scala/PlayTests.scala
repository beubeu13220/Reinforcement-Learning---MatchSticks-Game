import org.scalatest.FunSuite
import rf.examples._
import rf.examples.AllPlayer._
import rf.examples.Play._

class PlayTests extends FunSuite{

  trait TrainTests {
    val game1 = new StickGame(12, List(1, 2, 3))
    val player1 = new StickPlayer("Player1", game1.getStateListGame(), 0.5, game1.permittedAction)
    val player2 = new StickPlayer("Player2", game1.getStateListGame(), 0.5, game1.permittedAction)
    val transitionList =  List(Transition(null,1,1,-1), Transition(1,6,3,0), Transition(6,10,1,0), Transition(10,12,1,0))

  }

  test("[Train] - Compute Transition") {
    new TrainTests {
      val V = game1.getStateListGame()
      val player1Prime = player1.train(transitionList, valueFunctionPlay)
      assert(player1Prime.stateList.mapValues(math.floor(_)) == Map(5 -> 0.0,
        10 -> -1.0, 1 -> -1.0, 6 -> -1.0, 9 -> 0.0, 2 -> 0.0, 12 -> -1.0, 7 -> 0.0, 3 -> 0.0,
        11 -> 0.0, 8 -> 0.0, 4 -> 0.0))
    }
  }

}

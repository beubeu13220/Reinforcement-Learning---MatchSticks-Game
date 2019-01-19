import org.scalatest.FunSuite
import rf.examples._

class StickGameTests extends FunSuite {

  trait TestStats {
    val game1 = new StickGame(12, List(1, 2, 3))
    val game2 = new StickGame(6, List(1, 2, 3))
    val game3 = new StickGame(0, List(1, 2, 3))
    val game4 = new StickGame(6, List(1, 2, 3, 4, 5, 6))

  }

  test("[getStateListGame] - length init state 12") {
    new TestStats {
      assert(game1.getStateListGame().toList.length == 12)
    }
  }

  test("[getStateListGame] - length init state 0") {
    new TestStats {
      assert(game3.getStateListGame().toList.length == 0)
    }
  }

  test("[isTerminate] - Game isn't terminate") {
    new TestStats {
      assert(game1.isTerminate == false)
    }
  }

  test("[isTerminate] - Game is terminate") {
    new TestStats {
      assert(game3.isTerminate == true)
    }
  }

  test("[action] - Require action Value with right action (1)") {
    new TestStats {
      assert(game1.action(1) == (11, 1, 0))
    }
  }


  test("[action] - Require action Value with right action and end game after action") {
    new TestStats {
      assert(game4.action(6) == (null, 6, -1))
    }
  }


  test("[action] - Action with not permitted values") {
    new TestStats {
      assertThrows[IllegalArgumentException] {
        game1.action(6)
      }
    }
  }

  test("[Game] - Test action effect on stick number and end game") {
    new TestStats {
      val _ = game2.action(3)
      assert(game2.action(3) == (null, 3, -1))
      assert(game2.isTerminate == true)
    }
  }


}

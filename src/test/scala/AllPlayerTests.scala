import org.scalatest.FunSuite
import rf.examples.AllPlayer._

class AllPlayerTests extends FunSuite {

  trait TestStats {
    val t1 = Stats(12, 11, 6)
    val t2 = Stats(1, 0, 0)
    val t3 = Stats(0, 1, 0)
    val t4 = Stats(0, 0, 1)
  }

  test("[Stats] - Add function with Win case") {
    new TestStats {
      assert((t1 add t2) == Stats(13, 11, 6))
    }
  }


  test("[Stats] - Add function with Loose case") {
    new TestStats {
      assert((t1 add t3) == Stats(12, 12, 6))
    }
  }


  test("[Stats] - Add function with Start case") {
    new TestStats {
      assert((t1 add t4) == Stats(12, 11, 7))
    }
  }


  test("[Stats] - Add function with Exception case") {
    new TestStats {
      assertThrows[IllegalArgumentException] {
        t4 add t1
      }
    }
  }

  trait TestTransition {
    val rewardArray = (8, 3, 0)
    val t1 = Transition(8, 12, 2, 0)
    val t2 = Transition(4, 8, 3, 0)
    val t2Prime = Transition(null, 8, 3, 0)
    val t3 = Transition(1, 4, 1, 0)
    val t4 = Transition(null, 1, 1, -1)
  }

  test("[addTransition] - Add transition to List[Transition]") {
    new TestTransition {
      assert(addTransition(List(t1), rewardArray) == List(t2Prime, t1))
    }
  }

  test("[setPrevState] - Set the next state when state is null") {
    new TestTransition {
      assert(setPrevState(List(t2Prime, t1), 4) == List(t2, t1))
    }
  }

  test("[setPrevState] - No Set the next state when state isn't null") {
    new TestTransition {
      assert(setPrevState(List(t2, t1), 12) == List(t2, t1))
    }
  }

  test("[setPrevState] - List[Transition] is Empty") {
    new TestTransition {
      assert(setPrevState(List(), 12) == List())
    }
  }

}

package hellotest

// example straight from scalatest.org

import scala.collection.mutable.Stack
import org.apache.commons.collections4.queue.CircularFifoQueue

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.collection.mutable
import org.scalatest.Suite
import org.scalatestplus.scalacheck.Checkers
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._
import scala.jdk.CollectionConverters._


class WordCloudSpec extends AnyFlatSpec with Matchers{

  "A Queue" should "maintain the order of words and limit size" in{
    val queue = new CircularFifoQueue[String](3) 
    queue.add("Hello")
    queue.add("World")
    queue.add("Scala")

    queue.size() shouldEqual 3

    // Adding another element should evict the oldest
    queue.add("Test")
    queue.size() shouldEqual 3
    queue.poll() shouldEqual "World" // "Hello" should be evicted
  }
  /*
  "A CircularFifoQueue" should "return null when polling an empty queue" in {
    val emptyQueue = new CircularFifoQueue   
    // Attempt to call poll (or another method that throws when empty)
    // Check that polling returns null

   // Use Option to handle the return value from poll
   val result = emptyQueue.poll()
  result should be null // Check that it is None
}

it should "throw NoSuchElementException when accessing an invalid index" in {
  val queue = new CircularFifoQueue 

  // Attempt to access an index that is invalid
  an[NoSuchElementException] should be thrownBy queue.get(0)
  }
*/
  "The word processing logic" should "correctly count word frequencies" in {
    // Setup
    val testOutputSink = new TestOutputSink
    val words = Iterator("apple", "banana", "apple", "orange", "banana", "banana")

    // Call the method
    val cloudSize = 3
    val minLength = 1
    val windowSize = 6
    val everyKSteps = 1
    val minFrequency = 1

    Main.wordCloud(cloudSize, minLength, windowSize, everyKSteps, minFrequency, words, testOutputSink)

    // Check the output
    val output = testOutputSink.result.mkString(" ")
    assert(output.contains("apple: 2"))
    assert(output.contains("banana: 3"))
    assert(output.contains("orange: 1"))
  }

  it should "return top N words correctly" in {
    // Setup
    val testOutputSink = new TestOutputSink
    val words = Iterator("apple", "banana", "apple", "orange", "banana", "banana", "grape")

    // Call the method
    val cloudSize = 3
    val minLength = 1
    val windowSize = 6
    val everyKSteps = 1
    val minFrequency = 1

    Main.wordCloud(cloudSize, minLength, windowSize, everyKSteps, minFrequency, words, testOutputSink)

    // Check the output
    val output = testOutputSink.result.mkString(" ")
    assert(output.contains("banana: 3"))
    assert(output.contains("apple: 2"))
    assert(output.contains("grape: 1"))
  }

  it should "output word counts in the correct format" in {
    // Setup
    val testOutputSink = new TestOutputSink
    val words = Iterator("apple", "banana", "apple", "banana", "banana")

    // Call the method
    val cloudSize = 3
    val minLength = 1
    val windowSize = 5
    val everyKSteps = 1
    val minFrequency = 1

    Main.wordCloud(cloudSize, minLength, windowSize, everyKSteps, minFrequency, words, testOutputSink)

    // Check the output format
    val output = testOutputSink.result.mkString(" ")
    assert(output == "banana: 3 apple: 2")
  }

}
    
end WordCloudSpec

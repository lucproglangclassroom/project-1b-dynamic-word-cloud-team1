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

  
  it should "not produce output for empty input" in {
    val testOutputSink = new TestOutputSink
    val words = Iterator.empty // No words provided

    Main.wordCloud(3, 1, 5, 1, 1, words, testOutputSink)

    val output = testOutputSink.result.mkString(" ")
    assert(output.isEmpty)
  }

  it should "exclude words shorter than the minimum length" in {
    val testOutputSink = new TestOutputSink
    val words = Iterator("a", "b", "cat", "dog")

    Main.wordCloud(3, 3, 5, 1, 1, words, testOutputSink)

    val output = testOutputSink.result.mkString(" ")
    assert(!output.contains("cat"))
    assert(!output.contains("dog"))
  }

  it should "filter out words below the minimum frequency" in {
    val testOutputSink = new TestOutputSink
    val words = Iterator("apple", "banana", "apple", "orange", "banana")

    Main.wordCloud(3, 1, 5, 1, 3, words, testOutputSink) // Min frequency set to 3

    val output = testOutputSink.result.mkString(" ")
    assert(!output.contains("apple"))
    assert(!output.contains("banana"))
    assert(!output.contains("orange"))
  }

  it should "correctly count and output words when queue size is matched" in {
    val testOutputSink = new TestOutputSink
    val words = Iterator("apple", "banana", "orange")

    Main.wordCloud(3, 1, 3, 1, 1, words, testOutputSink)

    val output = testOutputSink.result.mkString(" ")
    assert(output.contains("apple: 1"))
    assert(output.contains("banana: 1"))
    assert(output.contains("orange: 1"))
  }

  it should "count repeated words correctly" in {
    val testOutputSink = new TestOutputSink
    val words = Iterator("apple", "banana", "apple", "banana", "banana")

    Main.wordCloud(3, 1, 5, 1, 1, words, testOutputSink)

    val output = testOutputSink.result.mkString(" ")
    assert(output.contains("apple: 2"))
    assert(output.contains("banana: 3"))
  }

  it should "handle boundary conditions with single word" in {
    val testOutputSink = new TestOutputSink
    val words = Iterator("apple")

    Main.wordCloud(1, 1, 1, 1, 1, words, testOutputSink)

    val output = testOutputSink.result.mkString(" ")
    assert(output.contains("apple: 1"))
  }

  it should "handle repeated single word in the queue" in {
    val testOutputSink = new TestOutputSink
    val words = Iterator.fill(5)("apple") // Five "apple" words

    Main.wordCloud(1, 1, 5, 1, 1, words, testOutputSink)

    val output = testOutputSink.result.mkString(" ")
    assert(output.contains("apple: 5"))
  }
}



    
end WordCloudSpec

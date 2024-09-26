package hellotest

// example straight from scalatest.org

import scala.collection.mutable.Stack
import org.apache.commons.collections4.queue.CircularFifoQueue

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.Suite
import org.scalatest.matchers.must.Matchers.*
import org.scalatest.junit.JUnitSuite
import org.scalatestplus.scalacheck.Checkers
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._


class WordCloudSpec extends AnyFlatSpec with Matchers{

  "A Queue" should "maintain the order of words and limit size" in{
    val queue = new CircularFifoQueue 
    queue.add("Hello")
    queue.add("World")
    queue.add("Scala")

    queue.size() shouldEqual 3

    // Adding another element should evict the oldest
    queue.add("Test")
    queue.size() shouldEqual 3
    queue.poll() shouldEqual "World" // "Hello" should be evicted
  }

  it should "handle empty states correctly" in {
    val emptyQueue = new CircularFifoQueue 
    an[NoSuchElementException] should be thrownBy emptyQueue.poll()
  }

  "The word processing logic" should "correctly count word frequencies" in {
    val words = List("apple", "banana", "apple", "orange", "banana", "banana")
    val queue = new CircularFifoQueue 

    words.foreach(queue.add) // Add words to the queue

    val wordCount = mutable.Map[String, Int]()

    queue.forEach { w =>
      wordCount(w) = wordCount.getOrElse(w, 0) + 1
    }

    wordCount("apple") shouldEqual 2
    wordCount("banana") shouldEqual 3
    wordCount("orange") shouldEqual 1
  }

  it should "return top N words correctly" in {
    val words = List("apple", "banana", "apple", "orange", "banana", "banana", "grape")
    val queue = new CircularFifoQueue 
    words.foreach(queue.add)

    val wordCount = mutable.Map[String, Int]()

    queue.forEach { w =>
      wordCount(w) = wordCount.getOrElse(w, 0) + 1
    }

    val sortedWords = wordCount.toSeq.sortBy { case (word, count) => (-count, word) }
    val topWords = sortedWords.take(3)

    topWords should contain allOf (("banana", 3), ("apple", 2), ("grape", 1))
  }
}
    
end StackSpec

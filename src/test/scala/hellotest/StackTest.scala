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
  //TODO: empty queue poll should return null
  it should "handle empty states correctly" in {
    val emptyQueue = new CircularFifoQueue[String](2)  
    an[NoSuchElementException] should be thrownBy emptyQueue.poll()
  }

  "The word processing logic" should "correctly count word frequencies" in {
    val words = List("apple", "banana", "apple", "orange", "banana", "banana")
    val queue = new CircularFifoQueue[String](words.length)

    words.foreach(queue.add) // Add words to the queue

    val wordCount = mutable.Map[String, Int]()

    queue.asScala.collect { case s: String =>
      wordCount(s) = wordCount.getOrElse(s, 0) + 1
    }

    wordCount("apple") shouldEqual 2
    wordCount("banana") shouldEqual 3
    wordCount("orange") shouldEqual 1
  }

  it should "return top N words correctly" in {
    val words = List("apple", "banana", "apple", "orange", "banana", "banana", "grape")
    val queue = new CircularFifoQueue[String](words.length)  
    words.foreach(queue.add)

    val wordCount = mutable.Map[String, Int]()

    queue.asScala.collect { case s: String =>
      wordCount(s) = wordCount.getOrElse(s, 0) + 1
    }

    val sortedWords = wordCount.toSeq.sortBy { case (word, count) => (-count, word) }
    val topWords = sortedWords.take(3)

    topWords should contain allOf (("banana", 3), ("apple", 2), ("grape", 1))
  }

  it should "output word counts in the correct format" in {
    val words = List("apple", "banana", "apple", "banana", "banana")
    val queue = new CircularFifoQueue[String](words.length)
    words.foreach(queue.add)

    val wordCount = mutable.Map[String, Int]()
    queue.asScala.collect { case s: String =>
        wordCount(s) = wordCount.getOrElse(s, 0) + 1
    }

    val sortedWords = wordCount.toSeq.sortBy { case (word, count) => (-count, word) }
    val topWords = sortedWords.take(3)

    val output = topWords.map { case (word, count) => s"$word: $count" }.mkString(" ")

    output shouldEqual "banana: 3 apple: 2"
  }

}
    
end WordCloudSpec

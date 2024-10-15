package hellotest

import org.apache.commons.collections4.queue.CircularFifoQueue
import scala.collection.mutable
import scala.collection.immutable.Queue
import scala.language.unsafeNulls
import scala.io.Source
import mainargs.ParserForMethods
import mainargs.arg
import mainargs.{main, arg, ParserForMethods, Flag}
import org.log4s._
import sun.misc.{Signal, SignalHandler}
import scala.collection.immutable.Queue

// For console input, just in case
import scala.io.StdIn.{readLine, readInt}

// OutputSink trait for output abstraction
trait OutputSink {
  def doOutput(value: String): Unit
}

// ConsoleOutputSink for standard output
class ConsoleOutputSink extends OutputSink {
  def doOutput(value: String): Unit = {
    println(value)
  }
}

// TestOutputSink for capturing output in tests
class TestOutputSink extends OutputSink {
  private val outputValues = scala.collection.mutable.ListBuffer.empty[String]

  def doOutput(value: String): Unit = {
    outputValues += value
  }

  def result: Seq[String] = outputValues.toSeq
}

object Main:
  // Default values for arguments
  val CLOUD_SIZE = 5
  val LENGTH_AT_LEAST = 6
  val WINDOW_SIZE = 5
  val MIN_FREQ = 1
  private val logger = org.log4s.getLogger
  //logger.debug(f"howMany = $howMany minLength = $minLength lastNWords = $lastNWords")
  def initLogging(): Unit = {
    // Loggers are configured directly using logger names. 
    logger.trace("This is a trace message") // Will not be printed if level is set to higher
    logger.debug("This is a debug message") // Will not be printed if level is set to higher
    logger.info("This is an info message") // Will be printed
    logger.warn("This is a warning message") // Will be printed
    logger.error("This is an error message") // Will be printed
  }

  // Sets up the SIGPIPE handler
  def setupSigpipeHandler(): Unit = {
    val handler = new SignalHandler {
      def handle(signal: Signal): Unit = {
        logger.info("Received sigpipe, now exiting")
        System.exit(0)
    }
  }
    Signal.handle(new Signal("PIPE"), handler)
}

  def main(args: Array[String]): Unit =
   //setupSigpipeHandler() 
    ParserForMethods(this).runOrExit(args.toIndexedSeq)
    ()

  @main 
  def run(
    @arg(short = 'c', doc = "size of the sliding word cloud") cloudSize: Int = CLOUD_SIZE,
    @arg(short = 'l', doc = "minimum word length to be considered") minLength: Int = LENGTH_AT_LEAST,
    @arg(short = 'w', doc = "size of the sliding FIFO queue") windowSize: Int = WINDOW_SIZE,
    @arg(short = 's', doc = "number of steps between word cloud updates") everyKSteps: Int = 10,
    @arg(short = 'f', doc = "minimum frequency for a word to be included in the cloud") minFrequency: Int = MIN_FREQ
    ): Unit =
    
    logger.debug(f"howMany=$cloudSize minLength=$minLength lastNWords=$windowSize everyKSteps=$everyKSteps minFrequency=$minFrequency")
    
    // Set up input from stdin and process words
    val lines = scala.io.Source.fromInputStream(System.in)("UTF-8").getLines.toList
    //val lines = List("Example sentence with words of various lengths: elephant, kangaroo, zebra.")

   // println(s"Lines captured: ${lines.mkString(", ")}") // Debugging line

    val words = lines.flatMap(l => l.split("(?U)[^\\p{Alpha}0-9']+")).map(_.toLowerCase)
    //println(s"Words captured: ${words.mkString(", ")}") // Debugging line

    // Convert words to iterator after capturing
    
    //wordIterator.foreach(word => println(s"Word: $word")) // Debugging line
    val outputSink = new ConsoleOutputSink()
    wordCloud(cloudSize, minLength, windowSize, everyKSteps, minFrequency, words, outputSink)
   
  def wordCloud(
    cloudSize: Int,
    minLength: Int,
    windowSize: Int,
    everyKSteps: Int,
    minFrequency: Int,
    words: List[String],
    output: OutputSink // Accept words as an argument
  ): Unit = {
  println(s"Starting wordCloud with cloudSize=$cloudSize, minLength=$minLength, windowSize=$windowSize, everyKSteps=$everyKSteps, minFrequency=$minFrequency")
  val wordIterator = words.iterator
  //words.foreach(word => println(s"Word: $word")) // Debugging line
  val initialState = (Queue.empty[String], Map.empty[String, Int])
 // words.foreach(word => println(s"Word: $word")) // Debugging line

  val results = words.filter(_.length >= minLength).scanLeft(initialState) {
    case ((queue, wordCount), word) =>
     // println(s"Processing word: $word")
      val (updatedQueue, evictedWordCount) = if (queue.size == windowSize) {
        val (dequeuedWord, updatedQueue) = queue.dequeue
       // println(s"Dequeued word: $dequeuedWord")
        (updatedQueue.enqueue(word), wordCount.updated(dequeuedWord, wordCount.getOrElse(dequeuedWord, 0) - 1))
      } else {
        (queue.enqueue(word), wordCount)
      }
      val updatedWordCount = evictedWordCount.updated(word, evictedWordCount.getOrElse(word, 0) + 1)
     // println(s"Updated word count: $updatedWordCount")
      (updatedQueue, updatedWordCount)
  }.toList // Ensure it's fully evaluated

 // println(s"Results after scanLeft: $results")

  results.zipWithIndex.foreach {
    case ((queue, wordCount), index) =>
      val validStep = index % everyKSteps == 0 && queue.size == windowSize
    //  println(s"Index: $index, validStep: $validStep")
      if (validStep) {
       // println(s"Queue at step $index: ${queue.mkString(", ")}")
        val sortedWords = wordCount.toSeq.filter { case (_, count) => count >= minFrequency }
          .sortBy { case (word, count) => (-count, word) }
        val topWords = sortedWords.take(cloudSize)
        println(s"Top words: ${topWords.mkString(", ")}")
        if (topWords.nonEmpty) {
          output.doOutput(topWords.map { case (word, count) => s"$word: $count" }.mkString(" "))
          if (System.out.checkError()) {
            println("Error writing to stdout. Exiting.")
            sys.exit(1)
          }
        }
      }
  }
}
    

    // Process words and update word cloud
    // for (word <- words.filter(_.length >= minLength)) {
    //   queue.add(word)
    //   stepCounter += 1

    //   // start processing when the queue is full with window_size
    //   if (queue.size == windowSize && stepCounter % everyKSteps == 0) {
    //     val wordCount = mutable.Map[String, Int]()
    //     queue.forEach { w =>
    //       wordCount(w) = wordCount.getOrElse(w, 0) + 1
    //     }

    //     val sortedWords = wordCount.toSeq.
    //     filter { case (_, count) => count >= minFrequency }
    //     .sortBy { case (word, count) => (-count, word) }

    //     val topWords = sortedWords.take(cloudSize)
    //     if (topWords.nonEmpty) {
    //       output.doOutput(topWords.map { case (word, count) => s"$word: $count" }.mkString(" "))
    //       //println(topWords.map { case (word, count) => s"$word: $count" }.mkString(" "))
    //       if (System.out.checkError()) {
    //         println("Error writing to stdout. Exiting.")
    //         sys.exit(1)  // Exit with a failure code
    //       }
    //     }
    //   }
    // }

end Main

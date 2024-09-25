package hellotest

import org.apache.commons.collections4.queue.CircularFifoQueue
import scala.collection.mutable
import scala.language.unsafeNulls
import scala.io.Source
import mainargs.ParserForMethods
import mainargs.arg
import org.log4s._
//import Logs4sConfig._

object Main:
  // Default values for arguments
  val CLOUD_SIZE = 10
  val LENGTH_AT_LEAST = 6
  val WINDOW_SIZE = 1000
  private val logger = org.log4s.getLogger
  //logger.debug(f"howMany = $howMany minLength = $minLength lastNWords = $lastNWords")
  def initLogging(): Unit = {
    // Log4s doesn't have setLoggerThreshold, etc. We configure log levels via application configuration.
    
    // Loggers are configured directly using logger names. You typically do this in a configuration file.
    // Here is an example of logging at different levels:
    logger.trace("This is a trace message") // Will not be printed if level is set to higher
    logger.debug("This is a debug message") // Will not be printed if level is set to higher
    logger.info("This is an info message") // Will be printed
    logger.warn("This is a warning message") // Will be printed
    logger.error("This is an error message") // Will be printed


    // Since there are no ways to add appenders. Log4s does not allow you to add appenders directly in this way.
    // You would typically set appenders in a logging configuration file (e.g., log4s.xml or log4s.properties).
  }

  def main(args: Array[String]): Unit = ParserForMethods(this).runOrExit(args.toIndexedSeq)
  @main
  def run(
    @arg(short = 'c', doc = "size of the sliding word cloud") cloudSize: Int = 10,
    @arg(short = 'l', doc = "minimum word length to be considere") minLength: Int = 6,
    @arg(short = 'w', doc = "size of the sliding FIFO queue") windowSize: Int = 1000,
    @arg(short = 's', doc = "number of steps between word cloud updates") everyKSteps: Int = 10,
    @arg(short = 'f', doc = "minimum frequency for a word to be included in the cloud") minFrequency: Int = 3) =

  logger.debug(f"howMany=$cloudSize minLength=$minLength lastNWords=$windowSize everyKSteps=$everyKSteps minFrequency=$minFrequency")

    // Parse the command-line argument or use the default value
    var cloud_size = CLOUD_SIZE
    var length_at_least = LENGTH_AT_LEAST
    var window_size = WINDOW_SIZE

    // Set up input from stdin and process words
    val lines = scala.io.Source.fromInputStream(System.in)("UTF-8").getLines

    val words = lines.flatMap(l => l.split("(?U)[^\\p{Alpha}0-9']+")).map(_.toLowerCase)


    val queue = new CircularFifoQueue[String](window_size)

    // Process words and update word cloud
    words.filter(_.length >= length_at_least).foreach { word =>
      queue.add(word) // Add word to the queue

      // start processing when the queue is full with window_size
      if (queue.size == window_size) {
        val wordCount = mutable.Map[String, Int]()
        queue.forEach { w =>
          wordCount(w) = wordCount.getOrElse(w, 0) + 1
        }

        val sortedWords = wordCount.toSeq.sortBy { case (word, count) => (-count, word) }

         val topWords = sortedWords.take(cloud_size)

        println(topWords.map { case (word, count) => s"$word: $count" }.mkString(" "))
      }
    }

end Main

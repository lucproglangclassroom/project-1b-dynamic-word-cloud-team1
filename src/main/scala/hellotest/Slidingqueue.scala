package hellotest

import org.apache.commons.collections4.queue.CircularFifoQueue
import scala.io.Source
import scala.util.Try

import scala.language.unsafeNulls


object Slidingqueue {
  private val LAST_N_WORDS = 10

  def main(args: Array[String]): Unit = {
    // Argument validity checking
    if (args.length > 1) {
      Console.err.println("usage: ./target/universal/stage/bin/Slidingqueue [ last_n_words ]")
      sys.exit(2)
    }

    // Parse command-line argument or use default value
    val lastNWords = if (args.length == 1) {
      Try(args(0).toInt).toOption match {
        case Some(value) if value > 0 => value
        case _ =>
          Console.err.println("argument should be a natural number")
          sys.exit(4)
      }
    } else {
      LAST_N_WORDS
    }

    // Create a CircularFifoQueue
    val queue = new CircularFifoQueue[String](lastNWords)
    
    println("Enter number:")

    // Use Source.stdin.getLines() to read input
    Source.stdin.getLines().foreach { line =>

      if (Option(line).getOrElse("").trim.isEmpty) {
        println("Empty input , exiting...")
        sys.exit(0)
      }

      val words = splitintoWords(line)

      words.foreach { word =>
        queue.add(word)
        println(queue)

        // Terminate on I/O error (e.g., SIGPIPE)
        if (Console.out.checkError()) sys.exit(1)
      }
    }
  }

  // Method to  split a line into words on spaces and symbols
  private def splitintoWords(line: String): Seq[String] = {
    val delimiters = Set(' ', ',', '.', ';', ':', '!', '?', '\t', '\n', '\r')
    val currentWord = new StringBuilder
    val words = scala.collection.mutable.ListBuffer[String]()

    for (char <- line) {
      if (delimiters.contains(char)) {
        if (currentWord.nonEmpty) {
          words += currentWord.toString
          currentWord.clear()
        }
      } else {
        currentWord += char
      }
    }

    if (currentWord.nonEmpty) {
      words += currentWord.toString
    }

    words.toList
  }
}


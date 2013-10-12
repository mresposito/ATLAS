/*
 * Copyright (c) 2012 Twitter, Inc.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package edu.illinois.learn.hadoop

// Scalding
import com.twitter.scalding._

class WordCountJob(args : Args) extends Job(args) {
  TextLine(args("input"))
    .flatMap('line -> 'word) { line : String => tokenize(line) }
    .groupBy('word) { _.size }
    .write( Csv( args("output") ) )

  // Split a piece of text into individual words.
  def tokenize(text : String) : Array[String] = {
    // Lowercase each word and remove punctuation.
    text.toLowerCase.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+")
  }
}

class TypedWordCountJob(args : Args) extends Job(args) {

  // val textSource = TextLine( args("input") )
  // val tokens = textSource.flatMap('line -> 'word) { line : String => tokenize(line) }
    
  val input = TypedPipe.from(TextLine(args("input")))

  val words = input.flatMap(tokenize) 
  val wordGroups = words.groupBy(identity).size
  val unTyped = wordGroups.toPipe('words, 'size)
  unTyped.write(Csv(args("output")))

  // val size = tokens.groupBy('word) { _.size }
  // size

  // Split a piece of text into individual words.
  def tokenize(text : String) : Array[String] = {
    // Lowercase each word and remove punctuation.
    text.toLowerCase.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+")
  }
}

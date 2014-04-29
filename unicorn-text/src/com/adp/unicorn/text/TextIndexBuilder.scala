package com.adp.unicorn.text

import com.adp.unicorn._
import com.adp.unicorn.JsonValueImplicits._
import com.adp.unicorn.store.DataSet
import smile.nlp.stemmer.Stemmer
import smile.nlp.tokenizer.SimpleTokenizer
import smile.nlp.tokenizer.SimpleSentenceSplitter
import smile.nlp.dictionary.EnglishStopWords
import smile.nlp.dictionary.EnglishPunctuations

class TextIndexBuilder(storage: DataSet) extends TextIndex {

  val textLength = new Document(TextBodyLengthKey, TextIndexFamily)
  val titleLength = new Document(TextTitleLengthKey, TextIndexFamily)
  val anchorLength = new Document(TextAnchorLengthKey, TextIndexFamily)

  /**
   * Sentence splitter.
   */
  var sentenceSpliter = SimpleSentenceSplitter.getInstance
  
  /**
   * Tokenizer on sentences
   */
  var tokenizer = new SimpleTokenizer

  /**
   * Dictionary of stop words.
   */
  var stopWords = EnglishStopWords.DEFAULT
  
  /**
   * Punctuation.
   */
  var punctuations = EnglishPunctuations.getInstance
  
  /**
   * Process each token (after filtering stop words, numbers, and optional stemming).
   */
  def foreach[U](text: String)(f: ((String, Int)) => U): Unit = {
    var pos = 0
    
    sentenceSpliter.split(text).foreach { sentence =>
      tokenizer.split(sentence).foreach { token =>
        pos += 1
        val lower = token.toLowerCase
        if (!(punctuations.contains(lower) ||
              stopWords.contains(lower) ||
              lower.length == 1 ||
              lower.matches("[0-9\\.\\-\\+\\|\\(\\)]+"))) {
          val word = stemmer match {
            case Some(stemmer) => stemmer.stem(lower)
            case None => lower
          }

          f(word, pos)
        }
      }
      
      pos += 1
    }    
  }
  
  /**
   * Add a text into index.
   * @param doc   The id of document that owns the text.
   * @param field The filed name of text in the document.
   * @param text  The text body.
   */
  private def add(doc: String, field: String, text: String, sizeDoc: Document, indexKeySuffix: String) {
    val termFreq = scala.collection.mutable.Map[String, Int]().withDefaultValue(0)
    //val termPos = scala.collection.mutable.Map[String, Array[Int]]().withDefaultValue(Array[Int]())

    var size = 0
    foreach(text) { case (word, pos) =>
      size += 1
      termFreq(word) += 1
      //termPos(word) :+ pos
    }
      
    val key = doc + DocFieldSeparator + field.replace(Document.FieldSeparator, DocFieldSeparator)

    sizeDoc(key) = size    
    
    termFreq.foreach { case (word, freq) =>
      storage.put(word + indexKeySuffix, TextIndexFamily, key, JsonIntValue(freq).bytes)
    }

    /*
    termPos.foreach { case (word, pos) =>
      storage.put(word + TermPositionSuffix, TextIndexFamily, key, JsonBlobValue(pos).bytes)
    }
    */

    // termFreq and termPos updates will also be commit here.
    sizeDoc into storage
  }
  
  /**
   * Add a text into index.
   * @param doc   The id of document that owns the text.
   * @param field The filed name of text in the document.
   * @param text  The text body.
   */
  def add(doc: String, field: String, text: String) {
    add(doc, field, text, textLength, TermIndexSuffix)
  }
  
  /**
   * Add a title into index.
   * @param doc   The id of document that owns the text.
   * @param field The filed name of text in the document.
   * @param title  The title.
   */
  def addTitle(doc: String, field: String, title: String) {
    add(doc, field, title, titleLength, TermTitleIndexSuffix)
  }
  
  /**
   * Add an anchor text into index.
   * @param doc   The id of document that owns the text.
   * @param field The filed name of text in the document.
   * @param anchor  The anchor text.
   */
  def addAnchor(doc: String, field: String, anchor: String) {
    add(doc, field, anchor, anchorLength, TermAnchorIndexSuffix)
  }
}

object TextIndexBuilder {
  def apply(storage: DataSet): TextIndexBuilder = {
    new TextIndexBuilder(storage)
  }
}

package com.adp.cdg.store.accumulo

import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import com.adp.cdg._
import com.adp.cdg.DocumentImplicits._
import com.adp.cdg.store._

class UnitSpec extends FlatSpec with BeforeAndAfter {
  trait Context {
    // Turn off debug output
    org.apache.log4j.LogManager.getRootLogger().setLevel(org.apache.log4j.Level.ERROR)
    
    // Connect to Accumulo
    val server = AccumuloServer("local-poc", "127.0.0.1:2181", "tester", "adpadp")
    val table = server.dataset("small", "public")
    
    // Create a document 
    val person = Document("293050")
    person("name") = "Haifeng"
    person("gender") = "Male"
    person("salary") = 1.0
    person("zip") = 10011

    // Create another document.
    // Note the syntax is just like JavaScript
    val address = Document("293050")
    address.street = "135 W. 18th ST"
    address.city = "New York"
    address.state = "NY"
    address.zip = person.zip

    // add a document into another one
    person.address = address
    // add an array into a document
    person.projects = Array("GHCM", "Analytics")

    // Add some relationships
    person("work with", "Jim") = true
    person("work with", "Mike") = true
    person("report to", "Jerome") = true

    // Fetch a non-existing row to warm up the system (loading classes, etc.).
    "row1" of table
    // Do it twice
    "row1" of table
  }
}
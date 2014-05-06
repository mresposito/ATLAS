package edu.illinois.learn.models

/**
 * Super class, defines how
 * the json is parsed
 */
case class Serial(name: String,
  semesters: List[String],
  aggregations: List[Aggregation],
  columns: List[Column])

/**
 * One single agregation.
 * THis is how the data gets,
 * and fend into columns
 */
case class Aggregation(tag: String,
  input: Option[String] = None,
  filter: Option[String] = None)

case class Column(tag: String,
  query: String)
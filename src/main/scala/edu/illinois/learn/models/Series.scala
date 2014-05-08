package edu.illinois.learn.models

/**
 * Super class, defines how
 * the json is parsed
 */
case class Serial(name: String,
  semesters: List[String],
  aggregations: List[Aggregation],
  columns: List[Column]) {
  
  private def cols = columns.map { col =>
    Query(col, None, None) 
  }
  private def zip[T](col: Query,
    xs: List[T], cp: Query => T => Query) = if(xs.isEmpty) {
    List(col)
  } else {
    xs.map(cp(col))
  }
  def sm(query: Query)(sm: String): Query = query.copy(semester = Some(sm))
  def ag(query: Query)(sm: Aggregation): Query = query.copy(
    aggregation = Some(sm))
  def makeQueries: List[Query] = for {
    col <- cols
    sem <- zip(col, semesters, sm)
    agg <- zip(sem, aggregations, ag)
  } yield agg
}
  
case class Query(column: Column,
  semester: Option[String],
  aggregation: Option[Aggregation])

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
package edu.illinois.learn.classAnalysis

import java.io.PrintWriter
import edu.illinois.learn.models.DataAccessLayer

class RunWrapper[A <: ClassAnalysis](val cls: List[A], val output: String = "results/") {

  def count[B](a: Map[String, List[B]]): Map[String, Int] = a.map(k => (k._1, k._2.length))

  def run(fun: A => Map[String, List[Class]], jobName: String): Unit = cls.map { a: A =>
    writeResults(a.name + jobName, fun(a))
  }

  def writeResults[B](path: String, results: Map[String, List[B]]) = {
    val p = new PrintWriter(output + path + ".tsv", "UTF-8")
    p.print {
      count(results).toList.sortBy(el => el._2.toString.toInt). // little hack
        map(el => el._1 + "\t" + el._2.toString).mkString("\n")
    }
    p.close()
  }
}

object ClassRunner {
  def main(args: Array[String]) = {
    val classData = "data/AASZULUClasses.json"
    val wrapper = new RunWrapper(
      List(new AllClasses(classData), new Lectures(classData),
        new GenEds(classData), new Online(classData)))
    val dal = new DataAccessLayer

    wrapper.run(_.countSectionsPerDepartmens, "SectionsPerDepartments")
    wrapper.run(_.countInstructorPerSession, "InstuctorPerSection")
    wrapper.run(_.countSectionsPerClass, "SectionsPerClass")
    wrapper.run(_.countLocationPerSession, "LocationPerSection")
    
    wrapper.writeResults("forumPerClass", dal.countForumType)
  }
}

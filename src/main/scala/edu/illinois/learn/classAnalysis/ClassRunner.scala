package edu.illinois.learn.classAnalysis


class RunWrapper[A <: ClassAnalysis](val cls: List[A], val output: String = "results/") {
  
  def run(fun: A => Map[String, Any], jobName: String): Unit = cls.map { a: A =>
    a.writeResults(output + a.name + jobName + ".tsv", fun(a))
  }
}

object ClassRunner {
  def main(args: Array[String]) = {
    val classData = "data/AASZULUClasses.json"
    val wrapper = new RunWrapper(
      List(new AllClasses(classData), new Lectures(classData),
        new GenEds(classData), new Online(classData)))

    wrapper.run(_.countSectionsPerDepartmens, "SectionsPerDepartments")
    wrapper.run(_.countInstructorPerSession, "InstuctorPerSection")
    wrapper.run(_.countSectionsPerClass, "SectionsPerClass")
    wrapper.run(_.countLocationPerSession, "LocationPerSection")
  }
}

define ([
], function() {

  /**
   * Possible graphs: top, histogram
   * Possible stats: total, count, avg, stdev
   */
  return {
    // graph rendering
    graphRendering: {
      toDisplay: 15,
      buckets: 10
    },
    // semesters we are running on
    semesters: ["2012fall", "2012summer"],

    classes: {
      graphs: ["top", "histogram"],
      stats: ["avg", "stdev"],
      template: "classes",
      aggregations: [
        {tag: "allClasses", text: "All courses"},
        {tag: "genEds", text: "General Education Courses"},
        {tag: "moodle", text: "Moodle classes"},
        {tag: "moodleGenEd", text: "General Education Courses on Moodle"},
        {tag: "online", text: "Online Courses"}
      ],
      columns: [ // puts more graphs here
        {tag: "ClassesPerDepartment", stats: ["total"]},
        {tag: "SectionsPerDepartment", stats: ["total", "count"]}
      ]
    },
    forums: {
      graphs: ["top", "histogram"],
      stats: ["avg", "stdev"],
      template: "forums",
      aggregations: [
        {tag: "moodle", text: "All classes on Moodle"},
        {tag: "moodleGenEd", text: "General Education Courses on Moodle"},
        {tag: "moodleOnline", text: "Fully Online Courses"}
      ],
      columns: [
        {tag: "ForumPerClass", graphs: ["histogram"]},
        {tag: "ForumPostsPerClass"},
        {tag: "ForumPostsPerStudentPerClass"},
        {tag: "ForumPerDepartment", graphs: ["histogram"]},
        {tag: "ForumPostsPerDepartment"},
        {tag: "ForumPostsPerStudentPerDepartment"},
      ],
    }
  }
});

define ([
], function() {

  /**
   * Possible graphs: top, histogram
   * Possible stats: total, count, avg, stdev
   */
  return {
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
        // {tag: "SectionsPerClass"},
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
        {tag: "ForumPerDepartment", graphs: ["histogram"]},
        {tag: "ForumPostsPerDepartment"},
      ],
    }
  }
});

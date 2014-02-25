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
    }
  }
});

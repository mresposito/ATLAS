var baseData= "../../results/"
var d3Load = function(path, callback) {
  d3.text(baseData + path, function(text) {
    callback(d3.tsv.parseRows(text)); // automatically parse TSV
  });
};

var toDisplay = 15

var sum = function(xs) {
  return _.reduce(xs, function(xs, ttl) { return xs + ttl }, 0);
}

var avg = function(data) {
  return (sum(dataset(data))*1.0/data.length).toFixed(2);
}

var labels = function(xs) {
  return _.map(xs, function(el) { return el[0]})
}

var dataset = function(xs, idx) {
  return _.map(xs, function(el) { return parseInt(el[1])})
}

var renderGraph = function(tag, callback) {
  d3Load(tag + ".tsv", function(data) {
    if (callback) {
      callback(data)
    }

    $("strong."+tag).text(avg(data))
    var smallSample = _.last(data, toDisplay);
    smallSample = smallSample.reverse()

    var data = {
      labels: labels(smallSample),
      datasets: [{
        scaleFontSize : 13,
        fillColor : "rgba(151,187,205,0.5)",
        strokeColor : "rgba(151,187,205,1)",
        pointColor : "rgba(151,187,205,1)",
        data: dataset(smallSample)
      }]
    };
    var ctx = document.getElementById(tag).getContext("2d");
    new Chart(ctx).Bar(data);
  });
}

var renderClassGraphs = function(tag) {
  var cls = "." + tag
  renderGraph(tag + "SectionsPerDepartments", function(data) {
    $(cls + "Sections").text(sum(dataset(data)));
  })
  renderGraph(tag + "ClassesPerDepartment", function(data) {
    $(cls + "Classes").text(sum(dataset(data)));
    $(cls + "Dep").text(data.length);
  })
}

_.map(["allClasses", "genEds", "moodle", "moodleGenEd"], renderClassGraphs);

renderGraph("forumTypes", function(data) {
  $("strong.forumCount").text(sum(dataset(data)));
});

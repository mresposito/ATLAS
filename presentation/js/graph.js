var baseData= "../../results/"
var d3Load = function(path, callback) {
  d3.text(baseData + path, function(text) {
    var data = d3.tsv.parseRows(text);
    var parsed = _.zip(labels(data), dataset(data))
    callback(_.sortBy(parsed, function(el) { return el[1];} )); // automatically parse TSV
  });
};

var toDisplay = 15

var renderTopGraph = function(tag, callback) {
  renderProcessedGraph(tag, callback, function(data) {
    $("strong."+tag).text(avg(data))
    var smallSample = _.last(data, toDisplay);
    return smallSample.reverse();
  });
}

var makeLabels = function(hist, points) {
  var makeLabel = function (one, two) {
    return one.toFixed(0) + "-" + two.toFixed(0);
  }
  var min = _.min(points)
  var max = _.max(points)
  var labels = [];
  for(var i=0; i < hist.length; i++) {
    var label = "";
    var el = hist[i].x
    if (i == hist.length -1) {
      label = makeLabel(el, max)
    } else {
      label = makeLabel(el, hist[i+1].x -1)
    }
      labels.push(label)
  }
  return labels;
}

var renderHistogram = function(tag, callback) {
  var histogramTag = tag + "Histogram"
  renderProcessedGraph(histogramTag, callback, function(data) {
    var buckets = 10;
    var points = dataset(data)
    var hist = d3.layout.histogram().bins(buckets)(points);
    var labels = makeLabels(hist, points)
    var len = _.map(hist, function(el) { return el.length });
    var stDev = Stats(points).getStandardDeviation()
    var avger = avg(data);
    var txt = stDev.toFixed(2) + " (" + (stDev/avger).toFixed(2) + ")"
    $('.' + histogramTag).text(txt);

    return _.zip(labels, len)
  });
}

var renderProcessedGraph = function(tag, callback, process) {
  d3Load(tag.replace("Histogram", "") + ".tsv", function(data) {
    if(callback) {
      callback(data)
    }
    renderGraph(tag, process(data))
  });
}

var renderGraph = function(tag, data) {
  var data = {
    labels: labels(data),
    datasets: [{
      scaleFontSize : 13,
      fillColor : "rgba(151,187,205,0.5)",
      strokeColor : "rgba(151,187,205,1)",
      pointColor : "rgba(151,187,205,1)",
      data: dataset(data)
    }]
  };
  var ctx = document.getElementById(tag).getContext("2d");
  new Chart(ctx).Bar(data);
}

var renderClassGraphs = function(tag) {
  var cls = "." + tag
  var sections = tag + "SectionsPerDepartments"
  var classes = tag + "ClassesPerDepartment"

  renderTopGraph(sections, function(data) {
    $(cls + "Sections").text(sum(dataset(data)));
  })
  renderHistogram(sections);

  renderTopGraph(classes, function(data) {
    $(cls + "Classes").text(sum(dataset(data)));
    $(cls + "Dep").text(data.length);
  })

  renderHistogram(classes);
}

/**
 * 
 * Start plotting forum data
 *
 */
renderTopGraph("forumTypes", function(data) {
  $("strong.forumCount").text(sum(dataset(data)));
});

var renderForumGraphs = function(tag) {
  var cls = '.' + tag
  var classes = tag + 'ForumPerClass'
  var departments = tag + 'ForumPerDepartment'
  var posts = tag + 'ForumPostsPerClass'
  var postsPerDep = tag + 'ForumPostsPerDepartment'
 
  renderTopGraph(classes);
  renderTopGraph(departments);
  renderTopGraph(posts);
  renderTopGraph(postsPerDep);
  renderHistogram(classes);
  renderHistogram(departments);
}

_.map(["allClasses", "genEds", "moodle", "moodleGenEd", "online"], renderClassGraphs);
_.map(['moodle', 'moodleGenEd', 'moodleOnline'], renderForumGraphs);

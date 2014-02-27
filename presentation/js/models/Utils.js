define ([
  "underscore",
  "chart",
  "Stats",
  "d3",
  "settings"
], function (_, Chart, Stats, D3, Settings) {
  return {
    std: function(xs) {
      return Stats(xs).getStandardDeviation();
    },
    sum: function(xs) {
      return _.reduce(xs, function(xs, ttl) { return xs + ttl }, 0);
    },

    avg: function(data) {
      return (this.sum(data)*1.0/data.length).toFixed(2);
    },

    labels: function(xs) {
      return _.map(xs, function(el) { return el[0]})
    },

    dataset: function(xs, idx) {
      return _.map(xs, function(el) { return parseInt(el[1])})
    },

    renderGraph: function(tag, data) {
      var data = {
        labels: this.labels(data),
        datasets: [{
          scaleFontSize : 13,
          fillColor : "rgba(151,187,205,0.5)",
          strokeColor : "rgba(151,187,205,1)",
          pointColor : "rgba(151,187,205,1)",
          data: this.dataset(data)
        }]
      };
      var ctx = document.getElementById(tag.replace(".","")).getContext("2d");
      new Chart(ctx).Bar(data);
    },

    renderTopGraph: function(tag, data) {
      var smallSample = _.last(data, Settings.graphRendering.toDisplay);
      this.renderGraph(tag, smallSample.reverse());
    },

    renderHistogramGraph: function(tag, data) {
      var self = this;
      var makeHistogramLabels = function(hist, points) {
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
      var prepareHistogram = function() {
        var buckets = Settings.graphRendering.buckets;
        var points = self.dataset(data)
        var hist = d3.layout.histogram().bins(buckets)(points);
        var labels = makeHistogramLabels(hist, points)
        var len = _.map(hist, function(el) { return el.length });
        return _.zip(labels, len)
      }
      var histogramData = prepareHistogram(data);
      this.renderGraph(tag, histogramData);
    },

    switchGraph: function(graphType, cls, data) {
      var fullCls = cls + this.upperCase(graphType);
      if(graphType == "top") {
        this.renderTopGraph(fullCls, data);
      } else if(graphType == "histogram") {
        this.renderHistogramGraph(fullCls, data);
      }
    },
    switchStat: function(statType, cls, data) {
      var fullCls = cls + this.upperCase(statType);
      var dataset = this.dataset(data)
      var writeStat = function(value) {
        $(fullCls).text(" " + value);
      }
      if(statType == "total") {
        writeStat(this.sum(dataset));
      } else if (statType == "avg") {
        writeStat(this.avg(dataset));
      } else if (statType == "count") {
        writeStat(dataset.length);
      } else if (statType == "stdev") {
        writeStat(this.std(dataset).toFixed(2));
      }
    },
    upperCase: function(string) {
      return string.charAt(0).toUpperCase() + string.slice(1);
    },
    splitCamelCase: function(string) {
      return string.replace(/([A-Z])/g, ' $1');
    }
  };
});

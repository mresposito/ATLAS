define ([
  "underscore",
  "chart",
  "Stats"
], function (_, Chart, Stats) {
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

    switchGraph: function(graphType, cls, data) {
      var fullCls = cls + this.upperCase(graphType);
      if(graphType == "top") {
        this.renderGraph(fullCls, data);
      } else if(graphType == "histogram") {
        this.renderGraph(fullCls, data);
      }
    },
    switchStat: function(statType, cls, data) {
      var fullCls = cls + this.upperCase(statType);
      var dataset = this.dataset(data)
      if(statType == "total") {
        $(fullCls).text(this.sum(dataset));
      } else if (statType == "avg") {
        $(fullCls).text(this.avg(dataset));
      } else if (statType == "count") {
        $(fullCls).text(dataset.length);
      } else if (statType == "stdev") {
        $(fullCls).text(this.std(dataset));
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

define ([
  "jquery",
  "underscore",
  "backbone",
  "d3", 
  "models/Utils"
], function($, _, Backbone, D3, Utils) {

  var baseData= "../../results/"

  return Backbone.Model.extend({

    renderColumn: function(column, tag) {
      var self = this;
      _.map(this.get("semesters"), function(semester) {
        var tagWithNoSemester = tag.replace(semester, "");
        var path = baseData + semester +
          "/" + tagWithNoSemester;
        // load the TSV
        d3.text(path + ".tsv", function(text) {
          var data = d3.tsv.parseRows(text);
          var parsed = _.zip(Utils.labels(data), Utils.dataset(data))
          var sortedData = _.sortBy(parsed, function(el) { return el[1];})
          self.renderData(sortedData, column, tag, semester)
        });
      });
    },

    renderData: function(data, column, tag, semester) {
      var cls = "." + tag
      var loopOnGraphs = function(graphs) {
        _.map(graphs, function(graph) {
          Utils.switchGraph(graph, cls, data)
        });
      }
      var loopOnStats = function(stats) {
        _.map(stats, function(stat) {
          Utils.switchStat(stat, cls, data)
        });
      }
      // render all graphs in aggregate
      if("graphs" in column) {
        loopOnGraphs(column.graphs)
      }
      // render all stats in aggregate
      if("stats" in column) {
        loopOnStats(column.stats)
      }
      // render the default graphs
      loopOnGraphs(this.get("data").graphs)
      loopOnStats(this.get("data").stats)
    }
  });

});

define ([
  "jquery",
  "underscore",
  "backbone",
  "jade",
  "settings",
  "models/Utils",
  "text!/js/views/classes.jade",
  "text!/js/views/forums.jade"
], function($, _, Backbone, Jade, Settings, Utils, ClassesHTML, ForumsHTML) {

  return Backbone.View.extend({

    initialize: function(options) {
      var aggregations = this.model.get("data").aggregations;
      for(tag in aggregations) {
        this.renderClassGraph(aggregations[tag])
      }
    }, 

    hasHistogram: function(column) {
      if ("graphs" in column) {
        return column.graphs.indexOf("histogram") > -1;
      } else {
        return false;
      }
    },

    renderClassGraph: function(aggregate) {
      var self = this;
      var data = this.model.get("data")
      var templatePath = "text!/js/views/" + data.template + ".jade"
      var template = Jade.compile(require(templatePath));
      var columns = data.columns
      var templateVariables = {
        columns: columns,
        camel: Utils.splitCamelCase,
        hasHistogram: self.hasHistogram
      }
      // prepare the template
      var fullVariables = _.extend(aggregate, templateVariables)
      $(this.el).append(template(fullVariables));
      // Render a simple class graph
      _.map(columns, function(col) {
      var fullTag = aggregate.tag + col.tag
        self.model.renderColumn(col, fullTag)
      })
    }
  });
});

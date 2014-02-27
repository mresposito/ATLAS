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

    renderClassGraph: function(aggregate) {
      var self = this;
      var templatePath = "text!/js/views/" + this.model.get("data").template + ".jade"
      var template = Jade.compile(require(templatePath));
      var columns = this.model.get("data").columns
      // prepare the template
      var fullVariables = _.extend(aggregate, {columns: columns, camel: Utils.splitCamelCase})
      $(this.el).append(template(fullVariables));
      // Render a simple class graph
      _.map(columns, function(col) {
        self.model.renderColumn(col, aggregate)
      })
    }
  });
});

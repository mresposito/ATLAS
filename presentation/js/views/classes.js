define ([
  "jquery",
  "underscore",
  "backbone",
  "jade",
  "settings",
  "models/Utils",
  "text!/js/views/classes.jade"
], function($, _, Backbone, Jade, Settings, Utils, ClassesHTML) {

  return Backbone.View.extend({

    initialize: function(options) {
      var aggregations = this.model.get("data").aggregations;
      for(tag in aggregations) {
        this.renderClassGraph(aggregations[tag])
      }
    }, 

    renderClassGraph: function(aggregate) {
      var self = this;
      var template = Jade.compile(ClassesHTML);
      var columns = this.model.get("data").columns
      // prepare the template
      var fullVariables = _.extend(aggregate, {columns: columns, camel: Utils.splitCamelCase})
      console.log(fullVariables)
      $(this.el).append(template(fullVariables));
      // Render a simple class graph
      _.map(columns, function(col) {
        self.model.renderColumn(col, aggregate)
      })
    }
  });
});

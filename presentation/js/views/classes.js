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

    hasProperty: function(column, property) {
      if ("graphs" in column) {
        return column.graphs.indexOf(property) > -1;
      } else {
        return false;
      }
    },

    renderClassGraph: function(aggregate) {
      var self = this;
      this.createTemplate(aggregate);
      var columns = this.model.get("data").columns
      // Render a simple class graph
      _.map(columns, function(col) {
        self.model.renderColumn(col, aggregate)
      })
    },

    createTemplate: function(aggregate) {
      var self = this;
      var data = this.model.get("data")
      var templatePath = "text!/js/views/" + data.template + ".jade"
      var template = Jade.compile(require(templatePath));
      var columns = data.columns
      for(var idx in this.model.get("semesters")) {
        var semester = this.model.get("semesters")[idx]
        var templateVariables = {
          columns: columns,
          camel: Utils.splitCamelCase,
          hasProperty: self.hasProperty,
          semester: semester,
          prettySemester: Utils.prettySemester(semester)
        }
        // prepare the template
        var fullVariables = _.extend(aggregate, templateVariables)
        $(this.el).append(template(fullVariables));
      }
    }
  });
});

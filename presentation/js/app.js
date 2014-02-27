require.config({
  paths: {
    jquery: "//ajax.googleapis.com/ajax/libs/jquery/2.1.0/jquery.min",
    underscore: "//cdnjs.cloudflare.com/ajax/libs/underscore.js/1.5.2/underscore-min",
    backbone: "//cdnjs.cloudflare.com/ajax/libs/backbone.js/1.1.0/backbone-min",
    chart: "/lib/js/Chart.min",
    d3: "/lib/js/d3.min",
    jade: "//cdnjs.cloudflare.com/ajax/libs/jade/1.1.5/jade.min"
  },
  shim: {
    jquery: {
      exports: "$"
    },
    underscore: {
      exports: "_"
    },
    backbone: {
      deps: ["underscore"],
      exports: "Backbone"
    },
    chart: {
      exports: "Chart"
    },
    d3: {
      expents: "D3"
    },
    jade: {
      exports: "Jade"
    }
  }
});

require ([
  "jquery",
  "underscore",
  "backbone",
  "settings",
  "views/classes",
  "models/Graph"
], function($, _, Backbone, Settings, Classes, Graph) {

  var $el = $(".slides");
  _.map(Settings.series, function(serie) {
    new Classes({
      el: $el,
      model: new Graph({
        data: serie,
        semesters: Settings.semesters
      })
    })
  });

  $el.append("<section><h2>Thank you for your attention</h2></section>")
});

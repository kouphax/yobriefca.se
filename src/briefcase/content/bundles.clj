(ns briefcase.content.bundles
  (require [optimus.assets :as assets]))

(defn style-bundle
  "CSS Optimus bundle so it can be concatenated and minified on release"
  []
  (assets/load-bundle "static" "styles.css" ["/stylesheets/fontello.css"
                                             "/stylesheets/styles.css"
                                             "/stylesheets/syntax.css"]))
(defn static-bundle
  "Static bundle includes everything we dont want optimised but still
   pushed through Optimus.  Basically my maven repo and the images as
   these are refered in markdown and not rewritten"
  []
  (concat (assets/load-bundle "static" "images"         [#"/images/.*"
                                                         "/favicon.ico"])
          (assets/load-bundle "static" "maven"          [#"/maven/.*"])
          (assets/load-bundle "static" "presentations"  [#"/presentations/.*"])
          (assets/load-bundle "static" "assets"         [#"/assets/.*"])))

(defn metrics-page-bundle
  "Metrics bundle includes the css and js for the metrics contribution page"
  []
  (concat (assets/load-bundle "static" "metrics" ["/javascripts/d3.v3.min.js"
                                                  "/javascripts/metrics.js"
                                                  "/stylesheets/metrics.css"])))

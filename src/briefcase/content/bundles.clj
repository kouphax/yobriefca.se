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
  (concat (assets/load-bundle "static" "images"         [#"/images/.*"])
          (assets/load-bundle "static" "maven"          [#"/maven/.*"])
          (assets/load-bundle "static" "random-js"      [#"/javascripts/.*"])
          (assets/load-bundle "static" "presentations"  [#"/presentations/.*"])))


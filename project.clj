(defproject briefcase "1.0.0"
  :description "yobriefca.se site generator"
  :url "http://yobriefca.se"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ring "0.8.10"]]
  :ring { :handler briefcase.core/app }
  :aliases {"build-site" ["run" "-m" "briefcase.core/export"]}
  :dependencies [[org.clojure/clojure   "1.6.0"]
                 [stasis                "1.0.0"]
                 [ring/ring-core        "1.2.2"]
                 [me.raynes/cegdown     "0.1.1"]
                 [hiccup                "1.0.5"]
                 [clj-yaml              "0.4.0"]
                 [clj-time              "0.6.0"]
                 [me.raynes/fs          "1.4.4"]
                 [slugger               "1.0.1"]
                 [com.cemerick/url      "0.1.1"]
                 [enlive                "1.1.5"]
                 [clygments             "0.1.1"]
                 [optimus               "0.14.2"]
                 [org.clojure/data.xml  "0.0.7"]
                 [cheshire              "5.3.1"]])

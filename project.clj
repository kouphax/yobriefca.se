(defproject briefcase "1.0.0"
  :description "yobriefca.se site generator"
  :url "http://yobriefca.se"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ring "0.8.10"]]
  :ring { :handler briefcase.core/app }
  :aliases {"build-site" ["run" "-m" "briefcase.core/export"]}
  :dependencies [[org.clojure/clojure   "1.7.0"]
                 [stasis                "2.2.2"]
                 [ring/ring-core        "1.4.0"]
                 [me.raynes/cegdown     "0.1.1"]
                 [hiccup                "1.0.5"]
                 [clj-yaml              "0.4.0"]
                 [clj-time              "0.10.0"]
                 [me.raynes/fs          "1.4.6"]
                 [slugger               "1.0.1"]
                 [com.cemerick/url      "0.1.1"]
                 [enlive                "1.1.6"]
                 [clygments             "0.1.1"]
                 [optimus               "0.18.1"]
                 [org.clojure/data.xml  "0.0.8"]
                 [cheshire              "5.5.0"]])

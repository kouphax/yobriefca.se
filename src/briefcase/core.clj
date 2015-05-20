(ns briefcase.core
  (require [stasis.core           :as stasis]
           [briefcase.views       :as views]
           [optimus.prime         :as optimus]
           [optimus.optimizations :as optimizations]

           [optimus.strategies            :refer [serve-live-assets]]
           [optimus.export                :refer :all]
           [ring.middleware.content-type  :refer [wrap-content-type]]
           [briefcase.content.core        :refer [entry-sources index-sources entries breakdown-sources]]
           [briefcase.content.bundles     :refer :all]
           [briefcase.content.feed        :refer [atom-sources]]
           [briefcase.csv                 :as    csv]
           [briefcase.content.categories  :refer [category-sources]]))

(defn pages
  "defines the overall strcuture of the site via discrete sources"
  []
  (let [entries (entries)]
    (stasis/merge-page-sources
      { :static          { "/index.html"       #(views/index %)
                           "/background/"      #(views/background %)
                           "/404.html"         #(views/fourohfour %)
                           "/testimonials/"    #(views/testimonials %)
                           "/metrics/"         #(views/metrics %)
                           "/metrics/data.csv" (csv/daily-contributions entries) }
        :rss             (atom-sources entries)
        :categories      (category-sources entries)
        :daily-indexes   (breakdown-sources entries)
        :content         (entry-sources entries)
        :content-indexes (index-sources entries) })))

(def app
  "Entry point for running the site in dev mode via ring. Assets controlled
   via optimus are served without any sort of optimisation"
  (let [pages (pages)]
    (-> (stasis/serve-pages pages)
        (optimus/wrap
          (fn [] (concat (style-bundle)
                         (static-bundle)
                         (metrics-page-bundle)))
          optimizations/none serve-live-assets)
        (wrap-content-type))))

(defn export
  "When building the site this is the function that clears out the build
   directory and generates a fresh site along with optomised assets.

   Static assets that we dont want to optimise are handled seperatley"
  []
  (let [styles     (optimizations/all (style-bundle) {})
        static     (optimizations/none (static-bundle) {})
        metrics    (optimizations/none (metrics-page-bundle) {})
        output-dir "build"]
    (println "Clearing output directory")
    (stasis/empty-directory! output-dir)
    (println "Exporting optimised assets (css, javascript)")
    (save-assets styles output-dir)
    (println "Exporting non-optimised assets (maven, images)")
    (save-assets static  output-dir)
    (println "Exporting assets for metrics page (css, javascript)")
    (save-assets metrics output-dir)
    (println "Exporting CNAME")
    (spit (str output-dir "/CNAME") "yobriefca.se")
    (println "Exporting site")
    (stasis/export-pages (pages) output-dir { :optimus-assets styles })))

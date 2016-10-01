(ns briefcase.content.core
  (require [briefcase.views         :as views]
           [slugger.core            :refer [->slug]]
           [briefcase.content.utils :refer [slurp-content]]
           [clj-time.format :as f]
           [clj-time.coerce :as c]))

(defn to-slug
  "slugger attempts to match stringex style slugification.  However due to some
   legacy drift in the generated links on the site there are some differences so
   we thread through them here and make explicit changes so that we are borking
   urls"
  [title]
  (-> (->slug title)
      (clojure.string/replace #"-\+-" "-plus-")
      (clojure.string/replace #"-{2,}" "-")
      (clojure.string/replace #"^two-hidden-gems-in-play-2s-template-engine$" "two-hidden-gems-in-play-2-s-template-engine")))

(defn article-uri
  "derives the final uri path for an article so that 2011-12-12-test-this.md
   would become /blog/2011/12/12/test-this/index.html for example

  [TODO] This could be made more generic for other content types but we need
         to make sure we aren't breaking older urls too"
  [article]
  (let [formatter  (java.text.SimpleDateFormat. "yyyy/MM/dd")
        date-part  (.format formatter (:date article))
        title-part (to-slug (:title article))]
    (str "/blog" "/" date-part "/" title-part "/")))

(defn unlisted-uri
  "derives the final uri path for an article so that 2011-12-12-test-this.md
   would become /blog/2011/12/12/test-this/index.html for example

  [TODO] This could be made more generic for other content types but we need
         to make sure we aren't breaking older urls too"
  [article]
  (let [title-part (to-slug (:title article))]
    (str "/unlisted" "/" title-part "/")))

(defn screencast-uri
  "generates a uri for access non-external screencast entries"
  [screencast]
  (if (get screencast :external false)
    (:url screencast)
    (let [path (:path screencast)
          name (nth (re-matches #"^/\d\d\d\d-\d\d-\d\d-(.*)\.md$" path) 1)]
      (str "/screencasts/" name "/"))))

(defn entries
  []
  (let [source-dir (or (System/getenv "CONTENT_SOURCES")
                       "resources/content/entries")
        entries    (slurp-content source-dir)]
    (println (str "Building site from entries in " source-dir))
    (->> entries
         (filter :published)
         (map #(assoc % :uri
                      (case (keyword (:type %))
                        :article    (article-uri %)
                        :unlisted   (unlisted-uri %)
                        :screencast (screencast-uri %)
                        :talk       (:url %)
                        :project    (:url %))))
         (sort-by :date)
         (reverse))))

(defn render-entry
  "build the view function for the item"
  [entry]
  (case (keyword (:type entry))
    :article    #(views/article % entry)
    :unlisted   #(views/article % entry)
    :screencast #(views/screencast % entry)))

(defn render-list
  "renders a list or index view based on the entires. Janky multimethod
   jiggerypokery here that makes no sense and should be refactored"
  ([entries]
   (let [type           (-> entries first :type keyword)
         title-singular (-> type
                            (name)
                            (clojure.string/capitalize))
         title          (if (= :unlisted type)
                          title-singular
                          (str title-singular "s"))]
     (if (= :talk type)
       #(views/gallery-view % title entries)
       #(views/list-view %  title entries))))
  ([entries title]
   #(views/list-view % title entries)))

(defn unlisted-sources [entries]
  (let [unlisted (filter :unlisted entries)]
    (zipmap (map :uri unlisted)
            (map render-entry unlisted))))

(defn entry-sources [entries]
  (let [internal (filter #(and (not (:external %)) (not (:unlisted %))) entries)]
    (zipmap (map :uri internal)
            (map render-entry internal))))

(defn index-sources [entries]
  (let [types (group-by :type entries)]
    (zipmap (map #(str "/" (name %) (if (= % "unlisted") "/" "s/")) (keys types))
            (map render-list (vals types)))))

(defn breakdown-sources [entries]
  (let [formatter  (f/formatter "yyyy/MM/dd")
        grouper   #(str "/" (f/unparse formatter (c/from-date (:date %))) "/")
        dates      (group-by grouper entries)]
    (zipmap (keys dates)
            (map #(render-list % "On this day...") (vals dates)))))

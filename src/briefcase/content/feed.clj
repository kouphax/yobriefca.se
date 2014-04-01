(ns briefcase.content.feed
  (require [clojure.data.xml :as xml]
           [briefcase.content.categories :as categories]
           [slugger.core :refer [->slug]]))

(defn- entry
  "Generates the atom xml for a single content entry"
  [post]
  [:entry
    [:title   (:title post)]
    [:updated (:date post)]
    [:author  [:name "James Hughes"]]
    [:link    {:href (if (:external post)
                       (:uri post)
                       (str "http://yobriefca.se" (:uri post)))}]
    [:id      (str "urn:yobriefca-se:feed:post:" (:title post))]
    [:content {:type "html"}  (:html post)]])

(defn- atom-xml
  "Generates the xml document for atom feed of all content on the site"
  [category posts]
  (let [category-id (->slug category)]
    (xml/emit-str
      (xml/sexp-as-element
        [:feed {:xmlns "http://www.w3.org/2005/Atom"}
          [:id      (str "urn:yobriefca-se:feed:" category-id)]
          [:updated (-> posts first :date)]
          [:title   {:type "text"} (str "Yo! Briefcase: " category-id)]
          [:link    {:rel "self" :href (str "http://yobriefca.se/feed/" category-id ".xml")}]
          (map entry posts)]))))

(defn atom-sources
  "Generates the stasis compatible sources map from the atom feed. This is
   just going to be a single entry but hey ho."
  [entries]
  (into { "/feed/index.xml" (atom-xml "index" entries) }
        (for [category (categories/categories-for-entries entries)
              :let [title   (:title category)
                    uri     (str "/feed/" (->slug title) ".xml")
                    content (categories/category-content category entries)
                    feed    (atom-xml title content)]]
          [uri feed])))

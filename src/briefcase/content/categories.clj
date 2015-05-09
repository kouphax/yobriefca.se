(ns briefcase.content.categories
  (require [briefcase.views :as views]
           [slugger.core    :refer [->slug]]))

(defn- to-category-map
  "Taking a cateogry name this converts it into a map that can be piped around
   like all other bots of content

   [TODO] The url generation is also done in `views` so we could make this a
          utility class"
  [name]
  { :title name
    :uri   (str "/categories/" (->slug name) "/") })

(defn- merge-latest-date-from-content
  "Given a sequence of items/content this function

   1. finds all content of a desired category
   2. finds the most recent content item
   3. adds its creation date into the category map

   Think of it like a last-updated timestamp for a category"
  [content category]
  (->> content
       (filter #(some #{(:title category)} (:categories %)))
       (sort-by :date)
       (last)
       (:date)
       (assoc category :date)))

(defn categories-for-entries
  "a distinct list of all categories from the passed in content map"
  [content]
  (->> content
       (map :categories)
       (flatten)
       (distinct)
       (map to-category-map)
       (map #(merge-latest-date-from-content content %))
       (sort-by :title)))

(defn category-content
  "will find all content related to a particular category"
  [{ title :title } source]
  (filter #(some #{title} (:categories %)) source))

(defn category-sources
  "Generates a stasis compatible map of sources for site categories"
  [entries]
  (let [categories (categories-for-entries entries)]
    (into { "/categories/"  #(views/list-view % "Categories" categories) }
          (for [category categories
                :let [uri     (:uri category)
                      title   (:title category)
                      content (category-content category entries)
                      view    #(views/list-view % title content)]]
            [uri view]))))




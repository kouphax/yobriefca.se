(ns briefcase.csv
  (require [clj-time.format :as f]
           [clj-time.coerce :as c]
           [clojure.string :refer [join]]))

(defn- as-contribution-csv-lines [entries]
  (let [entries-by-date (into (sorted-map) (group-by :date entries))
        date-formatter (f/formatter "dd/MM/yyyy")
        date-column-formatter #(f/unparse date-formatter (c/from-date %))
        value-column-formatter #(str (count %))]
    (map #(str (date-column-formatter (first %)) "," (value-column-formatter (last %))) entries-by-date)))

(defn daily-contributions [entries]
  (let [header "Date,Count"
        lines  (as-contribution-csv-lines entries)]
    (join "\n" (cons header lines))))


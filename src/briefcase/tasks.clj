(ns briefcase.tasks)

(defn new-article
  "creates a new article in the drafts folder with the basic frontmatter for the
   article to reduce copypasta for me"
  [title & categories]
  (let [date         (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (java.util.Date.))
        slug         (-> title
                         (clojure.string/lower-case)
                         (clojure.string/replace #" " "-")
                         (clojure.string/replace #"'" ""))
        frontmatter  (str "---\n"
                          "date: " date "T00:00:00Z\n"
                          "title: \"" title "\"\n"
                          "published: true\n"
                          "categories: [" (clojure.string/join "," categories)  "]\n"
                          "type: article\n"
                          "external: false\n"
                          "---\n")
        filename     (str date "-" slug ".md")
        path-to-file (str "./resources/content/drafts/" filename)]
    (spit path-to-file frontmatter)
    (print "Created: " path-to-file)))

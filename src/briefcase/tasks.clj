(ns briefcase.tasks)

(declare new-thing)

(defn new-article
  "creates a new article in the drafts folder with the basic frontmatter for the
   article to reduce copypasta for me"
  [title & categories]
  (new-thing :article title categories))

(defn new-unlisted
  "creates a new unlisted in the drafts folder with the basic frontmatter for the
   article to reduce copypasta for me"
  [title & categories]
  (new-thing :unlisted title categories))

(defn new-thing
  "creates a new article in the drafts folder with the basic frontmatter for the
   article to reduce copypasta for me"
  [type title categories]
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
                          "type:" (name type) "\n"
                          "external: false\n"
                          "---\n")
        filename     (str date "-" slug ".md")
        path-to-file (str "./resources/content/drafts/" filename)]
    (spit path-to-file frontmatter)
    (print "Created: " path-to-file)))

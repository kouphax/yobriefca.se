(ns briefcase.views
  (require [clj-yaml.core     :as yaml]
           [me.raynes.cegdown :as md]
           [optimus.hiccup    :as bundles]
           [cemerick.url      :refer [url-encode]]
           [slugger.core      :refer [->slug]]
           [hiccup.page       :refer [html5]]
           [clj-time.core     :refer [minus months before?]]
           [clj-time.coerce   :refer [from-date]]))

(declare main-layout)

(defn- pretty-date
  "Converts standard dates into more human readable dates eg. 20/01/1980 will
   be converted to January, 20th 1980"
  [date]
  (.format (java.text.SimpleDateFormat. "MMMM dd, yyyy") date))

(defn- twitter-url
  "Generates a 'Tweet this' compatibale link for the passed in content"
  [data]
  (str "https://twitter.com/intent/tweet"
       "?url="  (url-encode (str "http://yobriefca.se" (:uri data)))
       "&text=" (url-encode (:title data))
       "&via=kouphax"))

(defn- category-anchor
  "Generates a link tage for a particular category"
  [category]
  (let [link (str "/categories/" (->slug category))]
    (hiccup.core/html [:a { :href link } category])))

(defn- categories
  "For a given bit of content this function generates an HTML snippet of
   all the categories it is declared under and links to these categories"
  [data]
  (clojure.string/join ", " (map category-anchor (:categories data))))

(defn- published
  "Generates the 'Published In ...' tag line that is renderd at the bottom
   of every piece of content on the site"
  [data]
  (str "Published in " (categories data) " on " (pretty-date  (:date data))))

(defn- footer
  "Generates the footer appended at the end of every content page"
  [data]
  (hiccup.core/html
    [:a.twitter { :target "_blank" :href (twitter-url data) } "Tweet This"]
    [:div.dater (pretty-date (:date data))]
    [:div.categories (published data)]))

(def ^:private six-months-ago 
  (minus (from-date (java.util.Date.)) (months 6)))

(defn- old-entry-warning
  "Generates a warning for entries that are more than 6 months or so old"
  [data]
  (when (before? (:date data) six-months-ago)
    (hiccup.core/html
      [:blockquote.warning "This post is over 6 months old. 
      	Some details, especially technical, may have changed 
      	since posting."])))

(defn index
  "Renders the landing page of the site"
  [request]
  (main-layout request "yobriefca.se"
               (md/to-html (slurp "resources/content/index.md"))))


(defn article
  "Renders an article"
  [request data]
  (main-layout request (:title data)
               [:h1 (:title data)]
               [:div.post (:html data)]
               (footer data)))

;(defn- vimeo-link
;  "Videos are hosted on vimeo.  This function generates a vimeo link based on
;   a contents video_url value"
;  [data]
;  (let [code (:video_url data)]
;    (str "http://player.vimeo.com/video/" code "?title=0&amp;byline=0&amp;portrait=0")))

(defn screencast
  "Renders a non-external screencast page"
  [request data]
  (main-layout request (:title data)
               (old-entry-warning data)
               [:div.vimeo
                 [:iframe { :src (:url data)
                            :width "631"
                            :height "355"
                            :frameborder "0"
                            :webkitAllowFullScreen true
                            :mozallowfullscreen true
                            :allowFullScreen true }]]
               [:h1.title (:title data)]
               (:html data)
               (footer data)))

(defn background
  "Renders the background page.  This page is drvien by YML configuration to
   support alternative views and outputs (pdf for example)."
  [request]
  (let [data (yaml/parse-string (slurp "resources/content/background.yml"))]
    (main-layout request "yobriefca.se"
      [:h1 "James Hughes"]
      [:ul
        (for [{ href :href name :name } (:links data)]
          [:li [:a { :href href } name]])]
      [:h2 "Abstract"]
      (md/to-html (:abstract data))
      [:div.experience
        [:h1 "Experience"]
        (for [org (:experience data)]
          [:div
            [:h2 (:name org)]
            (for [exp (:experience org)]
              [:div
                [:h3 (:role exp)]
                [:h4 (:dates exp)]
                [:h5 (clojure.string/join ", " (:technologies exp))]
                (md/to-html (:description exp))])])])))

(defn testimonials
  "Renders the testimonials page.  Driven by YML configuration to make it
   easier to add testimonials in the future and generate extra views."
  [request]
  (let [data (yaml/parse-string (slurp "resources/content/testimonials.yml"))]
    (main-layout request "yobriefca.se"
      [:h1.title.testimonial-header "Testimonials"]
      [:div.testimonial
       (for [{ text :text from :from } data]
         [:span
           (md/to-html text)
           [:div " - " from]])])))

(defn list-view
  "Many pages on the site are simply lists with titles and publication dates
   this function will take any of these item lists and render this view"
  [request list-title items]
  (main-layout request "yobriefca.se"
               [:h1.title list-title]
               [:div.entries
                (for [thingie items
                      :let [title (:title thingie)
                            url   (:uri thingie)
                            date  (pretty-date (:date thingie))]]
                  [:h3
                   [:a { :href url } title]
                   [:div.published_date "Published on " date]])]))

(defn- main-layout
  "provides the main page layout for the site"
  [request title & body]
  (html5
    [:head
     [:meta { :charset "utf-8" }]
     [:meta { :content "IE=edge,chrome=1" :http-equiv "X-UA-Compatible" }]
     [:meta { :name "viewport" :content "width=device-width, user-scalable=no" }]
     [:meta { :name "description" :content title}]
     [:link { :href "http://fonts.googleapis.com/css?family=Lora:400,700|Open+Sans:400,700" :rel "stylesheet" :type "text/css" }]
     (bundles/link-to-css-bundles request  ["styles.css"])
     [:title title]]
    [:body
     [:div.row body]
     [:div.homer
       [:a { :href "/" }
         [:i.icon-briefcase { :style "font-size:32px;" }]]]
     [:script { :type "text/javascript" } "(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-19143623-5', 'yobriefca.se');
  ga('send', 'pageview');"]]))

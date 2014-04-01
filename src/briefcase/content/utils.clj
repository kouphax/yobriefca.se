(ns briefcase.content.utils
  (require [me.raynes.cegdown      :as md]
           [clygments.core         :as pygments]
           [net.cgrand.enlive-html :as enlive]
           [clj-yaml.core          :as yaml]
           [stasis.core            :as stasis]
           [clojure.string         :refer [split join]]))

(defn- extract-code
  "The way pygments and enlive and pegdown all work together we get this
   nesting of pre and code tags that don't work correctly. This method unwraps
   the code once it has been highlighted so we dont get unnecessary content"
  [highlighted]
  (-> highlighted
      (java.io.StringReader.)
      (enlive/html-resource)
      (enlive/select [:pre])
      (first)
      (:content)))

(defn- clean-lang
  "the language specified in some of the older content files are either

   1. not present, or,
   2. present but wrapped in icky whitespace

   This function strips this whitespace and defaults non-existent values
   to plain `text`"
  [original]
  (clojure.string/trim (or original "text")))

(defn highlight
  "Performs pygments based syntax highlighting on a provided block of code and
   then unwraps the block as unwanted wrappage is occuring"
  [node]
  (let  [code (->> node :content  (apply str))
         lang (->> node :attrs :class clean-lang keyword)]
    (assoc node :content (-> code
                             (pygments/highlight lang :html)
                             (extract-code)))))

(defn- highlight-code-blocks
  "for a given generated html page this function will find pre/code blocks and
   highlight them with pygments then apply a class of highlight to the main pre
   so that the css can works its colour magic"
  [page]
  (enlive/sniptest page
    [:pre :code] highlight
    [[:pre (enlive/has [:code])]] (enlive/add-class "highlight")))

(defn to-html
  "Converts markdown to html and attempts to highlight any code blocks that
   exist"
  [markdown]
  (-> markdown
      (md/to-html [:autolinks :fenced-code-blocks :strikethrough])
      (highlight-code-blocks)))

(defn frontmatter
  "Extracts the YAML front matter from a content block.  This is defined as the
   block between the --- at the start of the document/content block"
  ([content]
    (yaml/parse-string (nth (split content #"---") 1))))

(defn content-body
  "extracts the content body from a content block (basically all the stuff
   after the front matter"
  [content]
  (join (drop 2 (split content #"---"))))

(defn- slurp-md
  "slurps all the markdown files from a given directory and sub subdirectories"
  [dir]
  (stasis/slurp-directory dir #".*\.md$"))

; these articles are already HTML and have had some post-processing applied
; if we attempt to markdwon transform them the post processed elements get
; transformed so we need to exclude them here.  This is the gist unembedding
; process
(def exclusions ["/2011-06-21-microorms-for-dotnet-inserts-updates-deletes.md"
                 "/2009-11-23--less-dynamic-css-for-net.md"
                 "/2009-04-15-updatepanels-alternatives.md"
                 "/2010-10-21-asp-net-mvc3-jsonvalueproviderfactory.md"
                 "/2011-05-23-micro-web-frameworks-101-nancy.md"
                 "/2011-08-16-opinionated-project-structure.md"
                 "/2011-07-18-micro-web-frameworks-101-tinyweb.md"
                 "/2011-05-18-why-use-micro-web-frameworks-in-net.md"
                 "/2011-07-05-nspec-debuggershim.md"
                 "/2009-10-19-lazy-function-definition-pattern.md"
                 "/2011-03-29-serialising-net-resources-to-json-for-web-apps.md"
                 "/2011-02-01-jquery-1-5-released.md"
                 "/2010-10-19--head-ache-including-javascript-in-asp-net-master-pages.md"
                 "/2010-10-18-t4mvc-strong-typing-vs-magic-strings.md"
                 "/2010-10-23-unobtrusive-javascript-in-mvc3.md"
                 "/2010-10-16-jquery-1-4-3-jquery-mobile-released.md"
                 "/2010-10-24-validateinputattribute-changes-in-mvc3-beta-2-.md"
                 "/2010-11-10-remote-validation-in-asp-net-mvc-3-rc1.md"
                 "/2011-05-31-announcing-baler.md"
                 "/2011-05-19-micro-web-frameworks-101-jessica.md"
                 "/2010-11-20-better-json-serialisation-for-asp-net-mvc.md"
                 "/2011-04-04-a-tale-of-unit-testing-technologies.md"
                 "/2010-11-18-the-razor-view-engine.md"
                 "/2010-10-12-outside-events-jquery-plugin.md"
                 "/2010-10-18-strong-typing-vs-magic-strings-part-ii-.md"
                 "/2010-10-18-jquery-mobile-quick-look.md"
                 "/2011-06-18-microorms-for-dotnet-stored-procedures.md"
                 "/2011-02-24-mobile-redirection-for-web-applications-in-asp-net-mvc.md"
                 "/2010-10-16-microsoft-s-trio-of-jquery-plugins.md"
                 "/2011-06-09-baler-1-2-and-extensions.md"
                 "/2010-01-13-css-hacks-for-ie-unnecessary.md"
                 "/2011-05-17-further-adventures-in-unit-testing.md"
                 "/2010-10-19-client-side-processing-vs-responsiveness.md"
                 "/2010-10-18-multi-touch-reactive-extensions.md"
                 "/2011-06-22-micro-web-frameworks-101-anna.md"
                 "/2010-07-28-asp-net-mvc-3-preview-1-first-look.md"
                 "/2010-10-26-mobilize-adapting-the-asp-net-mvc-project-template-for-jquery-mobile.md"
                 "/2011-06-16-microorms-for-dotnet-select-one.md"
                 "/2010-10-19-javascript-proxy-pattern.md"
                 "/2011-03-13-coffeescript-javascript-sans-bad-parts.md"
                 "/2011-02-11-entity-framework-code-first-head-first.md"
                 "/2009-05-19-javascript-method-overloading.md"
                 "/2010-10-22-practical-jquery-mobile-with-asp-net-mvc.md"
                 "/2011-01-07-vs2010-mvc-javascript-intellisense-html-hack-.md"
                 "/2011-01-17-using-coffeescript-in-net.md"
                 "/2010-11-09-all-change-validateinputattribute-and-skiprequestvalidation.md"
                 "/2010-11-12-underscore-js-the-javascript-utility-library.md"
                 "/2010-10-14-modernizr-requirejs.md"])

(defn- to-content-map
  "Converts a stasis map entry into the basic content map so it can be
   manipulated back into a statis source map via the specific pipeline"
  [[path content]]
  (assoc (frontmatter content)
         :content (content-body content)
         :html    (if (some #{path} exclusions)
                    (content-body content)
                    (to-html (content-body content)))
         :path    path))

(defn slurp-content
  "slurps a given resource directory and converts markdown entries into content
   maps"
  [dir]
  (map to-content-map (slurp-md dir)))


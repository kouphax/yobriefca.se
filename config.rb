require "redcarpet"
require "stringex"
require 'builder'

set :markdown_engine , :redcarpet
set :markdown        , :fenced_code_blocks => true, :smartypants => true
set :css_dir         , 'stylesheets'
set :js_dir          , 'javascripts'
set :images_dir      , 'images'

activate :livereload
activate :syntax

configure :build do
  activate :minify_css
  activate :minify_javascript
  activate :cache_buster
end

class Article

  @@dir         = "#{Dir.pwd}/source/data/articles/"
  @@date_range  = @@dir.size..@@dir.size+10

  attr_accessor :published, :year, :month, :day, :title, :file, :url, :slug, :date, :type, :body

  def initialize(resource)

    @published = resource.metadata[:page]["published"] || true

    # assumption that the file is named correctly!
    date_parts = resource.source_file[@@date_range].split('-')

    @year  = date_parts[0]
    @month = date_parts[1]
    @day   = date_parts[2]
    @date  = Date.new(@year.to_i, @month.to_i, @day.to_i)
    @title = resource.metadata[:page]["title"]
    @file  = resource.source_file["#{Dir.pwd}/source".size..-1].sub(/\.erb$/, '').sub(/\.markdown$/, '')
    @url   = "/blog/#{@year}/#{@month}/#{@day}/#{@title.to_url}"
    @slug  = resource.metadata[:page]["slug"] || "" 
    @type  = :article
    @body  = resource.render
  end

  def self.dir 
    @@dir
  end
end

class Screencast

  @@dir        = "#{Dir.pwd}/source/data/screencasts/"
  @@date_range = @@dir.size..@@dir.size+10

  attr_accessor :type, :date, :title, :subtitle, :file, :url, :screenshot, :body

  def initialize(resource)
    @title      = resource.metadata[:page]["title"]
    @file       = resource.source_file["#{Dir.pwd}/source".size..-1].sub(/\.erb$/, '').sub(/\.markdown$/, '')
    @screenshot = resource.metadata[:page]["screenshot"] || ""
    @sequence   = resource.metadata[:page]["sequence"]
    @date       = resource.metadata[:page]["date"]
    @subtitle   = resource.metadata[:page]["subtitle"] || ""
    @url        = "/screencasts/#{@sequence}-#{@title.to_url}"
    @type       = :screencast
    @body       = resource.render
  end

  def self.dir 
    @@dir
  end
end

class Talk

  @@dir        = "#{Dir.pwd}/source/data/talks/"
  @@date_range = @@dir.size..@@dir.size+10

  attr_accessor :type, :date, :title, :location, :video, :presentation, :url, :body

  def initialize(resource)
    @date         = resource.metadata[:page]["date"]
    @title        = resource.metadata[:page]["title"]
    @file         = resource.source_file["#{Dir.pwd}/source".size..-1].sub(/\.markdown$/, '')
    @type         = :talk
    @video        = resource.metadata[:page]["video"]
    @presentation = resource.metadata[:page]["presentation"]
    @url          = @video || @presentation
    @location     = resource.metadata[:page]["location"]
    @body         = ""
  end

  def self.dir 
    @@dir
  end
end

ready do

  articles    = []
  screencasts = []
  talks       = []

  sitemap.resources.each do |res| 
    case res.source_file 
    when /^#{Regexp.quote(Article.dir)}/
      article = Article.new(res)
      if article.published then
        articles.unshift article
        proxy "#{article.url}/index.html", article.file
      end
    when /^#{Regexp.quote(Screencast.dir)}/
      screencast = Screencast.new(res)
      screencasts.unshift screencast
      proxy "#{screencast.url}/index.html", screencast.file
    when /^#{Regexp.quote(Talk.dir)}/
      talks.unshift Talk.new(res)
    end
  end

  latest = [
    articles.first, 
    screencasts.first, 
    talks.first
  ].sort_by { |thing| 
    puts thing.date.to_s
    thing.date  
  }.last

  proxy "/blog/index.html",        "/writings.html",    :locals => { :articles => articles }
  proxy "/screencasts/index.html", "/screencasts.html", :locals => { :screencasts => screencasts }
  proxy "/talks/index.html",       "/talks.html",       :locals => { :talks => talks }
  proxy "/index.html",             "/dashboard.html",   :locals => { 
    :latest             => latest,
    :recent_articles    => articles[0..2],
    :recent_screencasts => screencasts[0..2],
    :recent_talks       => talks[0..2]
  }

  proxy "/feed/index.xml", "/feed.xml", :locals => { :items => (articles + screencasts + talks).sort_by { |item| item.date }.reverse }

  ignore "/writings.html"
  ignore "/dashboard.html"
  ignore "/talks.html"
  ignore "/screencasts.html"
  ignore "/data/*"
end
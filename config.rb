require 'redcarpet'
require 'stringex'
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

  attr_accessor :published, :year, :month, :day, :title, :file, :url, :slug, :date, :type, :body, :categories, :resource

  def initialize(resource)

    @resource  = resource
    @published = resource.metadata[:page]["published"]

    if @published == nil then
      @published = true
    end

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

    raw_categories = resource.metadata[:page]["categories"] || []
    if raw_categories.is_a? String then
      @categories = raw_categories.split(' ')
    else
      @categories = raw_categories
    end
  end

  def body
    @resource.render(:layout => false)
  end

  def self.dir
    @@dir
  end
end

class Screencast

  @@dir        = "#{Dir.pwd}/source/data/screencasts/"
  @@date_range = @@dir.size..@@dir.size+10

  attr_accessor :type, :date, :title, :subtitle, :file, :url, :screenshot, :body, :categories, :external

  def initialize(resource)

    @resource   = resource
    @title      = resource.metadata[:page]["title"]
    @file       = resource.source_file["#{Dir.pwd}/source".size..-1].sub(/\.erb$/, '').sub(/\.markdown$/, '')
    @screenshot = resource.metadata[:page]["screenshot"] || ""
    @external   = resource.metadata[:page]["external"] || false
    @sequence   = resource.metadata[:page]["sequence"]
    @date       = resource.metadata[:page]["date"]
    @subtitle   = resource.metadata[:page]["subtitle"] || ""
    @url        = if external then
      resource.metadata[:page]["url"]
    else
      "/screencasts/#{@sequence}-#{@title.to_url}"
    end
    @type       = :screencast

    raw_categories = resource.metadata[:page]["categories"] || []
    if raw_categories.is_a? String then
      @categories = raw_categories.split(' ')
    else
      @categories = raw_categories
    end
  end


  def body
    @resource.render(:layout => false)
  end

  def self.dir
    @@dir
  end
end

class Talk

  @@dir        = "#{Dir.pwd}/source/data/talks/"
  @@date_range = @@dir.size..@@dir.size+10

  attr_accessor :type, :date, :title, :location, :video, :presentation, :url, :body, :categories

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

    raw_categories = resource.metadata[:page]["categories"] || []
    if raw_categories.is_a? String then
      @categories = raw_categories.split(' ')
    else
      @categories = raw_categories
    end
  end

  def self.dir
    @@dir
  end
end

class Project

  @@dir        = "#{Dir.pwd}/source/data/projects/"
  @@date_range = @@dir.size..@@dir.size+10

  attr_accessor :type, :date, :title, :name, :description, :url, :body, :categories

  def initialize(resource)
    @date         = resource.metadata[:page]["date"]
    @name         = resource.metadata[:page]["name"]
    @description  = resource.metadata[:page]["description"]
    @title        = "#{@name}: #{@description}"
    @file         = resource.source_file["#{Dir.pwd}/source".size..-1].sub(/\.markdown$/, '')
    @type         = :project
    @url          = resource.metadata[:page]["url"]
    @body         = ""

    raw_categories = resource.metadata[:page]["categories"] || []
    if raw_categories.is_a? String then
      @categories = raw_categories.split(' ')
    else
      @categories = raw_categories
    end
  end

  def self.dir
    @@dir
  end
end

ready do

  articles    = []
  screencasts = []
  talks       = []
  projects    = []

  sitemap.resources.each do |res|
    case res.source_file
    when /^#{Regexp.quote(Article.dir)}/
      article = Article.new(res)
      if article.published then
        articles.unshift article
        proxy "#{article.url}/index.html", article.file, :locals => { :article => article }
      end
    when /^#{Regexp.quote(Screencast.dir)}/
      screencast = Screencast.new(res)
      screencasts.unshift screencast
      if !screencast.external then
        proxy "#{screencast.url}/index.html", screencast.file, :locals => {:screencast => screencast}
      end
    when /^#{Regexp.quote(Talk.dir)}/
      talks.unshift Talk.new(res)
    when /^#{Regexp.quote(Project.dir)}/
      projects.unshift Project.new(res)
    end
  end
  
  articles    = articles.sort_by { |item| item.date }.reverse
  screencasts = screencasts.sort_by { |item| item.date }.reverse
  projects    = projects.sort_by { |item| item.date }.reverse
  talks       = talks.sort_by { |item| item.date }.reverse
  zipped      = (articles + screencasts + talks + projects).sort_by { |item| item.date }.reverse

  proxy "/index.html"              , "/dashboard.html"    , :locals => { :entries => zipped  }
  proxy "/talks/index.html"        , "/thingies.html"     , :locals => { :entries => talks, :title => "Talks" }
  proxy "/screencasts/index.html"  , "/thingies.html"     , :locals => { :entries => screencasts, :title => "Screencasts" }
  proxy "/articles/index.html"     , "/thingies.html"     , :locals => { :entries => articles, :title => "Articles" }
  proxy "/projects/index.html"     , "/thingies.html"     , :locals => { :entries => projects, :title => "Projects" }
  proxy "/feed/index.xml"          , "/feed.xml"          , :locals => { :items => zipped }
  proxy "/testimonials/index.html" , "/testimonials.html"
  proxy "/background/index.html"   , "/background.html"

  # Calculate some statistics that we can use in the about page
  stats_all_time = zipped
    .flat_map { |entry| entry.categories }
    .group_by { |entry| entry }
    .map      { |category, group| [category, group.count] }
    .sort_by  { |category, count| -count }
    .take     5

  six_months_ago = (Time.now - 182 * 24 * 60 * 60).to_date # about 6 months in days
  stats_last_6_months = zipped
    .take_while { |entry| entry.date > six_months_ago }
    .flat_map { |entry| entry.categories }
    .group_by { |entry| entry }
    .map      { |category, group| [category, group.count] }
    .sort_by  { |category, count| -count }
    .take     5

  # Create analytical time slices
  earliest_date           = zipped.last.date
  latest_date             = zipped.first.date
  earliest_date_in_months = earliest_date.year * 12 + earliest_date.month
  latest_date_in_months   = latest_date.year * 12 + latest_date.month
  slices                  = earliest_date_in_months.upto(latest_date_in_months)

  categories = zipped
    .flat_map { |entry| entry.categories }
    .uniq
    .sort_by { |category| category }

#  slice_per_categories = categories
#    .map { |category|
#      count_per_slice = slices
#        .map { |month|
#          zipped
#            .select { |entry| ((entry.date.year * 12 + entry.date.month) == month) && entry.categories.include?(category) }
#            .size
#        }
#      accumulated_count = count_per_slice.reduce([0]) { |memo, slice|
#        memo + [memo.last + slice]
#      }
#
#      [category, count_per_slice]
#    }


#  proxy "/stats/index.html", "/stats.html", :locals => {
#    :latest_topics    => stats_last_6_months,
#    :all_topics       => stats_all_time,
#    :article_count    => articles.size,
#    :talk_count       => talks.size,
#    :screencast_count => screencasts.size,
#    :project_count    => projects.size,
#    :trends           => slice_per_categories
#  }

  # generate some category pages
  categories
    .each { |category|
      entries = zipped
        .select { |entry| entry.categories.include? category }
      proxy "/category/#{category.downcase.gsub(' ', '-')}/index.html",
            "/thingies.html" ,
            :locals => { :entries => entries, :title => category }
    }

  #empty_slice_categories  = Hash[categories.map { |category| [category, 0] }]
  #categories_per_slice = slices
  #  .map { |month|
  #     current = Hash[
  #       zipped
  #        .select   { |entry| (entry.date.year * 12 + entry.date.month) == month }
  #        .flat_map { |entry| entry.categories }
  #        .group_by { |entry| entry }
  #        .map      { |category, group| [category, group.size] }
  #     ]
  #     empty_slice_categories.merge(current)
  #  }
  #
  # puts categories_per_slice.to_json

  # exported_posts = articles.map do |article|
  #   {
  #     "title"            => article.title,
  #     "slug"             => article.slug,
  #     "markdown"         => File.read(article.resource.source_file).split("---").last,
  #     "html"             => article.body,
  #     "image"            => nil,
  #     "featured"         => 0,
  #     "page"             => 0,
  #     "status"           => "published",
  #     "language"         => "en_US",
  #     "meta_title"       => nil,
  #     "meta_description" => nil,
  #     "author_id"        => 1,
  #     "created_at"       => article.date.to_time.to_i,
  #     "created_by"       => 1,
  #     "updated_at"       => article.date.to_time.to_i,
  #     "updated_by"       => 1,
  #     "published_at"     => article.date.to_time.to_i,
  #     "published_by"     => 1,
  #     "tags"             => article.categories.map { |category| { "name" => category } }
  #   }
  # end
  # 
  # exported_tags = articles.map { |article| article.categories }
  #                         .flatten
  #                         .uniq
  #                         .each_with_index
  #                         .map { |category, index|
  #                             {
  #                                 "id"          => index,
  #                                 "name"        => category,
  #                                 "slug"        => category.downcase,
  #                                 "description" => ""
  #                             }
  #                         }
  # 
  # export_object = {
  #     "meta" => {
  #         "exported_on" => Time.now.to_i * 1000,
  #         "version"     => "000"
  #     },
  #     "data" => {
  #         "posts" => exported_posts
  #     }
  # }
  # 
  # File.write('ghost_export.json', JSON.generate(export_object))

  ignore "/writings.html"
  ignore "/feed.xml"
  ignore "/thingies.html"
  ignore "/dashboard.html"
  ignore "/talks.html"
  ignore "/screencasts.html"
  ignore "/stats.html"
  ignore "/data/*"
  ignore "/GenerateIndicies.scala"
end

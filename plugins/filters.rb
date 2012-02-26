module Jekyll

  module Filters
    
    def array_to_css_class_string(array)
      case array.length
      when 0
        ""
      else
        array.map {|c| c.downcase.gsub(/\./, '').gsub /\ /, '' }.join ' '
      end
    end
    
    def categories_as_filter_tags(posts)
      posts.map { |p| p.categories }.flatten.uniq.map { |p| "<a class='filter-trigger' href='#' data-filter='.#{p.downcase.gsub(/\./, '').gsub /\ /, ''}'>#{p.gsub(/Screencast/, 'All')}</a>"  }.join ' '
    end

  end
end
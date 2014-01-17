require 'set'
require 'uri'
require 'nokogiri'
require 'open-uri'
require 'colorize'

def crawl_site(starting_at)
  starting_uri = URI.parse(starting_at)
  seen_pages   = Set.new 
  gists        = Set.new
  crawl_page   = ->(page_uri, coming_from_uri) do
    unless seen_pages.include?(page_uri)
      seen_pages << page_uri
      begin
        doc   = Nokogiri.HTML(open(page_uri))
        hrefs = doc.css('a[href]').map  { |a| a['href'] }
        gists.merge(
          doc.css('script[src]')
             .map { |script| script['src'] if script['src'].include?('gist') }
             .compact
             .map { |gist| gist.scan(/(\d*).js$/)[0][0] rescue puts "multi file gist detected" }
             .compact
        )
        uris  = hrefs.map    { |href| URI.join(page_uri, href) rescue nil }.compact # Make these URIs, throwing out problem ones like mailto:
                     .select { |uri| uri.host == starting_uri.host }                # Pare it down to only those pages that are on the same site        
                     .each   { |uri| uri.fragment = nil }                           # Remove #foo fragments so that sub-page links aren't differentiated
                     .each   { |uri| crawl_page.call(uri, page_uri) }               # Recursively crawl the child URIs
      rescue OpenURI::HTTPError
      end
    end
  end

  crawl_page.call( starting_uri, starting_uri )
  puts gists.inspect
end

crawl_site('http://localhost:4567')


puts "Done!".green
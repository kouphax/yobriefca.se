require 'set'
require 'uri'
require 'nokogiri'
require 'open-uri'

def crawl_site(starting_at)
  starting_uri = URI.parse(starting_at)
  seen_pages = Set.new                      # Keep track of what we've seen

  crawl_page = ->(page_uri, coming_from_uri) do              # A re-usable mini-function
    unless seen_pages.include?(page_uri)
      seen_pages << page_uri                # Record that we've seen this
      begin
        doc = Nokogiri.HTML(open(page_uri)) # Get the page

        # Find all the links on the page
        hrefs = doc.css('a[href]').map{ |a| a['href'] }
        imgs  = doc.css('img[src]').map{ |img| img['src'] }
        links = hrefs + imgs

        # Make these URIs, throwing out problem ones like mailto:
        uris = links.map{ |href| URI.join( page_uri, href ) rescue nil }.compact

        # Pare it down to only those pages that are on the same site
        uris.select!{ |uri| uri.host == starting_uri.host }

        # Remove #foo fragments so that sub-page links aren't differentiated
        uris.each{ |uri| uri.fragment = nil }

        # Recursively crawl the child URIs
        uris.each{ |uri| crawl_page.call(uri, page_uri) }

      rescue OpenURI::HTTPError
        warn "✗ #{page_uri}"
        warn "    ↳ #{coming_from_uri}"
      end
    end
  end

  crawl_page.call( starting_uri, starting_uri )   # Kick it all off!
end

crawl_site('http://localhost:4567')
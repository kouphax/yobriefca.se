---
layout: false
---
xml.instruct!
xml.feed "xmlns" => "http://www.w3.org/2005/Atom" do
  xml.title "yobriefca.se"
  xml.subtitle "Published works of James Hughes"
  xml.id "http://yobriefca.se"
  xml.link "href" => "http://yobriefca.se"
  xml.link "href" => "http://yobriefca.se/feed", "rel" => "self"

  items.each do |item|
    xml.entry do
      xml.title item.title
      xml.link "rel" => "alternate", "href" => item.url
      xml.id item.url
      xml.published Time.parse(item.date.to_s).iso8601
      xml.updated Time.parse(item.date.to_s).iso8601
      xml.author { xml.name "James Hughes" }
      xml.summary item.body, "type" => "html"
      xml.content item.body, "type" => "html"
    end
  end
end
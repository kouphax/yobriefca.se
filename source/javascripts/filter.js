(function(global, document) {

  var filterElement = document.createElement("div");

  filterElement.setAttribute("class", "filter active")
  filterElement.innerHTML = [
    "Filter &rarr; ",
    "<a class='active' data-filter='all' href='#'>All</a> | ",
    "<a href='#' data-filter='article'>Articles</a> | ",
    "<a href='#' data-filter='talk'>Talks</a> | ",
    "<a href='#' data-filter='screencast'>Screencasts</a>"].join('');

  filterElement.addEventListener('click', function(event) {
    if(event.target.hasAttribute('data-filter')){
      document.querySelector('body').setAttribute("class",
        event.target.getAttribute('data-filter'));

      Array.prototype.forEach.call(
        filterElement.querySelectorAll("a"),
        function(a){ a.setAttribute('class', '') });

      event.target.setAttribute("class", "active");
    }
  })

  document.getElementById('filter-container').appendChild(filterElement);

})(this, document)

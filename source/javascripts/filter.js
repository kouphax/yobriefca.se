(function(global, document) {

	var filterElement = document.createElement("div");

	filterElement.setAttribute("class", "filter active")
	filterElement.innerHTML = [
		"Filter &rarr;",
		"<a class='active' data-filter='all' href='#'> All</a> | ",
		"<a href='#' data-filter='articles'>Articles</a> | ",
		"<a href='#' data-filter='talks'>Talks</a> | ",
		"<a href='#' data-filter='screencasts'>Screencasts</a>"].join('');

	filterElement.addEventListener('click', function(event) {

		if(event.target.hasAttribute('data-filter')){
			alert('delegated')
		}
	})

	document.getElementById('filter-container').appendChild(filterElement);

})(this, document)
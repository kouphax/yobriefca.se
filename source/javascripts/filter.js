(function(global, document) {

  var filterElement = document.getElementById('filter');

  if(filterElement){

    filterElement.addEventListener('click', function(event) {
      event.preventDefault();

      if(event.target.hasAttribute('data-filter')){
        document.querySelector('body').setAttribute("class",
          event.target.getAttribute('data-filter'));

        Array.prototype.forEach.call(
          filterElement.querySelectorAll("a"),
          function(a){ a.setAttribute('class', '') });

        event.target.setAttribute("class", "active");
      }
    })
  }


})(this, document)

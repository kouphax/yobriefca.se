var junkify = function( someClass ){
  // requires underscore.js
  var sparks = document.getElementsByClassName( someClass );
  _(sparks).each(function(e,i){
    var range = 14;
    var origContent = e.innerHTML;
    var d = origContent.split(","),
        max = _.max(d), min = _.min(d),
        dataRange = max-min;

    // an array like ["0","1",...,"e","f,","g"]
    var cj = _.range(17).map(function(e){return e.toString(17)})

    // remap data to start at "0" and end at "g"
    var rescaled = _(d).map(function(e){
      var idx = Math.ceil((e - min) * (range / dataRange) );
      return cj[idx]
    })

    // reassemble the string and add chartjunk's junkjunk classname
    var graphText = rescaled.join("")
    e.innerHTML = graphText;
    var cns = _.uniq( (e.className + " junkjunk").split(" ") ).join(" ")
    e.className = cns;
  })
}

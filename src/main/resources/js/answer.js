$( document ).ready(function() {
    $('#correct').hide();
    $('#wrong').hide();

    var tid = setInterval(pollQuestion, 800);
    function pollQuestion() {
      $.get("/answer/result", function( data ) {
        if(data.question) {
           abortTimer();
           showResult(data);
        }
      });
    }

    function abortTimer() {
      clearInterval(tid);
    }

    function showResult(data) {
        $('#spinner').hide();
        $('#correct').show();
        $('#correct').append(data);
    }
});
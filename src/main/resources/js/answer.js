$( document ).ready(function() {
    $('#correct').hide();
    $('#wrong').hide();
    $('#results').hide();

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
        if( data.localIsWinner ) {
            $('#correct').show();
        } else {
            $('#wrong').show();
        }
        printResults(data.results);
    }

    function printResults(results) {
        results.foreach(function(item) {
            var result = item.isCorrect ? "correct answer" : "wrong answer";
            $('#results').append("<tr><td>" + item.id + "</td><td>" + result + "</td></tr>")
        });
        $('#results').show();
    }
});
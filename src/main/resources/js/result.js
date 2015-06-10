$( document ).ready(function() {

    var tid = setInterval(pollQuestion, 800);
    function pollQuestion() {
      $.get("/answer/result", function( data ) {
        if(data != null && data.question) {
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
        printResults(data.results);
    }

    function printResults(results) {
        $.each(results, function( index, item ) {
            var result = item.isCorrect ? "correct answer" : "wrong answer";
            $('#results').append("<tr><td>" + item.id + "</td><td>" + result + "</td></tr>")
        });
    }
});
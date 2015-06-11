$( document ).ready(function() {

    var tid = setInterval(pollQuestion, 800);
    function pollQuestion() {
      $.get("/question/partial", function( data ) {
        if(data != null && data.question) {
            if( data.allResults) {
                abortTimer();
                $('#spinner').hide();
            }
            showResult(data);
        }
      });
    }

    function abortTimer() {
      clearInterval(tid);
    }

    function showResult(data) {
        printResults(data.results);
    }

    function printResults(results) {
        $('#results').empty();
        $.each(results, function( index, item ) {
            var result = item.isCorrect ? "correct answer" : "wrong answer";
            $('#results').append("<tr><td>" + item.id + "</td><td>" + result + "</td></tr>")
        });
    }
});
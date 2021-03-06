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
        $('#results').append("<tr><th>Participant</th><th>given answer</th><th>Result</th><th>Score</th></tr>")
        $.each(results, function( index, item ) {
            var result = item.isCorrect ? "correct answer" : "wrong answer";
            $('#results').append("<tr><td>" + item.id + "</td><td>" + item.answer + "</td><td>" + result + "</td><td>" + item.countOfValidAnswers + "</td></tr>")
        });
    }
});
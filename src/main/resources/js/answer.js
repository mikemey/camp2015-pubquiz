$( document ).ready(function() {
    $('#correct').hide();
    $('#wrong').hide();
    $('#results').hide();

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
        if( data.localIsWinner ) {
            $('#correct').show();
        } else {
            $('#wrong').show();
        }
        printResults(data.results);
    }

    function printResults(results) {
        $('#results').append("<tr><th>Participant</th><th>given answer</th><th>Result</th><th>Score</th></tr>")
        $.each(results, function( index, item ) {
            var result = item.isCorrect ? "correct answer" : "wrong answer";
            $('#results').append("<tr><td>" + item.id + "</td><td>" + item.answer + "</td><td>" + result + "</td><td>" + item.countOfValidAnswers + "</td></tr>")
        });
        $('#page-title').text('Final results');
        $('#results').show();
    }
});
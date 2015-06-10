$( document ).ready(function() {
    $('#answerForm').hide();

//    var tid = setInterval(pollQuestion, 800);
    function pollQuestion() {
      $.get("/quiz/question", function( data ) {
        if(data.question) {
           abortTimer();
           swapForms(data);
        }
      });
    }

    function abortTimer() {
      clearInterval(tid);
    }

    function showResult(data) {
        $('#spinner').hide();


        $('#curQuestion').text(data.question);
        setAnswer('#curAnswerA', data.answers[0]);
        setAnswer('#curAnswerB', data.answers[1]);
        setAnswer('#curAnswerC', data.answers[2]);
        setAnswer('#curAnswerD', data.answers[3]);

        $('#answerForm').show();
    }
});
$( document ).ready(function() {
    $('#answerForm').hide();

    var tid = setInterval(pollQuestion, 4000);
    function pollQuestion() {
      $.get("/quiz/question", function( data ) {
        if(data.question) {
           swapForms(data);
        }
      });
    }

    function abortTimer() { // to be called when you want to stop the timer
      clearInterval(tid);
    }

    function bla() {
        swapForms( {
            question: 'lalal',
            answers: [ 'lala 1', 'lala 2', 'lala 3', 'lala 4']
        });
    }

    function swapForms(data) {
        $('#curQuestion').text(data.question);
        $('#curAnswerA').text(data.answers[0]);
        $('#curAnswerA').val(data.answers[0]);
        $('#curAnswerB').text(data.answers[1]);
        $('#curAnswerB').val(data.answers[1]);
        $('#curAnswerC').text(data.answers[2]);
        $('#curAnswerC').val(data.answers[2]);
        $('#curAnswerD').text(data.answers[3]);
        $('#curAnswerD').val(data.answers[3]);

        $('#questionForm').hide();
        $('#answerForm').show();
    }
});
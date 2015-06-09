$( document ).ready(function() {
    $('#answerForm').hide();

    var tid = setInterval(pollQuestion, 4000);
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

    this.bla = function() {
        swapForms( {
            question: 'lalal',
            answers: [ 'lala 1', 'lala 2', 'lala 3', 'lala 4']
        });
    }

    function swapForms(data) {
        $('#curQuestion').text(data.question);
        setAnswer('#curAnswerA', data.answers[0]);
        setAnswer('#curAnswerB', data.answers[1]);
        setAnswer('#curAnswerC', data.answers[2]);
        setAnswer('#curAnswerD', data.answers[3]);

        $('#questionForm').hide();
        $('#answerForm').show();
    }

    function setAnswer(id, answer) {
        $(id).append(answer);
        $(id + "Val").val(answer);
    }
});
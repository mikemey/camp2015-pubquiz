$( document ).ready(function() {
    $('#answerForm').hide();
    $('#questionForm').hide();

    var tid = setInterval(pollQuestion, 800);
    function pollQuestion() {
      $.get("/quiz/question", function( data ) {
        if(data != null) {
          if(data.connected) {
            $('#waiting').hide();
            $('#questionForm').show();
          }
          if(data.question) {
           abortTimer();
           swapForms(data);
          }
        }
      });
    }

    function abortTimer() {
      clearInterval(tid);
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
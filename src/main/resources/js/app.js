$( document ).ready(function() {
    $('#answerForm').hide();

    var tid = setInterval(pollQuestion, 800);
    function pollQuestion() {
      $.get("/quiz/question", function( data ) {
        if(data != null && data.question) {
           abortTimer();
           showQuestionForm(data);
        } else {
            $('#questionForm').show();
            $('#answerForm').hide();
        }
      });
    }

    function abortTimer() {
      clearInterval(tid);
    }

    function showQuestionForm(data) {
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
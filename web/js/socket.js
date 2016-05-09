$(function() {
    var ws = new WebSocket('ws://localhost:8080/kwetter_war_exploded/socket');

    var container = $('#message-container');
    ws.onmessage = function(message) {
        container.append('<p>' + message.data + '</p>');
    };

    var input = $('#input');
    $('#send-button').on('click', function() {
        ws.send(input.val());
        input.val('');
    });
})
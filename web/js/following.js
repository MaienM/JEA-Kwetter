// Multiline Function String - Nate Ferrero - Public Domain - http://stackoverflow.com/a/14496573
function heredoc(f) {
    return f.toString().match(/\/\*\s*([\s\S]*?)\s*\*\//m)[1];
}

var template = _.template(heredoc(function(){/*
<tr>
    <td>
        <a href="user-tweets.xhtml?user=<%= follower.user.username %>"><%= follower.user.username %></a>
    </td>
    <td>
        <input type="checkbox" id="checkbox-<%= follower.user.id %>" class="filled-in" <%= follower.following ? 'checked' : '' %> value="<%= follower.user.username %>" />
        <label for="checkbox-<%= follower.user.id %>"></label>
    </td>
</tr>
*/}));

$(function() {
    var container = $('#followers-container');
    var current_user = null;
    $('#refresh-button').on('click', function() {
        current_user = $('select').val();
        $.get('rest-api/users/' + current_user + '/followers').then(function(data) {
            $('#save-button').removeClass('disabled');
            container.html('');
            data = JSON.parse(data);
            _.each(data, function(follower) {
                container.append(template({follower: follower}));
            });
        });
    });

    $('#save-button').on('click', function() {
        if ($(this).hasClass('disabled')) {
            return;
        }

        container.find('input[type=checkbox]').each(function() {
            if ($(this)[0].checked) {
                $.post('rest-api/users/' + current_user + '/follows/' + $(this).val());
            } else {
                $.ajax({
                    url: 'rest-api/users/' + current_user + '/follows/' + $(this).val(),
                    type: 'DELETE'
                });
            }
        });
    });
});

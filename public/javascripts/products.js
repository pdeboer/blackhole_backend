jQuery(function($) {
  var $table, loadUserDetails, loadUserTable, userDetailsUrl, userListUrl, saveRow;
  $table = $('.container table');
  userListUrl = $table.data('list');
  loadUserTable = function() {
    return $.get(userListUrl, function(users) {
      return $.each(users, function(index, email) {
        var row;
        row = $('<tr/>').append($('<td/>').text(email));
        row.attr('contenteditable', true);
        $table.append(row);
        return loadUserDetails(row);
      });
    });
  };
  userDetailsUrl = function(email) {
    return $table.data('details').replace('I', email);
  };
  loadUserDetails = function(tableRow) {
    var email;
    email = tableRow.text();
    return $.get(userDetailsUrl(email), function(user) {
      tableRow.append($('<td/>').text(user.firstname));
      tableRow.append($('<td/>').text(user.lastname));
      return tableRow.append($('<td/>'));
    });
  };
  loadUserTable();
  saveRow = function($row) {
    var lastname, email, jqxhr, firstname, user, ref;
    ref = $row.children().map(function() {
      return $(this).text();
    }), email = ref[0], firstname = ref[1], lastname = ref[2], roleId = ref[3];
    user = {
      email: email,
      firstname: firstname,
      lastname: lastname
    };
    jqxhr = $.ajax({
      type: "PUT",
      url: userDetailsUrl(email),
      contentType: "application/json",
      data: JSON.stringify(user)
    });
    jqxhr.done(function(response) {
      var $label;
      $label = $('<span/>').addClass('label label-success');
      $row.children().last().append($label.text(response));
      return $label.delay(3000).fadeOut();
    });
    return jqxhr.fail(function(data) {
      var $label, message;
      $label = $('<span/>').addClass('label label-important');
      message = data.responseText || data.statusText;
      return $row.children().last().append($label.text(message));
    });
  };
  return $table.on('focusout', 'tr', function() {
    console.log('save');
    return saveRow($(this));
  });
});
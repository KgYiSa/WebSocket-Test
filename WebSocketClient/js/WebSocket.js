function setMessage(receive) {
    $("#receiveText").html(function (i, origText) {
        return origText + receive + "<br/>";
    });
}

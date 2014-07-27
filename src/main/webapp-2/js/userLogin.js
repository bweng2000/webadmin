$(document).ready(function() {
	$("#loginForm").submit(function() {
		$.ajax({
			url : "../adminrest/login",
			data : $("#loginForm").serialize(),
			contentType : 'application/x-www-form-urlencoded; charset=utf-8',
			type : "POST",
			//dataType : "json",
			//success : function(data, status, jqXHR) {
			success : function() {	
				window.location.href = '../pages/admin/index.html';
			},
			error : function(jqXHR, status, error) {
				alert(jqXHR.responseText);
				console.log(error);
				console.log(data);
			}
		});
		return false; //This is needed to prevent the form being redirected.
	});
});
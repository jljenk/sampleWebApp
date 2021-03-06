var web = {
			//context					: '/SampleWebApp/api/'
			context					: '/api/'
};

var method = {
		
		GET		:	'GET',
		PUT		:	'PUT',
		POST	:	'POST',
		DELTE	:	'DELETE'
}

var rest = {
				userLogin			: 'log/in',
				allCards			: 'cards/all'
};


function makeAJAXCall(method, url, data, success, error) {
	var token = sessionStorage['accessToken'];
	$.ajax({
		type: method,
		beforeSend: function (xhr) {
			xhr.setRequestHeader ("authorization", token);
		},
		contentType: 'application/json',
		url: web.context + url,
		data: data,
		dataType: "json",
		success: function(response){
			success(response);
		},
		error: function(jqXHR, textStatus, errorThrown){
			error (jqXHR, textStatus, errorThrown);
		}
	});
}


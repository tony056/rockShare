
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.afterSave("RequestMsg", function(request){
	var query = new Parse.Query(Parse.Installation);
	query.equalTo('username', request.object.get('to'));
	Parse.Push.send({
		where: query,
		data: {
			alert: request.object.get('from').toString() + " asks you to share music!",
			uri: "myapp://host/path"
		}
	}, {
		success: function() {
			console.log("Push was successful.");
		},
		error: function(){
			console.log("Push was wrong.");
		}
	});
});
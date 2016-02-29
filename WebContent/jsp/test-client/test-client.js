$("#connect-btn").click(connect);

function connect(){

	var websocket = null;
	var userId = $("#userId").val();
	var platform = $("#platform").val();
	var userType = $("#userType").val();
	var params = {};
	params.userId = userId;
	params.platform = platform;
	params.userType = userType;

	if ('WebSocket' in window) {
		websocket = new WebSocket("ws://localhost:8080/mcenter/webSocketServer?user=" + encodeURI(JSON.stringify(params)));
	} else if ('MozWebSocket' in window) {
		websocket = new MozWebSocket("ws://localhost:8080/mcenter/webSocketServer?user=" + encodeURI(JSON.stringify(params)));
	} else {
		websocket = new SockJS("http://localhost:8080/mcenter/sockjs/webSocketServer?user=" + encodeURI(JSON.stringify(params)));
	}

	websocket.onopen = function (evnt) {
		log('������');
	};

	websocket.onmessage = function (evnt) {
		log('�յ���Ϣ��' + evnt.data);
		var json = $.parseJSON(evnt.data);
		var feedback = {};
		feedback.msgId = json.id;
		feedback.type = 'feedback';
 		websocket.send(JSON.stringify(feedback));
 		log('�ѷ���');
	};

	websocket.onerror = function (evnt) {
		log('������');
	};

	websocket.onclose = function (evnt) {
		log('���ӹر�');
	}

}

function log(msg){
    var msg = $("#logs").val()+msg+"\n";
    $("#logs").val(msg);
}
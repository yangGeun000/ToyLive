let socket;
let stompClient;
let streamer;
let sender;
let msgContent;
let chatView = document.querySelector("#chatView");

// 소켓연결
function connect() {
	streamer = document.querySelector("#streamer").innerHTML;
	sender = document.querySelector("#sender").innerHTML;
	msgContent = document.querySelector("#msgContent");

	// 연결하고자하는 Socket 의 endPoint
	socket = new SockJS('/chatting');
	stompClient = Stomp.over(socket);

	stompClient.connect({}, onConnected, onError);
}


// 구독 설정
function onConnected() {
	console.log(streamer);
	console.log(sender);
	stompClient.subscribe('/sub/' + streamer, onMessageReceived);
	stompClient.subscribe('/sub/webrtc/' + sender, onSingnaling);
	let msg = {
		streamer: streamer,
		sender: sender,
		type: 'ENTER'
	};
	stompClient.send("/pub/enter", {}, JSON.stringify(msg));

}

// 메세지 받았을 때 동작
function onMessageReceived(result) {
	console.log("message received : " + result);
	let msg = JSON.parse(result.body);
	let color;
	let tag;
	switch (msg.type) {
		case "ENTER":
			tag = `<div>${msg.content}</div>`;
			if (streamer == sender && sender != msg.sender) createPeer(msg.sender);
			break;
		case "MESSAGE":
			if (streamer == msg.sender) color = "red";
			else if (sender == msg.sender) color = "green";
			tag = `<div style="color:${color}">${msg.sender} : ${msg.content}[${msg.sendDate}]</div>`;
			break;
		case "LEAVE":
			tag = `<div>${msg.content}</div>`;
			break;
		default:
			break;
	}
	chatView.insertAdjacentHTML("beforeend", tag);
}

// 메세지 보내기
function sendMessage() {
	let content = msgContent.value;

	if (content.trim() && stompClient) {
		let msg = {
			sender: sender,
			content: content,
			type: 'MESSAGE'
		};

		stompClient.send("/pub/send/" + streamer, {}, JSON.stringify(msg));
		msgContent.value = '';
	}

}

// 에러처리
function onError(error) {
	console.log("stompError : " + error);
	stompClient.connect({}, onConnected, onError);
}
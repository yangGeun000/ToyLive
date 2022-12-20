let socket;
let stompClient;
let streamer;
let sender;
let msgContent;
let chatView = document.querySelector("#chatView");
let viewList = document.querySelector("#viewList");

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
	let color = "black";
	let tag;
	switch (msg.type) {
		case "ENTER":
			tag = `<div class="badge bg-warning text-white text-wrap text-break w-100">
						${msg.content}
					</div>`;
			if (streamer == sender && sender != msg.sender) createPeer(msg.sender);
			if (streamer == sender) getViewList();
			break;
		case "MESSAGE":
			if (streamer == msg.sender) color = "red";
			else if (sender == msg.sender) color = "green";
			tag = `<div class="badge text-wrap text-break text-start w-100" style="color:${color}">
						${msg.sender} : ${msg.content}
						<br> 
						<span>[${msg.sendDate}]</span>
					</div>`;
			break;
		case "LEAVE":
			tag = `<div class="badge bg-warning text-white text-wrap text-break w-100">
						${msg.content}
					</div>`;
			if (streamer == sender) {
				getViewList();
				pc.get(msg.sender).close();
				pc.delete(msg.sender);
			}
			if (streamer == msg.sender) {
				stompClient.disconnect();
				socket.close();
				alert("방송이 종료되었습니다.");
			}
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

// 시청자 목록 받아오기
function getViewList() {
	ajax("/room/views/" + streamer, null, "get", function(result) {
		let tag = '';
		result.forEach((view) => {
			tag += `<div class="m-2 badge bg-secondary bg-gradient text-wrap text-break d-block">${view}</div>`;
		})
		viewList.replaceChildren();
		viewList.insertAdjacentHTML("beforeend", tag);

	})
}

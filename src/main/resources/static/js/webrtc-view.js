let remoteVideo = document.querySelector('#remoteVideo');
let peerConnection;

function onSingnaling(msg) {
	console.log("Singnaling message", msg);
	let content = JSON.parse(msg.body);
	let data = content.data;
	switch (content.event) {
		// when somebody wants to call us
		case "offer":
			handleOffer(data);
			break;
		case "answer":
			handleAnswer(data);
			break;
		// when a remote peer sends an ice candidate to us
		case "candidate":
			handleCandidate(data);
			break;
		default:
			break;
	}
}

function send(msg) {
	stompClient.send("/pub/webrtc/" + streamer, {}, JSON.stringify(msg));
}

function createPeer() {
	let configuration = {
		"iceServers": [{
			"url": "stun:stun.l.google.com:19302"
		}]
	};

	peerConnection = new RTCPeerConnection(configuration);
	peerConnection.ontrack = function(event) {
		console.log('Track Event: set stream to remote video element');
		remoteVideo.srcObject = event.streams[0];
	}

	peerConnection.onicecandidate = function(event) {
		console.log("candidate send!!!");
		if (event.candidate) {
			send({
				sender: sender,
				target: streamer,
				event: "candidate",
				data: event.candidate
			});
		}
	};
}

/**
 * offer를 받은 peer는 이를 remotedescription으로 설정하고
 * answer를 생성하여 처음 peer 에게 보낸다.
 */
function handleOffer(offer) {
	peerConnection.setRemoteDescription(new RTCSessionDescription(offer));

	// create and send an answer to an offer
	peerConnection.createAnswer(function(answer) {
		peerConnection.setLocalDescription(answer);
		send({
			sender: sender,
			target: streamer,
			event: "answer",
			data: answer
		});
	}, function(error) {
		alert("Error creating an answer");
	});
};

/**
 * 다른 peer가 보낸 ICE candidate를 처리해야 하는데
 * 이 candidate를 받은 remote Peer는 해당 candidate를 candidate pool의 추가
 */
function handleCandidate(candidate) {
	console.log("candidate add!!!");
	peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
};

/**
 * 처음 Peer는 anwser를 받고 setRemoteDescription 으로 설정
 */
function handleAnswer(answer) {
	peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
	console.log("connection established successfully!!");
};
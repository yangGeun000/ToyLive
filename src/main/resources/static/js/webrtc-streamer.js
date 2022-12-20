let localVideo = document.querySelector('#localVideo');
let localStream;
let pc = new Map();
const constraints = {
	video: true, audio: true
};

function getMedia() {
	navigator.mediaDevices.getDisplayMedia(constraints).
		then(function(stream) {
			localVideo.srcObject = stream;
			localStream = stream;
			const [videoTrack] = stream.getVideoTracks();
			pc.forEach((peer) => {
				const sender = peer.getSenders().find((s) => s.track.kind === videoTrack.kind);
				sender.replaceTrack(videoTrack);
			});
		})
		.catch(function(err) {
			console.log("Media ERROR : " + err);
		});
}

function onSingnaling(msg) {
	console.log("Singnaling message", msg);
	let content = JSON.parse(msg.body);
	let data = content.data;
	switch (content.event) {
		// when somebody wants to call us
		case "offer":
			handleOffer(data, content.sender);
			break;
		case "answer":
			handleAnswer(data, content.sender);
			break;
		// when a remote peer sends an ice candidate to us
		case "candidate":
			handleCandidate(data, content.sender);
			break;
		default:
			break;
	}
}

function send(msg) {
	stompClient.send("/pub/webrtc/" + msg.target, {}, JSON.stringify(msg));
}

function createPeer(target) {
	let configuration = {
		"iceServers": [{
			"url": "stun:stun.l.google.com:19302"
		}]
	};

	let peerConnection = new RTCPeerConnection(configuration);
	peerConnection.onconnectionstatechange = function(event) {
		switch (peerConnection.connectionState) {
			case "connected":
				console.log("connected");
				break;
			case "disconnected":
				console.log("disconnected");
				peerConnection.close();
				pc.delete(target);
			case "failed":
				console.log("failed");
				break;
			case "closed":
				console.log("closed");
				break;
		}
	}
	localStream.getTracks().forEach(track => peerConnection.addTrack(track, localStream));
	peerConnection.onicecandidate = function(event) {
		console.log("candidate send!!!");
		if (event.candidate) {
			send({
				sender: sender,
				target: target,
				event: "candidate",
				data: event.candidate
			});
		}
	};
	pc.set(target, peerConnection);
	createOffer(target);
}


function createOffer(target) {
	let peerConnection = pc.get(target);
	peerConnection.createOffer(function(offer) {
		//send 메소드는 오퍼 정보 를 전달하기 위해 Signaling Server를 호출
		send({
			sender: sender,
			target: target,
			event: "offer",
			data: offer
		});
		peerConnection.setLocalDescription(offer);
	}, function(error) {
		alert("Error creating an offer");
	});
}

/**
 * offer를 받은 peer는 이를 remotedescription으로 설정하고
 * answer를 생성하여 처음 peer 에게 보낸다.
 */
function handleOffer(offer, target) {
	let peerConnection = pc.get(target);
	peerConnection.setRemoteDescription(new RTCSessionDescription(offer));

	// create and send an answer to an offer
	peerConnection.createAnswer(function(answer) {
		peerConnection.setLocalDescription(answer);
		send({
			sender: sender,
			target: target,
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
function handleCandidate(candidate, target) {
	let peerConnection = pc.get(target);
	console.log("candidate add!!!");
	peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
};

/**
 * 처음 Peer는 anwser를 받고 setRemoteDescription 으로 설정
 */
function handleAnswer(answer, target) {
	let peerConnection = pc.get(target);
	peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
	console.log("connection established successfully!!");
};
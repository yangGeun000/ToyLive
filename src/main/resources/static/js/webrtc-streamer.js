let localVideo = document.querySelector('#localVideo');
let localStream;
var pc = new Map();
const constraints = {
	video: true, audio: true
};

function getMedia() {
	navigator.mediaDevices.getDisplayMedia(constraints).
		then(function(stream) {
			localVideo.srcObject = stream;
			localStream = stream;
			const [videoTrack] = stream.getVideoTracks();
			const [audioTrack] = stream.getAudioTracks();
			console.log(videoTrack);
			console.log(audioTrack);
			pc.forEach((peer) => {
				const vidioSender = peer.getSenders().find((s) => s.track.kind === videoTrack.kind);
				vidioSender.replaceTrack(videoTrack);
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
		console.log(event.currentTarget.connectionState);

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
		//send ???????????? ?????? ?????? ??? ???????????? ?????? Signaling Server??? ??????
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
 * offer??? ?????? peer??? ?????? remotedescription?????? ????????????
 * answer??? ???????????? ?????? peer ?????? ?????????.
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
 * ?????? peer??? ?????? ICE candidate??? ???????????? ?????????
 * ??? candidate??? ?????? remote Peer??? ?????? candidate??? candidate pool??? ??????
 */
function handleCandidate(candidate, target) {
	let peerConnection = pc.get(target);
	console.log("candidate add!!!");
	peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
};

/**
 * ?????? Peer??? anwser??? ?????? setRemoteDescription ?????? ??????
 */
function handleAnswer(answer, target) {
	let peerConnection = pc.get(target);
	peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
	console.log("connection established successfully!!");
};
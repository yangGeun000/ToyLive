function ajax(url, msg, type, calbak, async){
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		
		$.ajax({
			url: url,
			type: type,
			data: msg, // String -> json 형태로 변환
			beforeSend : function(xhr)
            {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
				xhr.setRequestHeader(header, token);
            },
			dataType: 'json', 
			async: async, //동기, 비동기 여부
			success: function(data){
				console.log(data);
				calbak(data);
			},
			error:function(error){
				console.log('error:'+error);
			}
		});
	}
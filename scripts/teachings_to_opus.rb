teachings = [
"Mỗi lần thấy sân lên thì tránh đi chỗ khác, đừng nói gì thêm hoặc bảo là tớ cần tránh đi 1 tý, chút nói chuyện lại.",
"Mỗi lần thấy sân hít sâu vào và thở ra hết vài lần, tưởng tượng rằng năng lượng xấu của cơn sân đi ra ngoài theo hơi thở và sự mát lành đi vào theo hơi thở.",
"Dừng lại 1 chút trước khi nói gì đó, nhất là khi sân, nếu được thì trong vài hơi thở, nếu không thì 1 hơi thở hoặc nửa hơi thở cũng được.",
"Không ai có trách nhiệm phải làm mình vui, hay phải làm cho mình điều này điều kia. Không ai cả. Mình chịu trách nhiệm với chính mình.",
"Cần tập thói quen rất quan trọng là thoải mái trong mỗi lúc. Thoải mái trong khi hành thiền cũng như trong cuộc sống.",
"Hãy làm cho mình thoải mái nhất có thể được. Khi thoải mái, tâm luôn TÍCH CỰC, mọi thứ sẽ luôn TÍCH CỰC.",
"Khi ngồi hoặc đi, làm việc gì ... cũng tự kiểm tra và tự hỏi xem thế này đã thoải mái chưa? Có thể thoải mái hơn được nữa không?",
"Hãy luôn cái tiến, kể cả cách suy nghĩ, cách làm việc, cách nói chuyện hay quan hệ ... làm sao để thoải mái hơn nữa. Tìm xem có cách nào để thoải mái hơn tý nữa được không?",
"Nếu cứ hay để ý đến cảm nhận hay đánh giá của người khác thì sẽ không thoải mái. Vậy lấy thoải mái đặt lên hàng đầu, việc nguời ta hiểu hay không, nghĩ thế nào ... để xuống hạng thứ 2.",
"Dòng suy nghĩ quá mạnh, tâm rất mờ và yếu. Muốn ngắt cần có biện pháp mạnh: đang làm gì, nhớ ra thì dừng lại, kiểu như \"đứng hình\" trong vài giây. Cảm nhận ngay tư thế toàn thân đang đứng hình ấy thật rõ rồi mới làm tiếp.", 
"Đang đi thấy suy nghĩ thì đứng lại, hết dòng suy nghĩ mới đi tiếp. Làm việc gì thất niệm, nhất quyết làm lại từ đầu.",
"Mọi cử động đều chậm đi, khoảng 1 nửa hoặc 2/3. Bấm đồng hồ đo chính xác, nếu phát hiện vừa làm vừa nghĩ hoặc làm nhanh hơn quy định, nhất định làm lại.",
"Nếu ngồi hoặc nằm mà nghĩ miên man, nhất định đứng dậy hoặc đi, hoặc làm việc. Không duy trì một tư thế quá lâu."
]

require "uri"
# `brew install sox opus-tools`
teachings.each_with_index do  |q, i|
	name = "~/src/_save/teachings/#{i}"
  	next if File.exist?("#{name}.ogg") && File.size("#{name}.ogg") > 0
	unless File.exist?("#{name}.mp3") && File.size("#{name}.mp3") > 0	 	
		url = "https://support.readaloud.app/read-aloud/speak/vi/GoogleTranslate%20Vietnamese?q="+URI.escape(q, Regexp.new("[^#{URI::PATTERN::UNRESERVED}]"))
		puts url, name
		`curl -o #{name}.mp3 #{url}`
	 end 
   	`sox #{name}.mp3 -t s16 --rate 44100 -c 1 - | opusenc --downmix-mono --bitrate 12 --raw-rate 44100 --raw-bits 16 --raw-chan 1 - #{name}.ogg`
end

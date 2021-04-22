def tto(splitter, audio_dir)
txt = File.open("android/app/src/main/java/dev/tiendung/tamedu/ReminderData.kt").read
txt = txt.split(splitter)[1]
txt = txt.split(")\n")[0]
teachings = txt.gsub(/\/\/.+?\n/,"").split(/",\s*\n?\s*"/).map{ |x| x.sub(/^\s*"\s*|\s*"\s*$/,"").strip }

require "uri"
# `brew install sox opus-tools`
teachings.each_with_index do  |q, i|
	# next if i < 1300
	name = "assets/#{audio_dir}/#{i}"
  	next if File.exist?("#{name}.ogg") && File.size("#{name}.ogg") > 0
	next if  File.exist?("#{name}.mp3") && File.size("#{name}.mp3") > 0
	t = q; q = t.gsub(/\(.+?\)/){ |m| m.length > 15 ? "- #{m[1...-1]} -" : "" }
	puts "#{t} => " if (t != q)
	url = "https://support.readaloud.app/read-aloud/speak/vi/GoogleTranslate%20Vietnamese?q="+
		URI.escape(q, Regexp.new("[^#{URI::PATTERN::UNRESERVED}]"))
	puts q, name
	`curl -o #{name}.mp3 #{url}`	
	`sox #{name}.mp3 -t s16 --rate 44100 -c 1 - | opusenc --downmix-mono --bitrate 12 --raw-rate 44100 --raw-bits 16 --raw-chan 1 - #{name}.ogg` if File.exist?("#{name}.mp3") && File.size("#{name}.mp3") > 0
end
end

# tto("val TEACHINGS = arrayOf(", "teachings")
x = tto("val QUOTES_BY_LEN_DESC = arrayOf(", "quotes")

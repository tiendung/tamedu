txt = File.open("android/app/src/main/java/dev/tiendung/tamedu/ReminderData.kt").read
txt = txt.split("val TEACHINGS = arrayOf(")[1]
txt = txt.split(")")[0]
teachings = txt.gsub(/\/\/.+?\n/,"").split(/",\s*\n\s*"/).map{ |x| x.sub(/^\s*"\s*|\s*"\s*$/,"").strip }

puts teachings
require "uri"
# `brew install sox opus-tools`
teachings.each_with_index do  |q, i|
	name = "assets/teachings/#{i}"
  	next if File.exist?("#{name}.ogg") && File.size("#{name}.ogg") > 0
	unless File.exist?("#{name}.mp3") && File.size("#{name}.mp3") > 0
		url = "https://support.readaloud.app/read-aloud/speak/vi/GoogleTranslate%20Vietnamese?q="+URI.escape(q, Regexp.new("[^#{URI::PATTERN::UNRESERVED}]"))
		puts url, name
		`curl -o #{name}.mp3 #{url}`
	 end 
   	`sox #{name}.mp3 -t s16 --rate 44100 -c 1 - | opusenc --downmix-mono --bitrate 12 --raw-rate 44100 --raw-bits 16 --raw-chan 1 - #{name}.ogg`
end

def qtm(splitter, dir)
txt = File.open("android/app/src/main/java/dev/tiendung/tamedu/ReminderData.kt").read
txt = txt.split(splitter)[1]; txt = txt.split(")\n")[0]
teachings = txt.gsub(/\/\/.+?\n/,"").split(/",\s*\n?\s*"/).map{ |x| x.sub(/^\s*"\s*|\s*"\s*$/,"").strip }
# return teachings
# `brew install ffmpeg imagemagick`
# `convert -list format | fgrep -i pango`
teachings.each_with_index do  |q, i|
	name = "#{dir}/#{i}"
  	next if File.exist?("#{name}.mpeg") && File.size("#{name}.mpeg") > 0
  	unless File.exist?("#{name}.png") && File.size("#{name}.png") > 0
`convert -background "#400D00" -size 800x -font Arial -pointsize 20 -border 25 -bordercolor "#400D00" \
       pango:'<span size="x-large" foreground="#E9DA95">#{q}</span>

<span foreground="#DDD"><i>sutamphap.com</i></span>' \
       #{name}.png`
  	end
  	puts q, name
	`ffmpeg -loop 1 -i ~/me/stp/quotes/#{i}.png -i assets/quotes/#{i}.mp3 -shortest #{name}.mpeg`
	# `ffmpeg -loop 1 -i #{name}.png -i assets/quotes/#{i}.mp3 -c:v libx264 -tune stillimage -c:a aac -b:a 192k -pix_fmt yuv420p -shortest #{name}.mp4 -vf "pad=ceil(iw/2)*2:ceil(ih/2)*2"`
end
end

puts qtm("val QUOTES_BY_LEN_DESC = arrayOf(", "../quotes")
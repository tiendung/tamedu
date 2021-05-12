## Install

https://gist.github.com/agrcrobles/165ac477a9ee51198f4a870c723cd441
https://gist.github.com/HugoMatilla/f92682b06068b06a6f2a

```sh
mkdir ~/tools
cd ~/tools
wget https://storage.googleapis.com/flutter_infra/releases/stable/macos/flutter_macos_2.0.6-stable.zip

brew install adoptopenjdk/openjdk/adoptopenjdk8
brew install gradle android-sdk
# brew install android-ndk
```
<!-- ~/.bash_profile -->
```sh
export PATH="$PATH:$HOME/tools/cmdline-tools/bin"
export PATH="$PATH:$HOME/tools/flutter/bin:$HOME/Library/Android/sdk/platform-tools"
export PATH="$PATH:/usr/local/opt/openjdk/bin"

export JAVA_HOME=/Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home
export CPPFLAGS="-I/usr/local/opt/openjdk/include"
```

```sh
touch ~/.android/repositories.cfg
yes | sdkmanager --licenses

sdkmanager --update
# sdkmanager "platform-tools" "platforms;android-28"
sdkmanager --no_https --install platform-tools
sdkmanager --no_https --install 'platforms;android-29'
sdkmanager --no_https --install 'build-tools;28.0.3'
# sdkmanager --list | grep 29
```

## Build an Android (iOS) app around the idea of ["Vượt qua dễ duôi"](https://sutamphap.com/hoi-dap-3-vuot-qua-de-duoi/)

...

### 2. Gần gũi bậc thiện trí, tiếp xúc thường xuyên với những thứ nhắc nhở mình tinh tấn

Sự nhắc nhở thường xuyên là rất quan trọng, đừng ỷ lại vào kiến thức và ảo tưởng về sức mạnh tinh tấn trong mình. Hãy đặt quanh mình, dán trên tường ở những chỗ mình hay nhìn, những hình ảnh nhắc nhở bản thân như: ảnh Phật, các câu nhắc chánh niệm, hay những câu quote gây động tâm, đặt ảnh màn hình chờ… Con có thể tải chương trình “Tiếng chuông chánh niệm” ở trang sutamphap.com về điện thoại để nó nhắc con mỗi 5 phút, 10 phút.

Ngoài ra, hãy nghe pháp thường xuyên, đó là sự nhắc nhở và sách tấn rất lớn. Nghe 1 lần con không hiểu hết đâu, thực hành rồi nghe lại, lại thấy như mới, hiểu sâu hơn và động tâm ở những câu trước kia mình không cảm nhận được gì. Đấy chính là gần gũi bậc thiện trí con ạ.

- [x] [**DONE:** Android widget to add quotes, "Nghe Pháp" on the homescreen, and play remind bell every 30-mins](docs/widget.md)

- - - 

### 3. Luyện thói quen trì hoãn, làm chậm mọi việc, dừng lại 1 phút trước khi quyết định làm gì, không bao giờ làm và quyết định khi cảm xúc đang ở đỉnh cao.

Sự thúc giục của cảm xúc luôn dâng trào bồng bột, nhưng chóng tàn. Chúng ta sẽ thấy sự thúc bách ấy như cưỡng ép, nhưng nếu trì hoãn 1 chút cho qua đỉnh cảm xúc, chúng ta sẽ có cơ hội chọn lựa có làm theo hay không, có cơ hội suy xét: đó là tham, sân, ngã mạn, ghen tỵ… muốn hay thực sự là mình muốn. Vì vậy, đừng đáp ứng ngay lập tức đòi hỏi của tâm, hãy tìm mọi cách trì hoãn, một phút tạm dừng đó sẽ cứu mình khỏi những bàn thua trông thấy.

### 4. Rèn luyện khả năng tự chế, bắt đầu từ những việc nhỏ nhất

Người càng dễ duôi, khả năng tự chế càng kém. Khả năng tự chế trước mọi cám dỗ và thúc bách của tâm không phải tự nhiên mà có, mà là kết quả của sự rèn luyện thường xuyên, lâu dài. Hãy bắt đầu từ những việc nhỏ nhất, chẳng hạn: Tự chế ngự sự thúc giục muốn mở điện thoại xem tin nhắn mới đến – làm các bài test thử xem được bao nhiêu phút. Tự chế chỉ ăn 80% dạ dày; hoặc nhất định không gắp món ngon kia; bữa thắng, bữa thua, không sao cả. Thắng thua không quan trọng, điều quan trọng là mình đang tự rèn luyện.

Tự chế không phạm giới bằng cách chủ động tham gia vào 1 câu chuyện với ý định rèn luyện không nói lời vô ích trong câu chuyện này. Để cái bánh ngọt trước mặt và nhìn nó, rèn luyện sự tự chế không ăn, đo xem chống cự được bao nhiêu phút để so với lần sau… (nhưng những cám dỗ quá lớn có khả năng phạm giới với hậu quả nặng thì đừng mang ra mà luyện. Tránh né là tốt nhất, yếu không nên ra gió).

Khả năng tự chế của chúng ta kém là vì chúng ta không bao giờ chủ động rèn luyện nó, mà chỉ khi gặp cám dỗ mới bị động mang đội quân chẳng bao giờ huấn luyện ra chống địch. Thua không oan.

- - - 

### 5. Có chế độ thưởng phạt và nghiêm túc làm theo. Kiểm điểm và ghi chép lại

Nhưng chớ có thưởng cho mình bằng cách được phép làm 1 cái gì có hại mà mình vẫn thường tự ngăn cấm bản thân. Phạt thì phạt theo cách tích cực, chẳng hạn lỡ ăn 1 miếng bánh thì phạt ngồi thiền 10 phút, hoặc tập thể dục 10 phút, đi bộ 1km...

Nếu thấy mình nghị lực và tự giác kém thì nhờ người nhà làm trọng tài giám sát hộ. Ghi chép lại mỗi ngày thành bảng biểu để theo dõi mức độ tiến bộ, nhìn lại có thể thấy việc nào hay dễ duôi nhất, dựa trên thống kê để có chiến lược đối phó thích hợp.

Đừng bao giờ nghĩ rằng mình đã hiểu bản thân. Sự ghi chép thực tế và khách quan luôn cho chúng ta thấy rõ các mặt khuất và các điểm mù. Người tu tập thành công luôn là người rất nghiêm túc với bản thân, rất tự giác và nỗ lực.

### 6. Thay thế ý định dễ duôi bằng việc làm ngay 1 việc tích cực.

Tập thói quen năng động, sử dụng tối đa thời gian, không bao giờ ngồi không

- [ ] **TODO:** (6+5) = Mimic [HabitNow](https://play.google.com/store/apps/details?id=com.habitnow) to track & form good-habits

| ![1](https://play-lh.googleusercontent.com/-oMKK_tov5OFjQHc0cIhz8CgD67xFKUqv0fvrvLL7_ncQp_liszs1LjCCszJx1avcb9B=w1440-h620)| ![2](https://play-lh.googleusercontent.com/mkWhNHo-8hAoHcj05Yghf8d-Lc0XW2UJs-wnkahQ_XihUh90onR14lNb_Sk1oPLh4UA=w1440-h620) | ![3](https://play-lh.googleusercontent.com/1XsphzsX_jx2ckAkt_HUuP942FEwtc_cz0xlakMzJWkB0zh9zqU1wTfJVPE1MSrfegY=w1440-h620) |
| ------------------- |:---------------------:|:---------------------:|


- - - 

https://github.com/tiendung/tiendung.github.io/archive/refs/heads/main.zip

- - - 

https://heartbeat.fritz.ai/10-tips-to-speed-up-android-gradle-build-times-c0ec665fc800

- - -

https://stackoverflow.com/questions/59823810/calling-a-function-and-passing-arguments-from-kotlin-to-flutter
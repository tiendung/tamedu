# Use Flutter for main app UI

https://github.com/olexale/flutter_roadmap
https://github.com/Solido/awesome-flutter
https://flutterstudio.app/ UI Builder

```bash
flutter doctor --android-licenses
flutter devices
flutter emulators
flutter emulators --launch Pixel_3a_API_30_x86
flutter run
```
## Run on real device

https://developer.android.com/studio/debug/dev-options
Tap 'Build Number' 7 times `Settings > About Phone > Software Information > Build Number` to enable developer options

https://flutter.dev/docs/resources/faq#what-devices-and-os-versions-does-flutter-run-on

Mobile operating systems: Android Jelly Bean, v16, 4.1.x or newer, and iOS 8 or newer.

Mobile hardware: iOS devices (iPhone 4S or newer) and ARM Android devices.


## Run on web (for fast prototying)

https://flutter.dev/docs/get-started/web

## Run on desktop (for fastest build)

https://github.com/go-flutter-desktop/hover

```bash
brew install go
GO111MODULE=on go get -u -a github.com/go-flutter-desktop/hover
~/go/bin/hover init github.com/tiendung/tamedu
~/go/bin/hover run
```

## Add navigation to main screen

https://flutter.dev/docs/cookbook/design/drawer
https://gallery.flutter.dev/#/demo/nav_rail

Các ứng dụng di động thường được hiển thị dưới dạng Full-screen thường được gọi là "screens" hoặc "pages". Trong Flutter, chúng được gọi là các routes và được quản lý bởi Navigator widget. Navigator giữ stack các route và cung cấp các method để quản lý stack đó như: Navigator.push và Navigator.pop

## Bonus

https://www.funkyspacemonkey.com/a-free-and-open-source-macos-system-cleaner
https://ingmarstein.github.io/Monolingual

```bash
brew install clean-me
brew install monolingual
brew install AppCleaner
brew install GrandPerspective
```

### Zola & geek sites
https://mrkaran.dev/posts/
https://endler.dev/2020/review/
https://jameslittle.me/

https://pub.dev/packages/permission_handler/example

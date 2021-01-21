# Use Flutter for main app UI

https://viblo.asia/p/cross-platform-showdown-2020-react-native-vs-flutter-1VgZv0GR5Aw
https://github.com/Solido/awesome-flutter

`flutter doctor --android-licenses`
`flutter devices`
`flutter emulators`
`flutter emulators --launch Pixel_3a_API_30_x86`
`flutter run`

## Run on real device

https://developer.android.com/studio/debug/dev-options
Tap 'Build Number' 7 times 'Settings > About Phone > Software Information > Build Number' to enable developer options

https://flutter.dev/docs/resources/faq#what-devices-and-os-versions-does-flutter-run-on
Mobile operating systems: Android Jelly Bean, v16, 4.1.x or newer, and iOS 8 or newer.
Mobile hardware: iOS devices (iPhone 4S or newer) and ARM Android devices.


## Run on web (for fast prototying)

https://flutter.dev/docs/get-started/web

## Run on desktop (for fastest build)

https://github.com/go-flutter-desktop/hover
`brew install go`
`GO111MODULE=on go get -u -a github.com/go-flutter-desktop/hover`
`~/go/bin/hover init github.com/tiendung/tameu`
`~/go/bin/hover run`

## Add navigation to main screen

https://flutter.dev/docs/cookbook/design/drawer
https://gallery.flutter.dev/#/demo/nav_rail

Các ứng dụng di động thường được hiển thị dưới dạng Full-screen thường được gọi là "screens" hoặc "pages". Trong Flutter, chúng được gọi là các routes và được quản lý bởi Navigator widget. Navigator giữ stack các route và cung cấp các method để quản lý stack đó như: Navigator.push và Navigator.pop
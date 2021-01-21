# Tamedu: Tsutamphap.com in Practice

Add a widget to android homescreen that show sutamphap.com's quotes:
+ [LƯU] button to save quote image (quote$id.png) to disk
+ [ĐỌC TO/DỪNG ĐỌC] button to turn on/off speaking quote outloud
+ [NGHE PHÁP] Play random seleted phaps
+ Change quote every 30 mins with a mindfulness bell
+ Click on quote image to change to the new one

- [Tamedu: Tsutamphap.com in Practice](#tamedu-tsutamphapcom-in-practice)
  - [1. Diplay random quote on homescreen widget and talk it outloud](#1-diplay-random-quote-on-homescreen-widget-and-talk-it-outloud)
    - [Resources](#resources)
    - [How to play audio from an url?](#how-to-play-audio-from-an-url)
    - [How to prepareAsync so that mediaPlayer don't block main UI?](#how-to-prepareasync-so-that-mediaplayer-dont-block-main-ui)
    - [How to refresh a widget after a fixed period of time?](#how-to-refresh-a-widget-after-a-fixed-period-of-time)
    - [Refine widget UI](#refine-widget-ui)
      - [Set widget minWidth and minHeight](#set-widget-minwidth-and-minheight)
    - [How to load image and audio from local resources (within the app)?](#how-to-load-image-and-audio-from-local-resources-within-the-app)
    - [How to save phap image to album?](#how-to-save-phap-image-to-album)
    - [LATER: How to make use of Google Keep widget and Zalo floating widget?](#later-how-to-make-use-of-google-keep-widget-and-zalo-floating-widget)
    - [where is APK file?](#where-is-apk-file)
  - [2. Use Flutter for main app UI](#2-use-flutter-for-main-app-ui)
    - [Run on real device](#run-on-real-device)
    - [Run on web (for fast prototying)](#run-on-web-for-fast-prototying)
    - [Run on desktop (for fastest build)](#run-on-desktop-for-fastest-build)
    - [Add navigation to main screen](#add-navigation-to-main-screen)
    - [TODO: Build main app based-on vuot-qua-de-duoi](#todo-build-main-app-based-on-vuot-qua-de-duoi)

## 1. Diplay random quote on homescreen widget and talk it outloud

### Resources

1675 quotes https://tiendung.github.io/quotes.js (50mb images + audios)
* image at https://tiendung.github.io/quotes/650x/i.png (20mb total, 12k avg)
* audio at https://tiendung.github.io/quotes/opus/i.ogg (30mb total, 18k avg)

999 shortest quotes (21mb images + audios)
* image at ./quotes/650x/i.png ( 9mb, total,  9k avg)
* audio at ./quotes/opus/i.ogg (12mb, total  12k avg)

### How to play audio from an url?

https://developer.android.com/guide/topics/media/media-formats
Opus		Android 5.0+		• Ogg (.ogg), • Matroska (.mkv)
https://developer.android.com/guide/topics/media/mediaplayer

### How to prepareAsync so that mediaPlayer don't block main UI?

https://developer.android.com/reference/android/media/MediaPlayer.OnPreparedListener

### How to refresh a widget after a fixed period of time?

https://developer.android.com/reference/android/appwidget/AppWidgetProviderInfo.html#updatePeriodMillis

Change field android:updatePeriodMillis attribute in the AppWidget meta-data file. (/app/src/main/res/xml/app_widget_info.xml)

Note: If the device is asleep when it is time for an update (as defined by updatePeriodMillis), then the device will wake up in order to perform the update. If you don't update more than once per hour, this probably won't cause significant problems for the battery life. If, however, you need to update more frequently and/or you do not need to update while the device is asleep, then you can instead perform updates based on an alarm that will not wake the device. To do so, set an alarm with an Intent that your AppWidgetProvider receives, using the AlarmManager. Set the alarm type to either ELAPSED_REALTIME or RTC, which will only deliver the alarm when the device is awake. Then set updatePeriodMillis to zero ("0").

LATER: Using AlarmManager to update quote every 5 minutes
https://code.tutsplus.com/tutorials/code-a-widget-for-your-android-app-updating-the-widget-continued--cms-30669
https://www.appsrox.com/android/tutorials/dailyvocab/2/

```Kotlin
class AppWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        val intent = Intent(context, AppWidget::class.java)
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)

        val service = PendingIntent.getService(context, 0,
                // intent, PendingIntent.FLAG_CANCEL_CURRENT)
                intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Set your update interval to 300000 milliseconds (5 mins).
        // 60000 milliseconds (1 min) is the minimum interval you can use
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                 SystemClock.elapsedRealtime(), 60000, service)
```

### Refine widget UI

#### Set widget minWidth and minHeight

https://developer.android.com/codelabs/advanced-android-training-widgets#7
| # of cols or rows   | minWidth or minHeight |
| ------------------- |:---------------------:|
| 1                   |  40 dp                |
| 2                   | 110 dp                |
| 3                   | 180 dp                |
| 4                   | 250 dp                |

Set app_widget_info.xml: minWidth to 4 rows (250dp) and 3 cols (180dp)

### How to load image and audio from local resources (within the app)?

https://developer.android.com/guide/topics/resources/providing-resources
If you need access to original file names and file hierarchy, you might consider 
saving some resources in the assets/ directory (instead of res/raw/). 
Files in assets/ aren't given a resource ID, so you can read them only using AssetManager.

https://developer.android.com/reference/kotlin/android/content/res/AssetManager
https://medium.com/mobile-app-development-publication/assets-or-resource-raw-folder-of-android-5bdc042570e0
https://stackoverflow.com/questions/16715003/simple-mediaplayer-play-mp3-from-file-path

```Kotlin
    val assetManager = context.getAssets()
    val inputStream = assetManager.open("quotes/$quoteId.png")
    context.getAssets().list("quotes/$quoteId.ogg")

    // Load audio from local resources
    // val audioUrl = if (quoteId == -1)  // Play a bell
    //         // context.getAssets().list("bell.ogg")?.first()
    //         "file:///android_asset/bell.ogg"
    //     else  // Play a quote
    //         // context.getAssets().list("quotes/$quoteId.ogg")?.first()
    //         "file:///android_asset/quotes/$quoteId.ogg"
```
The URI "file:///android_asset/" points to YourProject/app/src/main/assets/.
Caused by: java.io.FileNotFoundException: /android_asset/quotes/1159.ogg: open failed: ENOENT (No such file or directory)

Calling setDataSource(java.io.FileDescriptor), or setDataSource(java.lang.String), or setDataSource(android.content.Context,android.net.Uri), or setDataSource(java.io.FileDescriptor,long,long), or setDataSource(android.media.MediaDataSource) transfers a MediaPlayer object in the Idle state to the

### How to save phap image to album?

https://developer.android.com/reference/android/content/Context#getExternalFilesDir(java.lang.String)

```Kotlin
// Storage Permissions
// https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android
// https://developer.android.com/training/permissions/requesting.html
private const val REQUEST_EXTERNAL_STORAGE = 1
private val PERMISSIONS_STORAGE = arrayOf<String>(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
)
```
Removed code
```Kotlin
internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
  ...
    // Click on the quote image to open the main app
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    views.setOnClickPendingIntent(R.id.appwidget_image, pendingIntent)
```

NOW: save to ... 'Android/data/dev.tiendung.tamedu/files/quotes'

LATER: request access to storage permission to save to public `Pictures` folder
https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android
https://developer.android.com/training/permissions/requesting.html

### LATER: How to make use of Google Keep widget and Zalo floating widget?

 Google Keep widget click on [+] button will show a popup menu that dim the whole screen.
It very good to quick select a task. Select a phap to play for example ...

Floating widget (like Zalo chat)
https://viblo.asia/p/huong-dan-tao-ung-dung-su-dung-floating-widget-giong-facebook-messenger-gEmROxLEKpv

### where is APK file?

`ls app/build/outputs/apk/debug/app-debug.apk`


## 2. Use Flutter for main app UI

https://viblo.asia/p/cross-platform-showdown-2020-react-native-vs-flutter-1VgZv0GR5Aw
https://github.com/Solido/awesome-flutter

`flutter doctor --android-licenses`
`flutter devices`
`flutter emulators`
`flutter emulators --launch Pixel_3a_API_30_x86`
`flutter run`

### Run on real device

https://developer.android.com/studio/debug/dev-options
Tap 'Build Number' 7 times 'Settings > About Phone > Software Information > Build Number' to enable developer options

https://flutter.dev/docs/resources/faq#what-devices-and-os-versions-does-flutter-run-on
Mobile operating systems: Android Jelly Bean, v16, 4.1.x or newer, and iOS 8 or newer.
Mobile hardware: iOS devices (iPhone 4S or newer) and ARM Android devices.


### Run on web (for fast prototying)

https://flutter.dev/docs/get-started/web

### Run on desktop (for fastest build)

https://github.com/go-flutter-desktop/hover
`brew install go`
`GO111MODULE=on go get -u -a github.com/go-flutter-desktop/hover`
`~/go/bin/hover init github.com/tiendung/tameu`
`~/go/bin/hover run`

### Add navigation to main screen

https://flutter.dev/docs/cookbook/design/drawer
https://gallery.flutter.dev/#/demo/nav_rail

Các ứng dụng di động thường được hiển thị dưới dạng Full-screen thường được gọi là "screens" hoặc "pages". Trong Flutter, chúng được gọi là các routes và được quản lý bởi Navigator widget. Navigator giữ stack các route và cung cấp các method để quản lý stack đó như: Navigator.push và Navigator.pop

### TODO: Build main app based-on vuot-qua-de-duoi

Rèn luyện khả năng tự chế, bắt đầu từ những việc nhỏ nhất
Người càng dễ duôi, khả năng tự chế càng kém. Khả năng tự chế trước mọi cám dỗ và thúc bách của tâm không phải tự nhiên mà có, mà là kết quả của sự rèn luyện thường xuyên, lâu dài. Hãy bắt đầu từ những việc nhỏ nhất, chẳng hạn: Tự chế ngự sự thúc giục muốn mở điện thoại xem tin nhắn mới đến – làm các bài test thử xem được bao nhiêu phút. Tự chế chỉ ăn 80% dạ dày; hoặc nhất định không gắp món ngon kia; bữa thắng, bữa thua, không sao cả.

Thắng thua không quan trọng, điều quan trọng là mình đang tự rèn luyện. Tự chế không phạm giới bằng cách chủ động tham gia vào 1 câu chuyện với ý định rèn luyện không nói lời vô ích trong câu chuyện này. Để cái bánh ngọt trước mặt và nhìn nó, rèn luyện sự tự chế không ăn, đo xem chống cự được bao nhiêu phút để so với lần sau… (nhưng những cám dỗ quá lớn có khả năng phạm giới với hậu quả nặng thì đừng mang ra mà luyện. Tránh né là tốt nhất, yếu không nên ra gió).

Khả năng tự chế của chúng ta kém là vì chúng ta không bao giờ chủ động rèn luyện nó, mà chỉ khi gặp cám dỗ mới bị động mang đội quân chẳng bao giờ huấn luyện ra chống địch. Thua không oan.

- - - 

Có chế độ thưởng phạt và nghiêm túc làm theo. Kiểm điểm và ghi chép lại
Nhưng chớ có thưởng cho mình bằng cách được phép làm 1 cái gì có hại mà mình vẫn thường tự ngăn cấm bản thân. Phạt thì phạt theo cách tích cực, chẳng hạn lỡ ăn 1 miếng bánh thì phạt ngồi thiền 10 phút, hoặc tập thể dục 10 phút, đi bộ 1km...

Nếu thấy mình nghị lực và tự giác kém thì nhờ người nhà làm trọng tài giám sát hộ. Ghi chép lại mỗi ngày thành bảng biểu để theo dõi mức độ tiến bộ, nhìn lại có thể thấy việc nào hay dễ duôi nhất, dựa trên thống kê để có chiến lược đối phó thích hợp.

Đừng bao giờ nghĩ rằng mình đã hiểu bản thân. Sự ghi chép thực tế và khách quan luôn cho chúng ta thấy rõ các mặt khuất và các điểm mù. Người tu tập thành công luôn là người rất nghiêm túc với bản thân, rất tự giác và nỗ lực.

- - - 

Thay thế ý định dễ duôi bằng việc làm ngay 1 việc tích cực. Tập thói quen năng động, sử dụng tối đa thời gian, không bao giờ ngồi không

# Tamedu: The Mind Education App

Add a widget to android homescreen that show sutamphap.com's quotes:
+ [LƯU] button to save quote image (quote$id.png) to disk
+ [ĐỌC TO/DỪNG ĐỌC] button to turn on/off speaking quote outloud
+ [NGHE PHÁP] Play random seleted phaps
+ Change quote every 30 mins with a mindfulness bell
+ Click on quote image to change to the new one

- [Tamedu: The Mind Education App](#tamedu-the-mind-education-app)
  - [1. Diplay random quote on homescreen widget and talk it outloud](#1-diplay-random-quote-on-homescreen-widget-and-talk-it-outloud)
    - [Resources](#resources)
    - [How to create an android app with homescreen widget?](#how-to-create-an-android-app-with-homescreen-widget)
    - [How to add image from an url to android homesreen widget using glide kotlin?](#how-to-add-image-from-an-url-to-android-homesreen-widget-using-glide-kotlin)
    - [How to show Glide image autoscale to target width?](#how-to-show-glide-image-autoscale-to-target-width)
    - [How to play audio from an url?](#how-to-play-audio-from-an-url)
    - [How to prepareAsync so that mediaPlayer don't block main UI?](#how-to-prepareasync-so-that-mediaplayer-dont-block-main-ui)
    - [How to handle (click) events on widget?](#how-to-handle-click-events-on-widget)
    - [How to refresh a widget after a fixed period of time?](#how-to-refresh-a-widget-after-a-fixed-period-of-time)
    - [Refine widget UI](#refine-widget-ui)
      - [Set widget minWidth and minHeight](#set-widget-minwidth-and-minheight)
      - [Remove default textview](#remove-default-textview)
    - [How to load image and audio from local resources (within the app)?](#how-to-load-image-and-audio-from-local-resources-within-the-app)
    - [How to save phap image to album?](#how-to-save-phap-image-to-album)
    - [TODO: How to make use of Google Keep widget and Zalo floating widget?](#todo-how-to-make-use-of-google-keep-widget-and-zalo-floating-widget)
    - [where is APK file?](#where-is-apk-file)
  - [2. Use Flutter for main app UI](#2-use-flutter-for-main-app-ui)
    - [Run on real device](#run-on-real-device)
    - [Run on web (for fast prototying)](#run-on-web-for-fast-prototying)
    - [Run on desktop (for fastest build)](#run-on-desktop-for-fastest-build)
    - [Add navigation to main screen](#add-navigation-to-main-screen)
    - [TODO: Present vuot-qua-de-duoi in cards that swipable](#todo-present-vuot-qua-de-duoi-in-cards-that-swipable)
    - [TODO: List phaps as a list of item on main screen](#todo-list-phaps-as-a-list-of-item-on-main-screen)
  - [3. Use Android notifications to remind nghe-phap every day at 6am](#3-use-android-notifications-to-remind-nghe-phap-every-day-at-6am)
    - [TODO: Show a notification on both lock screen and notification bar. Click on it will lead to tiendung.github.io to nghe-phap](#todo-show-a-notification-on-both-lock-screen-and-notification-bar-click-on-it-will-lead-to-tiendunggithubio-to-nghe-phap)

## 1. Diplay random quote on homescreen widget and talk it outloud

### Resources

1675 quotes https://tiendung.github.io/quotes.js (50mb images + audios)
* image at https://tiendung.github.io/quotes/650x/i.png (20mb total, 12k avg)
* audio at https://tiendung.github.io/quotes/opus/i.ogg (30mb total, 18k avg)

999 shortest quotes (21mb images + audios)
* image at ./quotes/650x/i.png ( 9mb, total,  9k avg)
* audio at ./quotes/opus/i.ogg (12mb, total  12k avg)

### How to create an android app with homescreen widget?

* https://inspirecoding.app/android-widgets-advanced/#elementor-toc__heading-anchor-8
* https://inspirecoding.app/android-widgets-update-using-kotlin-flow-room-and-hilt/
* https://developer.android.com/reference/android/widget/RemoteViews

From Android 5.0 you can add widgets only to the Home screen. The previous Android versions allow you to place widgets on the lock screen as well.

Right mouse button click on the main source set (where you can find the MainAcitvity.kt file as well). Then, from the quick menu select New, then the Widget option. From the submenu choose the App Widget option.

### How to add image from an url to android homesreen widget using glide kotlin?

* https://bumptech.github.io/glide/doc/download-setup.html#gradle
* https://futurestud.io/tutorials/glide-loading-images-into-notifications-and-appwidgets !!
* https://www.c-sharpcorner.com/article/how-to-load-the-imageurl-to-imageview-using-glide-in-kotlin2/ !

`code app/build.gradle`

```Kotlin
dependencies {
    implementation "com.github.bumptech.glide:glide:4.11.0"
    annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'     
```

`code AppWidget.kt`

```Kotlin
import com.bumptech.glide.Glide  
...
```

### How to show Glide image autoscale to target width?

NOW: Set override(1200) to scale image to fit widescreen phone screen

LATER: need to find widget width to override(size) the image

### How to play audio from an url?

https://developer.android.com/guide/topics/media/media-formats
Opus		Android 5.0+		• Ogg (.ogg), • Matroska (.mkv)
https://developer.android.com/guide/topics/media/mediaplayer

### How to prepareAsync so that mediaPlayer don't block main UI?

https://developer.android.com/reference/android/media/MediaPlayer.OnPreparedListener

### How to handle (click) events on widget?

Android remoteview widget only support press (click) and scroll event
https://developer.android.com/codelabs/advanced-android-training-widgets#4

```Kotlin
internal fun updateAppWidget
... // ADD
    // Click on the quote image to update the widget
    val intent = Intent(context, AppWidget::class.java)
    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
    val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    views.setOnClickPendingIntent(R.id.appwidget_image, pendingIntent)
```

### How to refresh a widget after a fixed period of time?

https://developer.android.com/reference/android/appwidget/AppWidgetProviderInfo.html#updatePeriodMillis

Change field android:updatePeriodMillis attribute in the AppWidget meta-data file. (/app/src/main/res/xml/app_widget_info.xml)

Note: If the device is asleep when it is time for an update (as defined by updatePeriodMillis), then the device will wake up in order to perform the update. If you don't update more than once per hour, this probably won't cause significant problems for the battery life. If, however, you need to update more frequently and/or you do not need to update while the device is asleep, then you can instead perform updates based on an alarm that will not wake the device. To do so, set an alarm with an Intent that your AppWidgetProvider receives, using the AlarmManager. Set the alarm type to either ELAPSED_REALTIME or RTC, which will only deliver the alarm when the device is awake. Then set updatePeriodMillis to zero ("0").

TODO: Using AlarmManager to update quote every 5 minutes
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

#### Remove default textview

```XML
    <TextView
        android:id="@+id/appwidget_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerHorizontal="true"
        android:layout_centerVertical="true"
        android:layout_margin="8dp"
        android:background="?attr/appWidgetBackgroundColor"
        android:contentDescription="@string/appwidget_text"
        android:text="@string/appwidget_text"
        android:textColor="?attr/appWidgetTextColor"
        android:textSize="24sp"
        android:textStyle="bold|italic" />
```

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

Click on imageview to save it to album
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

### TODO: How to make use of Google Keep widget and Zalo floating widget?

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

### TODO: Present vuot-qua-de-duoi in cards that swipable

https://pub.dev/packages/flutter_swiper/install

### TODO: List phaps as a list of item on main screen

Data at https://tiendung.github.io/phaps.json

## 3. Use Android notifications to remind nghe-phap every day at 6am

### TODO: Show a notification on both lock screen and notification bar. Click on it will lead to tiendung.github.io to nghe-phap
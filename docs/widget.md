# The Android Widget

Add a widget to android homescreen that show sutamphap.com's quotes:
+ [LƯU] button to save quote image (quote$id.png) to disk
+ [ĐỌC TO/DỪNG ĐỌC] button to turn on/off speaking quote outloud
+ [NGHE PHÁP] Play random seleted phaps
+ Change quote every 30 mins with a mindfulness bell
+ Click on quote image to change to the new one

- [The Android Widget](#the-android-widget)
  - [1. Diplay random quote on homescreen widget and talk it outloud](#1-diplay-random-quote-on-homescreen-widget-and-talk-it-outloud)
    - [Resources](#resources)
    - [How to add image from an url to android homesreen widget using glide kotlin?](#how-to-add-image-from-an-url-to-android-homesreen-widget-using-glide-kotlin)
    - [How to play audio from an url?](#how-to-play-audio-from-an-url)
    - [How to prepareAsync so that mediaPlayer don't block main UI?](#how-to-prepareasync-so-that-mediaplayer-dont-block-main-ui)
    - [How to refresh a widget after a fixed period of time?](#how-to-refresh-a-widget-after-a-fixed-period-of-time)
    - [Refine widget UI](#refine-widget-ui)
      - [Set widget minWidth and minHeight](#set-widget-minwidth-and-minheight)
    - [How to load image and audio from local resources (within the app)?](#how-to-load-image-and-audio-from-local-resources-within-the-app)
    - [How to save phap image to album?](#how-to-save-phap-image-to-album)
    - [LATER: How to make use of Google Keep widget and Zalo floating widget?](#later-how-to-make-use-of-google-keep-widget-and-zalo-floating-widget)
    - [where is APK file?](#where-is-apk-file)

## 1. Diplay random quote on homescreen widget and talk it outloud

### Resources

1675 quotes https://tiendung.github.io/quotes.js (50mb images + audios)
* image at https://tiendung.github.io/quotes/650x/i.png (20mb total, 12k avg)
* audio at https://tiendung.github.io/quotes/opus/i.ogg (30mb total, 18k avg)

999 shortest quotes (21mb images + audios)
* image at ./quotes/650x/i.png ( 9mb, total,  9k avg)
* audio at ./quotes/opus/i.ogg (12mb, total  12k avg)

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
fun showQuote(quote: Quote, context: Context, views: RemoteViews, appWidgetId: Int) {
    val appWidgetTarget = AppWidgetTarget(context, R.id.appwidget_image, views, appWidgetId)
    Glide.with(context)
            .asBitmap()
            .load(quote.imageUri)
            .override(1200)
            .into(appWidgetTarget)
} 
```

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

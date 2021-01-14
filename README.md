# 1. Diplay random quote on homescreen widget and talk it outloud

## Resources
1675 quotes https://tiendung.github.io/quotes.js
* image at https://tiendung.github.io/quotes/650x/i.png (20mb total, 12k avg)
* audio at https://tiendung.github.io/quotes/opus/i.ogg (30mb total, 18k avg)

## How to create an android app with homescreen widget?
* https://inspirecoding.app/android-widgets-advanced/#elementor-toc__heading-anchor-8
* https://inspirecoding.app/android-widgets-update-using-kotlin-flow-room-and-hilt/
* https://developer.android.com/reference/android/widget/RemoteViews

From Android 5.0 you can add widgets only to the Home screen. The previous Android versions allow you to place widgets on the lock screen as well.

Right mouse button click on the main source set (where you can find the MainAcitvity.kt file as well). Then, from the quick menu select New, then the Widget option. From the submenu choose the App Widget option.

## How to add image from an url to android homesreen widget using glide kotlin?
* https://bumptech.github.io/glide/doc/download-setup.html#gradle
* https://futurestud.io/tutorials/glide-loading-images-into-notifications-and-appwidgets !!
* https://www.c-sharpcorner.com/article/how-to-load-the-imageurl-to-imageview-using-glide-in-kotlin2/ !

`code app/build.gradle`
```
dependencies {
    implementation "com.github.bumptech.glide:glide:4.11.0"
    annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'     
```
Click Sync Now to rebuild the project with the new dependency.

`AppWidget.kt`
```
import com.bumptech.glide.Glide  
...
```

## How to show Glide image autoscale to target width?
```
Glide
    .override(size) // width or height?
```
LATER: need to find widget width to override the image

## How to play audio from an url?
https://developer.android.com/guide/topics/media/media-formats
Opus		Android 5.0+		• Ogg (.ogg), • Matroska (.mkv)
https://developer.android.com/guide/topics/media/mediaplayer
LATER: load remote audio in a separate thread/worker/routine so it don't block the main app

## How to handle (click) events on widget?
android remoteview widget only support press (click) and scroll event
https://developer.android.com/codelabs/advanced-android-training-widgets#4
```
internal fun updateAppWidget
... // ADD
    val intent = Intent(context, AppWidget::class.java)
    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
    val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    views.setOnClickPendingIntent(R.id.appwidget_image, pendingIntent)
```

## How to refresh a widget after a fixed period of time?
https://developer.android.com/reference/android/appwidget/AppWidgetProviderInfo.html#updatePeriodMillis
Change field android:updatePeriodMillis attribute in the AppWidget meta-data file. (/app/src/main/res/xml/app_widget_info.xml)

Note: If the device is asleep when it is time for an update (as defined by updatePeriodMillis), then the device will wake up in order to perform the update. If you don't update more than once per hour, this probably won't cause significant problems for the battery life. If, however, you need to update more frequently and/or you do not need to update while the device is asleep, then you can instead perform updates based on an alarm that will not wake the device. To do so, set an alarm with an Intent that your AppWidgetProvider receives, using the AlarmManager. Set the alarm type to either ELAPSED_REALTIME or RTC, which will only deliver the alarm when the device is awake. Then set updatePeriodMillis to zero ("0").

### LATER: Using AlarmManager to update quote every 5 minutes
https://yalantis.com/blog/implement-app-widgets-android/

## Refine widget UI
### Set widget minWidth and minHeight
https://developer.android.com/codelabs/advanced-android-training-widgets#7
| # of cols or rows   | minWidth or minHeight |
| ------------------- |:---------------------:|
| 1                   |  40 dp                |
| 2                   | 110 dp                |
| 3                   | 180 dp                |
| 4                   | 250 dp                |

Set app_widget_info.xml: minWidth to 4 rows (250dp) and 3 cols (180dp)

### Remove default textview
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

## where is APK file?
`ls app/build/outputs/apk/debug/app-debug.apk`

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

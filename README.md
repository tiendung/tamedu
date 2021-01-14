# 1. Diplay random quote on homescreen widget and talk it outloud

## Resources
1675 quotes https://tiendung.github.io/quotes.js
* image at https://tiendung.github.io/quotes/650x/i.png (20mb total, 12k avg)
* audio at https://tiendung.github.io/quotes/opus/i.ogg (30mb total, 18k avg)

## How to create an android app with homescreen widget?
* https://inspirecoding.app/android-widgets-basics/
* https://inspirecoding.app/android-widgets-update-using-kotlin-flow-room-and-hilt/
* https://google-developer-training.github.io/android-developer-advanced-course-concepts/unit-1-expand-the-user-experience/lesson-2-app-widgets/2-1-c-app-widgets/2-1-c-app-widgets.html

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

## How to click button to play audio from an url?
https://developer.android.com/guide/topics/media/media-formats
Opus		Android 5.0+		• Ogg (.ogg), • Matroska (.mkv)
https://developer.android.com/guide/topics/media/mediaplayer
LATER: load remote audio in a separate thread/worker/routine so it don't block the main app

## How to refresh a widget after a fixed period of time?
https://developer.android.com/reference/android/appwidget/AppWidgetProviderInfo.html#updatePeriodMillis
Change field android:updatePeriodMillis attribute in the AppWidget meta-data file. (/app/src/main/res/xml/app_widget_info.xml)

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

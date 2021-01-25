#!/bin/sh
rm build/app/outputs/flutter-apk/app-release.apk
mv android/app/src/main/assets/quotes ../
flutter build apk --target-platform android-arm
mv ../quotes android/app/src/main/assets
adb install -r build/app/outputs/flutter-apk/app-release.apk
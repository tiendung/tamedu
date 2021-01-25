#!/bin/sh
rm build/app/outputs/flutter-apk/app-release.apk
mkdir ../_save
mv android/app/src/main/assets/quotes ../_save
flutter build apk --target-platform android-arm
mv ../_save/quotes android/app/src/main/assets
adb install -r build/app/outputs/flutter-apk/app-release.apk
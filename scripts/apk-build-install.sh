#!/bin/sh
rm build/app/outputs/flutter-apk/app-release.apk

./sub-assets.sh
flutter build apk --target-platform android-arm
./add-assets.sh

adb install -r build/app/outputs/flutter-apk/app-release.apk
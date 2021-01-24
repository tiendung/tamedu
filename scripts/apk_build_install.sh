#!/bin/sh
rm build/app/outputs/flutter-apk/app-release.apk
flutter build apk --target-platform android-arm
adb install -r build/app/outputs/flutter-apk/app-release.apk
#!/bin/sh
rm android/app/src/main/assets
flutter build apk --target-platform android-arm
ln -s assets android/app/src/main/assets
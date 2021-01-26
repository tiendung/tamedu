#!/bin/sh
rm android/app/src/main/assets
flutter run
ln -s assets android/app/src/main/assets
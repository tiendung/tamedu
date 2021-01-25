#!/bin/sh
./scripts/sub-assets.sh
flutter build apk --target-platform android-arm
./scripts/add-assets.sh

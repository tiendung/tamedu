#!/bin/sh
rm build/app/outputs/flutter-apk/app-release.apk
./scripts/apk-build.sh
./scripts/apk-install.sh
#!/bin/bash
apkFile=build/app/outputs/flutter-apk/app-release.apk
if [ -e $apkFile ]
then
	./scripts/apk-install.sh
else
	./scripts/apk-build.sh
	./scripts/apk-install.sh
fi
rm $apk
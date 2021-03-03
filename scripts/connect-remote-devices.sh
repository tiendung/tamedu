#!/bin/sh
adb usb
adb tcpip 9999
adb connect 192.168.12.30:9999 # home wifi
# adb connect 192.168.42.98:9999 # mobile hotspot

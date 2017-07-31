#!/usr/bin/env bash

rm -rf /Applications/Android\ Studio.app/Contents/plugins/android/lib/templates/other/RxViper
cp -r RxViper /Applications/Android\ Studio.app/Contents/plugins/android/lib/templates/other/

rm -rf /Applications/Android\ Studio\ 3.0.app/Contents/plugins/android/lib/templates/other/RxViper
cp -r RxViper /Applications/Android\ Studio\ 3.0.app/Contents/plugins/android/lib/templates/other/
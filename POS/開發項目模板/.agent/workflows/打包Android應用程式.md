---
description: 執行 Flutter 打包 APK，並依據版本號自動命名與移動至 root/apk 目錄，最後安裝至手機
---

1. **自動更新版本號**
   // turbo
   cd frontend/app && VER_LINE=$(grep '^version: ' pubspec.yaml) && OLD_VER=$(echo $VER_LINE | sed 's/version: //; s/+.*//') && BUILD_NUM=$(echo $VER_LINE | sed 's/.*+//') && IFS='.' read -r MAJOR MINOR PATCH <<< "$OLD_VER" && NEW_PATCH=$((PATCH + 1)) && NEW_BUILD=$((BUILD_NUM + 1)) && NEW_VER="${MAJOR}.${MINOR}.${NEW_PATCH}+${NEW_BUILD}" && sed -i '' "s/^version: .*/version: $NEW_VER/" pubspec.yaml && echo "Bumped version: $OLD_VER+$BUILD_NUM -> $NEW_VER"

2. **編譯與搬運**
   // turbo
   cd frontend/app && VERSION=$(grep '^version: ' pubspec.yaml | sed 's/version: //; s/+.*//') && flutter build apk --release && cd ../.. && mkdir -p apk && mv frontend/app/build/app/outputs/flutter-apk/app-release.apk apk/chuangsheng-moving-$VERSION.apk

3. **確認檔案**
   // turbo
   ls -lh apk/ | tail -n 5

4. **安裝至手機**
   // turbo
   VERSION=$(grep '^version: ' frontend/app/pubspec.yaml | sed 's/version: //; s/+.*//') && adb install -r apk/chuangsheng-moving-$VERSION.apk

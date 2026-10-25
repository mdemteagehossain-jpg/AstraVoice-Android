# AstraVoice AI — Android Java Project

এই repository-তে AstraVoice Android Studio project এবং GitHub Actions দিয়ে APK build করার workflow আছে। এটি **debug APK** তৈরি করবে; Play Store release-এর জন্য আলাদা signing configuration প্রয়োজন।

## GitHub-এ আপলোড ও APK তৈরির ধাপ

### 1. ZIP Extract করুন
এই ZIP extract করুন। GitHub-এ **AstraVoice-Android-Project** ফোল্ডারের ভেতরের ফাইলগুলো upload করবেন—বাইরের parent folder-টি আরেক স্তর হিসেবে নয়।

### 2. GitHub repository তৈরি করুন
1. https://github.com/new খুলুন এবং sign in করুন।
2. Repository name দিন `AstraVoice-Android`।
3. `Public` বা `Private` নির্বাচন করে **Create repository** চাপুন।
4. **Add file → Upload files** চাপুন।
5. Extract করা project-এর সব ফাইল ও `.github` ফোল্ডার drag-and-drop করুন। `.github/workflows/build-apk.yml`-ও যেন upload হয়।
6. **Commit changes** চাপুন।

> বিকল্প: Git ব্যবহার জানলে project folder-এ terminal খুলে `git init`, `git add .`, `git commit -m "Initial Android project"`, তারপর GitHub-এর নির্দেশনা অনুযায়ী remote যোগ করে `git push -u origin main` করতে পারেন।

### 3. Gemini API key (ঐচ্ছিক, AI chat-এর জন্য)
1. Google AI Studio থেকে নিজের Gemini API key নিন: https://aistudio.google.com/apikey
2. GitHub repository → **Settings → Secrets and variables → Actions** খুলুন।
3. **New repository secret** চাপুন। Name দিন `GEMINI_API_KEY`, Value-তে key দিন, Save করুন।
4. API key কখনো source code বা public repository-তে সরাসরি লিখবেন না। Secret না দিলেও build চলতে পারে, তবে AI chat feature কাজ নাও করতে পারে।

### 4. APK build চালান
- Repository-তে **Actions** tab খুলুন।
- বাম পাশে **Build Android APK** workflow নির্বাচন করুন।
- **Run workflow → Run workflow** চাপুন।
- Workflow শেষ হলে সব ধাপ সবুজ হয়েছে কি না দেখুন। কোনো ধাপ লাল হলে সেটি খুলে error log পড়ুন।

`main` branch-এ নতুন commit push করলেও workflow স্বয়ংক্রিয়ভাবে build করবে।

### 5. APK ডাউনলোড করুন
1. সফল workflow run খুলুন।
2. পৃষ্ঠার নিচে **Artifacts** অংশে `AstraVoice-debug-apk` চাপুন।
3. ডাউনলোড হওয়া ZIP extract করুন; এর ভেতরে `app-debug.apk` থাকবে।
4. APK ফোনে পাঠিয়ে install করুন। Android জিজ্ঞেস করলে ওই file manager/browser-এর জন্য **Install unknown apps** অনুমতি দিতে হতে পারে। নিজের তৈরি APK-ই install করুন।

## Android Studio দিয়ে local build
- Android Studio-তে এই project folder খুলুন।
- JDK 17 এবং Android SDK 34 ইনস্টল থাকতে হবে।
- Gradle sync শেষ হলে **Build → Build Bundle(s) / APK(s) → Build APK(s)** নির্বাচন করুন।
- APK সাধারণত `app/build/outputs/apk/debug/app-debug.apk`-এ তৈরি হবে।

## গুরুত্বপূর্ণ
- GitHub Actions workflow **debug APK** তৈরি করে, signed release APK নয়। Release/Play Store-এর জন্য keystore ও signing config নিরাপদভাবে সেট করতে হবে।
- এই project-এর কিছু ফোন-নিয়ন্ত্রণ feature Android version/permission/device policy অনুযায়ী সীমিত হতে পারে।
- `GEMINI_API_KEY`-কে APK-তে embed করা key-কে সম্পূর্ণ গোপন রাখে না। Public distribution-এর জন্য API usage restriction এবং server-side proxy বিবেচনা করুন।
- APK build সফল হওয়া মানেই সব feature device-এ পরীক্ষা করা হয়েছে—এমন নয়। ফোনে install করে microphone, permissions এবং AI chat আলাদাভাবে পরীক্ষা করুন।

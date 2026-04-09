# GrinGuard 🦷

GrinGuard is an intelligent Android application designed to assist users in identifying and managing dental health issues. GrinGuard provides immediate guidance for oral care.

## Features
- AI-Powered Diagnosis: Uses machine learning to detect dental conditions like Calculus, Caries, Gingivitis, and Fractures from photos.
- Virtual Assistant: A real-time assistant powered by Google Gemini to answer oral health questions and provide dental tips.
- Personalized Care Plans: Generates custom treatment and prevention plans based on your specific dental health results.
- User Authentication: Securely store your data using Firebase.
- Modern UI: Clean, responsive interface using Material Design.


## Requirements
1. This app supports Android smartphones, it is preferred to have at-least 8 megapixels of resolution.
2. Local phone storage is used to temporarily store images using Android Studio SDK.
3. Phone processor needs to be fast enough to avoid lagging.
4. There should be a good internet connection.


## Tech Stack
- Language: Java, XML
- AI: Google Gemini API
- ML Engine: TensorFlow Lite & ML Kit
- Backend: Firebase (Authentication & Realtime Database)
- Libraries: 
    - `Markwon`: For rendering rich text in chat.
    - `MPAndroidChart`: For dental health visualizations.
    - `Material Design`: For a modern look and feel.


## Workflow
GrinGuard/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/gringuard/
│   │   │   │   ├── Authentication/
│   │   │   │   │   ├── LoginActivity.java
│   │   │   │   │   └── WelcomeActivity.java
│   │   │   │   ├── Detection_Logic/
│   │   │   │   │   ├── Caries_Activity.java
│   │   │   │   │   ├── CalculusActivity1.java
│   │   │   │   │   ├── Fractured_Teeth_Activity.java
│   │   │   │   │   └── Gingivitis_Activity.java
│   │   │   │   ├── Severity_Handling/
│   │   │   │   │   ├── CariesLowActivity.java
│   │   │   │   │   ├── CariesMediumActivity.java
│   │   │   │   │   └── [Specific_Severity_Activities...]
│   │   │   │   ├── Data_Models/
│   │   │   │   │   ├── User.java
│   │   │   │   │   └── Treatment.java
│   │   │   │   └── Core_Features/
│   │   │   │       ├── DashBoardActivity.java
│   │   │   │       ├── plan_fo_21_days.java
│   │   │   │       └── Profile.java
│   │   │   ├── res/
│   │   │   │   ├── layout/ (XML Designs)
│   │   │   │   │   ├── activity_caries_low.xml
│   │   │   │   │   ├── follow_plan_fractured_medium.xml
│   │   │   │   │   ├── popup_21_day_plan.xml
│   │   │   │   │   └── [50+ UI Layout files...]
│   │   │   │   ├── drawable/ (Visual Assets)
│   │   │   │   │   ├── grin_logo.png
│   │   │   │   │   └── dental_image.jpg
│   │   │   └── AndroidManifest.xml
│   ├── google-services.json (Firebase Link)
│   └── build.gradle.kts
└── README.md


## How to Install and Run the App (Using APK)
If you have been provided with the `app-debug.apk` file, follow these steps to install GrinGuard on your Android device:

1. Transfer the APK: 
   Move the `app-debug.apk` file to your Android phone (via USB, Google Drive, or messaging apps).
2. Enable Unknown Sources: 
   If this is your first time installing an app outside the Play Store, go to "Settings > Security" (or "Apps > Special app access") and enable "Install unknown apps" for your file manager or browser.
3. Install: 
   Open your file manager, locate the `app-debug.apk` file, and tap on it. Select "Install".
4. Permissions: 
   When you first open the app, please grant the "Camera" and "Storage" permissions so the AI can analyze your dental photos.
5. Internet Connection: 
   Ensure you are connected to the internet to use the "GrinGuard AI Chat" and "Firebase" features.




# DroFFS (Droid Files Fuzzing System)
DroFFS is a framework that implements a device-server architecture to perform files fuzzing in Android. All fuzzing tests run on an Android device or emulator while their processes are managed by a server or workstation. It can perform fuzz testing on most Android Apps that open and process files and it supports a wide array of file formats. DroFFS automatically installs Apps Under Testing (AUT) in Android devices or emulator from the given APKs during fuzzing process. It also directs corrupt but structurally valid files to Android device or emulator and instructs AUTs to process these files. On each fuzzing cycle, DroFFS extracts  crucial error entries and tombstones from Android logcat while its Automatic Log Checker (ALoC) module provides an automation to summarize extensive log entries resulted from the fuzzing tests.

DroFFS is implemented in Java language and tested to work on Java version 7 and Android 5.1.1 (Lollipop) - API Level 22

##System Requirements
To run DroFFS, the following software is required:
<ol>
<li>Java SE SDK 7</li>
<li>Java IDE (i.e. Eclipse, etc.)</li>
<li>Android SDK with ADB (Android Debug Bridge) and AAPT (Android Asset Packaging Tool) tools</li>
<li>Android Emulator or Rooted Android Device</li>
<li>Python 2.7 if running radamsa mutator</li>
</ol>

##Steps to run DroFFS
<ol>
<li>Download and Install Java SDK 7 and IDE</li>
<li>Download & Install Android SDK</li>
<li>Set path to ADB (Android Debug Bridge) tool located in Android SDK folder in your OS</li>
<li>Set path to AAPT (Android Asset Packaging Tool) located in Android SDK folder in your OS</li>
<li>Create an AVD (Android Virtual Device) emulator or attach a rooted Android Device
<ul>
<li>If a rooted Android device is used, make sure the USB Debugging in the “Developer Options” menu is turned on before connecting your device. Read here display “Developer Options” menu on your Android device: http://www.greenbot.com/article/2457986/how-to-enable-developer-options-on-your-android-phone-or-tablet.html</li>
<li>	If an Android emulator is used, select armeabi-v7a as its ABI/platform target (you may get error installing APK if using x86)</li>
</ul>
</li>
<li>Check for successful connection to an Android AVD/Device</li>
<li>Put all APKs of the AUT (Apps Under Test) in AppsUnderTest folder and files in SourceFiles folder </li>
<li>Open Main/global.java and set various configuration variables</li>
<li>Run the software to perform fuzzing! Voila!</li>
</ol>

##Published Papers & Presentations
to be advised

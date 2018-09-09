# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/khiemnt/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

##---------------End: proguard configuration for Gson  ----------
#actionbarsherlock
-keep class android.support.v4.app.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class com.actionbarsherlock.** { *; }
-keep interface com.actionbarsherlock.** { *; }
-keepattributes *Annotation*
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep class diet.patients.ui.fragment.DiseaseDetailFragment$* { *; }
-keep class diet.patients.ui.fragment.PrincipleDetailFragment$* { *; }
# Keep SafeParcelable value, needed for reflection. This is required to support backwards
# compatibility of some classes.
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}


# Keep the names of classes/members we need for client functionality.
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

# Needed for Parcelable/SafeParcelable Creators to not get stripped
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}


# Keep the names of classes/members we need for client functionality.
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-dontwarn com.google.common.collect.**
-dontnote com.google.common.collect.**
-dontwarn com.google.common.base.**
-dontnote com.google.common.base.**

# Retrofit 2.X
## https://square.github.io/retrofit/ ##
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

#Warnings to be removed. Otherwise maven plugin stops, but not dangerous
-dontwarn com.nxp.**
-dontwarn android.support.**
-dontwarn com.sun.xml.internal.**
-dontwarn com.sun.istack.internal.**
-dontwarn org.codehaus.jackson.**
-dontwarn org.springframework.**
-dontwarn java.awt.**
-dontwarn javax.security.**
-dontwarn java.beans.**
-dontwarn javax.xml.**
-dontwarn java.util.**
-dontwarn org.w3c.dom.**
-dontwarn com.google.common.**
-dontwarn javax.annotation.**
-dontwarn android.app.**
-dontwarn android.support.**
-dontwarn android.view.**
-dontwarn android.widget.**
-dontwarn com.google.common.primitives.**
-dontwarn com.squareup.okhttp.**
-dontwarn net.sqlcipher.**
-dontwarn oauth.signpost.**
-dontwarn org.apache.**
-dontwarn org.spongycastle.**
-dontwarn android.graphics.**
-dontwarn android.content.**
-dontwarn android.util.**



-dontwarn **CompatHoneycomb
-dontwarn **CompatHoneycombMR2
-dontwarn **CompatCreatorHoneycombMR2

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.app.Activity
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep class net.sqlcipher.** {
    *;
}

-keep class net.sqlcipher.database.** {
    *;
}

-keep class com.google.**{
	public protected private *;
}

-keep class com.squareup.**{*;}
-keep class org.apache.**{*;}
-keep class com.google.**{*;}
-keep class javax.** { *; }
-keep class org.** { *; }
-keep class org.json.simple.** {*;}
-keep class android.support.v4.** { *; }
-keep class org.spongycastle.** { *; }

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


# Add this global rule
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }

-keepclassmembers class com.wiseid.digitalidentity.** {
  *;
}
# Basic ProGuard rules for Firebase Android SDK 2.0.0+
-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }


-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

-dontwarn twitter4j.**
-keep  class twitter4j.conf.PropertyConfigurationFactory
-keep class twitter4j.** { *; }

#-dontwarn com.google.zxing.**
#-keep class com.google.zxing.**
#-dontwarn com.mixpanel.android.**
#-keep class com.mixpanel.android.**{*;}

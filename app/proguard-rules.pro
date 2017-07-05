-dontobfuscate

#Retrofit2
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature

# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
-keepattributes Annotation
-keep class okhttp3.* { *; }
-keep interface okhttp3.* { *; }
-dontwarn okhttp3.
-dontwarn okio.**


#AVLoadingIndicatorView
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }


#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

-keep class android.support.v7.widget.SearchView { *; }

#otto
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

#shape image
-dontwarn com.github.siyamed.**
-keep class com.github.siyamed.shapeimageview.**{ *; }

#-keep class com.google.** { *; }
#-keep class android.support.** { *; }
#-dontwarn com.google.**
#-dontwarn android.support.**

###########################
#
##
## This ProGuard configuration file illustrates how to process a program
## library, such that it remains usable as a library.
## Usage:
##     java -jar proguard.jar @library.pro
##
#
## Specify the input jars, output jars, and library jars.
## In this case, the input jar is the program library that we want to process.
#
#
#
#
## Save the obfuscation mapping to a file, so we can de-obfuscate any stack
## traces later on. Keep a fixed source file attribute and all line number
## tables to get line numbers in the stack traces.
## You can comment this out if you're not interested in stack traces.
#
#-keepparameternames
#-renamesourcefileattribute SourceFile
#-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod
#
## Preserve all annotations.
#
#-keepattributes *Annotation*
#
## Preserve all public classes, and their public and protected fields and
## methods.
#
#-keep public class * {
#    public protected *;
#}
#
## Preserve all .class method names.
#
#-keepclassmembernames class * {
#    java.lang.Class class$(java.lang.String);
#    java.lang.Class class$(java.lang.String, boolean);
#}
#
## Preserve all native method names and the names of their classes.
#
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
## Preserve the special static methods that are required in all enumeration
## classes.
#
#-keepclassmembers class * extends java.lang.Enum {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
## Explicitly preserve all serialization members. The Serializable interface
## is only a marker interface, so it wouldn't save them.
## You can comment this out if your library doesn't use serialization.
## If your code contains serializable classes that have to be backward
## compatible, please refer to the manual.
#
#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    static final java.io.ObjectStreamField[] serialPersistentFields;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}
#
## Your library may contain more items that need to be preserved;
## typically classes that are dynamically created using Class.forName:
#
## -keep public class mypackage.MyClass
## -keep public interface mypackage.MyInterface
## -keep public class * implements mypackage.MyInterface
#
#-keep class com.google.android.gms.** { *; }
#-dontwarn com.google.android.gms.**
#-dontwarn com.firebase.**
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class com.firebase.** { *; }
#-keep class org.apache.** { *; }
#-keepnames class com.fasterxml.jackson.** { *; }
#-keepnames class javax.servlet.** { *; }
#-keepnames class org.ietf.jgss.** { *; }
#-dontwarn org.w3c.dom.**
#-dontwarn org.joda.time.**
#-dontwarn org.shaded.apache.**
#-dontwarn org.ietf.jgss.**
#-keepnames class * implements android.os.Parcelable { *; }
#-keepclassmembers class * implements android.os.Parcelable { *; }
#-keepclassmembers class ** {
#    public void onEvent*(***);
# }
#
#-keep class com.google.android.exoplayer.** {*;}
#
#-keep class org.apache.http.** { *; }
#-dontwarn org.apache.http.**
#-dontwarn android.net.**
#-dontwarn org.apache.commons.**
#

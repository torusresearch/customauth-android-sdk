# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class org.torusresearch.customauth.app.* { *; }
-keep class org.torusresearch.customauth.** { *; }
-keepclassmembers class org.torusresearch.customauth.**

# Web3j rules  https://github.com/web3j/web3j/wiki/Android-ProGuard-rules-for-web3j-android
-dontwarn java8.util.**
-dontwarn jnr.posix.**
-dontwarn com.kenai.**

-keep class org.bouncycastle.**
-dontwarn org.bouncycastle.jce.provider.X509LDAPCertStoreSpi
-dontwarn org.bouncycastle.x509.util.LDAPStoreHelper

-keepclassmembers class org.web3j.protocol.** { *; }
-keepclassmembers class org.web3j.crypto.* { *; }

-keep class * extends org.web3j.abi.TypeReference
-keep class * extends org.web3j.abi.datatypes.Type

-dontwarn java.lang.SafeVarargs
-dontwarn org.slf4j.**
#########################################################
-dontwarn com.fasterxml.jackson.databind.**
-keep class org.** {*;}
-keep class com.fasterxml.jackson.core.** {*;}
#-keep class * implements com.fasterxml.jackson.core {*;}
-keep public class * extends com.fasterxml.jackson.core.*
-keep class com.fasterxml.jackson.databind.introspect.VisibilityChecker$Std.*
-keep class com.fasterxml.jackson.databind.ObjectMapper.*
-keep class com.fasterxml.jackson.databind.** {*;}
-keep public class * extends com.fasterxml.jackson.databind.*
-keep class com.fasterxml.jackson.annotation.** {*;}
-keep interface com.fasterxml.jackson.annotation.** {*;}
#########################################################

##### okhttp3
# okHttp3
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
# I was able to get rid of this issue adding this Proguard line to my configuration :
-keep class okhttp3.Headers { *; }

#### GSON
# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer



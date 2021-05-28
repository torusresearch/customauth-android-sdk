package org.torusresearch.torusdirect.utils;

import org.torusresearch.torusdirect.types.JwtUserInfoResult;
import org.torusresearch.torusdirect.types.LoginType;
import org.torusresearch.torusdirect.types.NoAllowedBrowserFoundException;
import org.torusresearch.torusdirect.types.UserCancelledException;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import java8.util.concurrent.CompletableFuture;
import java8.util.concurrent.CompletionException;
import java8.util.concurrent.ForkJoinPool;

public class Helpers {
    public static HashMap<LoginType, String> loginToConnectionMap = new HashMap<LoginType, String>() {
        {
            put(LoginType.APPLE, "apple");
            put(LoginType.GITHUB, "github");
            put(LoginType.LINKEDIN, "linkedin");
            put(LoginType.TWITTER, "twitter");
            put(LoginType.WEIBO, "weibo");
            put(LoginType.LINE, "line");
            put(LoginType.EMAIL_PASSWORD, "Username-Password-Authentication");
        }
    };

    public static <T> T mergeValue(T a, T b) {
        if (isValid(b)) return b;
        return a;
    }

    public static <T> boolean isValid(T a) {
        // I check for null first because null.toString() throws
        return a != null && !isEmpty(a.toString());
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public static String capitalize(final String str) {
        final int strLen = length(str);
        if (strLen == 0) {
            return str;
        }

        final int firstCodePoint = str.codePointAt(0);
        final int newCodePoint = Character.toTitleCase(firstCodePoint);
        if (firstCodePoint == newCodePoint) {
            // already capitalized
            return str;
        }

        final int[] newCodePoints = new int[strLen]; // cannot be longer than the char array
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint; // copy the first codepoint
        for (int inOffset = Character.charCount(firstCodePoint); inOffset < strLen; ) {
            final int codePoint = str.codePointAt(inOffset);
            newCodePoints[outOffset++] = codePoint; // copy the remaining ones
            inOffset += Character.charCount(codePoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static String caseSensitiveField(String field, boolean isCaseSensitive) {
        return isCaseSensitive ? field : field.toLowerCase();
    }

    public static String getVerifierId(JwtUserInfoResult userInfo, LoginType typeOfLogin, String verifierIdField, boolean isVerifierIdCaseSensitive) {
        String name = userInfo.getName();
        String sub = userInfo.getSub();
        if (!isEmpty(verifierIdField)) {
            try {
                return caseSensitiveField(Objects.requireNonNull(userInfo.getClass().getMethod("get" + capitalize(verifierIdField)).invoke(userInfo)).toString(), isVerifierIdCaseSensitive);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        switch (typeOfLogin) {
            case EMAIL_PASSWORD:
                return caseSensitiveField(name, isVerifierIdCaseSensitive);
            case APPLE:
            case GITHUB:
            case LINKEDIN:
            case TWITTER:
            case WEIBO:
            case LINE:
            case JWT:
                return caseSensitiveField(sub, isVerifierIdCaseSensitive);
            default:
                throw new InvalidParameterException("Invalid typeOfLogin");
        }
    }

    public static <T> CompletableFuture<List<T>> allOfSequentially(List<CompletableFuture<T>> cfs) {
        CompletableFuture<List<T>> returnCf = new CompletableFuture<>();
        List<T> returnList = new ArrayList<>();
        ForkJoinPool.commonPool().execute(() -> {
            for (CompletableFuture<T> cf :
                    cfs) {
                try {
                    // Cannot wait on main thread
                    T resp = cf.join();
                    returnList.add(resp);
                } catch (Exception e) {
                    e.printStackTrace();
                    returnCf.completeExceptionally(e);
                }
            }
            returnCf.complete(returnList);
        });

        return returnCf;
    }

    public static Throwable unwrapCompletionException(Throwable error) {
        Throwable e = error;
        while (e instanceof CompletionException) e = e.getCause();
        return e;
    }

    public static boolean isUserCancelledException(Throwable error) {
        return (unwrapCompletionException(error) instanceof UserCancelledException);
    }

    public static boolean isNoAllowedBrowserFoundException(Throwable error) {
        return (unwrapCompletionException(error) instanceof NoAllowedBrowserFoundException);
    }
}

package org.telegram.messenger;

import com.google.mlkit.common.sdkinternal.MlKitContext;
import com.google.mlkit.nl.languageid.LanguageIdentification;

public class LanguageDetector {

    public interface ExceptionCallback {
        void run(Exception exc);
    }

    public interface StringCallback {
        void run(String str);
    }

    public static boolean hasSupport() {
        return true;
    }

    public static void detectLanguage(String text, StringCallback onSuccess, ExceptionCallback onFail) {
        detectLanguage(text, onSuccess, onFail, false);
    }

    public static void detectLanguage(String text, StringCallback onSuccess, ExceptionCallback onFail, boolean initializeFirst) {
        if (initializeFirst) {
            try {
                MlKitContext.zza(ApplicationLoader.applicationContext);
            } catch (IllegalStateException e) {
                if (!initializeFirst) {
                    detectLanguage(text, onSuccess, onFail, true);
                    return;
                } else if (onFail != null) {
                    onFail.run(e);
                    return;
                } else {
                    return;
                }
            } catch (Exception e2) {
                if (onFail != null) {
                    onFail.run(e2);
                    return;
                }
                return;
            } catch (Throwable th) {
                if (onFail != null) {
                    onFail.run((Exception) null);
                    return;
                }
                return;
            }
        }
        LanguageIdentification.getClient().identifyLanguage(text).addOnSuccessListener(new LanguageDetector$$ExternalSyntheticLambda1(onSuccess)).addOnFailureListener(new LanguageDetector$$ExternalSyntheticLambda0(onFail));
    }

    static /* synthetic */ void lambda$detectLanguage$0(StringCallback onSuccess, String str) {
        if (onSuccess != null) {
            onSuccess.run(str);
        }
    }

    static /* synthetic */ void lambda$detectLanguage$1(ExceptionCallback onFail, Exception e) {
        if (onFail != null) {
            onFail.run(e);
        }
    }
}

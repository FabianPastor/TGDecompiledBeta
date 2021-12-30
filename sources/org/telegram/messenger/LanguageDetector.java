package org.telegram.messenger;

import com.google.android.gms.tasks.Task;
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

    public static void detectLanguage(String str, StringCallback stringCallback, ExceptionCallback exceptionCallback) {
        Task<String> identifyLanguage = LanguageIdentification.getClient().identifyLanguage(str);
        stringCallback.getClass();
        Task<String> addOnSuccessListener = identifyLanguage.addOnSuccessListener(new LanguageDetector$$ExternalSyntheticLambda1(stringCallback));
        exceptionCallback.getClass();
        addOnSuccessListener.addOnFailureListener(new LanguageDetector$$ExternalSyntheticLambda0(exceptionCallback));
    }
}

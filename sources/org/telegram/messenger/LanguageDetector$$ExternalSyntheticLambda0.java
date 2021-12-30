package org.telegram.messenger;

import com.google.android.gms.tasks.OnFailureListener;
import org.telegram.messenger.LanguageDetector;

public final /* synthetic */ class LanguageDetector$$ExternalSyntheticLambda0 implements OnFailureListener {
    public final /* synthetic */ LanguageDetector.ExceptionCallback f$0;

    public /* synthetic */ LanguageDetector$$ExternalSyntheticLambda0(LanguageDetector.ExceptionCallback exceptionCallback) {
        this.f$0 = exceptionCallback;
    }

    public final void onFailure(Exception exc) {
        this.f$0.run(exc);
    }
}

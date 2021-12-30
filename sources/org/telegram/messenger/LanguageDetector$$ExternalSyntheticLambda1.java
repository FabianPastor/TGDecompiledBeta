package org.telegram.messenger;

import com.google.android.gms.tasks.OnSuccessListener;
import org.telegram.messenger.LanguageDetector;

public final /* synthetic */ class LanguageDetector$$ExternalSyntheticLambda1 implements OnSuccessListener {
    public final /* synthetic */ LanguageDetector.StringCallback f$0;

    public /* synthetic */ LanguageDetector$$ExternalSyntheticLambda1(LanguageDetector.StringCallback stringCallback) {
        this.f$0 = stringCallback;
    }

    public final void onSuccess(Object obj) {
        this.f$0.run((String) obj);
    }
}

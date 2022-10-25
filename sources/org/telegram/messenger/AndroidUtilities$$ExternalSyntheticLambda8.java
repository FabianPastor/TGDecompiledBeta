package org.telegram.messenger;

import com.google.android.gms.tasks.OnSuccessListener;
/* loaded from: classes.dex */
public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda8 implements OnSuccessListener {
    public static final /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda8 INSTANCE = new AndroidUtilities$$ExternalSyntheticLambda8();

    private /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda8() {
    }

    @Override // com.google.android.gms.tasks.OnSuccessListener
    public final void onSuccess(Object obj) {
        AndroidUtilities.lambda$setWaitingForSms$6((Void) obj);
    }
}

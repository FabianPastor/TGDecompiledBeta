package org.telegram.messenger;

import com.google.android.gms.tasks.OnSuccessListener;
/* loaded from: classes.dex */
public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda7 implements OnSuccessListener {
    public static final /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda7 INSTANCE = new AndroidUtilities$$ExternalSyntheticLambda7();

    private /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda7() {
    }

    @Override // com.google.android.gms.tasks.OnSuccessListener
    public final void onSuccess(Object obj) {
        AndroidUtilities.lambda$setWaitingForSms$6((Void) obj);
    }
}

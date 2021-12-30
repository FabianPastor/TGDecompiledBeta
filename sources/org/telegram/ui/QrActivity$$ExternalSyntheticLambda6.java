package org.telegram.ui;

import android.util.Pair;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class QrActivity$$ExternalSyntheticLambda6 implements ResultCallback {
    public final /* synthetic */ QrActivity f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ QrActivity$$ExternalSyntheticLambda6(QrActivity qrActivity, boolean z, boolean z2) {
        this.f$0 = qrActivity;
        this.f$1 = z;
        this.f$2 = z2;
    }

    public final void onComplete(Object obj) {
        this.f$0.lambda$onItemSelected$5(this.f$1, this.f$2, (Pair) obj);
    }

    public /* synthetic */ void onError(TLRPC$TL_error tLRPC$TL_error) {
        ResultCallback.CC.$default$onError((ResultCallback) this, tLRPC$TL_error);
    }
}

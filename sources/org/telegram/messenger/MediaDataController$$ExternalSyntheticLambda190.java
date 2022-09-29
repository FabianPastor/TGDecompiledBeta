package org.telegram.messenger;

import android.content.SharedPreferences;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda190 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ SharedPreferences f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda190(MediaDataController mediaDataController, SharedPreferences sharedPreferences) {
        this.f$0 = mediaDataController;
        this.f$1 = sharedPreferences;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadRecentAndTopReactions$207(this.f$1, tLObject, tLRPC$TL_error);
    }
}

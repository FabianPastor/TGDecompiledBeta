package org.telegram.messenger.voip;

import android.content.SharedPreferences;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda72 implements RequestDelegate {
    public final /* synthetic */ SharedPreferences f$0;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda72(SharedPreferences sharedPreferences) {
        this.f$0 = sharedPreferences;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        VoIPService.lambda$updateServerConfig$72(this.f$0, tLObject, tL_error);
    }
}

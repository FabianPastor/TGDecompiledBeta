package org.telegram.messenger.voip;

import android.content.SharedPreferences;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda77 implements RequestDelegate {
    public final /* synthetic */ SharedPreferences f$0;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda77(SharedPreferences sharedPreferences) {
        this.f$0 = sharedPreferences;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$updateServerConfig$74(this.f$0, tLObject, tLRPC$TL_error);
    }
}

package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatEditTypeActivity$$ExternalSyntheticLambda13 implements RequestDelegate {
    public final /* synthetic */ ChatEditTypeActivity f$0;

    public /* synthetic */ ChatEditTypeActivity$$ExternalSyntheticLambda13(ChatEditTypeActivity chatEditTypeActivity) {
        this.f$0 = chatEditTypeActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadAdminedChannels$11(tLObject, tLRPC$TL_error);
    }
}
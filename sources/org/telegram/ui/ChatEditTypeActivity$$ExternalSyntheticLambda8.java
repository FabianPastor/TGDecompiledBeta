package org.telegram.ui;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class ChatEditTypeActivity$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ ChatEditTypeActivity f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ ChatEditTypeActivity$$ExternalSyntheticLambda8(ChatEditTypeActivity chatEditTypeActivity, TLObject tLObject) {
        this.f$0 = chatEditTypeActivity;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadAdminedChannels$10(this.f$1);
    }
}
package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.Components.ChatActivityEnterView;

public final /* synthetic */ class ChatActivityEnterView$57$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ChatActivityEnterView.AnonymousClass57 f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC$Document f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ ChatActivityEnterView$57$$ExternalSyntheticLambda1(ChatActivityEnterView.AnonymousClass57 r1, String str, TLRPC$Document tLRPC$Document, long j) {
        this.f$0 = r1;
        this.f$1 = str;
        this.f$2 = tLRPC$Document;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$onCustomEmojiSelected$0(this.f$1, this.f$2, this.f$3);
    }
}

package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessageSeenView$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ MessageSeenView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ TLRPC.Chat f$5;

    public /* synthetic */ MessageSeenView$$ExternalSyntheticLambda2(MessageSeenView messageSeenView, TLRPC.TL_error tL_error, TLObject tLObject, long j, int i, TLRPC.Chat chat) {
        this.f$0 = messageSeenView;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = j;
        this.f$4 = i;
        this.f$5 = chat;
    }

    public final void run() {
        this.f$0.m3298lambda$new$4$orgtelegramuiMessageSeenView(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

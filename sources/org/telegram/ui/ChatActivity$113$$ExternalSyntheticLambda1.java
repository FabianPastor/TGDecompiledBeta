package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ReactionsContainerLayout;

public final /* synthetic */ class ChatActivity$113$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ChatActivity.AnonymousClass113 f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ ReactionsContainerLayout f$3;
    public final /* synthetic */ float f$4;
    public final /* synthetic */ float f$5;
    public final /* synthetic */ TLRPC$TL_availableReaction f$6;

    public /* synthetic */ ChatActivity$113$$ExternalSyntheticLambda1(ChatActivity.AnonymousClass113 r1, int i, boolean z, ReactionsContainerLayout reactionsContainerLayout, float f, float f2, TLRPC$TL_availableReaction tLRPC$TL_availableReaction) {
        this.f$0 = r1;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = reactionsContainerLayout;
        this.f$4 = f;
        this.f$5 = f2;
        this.f$6 = tLRPC$TL_availableReaction;
    }

    public final void run() {
        this.f$0.lambda$run$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

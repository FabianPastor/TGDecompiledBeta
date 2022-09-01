package org.telegram.ui;

import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class ChatActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ ChatActivity.ChatActivityAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC$InputStickerSet f$1;

    public /* synthetic */ ChatActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda2(ChatActivity.ChatActivityAdapter.AnonymousClass1 r1, TLRPC$InputStickerSet tLRPC$InputStickerSet) {
        this.f$0 = r1;
        this.f$1 = tLRPC$InputStickerSet;
    }

    public final void run() {
        this.f$0.lambda$didPressAnimatedEmoji$5(this.f$1);
    }
}

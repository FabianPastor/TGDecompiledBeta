package org.telegram.ui.Components;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.ChatMessageCell;

public final /* synthetic */ class SlotsDrawable$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ TLRPC.Document f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ ChatMessageCell f$3;
    public final /* synthetic */ TLRPC.TL_messages_stickerSet f$4;

    public /* synthetic */ SlotsDrawable$$ExternalSyntheticLambda2(TLRPC.Document document, int i, MessageObject messageObject, ChatMessageCell chatMessageCell, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        this.f$0 = document;
        this.f$1 = i;
        this.f$2 = messageObject;
        this.f$3 = chatMessageCell;
        this.f$4 = tL_messages_stickerSet;
    }

    public final void run() {
        SlotsDrawable.lambda$setDiceNumber$7(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

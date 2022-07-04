package org.telegram.ui.Components;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.ChatMessageCell;

public final /* synthetic */ class SlotsDrawable$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ SlotsDrawable f$0;
    public final /* synthetic */ TLRPC.TL_messages_stickerSet f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ MessageObject f$3;
    public final /* synthetic */ ChatMessageCell f$4;

    public /* synthetic */ SlotsDrawable$$ExternalSyntheticLambda9(SlotsDrawable slotsDrawable, TLRPC.TL_messages_stickerSet tL_messages_stickerSet, int i, MessageObject messageObject, ChatMessageCell chatMessageCell) {
        this.f$0 = slotsDrawable;
        this.f$1 = tL_messages_stickerSet;
        this.f$2 = i;
        this.f$3 = messageObject;
        this.f$4 = chatMessageCell;
    }

    public final void run() {
        this.f$0.m1419lambda$setBaseDice$5$orgtelegramuiComponentsSlotsDrawable(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

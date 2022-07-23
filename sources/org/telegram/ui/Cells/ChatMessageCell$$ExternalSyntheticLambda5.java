package org.telegram.ui.Cells;

import android.graphics.drawable.Drawable;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class ChatMessageCell$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ ChatMessageCell f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ TLRPC$Document f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ Drawable f$5;

    public /* synthetic */ ChatMessageCell$$ExternalSyntheticLambda5(ChatMessageCell chatMessageCell, MessageObject messageObject, TLRPC$Document tLRPC$Document, long j, String str, Drawable drawable) {
        this.f$0 = chatMessageCell;
        this.f$1 = messageObject;
        this.f$2 = tLRPC$Document;
        this.f$3 = j;
        this.f$4 = str;
        this.f$5 = drawable;
    }

    public final void run() {
        this.f$0.lambda$setMessageContent$6(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

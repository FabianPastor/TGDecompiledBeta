package org.telegram.ui.Cells;

import android.graphics.drawable.Drawable;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.Components.AnimatedEmojiDrawable;

public final /* synthetic */ class ChatMessageCell$$ExternalSyntheticLambda8 implements AnimatedEmojiDrawable.ReceivedDocument {
    public final /* synthetic */ ChatMessageCell f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ Drawable f$4;

    public /* synthetic */ ChatMessageCell$$ExternalSyntheticLambda8(ChatMessageCell chatMessageCell, MessageObject messageObject, long j, String str, Drawable drawable) {
        this.f$0 = chatMessageCell;
        this.f$1 = messageObject;
        this.f$2 = j;
        this.f$3 = str;
        this.f$4 = drawable;
    }

    public final void run(TLRPC$Document tLRPC$Document) {
        this.f$0.lambda$setMessageContent$7(this.f$1, this.f$2, this.f$3, this.f$4, tLRPC$Document);
    }
}

package org.telegram.ui;

import android.view.View;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.Components.AnimatedEmojiSpan;

public final /* synthetic */ class SelectAnimatedEmojiDialog$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ SelectAnimatedEmojiDialog f$0;
    public final /* synthetic */ View f$1;
    public final /* synthetic */ AnimatedEmojiSpan f$2;
    public final /* synthetic */ TLRPC$Document f$3;

    public /* synthetic */ SelectAnimatedEmojiDialog$$ExternalSyntheticLambda10(SelectAnimatedEmojiDialog selectAnimatedEmojiDialog, View view, AnimatedEmojiSpan animatedEmojiSpan, TLRPC$Document tLRPC$Document) {
        this.f$0 = selectAnimatedEmojiDialog;
        this.f$1 = view;
        this.f$2 = animatedEmojiSpan;
        this.f$3 = tLRPC$Document;
    }

    public final void run() {
        this.f$0.lambda$onEmojiClick$7(this.f$1, this.f$2, this.f$3);
    }
}

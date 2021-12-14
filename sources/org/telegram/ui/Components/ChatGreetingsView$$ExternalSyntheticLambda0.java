package org.telegram.ui.Components;

import android.view.View;
import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class ChatGreetingsView$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ ChatGreetingsView f$0;
    public final /* synthetic */ TLRPC$Document f$1;

    public /* synthetic */ ChatGreetingsView$$ExternalSyntheticLambda0(ChatGreetingsView chatGreetingsView, TLRPC$Document tLRPC$Document) {
        this.f$0 = chatGreetingsView;
        this.f$1 = tLRPC$Document;
    }

    public final void onClick(View view) {
        this.f$0.lambda$setSticker$0(this.f$1, view);
    }
}

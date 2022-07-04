package org.telegram.ui;

import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.EmojiThemes;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda233 implements ResultCallback {
    public final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda233(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void onComplete(Object obj) {
        this.f$0.lambda$setChatThemeEmoticon$250((EmojiThemes) obj);
    }

    public /* synthetic */ void onError(TLRPC$TL_error tLRPC$TL_error) {
        ResultCallback.CC.$default$onError((ResultCallback) this, tLRPC$TL_error);
    }
}

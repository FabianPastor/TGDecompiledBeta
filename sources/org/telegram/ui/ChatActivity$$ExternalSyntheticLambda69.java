package org.telegram.ui;

import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.EmojiThemes;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda69 implements ResultCallback {
    public final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda69(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void onComplete(Object obj) {
        this.f$0.m1794lambda$setChatThemeEmoticon$175$orgtelegramuiChatActivity((EmojiThemes) obj);
    }

    public /* synthetic */ void onError(Throwable th) {
        ResultCallback.CC.$default$onError((ResultCallback) this, th);
    }

    public /* synthetic */ void onError(TLRPC.TL_error tL_error) {
        ResultCallback.CC.$default$onError((ResultCallback) this, tL_error);
    }
}

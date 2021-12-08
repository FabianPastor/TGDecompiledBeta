package org.telegram.ui;

import android.util.Pair;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.MotionBackgroundDrawable;

public final /* synthetic */ class ChatActivity$ThemeDelegate$$ExternalSyntheticLambda6 implements ResultCallback {
    public final /* synthetic */ ChatActivity.ThemeDelegate f$0;
    public final /* synthetic */ EmojiThemes f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ MotionBackgroundDrawable f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ ChatActivity$ThemeDelegate$$ExternalSyntheticLambda6(ChatActivity.ThemeDelegate themeDelegate, EmojiThemes emojiThemes, boolean z, MotionBackgroundDrawable motionBackgroundDrawable, int i) {
        this.f$0 = themeDelegate;
        this.f$1 = emojiThemes;
        this.f$2 = z;
        this.f$3 = motionBackgroundDrawable;
        this.f$4 = i;
    }

    public final void onComplete(Object obj) {
        this.f$0.m1859x5563d1bf(this.f$1, this.f$2, this.f$3, this.f$4, (Pair) obj);
    }

    public /* synthetic */ void onError(Throwable th) {
        ResultCallback.CC.$default$onError((ResultCallback) this, th);
    }

    public /* synthetic */ void onError(TLRPC.TL_error tL_error) {
        ResultCallback.CC.$default$onError((ResultCallback) this, tL_error);
    }
}

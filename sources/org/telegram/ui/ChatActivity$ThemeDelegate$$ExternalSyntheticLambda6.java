package org.telegram.ui;

import android.util.Pair;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.ChatTheme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.MotionBackgroundDrawable;

public final /* synthetic */ class ChatActivity$ThemeDelegate$$ExternalSyntheticLambda6 implements ResultCallback {
    public final /* synthetic */ ChatActivity.ThemeDelegate f$0;
    public final /* synthetic */ ChatTheme f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ MotionBackgroundDrawable f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ ChatActivity$ThemeDelegate$$ExternalSyntheticLambda6(ChatActivity.ThemeDelegate themeDelegate, ChatTheme chatTheme, boolean z, MotionBackgroundDrawable motionBackgroundDrawable, int i) {
        this.f$0 = themeDelegate;
        this.f$1 = chatTheme;
        this.f$2 = z;
        this.f$3 = motionBackgroundDrawable;
        this.f$4 = i;
    }

    public final void onComplete(Object obj) {
        this.f$0.lambda$getBackgroundDrawableFromTheme$6(this.f$1, this.f$2, this.f$3, this.f$4, (Pair) obj);
    }

    public /* synthetic */ void onError(TLRPC$TL_error tLRPC$TL_error) {
        ResultCallback.CC.$default$onError((ResultCallback) this, tLRPC$TL_error);
    }
}
package org.telegram.ui.Components;

import android.util.Pair;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.ChatThemeBottomSheet;

public final /* synthetic */ class ChatThemeBottomSheet$Adapter$ChatThemeView$$ExternalSyntheticLambda1 implements ResultCallback {
    public final /* synthetic */ ChatThemeBottomSheet.Adapter.ChatThemeView f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ ChatThemeBottomSheet$Adapter$ChatThemeView$$ExternalSyntheticLambda1(ChatThemeBottomSheet.Adapter.ChatThemeView chatThemeView, long j, int i) {
        this.f$0 = chatThemeView;
        this.f$1 = j;
        this.f$2 = i;
    }

    public final void onComplete(Object obj) {
        this.f$0.lambda$setItem$0(this.f$1, this.f$2, (Pair) obj);
    }

    public /* synthetic */ void onError(TLRPC$TL_error tLRPC$TL_error) {
        ResultCallback.CC.$default$onError((ResultCallback) this, tLRPC$TL_error);
    }
}

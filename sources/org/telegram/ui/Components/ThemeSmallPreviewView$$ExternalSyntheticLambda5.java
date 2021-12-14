package org.telegram.ui.Components;

import android.util.Pair;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.ChatThemeBottomSheet;

public final /* synthetic */ class ThemeSmallPreviewView$$ExternalSyntheticLambda5 implements ResultCallback {
    public final /* synthetic */ ThemeSmallPreviewView f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ ChatThemeBottomSheet.ChatThemeItem f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ ThemeSmallPreviewView$$ExternalSyntheticLambda5(ThemeSmallPreviewView themeSmallPreviewView, long j, ChatThemeBottomSheet.ChatThemeItem chatThemeItem, int i) {
        this.f$0 = themeSmallPreviewView;
        this.f$1 = j;
        this.f$2 = chatThemeItem;
        this.f$3 = i;
    }

    public final void onComplete(Object obj) {
        this.f$0.lambda$setItem$0(this.f$1, this.f$2, this.f$3, (Pair) obj);
    }

    public /* synthetic */ void onError(TLRPC$TL_error tLRPC$TL_error) {
        ResultCallback.CC.$default$onError((ResultCallback) this, tLRPC$TL_error);
    }
}

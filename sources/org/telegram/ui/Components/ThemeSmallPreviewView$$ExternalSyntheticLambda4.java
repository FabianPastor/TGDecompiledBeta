package org.telegram.ui.Components;

import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.ui.Components.ChatThemeBottomSheet;

public final /* synthetic */ class ThemeSmallPreviewView$$ExternalSyntheticLambda4 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ ThemeSmallPreviewView f$0;
    public final /* synthetic */ ChatThemeBottomSheet.ChatThemeItem f$1;
    public final /* synthetic */ TLRPC$WallPaper f$2;

    public /* synthetic */ ThemeSmallPreviewView$$ExternalSyntheticLambda4(ThemeSmallPreviewView themeSmallPreviewView, ChatThemeBottomSheet.ChatThemeItem chatThemeItem, TLRPC$WallPaper tLRPC$WallPaper) {
        this.f$0 = themeSmallPreviewView;
        this.f$1 = chatThemeItem;
        this.f$2 = tLRPC$WallPaper;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        this.f$0.lambda$setItem$1(this.f$1, this.f$2, imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

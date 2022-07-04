package org.telegram.ui.Components;

import android.graphics.Bitmap;
import org.telegram.ui.Components.ChatThemeBottomSheet;

public final /* synthetic */ class ThemeSmallPreviewView$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ThemeSmallPreviewView f$0;
    public final /* synthetic */ ChatThemeBottomSheet.ChatThemeItem f$1;
    public final /* synthetic */ Bitmap f$2;

    public /* synthetic */ ThemeSmallPreviewView$$ExternalSyntheticLambda3(ThemeSmallPreviewView themeSmallPreviewView, ChatThemeBottomSheet.ChatThemeItem chatThemeItem, Bitmap bitmap) {
        this.f$0 = themeSmallPreviewView;
        this.f$1 = chatThemeItem;
        this.f$2 = bitmap;
    }

    public final void run() {
        this.f$0.lambda$setItem$2(this.f$1, this.f$2);
    }
}

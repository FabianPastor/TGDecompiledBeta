package org.telegram.ui.Components;

import android.view.MotionEvent;
import android.view.View;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class StickerMasksAlert$$ExternalSyntheticLambda2 implements View.OnTouchListener {
    public final /* synthetic */ StickerMasksAlert f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;

    public /* synthetic */ StickerMasksAlert$$ExternalSyntheticLambda2(StickerMasksAlert stickerMasksAlert, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = stickerMasksAlert;
        this.f$1 = resourcesProvider;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.m1425lambda$new$0$orgtelegramuiComponentsStickerMasksAlert(this.f$1, view, motionEvent);
    }
}

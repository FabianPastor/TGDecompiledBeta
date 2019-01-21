package org.telegram.ui.Components;

import android.view.KeyEvent;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener;

final /* synthetic */ class PhotoPaintView$$Lambda$14 implements OnDispatchKeyEventListener {
    private final PhotoPaintView arg$1;

    PhotoPaintView$$Lambda$14(PhotoPaintView photoPaintView) {
        this.arg$1 = photoPaintView;
    }

    public void onDispatchKeyEvent(KeyEvent keyEvent) {
        this.arg$1.lambda$showPopup$17$PhotoPaintView(keyEvent);
    }
}

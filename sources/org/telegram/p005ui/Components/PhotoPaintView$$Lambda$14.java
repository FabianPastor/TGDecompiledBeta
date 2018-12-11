package org.telegram.p005ui.Components;

import android.view.KeyEvent;
import org.telegram.p005ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener;

/* renamed from: org.telegram.ui.Components.PhotoPaintView$$Lambda$14 */
final /* synthetic */ class PhotoPaintView$$Lambda$14 implements OnDispatchKeyEventListener {
    private final PhotoPaintView arg$1;

    PhotoPaintView$$Lambda$14(PhotoPaintView photoPaintView) {
        this.arg$1 = photoPaintView;
    }

    public void onDispatchKeyEvent(KeyEvent keyEvent) {
        this.arg$1.lambda$showPopup$17$PhotoPaintView(keyEvent);
    }
}

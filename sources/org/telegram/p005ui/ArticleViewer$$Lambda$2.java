package org.telegram.p005ui;

import android.view.KeyEvent;
import org.telegram.p005ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$2 */
final /* synthetic */ class ArticleViewer$$Lambda$2 implements OnDispatchKeyEventListener {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$2(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public void onDispatchKeyEvent(KeyEvent keyEvent) {
        this.arg$1.lambda$showPopup$2$ArticleViewer(keyEvent);
    }
}

package org.telegram.ui;

import android.view.KeyEvent;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener;

final /* synthetic */ class ArticleViewer$$Lambda$2 implements OnDispatchKeyEventListener {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$2(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public void onDispatchKeyEvent(KeyEvent keyEvent) {
        this.arg$1.lambda$showPopup$2$ArticleViewer(keyEvent);
    }
}

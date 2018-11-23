package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$27 */
final /* synthetic */ class ArticleViewer$$Lambda$27 implements OnDismissListener {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$27(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$showDialog$35$ArticleViewer(dialogInterface);
    }
}

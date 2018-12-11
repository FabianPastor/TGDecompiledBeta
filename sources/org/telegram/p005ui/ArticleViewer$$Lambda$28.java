package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$28 */
final /* synthetic */ class ArticleViewer$$Lambda$28 implements OnDismissListener {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$28(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$showDialog$36$ArticleViewer(dialogInterface);
    }
}

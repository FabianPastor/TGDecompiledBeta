package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

final /* synthetic */ class ArticleViewer$$Lambda$28 implements OnDismissListener {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$28(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$showDialog$36$ArticleViewer(dialogInterface);
    }
}

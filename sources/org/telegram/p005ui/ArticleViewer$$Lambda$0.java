package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$0 */
final /* synthetic */ class ArticleViewer$$Lambda$0 implements OnClickListener {
    private final ArticleViewer arg$1;
    private final String arg$2;

    ArticleViewer$$Lambda$0(ArticleViewer articleViewer, String str) {
        this.arg$1 = articleViewer;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showCopyPopup$0$ArticleViewer(this.arg$2, dialogInterface, i);
    }
}

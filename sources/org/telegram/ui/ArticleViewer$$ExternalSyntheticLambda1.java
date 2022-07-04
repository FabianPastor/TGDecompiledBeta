package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda1(ArticleViewer articleViewer, String str) {
        this.f$0 = articleViewer;
        this.f$1 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showCopyPopup$0(this.f$1, dialogInterface, i);
    }
}

package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.PhotoViewer.AnonymousClass7;

final /* synthetic */ class PhotoViewer$7$$Lambda$1 implements OnClickListener {
    private final boolean[] arg$1;

    PhotoViewer$7$$Lambda$1(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        AnonymousClass7.lambda$onItemClick$1$PhotoViewer$7(this.arg$1, view);
    }
}

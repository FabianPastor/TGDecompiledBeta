package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.PhotoViewer.C15396;

/* renamed from: org.telegram.ui.PhotoViewer$6$$Lambda$1 */
final /* synthetic */ class PhotoViewer$6$$Lambda$1 implements OnClickListener {
    private final boolean[] arg$1;

    PhotoViewer$6$$Lambda$1(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        C15396.lambda$onItemClick$1$PhotoViewer$6(this.arg$1, view);
    }
}

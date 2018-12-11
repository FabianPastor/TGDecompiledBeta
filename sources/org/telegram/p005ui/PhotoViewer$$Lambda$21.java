package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.PhotoViewer$$Lambda$21 */
final /* synthetic */ class PhotoViewer$$Lambda$21 implements OnItemClickListener {
    private final PhotoViewer arg$1;

    PhotoViewer$$Lambda$21(PhotoViewer photoViewer) {
        this.arg$1 = photoViewer;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$setParentActivity$26$PhotoViewer(view, i);
    }
}

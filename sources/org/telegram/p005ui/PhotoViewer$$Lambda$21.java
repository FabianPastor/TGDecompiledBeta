package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListener;

/* renamed from: org.telegram.ui.PhotoViewer$$Lambda$21 */
final /* synthetic */ class PhotoViewer$$Lambda$21 implements OnItemLongClickListener {
    private final PhotoViewer arg$1;

    PhotoViewer$$Lambda$21(PhotoViewer photoViewer) {
        this.arg$1 = photoViewer;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$setParentActivity$27$PhotoViewer(view, i);
    }
}

package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class PhotoViewer$$Lambda$22 implements OnItemLongClickListener {
    private final PhotoViewer arg$1;

    PhotoViewer$$Lambda$22(PhotoViewer photoViewer) {
        this.arg$1 = photoViewer;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$setParentActivity$28$PhotoViewer(view, i);
    }
}

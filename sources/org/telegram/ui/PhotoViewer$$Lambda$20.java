package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class PhotoViewer$$Lambda$20 implements OnItemClickListener {
    private final PhotoViewer arg$1;

    PhotoViewer$$Lambda$20(PhotoViewer photoViewer) {
        this.arg$1 = photoViewer;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$setParentActivity$25$PhotoViewer(view, i);
    }
}

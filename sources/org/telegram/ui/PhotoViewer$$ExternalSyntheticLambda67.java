package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda67 implements RecyclerListView.OnItemLongClickListener {
    public final /* synthetic */ PhotoViewer f$0;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda67(PhotoViewer photoViewer) {
        this.f$0 = photoViewer;
    }

    public final boolean onItemClick(View view, int i) {
        return this.f$0.lambda$setParentActivity$38(view, i);
    }
}

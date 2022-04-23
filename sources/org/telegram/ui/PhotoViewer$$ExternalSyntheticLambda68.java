package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda68 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ PhotoViewer f$0;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda68(PhotoViewer photoViewer) {
        this.f$0 = photoViewer;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$setParentActivity$37(view, i);
    }
}

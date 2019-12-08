package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$bKgrSm6AofDvar__rtAGVFxdlER0 implements OnItemClickListener {
    private final /* synthetic */ PhotoViewer f$0;

    public /* synthetic */ -$$Lambda$PhotoViewer$bKgrSm6AofDvar__rtAGVFxdlER0(PhotoViewer photoViewer) {
        this.f$0 = photoViewer;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$setParentActivity$29$PhotoViewer(view, i);
    }
}

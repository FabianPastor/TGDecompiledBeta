package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$XK2mp7qvOBWcZwrjv0iPj-jz1vM implements OnItemLongClickListener {
    private final /* synthetic */ PhotoViewer f$0;

    public /* synthetic */ -$$Lambda$PhotoViewer$XK2mp7qvOBWcZwrjv0iPj-jz1vM(PhotoViewer photoViewer) {
        this.f$0 = photoViewer;
    }

    public final boolean onItemClick(View view, int i) {
        return this.f$0.lambda$setParentActivity$31$PhotoViewer(view, i);
    }
}

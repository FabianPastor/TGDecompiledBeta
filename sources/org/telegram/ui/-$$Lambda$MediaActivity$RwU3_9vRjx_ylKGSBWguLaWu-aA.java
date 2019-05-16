package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaActivity$RwU3_9vRjx_ylKGSBWguLaWu-aA implements OnItemLongClickListener {
    private final /* synthetic */ MediaActivity f$0;
    private final /* synthetic */ MediaPage f$1;

    public /* synthetic */ -$$Lambda$MediaActivity$RwU3_9vRjx_ylKGSBWguLaWu-aA(MediaActivity mediaActivity, MediaPage mediaPage) {
        this.f$0 = mediaActivity;
        this.f$1 = mediaPage;
    }

    public final boolean onItemClick(View view, int i) {
        return this.f$0.lambda$createView$3$MediaActivity(this.f$1, view, i);
    }
}

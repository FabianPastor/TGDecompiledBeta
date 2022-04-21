package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SharedMediaLayout;

public final /* synthetic */ class SharedMediaLayout$$ExternalSyntheticLambda7 implements RecyclerListView.OnItemLongClickListener {
    public final /* synthetic */ SharedMediaLayout f$0;
    public final /* synthetic */ SharedMediaLayout.MediaPage f$1;

    public /* synthetic */ SharedMediaLayout$$ExternalSyntheticLambda7(SharedMediaLayout sharedMediaLayout, SharedMediaLayout.MediaPage mediaPage) {
        this.f$0 = sharedMediaLayout;
        this.f$1 = mediaPage;
    }

    public final boolean onItemClick(View view, int i) {
        return this.f$0.m4368lambda$new$7$orgtelegramuiComponentsSharedMediaLayout(this.f$1, view, i);
    }
}

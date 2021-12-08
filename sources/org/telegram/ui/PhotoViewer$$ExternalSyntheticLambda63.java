package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda63 implements RecyclerListView.OnItemLongClickListener {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda63(PhotoViewer photoViewer, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = photoViewer;
        this.f$1 = resourcesProvider;
    }

    public final boolean onItemClick(View view, int i) {
        return this.f$0.m3601lambda$setParentActivity$38$orgtelegramuiPhotoViewer(this.f$1, view, i);
    }
}

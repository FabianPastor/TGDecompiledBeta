package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda75 implements RecyclerListView.OnItemLongClickListener {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda75(PhotoViewer photoViewer, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = photoViewer;
        this.f$1 = resourcesProvider;
    }

    public final boolean onItemClick(View view, int i) {
        return this.f$0.m4285lambda$setParentActivity$41$orgtelegramuiPhotoViewer(this.f$1, view, i);
    }
}

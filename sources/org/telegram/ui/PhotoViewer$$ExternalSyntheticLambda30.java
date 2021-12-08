package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda30 implements View.OnLongClickListener {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda30(PhotoViewer photoViewer, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = photoViewer;
        this.f$1 = resourcesProvider;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.m3579lambda$setParentActivity$14$orgtelegramuiPhotoViewer(this.f$1, view);
    }
}

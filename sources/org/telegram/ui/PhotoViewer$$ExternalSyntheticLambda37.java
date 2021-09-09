package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda37 implements View.OnLongClickListener {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda37(PhotoViewer photoViewer, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = photoViewer;
        this.f$1 = resourcesProvider;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.lambda$setParentActivity$14(this.f$1, view);
    }
}

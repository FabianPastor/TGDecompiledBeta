package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda43 implements View.OnClickListener {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda43(PhotoViewer photoViewer, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = photoViewer;
        this.f$1 = resourcesProvider;
    }

    public final void onClick(View view) {
        this.f$0.lambda$setParentActivity$29(this.f$1, view);
    }
}

package org.telegram.ui;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ViewSwitcher;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda35 implements ViewSwitcher.ViewFactory {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ LinkMovementMethod f$1;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda35(PhotoViewer photoViewer, LinkMovementMethod linkMovementMethod) {
        this.f$0 = photoViewer;
        this.f$1 = linkMovementMethod;
    }

    public final View makeView() {
        return this.f$0.m3603lambda$setParentActivity$5$orgtelegramuiPhotoViewer(this.f$1);
    }
}

package org.telegram.ui;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ViewSwitcher;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda41 implements ViewSwitcher.ViewFactory {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ LinkMovementMethod f$1;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda41(PhotoViewer photoViewer, LinkMovementMethod linkMovementMethod) {
        this.f$0 = photoViewer;
        this.f$1 = linkMovementMethod;
    }

    public final View makeView() {
        return this.f$0.lambda$setParentActivity$5(this.f$1);
    }
}

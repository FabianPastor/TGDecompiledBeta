package org.telegram.ui;

import org.telegram.ui.Components.SlideChooseView;

public final /* synthetic */ class LinkEditActivity$$ExternalSyntheticLambda12 implements SlideChooseView.Callback {
    public final /* synthetic */ LinkEditActivity f$0;

    public /* synthetic */ LinkEditActivity$$ExternalSyntheticLambda12(LinkEditActivity linkEditActivity) {
        this.f$0 = linkEditActivity;
    }

    public final void onOptionSelected(int i) {
        this.f$0.lambda$createView$4(i);
    }

    public /* synthetic */ void onTouchEnd() {
        SlideChooseView.Callback.CC.$default$onTouchEnd(this);
    }
}
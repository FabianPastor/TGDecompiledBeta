package org.telegram.ui.Components;

import android.view.View;

public final /* synthetic */ class AutoDeletePopupWrapper$$ExternalSyntheticLambda5 implements View.OnClickListener {
    public final /* synthetic */ PopupSwipeBackLayout f$0;

    public /* synthetic */ AutoDeletePopupWrapper$$ExternalSyntheticLambda5(PopupSwipeBackLayout popupSwipeBackLayout) {
        this.f$0 = popupSwipeBackLayout;
    }

    public final void onClick(View view) {
        this.f$0.closeForeground();
    }
}
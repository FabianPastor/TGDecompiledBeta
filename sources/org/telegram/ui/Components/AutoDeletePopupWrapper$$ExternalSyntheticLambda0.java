package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AutoDeletePopupWrapper;

public final /* synthetic */ class AutoDeletePopupWrapper$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ AutoDeletePopupWrapper f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ Theme.ResourcesProvider f$2;
    public final /* synthetic */ AutoDeletePopupWrapper.Callback f$3;

    public /* synthetic */ AutoDeletePopupWrapper$$ExternalSyntheticLambda0(AutoDeletePopupWrapper autoDeletePopupWrapper, Context context, Theme.ResourcesProvider resourcesProvider, AutoDeletePopupWrapper.Callback callback) {
        this.f$0 = autoDeletePopupWrapper;
        this.f$1 = context;
        this.f$2 = resourcesProvider;
        this.f$3 = callback;
    }

    public final void onClick(View view) {
        this.f$0.lambda$new$5(this.f$1, this.f$2, this.f$3, view);
    }
}

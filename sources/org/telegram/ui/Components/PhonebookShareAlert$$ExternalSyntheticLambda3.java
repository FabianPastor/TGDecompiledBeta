package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class PhonebookShareAlert$$ExternalSyntheticLambda3 implements View.OnLongClickListener {
    public final /* synthetic */ PhonebookShareAlert f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Theme.ResourcesProvider f$2;
    public final /* synthetic */ Context f$3;

    public /* synthetic */ PhonebookShareAlert$$ExternalSyntheticLambda3(PhonebookShareAlert phonebookShareAlert, int i, Theme.ResourcesProvider resourcesProvider, Context context) {
        this.f$0 = phonebookShareAlert;
        this.f$1 = i;
        this.f$2 = resourcesProvider;
        this.f$3 = context;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.lambda$new$3(this.f$1, this.f$2, this.f$3, view);
    }
}

package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda6 implements View.OnLongClickListener {
    public final /* synthetic */ ChatAttachAlert f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda6(ChatAttachAlert chatAttachAlert, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = chatAttachAlert;
        this.f$1 = resourcesProvider;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.lambda$new$15(this.f$1, view);
    }
}
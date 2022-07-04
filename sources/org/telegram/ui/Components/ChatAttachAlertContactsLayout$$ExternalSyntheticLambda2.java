package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatAttachAlertContactsLayout$$ExternalSyntheticLambda2 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ ChatAttachAlertContactsLayout f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;

    public /* synthetic */ ChatAttachAlertContactsLayout$$ExternalSyntheticLambda2(ChatAttachAlertContactsLayout chatAttachAlertContactsLayout, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = chatAttachAlertContactsLayout;
        this.f$1 = resourcesProvider;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$new$1(this.f$1, view, i);
    }
}

package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatAttachAlertLocationLayout$$ExternalSyntheticLambda27 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ ChatAttachAlertLocationLayout f$0;
    public final /* synthetic */ ChatActivity f$1;
    public final /* synthetic */ Theme.ResourcesProvider f$2;

    public /* synthetic */ ChatAttachAlertLocationLayout$$ExternalSyntheticLambda27(ChatAttachAlertLocationLayout chatAttachAlertLocationLayout, ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = chatAttachAlertLocationLayout;
        this.f$1 = chatActivity;
        this.f$2 = resourcesProvider;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$new$7(this.f$1, this.f$2, view, i);
    }
}

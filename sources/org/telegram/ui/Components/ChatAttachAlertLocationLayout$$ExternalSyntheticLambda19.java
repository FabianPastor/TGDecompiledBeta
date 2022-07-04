package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatAttachAlertLocationLayout$$ExternalSyntheticLambda19 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ ChatAttachAlertLocationLayout f$0;
    public final /* synthetic */ ChatActivity f$1;
    public final /* synthetic */ Theme.ResourcesProvider f$2;

    public /* synthetic */ ChatAttachAlertLocationLayout$$ExternalSyntheticLambda19(ChatAttachAlertLocationLayout chatAttachAlertLocationLayout, ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = chatAttachAlertLocationLayout;
        this.f$1 = chatActivity;
        this.f$2 = resourcesProvider;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.m812x3b5fdab(this.f$1, this.f$2, view, i);
    }
}

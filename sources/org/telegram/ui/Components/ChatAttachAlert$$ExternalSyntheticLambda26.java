package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda26 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ ChatAttachAlert f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda26(ChatAttachAlert chatAttachAlert, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = chatAttachAlert;
        this.f$1 = resourcesProvider;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.m3775lambda$new$7$orgtelegramuiComponentsChatAttachAlert(this.f$1, view, i);
    }
}

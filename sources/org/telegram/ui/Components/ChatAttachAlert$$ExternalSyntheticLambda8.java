package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda8 implements View.OnClickListener {
    public final /* synthetic */ ChatAttachAlert f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ChatActivity f$2;
    public final /* synthetic */ Theme.ResourcesProvider f$3;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda8(ChatAttachAlert chatAttachAlert, int i, ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = chatAttachAlert;
        this.f$1 = i;
        this.f$2 = chatActivity;
        this.f$3 = resourcesProvider;
    }

    public final void onClick(View view) {
        this.f$0.lambda$new$15(this.f$1, this.f$2, this.f$3, view);
    }
}

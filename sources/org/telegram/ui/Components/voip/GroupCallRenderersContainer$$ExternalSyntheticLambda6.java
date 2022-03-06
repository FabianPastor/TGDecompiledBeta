package org.telegram.ui.Components.voip;

import android.view.View;
import org.telegram.messenger.ChatObject;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallRenderersContainer$$ExternalSyntheticLambda6 implements View.OnClickListener {
    public final /* synthetic */ GroupCallRenderersContainer f$0;
    public final /* synthetic */ ChatObject.Call f$1;
    public final /* synthetic */ GroupCallActivity f$2;

    public /* synthetic */ GroupCallRenderersContainer$$ExternalSyntheticLambda6(GroupCallRenderersContainer groupCallRenderersContainer, ChatObject.Call call, GroupCallActivity groupCallActivity) {
        this.f$0 = groupCallRenderersContainer;
        this.f$1 = call;
        this.f$2 = groupCallActivity;
    }

    public final void onClick(View view) {
        this.f$0.lambda$new$2(this.f$1, this.f$2, view);
    }
}

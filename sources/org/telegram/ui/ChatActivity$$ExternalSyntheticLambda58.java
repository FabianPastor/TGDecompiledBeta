package org.telegram.ui;

import android.view.View;
import android.widget.FrameLayout;
import org.telegram.ui.Components.ChatActivityEnterTopView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda58 implements View.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ ChatActivityEnterTopView.EditViewButton f$1;
    public final /* synthetic */ FrameLayout f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda58(ChatActivity chatActivity, ChatActivityEnterTopView.EditViewButton editViewButton, FrameLayout frameLayout) {
        this.f$0 = chatActivity;
        this.f$1 = editViewButton;
        this.f$2 = frameLayout;
    }

    public final void onClick(View view) {
        this.f$0.lambda$createView$39(this.f$1, this.f$2, view);
    }
}

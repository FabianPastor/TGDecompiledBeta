package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatActivity$99$$ExternalSyntheticLambda0 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ ChatActivity.AnonymousClass99 f$0;
    public final /* synthetic */ MessageSeenView f$1;
    public final /* synthetic */ ActionBarPopupWindow f$2;

    public /* synthetic */ ChatActivity$99$$ExternalSyntheticLambda0(ChatActivity.AnonymousClass99 r1, MessageSeenView messageSeenView, ActionBarPopupWindow actionBarPopupWindow) {
        this.f$0 = r1;
        this.f$1 = messageSeenView;
        this.f$2 = actionBarPopupWindow;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$onClick$0(this.f$1, this.f$2, view, i);
    }
}

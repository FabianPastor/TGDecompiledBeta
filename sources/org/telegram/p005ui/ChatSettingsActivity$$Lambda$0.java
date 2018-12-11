package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListenerExtended;

/* renamed from: org.telegram.ui.ChatSettingsActivity$$Lambda$0 */
final /* synthetic */ class ChatSettingsActivity$$Lambda$0 implements OnItemClickListenerExtended {
    private final ChatSettingsActivity arg$1;

    ChatSettingsActivity$$Lambda$0(ChatSettingsActivity chatSettingsActivity) {
        this.arg$1 = chatSettingsActivity;
    }

    public void onItemClick(View view, int i, float f, float f2) {
        this.arg$1.lambda$createView$4$ChatSettingsActivity(view, i, f, f2);
    }
}

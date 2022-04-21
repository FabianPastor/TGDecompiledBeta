package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class PollVotesAlert$$ExternalSyntheticLambda6 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ PollVotesAlert f$0;
    public final /* synthetic */ ChatActivity f$1;

    public /* synthetic */ PollVotesAlert$$ExternalSyntheticLambda6(PollVotesAlert pollVotesAlert, ChatActivity chatActivity) {
        this.f$0 = pollVotesAlert;
        this.f$1 = chatActivity;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.m4244lambda$new$4$orgtelegramuiComponentsPollVotesAlert(this.f$1, view, i);
    }
}

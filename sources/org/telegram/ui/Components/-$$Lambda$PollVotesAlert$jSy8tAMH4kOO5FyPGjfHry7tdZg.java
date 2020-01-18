package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PollVotesAlert$jSy8tAMH4kOO5FyPGjfHry7tdZg implements OnItemClickListener {
    private final /* synthetic */ PollVotesAlert f$0;
    private final /* synthetic */ ChatActivity f$1;

    public /* synthetic */ -$$Lambda$PollVotesAlert$jSy8tAMH4kOO5FyPGjfHry7tdZg(PollVotesAlert pollVotesAlert, ChatActivity chatActivity) {
        this.f$0 = pollVotesAlert;
        this.f$1 = chatActivity;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$new$6$PollVotesAlert(this.f$1, view, i);
    }
}

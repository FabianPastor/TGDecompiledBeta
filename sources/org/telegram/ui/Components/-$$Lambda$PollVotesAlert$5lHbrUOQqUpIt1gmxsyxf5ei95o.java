package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ChatActivity;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PollVotesAlert$5lHbrUOQqUpIt1gmxsyxf5ei95o implements Runnable {
    private final /* synthetic */ PollVotesAlert f$0;
    private final /* synthetic */ VotesList f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ ChatActivity f$3;

    public /* synthetic */ -$$Lambda$PollVotesAlert$5lHbrUOQqUpIt1gmxsyxf5ei95o(PollVotesAlert pollVotesAlert, VotesList votesList, TLObject tLObject, ChatActivity chatActivity) {
        this.f$0 = pollVotesAlert;
        this.f$1 = votesList;
        this.f$2 = tLObject;
        this.f$3 = chatActivity;
    }

    public final void run() {
        this.f$0.lambda$null$4$PollVotesAlert(this.f$1, this.f$2, this.f$3);
    }
}

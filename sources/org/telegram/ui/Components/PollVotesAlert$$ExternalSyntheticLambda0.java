package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.PollVotesAlert;

public final /* synthetic */ class PollVotesAlert$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ PollVotesAlert f$0;
    public final /* synthetic */ PollVotesAlert.VotesList f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ ChatActivity f$3;

    public /* synthetic */ PollVotesAlert$$ExternalSyntheticLambda0(PollVotesAlert pollVotesAlert, PollVotesAlert.VotesList votesList, TLObject tLObject, ChatActivity chatActivity) {
        this.f$0 = pollVotesAlert;
        this.f$1 = votesList;
        this.f$2 = tLObject;
        this.f$3 = chatActivity;
    }

    public final void run() {
        this.f$0.lambda$new$2(this.f$1, this.f$2, this.f$3);
    }
}

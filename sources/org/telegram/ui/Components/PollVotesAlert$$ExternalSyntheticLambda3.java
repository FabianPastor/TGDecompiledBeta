package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.PollVotesAlert;

public final /* synthetic */ class PollVotesAlert$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ PollVotesAlert f$0;
    public final /* synthetic */ PollVotesAlert.VotesList f$1;
    public final /* synthetic */ ChatActivity f$2;

    public /* synthetic */ PollVotesAlert$$ExternalSyntheticLambda3(PollVotesAlert pollVotesAlert, PollVotesAlert.VotesList votesList, ChatActivity chatActivity) {
        this.f$0 = pollVotesAlert;
        this.f$1 = votesList;
        this.f$2 = chatActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$new$3(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}

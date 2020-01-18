package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChatActivity;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PollVotesAlert$LUEfex9WGWPDyK6C3yjoxgZbEU8 implements RequestDelegate {
    private final /* synthetic */ PollVotesAlert f$0;
    private final /* synthetic */ VotesList f$1;
    private final /* synthetic */ ChatActivity f$2;

    public /* synthetic */ -$$Lambda$PollVotesAlert$LUEfex9WGWPDyK6C3yjoxgZbEU8(PollVotesAlert pollVotesAlert, VotesList votesList, ChatActivity chatActivity) {
        this.f$0 = pollVotesAlert;
        this.f$1 = votesList;
        this.f$2 = chatActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$5$PollVotesAlert(this.f$1, this.f$2, tLObject, tL_error);
    }
}

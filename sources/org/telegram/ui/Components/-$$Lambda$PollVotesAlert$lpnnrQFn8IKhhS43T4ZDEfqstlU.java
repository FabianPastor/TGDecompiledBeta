package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_pollAnswerVoters;
import org.telegram.ui.ChatActivity;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PollVotesAlert$lpnnrQFn8IKhhS43T4ZDEfqstlU implements RequestDelegate {
    private final /* synthetic */ PollVotesAlert f$0;
    private final /* synthetic */ Integer[] f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ ChatActivity f$3;
    private final /* synthetic */ ArrayList f$4;
    private final /* synthetic */ TL_pollAnswerVoters f$5;

    public /* synthetic */ -$$Lambda$PollVotesAlert$lpnnrQFn8IKhhS43T4ZDEfqstlU(PollVotesAlert pollVotesAlert, Integer[] numArr, int i, ChatActivity chatActivity, ArrayList arrayList, TL_pollAnswerVoters tL_pollAnswerVoters) {
        this.f$0 = pollVotesAlert;
        this.f$1 = numArr;
        this.f$2 = i;
        this.f$3 = chatActivity;
        this.f$4 = arrayList;
        this.f$5 = tL_pollAnswerVoters;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$new$1$PollVotesAlert(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}

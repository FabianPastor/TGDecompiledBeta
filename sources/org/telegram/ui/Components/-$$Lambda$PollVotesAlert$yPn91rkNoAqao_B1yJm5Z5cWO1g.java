package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_pollAnswerVoters;
import org.telegram.ui.ChatActivity;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PollVotesAlert$yPn91rkNoAqao_B1yJm5Z5cWO1g implements Runnable {
    private final /* synthetic */ PollVotesAlert f$0;
    private final /* synthetic */ Integer[] f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ ChatActivity f$4;
    private final /* synthetic */ ArrayList f$5;
    private final /* synthetic */ TL_pollAnswerVoters f$6;

    public /* synthetic */ -$$Lambda$PollVotesAlert$yPn91rkNoAqao_B1yJm5Z5cWO1g(PollVotesAlert pollVotesAlert, Integer[] numArr, int i, TLObject tLObject, ChatActivity chatActivity, ArrayList arrayList, TL_pollAnswerVoters tL_pollAnswerVoters) {
        this.f$0 = pollVotesAlert;
        this.f$1 = numArr;
        this.f$2 = i;
        this.f$3 = tLObject;
        this.f$4 = chatActivity;
        this.f$5 = arrayList;
        this.f$6 = tL_pollAnswerVoters;
    }

    public final void run() {
        this.f$0.lambda$null$0$PollVotesAlert(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

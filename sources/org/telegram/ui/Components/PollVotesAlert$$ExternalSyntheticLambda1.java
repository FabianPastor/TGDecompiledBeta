package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class PollVotesAlert$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ PollVotesAlert f$0;
    public final /* synthetic */ Integer[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ ChatActivity f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ TLRPC.TL_pollAnswerVoters f$6;

    public /* synthetic */ PollVotesAlert$$ExternalSyntheticLambda1(PollVotesAlert pollVotesAlert, Integer[] numArr, int i, TLObject tLObject, ChatActivity chatActivity, ArrayList arrayList, TLRPC.TL_pollAnswerVoters tL_pollAnswerVoters) {
        this.f$0 = pollVotesAlert;
        this.f$1 = numArr;
        this.f$2 = i;
        this.f$3 = tLObject;
        this.f$4 = chatActivity;
        this.f$5 = arrayList;
        this.f$6 = tL_pollAnswerVoters;
    }

    public final void run() {
        this.f$0.m1220lambda$new$0$orgtelegramuiComponentsPollVotesAlert(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

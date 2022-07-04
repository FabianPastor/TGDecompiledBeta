package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_pollAnswerVoters;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class PollVotesAlert$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ PollVotesAlert f$0;
    public final /* synthetic */ Integer[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ ChatActivity f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ TLRPC$TL_pollAnswerVoters f$5;

    public /* synthetic */ PollVotesAlert$$ExternalSyntheticLambda4(PollVotesAlert pollVotesAlert, Integer[] numArr, int i, ChatActivity chatActivity, ArrayList arrayList, TLRPC$TL_pollAnswerVoters tLRPC$TL_pollAnswerVoters) {
        this.f$0 = pollVotesAlert;
        this.f$1 = numArr;
        this.f$2 = i;
        this.f$3 = chatActivity;
        this.f$4 = arrayList;
        this.f$5 = tLRPC$TL_pollAnswerVoters;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$new$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
    }
}

package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.PollCreateActivity;

public final /* synthetic */ class PollCreateActivity$1$$ExternalSyntheticLambda0 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ PollCreateActivity.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC$TL_messageMediaPoll f$1;
    public final /* synthetic */ HashMap f$2;

    public /* synthetic */ PollCreateActivity$1$$ExternalSyntheticLambda0(PollCreateActivity.AnonymousClass1 r1, TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, HashMap hashMap) {
        this.f$0 = r1;
        this.f$1 = tLRPC$TL_messageMediaPoll;
        this.f$2 = hashMap;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$onItemClick$0(this.f$1, this.f$2, z, i);
    }
}
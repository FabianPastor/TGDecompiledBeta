package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;
import org.telegram.ui.PollCreateActivity.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PollCreateActivity$1$7mDxOR-eTq9iu19UCqI6FzmRSHA implements ScheduleDatePickerDelegate {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ TL_messageMediaPoll f$1;
    private final /* synthetic */ HashMap f$2;

    public /* synthetic */ -$$Lambda$PollCreateActivity$1$7mDxOR-eTq9iu19UCqI6FzmRSHA(AnonymousClass1 anonymousClass1, TL_messageMediaPoll tL_messageMediaPoll, HashMap hashMap) {
        this.f$0 = anonymousClass1;
        this.f$1 = tL_messageMediaPoll;
        this.f$2 = hashMap;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$onItemClick$0$PollCreateActivity$1(this.f$1, this.f$2, z, i);
    }
}

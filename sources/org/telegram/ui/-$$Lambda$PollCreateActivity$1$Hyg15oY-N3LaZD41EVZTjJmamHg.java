package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;
import org.telegram.ui.PollCreateActivity.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PollCreateActivity$1$Hyg15oY-N3LaZD41EVZTjJmamHg implements ScheduleDatePickerDelegate {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ TL_messageMediaPoll f$1;

    public /* synthetic */ -$$Lambda$PollCreateActivity$1$Hyg15oY-N3LaZD41EVZTjJmamHg(AnonymousClass1 anonymousClass1, TL_messageMediaPoll tL_messageMediaPoll) {
        this.f$0 = anonymousClass1;
        this.f$1 = tL_messageMediaPoll;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$onItemClick$0$PollCreateActivity$1(this.f$1, z, i);
    }
}

package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$oOngw55yILtk_GEUhpvDDM76dDI implements ScheduleDatePickerDelegate {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ GroupedMessages f$1;
    private final /* synthetic */ MessageObject f$2;

    public /* synthetic */ -$$Lambda$ChatActivity$oOngw55yILtk_GEUhpvDDM76dDI(ChatActivity chatActivity, GroupedMessages groupedMessages, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = groupedMessages;
        this.f$2 = messageObject;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$processSelectedOption$98$ChatActivity(this.f$1, this.f$2, z, i);
    }
}

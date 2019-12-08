package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$5fyFEyTsN7srR25HWIzkmU-MazA implements ScheduleDatePickerDelegate {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ GroupedMessages f$1;
    private final /* synthetic */ MessageObject f$2;

    public /* synthetic */ -$$Lambda$ChatActivity$5fyFEyTsN7srR25HWIzkmU-MazA(ChatActivity chatActivity, GroupedMessages groupedMessages, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = groupedMessages;
        this.f$2 = messageObject;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$processSelectedOption$94$ChatActivity(this.f$1, this.f$2, z, i);
    }
}

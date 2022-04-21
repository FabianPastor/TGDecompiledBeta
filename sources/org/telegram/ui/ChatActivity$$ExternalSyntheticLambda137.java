package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda137 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessageObject.GroupedMessages f$1;
    public final /* synthetic */ MessageObject f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda137(ChatActivity chatActivity, MessageObject.GroupedMessages groupedMessages, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = groupedMessages;
        this.f$2 = messageObject;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.m1796lambda$processSelectedOption$204$orgtelegramuiChatActivity(this.f$1, this.f$2, z, i);
    }
}

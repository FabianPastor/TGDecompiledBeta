package org.telegram.ui.Components;

import org.telegram.messenger.ContactsController;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;

public final /* synthetic */ class ChatAttachAlertContactsLayout$ShareAdapter$$ExternalSyntheticLambda0 implements ChatAttachAlertContactsLayout.UserCell.CharSequenceCallback {
    public final /* synthetic */ ContactsController.Contact f$0;

    public /* synthetic */ ChatAttachAlertContactsLayout$ShareAdapter$$ExternalSyntheticLambda0(ContactsController.Contact contact) {
        this.f$0 = contact;
    }

    public final CharSequence run() {
        return ChatAttachAlertContactsLayout.ShareAdapter.lambda$onBindViewHolder$0(this.f$0);
    }
}

package org.telegram.ui.Components;

import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;

public final /* synthetic */ class ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda5 implements ChatAttachAlertContactsLayout.UserCell.CharSequenceCallback {
    public final /* synthetic */ TLRPC$User f$0;

    public /* synthetic */ ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda5(TLRPC$User tLRPC$User) {
        this.f$0 = tLRPC$User;
    }

    public final CharSequence run() {
        return PhoneFormat.getInstance();
    }
}

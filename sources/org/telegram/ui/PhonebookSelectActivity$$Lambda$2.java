package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.PhonebookSelectActivity.PhonebookSelectActivityDelegate;

final /* synthetic */ class PhonebookSelectActivity$$Lambda$2 implements PhonebookSelectActivityDelegate {
    private final PhonebookSelectActivity arg$1;

    PhonebookSelectActivity$$Lambda$2(PhonebookSelectActivity phonebookSelectActivity) {
        this.arg$1 = phonebookSelectActivity;
    }

    public void didSelectContact(User user) {
        this.arg$1.lambda$null$0$PhonebookSelectActivity(user);
    }
}

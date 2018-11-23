package org.telegram.p005ui;

import org.telegram.p005ui.PhonebookSelectActivity.PhonebookSelectActivityDelegate;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.PhonebookSelectActivity$$Lambda$2 */
final /* synthetic */ class PhonebookSelectActivity$$Lambda$2 implements PhonebookSelectActivityDelegate {
    private final PhonebookSelectActivity arg$1;

    PhonebookSelectActivity$$Lambda$2(PhonebookSelectActivity phonebookSelectActivity) {
        this.arg$1 = phonebookSelectActivity;
    }

    public void didSelectContact(User user) {
        this.arg$1.lambda$null$0$PhonebookSelectActivity(user);
    }
}

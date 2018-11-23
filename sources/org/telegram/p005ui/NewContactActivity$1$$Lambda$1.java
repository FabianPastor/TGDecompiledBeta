package org.telegram.p005ui;

import org.telegram.p005ui.NewContactActivity.C14831;
import org.telegram.tgnet.TLRPC.TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC.TL_contacts_importedContacts;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPhoneContact;

/* renamed from: org.telegram.ui.NewContactActivity$1$$Lambda$1 */
final /* synthetic */ class NewContactActivity$1$$Lambda$1 implements Runnable {
    private final C14831 arg$1;
    private final TL_contacts_importedContacts arg$2;
    private final TL_inputPhoneContact arg$3;
    private final TL_error arg$4;
    private final TL_contacts_importContacts arg$5;

    NewContactActivity$1$$Lambda$1(C14831 c14831, TL_contacts_importedContacts tL_contacts_importedContacts, TL_inputPhoneContact tL_inputPhoneContact, TL_error tL_error, TL_contacts_importContacts tL_contacts_importContacts) {
        this.arg$1 = c14831;
        this.arg$2 = tL_contacts_importedContacts;
        this.arg$3 = tL_inputPhoneContact;
        this.arg$4 = tL_error;
        this.arg$5 = tL_contacts_importContacts;
    }

    public void run() {
        this.arg$1.lambda$null$1$NewContactActivity$1(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

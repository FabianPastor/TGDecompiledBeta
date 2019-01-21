package org.telegram.ui.Adapters;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.TL_contact;

final /* synthetic */ class ContactsAdapter$$Lambda$0 implements Comparator {
    private final MessagesController arg$1;
    private final int arg$2;

    ContactsAdapter$$Lambda$0(MessagesController messagesController, int i) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
    }

    public int compare(Object obj, Object obj2) {
        return ContactsAdapter.lambda$sortOnlineContacts$0$ContactsAdapter(this.arg$1, this.arg$2, (TL_contact) obj, (TL_contact) obj2);
    }
}

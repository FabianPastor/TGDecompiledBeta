package org.telegram.ui.Adapters;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsAdapter$$ExternalSyntheticLambda0 implements Comparator {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ContactsAdapter$$ExternalSyntheticLambda0(MessagesController messagesController, int i) {
        this.f$0 = messagesController;
        this.f$1 = i;
    }

    public final int compare(Object obj, Object obj2) {
        return ContactsAdapter.lambda$sortOnlineContacts$0(this.f$0, this.f$1, (TLRPC.TL_contact) obj, (TLRPC.TL_contact) obj2);
    }
}

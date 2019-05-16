package org.telegram.ui.Adapters;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.TL_contact;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsAdapter$AjIuF4bNE-A90essgyL0wfJ8HaU implements Comparator {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$ContactsAdapter$AjIuF4bNE-A90essgyL0wfJ8HaU(MessagesController messagesController, int i) {
        this.f$0 = messagesController;
        this.f$1 = i;
    }

    public final int compare(Object obj, Object obj2) {
        return ContactsAdapter.lambda$sortOnlineContacts$0(this.f$0, this.f$1, (TL_contact) obj, (TL_contact) obj2);
    }
}

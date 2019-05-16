package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$EZvJWephnINCisVyT9aioUlUdEQ implements Runnable {
    private final /* synthetic */ ContactsController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ ArrayList f$3;

    public /* synthetic */ -$$Lambda$ContactsController$EZvJWephnINCisVyT9aioUlUdEQ(ContactsController contactsController, ArrayList arrayList, int i, ArrayList arrayList2) {
        this.f$0 = contactsController;
        this.f$1 = arrayList;
        this.f$2 = i;
        this.f$3 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$processLoadedContacts$35$ContactsController(this.f$1, this.f$2, this.f$3);
    }
}

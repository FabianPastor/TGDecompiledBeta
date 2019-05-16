package org.telegram.messenger;

import java.util.HashMap;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$zqe6opQgPyDWpCXT4MV8TEtpNRg implements Runnable {
    private final /* synthetic */ ContactsController f$0;
    private final /* synthetic */ HashMap f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ boolean f$4;

    public /* synthetic */ -$$Lambda$ContactsController$zqe6opQgPyDWpCXT4MV8TEtpNRg(ContactsController contactsController, HashMap hashMap, boolean z, boolean z2, boolean z3) {
        this.f$0 = contactsController;
        this.f$1 = hashMap;
        this.f$2 = z;
        this.f$3 = z2;
        this.f$4 = z3;
    }

    public final void run() {
        this.f$0.lambda$syncPhoneBookByAlert$6$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

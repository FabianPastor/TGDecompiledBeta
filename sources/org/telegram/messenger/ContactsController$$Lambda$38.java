package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

final /* synthetic */ class ContactsController$$Lambda$38 implements Runnable {
    private final ContactsController arg$1;
    private final int arg$2;
    private final ArrayList arg$3;
    private final SparseArray arg$4;
    private final ArrayList arg$5;
    private final boolean arg$6;

    ContactsController$$Lambda$38(ContactsController contactsController, int i, ArrayList arrayList, SparseArray sparseArray, ArrayList arrayList2, boolean z) {
        this.arg$1 = contactsController;
        this.arg$2 = i;
        this.arg$3 = arrayList;
        this.arg$4 = sparseArray;
        this.arg$5 = arrayList2;
        this.arg$6 = z;
    }

    public void run() {
        this.arg$1.lambda$null$34$ContactsController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}

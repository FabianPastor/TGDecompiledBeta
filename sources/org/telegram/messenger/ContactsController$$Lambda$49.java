package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ContactsController$$Lambda$49 implements RequestDelegate {
    private final ContactsController arg$1;
    private final HashMap arg$10;
    private final ArrayList arg$11;
    private final HashMap arg$12;
    private final HashMap arg$2;
    private final SparseArray arg$3;
    private final boolean[] arg$4;
    private final HashMap arg$5;
    private final TL_contacts_importContacts arg$6;
    private final int arg$7;
    private final HashMap arg$8;
    private final boolean arg$9;

    ContactsController$$Lambda$49(ContactsController contactsController, HashMap hashMap, SparseArray sparseArray, boolean[] zArr, HashMap hashMap2, TL_contacts_importContacts tL_contacts_importContacts, int i, HashMap hashMap3, boolean z, HashMap hashMap4, ArrayList arrayList, HashMap hashMap5) {
        this.arg$1 = contactsController;
        this.arg$2 = hashMap;
        this.arg$3 = sparseArray;
        this.arg$4 = zArr;
        this.arg$5 = hashMap2;
        this.arg$6 = tL_contacts_importContacts;
        this.arg$7 = i;
        this.arg$8 = hashMap3;
        this.arg$9 = z;
        this.arg$10 = hashMap4;
        this.arg$11 = arrayList;
        this.arg$12 = hashMap5;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$19$ContactsController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12, tLObject, tL_error);
    }
}

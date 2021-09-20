package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda60 implements RequestDelegate {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ HashMap f$1;
    public final /* synthetic */ ArrayList f$10;
    public final /* synthetic */ HashMap f$11;
    public final /* synthetic */ SparseArray f$2;
    public final /* synthetic */ boolean[] f$3;
    public final /* synthetic */ HashMap f$4;
    public final /* synthetic */ TLRPC$TL_contacts_importContacts f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ HashMap f$7;
    public final /* synthetic */ boolean f$8;
    public final /* synthetic */ HashMap f$9;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda60(ContactsController contactsController, HashMap hashMap, SparseArray sparseArray, boolean[] zArr, HashMap hashMap2, TLRPC$TL_contacts_importContacts tLRPC$TL_contacts_importContacts, int i, HashMap hashMap3, boolean z, HashMap hashMap4, ArrayList arrayList, HashMap hashMap5) {
        this.f$0 = contactsController;
        this.f$1 = hashMap;
        this.f$2 = sparseArray;
        this.f$3 = zArr;
        this.f$4 = hashMap2;
        this.f$5 = tLRPC$TL_contacts_importContacts;
        this.f$6 = i;
        this.f$7 = hashMap3;
        this.f$8 = z;
        this.f$9 = hashMap4;
        this.f$10 = arrayList;
        this.f$11 = hashMap5;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$performSyncPhoneBook$19(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, tLObject, tLRPC$TL_error);
    }
}

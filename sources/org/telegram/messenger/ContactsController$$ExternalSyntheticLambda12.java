package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda12 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda12(ContactsController contactsController, int i, ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, boolean z) {
        this.f$0 = contactsController;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = longSparseArray;
        this.f$4 = arrayList2;
        this.f$5 = z;
    }

    public final void run() {
        this.f$0.lambda$processLoadedContacts$37(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

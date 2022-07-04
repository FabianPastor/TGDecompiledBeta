package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;

public final /* synthetic */ class ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ChatAttachAlertDocumentLayout.SearchAdapter f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$10;
    public final /* synthetic */ ArrayList f$11;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ AccountInstance f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ ArrayList f$7;
    public final /* synthetic */ long f$8;
    public final /* synthetic */ long f$9;

    public /* synthetic */ ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda0(ChatAttachAlertDocumentLayout.SearchAdapter searchAdapter, int i, TLRPC.TL_error tL_error, TLObject tLObject, AccountInstance accountInstance, boolean z, String str, ArrayList arrayList, long j, long j2, ArrayList arrayList2, ArrayList arrayList3) {
        this.f$0 = searchAdapter;
        this.f$1 = i;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
        this.f$4 = accountInstance;
        this.f$5 = z;
        this.f$6 = str;
        this.f$7 = arrayList;
        this.f$8 = j;
        this.f$9 = j2;
        this.f$10 = arrayList2;
        this.f$11 = arrayList3;
    }

    public final void run() {
        this.f$0.m795x623fd2f4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11);
    }
}

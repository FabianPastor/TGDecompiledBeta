package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;

public final /* synthetic */ class ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ ChatAttachAlertDocumentLayout.SearchAdapter f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ long f$5;
    public final /* synthetic */ long f$6;
    public final /* synthetic */ ArrayList f$7;
    public final /* synthetic */ ArrayList f$8;

    public /* synthetic */ ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda5(ChatAttachAlertDocumentLayout.SearchAdapter searchAdapter, AccountInstance accountInstance, String str, int i, boolean z, long j, long j2, ArrayList arrayList, ArrayList arrayList2) {
        this.f$0 = searchAdapter;
        this.f$1 = accountInstance;
        this.f$2 = str;
        this.f$3 = i;
        this.f$4 = z;
        this.f$5 = j;
        this.f$6 = j2;
        this.f$7 = arrayList;
        this.f$8 = arrayList2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2139xe48a87d3(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, tLObject, tL_error);
    }
}

package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda90 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.photos_Photos f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ ArrayList f$6;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda90(MessagesController messagesController, TLRPC.photos_Photos photos_photos, boolean z, long j, int i, int i2, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = photos_photos;
        this.f$2 = z;
        this.f$3 = j;
        this.f$4 = i;
        this.f$5 = i2;
        this.f$6 = arrayList;
    }

    public final void run() {
        this.f$0.m319x3eCLASSNAMEde(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

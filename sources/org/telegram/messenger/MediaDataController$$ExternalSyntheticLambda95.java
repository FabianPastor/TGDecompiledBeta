package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$messages_Messages;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda95 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$messages_Messages f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ boolean f$7;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda95(MediaDataController mediaDataController, TLRPC$messages_Messages tLRPC$messages_Messages, int i, long j, ArrayList arrayList, int i2, int i3, boolean z) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$messages_Messages;
        this.f$2 = i;
        this.f$3 = j;
        this.f$4 = arrayList;
        this.f$5 = i2;
        this.f$6 = i3;
        this.f$7 = z;
    }

    public final void run() {
        this.f$0.lambda$processLoadedMedia$78(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
package org.telegram.messenger;

import java.io.File;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaController$fwtpfrjmuwNTgxHqhKcXwShkFp0 implements Runnable {
    private final /* synthetic */ MediaController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ MessageObject f$3;
    private final /* synthetic */ File f$4;
    private final /* synthetic */ boolean f$5;
    private final /* synthetic */ long f$6;

    public /* synthetic */ -$$Lambda$MediaController$fwtpfrjmuwNTgxHqhKcXwShkFp0(MediaController mediaController, boolean z, boolean z2, MessageObject messageObject, File file, boolean z3, long j) {
        this.f$0 = mediaController;
        this.f$1 = z;
        this.f$2 = z2;
        this.f$3 = messageObject;
        this.f$4 = file;
        this.f$5 = z3;
        this.f$6 = j;
    }

    public final void run() {
        this.f$0.lambda$didWriteData$30$MediaController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

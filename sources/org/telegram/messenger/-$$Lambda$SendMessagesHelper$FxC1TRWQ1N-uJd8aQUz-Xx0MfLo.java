package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$FxC1TRWQ1N-uJd8aQUz-Xx0MfLo implements Runnable {
    private final /* synthetic */ String f$0;
    private final /* synthetic */ AccountInstance f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$FxC1TRWQ1N-uJd8aQUz-Xx0MfLo(String str, AccountInstance accountInstance, long j, boolean z, int i) {
        this.f$0 = str;
        this.f$1 = accountInstance;
        this.f$2 = j;
        this.f$3 = z;
        this.f$4 = i;
    }

    public final void run() {
        Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$C9Pki8mqBDXlTcwTQJEUzomDgqs(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4));
    }
}

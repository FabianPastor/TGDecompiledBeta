package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$jhRjY8xWtPrfzqwQH7U_YEYkCUI implements Runnable {
    private final /* synthetic */ String f$0;
    private final /* synthetic */ AccountInstance f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$jhRjY8xWtPrfzqwQH7U_YEYkCUI(String str, AccountInstance accountInstance, long j) {
        this.f$0 = str;
        this.f$1 = accountInstance;
        this.f$2 = j;
    }

    public final void run() {
        Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$dOHb6o3Y0EVpuMmt53B5szTCFkc(this.f$0, this.f$1, this.f$2));
    }
}

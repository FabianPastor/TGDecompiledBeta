package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$Hr6-JyEMN0ABFvY-59Un0Q8hT8s implements Runnable {
    private final /* synthetic */ String f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$Hr6-JyEMN0ABFvY-59Un0Q8hT8s(String str, int i, long j) {
        this.f$0 = str;
        this.f$1 = i;
        this.f$2 = j;
    }

    public final void run() {
        Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$hFI0i-EQI0VJYrn3pOsOHXCSUWE(this.f$0, this.f$1, this.f$2));
    }
}

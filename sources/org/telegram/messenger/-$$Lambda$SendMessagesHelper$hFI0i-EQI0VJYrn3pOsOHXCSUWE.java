package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$hFI0i-EQI0VJYrn3pOsOHXCSUWE implements Runnable {
    private final /* synthetic */ String f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$hFI0i-EQI0VJYrn3pOsOHXCSUWE(String str, int i, long j) {
        this.f$0 = str;
        this.f$1 = i;
        this.f$2 = j;
    }

    public final void run() {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$y8bHJEytFFXgLJSkwvsFDf0WaNI(this.f$0, this.f$1, this.f$2));
    }
}

package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$tpZkmPQgB5HIhBhgaa85dMkZYPU implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ byte[] f$3;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$tpZkmPQgB5HIhBhgaa85dMkZYPU(SendMessagesHelper sendMessagesHelper, long j, int i, byte[] bArr) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = bArr;
    }

    public final void run() {
        this.f$0.lambda$sendNotificationCallback$16$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
    }
}

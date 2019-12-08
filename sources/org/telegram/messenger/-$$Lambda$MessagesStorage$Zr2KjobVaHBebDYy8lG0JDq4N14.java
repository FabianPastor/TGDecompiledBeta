package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$Zr2KjobVaHBebDYy8lG0JDq4N14 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ long f$5;

    public /* synthetic */ -$$Lambda$MessagesStorage$Zr2KjobVaHBebDYy8lG0JDq4N14(MessagesStorage messagesStorage, long j, long j2, boolean z, int i, long j3) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = j2;
        this.f$3 = z;
        this.f$4 = i;
        this.f$5 = j3;
    }

    public final void run() {
        this.f$0.lambda$processPendingRead$82$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

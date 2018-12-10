package org.telegram.tgnet;

final /* synthetic */ class ConnectionsManager$FirebaseTask$$Lambda$2 implements Runnable {
    private final FirebaseTask arg$1;
    private final boolean arg$2;

    ConnectionsManager$FirebaseTask$$Lambda$2(FirebaseTask firebaseTask, boolean z) {
        this.arg$1 = firebaseTask;
        this.arg$2 = z;
    }

    public void run() {
        this.arg$1.lambda$null$0$ConnectionsManager$FirebaseTask(this.arg$2);
    }
}

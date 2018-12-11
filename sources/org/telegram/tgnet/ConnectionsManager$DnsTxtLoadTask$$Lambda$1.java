package org.telegram.tgnet;

final /* synthetic */ class ConnectionsManager$DnsTxtLoadTask$$Lambda$1 implements Runnable {
    private final DnsTxtLoadTask arg$1;
    private final NativeByteBuffer arg$2;

    ConnectionsManager$DnsTxtLoadTask$$Lambda$1(DnsTxtLoadTask dnsTxtLoadTask, NativeByteBuffer nativeByteBuffer) {
        this.arg$1 = dnsTxtLoadTask;
        this.arg$2 = nativeByteBuffer;
    }

    public void run() {
        this.arg$1.lambda$onPostExecute$1$ConnectionsManager$DnsTxtLoadTask(this.arg$2);
    }
}

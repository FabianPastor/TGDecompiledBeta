package org.telegram.tgnet;

final /* synthetic */ class ConnectionsManager$AzureLoadTask$$Lambda$0 implements Runnable {
    private final AzureLoadTask arg$1;
    private final NativeByteBuffer arg$2;

    ConnectionsManager$AzureLoadTask$$Lambda$0(AzureLoadTask azureLoadTask, NativeByteBuffer nativeByteBuffer) {
        this.arg$1 = azureLoadTask;
        this.arg$2 = nativeByteBuffer;
    }

    public void run() {
        this.arg$1.lambda$onPostExecute$0$ConnectionsManager$AzureLoadTask(this.arg$2);
    }
}

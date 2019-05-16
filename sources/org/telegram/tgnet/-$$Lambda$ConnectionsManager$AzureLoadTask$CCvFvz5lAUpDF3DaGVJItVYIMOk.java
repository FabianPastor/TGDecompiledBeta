package org.telegram.tgnet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConnectionsManager$AzureLoadTask$CCvFvz5lAUpDF3DaGVJItVYIMOk implements Runnable {
    private final /* synthetic */ AzureLoadTask f$0;
    private final /* synthetic */ NativeByteBuffer f$1;

    public /* synthetic */ -$$Lambda$ConnectionsManager$AzureLoadTask$CCvFvz5lAUpDF3DaGVJItVYIMOk(AzureLoadTask azureLoadTask, NativeByteBuffer nativeByteBuffer) {
        this.f$0 = azureLoadTask;
        this.f$1 = nativeByteBuffer;
    }

    public final void run() {
        this.f$0.lambda$onPostExecute$0$ConnectionsManager$AzureLoadTask(this.f$1);
    }
}

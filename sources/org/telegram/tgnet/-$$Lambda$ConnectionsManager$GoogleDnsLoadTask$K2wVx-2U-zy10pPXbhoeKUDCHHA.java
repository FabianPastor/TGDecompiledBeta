package org.telegram.tgnet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConnectionsManager$GoogleDnsLoadTask$K2wVx-2U-zy10pPXbhoeKUDCHHA implements Runnable {
    private final /* synthetic */ GoogleDnsLoadTask f$0;
    private final /* synthetic */ NativeByteBuffer f$1;

    public /* synthetic */ -$$Lambda$ConnectionsManager$GoogleDnsLoadTask$K2wVx-2U-zy10pPXbhoeKUDCHHA(GoogleDnsLoadTask googleDnsLoadTask, NativeByteBuffer nativeByteBuffer) {
        this.f$0 = googleDnsLoadTask;
        this.f$1 = nativeByteBuffer;
    }

    public final void run() {
        this.f$0.lambda$onPostExecute$1$ConnectionsManager$GoogleDnsLoadTask(this.f$1);
    }
}

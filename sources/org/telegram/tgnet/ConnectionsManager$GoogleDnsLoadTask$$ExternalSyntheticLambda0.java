package org.telegram.tgnet;

import org.telegram.tgnet.ConnectionsManager;

public final /* synthetic */ class ConnectionsManager$GoogleDnsLoadTask$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ConnectionsManager.GoogleDnsLoadTask f$0;
    public final /* synthetic */ NativeByteBuffer f$1;

    public /* synthetic */ ConnectionsManager$GoogleDnsLoadTask$$ExternalSyntheticLambda0(ConnectionsManager.GoogleDnsLoadTask googleDnsLoadTask, NativeByteBuffer nativeByteBuffer) {
        this.f$0 = googleDnsLoadTask;
        this.f$1 = nativeByteBuffer;
    }

    public final void run() {
        this.f$0.lambda$onPostExecute$1(this.f$1);
    }
}

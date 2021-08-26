package org.telegram.tgnet;

import org.telegram.tgnet.ConnectionsManager;

public final /* synthetic */ class ConnectionsManager$DnsTxtLoadTask$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ConnectionsManager.DnsTxtLoadTask f$0;
    public final /* synthetic */ NativeByteBuffer f$1;

    public /* synthetic */ ConnectionsManager$DnsTxtLoadTask$$ExternalSyntheticLambda0(ConnectionsManager.DnsTxtLoadTask dnsTxtLoadTask, NativeByteBuffer nativeByteBuffer) {
        this.f$0 = dnsTxtLoadTask;
        this.f$1 = nativeByteBuffer;
    }

    public final void run() {
        this.f$0.lambda$onPostExecute$1(this.f$1);
    }
}

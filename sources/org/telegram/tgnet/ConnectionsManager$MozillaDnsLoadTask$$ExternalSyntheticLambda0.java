package org.telegram.tgnet;

import org.telegram.tgnet.ConnectionsManager;

public final /* synthetic */ class ConnectionsManager$MozillaDnsLoadTask$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ConnectionsManager.MozillaDnsLoadTask f$0;
    public final /* synthetic */ NativeByteBuffer f$1;

    public /* synthetic */ ConnectionsManager$MozillaDnsLoadTask$$ExternalSyntheticLambda0(ConnectionsManager.MozillaDnsLoadTask mozillaDnsLoadTask, NativeByteBuffer nativeByteBuffer) {
        this.f$0 = mozillaDnsLoadTask;
        this.f$1 = nativeByteBuffer;
    }

    public final void run() {
        this.f$0.lambda$onPostExecute$1(this.f$1);
    }
}

package org.telegram.tgnet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConnectionsManager$MozillaDnsLoadTask$_t5C-W7FV9GIIL0quYTnAJF2h8Y implements Runnable {
    private final /* synthetic */ MozillaDnsLoadTask f$0;
    private final /* synthetic */ NativeByteBuffer f$1;

    public /* synthetic */ -$$Lambda$ConnectionsManager$MozillaDnsLoadTask$_t5C-W7FV9GIIL0quYTnAJF2h8Y(MozillaDnsLoadTask mozillaDnsLoadTask, NativeByteBuffer nativeByteBuffer) {
        this.f$0 = mozillaDnsLoadTask;
        this.f$1 = nativeByteBuffer;
    }

    public final void run() {
        this.f$0.lambda$onPostExecute$1$ConnectionsManager$MozillaDnsLoadTask(this.f$1);
    }
}

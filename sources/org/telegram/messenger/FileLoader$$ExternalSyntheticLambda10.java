package org.telegram.messenger;

public final /* synthetic */ class FileLoader$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ FileLoader f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ FileLoader$$ExternalSyntheticLambda10(FileLoader fileLoader, boolean z, String str) {
        this.f$0 = fileLoader;
        this.f$1 = z;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$cancelFileUpload$2(this.f$1, this.f$2);
    }
}

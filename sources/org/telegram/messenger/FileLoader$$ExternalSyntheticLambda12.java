package org.telegram.messenger;

public final /* synthetic */ class FileLoader$$ExternalSyntheticLambda12 implements Runnable {
    public final /* synthetic */ FileLoader f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ long f$4;

    public /* synthetic */ FileLoader$$ExternalSyntheticLambda12(FileLoader fileLoader, boolean z, String str, long j, long j2) {
        this.f$0 = fileLoader;
        this.f$1 = z;
        this.f$2 = str;
        this.f$3 = j;
        this.f$4 = j2;
    }

    public final void run() {
        this.f$0.lambda$checkUploadNewDataAvailable$3(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

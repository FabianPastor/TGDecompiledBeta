package org.telegram.messenger;

public final /* synthetic */ class FileLoader$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ FileLoader f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ FileLoaderPriorityQueue f$2;

    public /* synthetic */ FileLoader$$ExternalSyntheticLambda3(FileLoader fileLoader, String str, FileLoaderPriorityQueue fileLoaderPriorityQueue) {
        this.f$0 = fileLoader;
        this.f$1 = str;
        this.f$2 = fileLoaderPriorityQueue;
    }

    public final void run() {
        this.f$0.lambda$checkDownloadQueue$11(this.f$1, this.f$2);
    }
}

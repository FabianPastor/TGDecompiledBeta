package org.telegram.messenger;

final /* synthetic */ class FileLoader$$Lambda$4 implements Runnable {
    private final FileLoader arg$1;
    private final boolean arg$2;

    FileLoader$$Lambda$4(FileLoader fileLoader, boolean z) {
        this.arg$1 = fileLoader;
        this.arg$2 = z;
    }

    public void run() {
        this.arg$1.lambda$onNetworkChanged$4$FileLoader(this.arg$2);
    }
}

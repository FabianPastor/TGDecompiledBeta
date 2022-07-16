package org.telegram.messenger;

public final /* synthetic */ class DispatchQueuePoolBackground$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ DispatchQueuePoolBackground f$0;
    public final /* synthetic */ DispatchQueue f$1;

    public /* synthetic */ DispatchQueuePoolBackground$$ExternalSyntheticLambda3(DispatchQueuePoolBackground dispatchQueuePoolBackground, DispatchQueue dispatchQueue) {
        this.f$0 = dispatchQueuePoolBackground;
        this.f$1 = dispatchQueue;
    }

    public final void run() {
        this.f$0.lambda$execute$0(this.f$1);
    }
}

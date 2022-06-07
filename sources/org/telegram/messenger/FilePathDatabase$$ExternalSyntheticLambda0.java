package org.telegram.messenger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class FilePathDatabase$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ArrayList f$0;
    public final /* synthetic */ CountDownLatch f$1;

    public /* synthetic */ FilePathDatabase$$ExternalSyntheticLambda0(ArrayList arrayList, CountDownLatch countDownLatch) {
        this.f$0 = arrayList;
        this.f$1 = countDownLatch;
    }

    public final void run() {
        FilePathDatabase.lambda$checkMediaExistance$3(this.f$0, this.f$1);
    }
}

package org.telegram.ui;

import org.telegram.messenger.AccountInstance;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda31 implements Runnable {
    public final /* synthetic */ AccountInstance f$0;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda31(AccountInstance accountInstance) {
        this.f$0 = accountInstance;
    }

    public final void run() {
        this.f$0.getDownloadController().loadDownloadingFiles();
    }
}

package org.telegram.ui;

import org.telegram.messenger.AccountInstance;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda26 implements Runnable {
    public final /* synthetic */ AccountInstance f$0;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda26(AccountInstance accountInstance) {
        this.f$0 = accountInstance;
    }

    public final void run() {
        this.f$0.getMessagesStorage().loadDownloadingFiles();
    }
}

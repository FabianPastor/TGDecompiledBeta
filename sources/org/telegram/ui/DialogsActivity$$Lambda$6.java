package org.telegram.ui;

import org.telegram.messenger.MessagesStorage.IntCallback;

final /* synthetic */ class DialogsActivity$$Lambda$6 implements IntCallback {
    private final DialogsActivity arg$1;

    DialogsActivity$$Lambda$6(DialogsActivity dialogsActivity) {
        this.arg$1 = dialogsActivity;
    }

    public void run(int i) {
        this.arg$1.lambda$askForPermissons$6$DialogsActivity(i);
    }
}

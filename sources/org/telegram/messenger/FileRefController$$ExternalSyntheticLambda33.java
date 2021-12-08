package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda33 implements Runnable {
    public final /* synthetic */ TLRPC.TL_theme f$0;

    public /* synthetic */ FileRefController$$ExternalSyntheticLambda33(TLRPC.TL_theme tL_theme) {
        this.f$0 = tL_theme;
    }

    public final void run() {
        Theme.setThemeFileReference(this.f$0);
    }
}

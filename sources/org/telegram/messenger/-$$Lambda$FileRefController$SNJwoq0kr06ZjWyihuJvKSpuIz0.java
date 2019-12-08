package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.ui.ActionBar.Theme;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileRefController$SNJwoq0kr06ZjWyihuJvKSpuIz0 implements Runnable {
    private final /* synthetic */ TL_theme f$0;

    public /* synthetic */ -$$Lambda$FileRefController$SNJwoq0kr06ZjWyihuJvKSpuIz0(TL_theme tL_theme) {
        this.f$0 = tL_theme;
    }

    public final void run() {
        Theme.setThemeFileReference(this.f$0);
    }
}

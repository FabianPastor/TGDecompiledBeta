package org.telegram.ui.ActionBar;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ Theme$$ExternalSyntheticLambda0(int i, TLObject tLObject) {
        this.f$0 = i;
        this.f$1 = tLObject;
    }

    public final void run() {
        Theme.lambda$loadRemoteThemes$5(this.f$0, this.f$1);
    }
}

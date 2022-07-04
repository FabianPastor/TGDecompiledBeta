package org.telegram.ui.Components;

import org.telegram.ui.Components.SharedMediaLayout;

public final /* synthetic */ class SharedMediaLayout$$ExternalSyntheticLambda14 implements Runnable {
    public final /* synthetic */ SharedMediaLayout.MediaPage f$0;
    public final /* synthetic */ SharedMediaFastScrollTooltip f$1;

    public /* synthetic */ SharedMediaLayout$$ExternalSyntheticLambda14(SharedMediaLayout.MediaPage mediaPage, SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip) {
        this.f$0 = mediaPage;
        this.f$1 = sharedMediaFastScrollTooltip;
    }

    public final void run() {
        SharedMediaLayout.lambda$showFastScrollHint$14(this.f$0, this.f$1);
    }
}

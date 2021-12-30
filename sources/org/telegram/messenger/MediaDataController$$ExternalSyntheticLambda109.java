package org.telegram.messenger;

import org.telegram.ui.Components.Bulletin;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda109 implements Runnable {
    public final /* synthetic */ Bulletin.UndoButton f$0;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda109(Bulletin.UndoButton undoButton) {
        this.f$0 = undoButton;
    }

    public final void run() {
        this.f$0.undo();
    }
}

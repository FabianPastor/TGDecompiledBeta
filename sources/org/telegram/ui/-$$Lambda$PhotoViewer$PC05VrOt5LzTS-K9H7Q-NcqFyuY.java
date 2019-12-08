package org.telegram.ui;

import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$PCLASSNAMEVrOt5LzTS-K9H7Q-NcqFyuY implements ImageReceiverDelegate {
    private final /* synthetic */ PhotoViewer f$0;

    public /* synthetic */ -$$Lambda$PhotoViewer$PCLASSNAMEVrOt5LzTS-K9H7Q-NcqFyuY(PhotoViewer photoViewer) {
        this.f$0 = photoViewer;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
        this.f$0.lambda$setParentActivity$25$PhotoViewer(imageReceiver, z, z2);
    }
}

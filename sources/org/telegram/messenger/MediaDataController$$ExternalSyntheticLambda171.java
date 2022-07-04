package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda171 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.TL_contacts_topPeers f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda171(MediaDataController mediaDataController, TLRPC.TL_contacts_topPeers tL_contacts_topPeers) {
        this.f$0 = mediaDataController;
        this.f$1 = tL_contacts_topPeers;
    }

    public final void run() {
        this.f$0.m2021lambda$loadHints$107$orgtelegrammessengerMediaDataController(this.f$1);
    }
}

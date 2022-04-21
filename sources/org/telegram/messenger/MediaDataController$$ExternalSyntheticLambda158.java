package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda158 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.TL_emojiKeywordsDifference f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda158(MediaDataController mediaDataController, TLRPC.TL_emojiKeywordsDifference tL_emojiKeywordsDifference, String str) {
        this.f$0 = mediaDataController;
        this.f$1 = tL_emojiKeywordsDifference;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.m807x53cvar_cc(this.f$1, this.f$2);
    }
}

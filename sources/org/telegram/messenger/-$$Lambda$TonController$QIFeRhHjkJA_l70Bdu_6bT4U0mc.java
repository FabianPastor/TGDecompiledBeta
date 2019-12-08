package org.telegram.messenger;

import drinkless.org.ton.TonApi.InputKey;
import org.telegram.messenger.TonController.DangerousCallback;
import org.telegram.messenger.TonController.ErrorCallback;
import org.telegram.messenger.TonController.TonLibCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$QIFeRhHjkJA_l70Bdu_6bT4U0mc implements TonLibCallback {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ Runnable f$5;
    private final /* synthetic */ Runnable f$6;
    private final /* synthetic */ DangerousCallback f$7;
    private final /* synthetic */ InputKey f$8;
    private final /* synthetic */ ErrorCallback f$9;

    public /* synthetic */ -$$Lambda$TonController$QIFeRhHjkJA_l70Bdu_6bT4U0mc(TonController tonController, String str, String str2, long j, String str3, Runnable runnable, Runnable runnable2, DangerousCallback dangerousCallback, InputKey inputKey, ErrorCallback errorCallback) {
        this.f$0 = tonController;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = j;
        this.f$4 = str3;
        this.f$5 = runnable;
        this.f$6 = runnable2;
        this.f$7 = dangerousCallback;
        this.f$8 = inputKey;
        this.f$9 = errorCallback;
    }

    public final void run(Object obj) {
        this.f$0.lambda$null$33$TonController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, obj);
    }
}

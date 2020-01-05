package org.telegram.messenger;

import drinkless.org.ton.TonApi.InputKey;
import javax.crypto.Cipher;
import org.telegram.messenger.TonController.DangerousCallback;
import org.telegram.messenger.TonController.ErrorCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$pCFvgPRO7LoJVVjLo13UdJcy_fw implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ InputKey f$1;
    private final /* synthetic */ Runnable f$10;
    private final /* synthetic */ Runnable f$11;
    private final /* synthetic */ DangerousCallback f$12;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ Cipher f$3;
    private final /* synthetic */ Runnable f$4;
    private final /* synthetic */ ErrorCallback f$5;
    private final /* synthetic */ String f$6;
    private final /* synthetic */ String f$7;
    private final /* synthetic */ long f$8;
    private final /* synthetic */ String f$9;

    public /* synthetic */ -$$Lambda$TonController$pCFvgPRO7LoJVVjLo13UdJcy_fw(TonController tonController, InputKey inputKey, String str, Cipher cipher, Runnable runnable, ErrorCallback errorCallback, String str2, String str3, long j, String str4, Runnable runnable2, Runnable runnable3, DangerousCallback dangerousCallback) {
        this.f$0 = tonController;
        this.f$1 = inputKey;
        this.f$2 = str;
        this.f$3 = cipher;
        this.f$4 = runnable;
        this.f$5 = errorCallback;
        this.f$6 = str2;
        this.f$7 = str3;
        this.f$8 = j;
        this.f$9 = str4;
        this.f$10 = runnable2;
        this.f$11 = runnable3;
        this.f$12 = dangerousCallback;
    }

    public final void run() {
        this.f$0.lambda$sendGrams$40$TonController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
    }
}

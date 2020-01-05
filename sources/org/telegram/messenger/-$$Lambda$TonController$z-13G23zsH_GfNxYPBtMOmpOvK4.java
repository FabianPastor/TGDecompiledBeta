package org.telegram.messenger;

import drinkless.org.ton.TonApi.QueryInfo;
import org.telegram.messenger.TonController.ErrorCallback;
import org.telegram.messenger.TonController.TonLibCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$z-13G23zsH_GfNxYPBtMOmpOvK4 implements TonLibCallback {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ QueryInfo f$5;
    private final /* synthetic */ Runnable f$6;
    private final /* synthetic */ Runnable f$7;
    private final /* synthetic */ ErrorCallback f$8;

    public /* synthetic */ -$$Lambda$TonController$z-13G23zsH_GfNxYPBtMOmpOvK4(TonController tonController, String str, String str2, long j, String str3, QueryInfo queryInfo, Runnable runnable, Runnable runnable2, ErrorCallback errorCallback) {
        this.f$0 = tonController;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = j;
        this.f$4 = str3;
        this.f$5 = queryInfo;
        this.f$6 = runnable;
        this.f$7 = runnable2;
        this.f$8 = errorCallback;
    }

    public final void run(Object obj) {
        this.f$0.lambda$null$37$TonController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, obj);
    }
}

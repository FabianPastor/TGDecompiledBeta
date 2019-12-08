package org.telegram.messenger;

import drinkless.org.ton.Client.ResultHandler;
import drinkless.org.ton.TonApi.Function;
import drinkless.org.ton.TonApi.Object;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.TonController.TonLibCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$v5N5zKbXFLbODGFjRDOM7843r2s implements ResultHandler {
    private final /* synthetic */ TonLibCallback f$0;
    private final /* synthetic */ Function f$1;
    private final /* synthetic */ Object[] f$2;
    private final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ -$$Lambda$TonController$v5N5zKbXFLbODGFjRDOM7843r2s(TonLibCallback tonLibCallback, Function function, Object[] objArr, CountDownLatch countDownLatch) {
        this.f$0 = tonLibCallback;
        this.f$1 = function;
        this.f$2 = objArr;
        this.f$3 = countDownLatch;
    }

    public final void onResult(Object object) {
        TonController.lambda$sendRequest$2(this.f$0, this.f$1, this.f$2, this.f$3, object);
    }
}

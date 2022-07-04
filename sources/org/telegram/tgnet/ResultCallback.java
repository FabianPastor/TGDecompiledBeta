package org.telegram.tgnet;

import org.telegram.tgnet.TLRPC;

public interface ResultCallback<T> {
    void onComplete(T t);

    void onError(Throwable th);

    void onError(TLRPC.TL_error tL_error);

    /* renamed from: org.telegram.tgnet.ResultCallback$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$onError(ResultCallback _this, TLRPC.TL_error error) {
        }

        public static void $default$onError(ResultCallback _this, Throwable throwable) {
        }
    }
}

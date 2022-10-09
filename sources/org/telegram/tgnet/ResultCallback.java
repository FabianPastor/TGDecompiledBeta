package org.telegram.tgnet;
/* loaded from: classes.dex */
public interface ResultCallback<T> {

    /* renamed from: org.telegram.tgnet.ResultCallback$-CC  reason: invalid class name */
    /* loaded from: classes.dex */
    public final /* synthetic */ class CC {
        public static void $default$onError(ResultCallback resultCallback, Throwable th) {
        }

        public static void $default$onError(ResultCallback resultCallback, TLRPC$TL_error tLRPC$TL_error) {
        }
    }

    void onComplete(T t);

    void onError(TLRPC$TL_error tLRPC$TL_error);
}

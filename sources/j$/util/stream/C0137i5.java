package j$.util.stream;

import j$.util.function.ToLongFunction;

/* renamed from: j$.util.stream.i5  reason: case insensitive filesystem */
class CLASSNAMEi5 extends U2 {
    final /* synthetic */ ToLongFunction m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEi5(CLASSNAMEu5 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, ToLongFunction toLongFunction) {
        super(upstream, inputShape, opFlags);
        this.m = toLongFunction;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEh5(this, sink);
    }
}

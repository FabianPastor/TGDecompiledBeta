package j$.util.stream;

import j$.util.function.ToDoubleFunction;

/* renamed from: j$.util.stream.k5  reason: case insensitive filesystem */
class CLASSNAMEk5 extends K1 {
    final /* synthetic */ ToDoubleFunction m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEk5(CLASSNAMEu5 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, ToDoubleFunction toDoubleFunction) {
        super(upstream, inputShape, opFlags);
        this.m = toDoubleFunction;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEj5(this, sink);
    }
}

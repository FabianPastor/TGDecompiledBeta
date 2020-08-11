package j$.util.stream;

import j$.util.function.F;

/* renamed from: j$.util.stream.o2  reason: case insensitive filesystem */
class CLASSNAMEo2 extends U2 {
    final /* synthetic */ F m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEo2(CLASSNAMEz2 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, F f) {
        super(upstream, inputShape, opFlags);
        this.m = f;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEn2(this, sink);
    }
}

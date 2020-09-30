package j$.util.stream;

import j$.util.function.B;

/* renamed from: j$.util.stream.f2  reason: case insensitive filesystem */
class CLASSNAMEf2 extends CLASSNAMEy2 {
    final /* synthetic */ B m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEf2(CLASSNAMEz2 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, B b) {
        super(upstream, inputShape, opFlags);
        this.m = b;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEe2(this, sink);
    }
}

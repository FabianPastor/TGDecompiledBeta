package j$.util.stream;

import j$.util.function.Predicate;

/* renamed from: j$.util.stream.c5  reason: case insensitive filesystem */
class CLASSNAMEc5 extends CLASSNAMEt5 {
    final /* synthetic */ Predicate m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEc5(CLASSNAMEu5 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, Predicate predicate) {
        super(upstream, inputShape, opFlags);
        this.m = predicate;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEb5(this, sink);
    }
}

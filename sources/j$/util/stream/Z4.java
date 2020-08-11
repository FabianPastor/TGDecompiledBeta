package j$.util.stream;

import j$.util.function.Consumer;

class Z4 extends CLASSNAMEt5 {
    final /* synthetic */ Consumer m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    Z4(CLASSNAMEu5 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, Consumer consumer) {
        super(upstream, inputShape, opFlags);
        this.m = consumer;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new Y4(this, sink);
    }
}

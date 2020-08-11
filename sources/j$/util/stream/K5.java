package j$.util.stream;

import j$.util.S;
import j$.util.Spliterator;
import j$.util.function.C;

class K5 extends CLASSNAMEx2 {
    final /* synthetic */ long m;
    final /* synthetic */ long n;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    K5(CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, long j, long j2) {
        super(upstream, inputShape, opFlags);
        this.m = j;
        this.n = j2;
    }

    /* access modifiers changed from: package-private */
    public S b1(S s, long skip, long limit, long sizeIfKnown) {
        if (skip <= sizeIfKnown) {
            long j = sizeIfKnown - skip;
            if (limit >= 0) {
                j = Math.min(limit, j);
            }
            limit = j;
            skip = 0;
        }
        return new U6(s, skip, limit);
    }

    /* access modifiers changed from: package-private */
    public Spliterator H0(CLASSNAMEq4 helper, Spliterator spliterator) {
        long size = helper.p0(spliterator);
        if (size <= 0) {
            Spliterator spliterator2 = spliterator;
        } else if (spliterator.hasCharacteristics(16384)) {
            long j = this.m;
            return new O6((S) helper.v0(spliterator), j, Q5.f(j, this.n));
        }
        if (!CLASSNAMEu6.ORDERED.f(helper.r0())) {
            return b1((S) helper.v0(spliterator), this.m, this.n, size);
        }
        return ((CLASSNAMEt3) new P5(this, helper, spliterator, CLASSNAMEr0.a, this.m, this.n).invoke()).spliterator();
    }

    static /* synthetic */ Integer[] a1(int x$0) {
        return new Integer[x$0];
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEt3 G0(CLASSNAMEq4 helper, Spliterator spliterator, C c) {
        long size = helper.p0(spliterator);
        if (size <= 0) {
            CLASSNAMEq4 q4Var = helper;
            Spliterator spliterator2 = spliterator;
        } else if (spliterator.hasCharacteristics(16384)) {
            return CLASSNAMEp4.h(helper, Q5.n(helper.q0(), spliterator, this.m, this.n), true);
        } else {
            CLASSNAMEq4 q4Var2 = helper;
        }
        if (!CLASSNAMEu6.ORDERED.f(helper.r0())) {
            return CLASSNAMEp4.h(this, b1((S) helper.v0(spliterator), this.m, this.n, size), true);
        }
        return (CLASSNAMEt3) new P5(this, helper, spliterator, c, this.m, this.n).invoke();
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new J5(this, sink);
    }
}

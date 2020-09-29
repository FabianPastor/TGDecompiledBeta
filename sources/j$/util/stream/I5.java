package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;

class I5 extends CLASSNAMEs5 {
    final /* synthetic */ long m;
    final /* synthetic */ long n;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    I5(CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, long j, long j2) {
        super(upstream, inputShape, opFlags);
        this.m = j;
        this.n = j2;
    }

    /* access modifiers changed from: package-private */
    public Spliterator R0(Spliterator spliterator, long skip, long limit, long sizeIfKnown) {
        if (skip <= sizeIfKnown) {
            long j = sizeIfKnown - skip;
            if (limit >= 0) {
                j = Math.min(limit, j);
            }
            limit = j;
            skip = 0;
        }
        return new X6(spliterator, skip, limit);
    }

    /* access modifiers changed from: package-private */
    public Spliterator H0(CLASSNAMEq4 helper, Spliterator spliterator) {
        long size = helper.p0(spliterator);
        if (size <= 0) {
            Spliterator spliterator2 = spliterator;
        } else if (spliterator.hasCharacteristics(16384)) {
            Spliterator v0 = helper.v0(spliterator);
            long j = this.m;
            return new R6(v0, j, Q5.f(j, this.n));
        }
        if (!CLASSNAMEu6.ORDERED.K(helper.r0())) {
            return R0(helper.v0(spliterator), this.m, this.n, size);
        }
        return ((CLASSNAMEt3) new P5(this, helper, spliterator, Q5.g(), this.m, this.n).invoke()).spliterator();
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEt3 G0(CLASSNAMEq4 helper, Spliterator spliterator, C c) {
        C c2 = c;
        long size = helper.p0(spliterator);
        if (size <= 0) {
            CLASSNAMEq4 q4Var = helper;
            Spliterator spliterator2 = spliterator;
        } else if (spliterator.hasCharacteristics(16384)) {
            return CLASSNAMEp4.f(helper, Q5.n(helper.q0(), spliterator, this.m, this.n), true, c2);
        } else {
            CLASSNAMEq4 q4Var2 = helper;
        }
        if (!CLASSNAMEu6.ORDERED.K(helper.r0())) {
            return CLASSNAMEp4.f(this, R0(helper.v0(spliterator), this.m, this.n, size), true, c2);
        }
        return (CLASSNAMEt3) new P5(this, helper, spliterator, c, this.m, this.n).invoke();
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new H5(this, sink);
    }
}

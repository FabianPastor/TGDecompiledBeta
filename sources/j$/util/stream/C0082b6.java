package j$.util.stream;

import j$.util.Comparator;
import j$.util.Spliterator;
import j$.util.function.C;
import java.util.Arrays;
import java.util.Comparator;

/* renamed from: j$.util.stream.b6  reason: case insensitive filesystem */
final class CLASSNAMEb6 extends CLASSNAMEs5 {
    private final boolean m;
    private final Comparator n;

    CLASSNAMEb6(CLASSNAMEh1 upstream) {
        super(upstream, CLASSNAMEv6.REFERENCE, CLASSNAMEu6.v | CLASSNAMEu6.t);
        this.m = true;
        this.n = Comparator.CC.l();
    }

    CLASSNAMEb6(CLASSNAMEh1 upstream, java.util.Comparator comparator) {
        super(upstream, CLASSNAMEv6.REFERENCE, CLASSNAMEu6.v | CLASSNAMEu6.u);
        this.m = false;
        comparator.getClass();
        this.n = comparator;
    }

    public G5 J0(int flags, G5 sink) {
        sink.getClass();
        if (CLASSNAMEu6.SORTED.f(flags) && this.m) {
            return sink;
        }
        if (CLASSNAMEu6.SIZED.f(flags)) {
            return new CLASSNAMEg6(sink, this.n);
        }
        return new CLASSNAMEc6(sink, this.n);
    }

    public CLASSNAMEt3 G0(CLASSNAMEq4 helper, Spliterator spliterator, C c) {
        if (CLASSNAMEu6.SORTED.f(helper.r0()) && this.m) {
            return helper.e(spliterator, false, c);
        }
        T[] flattenedData = helper.e(spliterator, true, c).x(c);
        Arrays.sort(flattenedData, this.n);
        return CLASSNAMEp4.z(flattenedData);
    }
}

package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import java.util.Arrays;

final class Y5 extends J1 {
    Y5(CLASSNAMEh1 upstream) {
        super(upstream, CLASSNAMEv6.DOUBLE_VALUE, CLASSNAMEu6.v | CLASSNAMEu6.t);
    }

    public G5 J0(int flags, G5 sink) {
        sink.getClass();
        if (CLASSNAMEu6.SORTED.f(flags)) {
            return sink;
        }
        if (CLASSNAMEu6.SIZED.f(flags)) {
            return new CLASSNAMEd6(sink);
        }
        return new V5(sink);
    }

    public CLASSNAMEt3 G0(CLASSNAMEq4 helper, Spliterator spliterator, C c) {
        if (CLASSNAMEu6.SORTED.f(helper.r0())) {
            return helper.e(spliterator, false, c);
        }
        double[] content = (double[]) ((CLASSNAMEm3) helper.e(spliterator, true, c)).i();
        Arrays.sort(content);
        return CLASSNAMEp4.v(content);
    }
}
package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import java.util.Arrays;

/* renamed from: j$.util.stream.a6  reason: case insensitive filesystem */
final class CLASSNAMEa6 extends T2 {
    CLASSNAMEa6(CLASSNAMEh1 upstream) {
        super(upstream, CLASSNAMEv6.LONG_VALUE, CLASSNAMEu6.v | CLASSNAMEu6.t);
    }

    public G5 J0(int flags, G5 sink) {
        sink.getClass();
        if (CLASSNAMEu6.SORTED.K(flags)) {
            return sink;
        }
        if (CLASSNAMEu6.SIZED.K(flags)) {
            return new CLASSNAMEf6(sink);
        }
        return new X5(sink);
    }

    public CLASSNAMEt3 G0(CLASSNAMEq4 helper, Spliterator spliterator, C c) {
        if (CLASSNAMEu6.SORTED.K(helper.r0())) {
            return helper.e(spliterator, false, c);
        }
        long[] content = (long[]) ((CLASSNAMEq3) helper.e(spliterator, true, c)).i();
        Arrays.sort(content);
        return CLASSNAMEp4.x(content);
    }
}

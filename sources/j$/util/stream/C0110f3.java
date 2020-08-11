package j$.util.stream;

import j$.util.function.CLASSNAMEv;
import j$.util.function.D;
import j$.util.function.L;
import j$.util.function.Predicate;

/* renamed from: j$.util.stream.f3  reason: case insensitive filesystem */
final class CLASSNAMEf3 {
    public static f7 h(Predicate predicate, CLASSNAMEc3 matchKind) {
        predicate.getClass();
        matchKind.getClass();
        return new CLASSNAMEd3(CLASSNAMEv6.REFERENCE, matchKind, new CLASSNAMEd0(matchKind, predicate));
    }

    static /* synthetic */ CLASSNAMEb3 d(CLASSNAMEc3 matchKind, Predicate predicate) {
        return new X2(matchKind, predicate);
    }

    public static f7 f(D predicate, CLASSNAMEc3 matchKind) {
        predicate.getClass();
        matchKind.getClass();
        return new CLASSNAMEd3(CLASSNAMEv6.INT_VALUE, matchKind, new CLASSNAMEe0(matchKind, predicate));
    }

    static /* synthetic */ CLASSNAMEb3 b(CLASSNAMEc3 matchKind, D predicate) {
        return new Y2(matchKind, predicate);
    }

    public static f7 g(L predicate, CLASSNAMEc3 matchKind) {
        predicate.getClass();
        matchKind.getClass();
        return new CLASSNAMEd3(CLASSNAMEv6.LONG_VALUE, matchKind, new CLASSNAMEf0(matchKind, predicate));
    }

    static /* synthetic */ CLASSNAMEb3 c(CLASSNAMEc3 matchKind, L predicate) {
        return new Z2(matchKind, predicate);
    }

    public static f7 e(CLASSNAMEv predicate, CLASSNAMEc3 matchKind) {
        predicate.getClass();
        matchKind.getClass();
        return new CLASSNAMEd3(CLASSNAMEv6.DOUBLE_VALUE, matchKind, new CLASSNAMEc0(matchKind, predicate));
    }

    static /* synthetic */ CLASSNAMEb3 a(CLASSNAMEc3 matchKind, CLASSNAMEv predicate) {
        return new CLASSNAMEa3(matchKind, predicate);
    }
}

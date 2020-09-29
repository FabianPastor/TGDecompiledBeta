package j$.util.stream;

import j$.util.P;
import j$.util.S;
import j$.util.Spliterator;
import j$.util.U;

public final class b7 {
    public static Stream d(Spliterator spliterator, boolean parallel) {
        spliterator.getClass();
        return new CLASSNAMEr5(spliterator, CLASSNAMEu6.x(spliterator), parallel);
    }

    public static A2 b(S spliterator, boolean parallel) {
        return new CLASSNAMEw2(spliterator, CLASSNAMEu6.x(spliterator), parallel);
    }

    public static W2 c(U spliterator, boolean parallel) {
        return new S2(spliterator, CLASSNAMEu6.x(spliterator), parallel);
    }

    public static M1 a(P spliterator, boolean parallel) {
        return new I1(spliterator, CLASSNAMEu6.x(spliterator), parallel);
    }
}

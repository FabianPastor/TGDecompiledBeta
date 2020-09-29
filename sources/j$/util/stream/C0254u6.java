package j$.util.stream;

import j$.util.Spliterator;
import java.util.EnumMap;
import java.util.Map;

/* renamed from: j$.util.stream.u6  reason: case insensitive filesystem */
enum CLASSNAMEu6 {
    DISTINCT(0, r1),
    SORTED(1, r1),
    ORDERED(2, r1),
    SIZED(3, r1),
    SHORT_CIRCUIT(12, r1);
    
    static final int k = 0;
    static final int l = 0;
    static final int m = 0;
    private static final int n = 0;
    private static final int o = 0;
    private static final int p = 0;
    static final int q = 0;
    static final int r = 0;
    static final int s = 0;
    static final int t = 0;
    static final int u = 0;
    static final int v = 0;
    static final int w = 0;
    static final int x = 0;
    static final int y = 0;
    static final int z = 0;
    private final Map a;
    private final int b;
    private final int c;
    private final int d;
    private final int e;

    static {
        k = r(CLASSNAMEt6.SPLITERATOR);
        l = r(CLASSNAMEt6.STREAM);
        m = r(CLASSNAMEt6.OP);
        r(CLASSNAMEt6.TERMINAL_OP);
        r(CLASSNAMEt6.UPSTREAM_TERMINAL_OP);
        n = p();
        int i = l;
        o = i;
        int i2 = i << 1;
        p = i2;
        q = i | i2;
        CLASSNAMEu6 u6Var = DISTINCT;
        r = u6Var.c;
        s = u6Var.d;
        CLASSNAMEu6 u6Var2 = SORTED;
        t = u6Var2.c;
        u = u6Var2.d;
        CLASSNAMEu6 u6Var3 = ORDERED;
        v = u6Var3.c;
        w = u6Var3.d;
        CLASSNAMEu6 u6Var4 = SIZED;
        x = u6Var4.c;
        y = u6Var4.d;
        z = SHORT_CIRCUIT.c;
    }

    private static CLASSNAMEs6 M(CLASSNAMEt6 t2) {
        CLASSNAMEs6 s6Var = new CLASSNAMEs6(new EnumMap(CLASSNAMEt6.class));
        s6Var.d(t2);
        return s6Var;
    }

    private CLASSNAMEu6(int position, CLASSNAMEs6 maskBuilder) {
        this.a = maskBuilder.a();
        int position2 = position * 2;
        this.b = position2;
        this.c = 1 << position2;
        this.d = 2 << position2;
        this.e = 3 << position2;
    }

    /* access modifiers changed from: package-private */
    public boolean K(int flags) {
        return (this.e & flags) == this.c;
    }

    /* access modifiers changed from: package-private */
    public boolean L(int flags) {
        int i = this.e;
        return (flags & i) == i;
    }

    private static int r(CLASSNAMEt6 t2) {
        int mask = 0;
        for (CLASSNAMEu6 flag : values()) {
            mask |= ((Integer) flag.a.get(t2)).intValue() << flag.b;
        }
        return mask;
    }

    private static int p() {
        int mask = 0;
        for (CLASSNAMEu6 flag : values()) {
            mask |= flag.e;
        }
        return mask;
    }

    private static int A(int flags) {
        if (flags == 0) {
            return n;
        }
        return ((((o & flags) << 1) | flags) | ((p & flags) >> 1)) ^ -1;
    }

    static int i(int newStreamOrOpFlags, int prevCombOpFlags) {
        return (A(newStreamOrOpFlags) & prevCombOpFlags) | newStreamOrOpFlags;
    }

    static int P(int combOpFlags) {
        return ((combOpFlags ^ -1) >> 1) & o & combOpFlags;
    }

    static int O(int streamFlags) {
        return k & streamFlags;
    }

    static int x(Spliterator spliterator) {
        int characteristics = spliterator.characteristics();
        if ((characteristics & 4) == 0 || spliterator.getComparator() == null) {
            return k & characteristics;
        }
        return k & characteristics & -5;
    }
}

package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.v;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/* renamed from: j$.util.stream.r1  reason: case insensitive filesystem */
class CLASSNAMEr1 extends CLASSNAMEj5 {
    CLASSNAMEr1(CLASSNAMEh1 h1Var, CLASSNAMEh6 h6Var, int i) {
        super(h1Var, h6Var, i);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEl3 D0(CLASSNAMEi4 i4Var, Spliterator spliterator, v vVar) {
        if (CLASSNAMEg6.DISTINCT.d(i4Var.r0())) {
            return i4Var.o0(spliterator, false, vVar);
        }
        if (CLASSNAMEg6.ORDERED.d(i4Var.r0())) {
            return K0(i4Var, spliterator);
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        new W1(new CLASSNAMEm(atomicBoolean, concurrentHashMap), false).c(i4Var, spliterator);
        Set keySet = concurrentHashMap.keySet();
        if (atomicBoolean.get()) {
            HashSet hashSet = new HashSet(keySet);
            hashSet.add((Object) null);
            keySet = hashSet;
        }
        return new CLASSNAMEp3(keySet);
    }

    /* access modifiers changed from: package-private */
    public Spliterator E0(CLASSNAMEi4 i4Var, Spliterator spliterator) {
        return CLASSNAMEg6.DISTINCT.d(i4Var.r0()) ? i4Var.v0(spliterator) : CLASSNAMEg6.ORDERED.d(i4Var.r0()) ? ((CLASSNAMEp3) K0(i4Var, spliterator)).spliterator() : new q6(i4Var.v0(spliterator));
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEt5 G0(int i, CLASSNAMEt5 t5Var) {
        t5Var.getClass();
        if (CLASSNAMEg6.DISTINCT.d(i)) {
            return t5Var;
        }
        return CLASSNAMEg6.SORTED.d(i) ? new CLASSNAMEp1(this, t5Var) : new CLASSNAMEq1(this, t5Var);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEl3 K0(CLASSNAMEi4 i4Var, Spliterator spliterator) {
        A a = A.a;
        M0 m0 = M0.a;
        return new CLASSNAMEp3((Collection) new CLASSNAMEx4(CLASSNAMEh6.REFERENCE, CLASSNAMEi.a, m0, a).c(i4Var, spliterator));
    }
}

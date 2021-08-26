package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.BiConsumer;
import j$.util.function.l;
import j$.util.y;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/* renamed from: j$.util.stream.s  reason: case insensitive filesystem */
class CLASSNAMEs extends CLASSNAMEd3 {
    CLASSNAMEs(CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i) {
        super(cVar, f4Var, i);
    }

    /* access modifiers changed from: package-private */
    public B1 E0(CLASSNAMEz2 z2Var, y yVar, l lVar) {
        if (CLASSNAMEe4.DISTINCT.d(z2Var.s0())) {
            return z2Var.p0(yVar, false, lVar);
        }
        if (CLASSNAMEe4.ORDERED.d(z2Var.s0())) {
            return L0(z2Var, yVar);
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        new CLASSNAMEn0(new CLASSNAMEo(atomicBoolean, concurrentHashMap), false).c(z2Var, yVar);
        Set keySet = concurrentHashMap.keySet();
        if (atomicBoolean.get()) {
            HashSet hashSet = new HashSet(keySet);
            hashSet.add((Object) null);
            keySet = hashSet;
        }
        return new F1(keySet);
    }

    /* access modifiers changed from: package-private */
    public y F0(CLASSNAMEz2 z2Var, y yVar) {
        return CLASSNAMEe4.DISTINCT.d(z2Var.s0()) ? z2Var.w0(yVar) : CLASSNAMEe4.ORDERED.d(z2Var.s0()) ? ((F1) L0(z2Var, yVar)).spliterator() : new CLASSNAMEn4(z2Var.w0(yVar));
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEn3 H0(int i, CLASSNAMEn3 n3Var) {
        n3Var.getClass();
        return CLASSNAMEe4.DISTINCT.d(i) ? n3Var : CLASSNAMEe4.SORTED.d(i) ? new CLASSNAMEq(this, n3Var) : new r(this, n3Var);
    }

    /* access modifiers changed from: package-private */
    public B1 L0(CLASSNAMEz2 z2Var, y yVar) {
        CLASSNAMEp pVar = CLASSNAMEp.a;
        CLASSNAMEm mVar = CLASSNAMEm.a;
        return new F1((Collection) new A2(CLASSNAMEf4.REFERENCE, (BiConsumer) CLASSNAMEn.a, (BiConsumer) mVar, (j$.util.function.y) pVar).c(z2Var, yVar));
    }
}

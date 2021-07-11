package j$.util.stream;

import j$.util.Spliterator;
import j$.util.stream.CLASSNAMEi1;
import java.util.concurrent.atomic.AtomicReference;

/* renamed from: j$.util.stream.i1  reason: case insensitive filesystem */
abstract class CLASSNAMEi1<P_IN, P_OUT, R, K extends CLASSNAMEi1<P_IN, P_OUT, R, K>> extends CLASSNAMEk1<P_IN, P_OUT, R, K> {
    protected final AtomicReference h;
    protected volatile boolean i;

    protected CLASSNAMEi1(T1 t1, Spliterator spliterator) {
        super(t1, spliterator);
        this.h = new AtomicReference((Object) null);
    }

    protected CLASSNAMEi1(CLASSNAMEi1 i1Var, Spliterator spliterator) {
        super((CLASSNAMEk1) i1Var, spliterator);
        this.h = i1Var.h;
    }

    public Object b() {
        if (!e()) {
            return super.b();
        }
        Object obj = this.h.get();
        return obj == null ? k() : obj;
    }

    public void compute() {
        Object obj;
        Spliterator trySplit;
        Spliterator spliterator = this.c;
        long estimateSize = spliterator.estimateSize();
        long j = this.d;
        if (j == 0) {
            j = CLASSNAMEk1.h(estimateSize);
            this.d = j;
        }
        boolean z = false;
        AtomicReference atomicReference = this.h;
        CLASSNAMEi1 i1Var = this;
        while (true) {
            obj = atomicReference.get();
            if (obj != null) {
                break;
            }
            boolean z2 = i1Var.i;
            if (!z2) {
                CLASSNAMEk1 c = i1Var.c();
                while (true) {
                    CLASSNAMEi1 i1Var2 = (CLASSNAMEi1) c;
                    if (z2 || i1Var2 == null) {
                        break;
                    }
                    z2 = i1Var2.i;
                    c = i1Var2.c();
                }
            }
            if (z2) {
                obj = i1Var.k();
                break;
            } else if (estimateSize <= j || (trySplit = spliterator.trySplit()) == null) {
                obj = i1Var.a();
            } else {
                CLASSNAMEi1 i1Var3 = (CLASSNAMEi1) i1Var.f(trySplit);
                i1Var.e = i1Var3;
                CLASSNAMEi1 i1Var4 = (CLASSNAMEi1) i1Var.f(spliterator);
                i1Var.f = i1Var4;
                i1Var.setPendingCount(1);
                if (z) {
                    spliterator = trySplit;
                    i1Var = i1Var3;
                    i1Var3 = i1Var4;
                } else {
                    i1Var = i1Var4;
                }
                z = !z;
                i1Var3.fork();
                estimateSize = spliterator.estimateSize();
            }
        }
        i1Var.g(obj);
        i1Var.tryComplete();
    }

    /* access modifiers changed from: protected */
    public void g(Object obj) {
        if (!e()) {
            super.g(obj);
        } else if (obj != null) {
            this.h.compareAndSet((Object) null, obj);
        }
    }

    public Object getRawResult() {
        return b();
    }

    /* access modifiers changed from: protected */
    public void i() {
        this.i = true;
    }

    /* access modifiers changed from: protected */
    public void j() {
        CLASSNAMEi1 i1Var = this;
        for (CLASSNAMEi1 i1Var2 = (CLASSNAMEi1) c(); i1Var2 != null; i1Var2 = (CLASSNAMEi1) i1Var2.c()) {
            if (i1Var2.e == i1Var) {
                CLASSNAMEi1 i1Var3 = (CLASSNAMEi1) i1Var2.f;
                if (!i1Var3.i) {
                    i1Var3.i();
                }
            }
            i1Var = i1Var2;
        }
    }

    /* access modifiers changed from: protected */
    public abstract Object k();

    /* access modifiers changed from: protected */
    public void l(Object obj) {
        if (obj != null) {
            this.h.compareAndSet((Object) null, obj);
        }
    }
}

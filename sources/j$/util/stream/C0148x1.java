package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.stream.R1;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.x1  reason: case insensitive filesystem */
final class CLASSNAMEx1<S, T> extends CountedCompleter<Void> {
    public static final /* synthetic */ int a = 0;
    private final T1 b;
    private Spliterator c;
    private final long d;
    private final ConcurrentHashMap e;
    private final A2 f;
    private final CLASSNAMEx1 g;
    private R1 h;

    protected CLASSNAMEx1(T1 t1, Spliterator spliterator, A2 a2) {
        super((CountedCompleter) null);
        this.b = t1;
        this.c = spliterator;
        this.d = CLASSNAMEk1.h(spliterator.estimateSize());
        this.e = new ConcurrentHashMap(Math.max(16, CLASSNAMEk1.a << 1));
        this.f = a2;
        this.g = null;
    }

    CLASSNAMEx1(CLASSNAMEx1 x1Var, Spliterator spliterator, CLASSNAMEx1 x1Var2) {
        super(x1Var);
        this.b = x1Var.b;
        this.c = spliterator;
        this.d = x1Var.d;
        this.e = x1Var.e;
        this.f = x1Var.f;
        this.g = x1Var2;
    }

    public final void compute() {
        Spliterator trySplit;
        Spliterator spliterator = this.c;
        long j = this.d;
        boolean z = false;
        CLASSNAMEx1 x1Var = this;
        while (spliterator.estimateSize() > j && (trySplit = spliterator.trySplit()) != null) {
            CLASSNAMEx1 x1Var2 = new CLASSNAMEx1(x1Var, trySplit, x1Var.g);
            CLASSNAMEx1 x1Var3 = new CLASSNAMEx1(x1Var, spliterator, x1Var2);
            x1Var.addToPendingCount(1);
            x1Var3.addToPendingCount(1);
            x1Var.e.put(x1Var2, x1Var3);
            if (x1Var.g != null) {
                x1Var2.addToPendingCount(1);
                if (x1Var.e.replace(x1Var.g, x1Var, x1Var2)) {
                    x1Var.addToPendingCount(-1);
                } else {
                    x1Var2.addToPendingCount(-1);
                }
            }
            if (z) {
                spliterator = trySplit;
                x1Var = x1Var2;
                x1Var2 = x1Var3;
            } else {
                x1Var = x1Var3;
            }
            z = !z;
            x1Var2.fork();
        }
        if (x1Var.getPendingCount() > 0) {
            CLASSNAMEz zVar = CLASSNAMEz.a;
            T1 t1 = x1Var.b;
            R1.a s0 = t1.s0(t1.p0(spliterator), zVar);
            CLASSNAMEh1 h1Var = (CLASSNAMEh1) x1Var.b;
            h1Var.getClass();
            s0.getClass();
            h1Var.m0(h1Var.u0(s0), spliterator);
            x1Var.h = s0.a();
            x1Var.c = null;
        }
        x1Var.tryComplete();
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        R1 r1 = this.h;
        if (r1 != null) {
            r1.forEach(this.f);
            this.h = null;
        } else {
            Spliterator spliterator = this.c;
            if (spliterator != null) {
                T1 t1 = this.b;
                A2 a2 = this.f;
                CLASSNAMEh1 h1Var = (CLASSNAMEh1) t1;
                h1Var.getClass();
                a2.getClass();
                h1Var.m0(h1Var.u0(a2), spliterator);
                this.c = null;
            }
        }
        CLASSNAMEx1 x1Var = (CLASSNAMEx1) this.e.remove(this);
        if (x1Var != null) {
            x1Var.tryComplete();
        }
    }
}

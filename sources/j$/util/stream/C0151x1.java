package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.stream.R1;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.x1  reason: case insensitive filesystem */
final class CLASSNAMEx1<S, T> extends CountedCompleter<Void> {
    public static final /* synthetic */ int h = 0;
    private final T1 a;
    private Spliterator b;
    private final long c;
    private final ConcurrentHashMap d;
    private final A2 e;
    private final CLASSNAMEx1 f;
    private R1 g;

    protected CLASSNAMEx1(T1 t1, Spliterator spliterator, A2 a2) {
        super((CountedCompleter) null);
        this.a = t1;
        this.b = spliterator;
        this.c = CLASSNAMEk1.h(spliterator.estimateSize());
        this.d = new ConcurrentHashMap(Math.max(16, CLASSNAMEk1.g << 1));
        this.e = a2;
        this.f = null;
    }

    CLASSNAMEx1(CLASSNAMEx1 x1Var, Spliterator spliterator, CLASSNAMEx1 x1Var2) {
        super(x1Var);
        this.a = x1Var.a;
        this.b = spliterator;
        this.c = x1Var.c;
        this.d = x1Var.d;
        this.e = x1Var.e;
        this.f = x1Var2;
    }

    public final void compute() {
        Spliterator trySplit;
        Spliterator spliterator = this.b;
        long j = this.c;
        boolean z = false;
        CLASSNAMEx1 x1Var = this;
        while (spliterator.estimateSize() > j && (trySplit = spliterator.trySplit()) != null) {
            CLASSNAMEx1 x1Var2 = new CLASSNAMEx1(x1Var, trySplit, x1Var.f);
            CLASSNAMEx1 x1Var3 = new CLASSNAMEx1(x1Var, spliterator, x1Var2);
            x1Var.addToPendingCount(1);
            x1Var3.addToPendingCount(1);
            x1Var.d.put(x1Var2, x1Var3);
            if (x1Var.f != null) {
                x1Var2.addToPendingCount(1);
                if (x1Var.d.replace(x1Var.f, x1Var, x1Var2)) {
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
            T1 t1 = x1Var.a;
            R1.a s0 = t1.s0(t1.p0(spliterator), zVar);
            CLASSNAMEh1 h1Var = (CLASSNAMEh1) x1Var.a;
            h1Var.getClass();
            s0.getClass();
            h1Var.m0(h1Var.u0(s0), spliterator);
            x1Var.g = s0.a();
            x1Var.b = null;
        }
        x1Var.tryComplete();
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        R1 r1 = this.g;
        if (r1 != null) {
            r1.forEach(this.e);
            this.g = null;
        } else {
            Spliterator spliterator = this.b;
            if (spliterator != null) {
                T1 t1 = this.a;
                A2 a2 = this.e;
                CLASSNAMEh1 h1Var = (CLASSNAMEh1) t1;
                h1Var.getClass();
                a2.getClass();
                h1Var.m0(h1Var.u0(a2), spliterator);
                this.b = null;
            }
        }
        CLASSNAMEx1 x1Var = (CLASSNAMEx1) this.d.remove(this);
        if (x1Var != null) {
            x1Var.tryComplete();
        }
    }
}

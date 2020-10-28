package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountedCompleter;

final class Y1 extends CountedCompleter {
    public static final /* synthetic */ int h = 0;
    private final CLASSNAMEi4 a;
    private Spliterator b;
    private final long c;
    private final ConcurrentHashMap d;
    private final CLASSNAMEt5 e;
    private final Y1 f;
    private CLASSNAMEl3 g;

    Y1(Y1 y1, Spliterator spliterator, Y1 y12) {
        super(y1);
        this.a = y1.a;
        this.b = spliterator;
        this.c = y1.c;
        this.d = y1.d;
        this.e = y1.e;
        this.f = y12;
    }

    protected Y1(CLASSNAMEi4 i4Var, Spliterator spliterator, CLASSNAMEt5 t5Var) {
        super((CountedCompleter) null);
        this.a = i4Var;
        this.b = spliterator;
        this.c = CLASSNAMEk1.h(spliterator.estimateSize());
        this.d = new ConcurrentHashMap(Math.max(16, CLASSNAMEk1.g << 1));
        this.e = t5Var;
        this.f = null;
    }

    public final void compute() {
        Spliterator trySplit;
        Spliterator spliterator = this.b;
        long j = this.c;
        boolean z = false;
        Y1 y1 = this;
        while (spliterator.estimateSize() > j && (trySplit = spliterator.trySplit()) != null) {
            Y1 y12 = new Y1(y1, trySplit, y1.f);
            Y1 y13 = new Y1(y1, spliterator, y12);
            y1.addToPendingCount(1);
            y13.addToPendingCount(1);
            y1.d.put(y12, y13);
            if (y1.f != null) {
                y12.addToPendingCount(1);
                if (y1.d.replace(y1.f, y1, y12)) {
                    y1.addToPendingCount(-1);
                } else {
                    y12.addToPendingCount(-1);
                }
            }
            if (z) {
                spliterator = trySplit;
                y1 = y12;
                y12 = y13;
            } else {
                y1 = y13;
            }
            z = !z;
            y12.fork();
        }
        if (y1.getPendingCount() > 0) {
            CLASSNAMEz zVar = CLASSNAMEz.a;
            CLASSNAMEi4 i4Var = y1.a;
            CLASSNAMEg3 s0 = i4Var.s0(i4Var.p0(spliterator), zVar);
            CLASSNAMEh1 h1Var = (CLASSNAMEh1) y1.a;
            h1Var.getClass();
            s0.getClass();
            h1Var.m0(h1Var.u0(s0), spliterator);
            y1.g = s0.a();
            y1.b = null;
        }
        y1.tryComplete();
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        CLASSNAMEl3 l3Var = this.g;
        if (l3Var != null) {
            l3Var.forEach(this.e);
            this.g = null;
        } else {
            Spliterator spliterator = this.b;
            if (spliterator != null) {
                CLASSNAMEi4 i4Var = this.a;
                CLASSNAMEt5 t5Var = this.e;
                CLASSNAMEh1 h1Var = (CLASSNAMEh1) i4Var;
                h1Var.getClass();
                t5Var.getClass();
                h1Var.m0(h1Var.u0(t5Var), spliterator);
                this.b = null;
            }
        }
        Y1 y1 = (Y1) this.d.remove(this);
        if (y1 != null) {
            y1.tryComplete();
        }
    }
}

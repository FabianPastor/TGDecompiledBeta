package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import j$.util.y;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.q0  reason: case insensitive filesystem */
final class CLASSNAMEq0 extends CountedCompleter {
    public static final /* synthetic */ int h = 0;
    private final CLASSNAMEz2 a;
    private y b;
    private final long c;
    private final ConcurrentHashMap d;
    private final CLASSNAMEn3 e;
    private final CLASSNAMEq0 f;
    private B1 g;

    CLASSNAMEq0(CLASSNAMEq0 q0Var, y yVar, CLASSNAMEq0 q0Var2) {
        super(q0Var);
        this.a = q0Var.a;
        this.b = yVar;
        this.c = q0Var.c;
        this.d = q0Var.d;
        this.e = q0Var.e;
        this.f = q0Var2;
    }

    protected CLASSNAMEq0(CLASSNAMEz2 z2Var, y yVar, CLASSNAMEn3 n3Var) {
        super((CountedCompleter) null);
        this.a = z2Var;
        this.b = yVar;
        this.c = CLASSNAMEf.h(yVar.estimateSize());
        this.d = new ConcurrentHashMap(Math.max(16, CLASSNAMEf.g << 1));
        this.e = n3Var;
        this.f = null;
    }

    public final void compute() {
        y trySplit;
        y yVar = this.b;
        long j = this.c;
        boolean z = false;
        CLASSNAMEq0 q0Var = this;
        while (yVar.estimateSize() > j && (trySplit = yVar.trySplit()) != null) {
            CLASSNAMEq0 q0Var2 = new CLASSNAMEq0(q0Var, trySplit, q0Var.f);
            CLASSNAMEq0 q0Var3 = new CLASSNAMEq0(q0Var, yVar, q0Var2);
            q0Var.addToPendingCount(1);
            q0Var3.addToPendingCount(1);
            q0Var.d.put(q0Var2, q0Var3);
            if (q0Var.f != null) {
                q0Var2.addToPendingCount(1);
                if (q0Var.d.replace(q0Var.f, q0Var, q0Var2)) {
                    q0Var.addToPendingCount(-1);
                } else {
                    q0Var2.addToPendingCount(-1);
                }
            }
            if (z) {
                yVar = trySplit;
                q0Var = q0Var2;
                q0Var2 = q0Var3;
            } else {
                q0Var = q0Var3;
            }
            z = !z;
            q0Var2.fork();
        }
        if (q0Var.getPendingCount() > 0) {
            CLASSNAMEp0 p0Var = CLASSNAMEp0.a;
            CLASSNAMEz2 z2Var = q0Var.a;
            CLASSNAMEt1 t0 = z2Var.t0(z2Var.q0(yVar), p0Var);
            CLASSNAMEc cVar = (CLASSNAMEc) q0Var.a;
            cVar.getClass();
            t0.getClass();
            cVar.n0(cVar.v0(t0), yVar);
            q0Var.g = t0.a();
            q0Var.b = null;
        }
        q0Var.tryComplete();
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        B1 b1 = this.g;
        if (b1 != null) {
            b1.forEach(this.e);
            this.g = null;
        } else {
            y yVar = this.b;
            if (yVar != null) {
                CLASSNAMEz2 z2Var = this.a;
                CLASSNAMEn3 n3Var = this.e;
                CLASSNAMEc cVar = (CLASSNAMEc) z2Var;
                cVar.getClass();
                n3Var.getClass();
                cVar.n0(cVar.v0(n3Var), yVar);
                this.b = null;
            }
        }
        CLASSNAMEq0 q0Var = (CLASSNAMEq0) this.d.remove(this);
        if (q0Var != null) {
            q0Var.tryComplete();
        }
    }
}

package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import j$.util.u;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.q0  reason: case insensitive filesystem */
final class CLASSNAMEq0 extends CountedCompleter {
    public static final /* synthetic */ int h = 0;
    private final CLASSNAMEy2 a;
    private u b;
    private final long c;
    private final ConcurrentHashMap d;
    private final CLASSNAMEm3 e;
    private final CLASSNAMEq0 f;
    private A1 g;

    CLASSNAMEq0(CLASSNAMEq0 q0Var, u uVar, CLASSNAMEq0 q0Var2) {
        super(q0Var);
        this.a = q0Var.a;
        this.b = uVar;
        this.c = q0Var.c;
        this.d = q0Var.d;
        this.e = q0Var.e;
        this.f = q0Var2;
    }

    protected CLASSNAMEq0(CLASSNAMEy2 y2Var, u uVar, CLASSNAMEm3 m3Var) {
        super((CountedCompleter) null);
        this.a = y2Var;
        this.b = uVar;
        this.c = CLASSNAMEf.h(uVar.estimateSize());
        this.d = new ConcurrentHashMap(Math.max(16, CLASSNAMEf.g << 1));
        this.e = m3Var;
        this.f = null;
    }

    public final void compute() {
        u trySplit;
        u uVar = this.b;
        long j = this.c;
        boolean z = false;
        CLASSNAMEq0 q0Var = this;
        while (uVar.estimateSize() > j && (trySplit = uVar.trySplit()) != null) {
            CLASSNAMEq0 q0Var2 = new CLASSNAMEq0(q0Var, trySplit, q0Var.f);
            CLASSNAMEq0 q0Var3 = new CLASSNAMEq0(q0Var, uVar, q0Var2);
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
                uVar = trySplit;
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
            CLASSNAMEy2 y2Var = q0Var.a;
            CLASSNAMEs1 t0 = y2Var.t0(y2Var.q0(uVar), p0Var);
            CLASSNAMEc cVar = (CLASSNAMEc) q0Var.a;
            cVar.getClass();
            t0.getClass();
            cVar.n0(cVar.v0(t0), uVar);
            q0Var.g = t0.a();
            q0Var.b = null;
        }
        q0Var.tryComplete();
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        A1 a1 = this.g;
        if (a1 != null) {
            a1.forEach(this.e);
            this.g = null;
        } else {
            u uVar = this.b;
            if (uVar != null) {
                CLASSNAMEy2 y2Var = this.a;
                CLASSNAMEm3 m3Var = this.e;
                CLASSNAMEc cVar = (CLASSNAMEc) y2Var;
                cVar.getClass();
                m3Var.getClass();
                cVar.n0(cVar.v0(m3Var), uVar);
                this.b = null;
            }
        }
        CLASSNAMEq0 q0Var = (CLASSNAMEq0) this.d.remove(this);
        if (q0Var != null) {
            q0Var.tryComplete();
        }
    }
}

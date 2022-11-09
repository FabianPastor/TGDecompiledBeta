package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.q0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEq0 extends CountedCompleter {
    public static final /* synthetic */ int h = 0;
    private final AbstractCLASSNAMEy2 a;
    private j$.util.u b;
    private final long c;
    private final ConcurrentHashMap d;
    private final InterfaceCLASSNAMEm3 e;
    private final CLASSNAMEq0 f;
    private A1 g;

    CLASSNAMEq0(CLASSNAMEq0 CLASSNAMEq0, j$.util.u uVar, CLASSNAMEq0 CLASSNAMEq02) {
        super(CLASSNAMEq0);
        this.a = CLASSNAMEq0.a;
        this.b = uVar;
        this.c = CLASSNAMEq0.c;
        this.d = CLASSNAMEq0.d;
        this.e = CLASSNAMEq0.e;
        this.f = CLASSNAMEq02;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CLASSNAMEq0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(null);
        this.a = abstractCLASSNAMEy2;
        this.b = uVar;
        this.c = AbstractCLASSNAMEf.h(uVar.estimateSize());
        this.d = new ConcurrentHashMap(Math.max(16, AbstractCLASSNAMEf.g << 1));
        this.e = interfaceCLASSNAMEm3;
        this.f = null;
    }

    @Override // java.util.concurrent.CountedCompleter
    public final void compute() {
        j$.util.u mo326trySplit;
        j$.util.u uVar = this.b;
        long j = this.c;
        boolean z = false;
        CLASSNAMEq0 CLASSNAMEq0 = this;
        while (uVar.estimateSize() > j && (mo326trySplit = uVar.mo326trySplit()) != null) {
            CLASSNAMEq0 CLASSNAMEq02 = new CLASSNAMEq0(CLASSNAMEq0, mo326trySplit, CLASSNAMEq0.f);
            CLASSNAMEq0 CLASSNAMEq03 = new CLASSNAMEq0(CLASSNAMEq0, uVar, CLASSNAMEq02);
            CLASSNAMEq0.addToPendingCount(1);
            CLASSNAMEq03.addToPendingCount(1);
            CLASSNAMEq0.d.put(CLASSNAMEq02, CLASSNAMEq03);
            if (CLASSNAMEq0.f != null) {
                CLASSNAMEq02.addToPendingCount(1);
                if (CLASSNAMEq0.d.replace(CLASSNAMEq0.f, CLASSNAMEq0, CLASSNAMEq02)) {
                    CLASSNAMEq0.addToPendingCount(-1);
                } else {
                    CLASSNAMEq02.addToPendingCount(-1);
                }
            }
            if (z) {
                uVar = mo326trySplit;
                CLASSNAMEq0 = CLASSNAMEq02;
                CLASSNAMEq02 = CLASSNAMEq03;
            } else {
                CLASSNAMEq0 = CLASSNAMEq03;
            }
            z = !z;
            CLASSNAMEq02.fork();
        }
        if (CLASSNAMEq0.getPendingCount() > 0) {
            CLASSNAMEp0 CLASSNAMEp0 = CLASSNAMEp0.a;
            AbstractCLASSNAMEy2 abstractCLASSNAMEy2 = CLASSNAMEq0.a;
            InterfaceCLASSNAMEs1 t0 = abstractCLASSNAMEy2.t0(abstractCLASSNAMEy2.q0(uVar), CLASSNAMEp0);
            AbstractCLASSNAMEc abstractCLASSNAMEc = (AbstractCLASSNAMEc) CLASSNAMEq0.a;
            abstractCLASSNAMEc.getClass();
            t0.getClass();
            abstractCLASSNAMEc.n0(abstractCLASSNAMEc.v0(t0), uVar);
            CLASSNAMEq0.g = t0.mo291a();
            CLASSNAMEq0.b = null;
        }
        CLASSNAMEq0.tryComplete();
    }

    @Override // java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        A1 a1 = this.g;
        if (a1 != null) {
            a1.forEach(this.e);
            this.g = null;
        } else {
            j$.util.u uVar = this.b;
            if (uVar != null) {
                AbstractCLASSNAMEy2 abstractCLASSNAMEy2 = this.a;
                InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3 = this.e;
                AbstractCLASSNAMEc abstractCLASSNAMEc = (AbstractCLASSNAMEc) abstractCLASSNAMEy2;
                abstractCLASSNAMEc.getClass();
                interfaceCLASSNAMEm3.getClass();
                abstractCLASSNAMEc.n0(abstractCLASSNAMEc.v0(interfaceCLASSNAMEm3), uVar);
                this.b = null;
            }
        }
        CLASSNAMEq0 CLASSNAMEq0 = (CLASSNAMEq0) this.d.remove(this);
        if (CLASSNAMEq0 != null) {
            CLASSNAMEq0.tryComplete();
        }
    }
}

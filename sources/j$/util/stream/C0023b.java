package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.c;
import j$.util.function.m;
import j$.util.function.r;
import j$.util.function.y;
import j$.util.u;
import java.util.List;

/* renamed from: j$.util.stream.b  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb implements y, r, Consumer, c {
    public final /* synthetic */ int a = 2;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEb(u uVar) {
        this.b = uVar;
    }

    public void accept(Object obj) {
        switch (this.a) {
            case 3:
                ((CLASSNAMEm3) this.b).accept(obj);
                return;
            default:
                ((List) this.b).add(obj);
                return;
        }
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.a) {
            case 3:
                return Consumer.CC.$default$andThen(this, consumer);
            default:
                return Consumer.CC.$default$andThen(this, consumer);
        }
    }

    public Object apply(long j) {
        int i = H1.k;
        return CLASSNAMEx2.d(j, (m) this.b);
    }

    public Object get() {
        switch (this.a) {
            case 0:
                return (u) this.b;
            default:
                return ((CLASSNAMEc) this.b).D0();
        }
    }

    public /* synthetic */ CLASSNAMEb(m mVar) {
        this.b = mVar;
    }

    public /* synthetic */ CLASSNAMEb(CLASSNAMEc cVar) {
        this.b = cVar;
    }

    public /* synthetic */ CLASSNAMEb(CLASSNAMEm3 m3Var) {
        this.b = m3Var;
    }

    public /* synthetic */ CLASSNAMEb(CLASSNAMEo4 o4Var) {
        this.b = o4Var;
    }

    public /* synthetic */ CLASSNAMEb(CLASSNAMEq4 q4Var) {
        this.b = q4Var;
    }

    public /* synthetic */ CLASSNAMEb(s4 s4Var) {
        this.b = s4Var;
    }

    public /* synthetic */ CLASSNAMEb(L4 l4) {
        this.b = l4;
    }

    public /* synthetic */ CLASSNAMEb(List list) {
        this.b = list;
    }
}

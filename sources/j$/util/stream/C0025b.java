package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.c;
import j$.util.function.m;
import j$.util.function.r;
import j$.util.function.y;
import java.util.List;

/* renamed from: j$.util.stream.b  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb implements y, r, Consumer, c {
    public final /* synthetic */ int a = 2;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEb(j$.util.y yVar) {
        this.b = yVar;
    }

    public void accept(Object obj) {
        switch (this.a) {
            case 3:
                ((CLASSNAMEn3) this.b).accept(obj);
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
        int i = I1.k;
        return CLASSNAMEy2.d(j, (m) this.b);
    }

    public Object get() {
        switch (this.a) {
            case 0:
                return (j$.util.y) this.b;
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

    public /* synthetic */ CLASSNAMEb(CLASSNAMEn3 n3Var) {
        this.b = n3Var;
    }

    public /* synthetic */ CLASSNAMEb(CLASSNAMEp4 p4Var) {
        this.b = p4Var;
    }

    public /* synthetic */ CLASSNAMEb(CLASSNAMEr4 r4Var) {
        this.b = r4Var;
    }

    public /* synthetic */ CLASSNAMEb(t4 t4Var) {
        this.b = t4Var;
    }

    public /* synthetic */ CLASSNAMEb(M4 m4) {
        this.b = m4;
    }

    public /* synthetic */ CLASSNAMEb(List list) {
        this.b = list;
    }
}

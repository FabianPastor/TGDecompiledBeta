package j$.util.stream;

import j$.util.function.Consumer;
import java.util.List;
/* renamed from: j$.util.stream.b  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEb implements j$.util.function.y, j$.util.function.r, Consumer, j$.util.function.c {
    public final /* synthetic */ int a = 2;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEb(j$.util.u uVar) {
        this.b = uVar;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        switch (this.a) {
            case 3:
                ((InterfaceCLASSNAMEm3) this.b).accept((InterfaceCLASSNAMEm3) obj);
                return;
            default:
                ((List) this.b).add(obj);
                return;
        }
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.a) {
            case 3:
                return consumer.getClass();
            default:
                return consumer.getClass();
        }
    }

    @Override // j$.util.function.r
    public Object apply(long j) {
        int i = H1.k;
        return AbstractCLASSNAMEx2.d(j, (j$.util.function.m) this.b);
    }

    @Override // j$.util.function.y
    public Object get() {
        switch (this.a) {
            case 0:
                return (j$.util.u) this.b;
            default:
                return ((AbstractCLASSNAMEc) this.b).D0();
        }
    }

    public /* synthetic */ CLASSNAMEb(j$.util.function.m mVar) {
        this.b = mVar;
    }

    public /* synthetic */ CLASSNAMEb(AbstractCLASSNAMEc abstractCLASSNAMEc) {
        this.b = abstractCLASSNAMEc;
    }
}

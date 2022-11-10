package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.function.ToIntFunction;
/* loaded from: classes2.dex */
class Y2 extends AbstractCLASSNAMEi3 {
    public final /* synthetic */ int b = 5;
    final /* synthetic */ Object c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Y2(K k, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = k;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        switch (this.b) {
            case 0:
                ((Consumer) ((L) this.c).m).accept(obj);
                this.a.accept((InterfaceCLASSNAMEm3) obj);
                return;
            case 1:
                if (!((Predicate) ((L) this.c).m).test(obj)) {
                    return;
                }
                this.a.accept((InterfaceCLASSNAMEm3) obj);
                return;
            case 2:
                this.a.accept((InterfaceCLASSNAMEm3) ((CLASSNAMEa3) this.c).m.apply(obj));
                return;
            case 3:
                this.a.accept(((ToIntFunction) ((M) this.c).m).applyAsInt(obj));
                return;
            case 4:
                this.a.accept(((j$.util.function.A) ((N) this.c).m).applyAsLong(obj));
                return;
            case 5:
                this.a.accept(((j$.util.function.z) ((K) this.c).m).applyAsDouble(obj));
                return;
            default:
                Stream stream = (Stream) ((CLASSNAMEa3) this.c).m.apply(obj);
                if (stream != null) {
                    try {
                        ((Stream) stream.moNUMsequential()).forEach(this.a);
                    } finally {
                        try {
                            stream.close();
                        } catch (Throwable unused) {
                        }
                    }
                }
                if (stream == null) {
                    return;
                }
                return;
        }
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        switch (this.b) {
            case 1:
                this.a.n(-1L);
                return;
            case 6:
                this.a.n(-1L);
                return;
            default:
                this.a.n(j);
                return;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Y2(L l, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = l;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Y2(L l, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.lang.a aVar) {
        super(interfaceCLASSNAMEm3);
        this.c = l;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Y2(M m, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = m;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Y2(N n, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = n;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Y2(CLASSNAMEa3 CLASSNAMEa3, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = CLASSNAMEa3;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Y2(CLASSNAMEa3 CLASSNAMEa3, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.lang.a aVar) {
        super(interfaceCLASSNAMEm3);
        this.c = CLASSNAMEa3;
    }
}

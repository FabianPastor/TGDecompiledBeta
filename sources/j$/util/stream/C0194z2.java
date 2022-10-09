package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
/* renamed from: j$.util.stream.z2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
class CLASSNAMEz2 extends U2 {
    public final /* synthetic */ int b = 3;
    final /* synthetic */ Object c;
    final /* synthetic */ Object d;
    final /* synthetic */ Object e;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEz2(EnumCLASSNAMEe4 enumCLASSNAMEe4, BiConsumer biConsumer, BiConsumer biConsumer2, j$.util.function.y yVar) {
        super(enumCLASSNAMEe4);
        this.c = biConsumer;
        this.d = biConsumer2;
        this.e = yVar;
    }

    @Override // j$.util.stream.U2
    public S2 a() {
        switch (this.b) {
            case 0:
                return new A2((j$.util.function.y) this.e, (j$.util.function.w) this.d, (j$.util.function.b) this.c);
            case 1:
                return new F2((j$.util.function.y) this.e, (j$.util.function.u) this.d, (j$.util.function.b) this.c);
            case 2:
                return new G2(this.e, (BiFunction) this.d, (j$.util.function.b) this.c);
            case 3:
                return new K2((j$.util.function.y) this.e, (BiConsumer) this.d, (BiConsumer) this.c);
            default:
                return new O2((j$.util.function.y) this.e, (j$.util.function.v) this.d, (j$.util.function.b) this.c);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEz2(EnumCLASSNAMEe4 enumCLASSNAMEe4, j$.util.function.b bVar, BiFunction biFunction, Object obj) {
        super(enumCLASSNAMEe4);
        this.c = bVar;
        this.d = biFunction;
        this.e = obj;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEz2(EnumCLASSNAMEe4 enumCLASSNAMEe4, j$.util.function.b bVar, j$.util.function.u uVar, j$.util.function.y yVar) {
        super(enumCLASSNAMEe4);
        this.c = bVar;
        this.d = uVar;
        this.e = yVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEz2(EnumCLASSNAMEe4 enumCLASSNAMEe4, j$.util.function.b bVar, j$.util.function.v vVar, j$.util.function.y yVar) {
        super(enumCLASSNAMEe4);
        this.c = bVar;
        this.d = vVar;
        this.e = yVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEz2(EnumCLASSNAMEe4 enumCLASSNAMEe4, j$.util.function.b bVar, j$.util.function.w wVar, j$.util.function.y yVar) {
        super(enumCLASSNAMEe4);
        this.c = bVar;
        this.d = wVar;
        this.e = yVar;
    }
}

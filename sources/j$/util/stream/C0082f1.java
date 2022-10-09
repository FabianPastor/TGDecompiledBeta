package j$.util.stream;

import j$.util.function.Predicate;
/* renamed from: j$.util.stream.f1  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
class CLASSNAMEf1 extends AbstractCLASSNAMEj1 {
    final /* synthetic */ EnumCLASSNAMEk1 c;
    final /* synthetic */ Predicate d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEf1(EnumCLASSNAMEk1 enumCLASSNAMEk1, Predicate predicate) {
        super(enumCLASSNAMEk1);
        this.c = enumCLASSNAMEk1;
        this.d = predicate;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        boolean z;
        boolean z2;
        if (!this.a) {
            boolean test = this.d.test(obj);
            z = this.c.a;
            if (test != z) {
                return;
            }
            this.a = true;
            z2 = this.c.b;
            this.b = z2;
        }
    }
}

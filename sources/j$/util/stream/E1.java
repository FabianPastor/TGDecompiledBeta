package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.Collection$EL;
import j$.util.InterfaceCLASSNAMEb;
import j$.util.function.Consumer;
import java.util.Collection;
/* loaded from: classes2.dex */
final class E1 implements A1 {
    private final Collection a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public E1(Collection collection) {
        this.a = collection;
    }

    @Override // j$.util.stream.A1
    /* renamed from: b */
    public A1 moNUMb(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.A1
    public long count() {
        return this.a.size();
    }

    @Override // j$.util.stream.A1
    public void forEach(Consumer consumer) {
        Collection$EL.a(this.a, consumer);
    }

    @Override // j$.util.stream.A1
    public void i(Object[] objArr, int i) {
        for (Object obj : this.a) {
            objArr[i] = obj;
            i++;
        }
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ int p() {
        return 0;
    }

    @Override // j$.util.stream.A1
    public Object[] q(j$.util.function.m mVar) {
        Collection collection = this.a;
        return collection.toArray((Object[]) mVar.apply(collection.size()));
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ A1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.q(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u moNUMspliterator() {
        Collection collection = this.a;
        return (collection instanceof InterfaceCLASSNAMEb ? ((InterfaceCLASSNAMEb) collection).moNUMstream() : AbstractCLASSNAMEa.i(collection)).moNUMspliterator();
    }

    public String toString() {
        return String.format("CollectionNode[%d][%s]", Integer.valueOf(this.a.size()), this.a);
    }
}

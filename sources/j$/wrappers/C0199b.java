package j$.wrappers;

import j$.util.InterfaceCLASSNAMEn;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
/* renamed from: j$.wrappers.b  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEb implements PrimitiveIterator.OfDouble {
    final /* synthetic */ InterfaceCLASSNAMEn a;

    private /* synthetic */ CLASSNAMEb(InterfaceCLASSNAMEn interfaceCLASSNAMEn) {
        this.a = interfaceCLASSNAMEn;
    }

    public static /* synthetic */ PrimitiveIterator.OfDouble a(InterfaceCLASSNAMEn interfaceCLASSNAMEn) {
        if (interfaceCLASSNAMEn == null) {
            return null;
        }
        return interfaceCLASSNAMEn instanceof CLASSNAMEa ? ((CLASSNAMEa) interfaceCLASSNAMEn).a : new CLASSNAMEb(interfaceCLASSNAMEn);
    }

    @Override // java.util.PrimitiveIterator
    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.a.forEachRemaining(doubleConsumer);
    }

    @Override // java.util.PrimitiveIterator.OfDouble, java.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEw.b(consumer));
    }

    @Override // java.util.PrimitiveIterator.OfDouble
    /* renamed from: forEachRemaining  reason: avoid collision after fix types in other method */
    public /* synthetic */ void forEachRemaining2(DoubleConsumer doubleConsumer) {
        this.a.e(A.b(doubleConsumer));
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.n] */
    @Override // java.util.Iterator
    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    @Override // java.util.PrimitiveIterator.OfDouble, java.util.Iterator
    public /* synthetic */ Double next() {
        return this.a.moNUMnext();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.n] */
    @Override // java.util.PrimitiveIterator.OfDouble, java.util.Iterator
    /* renamed from: next  reason: collision with other method in class */
    public /* synthetic */ Object moNUMnext() {
        return this.a.moNUMnext();
    }

    @Override // java.util.PrimitiveIterator.OfDouble
    public /* synthetic */ double nextDouble() {
        return this.a.nextDouble();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.n] */
    @Override // java.util.Iterator
    public /* synthetic */ void remove() {
        this.a.remove();
    }
}

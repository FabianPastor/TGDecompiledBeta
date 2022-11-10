package j$.wrappers;

import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
/* renamed from: j$.wrappers.f */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEf implements PrimitiveIterator.OfLong {
    final /* synthetic */ j$.util.r a;

    private /* synthetic */ CLASSNAMEf(j$.util.r rVar) {
        this.a = rVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfLong a(j$.util.r rVar) {
        if (rVar == null) {
            return null;
        }
        return rVar instanceof CLASSNAMEe ? ((CLASSNAMEe) rVar).a : new CLASSNAMEf(rVar);
    }

    @Override // java.util.PrimitiveIterator
    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.a.forEachRemaining(longConsumer);
    }

    @Override // java.util.PrimitiveIterator.OfLong, java.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEw.b(consumer));
    }

    @Override // java.util.PrimitiveIterator.OfLong
    /* renamed from: forEachRemaining */
    public /* synthetic */ void forEachRemaining2(LongConsumer longConsumer) {
        this.a.d(CLASSNAMEf0.b(longConsumer));
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.r] */
    @Override // java.util.Iterator
    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    @Override // java.util.PrimitiveIterator.OfLong, java.util.Iterator
    public /* synthetic */ Long next() {
        return this.a.mo315next();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.r] */
    @Override // java.util.PrimitiveIterator.OfLong, java.util.Iterator
    /* renamed from: next */
    public /* synthetic */ Object mo316next() {
        return this.a.mo311next();
    }

    @Override // java.util.PrimitiveIterator.OfLong
    public /* synthetic */ long nextLong() {
        return this.a.nextLong();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.r] */
    @Override // java.util.Iterator
    public /* synthetic */ void remove() {
        this.a.remove();
    }
}

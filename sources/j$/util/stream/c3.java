package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.k;
import j$.util.s;
import java.util.Comparator;

abstract class c3<T, T_SPLITR extends Spliterator<T>> {

    /* renamed from: a  reason: collision with root package name */
    final long var_a;
    final long b;
    Spliterator c;
    long d;
    long e;

    static final class a extends d<Double, Spliterator.a, q> implements Spliterator.a {
        a(Spliterator.a aVar, long j, long j2) {
            super(aVar, j, j2);
        }

        a(Spliterator.a aVar, long j, long j2, long j3, long j4) {
            super(aVar, j, j2, j3, j4, (V2) null);
        }

        /* access modifiers changed from: protected */
        public Spliterator a(Spliterator spliterator, long j, long j2, long j3, long j4) {
            return new a((Spliterator.a) spliterator, j, j2, j3, j4);
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return s.d(this, consumer);
        }

        /* access modifiers changed from: protected */
        public /* bridge */ /* synthetic */ Object f() {
            return B0.var_a;
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            s.a(this, consumer);
        }
    }

    static final class b extends d<Integer, Spliterator.b, w> implements Spliterator.b {
        b(Spliterator.b bVar, long j, long j2) {
            super(bVar, j, j2);
        }

        b(Spliterator.b bVar, long j, long j2, long j3, long j4) {
            super(bVar, j, j2, j3, j4, (V2) null);
        }

        /* access modifiers changed from: protected */
        public Spliterator a(Spliterator spliterator, long j, long j2, long j3, long j4) {
            return new b((Spliterator.b) spliterator, j, j2, j3, j4);
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return s.e(this, consumer);
        }

        /* access modifiers changed from: protected */
        public /* bridge */ /* synthetic */ Object f() {
            return C0.var_a;
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            s.b(this, consumer);
        }
    }

    static final class c extends d<Long, Spliterator.c, C> implements Spliterator.c {
        c(Spliterator.c cVar, long j, long j2) {
            super(cVar, j, j2);
        }

        c(Spliterator.c cVar, long j, long j2, long j3, long j4) {
            super(cVar, j, j2, j3, j4, (V2) null);
        }

        /* access modifiers changed from: protected */
        public Spliterator a(Spliterator spliterator, long j, long j2, long j3, long j4) {
            return new c((Spliterator.c) spliterator, j, j2, j3, j4);
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return s.f(this, consumer);
        }

        /* access modifiers changed from: protected */
        public /* bridge */ /* synthetic */ Object f() {
            return D0.var_a;
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            s.c(this, consumer);
        }
    }

    static abstract class d<T, T_SPLITR extends Spliterator.d<T, T_CONS, T_SPLITR>, T_CONS> extends c3<T, T_SPLITR> implements Spliterator.d<T, T_CONS, T_SPLITR> {
        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        d(j$.util.Spliterator.d r13, long r14, long r16) {
            /*
                r12 = this;
                long r0 = r13.estimateSize()
                r6 = r16
                long r10 = java.lang.Math.min(r0, r6)
                r8 = 0
                r2 = r12
                r3 = r13
                r4 = r14
                r2.<init>(r3, r4, r6, r8, r10)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.c3.d.<init>(j$.util.Spliterator$d, long, long):void");
        }

        /* access modifiers changed from: protected */
        public abstract Object f();

        /* renamed from: forEachRemaining */
        public void e(Object obj) {
            obj.getClass();
            long j = this.var_a;
            long j2 = this.e;
            if (j < j2) {
                long j3 = this.d;
                if (j3 < j2) {
                    if (j3 < j || ((Spliterator.d) this.c).estimateSize() + j3 > this.b) {
                        while (this.var_a > this.d) {
                            ((Spliterator.d) this.c).tryAdvance(f());
                            this.d++;
                        }
                        while (this.d < this.e) {
                            ((Spliterator.d) this.c).tryAdvance(obj);
                            this.d++;
                        }
                        return;
                    }
                    ((Spliterator.d) this.c).forEachRemaining(obj);
                    this.d = this.e;
                }
            }
        }

        public Comparator getComparator() {
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return k.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return k.f(this, i);
        }

        /* renamed from: tryAdvance */
        public boolean o(Object obj) {
            long j;
            obj.getClass();
            if (this.var_a >= this.e) {
                return false;
            }
            while (true) {
                long j2 = this.var_a;
                j = this.d;
                if (j2 <= j) {
                    break;
                }
                ((Spliterator.d) this.c).tryAdvance(f());
                this.d++;
            }
            if (j >= this.e) {
                return false;
            }
            this.d = j + 1;
            return ((Spliterator.d) this.c).tryAdvance(obj);
        }

        d(Spliterator.d dVar, long j, long j2, long j3, long j4, V2 v2) {
            super(dVar, j, j2, j3, j4);
        }
    }

    static final class e<T> extends c3<T, Spliterator<T>> implements Spliterator<T> {
        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        e(j$.util.Spliterator r13, long r14, long r16) {
            /*
                r12 = this;
                long r0 = r13.estimateSize()
                r6 = r16
                long r10 = java.lang.Math.min(r0, r6)
                r8 = 0
                r2 = r12
                r3 = r13
                r4 = r14
                r2.<init>(r3, r4, r6, r8, r10)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.c3.e.<init>(j$.util.Spliterator, long, long):void");
        }

        private e(Spliterator spliterator, long j, long j2, long j3, long j4) {
            super(spliterator, j, j2, j3, j4);
        }

        /* access modifiers changed from: protected */
        public Spliterator a(Spliterator spliterator, long j, long j2, long j3, long j4) {
            return new e(spliterator, j, j2, j3, j4);
        }

        public boolean b(Consumer consumer) {
            long j;
            consumer.getClass();
            if (this.var_a >= this.e) {
                return false;
            }
            while (true) {
                long j2 = this.var_a;
                j = this.d;
                if (j2 <= j) {
                    break;
                }
                this.c.b(E0.var_a);
                this.d++;
            }
            if (j >= this.e) {
                return false;
            }
            this.d = j + 1;
            return this.c.b(consumer);
        }

        public void forEachRemaining(Consumer consumer) {
            consumer.getClass();
            long j = this.var_a;
            long j2 = this.e;
            if (j < j2) {
                long j3 = this.d;
                if (j3 < j2) {
                    if (j3 < j || this.c.estimateSize() + j3 > this.b) {
                        while (this.var_a > this.d) {
                            this.c.b(F0.var_a);
                            this.d++;
                        }
                        while (this.d < this.e) {
                            this.c.b(consumer);
                            this.d++;
                        }
                        return;
                    }
                    this.c.forEachRemaining(consumer);
                    this.d = this.e;
                }
            }
        }

        public Comparator getComparator() {
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return k.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return k.f(this, i);
        }
    }

    c3(Spliterator spliterator, long j, long j2, long j3, long j4) {
        this.c = spliterator;
        this.var_a = j;
        this.b = j2;
        this.d = j3;
        this.e = j4;
    }

    /* access modifiers changed from: protected */
    public abstract Spliterator a(Spliterator spliterator, long j, long j2, long j3, long j4);

    public int characteristics() {
        return this.c.characteristics();
    }

    public long estimateSize() {
        long j = this.var_a;
        long j2 = this.e;
        if (j < j2) {
            return j2 - Math.max(j, this.d);
        }
        return 0;
    }

    public Spliterator trySplit() {
        long j = this.var_a;
        long j2 = this.e;
        if (j >= j2 || this.d >= j2) {
            return null;
        }
        while (true) {
            Spliterator trySplit = this.c.trySplit();
            if (trySplit == null) {
                return null;
            }
            long estimateSize = trySplit.estimateSize() + this.d;
            long min = Math.min(estimateSize, this.b);
            long j3 = this.var_a;
            if (j3 >= min) {
                this.d = min;
            } else {
                long j4 = this.b;
                if (min >= j4) {
                    this.c = trySplit;
                    this.e = min;
                } else {
                    long j5 = this.d;
                    if (j5 < j3 || estimateSize > j4) {
                        this.d = min;
                        return a(trySplit, j3, j4, j5, min);
                    }
                    this.d = min;
                    return trySplit;
                }
            }
        }
    }
}

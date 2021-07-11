package j$.util.stream;

import j$.CLASSNAMEw;
import j$.lang.Iterable;
import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.CLASSNAMEf;
import j$.util.function.CLASSNAMEg;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.r;
import j$.util.u;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

class S2<E> extends CLASSNAMEj1 implements Consumer<E>, Iterable<E>, Iterable {
    protected Object[] e = new Object[16];
    protected Object[][] f;

    class a implements Spliterator<E> {
        int a;
        final int b;
        int c;
        final int d;
        Object[] e;

        a(int i, int i2, int i3, int i4) {
            this.a = i;
            this.b = i2;
            this.c = i3;
            this.d = i4;
            Object[][] objArr = S2.this.f;
            this.e = objArr == null ? S2.this.e : objArr[i];
        }

        public boolean b(Consumer consumer) {
            consumer.getClass();
            int i = this.a;
            int i2 = this.b;
            if (i >= i2 && (i != i2 || this.c >= this.d)) {
                return false;
            }
            Object[] objArr = this.e;
            int i3 = this.c;
            this.c = i3 + 1;
            consumer.accept(objArr[i3]);
            if (this.c == this.e.length) {
                this.c = 0;
                int i4 = this.a + 1;
                this.a = i4;
                Object[][] objArr2 = S2.this.f;
                if (objArr2 != null && i4 <= this.b) {
                    this.e = objArr2[i4];
                }
            }
            return true;
        }

        public int characteristics() {
            return 16464;
        }

        public long estimateSize() {
            int i = this.a;
            int i2 = this.b;
            if (i == i2) {
                return ((long) this.d) - ((long) this.c);
            }
            long[] jArr = S2.this.d;
            return ((jArr[i2] + ((long) this.d)) - jArr[i]) - ((long) this.c);
        }

        public void forEachRemaining(Consumer consumer) {
            int i;
            consumer.getClass();
            int i2 = this.a;
            int i3 = this.b;
            if (i2 < i3 || (i2 == i3 && this.c < this.d)) {
                int i4 = this.c;
                while (true) {
                    i = this.b;
                    if (i2 >= i) {
                        break;
                    }
                    Object[] objArr = S2.this.f[i2];
                    while (i4 < objArr.length) {
                        consumer.accept(objArr[i4]);
                        i4++;
                    }
                    i4 = 0;
                    i2++;
                }
                Object[] objArr2 = this.a == i ? this.e : S2.this.f[i];
                int i5 = this.d;
                while (i4 < i5) {
                    consumer.accept(objArr2[i4]);
                    i4++;
                }
                this.a = this.b;
                this.c = this.d;
            }
        }

        public Comparator getComparator() {
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return j$.time.a.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return j$.time.a.f(this, i);
        }

        public Spliterator trySplit() {
            int i = this.a;
            int i2 = this.b;
            if (i < i2) {
                S2 s2 = S2.this;
                int i3 = i2 - 1;
                a aVar = new a(i, i3, this.c, s2.f[i3].length);
                int i4 = this.b;
                this.a = i4;
                this.c = 0;
                this.e = S2.this.f[i4];
                return aVar;
            } else if (i != i2) {
                return null;
            } else {
                int i5 = this.d;
                int i6 = this.c;
                int i7 = (i5 - i6) / 2;
                if (i7 == 0) {
                    return null;
                }
                Spliterator n = u.n(this.e, i6, i6 + i7, 1040);
                this.c += i7;
                return n;
            }
        }
    }

    static class b extends e<Double, double[], q> implements q {

        class a extends e<Double, double[], q>.a<Spliterator.a> implements Spliterator.a {
            a(int i, int i2, int i3, int i4) {
                super(i, i2, i3, i4);
            }

            /* access modifiers changed from: package-private */
            public void a(Object obj, int i, Object obj2) {
                ((q) obj2).accept(((double[]) obj)[i]);
            }

            public /* synthetic */ boolean b(Consumer consumer) {
                return r.e(this, consumer);
            }

            /* access modifiers changed from: package-private */
            public Spliterator.d f(Object obj, int i, int i2) {
                return u.j((double[]) obj, i, i2 + i, 1040);
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                r.a(this, consumer);
            }

            /* access modifiers changed from: package-private */
            public Spliterator.d h(int i, int i2, int i3, int i4) {
                return new a(i, i2, i3, i4);
            }
        }

        b() {
        }

        b(int i) {
            super(i);
        }

        /* renamed from: A */
        public Spliterator.a spliterator() {
            return new a(0, this.c, 0, this.b);
        }

        public void accept(double d) {
            z();
            int i = this.b;
            this.b = i + 1;
            ((double[]) this.e)[i] = d;
        }

        public Object c(int i) {
            return new double[i];
        }

        public void forEach(Consumer consumer) {
            if (consumer instanceof q) {
                g((q) consumer);
            } else if (!i3.a) {
                spliterator().forEachRemaining(consumer);
            } else {
                i3.a(getClass(), "{0} calling SpinedBuffer.OfDouble.forEach(Consumer)");
                throw null;
            }
        }

        public Iterator iterator() {
            return u.f(spliterator());
        }

        public q j(q qVar) {
            qVar.getClass();
            return new CLASSNAMEf(this, qVar);
        }

        /* access modifiers changed from: protected */
        public void s(Object obj, int i, int i2, Object obj2) {
            double[] dArr = (double[]) obj;
            q qVar = (q) obj2;
            while (i < i2) {
                qVar.accept(dArr[i]);
                i++;
            }
        }

        /* access modifiers changed from: protected */
        public int t(Object obj) {
            return ((double[]) obj).length;
        }

        public String toString() {
            double[] dArr = (double[]) e();
            if (dArr.length < 200) {
                return String.format("%s[length=%d, chunks=%d]%s", new Object[]{getClass().getSimpleName(), Integer.valueOf(dArr.length), Integer.valueOf(this.c), Arrays.toString(dArr)});
            }
            return String.format("%s[length=%d, chunks=%d]%s...", new Object[]{getClass().getSimpleName(), Integer.valueOf(dArr.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(dArr, 200))});
        }

        /* access modifiers changed from: protected */
        public Object[] y(int i) {
            return new double[i][];
        }
    }

    static class c extends e<Integer, int[], w> implements w {

        class a extends e<Integer, int[], w>.a<Spliterator.b> implements Spliterator.b {
            a(int i, int i2, int i3, int i4) {
                super(i, i2, i3, i4);
            }

            /* access modifiers changed from: package-private */
            public void a(Object obj, int i, Object obj2) {
                ((w) obj2).accept(((int[]) obj)[i]);
            }

            public /* synthetic */ boolean b(Consumer consumer) {
                return r.f(this, consumer);
            }

            /* access modifiers changed from: package-private */
            public Spliterator.d f(Object obj, int i, int i2) {
                return u.k((int[]) obj, i, i2 + i, 1040);
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                r.b(this, consumer);
            }

            /* access modifiers changed from: package-private */
            public Spliterator.d h(int i, int i2, int i3, int i4) {
                return new a(i, i2, i3, i4);
            }
        }

        c() {
        }

        c(int i) {
            super(i);
        }

        /* renamed from: A */
        public Spliterator.b spliterator() {
            return new a(0, this.c, 0, this.b);
        }

        public void accept(int i) {
            z();
            int i2 = this.b;
            this.b = i2 + 1;
            ((int[]) this.e)[i2] = i;
        }

        public Object c(int i) {
            return new int[i];
        }

        public void forEach(Consumer consumer) {
            if (consumer instanceof w) {
                g((w) consumer);
            } else if (!i3.a) {
                spliterator().forEachRemaining(consumer);
            } else {
                i3.a(getClass(), "{0} calling SpinedBuffer.OfInt.forEach(Consumer)");
                throw null;
            }
        }

        public Iterator iterator() {
            return u.g(spliterator());
        }

        public w k(w wVar) {
            wVar.getClass();
            return new CLASSNAMEg(this, wVar);
        }

        /* access modifiers changed from: protected */
        public void s(Object obj, int i, int i2, Object obj2) {
            int[] iArr = (int[]) obj;
            w wVar = (w) obj2;
            while (i < i2) {
                wVar.accept(iArr[i]);
                i++;
            }
        }

        /* access modifiers changed from: protected */
        public int t(Object obj) {
            return ((int[]) obj).length;
        }

        public String toString() {
            int[] iArr = (int[]) e();
            if (iArr.length < 200) {
                return String.format("%s[length=%d, chunks=%d]%s", new Object[]{getClass().getSimpleName(), Integer.valueOf(iArr.length), Integer.valueOf(this.c), Arrays.toString(iArr)});
            }
            return String.format("%s[length=%d, chunks=%d]%s...", new Object[]{getClass().getSimpleName(), Integer.valueOf(iArr.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(iArr, 200))});
        }

        /* access modifiers changed from: protected */
        public Object[] y(int i) {
            return new int[i][];
        }
    }

    static class d extends e<Long, long[], C> implements C {

        class a extends e<Long, long[], C>.a<Spliterator.c> implements Spliterator.c {
            a(int i, int i2, int i3, int i4) {
                super(i, i2, i3, i4);
            }

            /* access modifiers changed from: package-private */
            public void a(Object obj, int i, Object obj2) {
                ((C) obj2).accept(((long[]) obj)[i]);
            }

            public /* synthetic */ boolean b(Consumer consumer) {
                return r.g(this, consumer);
            }

            /* access modifiers changed from: package-private */
            public Spliterator.d f(Object obj, int i, int i2) {
                return u.l((long[]) obj, i, i2 + i, 1040);
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                r.c(this, consumer);
            }

            /* access modifiers changed from: package-private */
            public Spliterator.d h(int i, int i2, int i3, int i4) {
                return new a(i, i2, i3, i4);
            }
        }

        d() {
        }

        d(int i) {
            super(i);
        }

        /* renamed from: A */
        public Spliterator.c spliterator() {
            return new a(0, this.c, 0, this.b);
        }

        public void accept(long j) {
            z();
            int i = this.b;
            this.b = i + 1;
            ((long[]) this.e)[i] = j;
        }

        public Object c(int i) {
            return new long[i];
        }

        public C f(C c) {
            c.getClass();
            return new CLASSNAMEh(this, c);
        }

        public void forEach(Consumer consumer) {
            if (consumer instanceof C) {
                g((C) consumer);
            } else if (!i3.a) {
                spliterator().forEachRemaining(consumer);
            } else {
                i3.a(getClass(), "{0} calling SpinedBuffer.OfLong.forEach(Consumer)");
                throw null;
            }
        }

        public Iterator iterator() {
            return u.h(spliterator());
        }

        /* access modifiers changed from: protected */
        public void s(Object obj, int i, int i2, Object obj2) {
            long[] jArr = (long[]) obj;
            C c = (C) obj2;
            while (i < i2) {
                c.accept(jArr[i]);
                i++;
            }
        }

        /* access modifiers changed from: protected */
        public int t(Object obj) {
            return ((long[]) obj).length;
        }

        public String toString() {
            long[] jArr = (long[]) e();
            if (jArr.length < 200) {
                return String.format("%s[length=%d, chunks=%d]%s", new Object[]{getClass().getSimpleName(), Integer.valueOf(jArr.length), Integer.valueOf(this.c), Arrays.toString(jArr)});
            }
            return String.format("%s[length=%d, chunks=%d]%s...", new Object[]{getClass().getSimpleName(), Integer.valueOf(jArr.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(jArr, 200))});
        }

        /* access modifiers changed from: protected */
        public Object[] y(int i) {
            return new long[i][];
        }
    }

    static abstract class e<E, T_ARR, T_CONS> extends CLASSNAMEj1 implements Iterable<E>, Iterable {
        Object e = c(16);
        Object[] f;

        abstract class a<T_SPLITR extends Spliterator.d<E, T_CONS, T_SPLITR>> implements Spliterator.d<E, T_CONS, T_SPLITR> {
            int a;
            final int b;
            int c;
            final int d;
            Object e;

            a(int i, int i2, int i3, int i4) {
                this.a = i;
                this.b = i2;
                this.c = i3;
                this.d = i4;
                Object[] objArr = e.this.f;
                this.e = objArr == null ? e.this.e : objArr[i];
            }

            /* access modifiers changed from: package-private */
            public abstract void a(Object obj, int i, Object obj2);

            public int characteristics() {
                return 16464;
            }

            public long estimateSize() {
                int i = this.a;
                int i2 = this.b;
                if (i == i2) {
                    return ((long) this.d) - ((long) this.c);
                }
                long[] jArr = e.this.d;
                return ((jArr[i2] + ((long) this.d)) - jArr[i]) - ((long) this.c);
            }

            /* access modifiers changed from: package-private */
            public abstract Spliterator.d f(Object obj, int i, int i2);

            /* renamed from: forEachRemaining */
            public void e(Object obj) {
                int i;
                obj.getClass();
                int i2 = this.a;
                int i3 = this.b;
                if (i2 < i3 || (i2 == i3 && this.c < this.d)) {
                    int i4 = this.c;
                    while (true) {
                        i = this.b;
                        if (i2 >= i) {
                            break;
                        }
                        e eVar = e.this;
                        Object obj2 = eVar.f[i2];
                        eVar.s(obj2, i4, eVar.t(obj2), obj);
                        i4 = 0;
                        i2++;
                    }
                    e.this.s(this.a == i ? this.e : e.this.f[i], i4, this.d, obj);
                    this.a = this.b;
                    this.c = this.d;
                }
            }

            public Comparator getComparator() {
                throw new IllegalStateException();
            }

            public /* synthetic */ long getExactSizeIfKnown() {
                return j$.time.a.e(this);
            }

            /* access modifiers changed from: package-private */
            public abstract Spliterator.d h(int i, int i2, int i3, int i4);

            public /* synthetic */ boolean hasCharacteristics(int i) {
                return j$.time.a.f(this, i);
            }

            /* renamed from: tryAdvance */
            public boolean n(Object obj) {
                obj.getClass();
                int i = this.a;
                int i2 = this.b;
                if (i >= i2 && (i != i2 || this.c >= this.d)) {
                    return false;
                }
                Object obj2 = this.e;
                int i3 = this.c;
                this.c = i3 + 1;
                a(obj2, i3, obj);
                if (this.c == e.this.t(this.e)) {
                    this.c = 0;
                    int i4 = this.a + 1;
                    this.a = i4;
                    Object[] objArr = e.this.f;
                    if (objArr != null && i4 <= this.b) {
                        this.e = objArr[i4];
                    }
                }
                return true;
            }

            public Spliterator.d trySplit() {
                int i = this.a;
                int i2 = this.b;
                if (i < i2) {
                    int i3 = this.c;
                    e eVar = e.this;
                    Spliterator.d h = h(i, i2 - 1, i3, eVar.t(eVar.f[i2 - 1]));
                    int i4 = this.b;
                    this.a = i4;
                    this.c = 0;
                    this.e = e.this.f[i4];
                    return h;
                } else if (i != i2) {
                    return null;
                } else {
                    int i5 = this.d;
                    int i6 = this.c;
                    int i7 = (i5 - i6) / 2;
                    if (i7 == 0) {
                        return null;
                    }
                    Spliterator.d f2 = f(this.e, i6, i7);
                    this.c += i7;
                    return f2;
                }
            }
        }

        e() {
        }

        e(int i) {
            super(i);
        }

        private void x() {
            if (this.f == null) {
                Object[] y = y(8);
                this.f = y;
                this.d = new long[8];
                y[0] = this.e;
            }
        }

        public abstract Object c(int i);

        public void clear() {
            Object[] objArr = this.f;
            if (objArr != null) {
                this.e = objArr[0];
                this.f = null;
                this.d = null;
            }
            this.b = 0;
            this.c = 0;
        }

        public void d(Object obj, int i) {
            long j = (long) i;
            long count = count() + j;
            if (count > ((long) t(obj)) || count < j) {
                throw new IndexOutOfBoundsException("does not fit");
            } else if (this.c == 0) {
                System.arraycopy(this.e, 0, obj, i, this.b);
            } else {
                for (int i2 = 0; i2 < this.c; i2++) {
                    Object[] objArr = this.f;
                    System.arraycopy(objArr[i2], 0, obj, i, t(objArr[i2]));
                    i += t(this.f[i2]);
                }
                int i3 = this.b;
                if (i3 > 0) {
                    System.arraycopy(this.e, 0, obj, i, i3);
                }
            }
        }

        public Object e() {
            long count = count();
            if (count < NUM) {
                Object c = c((int) count);
                d(c, 0);
                return c;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        public void g(Object obj) {
            for (int i = 0; i < this.c; i++) {
                Object[] objArr = this.f;
                s(objArr[i], 0, t(objArr[i]), obj);
            }
            s(this.e, 0, this.b, obj);
        }

        /* access modifiers changed from: protected */
        public abstract void s(Object obj, int i, int i2, Object obj2);

        public abstract Spliterator spliterator();

        /* access modifiers changed from: protected */
        public abstract int t(Object obj);

        /* access modifiers changed from: protected */
        public long u() {
            int i = this.c;
            if (i == 0) {
                return (long) t(this.e);
            }
            return ((long) t(this.f[i])) + this.d[i];
        }

        /* access modifiers changed from: protected */
        public int v(long j) {
            if (this.c == 0) {
                if (j < ((long) this.b)) {
                    return 0;
                }
                throw new IndexOutOfBoundsException(Long.toString(j));
            } else if (j < count()) {
                for (int i = 0; i <= this.c; i++) {
                    if (j < this.d[i] + ((long) t(this.f[i]))) {
                        return i;
                    }
                }
                throw new IndexOutOfBoundsException(Long.toString(j));
            } else {
                throw new IndexOutOfBoundsException(Long.toString(j));
            }
        }

        /* access modifiers changed from: protected */
        public final void w(long j) {
            long u = u();
            if (j > u) {
                x();
                int i = this.c;
                while (true) {
                    i++;
                    if (j > u) {
                        Object[] objArr = this.f;
                        if (i >= objArr.length) {
                            int length = objArr.length * 2;
                            this.f = Arrays.copyOf(objArr, length);
                            this.d = Arrays.copyOf(this.d, length);
                        }
                        int r = r(i);
                        this.f[i] = c(r);
                        long[] jArr = this.d;
                        int i2 = i - 1;
                        jArr[i] = jArr[i2] + ((long) t(this.f[i2]));
                        u += (long) r;
                    } else {
                        return;
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public abstract Object[] y(int i);

        /* access modifiers changed from: protected */
        public void z() {
            if (this.b == t(this.e)) {
                x();
                int i = this.c;
                int i2 = i + 1;
                Object[] objArr = this.f;
                if (i2 >= objArr.length || objArr[i + 1] == null) {
                    w(u() + 1);
                }
                this.b = 0;
                int i3 = this.c + 1;
                this.c = i3;
                this.e = this.f[i3];
            }
        }
    }

    S2() {
    }

    private void u() {
        if (this.f == null) {
            Object[][] objArr = new Object[8][];
            this.f = objArr;
            this.d = new long[8];
            objArr[0] = this.e;
        }
    }

    public void accept(Object obj) {
        if (this.b == this.e.length) {
            u();
            int i = this.c;
            int i2 = i + 1;
            Object[][] objArr = this.f;
            if (i2 >= objArr.length || objArr[i + 1] == null) {
                t(s() + 1);
            }
            this.b = 0;
            int i3 = this.c + 1;
            this.c = i3;
            this.e = this.f[i3];
        }
        Object[] objArr2 = this.e;
        int i4 = this.b;
        this.b = i4 + 1;
        objArr2[i4] = obj;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public void clear() {
        Object[][] objArr = this.f;
        if (objArr != null) {
            this.e = objArr[0];
            int i = 0;
            while (true) {
                Object[] objArr2 = this.e;
                if (i >= objArr2.length) {
                    break;
                }
                objArr2[i] = null;
                i++;
            }
            this.f = null;
            this.d = null;
        } else {
            for (int i2 = 0; i2 < this.b; i2++) {
                this.e[i2] = null;
            }
        }
        this.b = 0;
        this.c = 0;
    }

    public void forEach(Consumer consumer) {
        for (int i = 0; i < this.c; i++) {
            for (Object accept : this.f[i]) {
                consumer.accept(accept);
            }
        }
        for (int i2 = 0; i2 < this.b; i2++) {
            consumer.accept(this.e[i2]);
        }
    }

    public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
        forEach(CLASSNAMEw.b(consumer));
    }

    public void i(Object[] objArr, int i) {
        long j = (long) i;
        long count = count() + j;
        if (count > ((long) objArr.length) || count < j) {
            throw new IndexOutOfBoundsException("does not fit");
        } else if (this.c == 0) {
            System.arraycopy(this.e, 0, objArr, i, this.b);
        } else {
            for (int i2 = 0; i2 < this.c; i2++) {
                Object[][] objArr2 = this.f;
                System.arraycopy(objArr2[i2], 0, objArr, i, objArr2[i2].length);
                i += this.f[i2].length;
            }
            int i3 = this.b;
            if (i3 > 0) {
                System.arraycopy(this.e, 0, objArr, i, i3);
            }
        }
    }

    public Iterator iterator() {
        return u.i(spliterator());
    }

    /* access modifiers changed from: protected */
    public long s() {
        int i = this.c;
        if (i == 0) {
            return (long) this.e.length;
        }
        return ((long) this.f[i].length) + this.d[i];
    }

    public Spliterator spliterator() {
        return new a(0, this.c, 0, this.b);
    }

    /* access modifiers changed from: protected */
    public final void t(long j) {
        long s = s();
        if (j > s) {
            u();
            int i = this.c;
            while (true) {
                i++;
                if (j > s) {
                    Object[][] objArr = this.f;
                    if (i >= objArr.length) {
                        int length = objArr.length * 2;
                        this.f = (Object[][]) Arrays.copyOf(objArr, length);
                        this.d = Arrays.copyOf(this.d, length);
                    }
                    int r = r(i);
                    Object[][] objArr2 = this.f;
                    objArr2[i] = new Object[r];
                    long[] jArr = this.d;
                    int i2 = i - 1;
                    jArr[i] = jArr[i2] + ((long) objArr2[i2].length);
                    s += (long) r;
                } else {
                    return;
                }
            }
        }
    }

    public String toString() {
        ArrayList arrayList = new ArrayList();
        forEach((Consumer) new CLASSNAMEb1(arrayList));
        return "SpinedBuffer:" + arrayList.toString();
    }
}

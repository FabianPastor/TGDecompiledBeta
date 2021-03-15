package j$.util.stream;

import a.CLASSNAMEz;
import j$.lang.Iterable;
import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEf;
import j$.util.function.CLASSNAMEg;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.k;
import j$.util.s;
import j$.util.v;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

class S2<E> extends CLASSNAMEj1 implements Consumer<E>, Iterable<E>, Iterable {
    protected Object[] e = new Object[16];
    protected Object[][] f;

    class a implements Spliterator<E> {

        /* renamed from: a  reason: collision with root package name */
        int var_a;
        final int b;
        int c;
        final int d;
        Object[] e;

        a(int i, int i2, int i3, int i4) {
            this.var_a = i;
            this.b = i2;
            this.c = i3;
            this.d = i4;
            Object[][] objArr = S2.this.f;
            this.e = objArr == null ? S2.this.e : objArr[i];
        }

        public boolean b(Consumer consumer) {
            consumer.getClass();
            int i = this.var_a;
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
                int i4 = this.var_a + 1;
                this.var_a = i4;
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
            int i = this.var_a;
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
            int i2 = this.var_a;
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
                Object[] objArr2 = this.var_a == i ? this.e : S2.this.f[i];
                int i5 = this.d;
                while (i4 < i5) {
                    consumer.accept(objArr2[i4]);
                    i4++;
                }
                this.var_a = this.b;
                this.c = this.d;
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

        public Spliterator trySplit() {
            int i = this.var_a;
            int i2 = this.b;
            if (i < i2) {
                S2 s2 = S2.this;
                int i3 = i2 - 1;
                a aVar = new a(i, i3, this.c, s2.f[i3].length);
                int i4 = this.b;
                this.var_a = i4;
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
                Spliterator m = v.m(this.e, i6, i6 + i7, 1040);
                this.c += i7;
                return m;
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
                return s.d(this, consumer);
            }

            /* access modifiers changed from: package-private */
            public Spliterator.d f(Object obj, int i, int i2) {
                return v.j((double[]) obj, i, i2 + i, 1040);
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                s.a(this, consumer);
            }

            /* access modifiers changed from: package-private */
            public Spliterator.d g(int i, int i2, int i3, int i4) {
                return new a(i, i2, i3, i4);
            }
        }

        b() {
        }

        b(int i) {
            super(i);
        }

        /* renamed from: B */
        public Spliterator.a spliterator() {
            return new a(0, this.c, 0, this.b);
        }

        public void accept(double d) {
            A();
            int i = this.b;
            this.b = i + 1;
            ((double[]) this.e)[i] = d;
        }

        public Object c(int i) {
            return new double[i];
        }

        public void forEach(Consumer consumer) {
            if (consumer instanceof q) {
                h((q) consumer);
            } else if (!i3.var_a) {
                spliterator().forEachRemaining(consumer);
            } else {
                i3.a(getClass(), "{0} calling SpinedBuffer.OfDouble.forEach(Consumer)");
                throw null;
            }
        }

        public Iterator iterator() {
            return v.f(spliterator());
        }

        public q k(q qVar) {
            qVar.getClass();
            return new CLASSNAMEf(this, qVar);
        }

        /* access modifiers changed from: protected */
        public void t(Object obj, int i, int i2, Object obj2) {
            double[] dArr = (double[]) obj;
            q qVar = (q) obj2;
            while (i < i2) {
                qVar.accept(dArr[i]);
                i++;
            }
        }

        public String toString() {
            double[] dArr = (double[]) e();
            if (dArr.length < 200) {
                return String.format("%s[length=%d, chunks=%d]%s", new Object[]{getClass().getSimpleName(), Integer.valueOf(dArr.length), Integer.valueOf(this.c), Arrays.toString(dArr)});
            }
            return String.format("%s[length=%d, chunks=%d]%s...", new Object[]{getClass().getSimpleName(), Integer.valueOf(dArr.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(dArr, 200))});
        }

        /* access modifiers changed from: protected */
        public int u(Object obj) {
            return ((double[]) obj).length;
        }

        /* access modifiers changed from: protected */
        public Object[] z(int i) {
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
                return s.e(this, consumer);
            }

            /* access modifiers changed from: package-private */
            public Spliterator.d f(Object obj, int i, int i2) {
                return v.k((int[]) obj, i, i2 + i, 1040);
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                s.b(this, consumer);
            }

            /* access modifiers changed from: package-private */
            public Spliterator.d g(int i, int i2, int i3, int i4) {
                return new a(i, i2, i3, i4);
            }
        }

        c() {
        }

        c(int i) {
            super(i);
        }

        /* renamed from: B */
        public Spliterator.b spliterator() {
            return new a(0, this.c, 0, this.b);
        }

        public void accept(int i) {
            A();
            int i2 = this.b;
            this.b = i2 + 1;
            ((int[]) this.e)[i2] = i;
        }

        public Object c(int i) {
            return new int[i];
        }

        public void forEach(Consumer consumer) {
            if (consumer instanceof w) {
                h((w) consumer);
            } else if (!i3.var_a) {
                spliterator().forEachRemaining(consumer);
            } else {
                i3.a(getClass(), "{0} calling SpinedBuffer.OfInt.forEach(Consumer)");
                throw null;
            }
        }

        public Iterator iterator() {
            return v.g(spliterator());
        }

        public w l(w wVar) {
            wVar.getClass();
            return new CLASSNAMEg(this, wVar);
        }

        /* access modifiers changed from: protected */
        public void t(Object obj, int i, int i2, Object obj2) {
            int[] iArr = (int[]) obj;
            w wVar = (w) obj2;
            while (i < i2) {
                wVar.accept(iArr[i]);
                i++;
            }
        }

        public String toString() {
            int[] iArr = (int[]) e();
            if (iArr.length < 200) {
                return String.format("%s[length=%d, chunks=%d]%s", new Object[]{getClass().getSimpleName(), Integer.valueOf(iArr.length), Integer.valueOf(this.c), Arrays.toString(iArr)});
            }
            return String.format("%s[length=%d, chunks=%d]%s...", new Object[]{getClass().getSimpleName(), Integer.valueOf(iArr.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(iArr, 200))});
        }

        /* access modifiers changed from: protected */
        public int u(Object obj) {
            return ((int[]) obj).length;
        }

        /* access modifiers changed from: protected */
        public Object[] z(int i) {
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
                return s.f(this, consumer);
            }

            /* access modifiers changed from: package-private */
            public Spliterator.d f(Object obj, int i, int i2) {
                return v.l((long[]) obj, i, i2 + i, 1040);
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                s.c(this, consumer);
            }

            /* access modifiers changed from: package-private */
            public Spliterator.d g(int i, int i2, int i3, int i4) {
                return new a(i, i2, i3, i4);
            }
        }

        d() {
        }

        d(int i) {
            super(i);
        }

        /* renamed from: B */
        public Spliterator.c spliterator() {
            return new a(0, this.c, 0, this.b);
        }

        public void accept(long j) {
            A();
            int i = this.b;
            this.b = i + 1;
            ((long[]) this.e)[i] = j;
        }

        public Object c(int i) {
            return new long[i];
        }

        public void forEach(Consumer consumer) {
            if (consumer instanceof C) {
                h((C) consumer);
            } else if (!i3.var_a) {
                spliterator().forEachRemaining(consumer);
            } else {
                i3.a(getClass(), "{0} calling SpinedBuffer.OfLong.forEach(Consumer)");
                throw null;
            }
        }

        public C g(C c) {
            c.getClass();
            return new CLASSNAMEh(this, c);
        }

        public Iterator iterator() {
            return v.h(spliterator());
        }

        /* access modifiers changed from: protected */
        public void t(Object obj, int i, int i2, Object obj2) {
            long[] jArr = (long[]) obj;
            C c = (C) obj2;
            while (i < i2) {
                c.accept(jArr[i]);
                i++;
            }
        }

        public String toString() {
            long[] jArr = (long[]) e();
            if (jArr.length < 200) {
                return String.format("%s[length=%d, chunks=%d]%s", new Object[]{getClass().getSimpleName(), Integer.valueOf(jArr.length), Integer.valueOf(this.c), Arrays.toString(jArr)});
            }
            return String.format("%s[length=%d, chunks=%d]%s...", new Object[]{getClass().getSimpleName(), Integer.valueOf(jArr.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(jArr, 200))});
        }

        /* access modifiers changed from: protected */
        public int u(Object obj) {
            return ((long[]) obj).length;
        }

        /* access modifiers changed from: protected */
        public Object[] z(int i) {
            return new long[i][];
        }
    }

    static abstract class e<E, T_ARR, T_CONS> extends CLASSNAMEj1 implements Iterable<E>, Iterable {
        Object e = c(16);
        Object[] f;

        abstract class a<T_SPLITR extends Spliterator.d<E, T_CONS, T_SPLITR>> implements Spliterator.d<E, T_CONS, T_SPLITR> {

            /* renamed from: a  reason: collision with root package name */
            int var_a;
            final int b;
            int c;
            final int d;
            Object e;

            a(int i, int i2, int i3, int i4) {
                this.var_a = i;
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
                int i = this.var_a;
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
                int i2 = this.var_a;
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
                        eVar.t(obj2, i4, eVar.u(obj2), obj);
                        i4 = 0;
                        i2++;
                    }
                    e.this.t(this.var_a == i ? this.e : e.this.f[i], i4, this.d, obj);
                    this.var_a = this.b;
                    this.c = this.d;
                }
            }

            /* access modifiers changed from: package-private */
            public abstract Spliterator.d g(int i, int i2, int i3, int i4);

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
                obj.getClass();
                int i = this.var_a;
                int i2 = this.b;
                if (i >= i2 && (i != i2 || this.c >= this.d)) {
                    return false;
                }
                Object obj2 = this.e;
                int i3 = this.c;
                this.c = i3 + 1;
                a(obj2, i3, obj);
                if (this.c == e.this.u(this.e)) {
                    this.c = 0;
                    int i4 = this.var_a + 1;
                    this.var_a = i4;
                    Object[] objArr = e.this.f;
                    if (objArr != null && i4 <= this.b) {
                        this.e = objArr[i4];
                    }
                }
                return true;
            }

            public Spliterator.d trySplit() {
                int i = this.var_a;
                int i2 = this.b;
                if (i < i2) {
                    int i3 = this.c;
                    e eVar = e.this;
                    Spliterator.d g = g(i, i2 - 1, i3, eVar.u(eVar.f[i2 - 1]));
                    int i4 = this.b;
                    this.var_a = i4;
                    this.c = 0;
                    this.e = e.this.f[i4];
                    return g;
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

        private void y() {
            if (this.f == null) {
                Object[] z = z(8);
                this.f = z;
                this.d = new long[8];
                z[0] = this.e;
            }
        }

        /* access modifiers changed from: protected */
        public void A() {
            if (this.b == u(this.e)) {
                y();
                int i = this.c;
                int i2 = i + 1;
                Object[] objArr = this.f;
                if (i2 >= objArr.length || objArr[i + 1] == null) {
                    x(v() + 1);
                }
                this.b = 0;
                int i3 = this.c + 1;
                this.c = i3;
                this.e = this.f[i3];
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
            if (count > ((long) u(obj)) || count < j) {
                throw new IndexOutOfBoundsException("does not fit");
            } else if (this.c == 0) {
                System.arraycopy(this.e, 0, obj, i, this.b);
            } else {
                for (int i2 = 0; i2 < this.c; i2++) {
                    Object[] objArr = this.f;
                    System.arraycopy(objArr[i2], 0, obj, i, u(objArr[i2]));
                    i += u(this.f[i2]);
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

        public void h(Object obj) {
            for (int i = 0; i < this.c; i++) {
                Object[] objArr = this.f;
                t(objArr[i], 0, u(objArr[i]), obj);
            }
            t(this.e, 0, this.b, obj);
        }

        public abstract Spliterator spliterator();

        /* access modifiers changed from: protected */
        public abstract void t(Object obj, int i, int i2, Object obj2);

        /* access modifiers changed from: protected */
        public abstract int u(Object obj);

        /* access modifiers changed from: protected */
        public long v() {
            int i = this.c;
            if (i == 0) {
                return (long) u(this.e);
            }
            return ((long) u(this.f[i])) + this.d[i];
        }

        /* access modifiers changed from: protected */
        public int w(long j) {
            if (this.c == 0) {
                if (j < ((long) this.b)) {
                    return 0;
                }
                throw new IndexOutOfBoundsException(Long.toString(j));
            } else if (j < count()) {
                for (int i = 0; i <= this.c; i++) {
                    if (j < this.d[i] + ((long) u(this.f[i]))) {
                        return i;
                    }
                }
                throw new IndexOutOfBoundsException(Long.toString(j));
            } else {
                throw new IndexOutOfBoundsException(Long.toString(j));
            }
        }

        /* access modifiers changed from: protected */
        public final void x(long j) {
            long v = v();
            if (j > v) {
                y();
                int i = this.c;
                while (true) {
                    i++;
                    if (j > v) {
                        Object[] objArr = this.f;
                        if (i >= objArr.length) {
                            int length = objArr.length * 2;
                            this.f = Arrays.copyOf(objArr, length);
                            this.d = Arrays.copyOf(this.d, length);
                        }
                        int s = s(i);
                        this.f[i] = c(s);
                        long[] jArr = this.d;
                        int i2 = i - 1;
                        jArr[i] = jArr[i2] + ((long) u(this.f[i2]));
                        v += (long) s;
                    } else {
                        return;
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public abstract Object[] z(int i);
    }

    S2() {
    }

    private void v() {
        if (this.f == null) {
            Object[][] objArr = new Object[8][];
            this.f = objArr;
            this.d = new long[8];
            objArr[0] = this.e;
        }
    }

    public void accept(Object obj) {
        if (this.b == this.e.length) {
            v();
            int i = this.c;
            int i2 = i + 1;
            Object[][] objArr = this.f;
            if (i2 >= objArr.length || objArr[i + 1] == null) {
                u(t() + 1);
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

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
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
        forEach(CLASSNAMEz.b(consumer));
    }

    public Iterator iterator() {
        return v.i(spliterator());
    }

    public void j(Object[] objArr, int i) {
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

    public Spliterator spliterator() {
        return new a(0, this.c, 0, this.b);
    }

    /* access modifiers changed from: protected */
    public long t() {
        int i = this.c;
        if (i == 0) {
            return (long) this.e.length;
        }
        return ((long) this.f[i].length) + this.d[i];
    }

    public String toString() {
        ArrayList arrayList = new ArrayList();
        forEach((Consumer) new CLASSNAMEb1(arrayList));
        return "SpinedBuffer:" + arrayList.toString();
    }

    /* access modifiers changed from: protected */
    public final void u(long j) {
        long t = t();
        if (j > t) {
            v();
            int i = this.c;
            while (true) {
                i++;
                if (j > t) {
                    Object[][] objArr = this.f;
                    if (i >= objArr.length) {
                        int length = objArr.length * 2;
                        this.f = (Object[][]) Arrays.copyOf(objArr, length);
                        this.d = Arrays.copyOf(this.d, length);
                    }
                    int s = s(i);
                    Object[][] objArr2 = this.f;
                    objArr2[i] = new Object[s];
                    long[] jArr = this.d;
                    int i2 = i - 1;
                    jArr[i] = jArr[i2] + ((long) objArr2[i2].length);
                    t += (long) s;
                } else {
                    return;
                }
            }
        }
    }
}

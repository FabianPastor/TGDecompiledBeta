package j$.util.stream;

import j$.lang.Iterable;
import j$.util.DesugarArrays;
import j$.util.PrimitiveIterator;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.IntFunction;
import j$.util.function.LongConsumer;
import j$.wrappers.C$r8$wrapper$java$util$function$Consumer$VWRP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

class SpinedBuffer<E> extends AbstractSpinedBuffer implements Consumer<E>, Iterable<E>, Iterable<E> {
    private static final int SPLITERATOR_CHARACTERISTICS = 16464;
    protected E[] curChunk = new Object[(1 << this.initialChunkPower)];
    protected E[][] spine;

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
        forEach(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
    }

    SpinedBuffer(int initialCapacity) {
        super(initialCapacity);
    }

    SpinedBuffer() {
    }

    /* access modifiers changed from: protected */
    public long capacity() {
        if (this.spineIndex == 0) {
            return (long) this.curChunk.length;
        }
        return this.priorElementCount[this.spineIndex] + ((long) this.spine[this.spineIndex].length);
    }

    private void inflateSpine() {
        if (this.spine == null) {
            this.spine = new Object[8][];
            this.priorElementCount = new long[8];
            this.spine[0] = this.curChunk;
        }
    }

    /* access modifiers changed from: protected */
    public final void ensureCapacity(long targetSize) {
        long capacity = capacity();
        if (targetSize > capacity) {
            inflateSpine();
            int i = this.spineIndex;
            while (true) {
                i++;
                if (targetSize > capacity) {
                    E[][] eArr = this.spine;
                    if (i >= eArr.length) {
                        int newSpineSize = eArr.length * 2;
                        this.spine = (Object[][]) Arrays.copyOf(eArr, newSpineSize);
                        this.priorElementCount = Arrays.copyOf(this.priorElementCount, newSpineSize);
                    }
                    int nextChunkSize = chunkSize(i);
                    this.spine[i] = new Object[nextChunkSize];
                    this.priorElementCount[i] = this.priorElementCount[i - 1] + ((long) this.spine[i - 1].length);
                    capacity += (long) nextChunkSize;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void increaseCapacity() {
        ensureCapacity(capacity() + 1);
    }

    public E get(long index) {
        if (this.spineIndex == 0) {
            if (index < ((long) this.elementIndex)) {
                return this.curChunk[(int) index];
            }
            throw new IndexOutOfBoundsException(Long.toString(index));
        } else if (index < count()) {
            for (int j = 0; j <= this.spineIndex; j++) {
                long j2 = this.priorElementCount[j];
                E[][] eArr = this.spine;
                if (index < j2 + ((long) eArr[j].length)) {
                    return eArr[j][(int) (index - this.priorElementCount[j])];
                }
            }
            throw new IndexOutOfBoundsException(Long.toString(index));
        } else {
            throw new IndexOutOfBoundsException(Long.toString(index));
        }
    }

    public void copyInto(E[] array, int offset) {
        long finalOffset = ((long) offset) + count();
        if (finalOffset > ((long) array.length) || finalOffset < ((long) offset)) {
            throw new IndexOutOfBoundsException("does not fit");
        } else if (this.spineIndex == 0) {
            System.arraycopy(this.curChunk, 0, array, offset, this.elementIndex);
        } else {
            for (int i = 0; i < this.spineIndex; i++) {
                E[][] eArr = this.spine;
                System.arraycopy(eArr[i], 0, array, offset, eArr[i].length);
                offset += this.spine[i].length;
            }
            if (this.elementIndex > 0) {
                System.arraycopy(this.curChunk, 0, array, offset, this.elementIndex);
            }
        }
    }

    public E[] asArray(IntFunction<E[]> intFunction) {
        long size = count();
        if (size < NUM) {
            E[] result = (Object[]) intFunction.apply((int) size);
            copyInto(result, 0);
            return result;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public void clear() {
        E[][] eArr = this.spine;
        if (eArr != null) {
            this.curChunk = eArr[0];
            int i = 0;
            while (true) {
                E[] eArr2 = this.curChunk;
                if (i >= eArr2.length) {
                    break;
                }
                eArr2[i] = null;
                i++;
            }
            this.spine = null;
            this.priorElementCount = null;
        } else {
            for (int i2 = 0; i2 < this.elementIndex; i2++) {
                this.curChunk[i2] = null;
            }
        }
        this.elementIndex = 0;
        this.spineIndex = 0;
    }

    public Iterator<E> iterator() {
        return Spliterators.iterator(Iterable.EL.spliterator(this));
    }

    public void forEach(Consumer<? super E> consumer) {
        for (int j = 0; j < this.spineIndex; j++) {
            for (E t : this.spine[j]) {
                consumer.accept(t);
            }
        }
        for (int i = 0; i < this.elementIndex; i++) {
            consumer.accept(this.curChunk[i]);
        }
    }

    public void accept(E e) {
        if (this.elementIndex == this.curChunk.length) {
            inflateSpine();
            int i = this.spineIndex + 1;
            E[][] eArr = this.spine;
            if (i >= eArr.length || eArr[this.spineIndex + 1] == null) {
                increaseCapacity();
            }
            this.elementIndex = 0;
            this.spineIndex++;
            this.curChunk = this.spine[this.spineIndex];
        }
        E[] eArr2 = this.curChunk;
        int i2 = this.elementIndex;
        this.elementIndex = i2 + 1;
        eArr2[i2] = e;
    }

    public String toString() {
        List<E> list = new ArrayList<>();
        list.getClass();
        Iterable.EL.forEach(this, new SpinedBuffer$$ExternalSyntheticLambda0(list));
        return "SpinedBuffer:" + list.toString();
    }

    /* renamed from: j$.util.stream.SpinedBuffer$1Splitr  reason: invalid class name */
    class AnonymousClass1Splitr implements Spliterator<E> {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        final int lastSpineElementFence;
        final int lastSpineIndex;
        E[] splChunk;
        int splElementIndex;
        int splSpineIndex;

        public /* synthetic */ Comparator getComparator() {
            return Spliterator.CC.$default$getComparator(this);
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        {
            this.splSpineIndex = firstSpineIndex;
            this.lastSpineIndex = lastSpineIndex;
            this.splElementIndex = firstSpineElementIndex;
            this.lastSpineElementFence = lastSpineElementFence;
            if ($assertionsDisabled || SpinedBuffer.this.spine != null || (firstSpineIndex == 0 && lastSpineIndex == 0)) {
                this.splChunk = SpinedBuffer.this.spine == null ? SpinedBuffer.this.curChunk : SpinedBuffer.this.spine[firstSpineIndex];
                return;
            }
            throw new AssertionError();
        }

        public long estimateSize() {
            if (this.splSpineIndex == this.lastSpineIndex) {
                return ((long) this.lastSpineElementFence) - ((long) this.splElementIndex);
            }
            return ((SpinedBuffer.this.priorElementCount[this.lastSpineIndex] + ((long) this.lastSpineElementFence)) - SpinedBuffer.this.priorElementCount[this.splSpineIndex]) - ((long) this.splElementIndex);
        }

        public int characteristics() {
            return 16464;
        }

        public boolean tryAdvance(Consumer<? super E> consumer) {
            consumer.getClass();
            int i = this.splSpineIndex;
            int i2 = this.lastSpineIndex;
            if (i >= i2 && (i != i2 || this.splElementIndex >= this.lastSpineElementFence)) {
                return false;
            }
            E[] eArr = this.splChunk;
            int i3 = this.splElementIndex;
            this.splElementIndex = i3 + 1;
            consumer.accept(eArr[i3]);
            if (this.splElementIndex == this.splChunk.length) {
                this.splElementIndex = 0;
                this.splSpineIndex++;
                if (SpinedBuffer.this.spine != null && this.splSpineIndex <= this.lastSpineIndex) {
                    this.splChunk = SpinedBuffer.this.spine[this.splSpineIndex];
                }
            }
            return true;
        }

        public void forEachRemaining(Consumer<? super E> consumer) {
            int i;
            consumer.getClass();
            int i2 = this.splSpineIndex;
            int i3 = this.lastSpineIndex;
            if (i2 < i3 || (i2 == i3 && this.splElementIndex < this.lastSpineElementFence)) {
                int i4 = this.splElementIndex;
                int sp = this.splSpineIndex;
                while (true) {
                    i = this.lastSpineIndex;
                    if (sp >= i) {
                        break;
                    }
                    E[] chunk = SpinedBuffer.this.spine[sp];
                    while (i4 < chunk.length) {
                        consumer.accept(chunk[i4]);
                        i4++;
                    }
                    i4 = 0;
                    sp++;
                }
                E[] chunk2 = this.splSpineIndex == i ? this.splChunk : SpinedBuffer.this.spine[this.lastSpineIndex];
                int hElementIndex = this.lastSpineElementFence;
                while (i4 < hElementIndex) {
                    consumer.accept(chunk2[i4]);
                    i4++;
                }
                this.splSpineIndex = this.lastSpineIndex;
                this.splElementIndex = this.lastSpineElementFence;
            }
        }

        public Spliterator<E> trySplit() {
            int i = this.splSpineIndex;
            int i2 = this.lastSpineIndex;
            if (i < i2) {
                SpinedBuffer spinedBuffer = SpinedBuffer.this;
                AnonymousClass1Splitr r2 = new AnonymousClass1Splitr(this.splSpineIndex, this.lastSpineIndex - 1, this.splElementIndex, spinedBuffer.spine[this.lastSpineIndex - 1].length);
                this.splSpineIndex = this.lastSpineIndex;
                this.splElementIndex = 0;
                this.splChunk = SpinedBuffer.this.spine[this.splSpineIndex];
                return r2;
            } else if (i != i2) {
                return null;
            } else {
                int i3 = this.lastSpineElementFence;
                int i4 = this.splElementIndex;
                int t = (i3 - i4) / 2;
                if (t == 0) {
                    return null;
                }
                Spliterator<E> spliterator = DesugarArrays.spliterator((T[]) this.splChunk, i4, i4 + t);
                this.splElementIndex += t;
                return spliterator;
            }
        }
    }

    public Spliterator<E> spliterator() {
        return new AnonymousClass1Splitr(0, this.spineIndex, 0, this.elementIndex);
    }

    static abstract class OfPrimitive<E, T_ARR, T_CONS> extends AbstractSpinedBuffer implements Iterable<E>, Iterable<E> {
        T_ARR curChunk = newArray(1 << this.initialChunkPower);
        T_ARR[] spine;

        /* access modifiers changed from: protected */
        public abstract void arrayForEach(T_ARR t_arr, int i, int i2, T_CONS t_cons);

        /* access modifiers changed from: protected */
        public abstract int arrayLength(T_ARR t_arr);

        public abstract void forEach(Consumer<? super E> consumer);

        public abstract Iterator<E> iterator();

        public abstract T_ARR newArray(int i);

        /* access modifiers changed from: protected */
        public abstract T_ARR[] newArrayArray(int i);

        OfPrimitive(int initialCapacity) {
            super(initialCapacity);
        }

        OfPrimitive() {
        }

        public Spliterator<E> spliterator() {
            return Spliterators.spliteratorUnknownSize(iterator(), 0);
        }

        /* access modifiers changed from: protected */
        public long capacity() {
            if (this.spineIndex == 0) {
                return (long) arrayLength(this.curChunk);
            }
            return this.priorElementCount[this.spineIndex] + ((long) arrayLength(this.spine[this.spineIndex]));
        }

        private void inflateSpine() {
            if (this.spine == null) {
                this.spine = newArrayArray(8);
                this.priorElementCount = new long[8];
                this.spine[0] = this.curChunk;
            }
        }

        /* access modifiers changed from: protected */
        public final void ensureCapacity(long targetSize) {
            long capacity = capacity();
            if (targetSize > capacity) {
                inflateSpine();
                int i = this.spineIndex;
                while (true) {
                    i++;
                    if (targetSize > capacity) {
                        T_ARR[] t_arrArr = this.spine;
                        if (i >= t_arrArr.length) {
                            int newSpineSize = t_arrArr.length * 2;
                            this.spine = Arrays.copyOf(t_arrArr, newSpineSize);
                            this.priorElementCount = Arrays.copyOf(this.priorElementCount, newSpineSize);
                        }
                        int nextChunkSize = chunkSize(i);
                        this.spine[i] = newArray(nextChunkSize);
                        this.priorElementCount[i] = this.priorElementCount[i - 1] + ((long) arrayLength(this.spine[i - 1]));
                        capacity += (long) nextChunkSize;
                    } else {
                        return;
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void increaseCapacity() {
            ensureCapacity(capacity() + 1);
        }

        /* access modifiers changed from: protected */
        public int chunkFor(long index) {
            if (this.spineIndex == 0) {
                if (index < ((long) this.elementIndex)) {
                    return 0;
                }
                throw new IndexOutOfBoundsException(Long.toString(index));
            } else if (index < count()) {
                for (int j = 0; j <= this.spineIndex; j++) {
                    if (index < this.priorElementCount[j] + ((long) arrayLength(this.spine[j]))) {
                        return j;
                    }
                }
                throw new IndexOutOfBoundsException(Long.toString(index));
            } else {
                throw new IndexOutOfBoundsException(Long.toString(index));
            }
        }

        public void copyInto(T_ARR array, int offset) {
            long finalOffset = ((long) offset) + count();
            if (finalOffset > ((long) arrayLength(array)) || finalOffset < ((long) offset)) {
                throw new IndexOutOfBoundsException("does not fit");
            } else if (this.spineIndex == 0) {
                System.arraycopy(this.curChunk, 0, array, offset, this.elementIndex);
            } else {
                for (int i = 0; i < this.spineIndex; i++) {
                    T_ARR[] t_arrArr = this.spine;
                    System.arraycopy(t_arrArr[i], 0, array, offset, arrayLength(t_arrArr[i]));
                    offset += arrayLength(this.spine[i]);
                }
                if (this.elementIndex > 0) {
                    System.arraycopy(this.curChunk, 0, array, offset, this.elementIndex);
                }
            }
        }

        public T_ARR asPrimitiveArray() {
            long size = count();
            if (size < NUM) {
                T_ARR result = newArray((int) size);
                copyInto(result, 0);
                return result;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        /* access modifiers changed from: protected */
        public void preAccept() {
            if (this.elementIndex == arrayLength(this.curChunk)) {
                inflateSpine();
                int i = this.spineIndex + 1;
                T_ARR[] t_arrArr = this.spine;
                if (i >= t_arrArr.length || t_arrArr[this.spineIndex + 1] == null) {
                    increaseCapacity();
                }
                this.elementIndex = 0;
                this.spineIndex++;
                this.curChunk = this.spine[this.spineIndex];
            }
        }

        public void clear() {
            T_ARR[] t_arrArr = this.spine;
            if (t_arrArr != null) {
                this.curChunk = t_arrArr[0];
                this.spine = null;
                this.priorElementCount = null;
            }
            this.elementIndex = 0;
            this.spineIndex = 0;
        }

        public void forEach(T_CONS consumer) {
            for (int j = 0; j < this.spineIndex; j++) {
                T_ARR[] t_arrArr = this.spine;
                arrayForEach(t_arrArr[j], 0, arrayLength(t_arrArr[j]), consumer);
            }
            arrayForEach(this.curChunk, 0, this.elementIndex, consumer);
        }

        abstract class BaseSpliterator<T_SPLITR extends Spliterator.OfPrimitive<E, T_CONS, T_SPLITR>> implements Spliterator.OfPrimitive<E, T_CONS, T_SPLITR> {
            static final /* synthetic */ boolean $assertionsDisabled = true;
            final int lastSpineElementFence;
            final int lastSpineIndex;
            T_ARR splChunk;
            int splElementIndex;
            int splSpineIndex;

            /* access modifiers changed from: package-private */
            public abstract void arrayForOne(T_ARR t_arr, int i, T_CONS t_cons);

            /* access modifiers changed from: package-private */
            public abstract T_SPLITR arraySpliterator(T_ARR t_arr, int i, int i2);

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.CC.$default$forEachRemaining(this, consumer);
            }

            public /* synthetic */ Comparator getComparator() {
                return Spliterator.CC.$default$getComparator(this);
            }

            public /* synthetic */ long getExactSizeIfKnown() {
                return Spliterator.CC.$default$getExactSizeIfKnown(this);
            }

            public /* synthetic */ boolean hasCharacteristics(int i) {
                return Spliterator.CC.$default$hasCharacteristics(this, i);
            }

            /* access modifiers changed from: package-private */
            public abstract T_SPLITR newSpliterator(int i, int i2, int i3, int i4);

            BaseSpliterator(int firstSpineIndex, int lastSpineIndex2, int firstSpineElementIndex, int lastSpineElementFence2) {
                this.splSpineIndex = firstSpineIndex;
                this.lastSpineIndex = lastSpineIndex2;
                this.splElementIndex = firstSpineElementIndex;
                this.lastSpineElementFence = lastSpineElementFence2;
                if ($assertionsDisabled || OfPrimitive.this.spine != null || (firstSpineIndex == 0 && lastSpineIndex2 == 0)) {
                    this.splChunk = OfPrimitive.this.spine == null ? OfPrimitive.this.curChunk : OfPrimitive.this.spine[firstSpineIndex];
                    return;
                }
                throw new AssertionError();
            }

            public long estimateSize() {
                if (this.splSpineIndex == this.lastSpineIndex) {
                    return ((long) this.lastSpineElementFence) - ((long) this.splElementIndex);
                }
                return ((OfPrimitive.this.priorElementCount[this.lastSpineIndex] + ((long) this.lastSpineElementFence)) - OfPrimitive.this.priorElementCount[this.splSpineIndex]) - ((long) this.splElementIndex);
            }

            public int characteristics() {
                return 16464;
            }

            public boolean tryAdvance(T_CONS consumer) {
                consumer.getClass();
                int i = this.splSpineIndex;
                int i2 = this.lastSpineIndex;
                if (i >= i2 && (i != i2 || this.splElementIndex >= this.lastSpineElementFence)) {
                    return false;
                }
                T_ARR t_arr = this.splChunk;
                int i3 = this.splElementIndex;
                this.splElementIndex = i3 + 1;
                arrayForOne(t_arr, i3, consumer);
                if (this.splElementIndex == OfPrimitive.this.arrayLength(this.splChunk)) {
                    this.splElementIndex = 0;
                    this.splSpineIndex++;
                    if (OfPrimitive.this.spine != null && this.splSpineIndex <= this.lastSpineIndex) {
                        this.splChunk = OfPrimitive.this.spine[this.splSpineIndex];
                    }
                }
                return true;
            }

            public void forEachRemaining(T_CONS consumer) {
                int i;
                consumer.getClass();
                int i2 = this.splSpineIndex;
                int i3 = this.lastSpineIndex;
                if (i2 < i3 || (i2 == i3 && this.splElementIndex < this.lastSpineElementFence)) {
                    int i4 = this.splElementIndex;
                    int sp = this.splSpineIndex;
                    while (true) {
                        i = this.lastSpineIndex;
                        if (sp >= i) {
                            break;
                        }
                        T_ARR chunk = OfPrimitive.this.spine[sp];
                        OfPrimitive ofPrimitive = OfPrimitive.this;
                        ofPrimitive.arrayForEach(chunk, i4, ofPrimitive.arrayLength(chunk), consumer);
                        i4 = 0;
                        sp++;
                    }
                    OfPrimitive.this.arrayForEach(this.splSpineIndex == i ? this.splChunk : OfPrimitive.this.spine[this.lastSpineIndex], i4, this.lastSpineElementFence, consumer);
                    this.splSpineIndex = this.lastSpineIndex;
                    this.splElementIndex = this.lastSpineElementFence;
                }
            }

            public T_SPLITR trySplit() {
                int i = this.splSpineIndex;
                int i2 = this.lastSpineIndex;
                if (i < i2) {
                    int i3 = this.splElementIndex;
                    OfPrimitive ofPrimitive = OfPrimitive.this;
                    T_SPLITR ret = newSpliterator(i, i2 - 1, i3, ofPrimitive.arrayLength(ofPrimitive.spine[this.lastSpineIndex - 1]));
                    this.splSpineIndex = this.lastSpineIndex;
                    this.splElementIndex = 0;
                    this.splChunk = OfPrimitive.this.spine[this.splSpineIndex];
                    return ret;
                } else if (i != i2) {
                    return null;
                } else {
                    int i4 = this.lastSpineElementFence;
                    int i5 = this.splElementIndex;
                    int t = (i4 - i5) / 2;
                    if (t == 0) {
                        return null;
                    }
                    T_SPLITR ret2 = arraySpliterator(this.splChunk, i5, t);
                    this.splElementIndex += t;
                    return ret2;
                }
            }
        }
    }

    static class OfInt extends OfPrimitive<Integer, int[], IntConsumer> implements IntConsumer {
        public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
            return IntConsumer.CC.$default$andThen(this, intConsumer);
        }

        OfInt() {
        }

        OfInt(int initialCapacity) {
            super(initialCapacity);
        }

        public void forEach(Consumer<? super Integer> consumer) {
            if (consumer instanceof IntConsumer) {
                forEach((IntConsumer) consumer);
                return;
            }
            if (Tripwire.ENABLED) {
                Tripwire.trip(getClass(), "{0} calling SpinedBuffer.OfInt.forEach(Consumer)");
            }
            spliterator().forEachRemaining(consumer);
        }

        /* access modifiers changed from: protected */
        public int[][] newArrayArray(int size) {
            return new int[size][];
        }

        public int[] newArray(int size) {
            return new int[size];
        }

        /* access modifiers changed from: protected */
        public int arrayLength(int[] array) {
            return array.length;
        }

        /* access modifiers changed from: protected */
        public void arrayForEach(int[] array, int from, int to, IntConsumer consumer) {
            for (int i = from; i < to; i++) {
                consumer.accept(array[i]);
            }
        }

        public void accept(int i) {
            preAccept();
            int i2 = this.elementIndex;
            this.elementIndex = i2 + 1;
            ((int[]) this.curChunk)[i2] = i;
        }

        public int get(long index) {
            int ch = chunkFor(index);
            if (this.spineIndex == 0 && ch == 0) {
                return ((int[]) this.curChunk)[(int) index];
            }
            return ((int[][]) this.spine)[ch][(int) (index - this.priorElementCount[ch])];
        }

        public PrimitiveIterator.OfInt iterator() {
            return Spliterators.iterator(spliterator());
        }

        public Spliterator.OfInt spliterator() {
            return new Spliterator.OfInt(0, this.spineIndex, 0, this.elementIndex) {
                public /* synthetic */ void forEachRemaining(Consumer consumer) {
                    Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
                }

                public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                    return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
                }

                public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                    super.forEachRemaining(intConsumer);
                }

                public /* bridge */ /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
                    return super.tryAdvance(intConsumer);
                }

                public /* bridge */ /* synthetic */ Spliterator.OfInt trySplit() {
                    return (Spliterator.OfInt) super.trySplit();
                }

                /* access modifiers changed from: package-private */
                public AnonymousClass1Splitr newSpliterator(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                    return 

                    static class OfLong extends OfPrimitive<Long, long[], LongConsumer> implements LongConsumer {
                        public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                            return LongConsumer.CC.$default$andThen(this, longConsumer);
                        }

                        OfLong() {
                        }

                        OfLong(int initialCapacity) {
                            super(initialCapacity);
                        }

                        public void forEach(Consumer<? super Long> consumer) {
                            if (consumer instanceof LongConsumer) {
                                forEach((LongConsumer) consumer);
                                return;
                            }
                            if (Tripwire.ENABLED) {
                                Tripwire.trip(getClass(), "{0} calling SpinedBuffer.OfLong.forEach(Consumer)");
                            }
                            spliterator().forEachRemaining(consumer);
                        }

                        /* access modifiers changed from: protected */
                        public long[][] newArrayArray(int size) {
                            return new long[size][];
                        }

                        public long[] newArray(int size) {
                            return new long[size];
                        }

                        /* access modifiers changed from: protected */
                        public int arrayLength(long[] array) {
                            return array.length;
                        }

                        /* access modifiers changed from: protected */
                        public void arrayForEach(long[] array, int from, int to, LongConsumer consumer) {
                            for (int i = from; i < to; i++) {
                                consumer.accept(array[i]);
                            }
                        }

                        public void accept(long i) {
                            preAccept();
                            int i2 = this.elementIndex;
                            this.elementIndex = i2 + 1;
                            ((long[]) this.curChunk)[i2] = i;
                        }

                        public long get(long index) {
                            int ch = chunkFor(index);
                            if (this.spineIndex == 0 && ch == 0) {
                                return ((long[]) this.curChunk)[(int) index];
                            }
                            return ((long[][]) this.spine)[ch][(int) (index - this.priorElementCount[ch])];
                        }

                        public PrimitiveIterator.OfLong iterator() {
                            return Spliterators.iterator(spliterator());
                        }

                        public Spliterator.OfLong spliterator() {
                            return new Spliterator.OfLong(0, this.spineIndex, 0, this.elementIndex) {
                                public /* synthetic */ void forEachRemaining(Consumer consumer) {
                                    Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
                                }

                                public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                                    return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
                                }

                                public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                                    super.forEachRemaining(longConsumer);
                                }

                                public /* bridge */ /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
                                    return super.tryAdvance(longConsumer);
                                }

                                public /* bridge */ /* synthetic */ Spliterator.OfLong trySplit() {
                                    return (Spliterator.OfLong) super.trySplit();
                                }

                                /* access modifiers changed from: package-private */
                                public AnonymousClass1Splitr newSpliterator(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                                    return 

                                    static class OfDouble extends OfPrimitive<Double, double[], DoubleConsumer> implements DoubleConsumer {
                                        public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                                            return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
                                        }

                                        OfDouble() {
                                        }

                                        OfDouble(int initialCapacity) {
                                            super(initialCapacity);
                                        }

                                        public void forEach(Consumer<? super Double> consumer) {
                                            if (consumer instanceof DoubleConsumer) {
                                                forEach((DoubleConsumer) consumer);
                                                return;
                                            }
                                            if (Tripwire.ENABLED) {
                                                Tripwire.trip(getClass(), "{0} calling SpinedBuffer.OfDouble.forEach(Consumer)");
                                            }
                                            spliterator().forEachRemaining(consumer);
                                        }

                                        /* access modifiers changed from: protected */
                                        public double[][] newArrayArray(int size) {
                                            return new double[size][];
                                        }

                                        public double[] newArray(int size) {
                                            return new double[size];
                                        }

                                        /* access modifiers changed from: protected */
                                        public int arrayLength(double[] array) {
                                            return array.length;
                                        }

                                        /* access modifiers changed from: protected */
                                        public void arrayForEach(double[] array, int from, int to, DoubleConsumer consumer) {
                                            for (int i = from; i < to; i++) {
                                                consumer.accept(array[i]);
                                            }
                                        }

                                        public void accept(double i) {
                                            preAccept();
                                            int i2 = this.elementIndex;
                                            this.elementIndex = i2 + 1;
                                            ((double[]) this.curChunk)[i2] = i;
                                        }

                                        public double get(long index) {
                                            int ch = chunkFor(index);
                                            if (this.spineIndex == 0 && ch == 0) {
                                                return ((double[]) this.curChunk)[(int) index];
                                            }
                                            return ((double[][]) this.spine)[ch][(int) (index - this.priorElementCount[ch])];
                                        }

                                        public PrimitiveIterator.OfDouble iterator() {
                                            return Spliterators.iterator(spliterator());
                                        }

                                        public Spliterator.OfDouble spliterator() {
                                            return new Spliterator.OfDouble(0, this.spineIndex, 0, this.elementIndex) {
                                                public /* synthetic */ void forEachRemaining(Consumer consumer) {
                                                    Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
                                                }

                                                public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                                                    return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
                                                }

                                                public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                                                    super.forEachRemaining(doubleConsumer);
                                                }

                                                public /* bridge */ /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
                                                    return super.tryAdvance(doubleConsumer);
                                                }

                                                public /* bridge */ /* synthetic */ Spliterator.OfDouble trySplit() {
                                                    return (Spliterator.OfDouble) super.trySplit();
                                                }

                                                /* access modifiers changed from: package-private */
                                                public AnonymousClass1Splitr newSpliterator(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                                                    return 
                                                }

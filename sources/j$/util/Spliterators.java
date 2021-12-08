package j$.util;

import j$.util.Iterator;
import j$.util.PrimitiveIterator;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.LongConsumer;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;

public final class Spliterators {
    private static final Spliterator.OfDouble EMPTY_DOUBLE_SPLITERATOR = new EmptySpliterator.OfDouble();
    private static final Spliterator.OfInt EMPTY_INT_SPLITERATOR = new EmptySpliterator.OfInt();
    private static final Spliterator.OfLong EMPTY_LONG_SPLITERATOR = new EmptySpliterator.OfLong();
    private static final Spliterator<Object> EMPTY_SPLITERATOR = new EmptySpliterator.OfRef();

    private Spliterators() {
    }

    public static <T> Spliterator<T> emptySpliterator() {
        return EMPTY_SPLITERATOR;
    }

    public static Spliterator.OfInt emptyIntSpliterator() {
        return EMPTY_INT_SPLITERATOR;
    }

    public static Spliterator.OfLong emptyLongSpliterator() {
        return EMPTY_LONG_SPLITERATOR;
    }

    public static Spliterator.OfDouble emptyDoubleSpliterator() {
        return EMPTY_DOUBLE_SPLITERATOR;
    }

    public static <T> Spliterator<T> spliterator(Object[] array, int additionalCharacteristics) {
        array.getClass();
        return new ArraySpliterator(array, additionalCharacteristics);
    }

    public static <T> Spliterator<T> spliterator(Object[] array, int fromIndex, int toIndex, int additionalCharacteristics) {
        array.getClass();
        checkFromToBounds(array.length, fromIndex, toIndex);
        return new ArraySpliterator(array, fromIndex, toIndex, additionalCharacteristics);
    }

    public static Spliterator.OfInt spliterator(int[] array, int additionalCharacteristics) {
        array.getClass();
        return new IntArraySpliterator(array, additionalCharacteristics);
    }

    public static Spliterator.OfInt spliterator(int[] array, int fromIndex, int toIndex, int additionalCharacteristics) {
        array.getClass();
        checkFromToBounds(array.length, fromIndex, toIndex);
        return new IntArraySpliterator(array, fromIndex, toIndex, additionalCharacteristics);
    }

    public static Spliterator.OfLong spliterator(long[] array, int additionalCharacteristics) {
        array.getClass();
        return new LongArraySpliterator(array, additionalCharacteristics);
    }

    public static Spliterator.OfLong spliterator(long[] array, int fromIndex, int toIndex, int additionalCharacteristics) {
        array.getClass();
        checkFromToBounds(array.length, fromIndex, toIndex);
        return new LongArraySpliterator(array, fromIndex, toIndex, additionalCharacteristics);
    }

    public static Spliterator.OfDouble spliterator(double[] array, int additionalCharacteristics) {
        array.getClass();
        return new DoubleArraySpliterator(array, additionalCharacteristics);
    }

    public static Spliterator.OfDouble spliterator(double[] array, int fromIndex, int toIndex, int additionalCharacteristics) {
        array.getClass();
        checkFromToBounds(array.length, fromIndex, toIndex);
        return new DoubleArraySpliterator(array, fromIndex, toIndex, additionalCharacteristics);
    }

    private static void checkFromToBounds(int arrayLength, int origin, int fence) {
        if (origin > fence) {
            throw new ArrayIndexOutOfBoundsException("origin(" + origin + ") > fence(" + fence + ")");
        } else if (origin < 0) {
            throw new ArrayIndexOutOfBoundsException(origin);
        } else if (fence > arrayLength) {
            throw new ArrayIndexOutOfBoundsException(fence);
        }
    }

    public static <T> Spliterator<T> spliterator(Collection<? extends T> c, int characteristics) {
        c.getClass();
        return new IteratorSpliterator(c, characteristics);
    }

    public static <T> Spliterator<T> spliterator(Iterator<? extends T> iterator, long size, int characteristics) {
        iterator.getClass();
        return new IteratorSpliterator(iterator, size, characteristics);
    }

    public static <T> Spliterator<T> spliteratorUnknownSize(Iterator<? extends T> iterator, int characteristics) {
        iterator.getClass();
        return new IteratorSpliterator(iterator, characteristics);
    }

    public static Spliterator.OfInt spliterator(PrimitiveIterator.OfInt iterator, long size, int characteristics) {
        iterator.getClass();
        return new IntIteratorSpliterator(iterator, size, characteristics);
    }

    public static Spliterator.OfInt spliteratorUnknownSize(PrimitiveIterator.OfInt iterator, int characteristics) {
        iterator.getClass();
        return new IntIteratorSpliterator(iterator, characteristics);
    }

    public static Spliterator.OfLong spliterator(PrimitiveIterator.OfLong iterator, long size, int characteristics) {
        iterator.getClass();
        return new LongIteratorSpliterator(iterator, size, characteristics);
    }

    public static Spliterator.OfLong spliteratorUnknownSize(PrimitiveIterator.OfLong iterator, int characteristics) {
        iterator.getClass();
        return new LongIteratorSpliterator(iterator, characteristics);
    }

    public static Spliterator.OfDouble spliterator(PrimitiveIterator.OfDouble iterator, long size, int characteristics) {
        iterator.getClass();
        return new DoubleIteratorSpliterator(iterator, size, characteristics);
    }

    public static Spliterator.OfDouble spliteratorUnknownSize(PrimitiveIterator.OfDouble iterator, int characteristics) {
        iterator.getClass();
        return new DoubleIteratorSpliterator(iterator, characteristics);
    }

    public static <T> Iterator<T> iterator(final Spliterator<? extends T> spliterator) {
        spliterator.getClass();
        return new Object() {
            T nextElement;
            boolean valueReady = false;

            public /* synthetic */ Consumer andThen(Consumer consumer) {
                return Consumer.CC.$default$andThen(this, consumer);
            }

            public void accept(T t) {
                this.valueReady = true;
                this.nextElement = t;
            }

            public boolean hasNext() {
                if (!this.valueReady) {
                    Spliterator.this.tryAdvance(this);
                }
                return this.valueReady;
            }

            public T next() {
                if (this.valueReady || hasNext()) {
                    this.valueReady = false;
                    return this.nextElement;
                }
                throw new NoSuchElementException();
            }
        };
    }

    public static PrimitiveIterator.OfInt iterator(final Spliterator.OfInt spliterator) {
        spliterator.getClass();
        return new Object() {
            int nextElement;
            boolean valueReady = false;

            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return IntConsumer.CC.$default$andThen(this, intConsumer);
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                PrimitiveIterator.OfInt.CC.$default$forEachRemaining((PrimitiveIterator.OfInt) this, consumer);
            }

            public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                PrimitiveIterator.OfInt.CC.$default$forEachRemaining((PrimitiveIterator.OfInt) this, intConsumer);
            }

            public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
                forEachRemaining((IntConsumer) obj);
            }

            public /* synthetic */ void remove() {
                Iterator.CC.$default$remove(this);
            }

            public void accept(int t) {
                this.valueReady = true;
                this.nextElement = t;
            }

            public boolean hasNext() {
                if (!this.valueReady) {
                    Spliterator.OfInt.this.tryAdvance((IntConsumer) this);
                }
                return this.valueReady;
            }

            public int nextInt() {
                if (this.valueReady || hasNext()) {
                    this.valueReady = false;
                    return this.nextElement;
                }
                throw new NoSuchElementException();
            }
        };
    }

    public static PrimitiveIterator.OfLong iterator(final Spliterator.OfLong spliterator) {
        spliterator.getClass();
        return new Object() {
            long nextElement;
            boolean valueReady = false;

            public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                return LongConsumer.CC.$default$andThen(this, longConsumer);
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                PrimitiveIterator.OfLong.CC.$default$forEachRemaining((PrimitiveIterator.OfLong) this, consumer);
            }

            public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                PrimitiveIterator.OfLong.CC.$default$forEachRemaining((PrimitiveIterator.OfLong) this, longConsumer);
            }

            public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
                forEachRemaining((LongConsumer) obj);
            }

            public /* synthetic */ void remove() {
                Iterator.CC.$default$remove(this);
            }

            public void accept(long t) {
                this.valueReady = true;
                this.nextElement = t;
            }

            public boolean hasNext() {
                if (!this.valueReady) {
                    Spliterator.OfLong.this.tryAdvance((LongConsumer) this);
                }
                return this.valueReady;
            }

            public long nextLong() {
                if (this.valueReady || hasNext()) {
                    this.valueReady = false;
                    return this.nextElement;
                }
                throw new NoSuchElementException();
            }
        };
    }

    public static PrimitiveIterator.OfDouble iterator(final Spliterator.OfDouble spliterator) {
        spliterator.getClass();
        return new Object() {
            double nextElement;
            boolean valueReady = false;

            public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                PrimitiveIterator.OfDouble.CC.$default$forEachRemaining((PrimitiveIterator.OfDouble) this, consumer);
            }

            public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                PrimitiveIterator.OfDouble.CC.$default$forEachRemaining((PrimitiveIterator.OfDouble) this, doubleConsumer);
            }

            public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
                forEachRemaining((DoubleConsumer) obj);
            }

            public /* synthetic */ void remove() {
                Iterator.CC.$default$remove(this);
            }

            public void accept(double t) {
                this.valueReady = true;
                this.nextElement = t;
            }

            public boolean hasNext() {
                if (!this.valueReady) {
                    Spliterator.OfDouble.this.tryAdvance((DoubleConsumer) this);
                }
                return this.valueReady;
            }

            public double nextDouble() {
                if (this.valueReady || hasNext()) {
                    this.valueReady = false;
                    return this.nextElement;
                }
                throw new NoSuchElementException();
            }
        };
    }

    private static abstract class EmptySpliterator<T, S extends Spliterator<T>, C> {
        EmptySpliterator() {
        }

        public S trySplit() {
            return null;
        }

        public boolean tryAdvance(C consumer) {
            consumer.getClass();
            return false;
        }

        public void forEachRemaining(C consumer) {
            consumer.getClass();
        }

        public long estimateSize() {
            return 0;
        }

        public int characteristics() {
            return 16448;
        }

        private static final class OfRef<T> extends EmptySpliterator<T, Spliterator<T>, Consumer<? super T>> implements Spliterator<T> {
            public /* synthetic */ Comparator getComparator() {
                return Spliterator.CC.$default$getComparator(this);
            }

            public /* synthetic */ long getExactSizeIfKnown() {
                return Spliterator.CC.$default$getExactSizeIfKnown(this);
            }

            public /* synthetic */ boolean hasCharacteristics(int i) {
                return Spliterator.CC.$default$hasCharacteristics(this, i);
            }

            public /* bridge */ /* synthetic */ void forEachRemaining(Consumer consumer) {
                super.forEachRemaining(consumer);
            }

            public /* bridge */ /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return super.tryAdvance(consumer);
            }

            OfRef() {
            }
        }

        private static final class OfInt extends EmptySpliterator<Integer, Spliterator.OfInt, IntConsumer> implements Spliterator.OfInt {
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
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

            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
            }

            public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                super.forEachRemaining(intConsumer);
            }

            public /* bridge */ /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
                return super.tryAdvance(intConsumer);
            }

            OfInt() {
            }
        }

        private static final class OfLong extends EmptySpliterator<Long, Spliterator.OfLong, LongConsumer> implements Spliterator.OfLong {
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
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

            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
            }

            public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                super.forEachRemaining(longConsumer);
            }

            public /* bridge */ /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
                return super.tryAdvance(longConsumer);
            }

            OfLong() {
            }
        }

        private static final class OfDouble extends EmptySpliterator<Double, Spliterator.OfDouble, DoubleConsumer> implements Spliterator.OfDouble {
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
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

            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
            }

            public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                super.forEachRemaining(doubleConsumer);
            }

            public /* bridge */ /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
                return super.tryAdvance(doubleConsumer);
            }

            OfDouble() {
            }
        }
    }

    static final class ArraySpliterator<T> implements Spliterator<T> {
        private final Object[] array;
        private final int characteristics;
        private final int fence;
        private int index;

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        public ArraySpliterator(Object[] array2, int additionalCharacteristics) {
            this(array2, 0, array2.length, additionalCharacteristics);
        }

        public ArraySpliterator(Object[] array2, int origin, int fence2, int additionalCharacteristics) {
            this.array = array2;
            this.index = origin;
            this.fence = fence2;
            this.characteristics = additionalCharacteristics | 64 | 16384;
        }

        public Spliterator<T> trySplit() {
            int lo = this.index;
            int mid = (this.fence + lo) >>> 1;
            if (lo >= mid) {
                return null;
            }
            Object[] objArr = this.array;
            this.index = mid;
            return new ArraySpliterator(objArr, lo, mid, this.characteristics);
        }

        public void forEachRemaining(Consumer<? super T> consumer) {
            if (consumer != null) {
                Object[] objArr = this.array;
                Object[] a = objArr;
                int length = objArr.length;
                int i = this.fence;
                int hi = i;
                if (length >= i) {
                    int i2 = this.index;
                    int i3 = i2;
                    if (i2 >= 0) {
                        this.index = hi;
                        if (i3 < hi) {
                            do {
                                consumer.accept(a[i3]);
                                i3++;
                            } while (i3 < hi);
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
            }
            throw new NullPointerException();
        }

        public boolean tryAdvance(Consumer<? super T> consumer) {
            if (consumer != null) {
                int i = this.index;
                if (i < 0 || i >= this.fence) {
                    return false;
                }
                T[] tArr = this.array;
                this.index = i + 1;
                consumer.accept(tArr[i]);
                return true;
            }
            throw new NullPointerException();
        }

        public long estimateSize() {
            return (long) (this.fence - this.index);
        }

        public int characteristics() {
            return this.characteristics;
        }

        public Comparator<? super T> getComparator() {
            if (hasCharacteristics(4)) {
                return null;
            }
            throw new IllegalStateException();
        }
    }

    static final class IntArraySpliterator implements Spliterator.OfInt {
        private final int[] array;
        private final int characteristics;
        private final int fence;
        private int index;

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
        }

        public IntArraySpliterator(int[] array2, int additionalCharacteristics) {
            this(array2, 0, array2.length, additionalCharacteristics);
        }

        public IntArraySpliterator(int[] array2, int origin, int fence2, int additionalCharacteristics) {
            this.array = array2;
            this.index = origin;
            this.fence = fence2;
            this.characteristics = additionalCharacteristics | 64 | 16384;
        }

        public Spliterator.OfInt trySplit() {
            int lo = this.index;
            int mid = (this.fence + lo) >>> 1;
            if (lo >= mid) {
                return null;
            }
            int[] iArr = this.array;
            this.index = mid;
            return new IntArraySpliterator(iArr, lo, mid, this.characteristics);
        }

        public void forEachRemaining(IntConsumer action) {
            if (action != null) {
                int[] iArr = this.array;
                int[] a = iArr;
                int length = iArr.length;
                int i = this.fence;
                int hi = i;
                if (length >= i) {
                    int i2 = this.index;
                    int i3 = i2;
                    if (i2 >= 0) {
                        this.index = hi;
                        if (i3 < hi) {
                            do {
                                action.accept(a[i3]);
                                i3++;
                            } while (i3 < hi);
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
            }
            throw new NullPointerException();
        }

        public boolean tryAdvance(IntConsumer action) {
            if (action != null) {
                int i = this.index;
                if (i < 0 || i >= this.fence) {
                    return false;
                }
                int[] iArr = this.array;
                this.index = i + 1;
                action.accept(iArr[i]);
                return true;
            }
            throw new NullPointerException();
        }

        public long estimateSize() {
            return (long) (this.fence - this.index);
        }

        public int characteristics() {
            return this.characteristics;
        }

        public Comparator<? super Integer> getComparator() {
            if (hasCharacteristics(4)) {
                return null;
            }
            throw new IllegalStateException();
        }
    }

    static final class LongArraySpliterator implements Spliterator.OfLong {
        private final long[] array;
        private final int characteristics;
        private final int fence;
        private int index;

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
        }

        public LongArraySpliterator(long[] array2, int additionalCharacteristics) {
            this(array2, 0, array2.length, additionalCharacteristics);
        }

        public LongArraySpliterator(long[] array2, int origin, int fence2, int additionalCharacteristics) {
            this.array = array2;
            this.index = origin;
            this.fence = fence2;
            this.characteristics = additionalCharacteristics | 64 | 16384;
        }

        public Spliterator.OfLong trySplit() {
            int lo = this.index;
            int mid = (this.fence + lo) >>> 1;
            if (lo >= mid) {
                return null;
            }
            long[] jArr = this.array;
            this.index = mid;
            return new LongArraySpliterator(jArr, lo, mid, this.characteristics);
        }

        public void forEachRemaining(LongConsumer action) {
            if (action != null) {
                long[] jArr = this.array;
                long[] a = jArr;
                int length = jArr.length;
                int i = this.fence;
                int hi = i;
                if (length >= i) {
                    int i2 = this.index;
                    int i3 = i2;
                    if (i2 >= 0) {
                        this.index = hi;
                        if (i3 < hi) {
                            do {
                                action.accept(a[i3]);
                                i3++;
                            } while (i3 < hi);
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
            }
            throw new NullPointerException();
        }

        public boolean tryAdvance(LongConsumer action) {
            if (action != null) {
                int i = this.index;
                if (i < 0 || i >= this.fence) {
                    return false;
                }
                long[] jArr = this.array;
                this.index = i + 1;
                action.accept(jArr[i]);
                return true;
            }
            throw new NullPointerException();
        }

        public long estimateSize() {
            return (long) (this.fence - this.index);
        }

        public int characteristics() {
            return this.characteristics;
        }

        public Comparator<? super Long> getComparator() {
            if (hasCharacteristics(4)) {
                return null;
            }
            throw new IllegalStateException();
        }
    }

    static final class DoubleArraySpliterator implements Spliterator.OfDouble {
        private final double[] array;
        private final int characteristics;
        private final int fence;
        private int index;

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
        }

        public DoubleArraySpliterator(double[] array2, int additionalCharacteristics) {
            this(array2, 0, array2.length, additionalCharacteristics);
        }

        public DoubleArraySpliterator(double[] array2, int origin, int fence2, int additionalCharacteristics) {
            this.array = array2;
            this.index = origin;
            this.fence = fence2;
            this.characteristics = additionalCharacteristics | 64 | 16384;
        }

        public Spliterator.OfDouble trySplit() {
            int lo = this.index;
            int mid = (this.fence + lo) >>> 1;
            if (lo >= mid) {
                return null;
            }
            double[] dArr = this.array;
            this.index = mid;
            return new DoubleArraySpliterator(dArr, lo, mid, this.characteristics);
        }

        public void forEachRemaining(DoubleConsumer action) {
            if (action != null) {
                double[] dArr = this.array;
                double[] a = dArr;
                int length = dArr.length;
                int i = this.fence;
                int hi = i;
                if (length >= i) {
                    int i2 = this.index;
                    int i3 = i2;
                    if (i2 >= 0) {
                        this.index = hi;
                        if (i3 < hi) {
                            do {
                                action.accept(a[i3]);
                                i3++;
                            } while (i3 < hi);
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
            }
            throw new NullPointerException();
        }

        public boolean tryAdvance(DoubleConsumer action) {
            if (action != null) {
                int i = this.index;
                if (i < 0 || i >= this.fence) {
                    return false;
                }
                double[] dArr = this.array;
                this.index = i + 1;
                action.accept(dArr[i]);
                return true;
            }
            throw new NullPointerException();
        }

        public long estimateSize() {
            return (long) (this.fence - this.index);
        }

        public int characteristics() {
            return this.characteristics;
        }

        public Comparator<? super Double> getComparator() {
            if (hasCharacteristics(4)) {
                return null;
            }
            throw new IllegalStateException();
        }
    }

    public static abstract class AbstractSpliterator<T> implements Spliterator<T> {
        static final int BATCH_UNIT = 1024;
        static final int MAX_BATCH = 33554432;
        private int batch;
        private final int characteristics;
        private long est;

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

        protected AbstractSpliterator(long est2, int additionalCharacteristics) {
            int i;
            this.est = est2;
            if ((additionalCharacteristics & 64) != 0) {
                i = additionalCharacteristics | 16384;
            } else {
                i = additionalCharacteristics;
            }
            this.characteristics = i;
        }

        static final class HoldingConsumer<T> implements Consumer<T> {
            Object value;

            public /* synthetic */ Consumer andThen(Consumer consumer) {
                return Consumer.CC.$default$andThen(this, consumer);
            }

            HoldingConsumer() {
            }

            public void accept(T value2) {
                this.value = value2;
            }
        }

        public Spliterator<T> trySplit() {
            Spliterators.AbstractSpliterator.HoldingConsumer<T> holder = new HoldingConsumer<>();
            long s = this.est;
            if (s <= 1 || !tryAdvance(holder)) {
                return null;
            }
            int n = this.batch + 1024;
            if (((long) n) > s) {
                n = (int) s;
            }
            if (n > 33554432) {
                n = 33554432;
            }
            Object[] a = new Object[n];
            int j = 0;
            do {
                a[j] = holder.value;
                j++;
                if (j >= n || !tryAdvance(holder)) {
                    this.batch = j;
                    long j2 = this.est;
                }
                a[j] = holder.value;
                j++;
                break;
            } while (!tryAdvance(holder));
            this.batch = j;
            long j22 = this.est;
            if (j22 != Long.MAX_VALUE) {
                this.est = j22 - ((long) j);
            }
            return new ArraySpliterator(a, 0, j, characteristics());
        }

        public long estimateSize() {
            return this.est;
        }

        public int characteristics() {
            return this.characteristics;
        }
    }

    public static abstract class AbstractIntSpliterator implements Spliterator.OfInt {
        static final int BATCH_UNIT = 1024;
        static final int MAX_BATCH = 33554432;
        private int batch;
        private final int characteristics;
        private long est;

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
        }

        public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
            Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, intConsumer);
        }

        public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
            forEachRemaining((IntConsumer) obj);
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

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
        }

        public /* bridge */ /* synthetic */ boolean tryAdvance(Object obj) {
            return tryAdvance((IntConsumer) obj);
        }

        protected AbstractIntSpliterator(long est2, int additionalCharacteristics) {
            int i;
            this.est = est2;
            if ((additionalCharacteristics & 64) != 0) {
                i = additionalCharacteristics | 16384;
            } else {
                i = additionalCharacteristics;
            }
            this.characteristics = i;
        }

        static final class HoldingIntConsumer implements IntConsumer {
            int value;

            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return IntConsumer.CC.$default$andThen(this, intConsumer);
            }

            HoldingIntConsumer() {
            }

            public void accept(int value2) {
                this.value = value2;
            }
        }

        public Spliterator.OfInt trySplit() {
            HoldingIntConsumer holder = new HoldingIntConsumer();
            long s = this.est;
            if (s <= 1 || !tryAdvance((IntConsumer) holder)) {
                return null;
            }
            int n = this.batch + 1024;
            if (((long) n) > s) {
                n = (int) s;
            }
            if (n > 33554432) {
                n = 33554432;
            }
            int[] a = new int[n];
            int j = 0;
            do {
                a[j] = holder.value;
                j++;
                if (j >= n || !tryAdvance((IntConsumer) holder)) {
                    this.batch = j;
                    long j2 = this.est;
                }
                a[j] = holder.value;
                j++;
                break;
            } while (!tryAdvance((IntConsumer) holder));
            this.batch = j;
            long j22 = this.est;
            if (j22 != Long.MAX_VALUE) {
                this.est = j22 - ((long) j);
            }
            return new IntArraySpliterator(a, 0, j, characteristics());
        }

        public long estimateSize() {
            return this.est;
        }

        public int characteristics() {
            return this.characteristics;
        }
    }

    public static abstract class AbstractLongSpliterator implements Spliterator.OfLong {
        static final int BATCH_UNIT = 1024;
        static final int MAX_BATCH = 33554432;
        private int batch;
        private final int characteristics;
        private long est;

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
        }

        public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
            Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, longConsumer);
        }

        public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
            forEachRemaining((LongConsumer) obj);
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

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
        }

        public /* bridge */ /* synthetic */ boolean tryAdvance(Object obj) {
            return tryAdvance((LongConsumer) obj);
        }

        protected AbstractLongSpliterator(long est2, int additionalCharacteristics) {
            int i;
            this.est = est2;
            if ((additionalCharacteristics & 64) != 0) {
                i = additionalCharacteristics | 16384;
            } else {
                i = additionalCharacteristics;
            }
            this.characteristics = i;
        }

        static final class HoldingLongConsumer implements LongConsumer {
            long value;

            public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                return LongConsumer.CC.$default$andThen(this, longConsumer);
            }

            HoldingLongConsumer() {
            }

            public void accept(long value2) {
                this.value = value2;
            }
        }

        public Spliterator.OfLong trySplit() {
            HoldingLongConsumer holder = new HoldingLongConsumer();
            long s = this.est;
            if (s <= 1 || !tryAdvance((LongConsumer) holder)) {
                return null;
            }
            int n = this.batch + 1024;
            if (((long) n) > s) {
                n = (int) s;
            }
            if (n > 33554432) {
                n = 33554432;
            }
            long[] a = new long[n];
            int j = 0;
            do {
                a[j] = holder.value;
                j++;
                if (j >= n || !tryAdvance((LongConsumer) holder)) {
                    this.batch = j;
                    long j2 = this.est;
                }
                a[j] = holder.value;
                j++;
                break;
            } while (!tryAdvance((LongConsumer) holder));
            this.batch = j;
            long j22 = this.est;
            if (j22 != Long.MAX_VALUE) {
                this.est = j22 - ((long) j);
            }
            return new LongArraySpliterator(a, 0, j, characteristics());
        }

        public long estimateSize() {
            return this.est;
        }

        public int characteristics() {
            return this.characteristics;
        }
    }

    public static abstract class AbstractDoubleSpliterator implements Spliterator.OfDouble {
        static final int BATCH_UNIT = 1024;
        static final int MAX_BATCH = 33554432;
        private int batch;
        private final int characteristics;
        private long est;

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
        }

        public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
            Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, doubleConsumer);
        }

        public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
            forEachRemaining((DoubleConsumer) obj);
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

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
        }

        public /* bridge */ /* synthetic */ boolean tryAdvance(Object obj) {
            return tryAdvance((DoubleConsumer) obj);
        }

        protected AbstractDoubleSpliterator(long est2, int additionalCharacteristics) {
            int i;
            this.est = est2;
            if ((additionalCharacteristics & 64) != 0) {
                i = additionalCharacteristics | 16384;
            } else {
                i = additionalCharacteristics;
            }
            this.characteristics = i;
        }

        static final class HoldingDoubleConsumer implements DoubleConsumer {
            double value;

            public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
            }

            HoldingDoubleConsumer() {
            }

            public void accept(double value2) {
                this.value = value2;
            }
        }

        public Spliterator.OfDouble trySplit() {
            HoldingDoubleConsumer holder = new HoldingDoubleConsumer();
            long s = this.est;
            if (s <= 1 || !tryAdvance((DoubleConsumer) holder)) {
                return null;
            }
            int n = this.batch + 1024;
            if (((long) n) > s) {
                n = (int) s;
            }
            if (n > 33554432) {
                n = 33554432;
            }
            double[] a = new double[n];
            int j = 0;
            do {
                a[j] = holder.value;
                j++;
                if (j >= n || !tryAdvance((DoubleConsumer) holder)) {
                    this.batch = j;
                    long j2 = this.est;
                }
                a[j] = holder.value;
                j++;
                break;
            } while (!tryAdvance((DoubleConsumer) holder));
            this.batch = j;
            long j22 = this.est;
            if (j22 != Long.MAX_VALUE) {
                this.est = j22 - ((long) j);
            }
            return new DoubleArraySpliterator(a, 0, j, characteristics());
        }

        public long estimateSize() {
            return this.est;
        }

        public int characteristics() {
            return this.characteristics;
        }
    }

    static class IteratorSpliterator<T> implements Spliterator<T> {
        static final int BATCH_UNIT = 1024;
        static final int MAX_BATCH = 33554432;
        private int batch;
        private final int characteristics;
        private final Collection<? extends T> collection;
        private long est;
        private java.util.Iterator<? extends T> it;

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        public IteratorSpliterator(Collection<? extends T> collection2, int characteristics2) {
            int i;
            this.collection = collection2;
            this.it = null;
            if ((characteristics2 & 4096) == 0) {
                i = characteristics2 | 64 | 16384;
            } else {
                i = characteristics2;
            }
            this.characteristics = i;
        }

        public IteratorSpliterator(java.util.Iterator<? extends T> iterator, long size, int characteristics2) {
            int i;
            this.collection = null;
            this.it = iterator;
            this.est = size;
            if ((characteristics2 & 4096) == 0) {
                i = characteristics2 | 64 | 16384;
            } else {
                i = characteristics2;
            }
            this.characteristics = i;
        }

        public IteratorSpliterator(java.util.Iterator<? extends T> iterator, int characteristics2) {
            this.collection = null;
            this.it = iterator;
            this.est = Long.MAX_VALUE;
            this.characteristics = characteristics2 & -16449;
        }

        public Spliterator<T> trySplit() {
            long s;
            java.util.Iterator<? extends T> it2 = this.it;
            java.util.Iterator<? extends T> i = it2;
            if (it2 == null) {
                java.util.Iterator<? extends T> it3 = this.collection.iterator();
                this.it = it3;
                i = it3;
                s = (long) this.collection.size();
                this.est = s;
            } else {
                s = this.est;
            }
            if (s <= 1 || !i.hasNext()) {
                return null;
            }
            int n = this.batch + 1024;
            if (((long) n) > s) {
                n = (int) s;
            }
            if (n > 33554432) {
                n = 33554432;
            }
            Object[] a = new Object[n];
            int j = 0;
            do {
                a[j] = i.next();
                j++;
                if (j >= n || !i.hasNext()) {
                    this.batch = j;
                    long j2 = this.est;
                }
                a[j] = i.next();
                j++;
                break;
            } while (!i.hasNext());
            this.batch = j;
            long j22 = this.est;
            if (j22 != Long.MAX_VALUE) {
                this.est = j22 - ((long) j);
            }
            return new ArraySpliterator(a, 0, j, this.characteristics);
        }

        public void forEachRemaining(Consumer<? super T> consumer) {
            if (consumer != null) {
                java.util.Iterator<? extends T> it2 = this.it;
                java.util.Iterator<? extends T> i = it2;
                if (it2 == null) {
                    java.util.Iterator<? extends T> it3 = this.collection.iterator();
                    this.it = it3;
                    i = it3;
                    this.est = (long) this.collection.size();
                }
                Iterator.EL.forEachRemaining(i, consumer);
                return;
            }
            throw new NullPointerException();
        }

        public boolean tryAdvance(Consumer<? super T> consumer) {
            if (consumer != null) {
                if (this.it == null) {
                    this.it = this.collection.iterator();
                    this.est = (long) this.collection.size();
                }
                if (!this.it.hasNext()) {
                    return false;
                }
                consumer.accept(this.it.next());
                return true;
            }
            throw new NullPointerException();
        }

        public long estimateSize() {
            if (this.it != null) {
                return this.est;
            }
            this.it = this.collection.iterator();
            long size = (long) this.collection.size();
            this.est = size;
            return size;
        }

        public int characteristics() {
            return this.characteristics;
        }

        public Comparator<? super T> getComparator() {
            if (hasCharacteristics(4)) {
                return null;
            }
            throw new IllegalStateException();
        }
    }

    static final class IntIteratorSpliterator implements Spliterator.OfInt {
        static final int BATCH_UNIT = 1024;
        static final int MAX_BATCH = 33554432;
        private int batch;
        private final int characteristics;
        private long est;
        private PrimitiveIterator.OfInt it;

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
        }

        public IntIteratorSpliterator(PrimitiveIterator.OfInt iterator, long size, int characteristics2) {
            int i;
            this.it = iterator;
            this.est = size;
            if ((characteristics2 & 4096) == 0) {
                i = characteristics2 | 64 | 16384;
            } else {
                i = characteristics2;
            }
            this.characteristics = i;
        }

        public IntIteratorSpliterator(PrimitiveIterator.OfInt iterator, int characteristics2) {
            this.it = iterator;
            this.est = Long.MAX_VALUE;
            this.characteristics = characteristics2 & -16449;
        }

        public Spliterator.OfInt trySplit() {
            PrimitiveIterator.OfInt i = this.it;
            long s = this.est;
            if (s <= 1 || !i.hasNext()) {
                return null;
            }
            int n = this.batch + 1024;
            if (((long) n) > s) {
                n = (int) s;
            }
            if (n > 33554432) {
                n = 33554432;
            }
            int[] a = new int[n];
            int j = 0;
            do {
                a[j] = i.nextInt();
                j++;
                if (j >= n || !i.hasNext()) {
                    this.batch = j;
                    long j2 = this.est;
                }
                a[j] = i.nextInt();
                j++;
                break;
            } while (!i.hasNext());
            this.batch = j;
            long j22 = this.est;
            if (j22 != Long.MAX_VALUE) {
                this.est = j22 - ((long) j);
            }
            return new IntArraySpliterator(a, 0, j, this.characteristics);
        }

        public void forEachRemaining(IntConsumer action) {
            if (action != null) {
                this.it.forEachRemaining(action);
                return;
            }
            throw new NullPointerException();
        }

        public boolean tryAdvance(IntConsumer action) {
            if (action == null) {
                throw new NullPointerException();
            } else if (!this.it.hasNext()) {
                return false;
            } else {
                action.accept(this.it.nextInt());
                return true;
            }
        }

        public long estimateSize() {
            return this.est;
        }

        public int characteristics() {
            return this.characteristics;
        }

        public Comparator<? super Integer> getComparator() {
            if (hasCharacteristics(4)) {
                return null;
            }
            throw new IllegalStateException();
        }
    }

    static final class LongIteratorSpliterator implements Spliterator.OfLong {
        static final int BATCH_UNIT = 1024;
        static final int MAX_BATCH = 33554432;
        private int batch;
        private final int characteristics;
        private long est;
        private PrimitiveIterator.OfLong it;

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
        }

        public LongIteratorSpliterator(PrimitiveIterator.OfLong iterator, long size, int characteristics2) {
            int i;
            this.it = iterator;
            this.est = size;
            if ((characteristics2 & 4096) == 0) {
                i = characteristics2 | 64 | 16384;
            } else {
                i = characteristics2;
            }
            this.characteristics = i;
        }

        public LongIteratorSpliterator(PrimitiveIterator.OfLong iterator, int characteristics2) {
            this.it = iterator;
            this.est = Long.MAX_VALUE;
            this.characteristics = characteristics2 & -16449;
        }

        public Spliterator.OfLong trySplit() {
            PrimitiveIterator.OfLong i = this.it;
            long s = this.est;
            if (s <= 1 || !i.hasNext()) {
                return null;
            }
            int n = this.batch + 1024;
            if (((long) n) > s) {
                n = (int) s;
            }
            if (n > 33554432) {
                n = 33554432;
            }
            long[] a = new long[n];
            int j = 0;
            do {
                a[j] = i.nextLong();
                j++;
                if (j >= n || !i.hasNext()) {
                    this.batch = j;
                    long j2 = this.est;
                }
                a[j] = i.nextLong();
                j++;
                break;
            } while (!i.hasNext());
            this.batch = j;
            long j22 = this.est;
            if (j22 != Long.MAX_VALUE) {
                this.est = j22 - ((long) j);
            }
            return new LongArraySpliterator(a, 0, j, this.characteristics);
        }

        public void forEachRemaining(LongConsumer action) {
            if (action != null) {
                this.it.forEachRemaining(action);
                return;
            }
            throw new NullPointerException();
        }

        public boolean tryAdvance(LongConsumer action) {
            if (action == null) {
                throw new NullPointerException();
            } else if (!this.it.hasNext()) {
                return false;
            } else {
                action.accept(this.it.nextLong());
                return true;
            }
        }

        public long estimateSize() {
            return this.est;
        }

        public int characteristics() {
            return this.characteristics;
        }

        public Comparator<? super Long> getComparator() {
            if (hasCharacteristics(4)) {
                return null;
            }
            throw new IllegalStateException();
        }
    }

    static final class DoubleIteratorSpliterator implements Spliterator.OfDouble {
        static final int BATCH_UNIT = 1024;
        static final int MAX_BATCH = 33554432;
        private int batch;
        private final int characteristics;
        private long est;
        private PrimitiveIterator.OfDouble it;

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
        }

        public DoubleIteratorSpliterator(PrimitiveIterator.OfDouble iterator, long size, int characteristics2) {
            int i;
            this.it = iterator;
            this.est = size;
            if ((characteristics2 & 4096) == 0) {
                i = characteristics2 | 64 | 16384;
            } else {
                i = characteristics2;
            }
            this.characteristics = i;
        }

        public DoubleIteratorSpliterator(PrimitiveIterator.OfDouble iterator, int characteristics2) {
            this.it = iterator;
            this.est = Long.MAX_VALUE;
            this.characteristics = characteristics2 & -16449;
        }

        public Spliterator.OfDouble trySplit() {
            PrimitiveIterator.OfDouble i = this.it;
            long s = this.est;
            if (s <= 1 || !i.hasNext()) {
                return null;
            }
            int n = this.batch + 1024;
            if (((long) n) > s) {
                n = (int) s;
            }
            if (n > 33554432) {
                n = 33554432;
            }
            double[] a = new double[n];
            int j = 0;
            do {
                a[j] = i.nextDouble();
                j++;
                if (j >= n || !i.hasNext()) {
                    this.batch = j;
                    long j2 = this.est;
                }
                a[j] = i.nextDouble();
                j++;
                break;
            } while (!i.hasNext());
            this.batch = j;
            long j22 = this.est;
            if (j22 != Long.MAX_VALUE) {
                this.est = j22 - ((long) j);
            }
            return new DoubleArraySpliterator(a, 0, j, this.characteristics);
        }

        public void forEachRemaining(DoubleConsumer action) {
            if (action != null) {
                this.it.forEachRemaining(action);
                return;
            }
            throw new NullPointerException();
        }

        public boolean tryAdvance(DoubleConsumer action) {
            if (action == null) {
                throw new NullPointerException();
            } else if (!this.it.hasNext()) {
                return false;
            } else {
                action.accept(this.it.nextDouble());
                return true;
            }
        }

        public long estimateSize() {
            return this.est;
        }

        public int characteristics() {
            return this.characteristics;
        }

        public Comparator<? super Double> getComparator() {
            if (hasCharacteristics(4)) {
                return null;
            }
            throw new IllegalStateException();
        }
    }
}

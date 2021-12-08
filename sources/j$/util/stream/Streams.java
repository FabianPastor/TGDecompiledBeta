package j$.util.stream;

import j$.lang.Iterable;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.LongConsumer;
import j$.util.stream.DoubleStream;
import j$.util.stream.IntStream;
import j$.util.stream.LongStream;
import j$.util.stream.SpinedBuffer;
import j$.util.stream.Stream;
import java.util.Comparator;

final class Streams {
    static final Object NONE = new Object();

    private Streams() {
        throw new Error("no instances");
    }

    static final class RangeIntSpliterator implements Spliterator.OfInt {
        private static final int BALANCED_SPLIT_THRESHOLD = 16777216;
        private static final int RIGHT_BALANCED_SPLIT_RATIO = 8;
        private int from;
        private int last;
        private final int upTo;

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

        RangeIntSpliterator(int from2, int upTo2, boolean closed) {
            this(from2, upTo2, (int) closed);
        }

        private RangeIntSpliterator(int from2, int upTo2, int last2) {
            this.from = from2;
            this.upTo = upTo2;
            this.last = last2;
        }

        public boolean tryAdvance(IntConsumer consumer) {
            consumer.getClass();
            int i = this.from;
            if (i < this.upTo) {
                this.from++;
                consumer.accept(i);
                return true;
            } else if (this.last <= 0) {
                return false;
            } else {
                this.last = 0;
                consumer.accept(i);
                return true;
            }
        }

        public void forEachRemaining(IntConsumer consumer) {
            consumer.getClass();
            int i = this.from;
            int hUpTo = this.upTo;
            int hLast = this.last;
            this.from = this.upTo;
            this.last = 0;
            while (i < hUpTo) {
                consumer.accept(i);
                i++;
            }
            if (hLast > 0) {
                consumer.accept(i);
            }
        }

        public long estimateSize() {
            return (((long) this.upTo) - ((long) this.from)) + ((long) this.last);
        }

        public int characteristics() {
            return 17749;
        }

        public Comparator<? super Integer> getComparator() {
            return null;
        }

        public Spliterator.OfInt trySplit() {
            long size = estimateSize();
            if (size <= 1) {
                return null;
            }
            int i = this.from;
            int splitPoint = splitPoint(size) + i;
            this.from = splitPoint;
            return new RangeIntSpliterator(i, splitPoint, 0);
        }

        private int splitPoint(long size) {
            return (int) (size / ((long) (size < 16777216 ? 2 : 8)));
        }
    }

    static final class RangeLongSpliterator implements Spliterator.OfLong {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        private static final long BALANCED_SPLIT_THRESHOLD = 16777216;
        private static final long RIGHT_BALANCED_SPLIT_RATIO = 8;
        private long from;
        private int last;
        private final long upTo;

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

        RangeLongSpliterator(long from2, long upTo2, boolean closed) {
            this(from2, upTo2, (int) closed);
        }

        private RangeLongSpliterator(long from2, long upTo2, int last2) {
            if ($assertionsDisabled || (upTo2 - from2) + ((long) last2) > 0) {
                this.from = from2;
                this.upTo = upTo2;
                this.last = last2;
                return;
            }
            throw new AssertionError();
        }

        public boolean tryAdvance(LongConsumer consumer) {
            consumer.getClass();
            long i = this.from;
            if (i < this.upTo) {
                this.from++;
                consumer.accept(i);
                return true;
            } else if (this.last <= 0) {
                return false;
            } else {
                this.last = 0;
                consumer.accept(i);
                return true;
            }
        }

        public void forEachRemaining(LongConsumer consumer) {
            consumer.getClass();
            long i = this.from;
            long hUpTo = this.upTo;
            int hLast = this.last;
            this.from = this.upTo;
            this.last = 0;
            while (i < hUpTo) {
                consumer.accept(i);
                i = 1 + i;
            }
            if (hLast > 0) {
                consumer.accept(i);
            }
        }

        public long estimateSize() {
            return (this.upTo - this.from) + ((long) this.last);
        }

        public int characteristics() {
            return 17749;
        }

        public Comparator<? super Long> getComparator() {
            return null;
        }

        public Spliterator.OfLong trySplit() {
            long size = estimateSize();
            if (size <= 1) {
                return null;
            }
            long j = this.from;
            long splitPoint = splitPoint(size) + j;
            this.from = splitPoint;
            return new RangeLongSpliterator(j, splitPoint, 0);
        }

        private long splitPoint(long size) {
            return size / (size < 16777216 ? 2 : 8);
        }
    }

    private static abstract class AbstractStreamBuilderImpl<T, S extends Spliterator<T>> implements Spliterator<T> {
        int count;

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

        private AbstractStreamBuilderImpl() {
        }

        public S trySplit() {
            return null;
        }

        public long estimateSize() {
            return (long) ((-this.count) - 1);
        }

        public int characteristics() {
            return 17488;
        }
    }

    static final class StreamBuilderImpl<T> extends AbstractStreamBuilderImpl<T, Spliterator<T>> implements Stream.Builder<T> {
        SpinedBuffer<T> buffer;
        T first;

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        StreamBuilderImpl() {
            super();
        }

        StreamBuilderImpl(T t) {
            super();
            this.first = t;
            this.count = -2;
        }

        public void accept(T t) {
            if (this.count == 0) {
                this.first = t;
                this.count++;
            } else if (this.count > 0) {
                if (this.buffer == null) {
                    SpinedBuffer<T> spinedBuffer = new SpinedBuffer<>();
                    this.buffer = spinedBuffer;
                    spinedBuffer.accept(this.first);
                    this.count++;
                }
                this.buffer.accept(t);
            } else {
                throw new IllegalStateException();
            }
        }

        public Stream.Builder<T> add(T t) {
            accept(t);
            return this;
        }

        public Stream<T> build() {
            int c = this.count;
            if (c >= 0) {
                this.count = (-this.count) - 1;
                return c < 2 ? StreamSupport.stream(this, false) : StreamSupport.stream(Iterable.EL.spliterator(this.buffer), false);
            }
            throw new IllegalStateException();
        }

        public boolean tryAdvance(Consumer<? super T> consumer) {
            consumer.getClass();
            if (this.count != -2) {
                return false;
            }
            consumer.accept(this.first);
            this.count = -1;
            return true;
        }

        public void forEachRemaining(Consumer<? super T> consumer) {
            consumer.getClass();
            if (this.count == -2) {
                consumer.accept(this.first);
                this.count = -1;
            }
        }
    }

    static final class IntStreamBuilderImpl extends AbstractStreamBuilderImpl<Integer, Spliterator.OfInt> implements IntStream.Builder, Spliterator.OfInt {
        SpinedBuffer.OfInt buffer;
        int first;

        public /* synthetic */ IntStream.Builder add(int i) {
            return IntStream.Builder.CC.$default$add(this, i);
        }

        public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
            return IntConsumer.CC.$default$andThen(this, intConsumer);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
        }

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
        }

        IntStreamBuilderImpl() {
            super();
        }

        IntStreamBuilderImpl(int t) {
            super();
            this.first = t;
            this.count = -2;
        }

        public void accept(int t) {
            if (this.count == 0) {
                this.first = t;
                this.count++;
            } else if (this.count > 0) {
                if (this.buffer == null) {
                    SpinedBuffer.OfInt ofInt = new SpinedBuffer.OfInt();
                    this.buffer = ofInt;
                    ofInt.accept(this.first);
                    this.count++;
                }
                this.buffer.accept(t);
            } else {
                throw new IllegalStateException();
            }
        }

        public IntStream build() {
            int c = this.count;
            if (c >= 0) {
                this.count = (-this.count) - 1;
                return c < 2 ? StreamSupport.intStream(this, false) : StreamSupport.intStream(this.buffer.spliterator(), false);
            }
            throw new IllegalStateException();
        }

        public boolean tryAdvance(IntConsumer action) {
            action.getClass();
            if (this.count != -2) {
                return false;
            }
            action.accept(this.first);
            this.count = -1;
            return true;
        }

        public void forEachRemaining(IntConsumer action) {
            action.getClass();
            if (this.count == -2) {
                action.accept(this.first);
                this.count = -1;
            }
        }
    }

    static final class LongStreamBuilderImpl extends AbstractStreamBuilderImpl<Long, Spliterator.OfLong> implements LongStream.Builder, Spliterator.OfLong {
        SpinedBuffer.OfLong buffer;
        long first;

        public /* synthetic */ LongStream.Builder add(long j) {
            return LongStream.Builder.CC.$default$add(this, j);
        }

        public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
            return LongConsumer.CC.$default$andThen(this, longConsumer);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
        }

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
        }

        LongStreamBuilderImpl() {
            super();
        }

        LongStreamBuilderImpl(long t) {
            super();
            this.first = t;
            this.count = -2;
        }

        public void accept(long t) {
            if (this.count == 0) {
                this.first = t;
                this.count++;
            } else if (this.count > 0) {
                if (this.buffer == null) {
                    SpinedBuffer.OfLong ofLong = new SpinedBuffer.OfLong();
                    this.buffer = ofLong;
                    ofLong.accept(this.first);
                    this.count++;
                }
                this.buffer.accept(t);
            } else {
                throw new IllegalStateException();
            }
        }

        public LongStream build() {
            int c = this.count;
            if (c >= 0) {
                this.count = (-this.count) - 1;
                return c < 2 ? StreamSupport.longStream(this, false) : StreamSupport.longStream(this.buffer.spliterator(), false);
            }
            throw new IllegalStateException();
        }

        public boolean tryAdvance(LongConsumer action) {
            action.getClass();
            if (this.count != -2) {
                return false;
            }
            action.accept(this.first);
            this.count = -1;
            return true;
        }

        public void forEachRemaining(LongConsumer action) {
            action.getClass();
            if (this.count == -2) {
                action.accept(this.first);
                this.count = -1;
            }
        }
    }

    static final class DoubleStreamBuilderImpl extends AbstractStreamBuilderImpl<Double, Spliterator.OfDouble> implements DoubleStream.Builder, Spliterator.OfDouble {
        SpinedBuffer.OfDouble buffer;
        double first;

        public /* synthetic */ DoubleStream.Builder add(double d) {
            return DoubleStream.Builder.CC.$default$add(this, d);
        }

        public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
            return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
        }

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
        }

        DoubleStreamBuilderImpl() {
            super();
        }

        DoubleStreamBuilderImpl(double t) {
            super();
            this.first = t;
            this.count = -2;
        }

        public void accept(double t) {
            if (this.count == 0) {
                this.first = t;
                this.count++;
            } else if (this.count > 0) {
                if (this.buffer == null) {
                    SpinedBuffer.OfDouble ofDouble = new SpinedBuffer.OfDouble();
                    this.buffer = ofDouble;
                    ofDouble.accept(this.first);
                    this.count++;
                }
                this.buffer.accept(t);
            } else {
                throw new IllegalStateException();
            }
        }

        public DoubleStream build() {
            int c = this.count;
            if (c >= 0) {
                this.count = (-this.count) - 1;
                return c < 2 ? StreamSupport.doubleStream(this, false) : StreamSupport.doubleStream(this.buffer.spliterator(), false);
            }
            throw new IllegalStateException();
        }

        public boolean tryAdvance(DoubleConsumer action) {
            action.getClass();
            if (this.count != -2) {
                return false;
            }
            action.accept(this.first);
            this.count = -1;
            return true;
        }

        public void forEachRemaining(DoubleConsumer action) {
            action.getClass();
            if (this.count == -2) {
                action.accept(this.first);
                this.count = -1;
            }
        }
    }

    static abstract class ConcatSpliterator<T, T_SPLITR extends Spliterator<T>> implements Spliterator<T> {
        protected final T_SPLITR aSpliterator;
        protected final T_SPLITR bSpliterator;
        boolean beforeSplit = true;
        final boolean unsized;

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        public ConcatSpliterator(T_SPLITR aSpliterator2, T_SPLITR bSpliterator2) {
            this.aSpliterator = aSpliterator2;
            this.bSpliterator = bSpliterator2;
            boolean z = true;
            this.unsized = aSpliterator2.estimateSize() + bSpliterator2.estimateSize() >= 0 ? false : z;
        }

        public T_SPLITR trySplit() {
            T_SPLITR ret = this.beforeSplit ? this.aSpliterator : this.bSpliterator.trySplit();
            this.beforeSplit = false;
            return ret;
        }

        public boolean tryAdvance(Consumer<? super T> consumer) {
            if (!this.beforeSplit) {
                return this.bSpliterator.tryAdvance(consumer);
            }
            boolean hasNext = this.aSpliterator.tryAdvance(consumer);
            if (hasNext) {
                return hasNext;
            }
            this.beforeSplit = false;
            return this.bSpliterator.tryAdvance(consumer);
        }

        public void forEachRemaining(Consumer<? super T> consumer) {
            if (this.beforeSplit) {
                this.aSpliterator.forEachRemaining(consumer);
            }
            this.bSpliterator.forEachRemaining(consumer);
        }

        public long estimateSize() {
            if (!this.beforeSplit) {
                return this.bSpliterator.estimateSize();
            }
            long size = this.aSpliterator.estimateSize() + this.bSpliterator.estimateSize();
            if (size >= 0) {
                return size;
            }
            return Long.MAX_VALUE;
        }

        public int characteristics() {
            if (!this.beforeSplit) {
                return this.bSpliterator.characteristics();
            }
            return this.aSpliterator.characteristics() & this.bSpliterator.characteristics() & (((this.unsized ? 16448 : 0) | 5) ^ -1);
        }

        public Comparator<? super T> getComparator() {
            if (!this.beforeSplit) {
                return this.bSpliterator.getComparator();
            }
            throw new IllegalStateException();
        }

        static class OfRef<T> extends ConcatSpliterator<T, Spliterator<T>> {
            OfRef(Spliterator<T> spliterator, Spliterator<T> spliterator2) {
                super(spliterator, spliterator2);
            }
        }

        private static abstract class OfPrimitive<T, T_CONS, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends ConcatSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
            public /* bridge */ /* synthetic */ Spliterator.OfPrimitive trySplit() {
                return (Spliterator.OfPrimitive) super.trySplit();
            }

            private OfPrimitive(T_SPLITR aSpliterator, T_SPLITR bSpliterator) {
                super(aSpliterator, bSpliterator);
            }

            public boolean tryAdvance(T_CONS action) {
                if (!this.beforeSplit) {
                    return ((Spliterator.OfPrimitive) this.bSpliterator).tryAdvance(action);
                }
                boolean hasNext = ((Spliterator.OfPrimitive) this.aSpliterator).tryAdvance(action);
                if (hasNext) {
                    return hasNext;
                }
                this.beforeSplit = false;
                return ((Spliterator.OfPrimitive) this.bSpliterator).tryAdvance(action);
            }

            public void forEachRemaining(T_CONS action) {
                if (this.beforeSplit) {
                    ((Spliterator.OfPrimitive) this.aSpliterator).forEachRemaining(action);
                }
                ((Spliterator.OfPrimitive) this.bSpliterator).forEachRemaining(action);
            }
        }

        static class OfInt extends OfPrimitive<Integer, IntConsumer, Spliterator.OfInt> implements Spliterator.OfInt {
            public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                super.forEachRemaining(intConsumer);
            }

            public /* bridge */ /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
                return super.tryAdvance(intConsumer);
            }

            public /* bridge */ /* synthetic */ Spliterator.OfInt trySplit() {
                return (Spliterator.OfInt) super.trySplit();
            }

            OfInt(Spliterator.OfInt aSpliterator, Spliterator.OfInt bSpliterator) {
                super(aSpliterator, bSpliterator);
            }
        }

        static class OfLong extends OfPrimitive<Long, LongConsumer, Spliterator.OfLong> implements Spliterator.OfLong {
            public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                super.forEachRemaining(longConsumer);
            }

            public /* bridge */ /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
                return super.tryAdvance(longConsumer);
            }

            public /* bridge */ /* synthetic */ Spliterator.OfLong trySplit() {
                return (Spliterator.OfLong) super.trySplit();
            }

            OfLong(Spliterator.OfLong aSpliterator, Spliterator.OfLong bSpliterator) {
                super(aSpliterator, bSpliterator);
            }
        }

        static class OfDouble extends OfPrimitive<Double, DoubleConsumer, Spliterator.OfDouble> implements Spliterator.OfDouble {
            public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                super.forEachRemaining(doubleConsumer);
            }

            public /* bridge */ /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
                return super.tryAdvance(doubleConsumer);
            }

            public /* bridge */ /* synthetic */ Spliterator.OfDouble trySplit() {
                return (Spliterator.OfDouble) super.trySplit();
            }

            OfDouble(Spliterator.OfDouble aSpliterator, Spliterator.OfDouble bSpliterator) {
                super(aSpliterator, bSpliterator);
            }
        }
    }

    static Runnable composeWithExceptions(final Runnable a, final Runnable b) {
        return new Runnable() {
            public void run() {
                try {
                    a.run();
                    b.run();
                    return;
                } catch (Throwable th) {
                }
                throw e1;
            }
        };
    }

    static Runnable composedClose(final BaseStream<?, ?> baseStream, final BaseStream<?, ?> baseStream2) {
        return new Runnable() {
            public void run() {
                try {
                    BaseStream.this.close();
                    baseStream2.close();
                    return;
                } catch (Throwable th) {
                }
                throw e1;
            }
        };
    }
}

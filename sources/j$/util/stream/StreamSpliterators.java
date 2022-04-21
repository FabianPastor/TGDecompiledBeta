package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.BooleanSupplier;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.DoubleSupplier;
import j$.util.function.IntConsumer;
import j$.util.function.IntSupplier;
import j$.util.function.LongConsumer;
import j$.util.function.LongSupplier;
import j$.util.function.Supplier;
import j$.util.stream.SpinedBuffer;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.SpinedBuffer;
import java.util.stream.StreamSpliterators;

class StreamSpliterators {
    StreamSpliterators() {
    }

    private static abstract class AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends AbstractSpinedBuffer> implements Spliterator<P_OUT> {
        T_BUFFER buffer;
        Sink<P_IN> bufferSink;
        boolean finished;
        final boolean isParallel;
        long nextToConsume;
        final PipelineHelper<P_OUT> ph;
        BooleanSupplier pusher;
        Spliterator<P_IN> spliterator;
        private Supplier<Spliterator<P_IN>> spliteratorSupplier;

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.CC.$default$forEachRemaining(this, consumer);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        /* access modifiers changed from: package-private */
        public abstract void initPartialTraversalState();

        /* access modifiers changed from: package-private */
        public abstract AbstractWrappingSpliterator<P_IN, P_OUT, ?> wrap(Spliterator<P_IN> spliterator2);

        AbstractWrappingSpliterator(PipelineHelper<P_OUT> ph2, Supplier<Spliterator<P_IN>> supplier, boolean parallel) {
            this.ph = ph2;
            this.spliteratorSupplier = supplier;
            this.spliterator = null;
            this.isParallel = parallel;
        }

        AbstractWrappingSpliterator(PipelineHelper<P_OUT> ph2, Spliterator<P_IN> spliterator2, boolean parallel) {
            this.ph = ph2;
            this.spliteratorSupplier = null;
            this.spliterator = spliterator2;
            this.isParallel = parallel;
        }

        /* access modifiers changed from: package-private */
        public final void init() {
            if (this.spliterator == null) {
                this.spliterator = this.spliteratorSupplier.get();
                this.spliteratorSupplier = null;
            }
        }

        /* access modifiers changed from: package-private */
        public final boolean doAdvance() {
            T_BUFFER t_buffer = this.buffer;
            boolean z = false;
            if (t_buffer != null) {
                long j = this.nextToConsume + 1;
                this.nextToConsume = j;
                if (j < t_buffer.count()) {
                    z = true;
                }
                boolean hasNext = z;
                if (hasNext) {
                    return hasNext;
                }
                this.nextToConsume = 0;
                this.buffer.clear();
                return fillBuffer();
            } else if (this.finished) {
                return false;
            } else {
                init();
                initPartialTraversalState();
                this.nextToConsume = 0;
                this.bufferSink.begin(this.spliterator.getExactSizeIfKnown());
                return fillBuffer();
            }
        }

        public Spliterator<P_OUT> trySplit() {
            if (!this.isParallel || this.finished) {
                return null;
            }
            init();
            Spliterator<P_IN> trySplit = this.spliterator.trySplit();
            if (trySplit == null) {
                return null;
            }
            return wrap(trySplit);
        }

        private boolean fillBuffer() {
            while (this.buffer.count() == 0) {
                if (this.bufferSink.cancellationRequested() || !this.pusher.getAsBoolean()) {
                    if (this.finished) {
                        return false;
                    }
                    this.bufferSink.end();
                    this.finished = true;
                }
            }
            return true;
        }

        public final long estimateSize() {
            init();
            return this.spliterator.estimateSize();
        }

        public final long getExactSizeIfKnown() {
            init();
            if (StreamOpFlag.SIZED.isKnown(this.ph.getStreamAndOpFlags())) {
                return this.spliterator.getExactSizeIfKnown();
            }
            return -1;
        }

        public final int characteristics() {
            init();
            int c = StreamOpFlag.toCharacteristics(StreamOpFlag.toStreamFlags(this.ph.getStreamAndOpFlags()));
            if ((c & 64) != 0) {
                return (c & -16449) | (this.spliterator.characteristics() & 16448);
            }
            return c;
        }

        public Comparator<? super P_OUT> getComparator() {
            if (hasCharacteristics(4)) {
                return null;
            }
            throw new IllegalStateException();
        }

        public final String toString() {
            return String.format("%s[%s]", new Object[]{getClass().getName(), this.spliterator});
        }
    }

    static final class WrappingSpliterator<P_IN, P_OUT> extends AbstractWrappingSpliterator<P_IN, P_OUT, SpinedBuffer<P_OUT>> {
        WrappingSpliterator(PipelineHelper<P_OUT> ph, Supplier<Spliterator<P_IN>> supplier, boolean parallel) {
            super(ph, supplier, parallel);
        }

        WrappingSpliterator(PipelineHelper<P_OUT> ph, Spliterator<P_IN> spliterator, boolean parallel) {
            super(ph, spliterator, parallel);
        }

        /* access modifiers changed from: package-private */
        public WrappingSpliterator<P_IN, P_OUT> wrap(Spliterator<P_IN> spliterator) {
            return new WrappingSpliterator<>(this.ph, spliterator, this.isParallel);
        }

        /* access modifiers changed from: package-private */
        public void initPartialTraversalState() {
            SpinedBuffer<P_OUT> b = new SpinedBuffer<>();
            this.buffer = b;
            PipelineHelper pipelineHelper = this.ph;
            b.getClass();
            this.bufferSink = pipelineHelper.wrapSink(new StreamSpliterators$WrappingSpliterator$$ExternalSyntheticLambda2(b));
            this.pusher = new StreamSpliterators$WrappingSpliterator$$ExternalSyntheticLambda0(this);
        }

        /* renamed from: lambda$initPartialTraversalState$0$java-util-stream-StreamSpliterators$WrappingSpliterator  reason: not valid java name */
        public /* synthetic */ boolean m519xvar_cCLASSNAMEf() {
            return this.spliterator.tryAdvance(this.bufferSink);
        }

        public boolean tryAdvance(Consumer<? super P_OUT> consumer) {
            consumer.getClass();
            boolean hasNext = doAdvance();
            if (hasNext) {
                consumer.accept(((SpinedBuffer) this.buffer).get(this.nextToConsume));
            }
            return hasNext;
        }

        public void forEachRemaining(Consumer<? super P_OUT> consumer) {
            if (this.buffer != null || this.finished) {
                do {
                } while (tryAdvance(consumer));
                return;
            }
            consumer.getClass();
            init();
            PipelineHelper pipelineHelper = this.ph;
            consumer.getClass();
            pipelineHelper.wrapAndCopyInto(new StreamSpliterators$WrappingSpliterator$$ExternalSyntheticLambda1(consumer), this.spliterator);
            this.finished = true;
        }
    }

    static final class IntWrappingSpliterator<P_IN> extends AbstractWrappingSpliterator<P_IN, Integer, SpinedBuffer.OfInt> implements Spliterator.OfInt {
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
        }

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
        }

        IntWrappingSpliterator(PipelineHelper<Integer> ph, Supplier<Spliterator<P_IN>> supplier, boolean parallel) {
            super(ph, supplier, parallel);
        }

        IntWrappingSpliterator(PipelineHelper<Integer> ph, Spliterator<P_IN> spliterator, boolean parallel) {
            super(ph, spliterator, parallel);
        }

        /* access modifiers changed from: package-private */
        public AbstractWrappingSpliterator<P_IN, Integer, ?> wrap(Spliterator<P_IN> spliterator) {
            return new IntWrappingSpliterator((PipelineHelper<Integer>) this.ph, spliterator, this.isParallel);
        }

        /* access modifiers changed from: package-private */
        public void initPartialTraversalState() {
            SpinedBuffer.OfInt b = new SpinedBuffer.OfInt();
            this.buffer = b;
            PipelineHelper pipelineHelper = this.ph;
            b.getClass();
            this.bufferSink = pipelineHelper.wrapSink(new StreamSpliterators$IntWrappingSpliterator$$ExternalSyntheticLambda2(b));
            this.pusher = new StreamSpliterators$IntWrappingSpliterator$$ExternalSyntheticLambda0(this);
        }

        /* renamed from: lambda$initPartialTraversalState$0$java-util-stream-StreamSpliterators$IntWrappingSpliterator  reason: not valid java name */
        public /* synthetic */ boolean m517x68714704() {
            return this.spliterator.tryAdvance(this.bufferSink);
        }

        public Spliterator.OfInt trySplit() {
            return (Spliterator.OfInt) super.trySplit();
        }

        public boolean tryAdvance(IntConsumer consumer) {
            consumer.getClass();
            boolean hasNext = doAdvance();
            if (hasNext) {
                consumer.accept(((SpinedBuffer.OfInt) this.buffer).get(this.nextToConsume));
            }
            return hasNext;
        }

        public void forEachRemaining(IntConsumer consumer) {
            if (this.buffer != null || this.finished) {
                do {
                } while (tryAdvance(consumer));
                return;
            }
            consumer.getClass();
            init();
            PipelineHelper pipelineHelper = this.ph;
            consumer.getClass();
            pipelineHelper.wrapAndCopyInto(new StreamSpliterators$IntWrappingSpliterator$$ExternalSyntheticLambda1(consumer), this.spliterator);
            this.finished = true;
        }
    }

    static final class LongWrappingSpliterator<P_IN> extends AbstractWrappingSpliterator<P_IN, Long, SpinedBuffer.OfLong> implements Spliterator.OfLong {
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
        }

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
        }

        LongWrappingSpliterator(PipelineHelper<Long> ph, Supplier<Spliterator<P_IN>> supplier, boolean parallel) {
            super(ph, supplier, parallel);
        }

        LongWrappingSpliterator(PipelineHelper<Long> ph, Spliterator<P_IN> spliterator, boolean parallel) {
            super(ph, spliterator, parallel);
        }

        /* access modifiers changed from: package-private */
        public AbstractWrappingSpliterator<P_IN, Long, ?> wrap(Spliterator<P_IN> spliterator) {
            return new LongWrappingSpliterator((PipelineHelper<Long>) this.ph, spliterator, this.isParallel);
        }

        /* access modifiers changed from: package-private */
        public void initPartialTraversalState() {
            SpinedBuffer.OfLong b = new SpinedBuffer.OfLong();
            this.buffer = b;
            PipelineHelper pipelineHelper = this.ph;
            b.getClass();
            this.bufferSink = pipelineHelper.wrapSink(new StreamSpliterators$LongWrappingSpliterator$$ExternalSyntheticLambda2(b));
            this.pusher = new StreamSpliterators$LongWrappingSpliterator$$ExternalSyntheticLambda0(this);
        }

        /* renamed from: lambda$initPartialTraversalState$0$java-util-stream-StreamSpliterators$LongWrappingSpliterator  reason: not valid java name */
        public /* synthetic */ boolean m518x44d1e433() {
            return this.spliterator.tryAdvance(this.bufferSink);
        }

        public Spliterator.OfLong trySplit() {
            return (Spliterator.OfLong) super.trySplit();
        }

        public boolean tryAdvance(LongConsumer consumer) {
            consumer.getClass();
            boolean hasNext = doAdvance();
            if (hasNext) {
                consumer.accept(((SpinedBuffer.OfLong) this.buffer).get(this.nextToConsume));
            }
            return hasNext;
        }

        public void forEachRemaining(LongConsumer consumer) {
            if (this.buffer != null || this.finished) {
                do {
                } while (tryAdvance(consumer));
                return;
            }
            consumer.getClass();
            init();
            PipelineHelper pipelineHelper = this.ph;
            consumer.getClass();
            pipelineHelper.wrapAndCopyInto(new StreamSpliterators$LongWrappingSpliterator$$ExternalSyntheticLambda1(consumer), this.spliterator);
            this.finished = true;
        }
    }

    static final class DoubleWrappingSpliterator<P_IN> extends AbstractWrappingSpliterator<P_IN, Double, SpinedBuffer.OfDouble> implements Spliterator.OfDouble {
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
        }

        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
        }

        DoubleWrappingSpliterator(PipelineHelper<Double> ph, Supplier<Spliterator<P_IN>> supplier, boolean parallel) {
            super(ph, supplier, parallel);
        }

        DoubleWrappingSpliterator(PipelineHelper<Double> ph, Spliterator<P_IN> spliterator, boolean parallel) {
            super(ph, spliterator, parallel);
        }

        /* access modifiers changed from: package-private */
        public AbstractWrappingSpliterator<P_IN, Double, ?> wrap(Spliterator<P_IN> spliterator) {
            return new DoubleWrappingSpliterator((PipelineHelper<Double>) this.ph, spliterator, this.isParallel);
        }

        /* access modifiers changed from: package-private */
        public void initPartialTraversalState() {
            SpinedBuffer.OfDouble b = new SpinedBuffer.OfDouble();
            this.buffer = b;
            PipelineHelper pipelineHelper = this.ph;
            b.getClass();
            this.bufferSink = pipelineHelper.wrapSink(new StreamSpliterators$DoubleWrappingSpliterator$$ExternalSyntheticLambda2(b));
            this.pusher = new StreamSpliterators$DoubleWrappingSpliterator$$ExternalSyntheticLambda0(this);
        }

        /* renamed from: lambda$initPartialTraversalState$0$java-util-stream-StreamSpliterators$DoubleWrappingSpliterator  reason: not valid java name */
        public /* synthetic */ boolean m516xbf8var_e() {
            return this.spliterator.tryAdvance(this.bufferSink);
        }

        public Spliterator.OfDouble trySplit() {
            return (Spliterator.OfDouble) super.trySplit();
        }

        public boolean tryAdvance(DoubleConsumer consumer) {
            consumer.getClass();
            boolean hasNext = doAdvance();
            if (hasNext) {
                consumer.accept(((SpinedBuffer.OfDouble) this.buffer).get(this.nextToConsume));
            }
            return hasNext;
        }

        public void forEachRemaining(DoubleConsumer consumer) {
            if (this.buffer != null || this.finished) {
                do {
                } while (tryAdvance(consumer));
                return;
            }
            consumer.getClass();
            init();
            PipelineHelper pipelineHelper = this.ph;
            consumer.getClass();
            pipelineHelper.wrapAndCopyInto(new StreamSpliterators$DoubleWrappingSpliterator$$ExternalSyntheticLambda1(consumer), this.spliterator);
            this.finished = true;
        }
    }

    static class DelegatingSpliterator<T, T_SPLITR extends Spliterator<T>> implements Spliterator<T> {
        private T_SPLITR s;
        private final Supplier<? extends T_SPLITR> supplier;

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        DelegatingSpliterator(Supplier<? extends T_SPLITR> supplier2) {
            this.supplier = supplier2;
        }

        /* access modifiers changed from: package-private */
        public T_SPLITR get() {
            if (this.s == null) {
                this.s = (Spliterator) this.supplier.get();
            }
            return this.s;
        }

        public T_SPLITR trySplit() {
            return get().trySplit();
        }

        public boolean tryAdvance(Consumer<? super T> consumer) {
            return get().tryAdvance(consumer);
        }

        public void forEachRemaining(Consumer<? super T> consumer) {
            get().forEachRemaining(consumer);
        }

        public long estimateSize() {
            return get().estimateSize();
        }

        public int characteristics() {
            return get().characteristics();
        }

        public Comparator<? super T> getComparator() {
            return get().getComparator();
        }

        public long getExactSizeIfKnown() {
            return get().getExactSizeIfKnown();
        }

        public String toString() {
            return getClass().getName() + "[" + get() + "]";
        }

        static class OfPrimitive<T, T_CONS, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends DelegatingSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
            public /* bridge */ /* synthetic */ Spliterator.OfPrimitive trySplit() {
                return (Spliterator.OfPrimitive) super.trySplit();
            }

            OfPrimitive(Supplier<? extends T_SPLITR> supplier) {
                super(supplier);
            }

            public boolean tryAdvance(T_CONS consumer) {
                return ((Spliterator.OfPrimitive) get()).tryAdvance(consumer);
            }

            public void forEachRemaining(T_CONS consumer) {
                ((Spliterator.OfPrimitive) get()).forEachRemaining(consumer);
            }
        }

        static final class OfInt extends OfPrimitive<Integer, IntConsumer, Spliterator.OfInt> implements Spliterator.OfInt {
            public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                super.forEachRemaining(intConsumer);
            }

            public /* bridge */ /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
                return super.tryAdvance(intConsumer);
            }

            public /* bridge */ /* synthetic */ Spliterator.OfInt trySplit() {
                return (Spliterator.OfInt) super.trySplit();
            }

            OfInt(Supplier<Spliterator.OfInt> supplier) {
                super(supplier);
            }
        }

        static final class OfLong extends OfPrimitive<Long, LongConsumer, Spliterator.OfLong> implements Spliterator.OfLong {
            public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                super.forEachRemaining(longConsumer);
            }

            public /* bridge */ /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
                return super.tryAdvance(longConsumer);
            }

            public /* bridge */ /* synthetic */ Spliterator.OfLong trySplit() {
                return (Spliterator.OfLong) super.trySplit();
            }

            OfLong(Supplier<Spliterator.OfLong> supplier) {
                super(supplier);
            }
        }

        static final class OfDouble extends OfPrimitive<Double, DoubleConsumer, Spliterator.OfDouble> implements Spliterator.OfDouble {
            public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                super.forEachRemaining(doubleConsumer);
            }

            public /* bridge */ /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
                return super.tryAdvance(doubleConsumer);
            }

            public /* bridge */ /* synthetic */ Spliterator.OfDouble trySplit() {
                return (Spliterator.OfDouble) super.trySplit();
            }

            OfDouble(Supplier<Spliterator.OfDouble> supplier) {
                super(supplier);
            }
        }
    }

    static abstract class SliceSpliterator<T, T_SPLITR extends Spliterator<T>> {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        long fence;
        long index;
        T_SPLITR s;
        final long sliceFence;
        final long sliceOrigin;

        /* access modifiers changed from: protected */
        public abstract T_SPLITR makeSpliterator(T_SPLITR t_splitr, long j, long j2, long j3, long j4);

        SliceSpliterator(T_SPLITR s2, long sliceOrigin2, long sliceFence2, long origin, long fence2) {
            if ($assertionsDisabled || s2.hasCharacteristics(16384)) {
                this.s = s2;
                this.sliceOrigin = sliceOrigin2;
                this.sliceFence = sliceFence2;
                this.index = origin;
                this.fence = fence2;
                return;
            }
            throw new AssertionError();
        }

        public T_SPLITR trySplit() {
            long j = this.sliceOrigin;
            long j2 = this.fence;
            if (j >= j2 || this.index >= j2) {
                return null;
            }
            while (true) {
                T_SPLITR leftSplit = this.s.trySplit();
                if (leftSplit == null) {
                    return null;
                }
                long leftSplitFenceUnbounded = this.index + leftSplit.estimateSize();
                long leftSplitFence = Math.min(leftSplitFenceUnbounded, this.sliceFence);
                long j3 = this.sliceOrigin;
                if (j3 >= leftSplitFence) {
                    this.index = leftSplitFence;
                } else {
                    long j4 = this.sliceFence;
                    if (leftSplitFence >= j4) {
                        this.s = leftSplit;
                        this.fence = leftSplitFence;
                    } else {
                        long j5 = this.index;
                        if (j5 < j3 || leftSplitFenceUnbounded > j4) {
                            this.index = leftSplitFence;
                            return makeSpliterator(leftSplit, j3, j4, j5, leftSplitFence);
                        }
                        this.index = leftSplitFence;
                        return leftSplit;
                    }
                }
            }
        }

        public long estimateSize() {
            long j = this.sliceOrigin;
            long j2 = this.fence;
            if (j < j2) {
                return j2 - Math.max(j, this.index);
            }
            return 0;
        }

        public int characteristics() {
            return this.s.characteristics();
        }

        static final class OfRef<T> extends SliceSpliterator<T, Spliterator<T>> implements Spliterator<T> {
            public /* synthetic */ Comparator getComparator() {
                return Spliterator.CC.$default$getComparator(this);
            }

            public /* synthetic */ long getExactSizeIfKnown() {
                return Spliterator.CC.$default$getExactSizeIfKnown(this);
            }

            public /* synthetic */ boolean hasCharacteristics(int i) {
                return Spliterator.CC.$default$hasCharacteristics(this, i);
            }

            OfRef(Spliterator<T> spliterator, long sliceOrigin, long sliceFence) {
                this(spliterator, sliceOrigin, sliceFence, 0, Math.min(spliterator.estimateSize(), sliceFence));
            }

            private OfRef(Spliterator<T> spliterator, long sliceOrigin, long sliceFence, long origin, long fence) {
                super(spliterator, sliceOrigin, sliceFence, origin, fence);
            }

            /* access modifiers changed from: protected */
            public Spliterator<T> makeSpliterator(Spliterator<T> spliterator, long sliceOrigin, long sliceFence, long origin, long fence) {
                return new OfRef(spliterator, sliceOrigin, sliceFence, origin, fence);
            }

            public boolean tryAdvance(Consumer<? super T> consumer) {
                consumer.getClass();
                if (this.sliceOrigin >= this.fence) {
                    return false;
                }
                while (this.sliceOrigin > this.index) {
                    this.s.tryAdvance(StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda1.INSTANCE);
                    this.index++;
                }
                if (this.index >= this.fence) {
                    return false;
                }
                this.index++;
                return this.s.tryAdvance(consumer);
            }

            static /* synthetic */ void lambda$tryAdvance$0(Object e) {
            }

            public void forEachRemaining(Consumer<? super T> consumer) {
                consumer.getClass();
                if (this.sliceOrigin >= this.fence || this.index >= this.fence) {
                    return;
                }
                if (this.index < this.sliceOrigin || this.index + this.s.estimateSize() > this.sliceFence) {
                    while (this.sliceOrigin > this.index) {
                        this.s.tryAdvance(StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda0.INSTANCE);
                        this.index++;
                    }
                    while (this.index < this.fence) {
                        this.s.tryAdvance(consumer);
                        this.index++;
                    }
                    return;
                }
                this.s.forEachRemaining(consumer);
                this.index = this.fence;
            }

            static /* synthetic */ void lambda$forEachRemaining$1(Object e) {
            }
        }

        static abstract class OfPrimitive<T, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>, T_CONS> extends SliceSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
            /* access modifiers changed from: protected */
            public abstract T_CONS emptyConsumer();

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

            public /* bridge */ /* synthetic */ Spliterator.OfPrimitive trySplit() {
                return (Spliterator.OfPrimitive) super.trySplit();
            }

            OfPrimitive(T_SPLITR s, long sliceOrigin, long sliceFence) {
                this(s, sliceOrigin, sliceFence, 0, Math.min(s.estimateSize(), sliceFence));
            }

            private OfPrimitive(T_SPLITR s, long sliceOrigin, long sliceFence, long origin, long fence) {
                super(s, sliceOrigin, sliceFence, origin, fence);
            }

            public boolean tryAdvance(T_CONS action) {
                action.getClass();
                if (this.sliceOrigin >= this.fence) {
                    return false;
                }
                while (this.sliceOrigin > this.index) {
                    ((Spliterator.OfPrimitive) this.s).tryAdvance(emptyConsumer());
                    this.index++;
                }
                if (this.index >= this.fence) {
                    return false;
                }
                this.index++;
                return ((Spliterator.OfPrimitive) this.s).tryAdvance(action);
            }

            public void forEachRemaining(T_CONS action) {
                action.getClass();
                if (this.sliceOrigin >= this.fence || this.index >= this.fence) {
                    return;
                }
                if (this.index < this.sliceOrigin || this.index + ((Spliterator.OfPrimitive) this.s).estimateSize() > this.sliceFence) {
                    while (this.sliceOrigin > this.index) {
                        ((Spliterator.OfPrimitive) this.s).tryAdvance(emptyConsumer());
                        this.index++;
                    }
                    while (this.index < this.fence) {
                        ((Spliterator.OfPrimitive) this.s).tryAdvance(action);
                        this.index++;
                    }
                    return;
                }
                ((Spliterator.OfPrimitive) this.s).forEachRemaining(action);
                this.index = this.fence;
            }
        }

        static final class OfInt extends OfPrimitive<Integer, Spliterator.OfInt, IntConsumer> implements Spliterator.OfInt {
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

            OfInt(Spliterator.OfInt s, long sliceOrigin, long sliceFence) {
                super(s, sliceOrigin, sliceFence);
            }

            OfInt(Spliterator.OfInt s, long sliceOrigin, long sliceFence, long origin, long fence) {
                super(s, sliceOrigin, sliceFence, origin, fence);
            }

            /* access modifiers changed from: protected */
            public Spliterator.OfInt makeSpliterator(Spliterator.OfInt s, long sliceOrigin, long sliceFence, long origin, long fence) {
                return new OfInt(s, sliceOrigin, sliceFence, origin, fence);
            }

            static /* synthetic */ void lambda$emptyConsumer$0(int e) {
            }

            /* access modifiers changed from: protected */
            public IntConsumer emptyConsumer() {
                return StreamSpliterators$SliceSpliterator$OfInt$$ExternalSyntheticLambda0.INSTANCE;
            }
        }

        static final class OfLong extends OfPrimitive<Long, Spliterator.OfLong, LongConsumer> implements Spliterator.OfLong {
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

            OfLong(Spliterator.OfLong s, long sliceOrigin, long sliceFence) {
                super(s, sliceOrigin, sliceFence);
            }

            OfLong(Spliterator.OfLong s, long sliceOrigin, long sliceFence, long origin, long fence) {
                super(s, sliceOrigin, sliceFence, origin, fence);
            }

            /* access modifiers changed from: protected */
            public Spliterator.OfLong makeSpliterator(Spliterator.OfLong s, long sliceOrigin, long sliceFence, long origin, long fence) {
                return new OfLong(s, sliceOrigin, sliceFence, origin, fence);
            }

            static /* synthetic */ void lambda$emptyConsumer$0(long e) {
            }

            /* access modifiers changed from: protected */
            public LongConsumer emptyConsumer() {
                return StreamSpliterators$SliceSpliterator$OfLong$$ExternalSyntheticLambda0.INSTANCE;
            }
        }

        static final class OfDouble extends OfPrimitive<Double, Spliterator.OfDouble, DoubleConsumer> implements Spliterator.OfDouble {
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

            OfDouble(Spliterator.OfDouble s, long sliceOrigin, long sliceFence) {
                super(s, sliceOrigin, sliceFence);
            }

            OfDouble(Spliterator.OfDouble s, long sliceOrigin, long sliceFence, long origin, long fence) {
                super(s, sliceOrigin, sliceFence, origin, fence);
            }

            /* access modifiers changed from: protected */
            public Spliterator.OfDouble makeSpliterator(Spliterator.OfDouble s, long sliceOrigin, long sliceFence, long origin, long fence) {
                return new OfDouble(s, sliceOrigin, sliceFence, origin, fence);
            }

            static /* synthetic */ void lambda$emptyConsumer$0(double e) {
            }

            /* access modifiers changed from: protected */
            public DoubleConsumer emptyConsumer() {
                return StreamSpliterators$SliceSpliterator$OfDouble$$ExternalSyntheticLambda0.INSTANCE;
            }
        }
    }

    static abstract class UnorderedSliceSpliterator<T, T_SPLITR extends Spliterator<T>> {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        static final int CHUNK_SIZE = 128;
        private final AtomicLong permits;
        protected final T_SPLITR s;
        private final long skipThreshold;
        protected final boolean unlimited;

        enum PermitStatus {
            NO_MORE,
            MAYBE_MORE,
            UNLIMITED
        }

        /* access modifiers changed from: protected */
        public abstract T_SPLITR makeSpliterator(T_SPLITR t_splitr);

        UnorderedSliceSpliterator(T_SPLITR s2, long skip, long limit) {
            this.s = s2;
            this.unlimited = limit < 0;
            this.skipThreshold = limit >= 0 ? limit : 0;
            this.permits = new AtomicLong(limit >= 0 ? skip + limit : skip);
        }

        UnorderedSliceSpliterator(T_SPLITR s2, UnorderedSliceSpliterator<T, T_SPLITR> parent) {
            this.s = s2;
            this.unlimited = parent.unlimited;
            this.permits = parent.permits;
            this.skipThreshold = parent.skipThreshold;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:11:0x0021  */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x001b A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final long acquirePermits(long r10) {
            /*
                r9 = this;
                boolean r0 = $assertionsDisabled
                r1 = 0
                if (r0 != 0) goto L_0x0011
                int r0 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x000b
                goto L_0x0011
            L_0x000b:
                java.lang.AssertionError r0 = new java.lang.AssertionError
                r0.<init>()
                throw r0
            L_0x0011:
                java.util.concurrent.atomic.AtomicLong r0 = r9.permits
                long r3 = r0.get()
                int r0 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
                if (r0 != 0) goto L_0x0021
                boolean r0 = r9.unlimited
                if (r0 == 0) goto L_0x0020
                r1 = r10
            L_0x0020:
                return r1
            L_0x0021:
                long r5 = java.lang.Math.min(r3, r10)
                int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x0033
                java.util.concurrent.atomic.AtomicLong r0 = r9.permits
                long r7 = r3 - r5
                boolean r0 = r0.compareAndSet(r3, r7)
                if (r0 == 0) goto L_0x0011
            L_0x0033:
                boolean r0 = r9.unlimited
                if (r0 == 0) goto L_0x003e
                long r7 = r10 - r5
                long r0 = java.lang.Math.max(r7, r1)
                return r0
            L_0x003e:
                long r7 = r9.skipThreshold
                int r0 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                if (r0 <= 0) goto L_0x004d
                long r7 = r3 - r7
                long r7 = r5 - r7
                long r0 = java.lang.Math.max(r7, r1)
                return r0
            L_0x004d:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator.acquirePermits(long):long");
        }

        /* access modifiers changed from: protected */
        public final PermitStatus permitStatus() {
            if (this.permits.get() > 0) {
                return PermitStatus.MAYBE_MORE;
            }
            return this.unlimited ? PermitStatus.UNLIMITED : PermitStatus.NO_MORE;
        }

        public final T_SPLITR trySplit() {
            T_SPLITR split;
            if (this.permits.get() == 0 || (split = this.s.trySplit()) == null) {
                return null;
            }
            return makeSpliterator(split);
        }

        public final long estimateSize() {
            return this.s.estimateSize();
        }

        public final int characteristics() {
            return this.s.characteristics() & -16465;
        }

        static final class OfRef<T> extends UnorderedSliceSpliterator<T, Spliterator<T>> implements Spliterator<T>, Consumer<T> {
            T tmpSlot;

            public /* synthetic */ Consumer andThen(Consumer consumer) {
                return Consumer.CC.$default$andThen(this, consumer);
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

            OfRef(Spliterator<T> spliterator, long skip, long limit) {
                super(spliterator, skip, limit);
            }

            OfRef(Spliterator<T> spliterator, OfRef<T> parent) {
                super(spliterator, parent);
            }

            public final void accept(T t) {
                this.tmpSlot = t;
            }

            public boolean tryAdvance(Consumer<? super T> consumer) {
                consumer.getClass();
                while (permitStatus() != PermitStatus.NO_MORE && this.s.tryAdvance(this)) {
                    if (acquirePermits(1) == 1) {
                        consumer.accept(this.tmpSlot);
                        this.tmpSlot = null;
                        return true;
                    }
                }
                return false;
            }

            public void forEachRemaining(Consumer<? super T> consumer) {
                consumer.getClass();
                StreamSpliterators.ArrayBuffer.OfRef<T> sb = null;
                while (true) {
                    PermitStatus permitStatus = permitStatus();
                    PermitStatus permitStatus2 = permitStatus;
                    if (permitStatus == PermitStatus.NO_MORE) {
                        return;
                    }
                    if (permitStatus2 == PermitStatus.MAYBE_MORE) {
                        if (sb == null) {
                            sb = new ArrayBuffer.OfRef<>(128);
                        } else {
                            sb.reset();
                        }
                        long permitsRequested = 0;
                        while (this.s.tryAdvance(sb)) {
                            long j = 1 + permitsRequested;
                            permitsRequested = j;
                            if (j >= 128) {
                                break;
                            }
                        }
                        if (permitsRequested != 0) {
                            sb.forEach(consumer, acquirePermits(permitsRequested));
                        } else {
                            return;
                        }
                    } else {
                        this.s.forEachRemaining(consumer);
                        return;
                    }
                }
            }

            /* access modifiers changed from: protected */
            public Spliterator<T> makeSpliterator(Spliterator<T> spliterator) {
                return new OfRef(spliterator, this);
            }
        }

        static abstract class OfPrimitive<T, T_CONS, T_BUFF extends ArrayBuffer.OfPrimitive<T_CONS>, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends UnorderedSliceSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
            /* access modifiers changed from: protected */
            public abstract void acceptConsumed(T_CONS t_cons);

            /* access modifiers changed from: protected */
            public abstract T_BUFF bufferCreate(int i);

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

            public /* bridge */ /* synthetic */ Spliterator.OfPrimitive trySplit() {
                return (Spliterator.OfPrimitive) super.trySplit();
            }

            OfPrimitive(T_SPLITR s, long skip, long limit) {
                super(s, skip, limit);
            }

            OfPrimitive(T_SPLITR s, OfPrimitive<T, T_CONS, T_BUFF, T_SPLITR> parent) {
                super(s, parent);
            }

            public boolean tryAdvance(T_CONS action) {
                action.getClass();
                while (permitStatus() != PermitStatus.NO_MORE && ((Spliterator.OfPrimitive) this.s).tryAdvance(this)) {
                    if (acquirePermits(1) == 1) {
                        acceptConsumed(action);
                        return true;
                    }
                }
                return false;
            }

            public void forEachRemaining(T_CONS action) {
                action.getClass();
                T_BUFF sb = null;
                while (true) {
                    PermitStatus permitStatus = permitStatus();
                    PermitStatus permitStatus2 = permitStatus;
                    if (permitStatus == PermitStatus.NO_MORE) {
                        return;
                    }
                    if (permitStatus2 == PermitStatus.MAYBE_MORE) {
                        if (sb == null) {
                            sb = bufferCreate(128);
                        } else {
                            sb.reset();
                        }
                        T_BUFF sbc = sb;
                        long permitsRequested = 0;
                        while (((Spliterator.OfPrimitive) this.s).tryAdvance(sbc)) {
                            long j = 1 + permitsRequested;
                            permitsRequested = j;
                            if (j >= 128) {
                                break;
                            }
                        }
                        if (permitsRequested != 0) {
                            sb.forEach(action, acquirePermits(permitsRequested));
                        } else {
                            return;
                        }
                    } else {
                        ((Spliterator.OfPrimitive) this.s).forEachRemaining(action);
                        return;
                    }
                }
            }
        }

        static final class OfInt extends OfPrimitive<Integer, IntConsumer, ArrayBuffer.OfInt, Spliterator.OfInt> implements Spliterator.OfInt, IntConsumer {
            int tmpValue;

            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return IntConsumer.CC.$default$andThen(this, intConsumer);
            }

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

            OfInt(Spliterator.OfInt s, long skip, long limit) {
                super(s, skip, limit);
            }

            OfInt(Spliterator.OfInt s, OfInt parent) {
                super(s, parent);
            }

            public void accept(int value) {
                this.tmpValue = value;
            }

            /* access modifiers changed from: protected */
            public void acceptConsumed(IntConsumer action) {
                action.accept(this.tmpValue);
            }

            /* access modifiers changed from: protected */
            public ArrayBuffer.OfInt bufferCreate(int initialCapacity) {
                return new ArrayBuffer.OfInt(initialCapacity);
            }

            /* access modifiers changed from: protected */
            public Spliterator.OfInt makeSpliterator(Spliterator.OfInt s) {
                return new OfInt(s, this);
            }
        }

        static final class OfLong extends OfPrimitive<Long, LongConsumer, ArrayBuffer.OfLong, Spliterator.OfLong> implements Spliterator.OfLong, LongConsumer {
            long tmpValue;

            public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                return LongConsumer.CC.$default$andThen(this, longConsumer);
            }

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

            OfLong(Spliterator.OfLong s, long skip, long limit) {
                super(s, skip, limit);
            }

            OfLong(Spliterator.OfLong s, OfLong parent) {
                super(s, parent);
            }

            public void accept(long value) {
                this.tmpValue = value;
            }

            /* access modifiers changed from: protected */
            public void acceptConsumed(LongConsumer action) {
                action.accept(this.tmpValue);
            }

            /* access modifiers changed from: protected */
            public ArrayBuffer.OfLong bufferCreate(int initialCapacity) {
                return new ArrayBuffer.OfLong(initialCapacity);
            }

            /* access modifiers changed from: protected */
            public Spliterator.OfLong makeSpliterator(Spliterator.OfLong s) {
                return new OfLong(s, this);
            }
        }

        static final class OfDouble extends OfPrimitive<Double, DoubleConsumer, ArrayBuffer.OfDouble, Spliterator.OfDouble> implements Spliterator.OfDouble, DoubleConsumer {
            double tmpValue;

            public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
            }

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

            OfDouble(Spliterator.OfDouble s, long skip, long limit) {
                super(s, skip, limit);
            }

            OfDouble(Spliterator.OfDouble s, OfDouble parent) {
                super(s, parent);
            }

            public void accept(double value) {
                this.tmpValue = value;
            }

            /* access modifiers changed from: protected */
            public void acceptConsumed(DoubleConsumer action) {
                action.accept(this.tmpValue);
            }

            /* access modifiers changed from: protected */
            public ArrayBuffer.OfDouble bufferCreate(int initialCapacity) {
                return new ArrayBuffer.OfDouble(initialCapacity);
            }

            /* access modifiers changed from: protected */
            public Spliterator.OfDouble makeSpliterator(Spliterator.OfDouble s) {
                return new OfDouble(s, this);
            }
        }
    }

    static final class DistinctSpliterator<T> implements Spliterator<T>, Consumer<T> {
        private static final Object NULL_VALUE = new Object();
        private final Spliterator<T> s;
        private final ConcurrentHashMap<T, Boolean> seen;
        private T tmpSlot;

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        DistinctSpliterator(Spliterator<T> spliterator) {
            this(spliterator, new ConcurrentHashMap());
        }

        private DistinctSpliterator(Spliterator<T> spliterator, ConcurrentHashMap<T, Boolean> concurrentHashMap) {
            this.s = spliterator;
            this.seen = concurrentHashMap;
        }

        public void accept(T t) {
            this.tmpSlot = t;
        }

        private T mapNull(T t) {
            return t != null ? t : NULL_VALUE;
        }

        public boolean tryAdvance(Consumer<? super T> consumer) {
            while (this.s.tryAdvance(this)) {
                if (this.seen.putIfAbsent(mapNull(this.tmpSlot), Boolean.TRUE) == null) {
                    consumer.accept(this.tmpSlot);
                    this.tmpSlot = null;
                    return true;
                }
            }
            return false;
        }

        public void forEachRemaining(Consumer<? super T> consumer) {
            this.s.forEachRemaining(new StreamSpliterators$DistinctSpliterator$$ExternalSyntheticLambda0(this, consumer));
        }

        /* renamed from: lambda$forEachRemaining$0$java-util-stream-StreamSpliterators$DistinctSpliterator  reason: not valid java name */
        public /* synthetic */ void m515xb9bff3f1(Consumer action, Object t) {
            if (this.seen.putIfAbsent(mapNull(t), Boolean.TRUE) == null) {
                action.accept(t);
            }
        }

        public Spliterator<T> trySplit() {
            Spliterator<T> trySplit = this.s.trySplit();
            if (trySplit != null) {
                return new DistinctSpliterator(trySplit, this.seen);
            }
            return null;
        }

        public long estimateSize() {
            return this.s.estimateSize();
        }

        public int characteristics() {
            return (this.s.characteristics() & -16469) | 1;
        }

        public Comparator<? super T> getComparator() {
            return this.s.getComparator();
        }
    }

    static abstract class InfiniteSupplyingSpliterator<T> implements Spliterator<T> {
        long estimate;

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

        protected InfiniteSupplyingSpliterator(long estimate2) {
            this.estimate = estimate2;
        }

        public long estimateSize() {
            return this.estimate;
        }

        public int characteristics() {
            return 1024;
        }

        static final class OfRef<T> extends InfiniteSupplyingSpliterator<T> {
            final Supplier<T> s;

            OfRef(long size, Supplier<T> supplier) {
                super(size);
                this.s = supplier;
            }

            public boolean tryAdvance(Consumer<? super T> consumer) {
                consumer.getClass();
                consumer.accept(this.s.get());
                return true;
            }

            public Spliterator<T> trySplit() {
                if (this.estimate == 0) {
                    return null;
                }
                long j = this.estimate >>> 1;
                this.estimate = j;
                return new OfRef(j, this.s);
            }
        }

        static final class OfInt extends InfiniteSupplyingSpliterator<Integer> implements Spliterator.OfInt {
            final IntSupplier s;

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
            }

            public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, intConsumer);
            }

            public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
                forEachRemaining((IntConsumer) obj);
            }

            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
            }

            OfInt(long size, IntSupplier s2) {
                super(size);
                this.s = s2;
            }

            public boolean tryAdvance(IntConsumer action) {
                action.getClass();
                action.accept(this.s.getAsInt());
                return true;
            }

            public Spliterator.OfInt trySplit() {
                if (this.estimate == 0) {
                    return null;
                }
                long j = this.estimate >>> 1;
                this.estimate = j;
                return new OfInt(j, this.s);
            }
        }

        static final class OfLong extends InfiniteSupplyingSpliterator<Long> implements Spliterator.OfLong {
            final LongSupplier s;

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
            }

            public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, longConsumer);
            }

            public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
                forEachRemaining((LongConsumer) obj);
            }

            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
            }

            OfLong(long size, LongSupplier s2) {
                super(size);
                this.s = s2;
            }

            public boolean tryAdvance(LongConsumer action) {
                action.getClass();
                action.accept(this.s.getAsLong());
                return true;
            }

            public Spliterator.OfLong trySplit() {
                if (this.estimate == 0) {
                    return null;
                }
                long j = this.estimate >>> 1;
                this.estimate = j;
                return new OfLong(j, this.s);
            }
        }

        static final class OfDouble extends InfiniteSupplyingSpliterator<Double> implements Spliterator.OfDouble {
            final DoubleSupplier s;

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
            }

            public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, doubleConsumer);
            }

            public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
                forEachRemaining((DoubleConsumer) obj);
            }

            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
            }

            OfDouble(long size, DoubleSupplier s2) {
                super(size);
                this.s = s2;
            }

            public boolean tryAdvance(DoubleConsumer action) {
                action.getClass();
                action.accept(this.s.getAsDouble());
                return true;
            }

            public Spliterator.OfDouble trySplit() {
                if (this.estimate == 0) {
                    return null;
                }
                long j = this.estimate >>> 1;
                this.estimate = j;
                return new OfDouble(j, this.s);
            }
        }
    }

    static abstract class ArrayBuffer {
        int index;

        ArrayBuffer() {
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.index = 0;
        }

        static final class OfRef<T> extends ArrayBuffer implements Consumer<T> {
            final Object[] array;

            public /* synthetic */ Consumer andThen(Consumer consumer) {
                return Consumer.CC.$default$andThen(this, consumer);
            }

            OfRef(int size) {
                this.array = new Object[size];
            }

            public void accept(T t) {
                Object[] objArr = this.array;
                int i = this.index;
                this.index = i + 1;
                objArr[i] = t;
            }

            public void forEach(Consumer<? super T> consumer, long fence) {
                for (int i = 0; ((long) i) < fence; i++) {
                    consumer.accept(this.array[i]);
                }
            }
        }

        static abstract class OfPrimitive<T_CONS> extends ArrayBuffer {
            int index;

            /* access modifiers changed from: package-private */
            public abstract void forEach(T_CONS t_cons, long j);

            OfPrimitive() {
            }

            /* access modifiers changed from: package-private */
            public void reset() {
                this.index = 0;
            }
        }

        static final class OfInt extends OfPrimitive<IntConsumer> implements IntConsumer {
            final int[] array;

            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return IntConsumer.CC.$default$andThen(this, intConsumer);
            }

            OfInt(int size) {
                this.array = new int[size];
            }

            public void accept(int t) {
                int[] iArr = this.array;
                int i = this.index;
                this.index = i + 1;
                iArr[i] = t;
            }

            public void forEach(IntConsumer action, long fence) {
                for (int i = 0; ((long) i) < fence; i++) {
                    action.accept(this.array[i]);
                }
            }
        }

        static final class OfLong extends OfPrimitive<LongConsumer> implements LongConsumer {
            final long[] array;

            public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                return LongConsumer.CC.$default$andThen(this, longConsumer);
            }

            OfLong(int size) {
                this.array = new long[size];
            }

            public void accept(long t) {
                long[] jArr = this.array;
                int i = this.index;
                this.index = i + 1;
                jArr[i] = t;
            }

            public void forEach(LongConsumer action, long fence) {
                for (int i = 0; ((long) i) < fence; i++) {
                    action.accept(this.array[i]);
                }
            }
        }

        static final class OfDouble extends OfPrimitive<DoubleConsumer> implements DoubleConsumer {
            final double[] array;

            public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
            }

            OfDouble(int size) {
                this.array = new double[size];
            }

            public void accept(double t) {
                double[] dArr = this.array;
                int i = this.index;
                this.index = i + 1;
                dArr[i] = t;
            }

            /* access modifiers changed from: package-private */
            public void forEach(DoubleConsumer action, long fence) {
                for (int i = 0; ((long) i) < fence; i++) {
                    action.accept(this.array[i]);
                }
            }
        }
    }
}

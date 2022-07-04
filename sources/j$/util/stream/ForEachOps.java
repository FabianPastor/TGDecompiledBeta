package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.LongConsumer;
import j$.util.stream.Node;
import j$.util.stream.Sink;
import j$.util.stream.TerminalOp;
import java.util.concurrent.CountedCompleter;
import java.util.stream.ForEachOps;

final class ForEachOps {
    private ForEachOps() {
    }

    public static <T> TerminalOp<T, Void> makeRef(Consumer<? super T> consumer, boolean ordered) {
        consumer.getClass();
        return new ForEachOp.OfRef(consumer, ordered);
    }

    public static TerminalOp<Integer, Void> makeInt(IntConsumer action, boolean ordered) {
        action.getClass();
        return new ForEachOp.OfInt(action, ordered);
    }

    public static TerminalOp<Long, Void> makeLong(LongConsumer action, boolean ordered) {
        action.getClass();
        return new ForEachOp.OfLong(action, ordered);
    }

    public static TerminalOp<Double, Void> makeDouble(DoubleConsumer action, boolean ordered) {
        action.getClass();
        return new ForEachOp.OfDouble(action, ordered);
    }

    static abstract class ForEachOp<T> implements TerminalOp<T, Void>, TerminalSink<T, Void> {
        private final boolean ordered;

        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept((Sink) this, d);
        }

        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ void begin(long j) {
            Sink.CC.$default$begin(this, j);
        }

        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        public /* synthetic */ StreamShape inputShape() {
            return TerminalOp.CC.$default$inputShape(this);
        }

        protected ForEachOp(boolean ordered2) {
            this.ordered = ordered2;
        }

        public int getOpFlags() {
            if (this.ordered) {
                return 0;
            }
            return StreamOpFlag.NOT_ORDERED;
        }

        public <S> Void evaluateSequential(PipelineHelper<T> helper, Spliterator<S> spliterator) {
            return ((ForEachOp) helper.wrapAndCopyInto(this, spliterator)).get();
        }

        public <S> Void evaluateParallel(PipelineHelper<T> helper, Spliterator<S> spliterator) {
            if (this.ordered) {
                new ForEachOrderedTask(helper, spliterator, this).invoke();
                return null;
            }
            new ForEachTask(helper, spliterator, helper.wrapSink(this)).invoke();
            return null;
        }

        public Void get() {
            return null;
        }

        static final class OfRef<T> extends ForEachOp<T> {
            final Consumer<? super T> consumer;

            OfRef(Consumer<? super T> consumer2, boolean ordered) {
                super(ordered);
                this.consumer = consumer2;
            }

            public void accept(T t) {
                this.consumer.accept(t);
            }
        }

        static final class OfInt extends ForEachOp<Integer> implements Sink.OfInt {
            final IntConsumer consumer;

            public /* synthetic */ void accept(Integer num) {
                Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
            }

            public /* bridge */ /* synthetic */ void accept(Object obj) {
                accept((Integer) obj);
            }

            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return IntConsumer.CC.$default$andThen(this, intConsumer);
            }

            OfInt(IntConsumer consumer2, boolean ordered) {
                super(ordered);
                this.consumer = consumer2;
            }

            public StreamShape inputShape() {
                return StreamShape.INT_VALUE;
            }

            public void accept(int t) {
                this.consumer.accept(t);
            }
        }

        static final class OfLong extends ForEachOp<Long> implements Sink.OfLong {
            final LongConsumer consumer;

            public /* synthetic */ void accept(Long l) {
                Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
            }

            public /* bridge */ /* synthetic */ void accept(Object obj) {
                accept((Long) obj);
            }

            public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                return LongConsumer.CC.$default$andThen(this, longConsumer);
            }

            OfLong(LongConsumer consumer2, boolean ordered) {
                super(ordered);
                this.consumer = consumer2;
            }

            public StreamShape inputShape() {
                return StreamShape.LONG_VALUE;
            }

            public void accept(long t) {
                this.consumer.accept(t);
            }
        }

        static final class OfDouble extends ForEachOp<Double> implements Sink.OfDouble {
            final DoubleConsumer consumer;

            public /* synthetic */ void accept(Double d) {
                Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
            }

            public /* bridge */ /* synthetic */ void accept(Object obj) {
                accept((Double) obj);
            }

            public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
            }

            OfDouble(DoubleConsumer consumer2, boolean ordered) {
                super(ordered);
                this.consumer = consumer2;
            }

            public StreamShape inputShape() {
                return StreamShape.DOUBLE_VALUE;
            }

            public void accept(double t) {
                this.consumer.accept(t);
            }
        }
    }

    static final class ForEachTask<S, T> extends CountedCompleter<Void> {
        private final PipelineHelper<T> helper;
        private final Sink<S> sink;
        private Spliterator<S> spliterator;
        private long targetSize;

        ForEachTask(PipelineHelper<T> helper2, Spliterator<S> spliterator2, Sink<S> sink2) {
            super((CountedCompleter) null);
            this.sink = sink2;
            this.helper = helper2;
            this.spliterator = spliterator2;
            this.targetSize = 0;
        }

        ForEachTask(ForEachTask<S, T> parent, Spliterator<S> spliterator2) {
            super(parent);
            this.spliterator = spliterator2;
            this.sink = parent.sink;
            this.targetSize = parent.targetSize;
            this.helper = parent.helper;
        }

        public void compute() {
            ForEachTask forEachTask;
            Spliterator<S> spliterator2 = this.spliterator;
            long sizeEstimate = spliterator2.estimateSize();
            long j = this.targetSize;
            long sizeThreshold = j;
            if (j == 0) {
                long suggestTargetSize = AbstractTask.suggestTargetSize(sizeEstimate);
                sizeThreshold = suggestTargetSize;
                this.targetSize = suggestTargetSize;
            }
            boolean isShortCircuit = StreamOpFlag.SHORT_CIRCUIT.isKnown(this.helper.getStreamAndOpFlags());
            boolean forkRight = false;
            java.util.stream.Sink<S> taskSink = this.sink;
            ForEachTask forEachTask2 = this;
            while (true) {
                if (!isShortCircuit || !taskSink.cancellationRequested()) {
                    if (sizeEstimate <= sizeThreshold) {
                        break;
                    }
                    Spliterator<S> trySplit = spliterator2.trySplit();
                    Spliterator<S> spliterator3 = trySplit;
                    if (trySplit == null) {
                        break;
                    }
                    ForEachTask forEachTask3 = new ForEachTask(forEachTask2, spliterator3);
                    forEachTask2.addToPendingCount(1);
                    if (forkRight) {
                        forkRight = false;
                        spliterator2 = spliterator3;
                        forEachTask = forEachTask2;
                        forEachTask2 = forEachTask3;
                    } else {
                        forkRight = true;
                        forEachTask = forEachTask3;
                    }
                    forEachTask.fork();
                    sizeEstimate = spliterator2.estimateSize();
                } else {
                    break;
                }
            }
            forEachTask2.helper.copyInto(taskSink, spliterator2);
            forEachTask2.spliterator = null;
            forEachTask2.propagateCompletion();
        }
    }

    static final class ForEachOrderedTask<S, T> extends CountedCompleter<Void> {
        private final Sink<T> action;
        private final ConcurrentHashMap<ForEachOrderedTask<S, T>, ForEachOrderedTask<S, T>> completionMap;
        private final PipelineHelper<T> helper;
        private final ForEachOrderedTask<S, T> leftPredecessor;
        private Node<T> node;
        private Spliterator<S> spliterator;
        private final long targetSize;

        protected ForEachOrderedTask(PipelineHelper<T> helper2, Spliterator<S> spliterator2, Sink<T> action2) {
            super((CountedCompleter) null);
            this.helper = helper2;
            this.spliterator = spliterator2;
            this.targetSize = AbstractTask.suggestTargetSize(spliterator2.estimateSize());
            this.completionMap = new ConcurrentHashMap<>(Math.max(16, AbstractTask.LEAF_TARGET << 1));
            this.action = action2;
            this.leftPredecessor = null;
        }

        ForEachOrderedTask(ForEachOrderedTask<S, T> parent, Spliterator<S> spliterator2, ForEachOrderedTask<S, T> leftPredecessor2) {
            super(parent);
            this.helper = parent.helper;
            this.spliterator = spliterator2;
            this.targetSize = parent.targetSize;
            this.completionMap = parent.completionMap;
            this.action = parent.action;
            this.leftPredecessor = leftPredecessor2;
        }

        public final void compute() {
            doCompute(this);
        }

        private static <S, T> void doCompute(ForEachOps.ForEachOrderedTask<S, T> task) {
            ForEachOps.ForEachOrderedTask<S, T> taskToFork;
            Spliterator<S> spliterator2 = task.spliterator;
            long sizeThreshold = task.targetSize;
            boolean forkRight = false;
            while (spliterator2.estimateSize() > sizeThreshold) {
                Spliterator<S> trySplit = spliterator2.trySplit();
                Spliterator<S> spliterator3 = trySplit;
                if (trySplit == null) {
                    break;
                }
                ForEachOps.ForEachOrderedTask<S, T> leftChild = new ForEachOrderedTask<>(task, spliterator3, (ForEachOps.ForEachOrderedTask<S, T>) task.leftPredecessor);
                ForEachOps.ForEachOrderedTask<S, T> rightChild = new ForEachOrderedTask<>(task, spliterator2, leftChild);
                task.addToPendingCount(1);
                rightChild.addToPendingCount(1);
                task.completionMap.put(leftChild, rightChild);
                if (task.leftPredecessor != null) {
                    leftChild.addToPendingCount(1);
                    if (task.completionMap.replace(task.leftPredecessor, task, leftChild)) {
                        task.addToPendingCount(-1);
                    } else {
                        leftChild.addToPendingCount(-1);
                    }
                }
                if (forkRight) {
                    forkRight = false;
                    spliterator2 = spliterator3;
                    task = leftChild;
                    taskToFork = rightChild;
                } else {
                    forkRight = true;
                    task = rightChild;
                    taskToFork = leftChild;
                }
                taskToFork.fork();
            }
            if (task.getPendingCount() > 0) {
                ForEachOps$ForEachOrderedTask$$ExternalSyntheticLambda0 forEachOps$ForEachOrderedTask$$ExternalSyntheticLambda0 = ForEachOps$ForEachOrderedTask$$ExternalSyntheticLambda0.INSTANCE;
                PipelineHelper<T> pipelineHelper = task.helper;
                task.node = ((Node.Builder) task.helper.wrapAndCopyInto(pipelineHelper.makeNodeBuilder(pipelineHelper.exactOutputSizeIfKnown(spliterator2), forEachOps$ForEachOrderedTask$$ExternalSyntheticLambda0), spliterator2)).build();
                task.spliterator = null;
            }
            task.tryComplete();
        }

        static /* synthetic */ Object[] lambda$doCompute$0(int size) {
            return new Object[size];
        }

        public void onCompletion(CountedCompleter<?> countedCompleter) {
            Node<T> node2 = this.node;
            if (node2 != null) {
                node2.forEach(this.action);
                this.node = null;
            } else {
                Spliterator<S> spliterator2 = this.spliterator;
                if (spliterator2 != null) {
                    this.helper.wrapAndCopyInto(this.action, spliterator2);
                    this.spliterator = null;
                }
            }
            ForEachOps.ForEachOrderedTask<S, T> leftDescendant = (ForEachOrderedTask) this.completionMap.remove(this);
            if (leftDescendant != null) {
                leftDescendant.tryComplete();
            }
        }
    }
}

package j$.util;

public interface F extends Spliterator {
    void forEachRemaining(Object obj);

    boolean tryAdvance(Object obj);

    F trySplit();
}

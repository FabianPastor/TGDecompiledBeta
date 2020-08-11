package j$.util;

public interface V extends Spliterator {
    void forEachRemaining(Object obj);

    boolean tryAdvance(Object obj);

    V trySplit();
}

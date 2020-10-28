package j$.util;

import java.util.Iterator;

public interface A extends Iterator, Iterator {
    void forEachRemaining(Object obj);
}

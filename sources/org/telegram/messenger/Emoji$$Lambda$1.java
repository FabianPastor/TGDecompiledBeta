package org.telegram.messenger;

import java.util.Comparator;

final /* synthetic */ class Emoji$$Lambda$1 implements Comparator {
    static final Comparator $instance = new Emoji$$Lambda$1();

    private Emoji$$Lambda$1() {
    }

    public int compare(Object obj, Object obj2) {
        return Emoji.lambda$sortEmoji$1$Emoji((String) obj, (String) obj2);
    }
}

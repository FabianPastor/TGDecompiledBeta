package org.telegram.ui;

import java.io.File;
import java.util.Comparator;

final /* synthetic */ class DocumentSelectActivity$$Lambda$4 implements Comparator {
    static final Comparator $instance = new DocumentSelectActivity$$Lambda$4();

    private DocumentSelectActivity$$Lambda$4() {
    }

    public int compare(Object obj, Object obj2) {
        return DocumentSelectActivity.lambda$listFiles$4$DocumentSelectActivity((File) obj, (File) obj2);
    }
}

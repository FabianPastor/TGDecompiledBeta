package org.telegram.p005ui;

import java.io.File;
import java.util.Comparator;

/* renamed from: org.telegram.ui.DocumentSelectActivity$$Lambda$4 */
final /* synthetic */ class DocumentSelectActivity$$Lambda$4 implements Comparator {
    static final Comparator $instance = new DocumentSelectActivity$$Lambda$4();

    private DocumentSelectActivity$$Lambda$4() {
    }

    public int compare(Object obj, Object obj2) {
        return DocumentSelectActivity.lambda$listFiles$4$DocumentSelectActivity((File) obj, (File) obj2);
    }
}

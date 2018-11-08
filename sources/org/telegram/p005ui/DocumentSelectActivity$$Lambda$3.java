package org.telegram.p005ui;

import java.util.Comparator;
import org.telegram.p005ui.DocumentSelectActivity.ListItem;

/* renamed from: org.telegram.ui.DocumentSelectActivity$$Lambda$3 */
final /* synthetic */ class DocumentSelectActivity$$Lambda$3 implements Comparator {
    static final Comparator $instance = new DocumentSelectActivity$$Lambda$3();

    private DocumentSelectActivity$$Lambda$3() {
    }

    public int compare(Object obj, Object obj2) {
        return DocumentSelectActivity.lambda$loadRecentFiles$3$DocumentSelectActivity((ListItem) obj, (ListItem) obj2);
    }
}

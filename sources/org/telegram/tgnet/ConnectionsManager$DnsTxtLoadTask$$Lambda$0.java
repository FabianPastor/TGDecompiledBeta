package org.telegram.tgnet;

import java.util.Comparator;

final /* synthetic */ class ConnectionsManager$DnsTxtLoadTask$$Lambda$0 implements Comparator {
    static final Comparator $instance = new ConnectionsManager$DnsTxtLoadTask$$Lambda$0();

    private ConnectionsManager$DnsTxtLoadTask$$Lambda$0() {
    }

    public int compare(Object obj, Object obj2) {
        return DnsTxtLoadTask.lambda$doInBackground$0$ConnectionsManager$DnsTxtLoadTask((String) obj, (String) obj2);
    }
}

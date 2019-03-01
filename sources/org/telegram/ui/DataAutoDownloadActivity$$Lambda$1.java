package org.telegram.ui;

import java.util.Comparator;
import org.telegram.messenger.DownloadController.Preset;

final /* synthetic */ class DataAutoDownloadActivity$$Lambda$1 implements Comparator {
    static final Comparator $instance = new DataAutoDownloadActivity$$Lambda$1();

    private DataAutoDownloadActivity$$Lambda$1() {
    }

    public int compare(Object obj, Object obj2) {
        return DataAutoDownloadActivity.lambda$fillPresets$5$DataAutoDownloadActivity((Preset) obj, (Preset) obj2);
    }
}

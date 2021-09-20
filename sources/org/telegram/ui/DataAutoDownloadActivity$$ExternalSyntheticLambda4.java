package org.telegram.ui;

import java.util.Comparator;
import org.telegram.messenger.DownloadController;

public final /* synthetic */ class DataAutoDownloadActivity$$ExternalSyntheticLambda4 implements Comparator {
    public static final /* synthetic */ DataAutoDownloadActivity$$ExternalSyntheticLambda4 INSTANCE = new DataAutoDownloadActivity$$ExternalSyntheticLambda4();

    private /* synthetic */ DataAutoDownloadActivity$$ExternalSyntheticLambda4() {
    }

    public final int compare(Object obj, Object obj2) {
        return DataAutoDownloadActivity.lambda$fillPresets$5((DownloadController.Preset) obj, (DownloadController.Preset) obj2);
    }
}

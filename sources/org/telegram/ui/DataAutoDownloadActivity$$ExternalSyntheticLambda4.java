package org.telegram.ui;

import java.util.Comparator;
import org.telegram.messenger.DownloadController;
/* loaded from: classes3.dex */
public final /* synthetic */ class DataAutoDownloadActivity$$ExternalSyntheticLambda4 implements Comparator {
    public static final /* synthetic */ DataAutoDownloadActivity$$ExternalSyntheticLambda4 INSTANCE = new DataAutoDownloadActivity$$ExternalSyntheticLambda4();

    private /* synthetic */ DataAutoDownloadActivity$$ExternalSyntheticLambda4() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$fillPresets$5;
        lambda$fillPresets$5 = DataAutoDownloadActivity.lambda$fillPresets$5((DownloadController.Preset) obj, (DownloadController.Preset) obj2);
        return lambda$fillPresets$5;
    }
}

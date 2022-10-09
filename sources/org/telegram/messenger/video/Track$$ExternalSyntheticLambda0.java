package org.telegram.messenger.video;

import java.util.Comparator;
import org.telegram.messenger.video.Track;
/* loaded from: classes.dex */
public final /* synthetic */ class Track$$ExternalSyntheticLambda0 implements Comparator {
    public static final /* synthetic */ Track$$ExternalSyntheticLambda0 INSTANCE = new Track$$ExternalSyntheticLambda0();

    private /* synthetic */ Track$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$prepare$0;
        lambda$prepare$0 = Track.lambda$prepare$0((Track.SamplePresentationTime) obj, (Track.SamplePresentationTime) obj2);
        return lambda$prepare$0;
    }
}

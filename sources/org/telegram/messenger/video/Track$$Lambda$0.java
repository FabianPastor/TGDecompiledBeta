package org.telegram.messenger.video;

import java.util.Comparator;

final /* synthetic */ class Track$$Lambda$0 implements Comparator {
    static final Comparator $instance = new Track$$Lambda$0();

    private Track$$Lambda$0() {
    }

    public int compare(Object obj, Object obj2) {
        return Track.lambda$prepare$0$Track((SamplePresentationTime) obj, (SamplePresentationTime) obj2);
    }
}

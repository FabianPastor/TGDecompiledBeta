package org.telegram.messenger.exoplayer.text.eia608;

final class ClosedCaptionList implements Comparable<ClosedCaptionList> {
    public final ClosedCaption[] captions;
    public final boolean decodeOnly;
    public final long timeUs;

    public ClosedCaptionList(long timeUs, boolean decodeOnly, ClosedCaption[] captions) {
        this.timeUs = timeUs;
        this.decodeOnly = decodeOnly;
        this.captions = captions;
    }

    public int compareTo(ClosedCaptionList other) {
        long delta = this.timeUs - other.timeUs;
        if (delta == 0) {
            return 0;
        }
        return delta > 0 ? 1 : -1;
    }
}

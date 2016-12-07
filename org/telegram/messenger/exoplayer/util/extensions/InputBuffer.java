package org.telegram.messenger.exoplayer.util.extensions;

import org.telegram.messenger.exoplayer.SampleHolder;

public class InputBuffer extends Buffer {
    public final SampleHolder sampleHolder = new SampleHolder(2);

    public void reset() {
        super.reset();
        this.sampleHolder.clearData();
    }
}

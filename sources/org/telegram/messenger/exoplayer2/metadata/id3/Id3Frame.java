package org.telegram.messenger.exoplayer2.metadata.id3;

import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.telegram.messenger.exoplayer2.util.Assertions;

public abstract class Id3Frame implements Entry {
    public final String id;

    public int describeContents() {
        return 0;
    }

    public Id3Frame(String str) {
        this.id = (String) Assertions.checkNotNull(str);
    }
}

package org.telegram.messenger.exoplayer2.metadata.id3;

public final class TxxxFrame extends Id3Frame {
    public static final String ID = "TXXX";
    public final String description;
    public final String value;

    public TxxxFrame(String description, String value) {
        super(ID);
        this.description = description;
        this.value = value;
    }
}

package org.telegram.messenger.exoplayer2.metadata.id3;

public final class PrivFrame extends Id3Frame {
    public static final String ID = "PRIV";
    public final String owner;
    public final byte[] privateData;

    public PrivFrame(String owner, byte[] privateData) {
        super(ID);
        this.owner = owner;
        this.privateData = privateData;
    }
}

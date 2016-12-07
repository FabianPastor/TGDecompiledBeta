package org.telegram.messenger.exoplayer.extractor;

public interface SeekMap {
    public static final SeekMap UNSEEKABLE = new SeekMap() {
        public boolean isSeekable() {
            return false;
        }

        public long getPosition(long timeUs) {
            return 0;
        }
    };

    long getPosition(long j);

    boolean isSeekable();
}

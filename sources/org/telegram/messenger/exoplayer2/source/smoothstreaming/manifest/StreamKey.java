package org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest;

public final class StreamKey implements Comparable<StreamKey> {
    public final int streamElementIndex;
    public final int trackIndex;

    public StreamKey(int streamElementIndex, int trackIndex) {
        this.streamElementIndex = streamElementIndex;
        this.trackIndex = trackIndex;
    }

    public String toString() {
        return this.streamElementIndex + "." + this.trackIndex;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StreamKey that = (StreamKey) o;
        if (this.streamElementIndex == that.streamElementIndex && this.trackIndex == that.trackIndex) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (this.streamElementIndex * 31) + this.trackIndex;
    }

    public int compareTo(StreamKey o) {
        int result = this.streamElementIndex - o.streamElementIndex;
        if (result == 0) {
            return this.trackIndex - o.trackIndex;
        }
        return result;
    }
}

package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.util.Util;

public final class ChapterTocFrame extends Id3Frame {
    public static final Creator<ChapterTocFrame> CREATOR = new C05791();
    public static final String ID = "CTOC";
    public final String[] children;
    public final String elementId;
    public final boolean isOrdered;
    public final boolean isRoot;
    private final Id3Frame[] subFrames;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.id3.ChapterTocFrame$1 */
    static class C05791 implements Creator<ChapterTocFrame> {
        C05791() {
        }

        public ChapterTocFrame createFromParcel(Parcel in) {
            return new ChapterTocFrame(in);
        }

        public ChapterTocFrame[] newArray(int size) {
            return new ChapterTocFrame[size];
        }
    }

    public ChapterTocFrame(String elementId, boolean isRoot, boolean isOrdered, String[] children, Id3Frame[] subFrames) {
        super(ID);
        this.elementId = elementId;
        this.isRoot = isRoot;
        this.isOrdered = isOrdered;
        this.children = children;
        this.subFrames = subFrames;
    }

    ChapterTocFrame(Parcel in) {
        super(ID);
        this.elementId = in.readString();
        int i = 0;
        boolean z = true;
        this.isRoot = in.readByte() != (byte) 0;
        if (in.readByte() == (byte) 0) {
            z = false;
        }
        this.isOrdered = z;
        this.children = in.createStringArray();
        int subFrameCount = in.readInt();
        this.subFrames = new Id3Frame[subFrameCount];
        while (i < subFrameCount) {
            this.subFrames[i] = (Id3Frame) in.readParcelable(Id3Frame.class.getClassLoader());
            i++;
        }
    }

    public int getSubFrameCount() {
        return this.subFrames.length;
    }

    public Id3Frame getSubFrame(int index) {
        return this.subFrames[index];
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                ChapterTocFrame other = (ChapterTocFrame) obj;
                if (this.isRoot != other.isRoot || this.isOrdered != other.isOrdered || !Util.areEqual(this.elementId, other.elementId) || !Arrays.equals(this.children, other.children) || !Arrays.equals(this.subFrames, other.subFrames)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (31 * ((31 * ((31 * 17) + this.isRoot)) + this.isOrdered)) + (this.elementId != null ? this.elementId.hashCode() : 0);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.elementId);
        dest.writeByte((byte) this.isRoot);
        dest.writeByte((byte) this.isOrdered);
        dest.writeStringArray(this.children);
        dest.writeInt(this.subFrames.length);
        for (Id3Frame subFrame : this.subFrames) {
            dest.writeParcelable(subFrame, 0);
        }
    }
}

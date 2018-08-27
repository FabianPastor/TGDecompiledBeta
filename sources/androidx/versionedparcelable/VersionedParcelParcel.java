package androidx.versionedparcelable;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseIntArray;

class VersionedParcelParcel extends VersionedParcel {
    private int mCurrentField;
    private final int mEnd;
    private int mNextRead;
    private final int mOffset;
    private final Parcel mParcel;
    private final SparseIntArray mPositionLookup;
    private final String mPrefix;

    VersionedParcelParcel(Parcel p) {
        this(p, p.dataPosition(), p.dataSize(), TtmlNode.ANONYMOUS_REGION_ID);
    }

    VersionedParcelParcel(Parcel p, int offset, int end, String prefix) {
        this.mPositionLookup = new SparseIntArray();
        this.mCurrentField = -1;
        this.mNextRead = 0;
        this.mParcel = p;
        this.mOffset = offset;
        this.mEnd = end;
        this.mNextRead = this.mOffset;
        this.mPrefix = prefix;
    }

    private int readUntilField(int fieldId) {
        while (this.mNextRead < this.mEnd) {
            this.mParcel.setDataPosition(this.mNextRead);
            int size = this.mParcel.readInt();
            int fid = this.mParcel.readInt();
            this.mNextRead += size;
            if (fid == fieldId) {
                return this.mParcel.dataPosition();
            }
        }
        return -1;
    }

    public boolean readField(int fieldId) {
        int position = readUntilField(fieldId);
        if (position == -1) {
            return false;
        }
        this.mParcel.setDataPosition(position);
        return true;
    }

    public void setOutputField(int fieldId) {
        closeField();
        this.mCurrentField = fieldId;
        this.mPositionLookup.put(fieldId, this.mParcel.dataPosition());
        writeInt(0);
        writeInt(fieldId);
    }

    public void closeField() {
        if (this.mCurrentField >= 0) {
            int currentFieldPosition = this.mPositionLookup.get(this.mCurrentField);
            int position = this.mParcel.dataPosition();
            int size = position - currentFieldPosition;
            this.mParcel.setDataPosition(currentFieldPosition);
            this.mParcel.writeInt(size);
            this.mParcel.setDataPosition(position);
        }
    }

    protected VersionedParcel createSubParcel() {
        return new VersionedParcelParcel(this.mParcel, this.mParcel.dataPosition(), this.mNextRead == this.mOffset ? this.mEnd : this.mNextRead, this.mPrefix + "  ");
    }

    public void writeByteArray(byte[] b) {
        if (b != null) {
            this.mParcel.writeInt(b.length);
            this.mParcel.writeByteArray(b);
            return;
        }
        this.mParcel.writeInt(-1);
    }

    public void writeInt(int val) {
        this.mParcel.writeInt(val);
    }

    public void writeString(String val) {
        this.mParcel.writeString(val);
    }

    public void writeParcelable(Parcelable p) {
        this.mParcel.writeParcelable(p, 0);
    }

    public int readInt() {
        return this.mParcel.readInt();
    }

    public String readString() {
        return this.mParcel.readString();
    }

    public byte[] readByteArray() {
        int len = this.mParcel.readInt();
        if (len < 0) {
            return null;
        }
        byte[] bytes = new byte[len];
        this.mParcel.readByteArray(bytes);
        return bytes;
    }

    public <T extends Parcelable> T readParcelable() {
        return this.mParcel.readParcelable(getClass().getClassLoader());
    }
}

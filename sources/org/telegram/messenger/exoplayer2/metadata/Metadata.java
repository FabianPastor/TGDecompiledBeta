package org.telegram.messenger.exoplayer2.metadata;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class Metadata implements Parcelable {
    public static final Creator<Metadata> CREATOR = new C05771();
    private final Entry[] entries;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.Metadata$1 */
    static class C05771 implements Creator<Metadata> {
        C05771() {
        }

        public Metadata createFromParcel(Parcel parcel) {
            return new Metadata(parcel);
        }

        public Metadata[] newArray(int i) {
            return new Metadata[0];
        }
    }

    public interface Entry extends Parcelable {
    }

    public int describeContents() {
        return 0;
    }

    public Metadata(Entry... entryArr) {
        if (entryArr == null) {
            entryArr = new Entry[null];
        }
        this.entries = entryArr;
    }

    public Metadata(List<? extends Entry> list) {
        if (list != null) {
            this.entries = new Entry[list.size()];
            list.toArray(this.entries);
            return;
        }
        this.entries = new Entry[null];
    }

    Metadata(Parcel parcel) {
        this.entries = new Entry[parcel.readInt()];
        for (int i = 0; i < this.entries.length; i++) {
            this.entries[i] = (Entry) parcel.readParcelable(Entry.class.getClassLoader());
        }
    }

    public int length() {
        return this.entries.length;
    }

    public Entry get(int i) {
        return this.entries[i];
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                return Arrays.equals(this.entries, ((Metadata) obj).entries);
            }
        }
        return null;
    }

    public int hashCode() {
        return Arrays.hashCode(this.entries);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.entries.length);
        for (Parcelable writeParcelable : this.entries) {
            parcel.writeParcelable(writeParcelable, 0);
        }
    }
}

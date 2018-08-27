package androidx.versionedparcelable;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ParcelImpl implements Parcelable {
    public static final Creator<ParcelImpl> CREATOR = new C00101();
    private final VersionedParcelable mParcel;

    /* renamed from: androidx.versionedparcelable.ParcelImpl$1 */
    static class C00101 implements Creator<ParcelImpl> {
        C00101() {
        }

        public ParcelImpl createFromParcel(Parcel in) {
            return new ParcelImpl(in);
        }

        public ParcelImpl[] newArray(int size) {
            return new ParcelImpl[size];
        }
    }

    protected ParcelImpl(Parcel in) {
        this.mParcel = new VersionedParcelParcel(in).readVersionedParcelable();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        new VersionedParcelParcel(dest).writeVersionedParcelable(this.mParcel);
    }
}

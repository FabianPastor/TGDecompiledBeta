package org.telegram.messenger.exoplayer2.drm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DrmInitData implements Parcelable, Comparator<SchemeData> {
    public static final Creator<DrmInitData> CREATOR = new C05671();
    private int hashCode;
    public final int schemeDataCount;
    private final SchemeData[] schemeDatas;
    public final String schemeType;

    /* renamed from: org.telegram.messenger.exoplayer2.drm.DrmInitData$1 */
    static class C05671 implements Creator<DrmInitData> {
        C05671() {
        }

        public DrmInitData createFromParcel(Parcel parcel) {
            return new DrmInitData(parcel);
        }

        public DrmInitData[] newArray(int i) {
            return new DrmInitData[i];
        }
    }

    public static final class SchemeData implements Parcelable {
        public static final Creator<SchemeData> CREATOR = new C05681();
        public final byte[] data;
        private int hashCode;
        public final String mimeType;
        public final boolean requiresSecureDecryption;
        private final UUID uuid;

        /* renamed from: org.telegram.messenger.exoplayer2.drm.DrmInitData$SchemeData$1 */
        static class C05681 implements Creator<SchemeData> {
            C05681() {
            }

            public SchemeData createFromParcel(Parcel parcel) {
                return new SchemeData(parcel);
            }

            public SchemeData[] newArray(int i) {
                return new SchemeData[i];
            }
        }

        public int describeContents() {
            return 0;
        }

        public SchemeData(UUID uuid, String str, byte[] bArr) {
            this(uuid, str, bArr, false);
        }

        public SchemeData(UUID uuid, String str, byte[] bArr, boolean z) {
            this.uuid = (UUID) Assertions.checkNotNull(uuid);
            this.mimeType = (String) Assertions.checkNotNull(str);
            this.data = bArr;
            this.requiresSecureDecryption = z;
        }

        SchemeData(Parcel parcel) {
            this.uuid = new UUID(parcel.readLong(), parcel.readLong());
            this.mimeType = parcel.readString();
            this.data = parcel.createByteArray();
            this.requiresSecureDecryption = parcel.readByte() != null ? true : null;
        }

        public boolean matches(UUID uuid) {
            if (!C0542C.UUID_NIL.equals(this.uuid)) {
                if (uuid.equals(this.uuid) == null) {
                    return null;
                }
            }
            return true;
        }

        public boolean canReplace(SchemeData schemeData) {
            return (!hasData() || schemeData.hasData() || matches(schemeData.uuid) == null) ? null : true;
        }

        public boolean hasData() {
            return this.data != null;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof SchemeData)) {
                return false;
            }
            boolean z = true;
            if (obj == this) {
                return true;
            }
            SchemeData schemeData = (SchemeData) obj;
            if (!this.mimeType.equals(schemeData.mimeType) || !Util.areEqual(this.uuid, schemeData.uuid) || Arrays.equals(this.data, schemeData.data) == null) {
                z = false;
            }
            return z;
        }

        public int hashCode() {
            if (this.hashCode == 0) {
                this.hashCode = (31 * ((this.uuid.hashCode() * 31) + this.mimeType.hashCode())) + Arrays.hashCode(this.data);
            }
            return this.hashCode;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(this.uuid.getMostSignificantBits());
            parcel.writeLong(this.uuid.getLeastSignificantBits());
            parcel.writeString(this.mimeType);
            parcel.writeByteArray(this.data);
            parcel.writeByte((byte) this.requiresSecureDecryption);
        }
    }

    public int describeContents() {
        return 0;
    }

    public static DrmInitData createSessionCreationData(DrmInitData drmInitData, DrmInitData drmInitData2) {
        String str;
        int length;
        List arrayList = new ArrayList();
        int i = 0;
        if (drmInitData != null) {
            str = drmInitData.schemeType;
            for (SchemeData schemeData : drmInitData.schemeDatas) {
                if (schemeData.hasData()) {
                    arrayList.add(schemeData);
                }
            }
        } else {
            str = null;
        }
        if (drmInitData2 != null) {
            if (str == null) {
                str = drmInitData2.schemeType;
            }
            drmInitData = arrayList.size();
            drmInitData2 = drmInitData2.schemeDatas;
            length = drmInitData2.length;
            while (i < length) {
                SchemeData schemeData2 = drmInitData2[i];
                if (schemeData2.hasData() && !containsSchemeDataWithUuid(arrayList, drmInitData, schemeData2.uuid)) {
                    arrayList.add(schemeData2);
                }
                i++;
            }
        }
        if (arrayList.isEmpty() != null) {
            return null;
        }
        return new DrmInitData(str, arrayList);
    }

    public DrmInitData(List<SchemeData> list) {
        this(null, false, (SchemeData[]) list.toArray(new SchemeData[list.size()]));
    }

    public DrmInitData(String str, List<SchemeData> list) {
        this(str, false, (SchemeData[]) list.toArray(new SchemeData[list.size()]));
    }

    public DrmInitData(SchemeData... schemeDataArr) {
        this(null, schemeDataArr);
    }

    public DrmInitData(String str, SchemeData... schemeDataArr) {
        this(str, true, schemeDataArr);
    }

    private DrmInitData(String str, boolean z, SchemeData... schemeDataArr) {
        this.schemeType = str;
        if (z) {
            schemeDataArr = (SchemeData[]) schemeDataArr.clone();
        }
        Arrays.sort(schemeDataArr, this);
        this.schemeDatas = schemeDataArr;
        this.schemeDataCount = schemeDataArr.length;
    }

    DrmInitData(Parcel parcel) {
        this.schemeType = parcel.readString();
        this.schemeDatas = (SchemeData[]) parcel.createTypedArray(SchemeData.CREATOR);
        this.schemeDataCount = this.schemeDatas.length;
    }

    @Deprecated
    public SchemeData get(UUID uuid) {
        for (SchemeData schemeData : this.schemeDatas) {
            if (schemeData.matches(uuid)) {
                return schemeData;
            }
        }
        return null;
    }

    public SchemeData get(int i) {
        return this.schemeDatas[i];
    }

    public DrmInitData copyWithSchemeType(String str) {
        if (Util.areEqual(this.schemeType, str)) {
            return this;
        }
        return new DrmInitData(str, false, this.schemeDatas);
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = (31 * (this.schemeType == null ? 0 : this.schemeType.hashCode())) + Arrays.hashCode(this.schemeDatas);
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                DrmInitData drmInitData = (DrmInitData) obj;
                if (!Util.areEqual(this.schemeType, drmInitData.schemeType) || Arrays.equals(this.schemeDatas, drmInitData.schemeDatas) == null) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int compare(SchemeData schemeData, SchemeData schemeData2) {
        if (C0542C.UUID_NIL.equals(schemeData.uuid)) {
            return C0542C.UUID_NIL.equals(schemeData2.uuid) != null ? null : 1;
        } else {
            return schemeData.uuid.compareTo(schemeData2.uuid);
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.schemeType);
        parcel.writeTypedArray(this.schemeDatas, 0);
    }

    private static boolean containsSchemeDataWithUuid(ArrayList<SchemeData> arrayList, int i, UUID uuid) {
        for (int i2 = 0; i2 < i; i2++) {
            if (((SchemeData) arrayList.get(i2)).uuid.equals(uuid)) {
                return true;
            }
        }
        return false;
    }
}

package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$GeoPoint extends TLObject {
    public double _long;
    public long access_hash;
    public int accuracy_radius;
    public int flags;
    public double lat;

    public static TLRPC$GeoPoint TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$GeoPoint tLRPC$TL_geoPoint;
        switch (i) {
            case -1297942941:
                tLRPC$TL_geoPoint = new TLRPC$TL_geoPoint();
                break;
            case 43446532:
                tLRPC$TL_geoPoint = new TLRPC$TL_geoPoint() { // from class: org.telegram.tgnet.TLRPC$TL_geoPoint_layer119
                    public static int constructor = 43446532;

                    @Override // org.telegram.tgnet.TLRPC$TL_geoPoint, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this._long = abstractSerializedData2.readDouble(z2);
                        this.lat = abstractSerializedData2.readDouble(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_geoPoint, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeDouble(this._long);
                        abstractSerializedData2.writeDouble(this.lat);
                        abstractSerializedData2.writeInt64(this.access_hash);
                    }
                };
                break;
            case 286776671:
                tLRPC$TL_geoPoint = new TLRPC$GeoPoint() { // from class: org.telegram.tgnet.TLRPC$TL_geoPointEmpty
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 541710092:
                tLRPC$TL_geoPoint = new TLRPC$TL_geoPoint() { // from class: org.telegram.tgnet.TLRPC$TL_geoPoint_layer81
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_geoPoint, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this._long = abstractSerializedData2.readDouble(z2);
                        this.lat = abstractSerializedData2.readDouble(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_geoPoint, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeDouble(this._long);
                        abstractSerializedData2.writeDouble(this.lat);
                    }
                };
                break;
            default:
                tLRPC$TL_geoPoint = null;
                break;
        }
        if (tLRPC$TL_geoPoint != null || !z) {
            if (tLRPC$TL_geoPoint != null) {
                tLRPC$TL_geoPoint.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_geoPoint;
        }
        throw new RuntimeException(String.format("can't parse magic %x in GeoPoint", Integer.valueOf(i)));
    }
}

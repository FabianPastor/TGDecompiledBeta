package org.telegram.tgnet;

public abstract class TLRPC$GeoPoint extends TLObject {
    public double _long;
    public long access_hash;
    public int accuracy_radius;
    public int flags;
    public double lat;

    public static TLRPC$GeoPoint TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$GeoPoint tLRPC$GeoPoint;
        switch (i) {
            case -1297942941:
                tLRPC$GeoPoint = new TLRPC$TL_geoPoint();
                break;
            case 43446532:
                tLRPC$GeoPoint = new TLRPC$TL_geoPoint_layer119();
                break;
            case 286776671:
                tLRPC$GeoPoint = new TLRPC$TL_geoPointEmpty();
                break;
            case 541710092:
                tLRPC$GeoPoint = new TLRPC$TL_geoPoint_layer81();
                break;
            default:
                tLRPC$GeoPoint = null;
                break;
        }
        if (tLRPC$GeoPoint != null || !z) {
            if (tLRPC$GeoPoint != null) {
                tLRPC$GeoPoint.readParams(abstractSerializedData, z);
            }
            return tLRPC$GeoPoint;
        }
        throw new RuntimeException(String.format("can't parse magic %x in GeoPoint", new Object[]{Integer.valueOf(i)}));
    }
}

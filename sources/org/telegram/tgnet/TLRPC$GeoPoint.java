package org.telegram.tgnet;

public abstract class TLRPC$GeoPoint extends TLObject {
    public double _long;
    public long access_hash;
    public double lat;

    public static TLRPC$GeoPoint TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$GeoPoint tLRPC$GeoPoint;
        if (i != 43446532) {
            tLRPC$GeoPoint = i != NUM ? i != NUM ? null : new TLRPC$TL_geoPoint_layer81() : new TLRPC$TL_geoPointEmpty();
        } else {
            tLRPC$GeoPoint = new TLRPC$TL_geoPoint();
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

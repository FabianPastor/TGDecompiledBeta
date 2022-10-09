package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$InputGeoPoint extends TLObject {
    public double _long;
    public int accuracy_radius;
    public int flags;
    public double lat;

    public static TLRPC$InputGeoPoint TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputGeoPoint tLRPC$TL_inputGeoPointEmpty;
        if (i != -NUM) {
            tLRPC$TL_inputGeoPointEmpty = i != NUM ? null : new TLRPC$TL_inputGeoPoint();
        } else {
            tLRPC$TL_inputGeoPointEmpty = new TLRPC$TL_inputGeoPointEmpty();
        }
        if (tLRPC$TL_inputGeoPointEmpty != null || !z) {
            if (tLRPC$TL_inputGeoPointEmpty != null) {
                tLRPC$TL_inputGeoPointEmpty.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_inputGeoPointEmpty;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputGeoPoint", Integer.valueOf(i)));
    }
}

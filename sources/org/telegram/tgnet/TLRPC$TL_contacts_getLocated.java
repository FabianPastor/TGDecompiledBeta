package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_contacts_getLocated extends TLObject {
    public static int constructor = -NUM;
    public boolean background;
    public int flags;
    public TLRPC$InputGeoPoint geo_point;
    public int self_expires;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.background ? this.flags | 2 : this.flags & (-3);
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.geo_point.serializeToStream(abstractSerializedData);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.self_expires);
        }
    }
}

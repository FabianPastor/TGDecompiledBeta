package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$InputGame extends TLObject {
    public long access_hash;
    public TLRPC$InputUser bot_id;
    public long id;
    public String short_name;

    public static TLRPC$InputGame TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputGame tLRPC$TL_inputGameShortName;
        if (i == -NUM) {
            tLRPC$TL_inputGameShortName = new TLRPC$TL_inputGameShortName();
        } else {
            tLRPC$TL_inputGameShortName = i != 53231223 ? null : new TLRPC$InputGame() { // from class: org.telegram.tgnet.TLRPC$TL_inputGameID
                public static int constructor = 53231223;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.id = abstractSerializedData2.readInt64(z2);
                    this.access_hash = abstractSerializedData2.readInt64(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt64(this.id);
                    abstractSerializedData2.writeInt64(this.access_hash);
                }
            };
        }
        if (tLRPC$TL_inputGameShortName != null || !z) {
            if (tLRPC$TL_inputGameShortName != null) {
                tLRPC$TL_inputGameShortName.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_inputGameShortName;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputGame", Integer.valueOf(i)));
    }
}

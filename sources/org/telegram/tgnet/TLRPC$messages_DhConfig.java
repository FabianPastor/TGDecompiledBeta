package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$messages_DhConfig extends TLObject {
    public int g;
    public byte[] p;
    public byte[] random;
    public int version;

    public static TLRPC$messages_DhConfig TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_DhConfig tLRPC$messages_DhConfig;
        if (i == -NUM) {
            tLRPC$messages_DhConfig = new TLRPC$messages_DhConfig() { // from class: org.telegram.tgnet.TLRPC$TL_messages_dhConfigNotModified
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.random = abstractSerializedData2.readByteArray(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeByteArray(this.random);
                }
            };
        } else {
            tLRPC$messages_DhConfig = i != NUM ? null : new TLRPC$messages_DhConfig() { // from class: org.telegram.tgnet.TLRPC$TL_messages_dhConfig
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.g = abstractSerializedData2.readInt32(z2);
                    this.p = abstractSerializedData2.readByteArray(z2);
                    this.version = abstractSerializedData2.readInt32(z2);
                    this.random = abstractSerializedData2.readByteArray(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt32(this.g);
                    abstractSerializedData2.writeByteArray(this.p);
                    abstractSerializedData2.writeInt32(this.version);
                    abstractSerializedData2.writeByteArray(this.random);
                }
            };
        }
        if (tLRPC$messages_DhConfig != null || !z) {
            if (tLRPC$messages_DhConfig != null) {
                tLRPC$messages_DhConfig.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_DhConfig;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_DhConfig", Integer.valueOf(i)));
    }
}

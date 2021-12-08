package org.telegram.tgnet;

public abstract class TLRPC$messages_DhConfig extends TLObject {
    public int g;
    public byte[] p;
    public byte[] random;
    public int version;

    public static TLRPC$messages_DhConfig TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_DhConfig tLRPC$messages_DhConfig;
        if (i != -NUM) {
            tLRPC$messages_DhConfig = i != NUM ? null : new TLRPC$TL_messages_dhConfig();
        } else {
            tLRPC$messages_DhConfig = new TLRPC$TL_messages_dhConfigNotModified();
        }
        if (tLRPC$messages_DhConfig != null || !z) {
            if (tLRPC$messages_DhConfig != null) {
                tLRPC$messages_DhConfig.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_DhConfig;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_DhConfig", new Object[]{Integer.valueOf(i)}));
    }
}

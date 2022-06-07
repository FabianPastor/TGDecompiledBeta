package org.telegram.tgnet;

public abstract class TLRPC$AttachMenuBot extends TLObject {
    public static TLRPC$TL_attachMenuBot TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot;
        if (i != -NUM) {
            tLRPC$TL_attachMenuBot = i != -NUM ? null : new TLRPC$TL_attachMenuBot_layer140();
        } else {
            tLRPC$TL_attachMenuBot = new TLRPC$TL_attachMenuBot();
        }
        if (tLRPC$TL_attachMenuBot != null || !z) {
            if (tLRPC$TL_attachMenuBot != null) {
                tLRPC$TL_attachMenuBot.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_attachMenuBot;
        }
        throw new RuntimeException(String.format("can't parse magic %x in AttachMenuBot", new Object[]{Integer.valueOf(i)}));
    }
}

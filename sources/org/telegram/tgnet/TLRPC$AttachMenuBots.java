package org.telegram.tgnet;

public abstract class TLRPC$AttachMenuBots extends TLObject {
    public static TLRPC$AttachMenuBots TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$AttachMenuBots tLRPC$AttachMenuBots;
        if (i != -NUM) {
            tLRPC$AttachMenuBots = i != NUM ? null : new TLRPC$TL_attachMenuBots();
        } else {
            tLRPC$AttachMenuBots = new TLRPC$TL_attachMenuBotsNotModified();
        }
        if (tLRPC$AttachMenuBots != null || !z) {
            if (tLRPC$AttachMenuBots != null) {
                tLRPC$AttachMenuBots.readParams(abstractSerializedData, z);
            }
            return tLRPC$AttachMenuBots;
        }
        throw new RuntimeException(String.format("can't parse magic %x in AttachMenuBots", new Object[]{Integer.valueOf(i)}));
    }
}

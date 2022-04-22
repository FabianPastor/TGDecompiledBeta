package org.telegram.tgnet;

public abstract class TLRPC$help_AppUpdate extends TLObject {
    public static TLRPC$help_AppUpdate TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$help_AppUpdate tLRPC$help_AppUpdate;
        if (i != -NUM) {
            tLRPC$help_AppUpdate = i != -NUM ? null : new TLRPC$TL_help_appUpdate();
        } else {
            tLRPC$help_AppUpdate = new TLRPC$TL_help_noAppUpdate();
        }
        if (tLRPC$help_AppUpdate != null || !z) {
            if (tLRPC$help_AppUpdate != null) {
                tLRPC$help_AppUpdate.readParams(abstractSerializedData, z);
            }
            return tLRPC$help_AppUpdate;
        }
        throw new RuntimeException(String.format("can't parse magic %x in help_AppUpdate", new Object[]{Integer.valueOf(i)}));
    }
}

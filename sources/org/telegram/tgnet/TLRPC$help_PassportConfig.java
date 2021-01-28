package org.telegram.tgnet;

public abstract class TLRPC$help_PassportConfig extends TLObject {
    public static TLRPC$help_PassportConfig TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$help_PassportConfig tLRPC$help_PassportConfig;
        if (i != -NUM) {
            tLRPC$help_PassportConfig = i != -NUM ? null : new TLRPC$TL_help_passportConfigNotModified();
        } else {
            tLRPC$help_PassportConfig = new TLRPC$TL_help_passportConfig();
        }
        if (tLRPC$help_PassportConfig != null || !z) {
            if (tLRPC$help_PassportConfig != null) {
                tLRPC$help_PassportConfig.readParams(abstractSerializedData, z);
            }
            return tLRPC$help_PassportConfig;
        }
        throw new RuntimeException(String.format("can't parse magic %x in help_PassportConfig", new Object[]{Integer.valueOf(i)}));
    }
}

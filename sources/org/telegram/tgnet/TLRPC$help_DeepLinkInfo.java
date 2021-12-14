package org.telegram.tgnet;

public abstract class TLRPC$help_DeepLinkInfo extends TLObject {
    public static TLRPC$help_DeepLinkInfo TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$help_DeepLinkInfo tLRPC$help_DeepLinkInfo;
        if (i != NUM) {
            tLRPC$help_DeepLinkInfo = i != NUM ? null : new TLRPC$TL_help_deepLinkInfo();
        } else {
            tLRPC$help_DeepLinkInfo = new TLRPC$TL_help_deepLinkInfoEmpty();
        }
        if (tLRPC$help_DeepLinkInfo != null || !z) {
            if (tLRPC$help_DeepLinkInfo != null) {
                tLRPC$help_DeepLinkInfo.readParams(abstractSerializedData, z);
            }
            return tLRPC$help_DeepLinkInfo;
        }
        throw new RuntimeException(String.format("can't parse magic %x in help_DeepLinkInfo", new Object[]{Integer.valueOf(i)}));
    }
}

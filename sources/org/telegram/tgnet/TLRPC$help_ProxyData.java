package org.telegram.tgnet;

public abstract class TLRPC$help_ProxyData extends TLObject {
    public static TLRPC$help_ProxyData TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$help_ProxyData tLRPC$help_ProxyData;
        if (i != -NUM) {
            tLRPC$help_ProxyData = i != NUM ? null : new TLRPC$TL_help_proxyDataPromo();
        } else {
            tLRPC$help_ProxyData = new TLRPC$TL_help_proxyDataEmpty();
        }
        if (tLRPC$help_ProxyData != null || !z) {
            if (tLRPC$help_ProxyData != null) {
                tLRPC$help_ProxyData.readParams(abstractSerializedData, z);
            }
            return tLRPC$help_ProxyData;
        }
        throw new RuntimeException(String.format("can't parse magic %x in help_ProxyData", new Object[]{Integer.valueOf(i)}));
    }
}

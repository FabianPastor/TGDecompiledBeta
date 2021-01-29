package org.telegram.tgnet;

public abstract class TLRPC$help_TermsOfServiceUpdate extends TLObject {
    public static TLRPC$help_TermsOfServiceUpdate TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$help_TermsOfServiceUpdate tLRPC$help_TermsOfServiceUpdate;
        if (i != -NUM) {
            tLRPC$help_TermsOfServiceUpdate = i != NUM ? null : new TLRPC$TL_help_termsOfServiceUpdate();
        } else {
            tLRPC$help_TermsOfServiceUpdate = new TLRPC$TL_help_termsOfServiceUpdateEmpty();
        }
        if (tLRPC$help_TermsOfServiceUpdate != null || !z) {
            if (tLRPC$help_TermsOfServiceUpdate != null) {
                tLRPC$help_TermsOfServiceUpdate.readParams(abstractSerializedData, z);
            }
            return tLRPC$help_TermsOfServiceUpdate;
        }
        throw new RuntimeException(String.format("can't parse magic %x in help_TermsOfServiceUpdate", new Object[]{Integer.valueOf(i)}));
    }
}

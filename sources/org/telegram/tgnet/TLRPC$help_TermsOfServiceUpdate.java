package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$help_TermsOfServiceUpdate extends TLObject {
    public static TLRPC$help_TermsOfServiceUpdate TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$help_TermsOfServiceUpdate tLRPC$TL_help_termsOfServiceUpdateEmpty;
        if (i != -NUM) {
            tLRPC$TL_help_termsOfServiceUpdateEmpty = i != NUM ? null : new TLRPC$TL_help_termsOfServiceUpdate();
        } else {
            tLRPC$TL_help_termsOfServiceUpdateEmpty = new TLRPC$TL_help_termsOfServiceUpdateEmpty();
        }
        if (tLRPC$TL_help_termsOfServiceUpdateEmpty != null || !z) {
            if (tLRPC$TL_help_termsOfServiceUpdateEmpty != null) {
                tLRPC$TL_help_termsOfServiceUpdateEmpty.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_help_termsOfServiceUpdateEmpty;
        }
        throw new RuntimeException(String.format("can't parse magic %x in help_TermsOfServiceUpdate", Integer.valueOf(i)));
    }
}

package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_help_termsOfServiceUpdate extends TLRPC$help_TermsOfServiceUpdate {
    public static int constructor = NUM;
    public int expires;
    public TLRPC$TL_help_termsOfService terms_of_service;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.expires = abstractSerializedData.readInt32(z);
        this.terms_of_service = TLRPC$TL_help_termsOfService.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.expires);
        this.terms_of_service.serializeToStream(abstractSerializedData);
    }
}

package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_account_savedRingtoneConverted extends TLRPC$account_SavedRingtone {
    public static int constructor = NUM;
    public TLRPC$Document document;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.document = TLRPC$Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.document.serializeToStream(abstractSerializedData);
    }
}

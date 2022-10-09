package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_help_inviteText extends TLObject {
    public static int constructor = NUM;
    public String message;

    public static TLRPC$TL_help_inviteText TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_help_inviteText", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_help_inviteText tLRPC$TL_help_inviteText = new TLRPC$TL_help_inviteText();
        tLRPC$TL_help_inviteText.readParams(abstractSerializedData, z);
        return tLRPC$TL_help_inviteText;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.message = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.message);
    }
}

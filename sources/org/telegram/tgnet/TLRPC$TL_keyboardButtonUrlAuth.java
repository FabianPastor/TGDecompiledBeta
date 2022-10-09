package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_keyboardButtonUrlAuth extends TLRPC$KeyboardButton {
    public static int constructor = NUM;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.text = abstractSerializedData.readString(z);
        if ((this.flags & 1) != 0) {
            this.fwd_text = abstractSerializedData.readString(z);
        }
        this.url = abstractSerializedData.readString(z);
        this.button_id = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeString(this.text);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeString(this.fwd_text);
        }
        abstractSerializedData.writeString(this.url);
        abstractSerializedData.writeInt32(this.button_id);
    }
}

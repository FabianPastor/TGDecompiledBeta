package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_sendMessageEmojiInteraction extends TLRPC$SendMessageAction {
    public static int constructor = NUM;
    public String emoticon;
    public TLRPC$TL_dataJSON interaction;
    public int msg_id;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.emoticon = abstractSerializedData.readString(z);
        this.msg_id = abstractSerializedData.readInt32(z);
        this.interaction = TLRPC$TL_dataJSON.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.emoticon);
        abstractSerializedData.writeInt32(this.msg_id);
        this.interaction.serializeToStream(abstractSerializedData);
    }
}

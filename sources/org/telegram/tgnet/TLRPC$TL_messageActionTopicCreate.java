package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messageActionTopicCreate extends TLRPC$MessageAction {
    public static int constructor = NUM;
    public int flags;
    public int icon_color;
    public long icon_emoji_id;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.title = abstractSerializedData.readString(z);
        this.icon_color = abstractSerializedData.readInt32(z);
        if ((this.flags & 1) != 0) {
            this.icon_emoji_id = abstractSerializedData.readInt64(z);
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeString(this.title);
        abstractSerializedData.writeInt32(this.icon_color);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt64(this.icon_emoji_id);
        }
    }
}

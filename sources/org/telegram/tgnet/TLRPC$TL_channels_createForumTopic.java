package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_channels_createForumTopic extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputChannel channel;
    public int flags;
    public int icon_color;
    public long icon_emoji_id;
    public long random_id;
    public TLRPC$InputPeer send_as;
    public String title;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.channel.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.title);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.icon_color);
        }
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeInt64(this.icon_emoji_id);
        }
        abstractSerializedData.writeInt64(this.random_id);
        if ((this.flags & 4) != 0) {
            this.send_as.serializeToStream(abstractSerializedData);
        }
    }
}

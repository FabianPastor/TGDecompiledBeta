package org.telegram.tgnet;

public class TLRPC$TL_recentMeUrlChatInvite extends TLRPC$RecentMeUrl {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.url = abstractSerializedData.readString(z);
        this.chat_invite = TLRPC$ChatInvite.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.url);
        this.chat_invite.serializeToStream(abstractSerializedData);
    }
}

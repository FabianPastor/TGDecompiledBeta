package org.telegram.tgnet;

import java.util.ArrayList;
import org.telegram.messenger.MessageObject;
/* loaded from: classes.dex */
public class TLRPC$TL_forumTopic extends TLRPC$ForumTopic {
    public static int constructor = NUM;
    public boolean closed;
    public int date;
    public TLRPC$DraftMessage draft;
    public int flags;
    public TLRPC$Peer from_id;
    public ArrayList<MessageObject> groupedMessages;
    public int icon_color;
    public long icon_emoji_id;
    public int id;
    public boolean isShort;
    public boolean my;
    public TLRPC$PeerNotifySettings notify_settings;
    public boolean pinned;
    public int pinnedOrder;
    public int read_inbox_max_id;
    public int read_outbox_max_id;
    public String searchQuery;
    public String title;
    public TLRPC$Message topMessage;
    public int top_message;
    public TLRPC$Message topicStartMessage;
    public int unread_count;
    public int unread_mentions_count;
    public int unread_reactions_count;

    public static TLRPC$TL_forumTopic TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_forumTopic tLRPC$TL_forumTopicDeleted;
        if (i == 37687451) {
            tLRPC$TL_forumTopicDeleted = new TLRPC$TL_forumTopicDeleted();
        } else if (i == NUM) {
            tLRPC$TL_forumTopicDeleted = new TLRPC$TL_forumTopic_layer147();
        } else {
            tLRPC$TL_forumTopicDeleted = i != NUM ? null : new TLRPC$TL_forumTopic();
        }
        if (tLRPC$TL_forumTopicDeleted != null || !z) {
            if (tLRPC$TL_forumTopicDeleted != null) {
                tLRPC$TL_forumTopicDeleted.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_forumTopicDeleted;
        }
        throw new RuntimeException(String.format("can't parse magic %x in TL_forumTopic", Integer.valueOf(i)));
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.my = (readInt32 & 2) != 0;
        this.closed = (readInt32 & 4) != 0;
        this.pinned = (readInt32 & 8) != 0;
        if ((readInt32 & 32) != 0) {
            z2 = true;
        }
        this.isShort = z2;
        this.id = abstractSerializedData.readInt32(z);
        this.date = abstractSerializedData.readInt32(z);
        this.title = abstractSerializedData.readString(z);
        this.icon_color = abstractSerializedData.readInt32(z);
        if ((this.flags & 1) != 0) {
            this.icon_emoji_id = abstractSerializedData.readInt64(z);
        }
        this.top_message = abstractSerializedData.readInt32(z);
        this.read_inbox_max_id = abstractSerializedData.readInt32(z);
        this.read_outbox_max_id = abstractSerializedData.readInt32(z);
        this.unread_count = abstractSerializedData.readInt32(z);
        this.unread_mentions_count = abstractSerializedData.readInt32(z);
        this.unread_reactions_count = abstractSerializedData.readInt32(z);
        this.from_id = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.notify_settings = TLRPC$PeerNotifySettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if ((this.flags & 16) != 0) {
            this.draft = TLRPC$DraftMessage.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.my ? this.flags | 2 : this.flags & (-3);
        this.flags = i;
        int i2 = this.closed ? i | 4 : i & (-5);
        this.flags = i2;
        int i3 = this.pinned ? i2 | 8 : i2 & (-9);
        this.flags = i3;
        int i4 = this.isShort ? i3 | 32 : i3 & (-33);
        this.flags = i4;
        abstractSerializedData.writeInt32(i4);
        abstractSerializedData.writeInt32(this.id);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeString(this.title);
        abstractSerializedData.writeInt32(this.icon_color);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt64(this.icon_emoji_id);
        }
        abstractSerializedData.writeInt32(this.top_message);
        abstractSerializedData.writeInt32(this.read_inbox_max_id);
        abstractSerializedData.writeInt32(this.read_outbox_max_id);
        abstractSerializedData.writeInt32(this.unread_count);
        abstractSerializedData.writeInt32(this.unread_mentions_count);
        abstractSerializedData.writeInt32(this.unread_reactions_count);
        this.from_id.serializeToStream(abstractSerializedData);
        this.notify_settings.serializeToStream(abstractSerializedData);
        if ((this.flags & 16) != 0) {
            this.draft.serializeToStream(abstractSerializedData);
        }
    }
}

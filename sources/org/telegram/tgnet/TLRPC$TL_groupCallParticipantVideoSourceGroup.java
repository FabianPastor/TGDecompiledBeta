package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public class TLRPC$TL_groupCallParticipantVideoSourceGroup extends TLObject {
    public static int constructor = -NUM;
    public String semantics;
    public ArrayList<Integer> sources = new ArrayList<>();

    public static TLRPC$TL_groupCallParticipantVideoSourceGroup TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_groupCallParticipantVideoSourceGroup", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_groupCallParticipantVideoSourceGroup tLRPC$TL_groupCallParticipantVideoSourceGroup = new TLRPC$TL_groupCallParticipantVideoSourceGroup();
        tLRPC$TL_groupCallParticipantVideoSourceGroup.readParams(abstractSerializedData, z);
        return tLRPC$TL_groupCallParticipantVideoSourceGroup;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.semantics = abstractSerializedData.readString(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
            }
            return;
        }
        int readInt322 = abstractSerializedData.readInt32(z);
        for (int i = 0; i < readInt322; i++) {
            this.sources.add(Integer.valueOf(abstractSerializedData.readInt32(z)));
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.semantics);
        abstractSerializedData.writeInt32(NUM);
        int size = this.sources.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            abstractSerializedData.writeInt32(this.sources.get(i).intValue());
        }
    }
}

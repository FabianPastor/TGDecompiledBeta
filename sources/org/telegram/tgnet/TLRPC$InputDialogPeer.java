package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$InputDialogPeer extends TLObject {
    public static TLRPC$InputDialogPeer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputDialogPeer tLRPC$TL_inputDialogPeer;
        if (i == -55902537) {
            tLRPC$TL_inputDialogPeer = new TLRPC$TL_inputDialogPeer();
        } else {
            tLRPC$TL_inputDialogPeer = i != NUM ? null : new TLRPC$InputDialogPeer() { // from class: org.telegram.tgnet.TLRPC$TL_inputDialogPeerFolder
                public static int constructor = NUM;
                public int folder_id;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.folder_id = abstractSerializedData2.readInt32(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt32(this.folder_id);
                }
            };
        }
        if (tLRPC$TL_inputDialogPeer != null || !z) {
            if (tLRPC$TL_inputDialogPeer != null) {
                tLRPC$TL_inputDialogPeer.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_inputDialogPeer;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputDialogPeer", Integer.valueOf(i)));
    }
}

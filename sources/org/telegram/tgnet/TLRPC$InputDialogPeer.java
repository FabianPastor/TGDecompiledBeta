package org.telegram.tgnet;

public abstract class TLRPC$InputDialogPeer extends TLObject {
    public static TLRPC$InputDialogPeer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputDialogPeer tLRPC$InputDialogPeer;
        if (i != -55902537) {
            tLRPC$InputDialogPeer = i != NUM ? null : new TLRPC$TL_inputDialogPeerFolder();
        } else {
            tLRPC$InputDialogPeer = new TLRPC$TL_inputDialogPeer();
        }
        if (tLRPC$InputDialogPeer != null || !z) {
            if (tLRPC$InputDialogPeer != null) {
                tLRPC$InputDialogPeer.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputDialogPeer;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputDialogPeer", new Object[]{Integer.valueOf(i)}));
    }
}

package org.telegram.messenger.exoplayer.upstream;

public interface TransferListener {
    void onBytesTransferred(int i);

    void onTransferEnd();

    void onTransferStart();
}

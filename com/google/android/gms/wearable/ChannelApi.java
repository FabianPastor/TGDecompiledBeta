package com.google.android.gms.wearable;

@Deprecated
public interface ChannelApi {

    @Deprecated
    public interface ChannelListener {
        void onChannelClosed(Channel channel, int i, int i2);

        void onChannelOpened(Channel channel);

        void onInputClosed(Channel channel, int i, int i2);

        void onOutputClosed(Channel channel, int i, int i2);
    }
}

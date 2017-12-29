package com.google.android.gms.wearable;

import android.os.Parcelable;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.wearable.Wearable.WearableOptions;

public abstract class ChannelClient extends GoogleApi<WearableOptions> {

    public interface Channel extends Parcelable {
    }

    public static abstract class ChannelCallback {
        public void onChannelClosed(Channel channel, int i, int i2) {
        }

        public void onChannelOpened(Channel channel) {
        }

        public void onInputClosed(Channel channel, int i, int i2) {
        }

        public void onOutputClosed(Channel channel, int i, int i2) {
        }
    }
}

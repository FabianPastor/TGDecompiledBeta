package org.telegram.messenger.exoplayer2;

public class DefaultControlDispatcher implements ControlDispatcher {
    public boolean dispatchSetPlayWhenReady(Player player, boolean playWhenReady) {
        player.setPlayWhenReady(playWhenReady);
        return true;
    }

    public boolean dispatchSeekTo(Player player, int windowIndex, long positionMs) {
        player.seekTo(windowIndex, positionMs);
        return true;
    }

    public boolean dispatchSetRepeatMode(Player player, int repeatMode) {
        player.setRepeatMode(repeatMode);
        return true;
    }

    public boolean dispatchSetShuffleModeEnabled(Player player, boolean shuffleModeEnabled) {
        player.setShuffleModeEnabled(shuffleModeEnabled);
        return true;
    }

    public boolean dispatchStop(Player player, boolean reset) {
        player.stop(reset);
        return true;
    }
}

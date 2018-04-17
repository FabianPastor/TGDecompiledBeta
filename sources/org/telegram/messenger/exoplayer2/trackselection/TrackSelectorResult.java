package org.telegram.messenger.exoplayer2.trackselection;

import org.telegram.messenger.exoplayer2.RendererConfiguration;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class TrackSelectorResult {
    public final TrackGroupArray groups;
    public final Object info;
    public final RendererConfiguration[] rendererConfigurations;
    public final boolean[] renderersEnabled;
    public final TrackSelectionArray selections;

    public TrackSelectorResult(TrackGroupArray groups, boolean[] renderersEnabled, TrackSelectionArray selections, Object info, RendererConfiguration[] rendererConfigurations) {
        this.groups = groups;
        this.renderersEnabled = renderersEnabled;
        this.selections = selections;
        this.info = info;
        this.rendererConfigurations = rendererConfigurations;
    }

    public boolean isEquivalent(TrackSelectorResult other) {
        if (other != null) {
            if (other.selections.length == this.selections.length) {
                for (int i = 0; i < this.selections.length; i++) {
                    if (!isEquivalent(other, i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean isEquivalent(TrackSelectorResult other, int index) {
        boolean z = false;
        if (other == null) {
            return false;
        }
        if (this.renderersEnabled[index] == other.renderersEnabled[index] && Util.areEqual(this.selections.get(index), other.selections.get(index)) && Util.areEqual(this.rendererConfigurations[index], other.rendererConfigurations[index])) {
            z = true;
        }
        return z;
    }
}

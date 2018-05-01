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

    public TrackSelectorResult(TrackGroupArray trackGroupArray, boolean[] zArr, TrackSelectionArray trackSelectionArray, Object obj, RendererConfiguration[] rendererConfigurationArr) {
        this.groups = trackGroupArray;
        this.renderersEnabled = zArr;
        this.selections = trackSelectionArray;
        this.info = obj;
        this.rendererConfigurations = rendererConfigurationArr;
    }

    public boolean isEquivalent(TrackSelectorResult trackSelectorResult) {
        if (trackSelectorResult != null) {
            if (trackSelectorResult.selections.length == this.selections.length) {
                for (int i = 0; i < this.selections.length; i++) {
                    if (!isEquivalent(trackSelectorResult, i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean isEquivalent(TrackSelectorResult trackSelectorResult, int i) {
        boolean z = false;
        if (trackSelectorResult == null) {
            return false;
        }
        if (this.renderersEnabled[i] == trackSelectorResult.renderersEnabled[i] && Util.areEqual(this.selections.get(i), trackSelectorResult.selections.get(i)) && Util.areEqual(this.rendererConfigurations[i], trackSelectorResult.rendererConfigurations[i]) != null) {
            z = true;
        }
        return z;
    }
}

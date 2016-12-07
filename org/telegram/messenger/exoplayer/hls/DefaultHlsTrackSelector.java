package org.telegram.messenger.exoplayer.hls;

import android.content.Context;
import android.text.TextUtils;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer.chunk.VideoFormatSelectorUtil;
import org.telegram.messenger.exoplayer.hls.HlsTrackSelector.Output;

public final class DefaultHlsTrackSelector implements HlsTrackSelector {
    private static final int TYPE_AUDIO = 1;
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_SUBTITLE = 2;
    private final Context context;
    private final int type;

    public static DefaultHlsTrackSelector newDefaultInstance(Context context) {
        return new DefaultHlsTrackSelector(context, 0);
    }

    public static DefaultHlsTrackSelector newAudioInstance() {
        return new DefaultHlsTrackSelector(null, 1);
    }

    public static DefaultHlsTrackSelector newSubtitleInstance() {
        return new DefaultHlsTrackSelector(null, 2);
    }

    private DefaultHlsTrackSelector(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    public void selectTracks(HlsMasterPlaylist playlist, Output output) throws IOException {
        int i;
        if (this.type == 1 || this.type == 2) {
            List<Variant> variants = this.type == 1 ? playlist.audios : playlist.subtitles;
            if (variants != null && !variants.isEmpty()) {
                for (i = 0; i < variants.size(); i++) {
                    output.fixedTrack(playlist, (Variant) variants.get(i));
                }
                return;
            }
            return;
        }
        ArrayList<Variant> enabledVariantList = new ArrayList();
        int[] variantIndices = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay(this.context, playlist.variants, null, false);
        for (int i2 : variantIndices) {
            enabledVariantList.add(playlist.variants.get(i2));
        }
        ArrayList<Variant> definiteVideoVariants = new ArrayList();
        ArrayList<Variant> definiteAudioOnlyVariants = new ArrayList();
        for (i = 0; i < enabledVariantList.size(); i++) {
            Variant variant = (Variant) enabledVariantList.get(i);
            if (variant.format.height > 0 || variantHasExplicitCodecWithPrefix(variant, "avc")) {
                definiteVideoVariants.add(variant);
            } else if (variantHasExplicitCodecWithPrefix(variant, AudioSampleEntry.TYPE3)) {
                definiteAudioOnlyVariants.add(variant);
            }
        }
        if (!definiteVideoVariants.isEmpty()) {
            enabledVariantList = definiteVideoVariants;
        } else if (definiteAudioOnlyVariants.size() < enabledVariantList.size()) {
            enabledVariantList.removeAll(definiteAudioOnlyVariants);
        }
        if (enabledVariantList.size() > 1) {
            Variant[] enabledVariants = new Variant[enabledVariantList.size()];
            enabledVariantList.toArray(enabledVariants);
            output.adaptiveTrack(playlist, enabledVariants);
        }
        for (i = 0; i < enabledVariantList.size(); i++) {
            output.fixedTrack(playlist, (Variant) enabledVariantList.get(i));
        }
    }

    private static boolean variantHasExplicitCodecWithPrefix(Variant variant, String prefix) {
        String codecs = variant.format.codecs;
        if (TextUtils.isEmpty(codecs)) {
            return false;
        }
        String[] codecArray = codecs.split("(\\s*,\\s*)|(\\s*$)");
        for (String startsWith : codecArray) {
            if (startsWith.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}

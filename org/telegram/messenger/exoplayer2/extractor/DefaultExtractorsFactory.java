package org.telegram.messenger.exoplayer2.extractor;

import java.util.ArrayList;
import java.util.List;

public final class DefaultExtractorsFactory implements ExtractorsFactory {
    private static List<Class<? extends Extractor>> defaultExtractorClasses;

    public DefaultExtractorsFactory() {
        synchronized (DefaultExtractorsFactory.class) {
            if (defaultExtractorClasses == null) {
                List<Class<? extends Extractor>> extractorClasses = new ArrayList();
                try {
                    extractorClasses.add(Class.forName("org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor").asSubclass(Extractor.class));
                } catch (ClassNotFoundException e) {
                }
                try {
                    extractorClasses.add(Class.forName("org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor").asSubclass(Extractor.class));
                } catch (ClassNotFoundException e2) {
                }
                try {
                    extractorClasses.add(Class.forName("org.telegram.messenger.exoplayer2.extractor.mp4.Mp4Extractor").asSubclass(Extractor.class));
                } catch (ClassNotFoundException e3) {
                }
                try {
                    extractorClasses.add(Class.forName("org.telegram.messenger.exoplayer2.extractor.mp3.Mp3Extractor").asSubclass(Extractor.class));
                } catch (ClassNotFoundException e4) {
                }
                try {
                    extractorClasses.add(Class.forName("org.telegram.messenger.exoplayer2.extractor.ts.AdtsExtractor").asSubclass(Extractor.class));
                } catch (ClassNotFoundException e5) {
                }
                try {
                    extractorClasses.add(Class.forName("org.telegram.messenger.exoplayer2.extractor.ts.Ac3Extractor").asSubclass(Extractor.class));
                } catch (ClassNotFoundException e6) {
                }
                try {
                    extractorClasses.add(Class.forName("org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor").asSubclass(Extractor.class));
                } catch (ClassNotFoundException e7) {
                }
                try {
                    extractorClasses.add(Class.forName("org.telegram.messenger.exoplayer2.extractor.flv.FlvExtractor").asSubclass(Extractor.class));
                } catch (ClassNotFoundException e8) {
                }
                try {
                    extractorClasses.add(Class.forName("org.telegram.messenger.exoplayer2.extractor.ogg.OggExtractor").asSubclass(Extractor.class));
                } catch (ClassNotFoundException e9) {
                }
                try {
                    extractorClasses.add(Class.forName("org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor").asSubclass(Extractor.class));
                } catch (ClassNotFoundException e10) {
                }
                try {
                    extractorClasses.add(Class.forName("org.telegram.messenger.exoplayer2.extractor.wav.WavExtractor").asSubclass(Extractor.class));
                } catch (ClassNotFoundException e11) {
                }
                try {
                    extractorClasses.add(Class.forName("org.telegram.messenger.exoplayer2.ext.flac.FlacExtractor").asSubclass(Extractor.class));
                } catch (ClassNotFoundException e12) {
                }
                defaultExtractorClasses = extractorClasses;
            }
        }
    }

    public Extractor[] createExtractors() {
        Extractor[] extractors = new Extractor[defaultExtractorClasses.size()];
        int i = 0;
        while (i < extractors.length) {
            try {
                extractors[i] = (Extractor) ((Class) defaultExtractorClasses.get(i)).getConstructor(new Class[0]).newInstance(new Object[0]);
                i++;
            } catch (Exception e) {
                throw new IllegalStateException("Unexpected error creating default extractor", e);
            }
        }
        return extractors;
    }
}

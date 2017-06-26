package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;
import java.util.HashSet;
import java.util.Set;

public class MultiProcessor<T> implements Processor<T> {
    private int zzbMP;
    private Factory<T> zzbNb;
    private SparseArray<zza> zzbNc;

    public static class Builder<T> {
        private MultiProcessor<T> zzbNd = new MultiProcessor();

        public Builder(Factory<T> factory) {
            if (factory == null) {
                throw new IllegalArgumentException("No factory supplied.");
            }
            this.zzbNd.zzbNb = factory;
        }

        public MultiProcessor<T> build() {
            return this.zzbNd;
        }

        public Builder<T> setMaxGapFrames(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("Invalid max gap: " + i);
            }
            this.zzbNd.zzbMP = i;
            return this;
        }
    }

    public interface Factory<T> {
        Tracker<T> create(T t);
    }

    class zza {
        private Tracker<T> zzbMO;
        private int zzbMS;

        private zza(MultiProcessor multiProcessor) {
            this.zzbMS = 0;
        }
    }

    private MultiProcessor() {
        this.zzbNc = new SparseArray();
        this.zzbMP = 3;
    }

    private final void zza(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.zzbNc.get(keyAt);
            com_google_android_gms_vision_MultiProcessor_zza.zzbMS = 0;
            com_google_android_gms_vision_MultiProcessor_zza.zzbMO.onUpdate(detections, valueAt);
        }
    }

    public void receiveDetections(Detections<T> detections) {
        int i = 0;
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i2 = 0; i2 < detectedItems.size(); i2++) {
            int keyAt = detectedItems.keyAt(i2);
            Object valueAt = detectedItems.valueAt(i2);
            if (this.zzbNc.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = new zza();
                com_google_android_gms_vision_MultiProcessor_zza.zzbMO = this.zzbNb.create(valueAt);
                com_google_android_gms_vision_MultiProcessor_zza.zzbMO.onNewItem(keyAt, valueAt);
                this.zzbNc.append(keyAt, com_google_android_gms_vision_MultiProcessor_zza);
            }
        }
        detectedItems = detections.getDetectedItems();
        Set<Integer> hashSet = new HashSet();
        while (i < this.zzbNc.size()) {
            int keyAt2 = this.zzbNc.keyAt(i);
            if (detectedItems.get(keyAt2) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza2 = (zza) this.zzbNc.valueAt(i);
                com_google_android_gms_vision_MultiProcessor_zza2.zzbMS = com_google_android_gms_vision_MultiProcessor_zza2.zzbMS + 1;
                if (com_google_android_gms_vision_MultiProcessor_zza2.zzbMS >= this.zzbMP) {
                    com_google_android_gms_vision_MultiProcessor_zza2.zzbMO.onDone();
                    hashSet.add(Integer.valueOf(keyAt2));
                } else {
                    com_google_android_gms_vision_MultiProcessor_zza2.zzbMO.onMissing(detections);
                }
            }
            i++;
        }
        for (Integer intValue : hashSet) {
            this.zzbNc.delete(intValue.intValue());
        }
        zza(detections);
    }

    public void release() {
        for (int i = 0; i < this.zzbNc.size(); i++) {
            ((zza) this.zzbNc.valueAt(i)).zzbMO.onDone();
        }
        this.zzbNc.clear();
    }
}

package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;
import java.util.HashSet;
import java.util.Set;

public class MultiProcessor<T> implements Processor<T> {
    private Factory<T> zzbOF;
    private SparseArray<zza> zzbOG;
    private int zzbOt;

    public static class Builder<T> {
        private MultiProcessor<T> zzbOH = new MultiProcessor();

        public Builder(Factory<T> factory) {
            if (factory == null) {
                throw new IllegalArgumentException("No factory supplied.");
            }
            this.zzbOH.zzbOF = factory;
        }

        public MultiProcessor<T> build() {
            return this.zzbOH;
        }

        public Builder<T> setMaxGapFrames(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("Invalid max gap: " + i);
            }
            this.zzbOH.zzbOt = i;
            return this;
        }
    }

    public interface Factory<T> {
        Tracker<T> create(T t);
    }

    private class zza {
        private Tracker<T> zzbOs;
        private int zzbOw;

        private zza(MultiProcessor multiProcessor) {
            this.zzbOw = 0;
        }
    }

    private MultiProcessor() {
        this.zzbOG = new SparseArray();
        this.zzbOt = 3;
    }

    private void zza(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            if (this.zzbOG.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = new zza();
                com_google_android_gms_vision_MultiProcessor_zza.zzbOs = this.zzbOF.create(valueAt);
                com_google_android_gms_vision_MultiProcessor_zza.zzbOs.onNewItem(keyAt, valueAt);
                this.zzbOG.append(keyAt, com_google_android_gms_vision_MultiProcessor_zza);
            }
        }
    }

    private void zzb(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        Set<Integer> hashSet = new HashSet();
        for (int i = 0; i < this.zzbOG.size(); i++) {
            int keyAt = this.zzbOG.keyAt(i);
            if (detectedItems.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.zzbOG.valueAt(i);
                com_google_android_gms_vision_MultiProcessor_zza.zzbOw = com_google_android_gms_vision_MultiProcessor_zza.zzbOw + 1;
                if (com_google_android_gms_vision_MultiProcessor_zza.zzbOw >= this.zzbOt) {
                    com_google_android_gms_vision_MultiProcessor_zza.zzbOs.onDone();
                    hashSet.add(Integer.valueOf(keyAt));
                } else {
                    com_google_android_gms_vision_MultiProcessor_zza.zzbOs.onMissing(detections);
                }
            }
        }
        for (Integer intValue : hashSet) {
            this.zzbOG.delete(intValue.intValue());
        }
    }

    private void zzc(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.zzbOG.get(keyAt);
            com_google_android_gms_vision_MultiProcessor_zza.zzbOw = 0;
            com_google_android_gms_vision_MultiProcessor_zza.zzbOs.onUpdate(detections, valueAt);
        }
    }

    public void receiveDetections(Detections<T> detections) {
        zza(detections);
        zzb(detections);
        zzc(detections);
    }

    public void release() {
        for (int i = 0; i < this.zzbOG.size(); i++) {
            ((zza) this.zzbOG.valueAt(i)).zzbOs.onDone();
        }
        this.zzbOG.clear();
    }
}

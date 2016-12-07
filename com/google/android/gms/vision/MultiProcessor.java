package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;
import java.util.HashSet;
import java.util.Set;

public class MultiProcessor<T> implements Processor<T> {
    private int aNH;
    private Factory<T> aNT;
    private SparseArray<zza> aNU;

    public static class Builder<T> {
        private MultiProcessor<T> aNV = new MultiProcessor();

        public Builder(Factory<T> factory) {
            if (factory == null) {
                throw new IllegalArgumentException("No factory supplied.");
            }
            this.aNV.aNT = factory;
        }

        public MultiProcessor<T> build() {
            return this.aNV;
        }

        public Builder<T> setMaxGapFrames(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("Invalid max gap: " + i);
            }
            this.aNV.aNH = i;
            return this;
        }
    }

    public interface Factory<T> {
        Tracker<T> create(T t);
    }

    private class zza {
        private Tracker<T> aNG;
        private int aNK;
        final /* synthetic */ MultiProcessor aNW;

        private zza(MultiProcessor multiProcessor) {
            this.aNW = multiProcessor;
            this.aNK = 0;
        }
    }

    private MultiProcessor() {
        this.aNU = new SparseArray();
        this.aNH = 3;
    }

    private void zza(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            if (this.aNU.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = new zza();
                com_google_android_gms_vision_MultiProcessor_zza.aNG = this.aNT.create(valueAt);
                com_google_android_gms_vision_MultiProcessor_zza.aNG.onNewItem(keyAt, valueAt);
                this.aNU.append(keyAt, com_google_android_gms_vision_MultiProcessor_zza);
            }
        }
    }

    private void zzb(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        Set<Integer> hashSet = new HashSet();
        for (int i = 0; i < this.aNU.size(); i++) {
            int keyAt = this.aNU.keyAt(i);
            if (detectedItems.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.aNU.valueAt(i);
                com_google_android_gms_vision_MultiProcessor_zza.aNK = com_google_android_gms_vision_MultiProcessor_zza.aNK + 1;
                if (com_google_android_gms_vision_MultiProcessor_zza.aNK >= this.aNH) {
                    com_google_android_gms_vision_MultiProcessor_zza.aNG.onDone();
                    hashSet.add(Integer.valueOf(keyAt));
                } else {
                    com_google_android_gms_vision_MultiProcessor_zza.aNG.onMissing(detections);
                }
            }
        }
        for (Integer intValue : hashSet) {
            this.aNU.delete(intValue.intValue());
        }
    }

    private void zzc(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.aNU.get(keyAt);
            com_google_android_gms_vision_MultiProcessor_zza.aNK = 0;
            com_google_android_gms_vision_MultiProcessor_zza.aNG.onUpdate(detections, valueAt);
        }
    }

    public void receiveDetections(Detections<T> detections) {
        zza(detections);
        zzb(detections);
        zzc(detections);
    }

    public void release() {
        for (int i = 0; i < this.aNU.size(); i++) {
            ((zza) this.aNU.valueAt(i)).aNG.onDone();
        }
        this.aNU.clear();
    }
}

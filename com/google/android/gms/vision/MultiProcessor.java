package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;
import java.util.HashSet;
import java.util.Set;

public class MultiProcessor<T> implements Processor<T> {
    private Factory<T> aKI;
    private SparseArray<zza> aKJ;
    private int aKw;

    public static class Builder<T> {
        private MultiProcessor<T> aKK = new MultiProcessor();

        public Builder(Factory<T> factory) {
            if (factory == null) {
                throw new IllegalArgumentException("No factory supplied.");
            }
            this.aKK.aKI = factory;
        }

        public MultiProcessor<T> build() {
            return this.aKK;
        }

        public Builder<T> setMaxGapFrames(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("Invalid max gap: " + i);
            }
            this.aKK.aKw = i;
            return this;
        }
    }

    public interface Factory<T> {
        Tracker<T> create(T t);
    }

    private class zza {
        final /* synthetic */ MultiProcessor aKL;
        private Tracker<T> aKv;
        private int aKz;

        private zza(MultiProcessor multiProcessor) {
            this.aKL = multiProcessor;
            this.aKz = 0;
        }
    }

    private MultiProcessor() {
        this.aKJ = new SparseArray();
        this.aKw = 3;
    }

    private void zza(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            if (this.aKJ.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = new zza();
                com_google_android_gms_vision_MultiProcessor_zza.aKv = this.aKI.create(valueAt);
                com_google_android_gms_vision_MultiProcessor_zza.aKv.onNewItem(keyAt, valueAt);
                this.aKJ.append(keyAt, com_google_android_gms_vision_MultiProcessor_zza);
            }
        }
    }

    private void zzb(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        Set<Integer> hashSet = new HashSet();
        for (int i = 0; i < this.aKJ.size(); i++) {
            int keyAt = this.aKJ.keyAt(i);
            if (detectedItems.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.aKJ.valueAt(i);
                com_google_android_gms_vision_MultiProcessor_zza.aKz = com_google_android_gms_vision_MultiProcessor_zza.aKz + 1;
                if (com_google_android_gms_vision_MultiProcessor_zza.aKz >= this.aKw) {
                    com_google_android_gms_vision_MultiProcessor_zza.aKv.onDone();
                    hashSet.add(Integer.valueOf(keyAt));
                } else {
                    com_google_android_gms_vision_MultiProcessor_zza.aKv.onMissing(detections);
                }
            }
        }
        for (Integer intValue : hashSet) {
            this.aKJ.delete(intValue.intValue());
        }
    }

    private void zzc(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.aKJ.get(keyAt);
            com_google_android_gms_vision_MultiProcessor_zza.aKz = 0;
            com_google_android_gms_vision_MultiProcessor_zza.aKv.onUpdate(detections, valueAt);
        }
    }

    public void receiveDetections(Detections<T> detections) {
        zza(detections);
        zzb(detections);
        zzc(detections);
    }

    public void release() {
        for (int i = 0; i < this.aKJ.size(); i++) {
            ((zza) this.aKJ.valueAt(i)).aKv.onDone();
        }
        this.aKJ.clear();
    }
}

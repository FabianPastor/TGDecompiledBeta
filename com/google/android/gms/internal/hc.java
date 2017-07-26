package com.google.android.gms.internal;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public final class hc {
    private static int zza(String str, hg[] hgVarArr) {
        int i = 14;
        for (hg hgVar : hgVarArr) {
            if (i == 14) {
                if (hgVar.type == 9 || hgVar.type == 2 || hgVar.type == 6) {
                    i = hgVar.type;
                } else if (hgVar.type != 14) {
                    throw new IllegalArgumentException(new StringBuilder(String.valueOf(str).length() + 48).append("Unexpected TypedValue type: ").append(hgVar.type).append(" for key ").append(str).toString());
                }
            } else if (hgVar.type != i) {
                throw new IllegalArgumentException(new StringBuilder(String.valueOf(str).length() + 126).append("The ArrayList elements should all be the same type, but ArrayList with key ").append(str).append(" contains items of type ").append(i).append(" and ").append(hgVar.type).toString());
            }
        }
        return i;
    }

    public static hd zza(DataMap dataMap) {
        he heVar = new he();
        List arrayList = new ArrayList();
        heVar.zzbTH = zza(dataMap, arrayList);
        return new hd(heVar, arrayList);
    }

    private static hg zza(List<Asset> list, Object obj) {
        hg hgVar = new hg();
        if (obj == null) {
            hgVar.type = 14;
            return hgVar;
        }
        hgVar.zzbTL = new hh();
        if (obj instanceof String) {
            hgVar.type = 2;
            hgVar.zzbTL.zzbTN = (String) obj;
        } else if (obj instanceof Integer) {
            hgVar.type = 6;
            hgVar.zzbTL.zzbTR = ((Integer) obj).intValue();
        } else if (obj instanceof Long) {
            hgVar.type = 5;
            hgVar.zzbTL.zzbTQ = ((Long) obj).longValue();
        } else if (obj instanceof Double) {
            hgVar.type = 3;
            hgVar.zzbTL.zzbTO = ((Double) obj).doubleValue();
        } else if (obj instanceof Float) {
            hgVar.type = 4;
            hgVar.zzbTL.zzbTP = ((Float) obj).floatValue();
        } else if (obj instanceof Boolean) {
            hgVar.type = 8;
            hgVar.zzbTL.zzbTT = ((Boolean) obj).booleanValue();
        } else if (obj instanceof Byte) {
            hgVar.type = 7;
            hgVar.zzbTL.zzbTS = ((Byte) obj).byteValue();
        } else if (obj instanceof byte[]) {
            hgVar.type = 1;
            hgVar.zzbTL.zzbTM = (byte[]) obj;
        } else if (obj instanceof String[]) {
            hgVar.type = 11;
            hgVar.zzbTL.zzbTW = (String[]) obj;
        } else if (obj instanceof long[]) {
            hgVar.type = 12;
            hgVar.zzbTL.zzbTX = (long[]) obj;
        } else if (obj instanceof float[]) {
            hgVar.type = 15;
            hgVar.zzbTL.zzbTY = (float[]) obj;
        } else if (obj instanceof Asset) {
            hgVar.type = 13;
            hh hhVar = hgVar.zzbTL;
            list.add((Asset) obj);
            hhVar.zzbTZ = (long) (list.size() - 1);
        } else if (obj instanceof DataMap) {
            hgVar.type = 9;
            DataMap dataMap = (DataMap) obj;
            TreeSet treeSet = new TreeSet(dataMap.keySet());
            hf[] hfVarArr = new hf[treeSet.size()];
            Iterator it = treeSet.iterator();
            r1 = 0;
            while (it.hasNext()) {
                r0 = (String) it.next();
                hfVarArr[r1] = new hf();
                hfVarArr[r1].name = r0;
                hfVarArr[r1].zzbTJ = zza((List) list, dataMap.get(r0));
                r1++;
            }
            hgVar.zzbTL.zzbTU = hfVarArr;
        } else if (obj instanceof ArrayList) {
            hgVar.type = 10;
            ArrayList arrayList = (ArrayList) obj;
            hg[] hgVarArr = new hg[arrayList.size()];
            Object obj2 = null;
            int size = arrayList.size();
            int i = 0;
            int i2 = 14;
            while (i < size) {
                Object obj3 = arrayList.get(i);
                hg zza = zza((List) list, obj3);
                if (zza.type == 14 || zza.type == 2 || zza.type == 6 || zza.type == 9) {
                    if (i2 == 14 && zza.type != 14) {
                        r1 = zza.type;
                    } else if (zza.type != i2) {
                        String valueOf = String.valueOf(obj2.getClass());
                        r0 = String.valueOf(obj3.getClass());
                        throw new IllegalArgumentException(new StringBuilder((String.valueOf(valueOf).length() + 80) + String.valueOf(r0).length()).append("ArrayList elements must all be of the sameclass, but this one contains a ").append(valueOf).append(" and a ").append(r0).toString());
                    } else {
                        obj3 = obj2;
                        r1 = i2;
                    }
                    hgVarArr[i] = zza;
                    i++;
                    i2 = r1;
                    obj2 = obj3;
                } else {
                    r0 = String.valueOf(obj3.getClass());
                    throw new IllegalArgumentException(new StringBuilder(String.valueOf(r0).length() + TsExtractor.TS_STREAM_TYPE_HDMV_DTS).append("The only ArrayList element types supported by DataBundleUtil are String, Integer, Bundle, and null, but this ArrayList contains a ").append(r0).toString());
                }
            }
            hgVar.zzbTL.zzbTV = hgVarArr;
        } else {
            String str = "newFieldValueFromValue: unexpected value ";
            r0 = String.valueOf(obj.getClass().getSimpleName());
            throw new RuntimeException(r0.length() != 0 ? str.concat(r0) : new String(str));
        }
        return hgVar;
    }

    public static DataMap zza(hd hdVar) {
        DataMap dataMap = new DataMap();
        for (hf hfVar : hdVar.zzbTF.zzbTH) {
            zza(hdVar.zzbTG, dataMap, hfVar.name, hfVar.zzbTJ);
        }
        return dataMap;
    }

    private static ArrayList zza(List<Asset> list, hh hhVar, int i) {
        ArrayList arrayList = new ArrayList(hhVar.zzbTV.length);
        for (hg hgVar : hhVar.zzbTV) {
            if (hgVar.type == 14) {
                arrayList.add(null);
            } else if (i == 9) {
                DataMap dataMap = new DataMap();
                for (hf hfVar : hgVar.zzbTL.zzbTU) {
                    zza(list, dataMap, hfVar.name, hfVar.zzbTJ);
                }
                arrayList.add(dataMap);
            } else if (i == 2) {
                arrayList.add(hgVar.zzbTL.zzbTN);
            } else if (i == 6) {
                arrayList.add(Integer.valueOf(hgVar.zzbTL.zzbTR));
            } else {
                throw new IllegalArgumentException("Unexpected typeOfArrayList: " + i);
            }
        }
        return arrayList;
    }

    private static void zza(List<Asset> list, DataMap dataMap, String str, hg hgVar) {
        int i = hgVar.type;
        if (i == 14) {
            dataMap.putString(str, null);
            return;
        }
        hh hhVar = hgVar.zzbTL;
        if (i == 1) {
            dataMap.putByteArray(str, hhVar.zzbTM);
        } else if (i == 11) {
            dataMap.putStringArray(str, hhVar.zzbTW);
        } else if (i == 12) {
            dataMap.putLongArray(str, hhVar.zzbTX);
        } else if (i == 15) {
            dataMap.putFloatArray(str, hhVar.zzbTY);
        } else if (i == 2) {
            dataMap.putString(str, hhVar.zzbTN);
        } else if (i == 3) {
            dataMap.putDouble(str, hhVar.zzbTO);
        } else if (i == 4) {
            dataMap.putFloat(str, hhVar.zzbTP);
        } else if (i == 5) {
            dataMap.putLong(str, hhVar.zzbTQ);
        } else if (i == 6) {
            dataMap.putInt(str, hhVar.zzbTR);
        } else if (i == 7) {
            dataMap.putByte(str, (byte) hhVar.zzbTS);
        } else if (i == 8) {
            dataMap.putBoolean(str, hhVar.zzbTT);
        } else if (i == 13) {
            if (list == null) {
                String str2 = "populateBundle: unexpected type for: ";
                String valueOf = String.valueOf(str);
                throw new RuntimeException(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            }
            dataMap.putAsset(str, (Asset) list.get((int) hhVar.zzbTZ));
        } else if (i == 9) {
            DataMap dataMap2 = new DataMap();
            for (hf hfVar : hhVar.zzbTU) {
                zza(list, dataMap2, hfVar.name, hfVar.zzbTJ);
            }
            dataMap.putDataMap(str, dataMap2);
        } else if (i == 10) {
            i = zza(str, hhVar.zzbTV);
            ArrayList zza = zza(list, hhVar, i);
            if (i == 14) {
                dataMap.putStringArrayList(str, zza);
            } else if (i == 9) {
                dataMap.putDataMapArrayList(str, zza);
            } else if (i == 2) {
                dataMap.putStringArrayList(str, zza);
            } else if (i == 6) {
                dataMap.putIntegerArrayList(str, zza);
            } else {
                throw new IllegalStateException("Unexpected typeOfArrayList: " + i);
            }
        } else {
            throw new RuntimeException("populateBundle: unexpected type " + i);
        }
    }

    private static hf[] zza(DataMap dataMap, List<Asset> list) {
        TreeSet treeSet = new TreeSet(dataMap.keySet());
        hf[] hfVarArr = new hf[treeSet.size()];
        Iterator it = treeSet.iterator();
        int i = 0;
        while (it.hasNext()) {
            String str = (String) it.next();
            Object obj = dataMap.get(str);
            hfVarArr[i] = new hf();
            hfVarArr[i].name = str;
            hfVarArr[i].zzbTJ = zza((List) list, obj);
            i++;
        }
        return hfVarArr;
    }
}

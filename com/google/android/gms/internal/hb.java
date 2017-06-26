package com.google.android.gms.internal;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public final class hb {
    private static int zza(String str, hf[] hfVarArr) {
        int i = 14;
        for (hf hfVar : hfVarArr) {
            if (i == 14) {
                if (hfVar.type == 9 || hfVar.type == 2 || hfVar.type == 6) {
                    i = hfVar.type;
                } else if (hfVar.type != 14) {
                    throw new IllegalArgumentException(new StringBuilder(String.valueOf(str).length() + 48).append("Unexpected TypedValue type: ").append(hfVar.type).append(" for key ").append(str).toString());
                }
            } else if (hfVar.type != i) {
                throw new IllegalArgumentException(new StringBuilder(String.valueOf(str).length() + 126).append("The ArrayList elements should all be the same type, but ArrayList with key ").append(str).append(" contains items of type ").append(i).append(" and ").append(hfVar.type).toString());
            }
        }
        return i;
    }

    public static hc zza(DataMap dataMap) {
        hd hdVar = new hd();
        List arrayList = new ArrayList();
        hdVar.zzbTF = zza(dataMap, arrayList);
        return new hc(hdVar, arrayList);
    }

    private static hf zza(List<Asset> list, Object obj) {
        hf hfVar = new hf();
        if (obj == null) {
            hfVar.type = 14;
            return hfVar;
        }
        hfVar.zzbTJ = new hg();
        if (obj instanceof String) {
            hfVar.type = 2;
            hfVar.zzbTJ.zzbTL = (String) obj;
        } else if (obj instanceof Integer) {
            hfVar.type = 6;
            hfVar.zzbTJ.zzbTP = ((Integer) obj).intValue();
        } else if (obj instanceof Long) {
            hfVar.type = 5;
            hfVar.zzbTJ.zzbTO = ((Long) obj).longValue();
        } else if (obj instanceof Double) {
            hfVar.type = 3;
            hfVar.zzbTJ.zzbTM = ((Double) obj).doubleValue();
        } else if (obj instanceof Float) {
            hfVar.type = 4;
            hfVar.zzbTJ.zzbTN = ((Float) obj).floatValue();
        } else if (obj instanceof Boolean) {
            hfVar.type = 8;
            hfVar.zzbTJ.zzbTR = ((Boolean) obj).booleanValue();
        } else if (obj instanceof Byte) {
            hfVar.type = 7;
            hfVar.zzbTJ.zzbTQ = ((Byte) obj).byteValue();
        } else if (obj instanceof byte[]) {
            hfVar.type = 1;
            hfVar.zzbTJ.zzbTK = (byte[]) obj;
        } else if (obj instanceof String[]) {
            hfVar.type = 11;
            hfVar.zzbTJ.zzbTU = (String[]) obj;
        } else if (obj instanceof long[]) {
            hfVar.type = 12;
            hfVar.zzbTJ.zzbTV = (long[]) obj;
        } else if (obj instanceof float[]) {
            hfVar.type = 15;
            hfVar.zzbTJ.zzbTW = (float[]) obj;
        } else if (obj instanceof Asset) {
            hfVar.type = 13;
            hg hgVar = hfVar.zzbTJ;
            list.add((Asset) obj);
            hgVar.zzbTX = (long) (list.size() - 1);
        } else if (obj instanceof DataMap) {
            hfVar.type = 9;
            DataMap dataMap = (DataMap) obj;
            TreeSet treeSet = new TreeSet(dataMap.keySet());
            he[] heVarArr = new he[treeSet.size()];
            Iterator it = treeSet.iterator();
            r1 = 0;
            while (it.hasNext()) {
                r0 = (String) it.next();
                heVarArr[r1] = new he();
                heVarArr[r1].name = r0;
                heVarArr[r1].zzbTH = zza((List) list, dataMap.get(r0));
                r1++;
            }
            hfVar.zzbTJ.zzbTS = heVarArr;
        } else if (obj instanceof ArrayList) {
            hfVar.type = 10;
            ArrayList arrayList = (ArrayList) obj;
            hf[] hfVarArr = new hf[arrayList.size()];
            Object obj2 = null;
            int size = arrayList.size();
            int i = 0;
            int i2 = 14;
            while (i < size) {
                Object obj3 = arrayList.get(i);
                hf zza = zza((List) list, obj3);
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
                    hfVarArr[i] = zza;
                    i++;
                    i2 = r1;
                    obj2 = obj3;
                } else {
                    r0 = String.valueOf(obj3.getClass());
                    throw new IllegalArgumentException(new StringBuilder(String.valueOf(r0).length() + TsExtractor.TS_STREAM_TYPE_HDMV_DTS).append("The only ArrayList element types supported by DataBundleUtil are String, Integer, Bundle, and null, but this ArrayList contains a ").append(r0).toString());
                }
            }
            hfVar.zzbTJ.zzbTT = hfVarArr;
        } else {
            String str = "newFieldValueFromValue: unexpected value ";
            r0 = String.valueOf(obj.getClass().getSimpleName());
            throw new RuntimeException(r0.length() != 0 ? str.concat(r0) : new String(str));
        }
        return hfVar;
    }

    public static DataMap zza(hc hcVar) {
        DataMap dataMap = new DataMap();
        for (he heVar : hcVar.zzbTD.zzbTF) {
            zza(hcVar.zzbTE, dataMap, heVar.name, heVar.zzbTH);
        }
        return dataMap;
    }

    private static ArrayList zza(List<Asset> list, hg hgVar, int i) {
        ArrayList arrayList = new ArrayList(hgVar.zzbTT.length);
        for (hf hfVar : hgVar.zzbTT) {
            if (hfVar.type == 14) {
                arrayList.add(null);
            } else if (i == 9) {
                DataMap dataMap = new DataMap();
                for (he heVar : hfVar.zzbTJ.zzbTS) {
                    zza(list, dataMap, heVar.name, heVar.zzbTH);
                }
                arrayList.add(dataMap);
            } else if (i == 2) {
                arrayList.add(hfVar.zzbTJ.zzbTL);
            } else if (i == 6) {
                arrayList.add(Integer.valueOf(hfVar.zzbTJ.zzbTP));
            } else {
                throw new IllegalArgumentException("Unexpected typeOfArrayList: " + i);
            }
        }
        return arrayList;
    }

    private static void zza(List<Asset> list, DataMap dataMap, String str, hf hfVar) {
        int i = hfVar.type;
        if (i == 14) {
            dataMap.putString(str, null);
            return;
        }
        hg hgVar = hfVar.zzbTJ;
        if (i == 1) {
            dataMap.putByteArray(str, hgVar.zzbTK);
        } else if (i == 11) {
            dataMap.putStringArray(str, hgVar.zzbTU);
        } else if (i == 12) {
            dataMap.putLongArray(str, hgVar.zzbTV);
        } else if (i == 15) {
            dataMap.putFloatArray(str, hgVar.zzbTW);
        } else if (i == 2) {
            dataMap.putString(str, hgVar.zzbTL);
        } else if (i == 3) {
            dataMap.putDouble(str, hgVar.zzbTM);
        } else if (i == 4) {
            dataMap.putFloat(str, hgVar.zzbTN);
        } else if (i == 5) {
            dataMap.putLong(str, hgVar.zzbTO);
        } else if (i == 6) {
            dataMap.putInt(str, hgVar.zzbTP);
        } else if (i == 7) {
            dataMap.putByte(str, (byte) hgVar.zzbTQ);
        } else if (i == 8) {
            dataMap.putBoolean(str, hgVar.zzbTR);
        } else if (i == 13) {
            if (list == null) {
                String str2 = "populateBundle: unexpected type for: ";
                String valueOf = String.valueOf(str);
                throw new RuntimeException(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            }
            dataMap.putAsset(str, (Asset) list.get((int) hgVar.zzbTX));
        } else if (i == 9) {
            DataMap dataMap2 = new DataMap();
            for (he heVar : hgVar.zzbTS) {
                zza(list, dataMap2, heVar.name, heVar.zzbTH);
            }
            dataMap.putDataMap(str, dataMap2);
        } else if (i == 10) {
            i = zza(str, hgVar.zzbTT);
            ArrayList zza = zza(list, hgVar, i);
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

    private static he[] zza(DataMap dataMap, List<Asset> list) {
        TreeSet treeSet = new TreeSet(dataMap.keySet());
        he[] heVarArr = new he[treeSet.size()];
        Iterator it = treeSet.iterator();
        int i = 0;
        while (it.hasNext()) {
            String str = (String) it.next();
            Object obj = dataMap.get(str);
            heVarArr[i] = new he();
            heVarArr[i].name = str;
            heVarArr[i].zzbTH = zza((List) list, obj);
            i++;
        }
        return heVarArr;
    }
}

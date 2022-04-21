package org.aspectj.lang.reflect;

import j$.util.DesugarCollections;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import org.aspectj.internal.lang.reflect.AjTypeImpl;

public class AjTypeSystem {
    private static Map<Class, WeakReference<AjType>> ajTypes = DesugarCollections.synchronizedMap(new WeakHashMap());

    public static <T> AjType<T> getAjType(Class<T> fromClass) {
        WeakReference<AjType> weakRefToAjType = ajTypes.get(fromClass);
        if (weakRefToAjType != null) {
            AjType<T> theAjType = (AjType) weakRefToAjType.get();
            if (theAjType != null) {
                return theAjType;
            }
            AjType<T> theAjType2 = new AjTypeImpl<>(fromClass);
            ajTypes.put(fromClass, new WeakReference(theAjType2));
            return theAjType2;
        }
        AjType<T> theAjType3 = new AjTypeImpl<>(fromClass);
        ajTypes.put(fromClass, new WeakReference(theAjType3));
        return theAjType3;
    }
}

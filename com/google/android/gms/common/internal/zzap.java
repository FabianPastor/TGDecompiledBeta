package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

public class zzap {
    public static String zza(String str, String str2, Context context, AttributeSet attributeSet, boolean z, boolean z2, String str3) {
        String attributeValue = attributeSet == null ? null : attributeSet.getAttributeValue(str, str2);
        if (attributeValue != null && attributeValue.startsWith("@string/") && z) {
            String substring = attributeValue.substring("@string/".length());
            String packageName = context.getPackageName();
            TypedValue typedValue = new TypedValue();
            try {
                context.getResources().getValue(new StringBuilder((String.valueOf(packageName).length() + 8) + String.valueOf(substring).length()).append(packageName).append(":string/").append(substring).toString(), typedValue, true);
            } catch (NotFoundException e) {
                Log.w(str3, new StringBuilder((String.valueOf(str2).length() + 30) + String.valueOf(attributeValue).length()).append("Could not find resource for ").append(str2).append(": ").append(attributeValue).toString());
            }
            if (typedValue.string != null) {
                attributeValue = typedValue.string.toString();
            } else {
                substring = String.valueOf(typedValue);
                Log.w(str3, new StringBuilder((String.valueOf(str2).length() + 28) + String.valueOf(substring).length()).append("Resource ").append(str2).append(" was not a string: ").append(substring).toString());
            }
        }
        if (z2 && attributeValue == null) {
            Log.w(str3, new StringBuilder(String.valueOf(str2).length() + 33).append("Required XML attribute \"").append(str2).append("\" missing").toString());
        }
        return attributeValue;
    }
}

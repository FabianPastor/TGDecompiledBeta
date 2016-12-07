package com.google.android.gms.common.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class zzg {
    public static final zzg BB = zza((CharSequence) "\t\n\u000b\f\r     　 ᠎ ").zza(zza(' ', ' '));
    public static final zzg BC = zza((CharSequence) "\t\n\u000b\f\r     　").zza(zza(' ', ' ')).zza(zza(' ', ' '));
    public static final zzg BD = zza('\u0000', '');
    public static final zzg BE;
    public static final zzg BF = zza('\t', '\r').zza(zza('\u001c', ' ')).zza(zzc(' ')).zza(zzc('᠎')).zza(zza(' ', ' ')).zza(zza(' ', '​')).zza(zza(' ', ' ')).zza(zzc(' ')).zza(zzc('　'));
    public static final zzg BG = new zzg() {
        public boolean zzd(char c) {
            return Character.isDigit(c);
        }
    };
    public static final zzg BH = new zzg() {
        public boolean zzd(char c) {
            return Character.isLetter(c);
        }
    };
    public static final zzg BI = new zzg() {
        public boolean zzd(char c) {
            return Character.isLetterOrDigit(c);
        }
    };
    public static final zzg BJ = new zzg() {
        public boolean zzd(char c) {
            return Character.isUpperCase(c);
        }
    };
    public static final zzg BK = new zzg() {
        public boolean zzd(char c) {
            return Character.isLowerCase(c);
        }
    };
    public static final zzg BL = zza('\u0000', '\u001f').zza(zza('', ''));
    public static final zzg BM = zza('\u0000', ' ').zza(zza('', ' ')).zza(zzc('­')).zza(zza('؀', '؃')).zza(zza((CharSequence) "۝܏ ឴឵᠎")).zza(zza(' ', '‏')).zza(zza(' ', ' ')).zza(zza(' ', '⁤')).zza(zza('⁪', '⁯')).zza(zzc('　')).zza(zza('?', '')).zza(zza((CharSequence) "﻿￹￺￻"));
    public static final zzg BN = zza('\u0000', 'ӹ').zza(zzc('־')).zza(zza('א', 'ת')).zza(zzc('׳')).zza(zzc('״')).zza(zza('؀', 'ۿ')).zza(zza('ݐ', 'ݿ')).zza(zza('฀', '๿')).zza(zza('Ḁ', '₯')).zza(zza('℀', '℺')).zza(zza('ﭐ', '﷿')).zza(zza('ﹰ', '﻿')).zza(zza('｡', 'ￜ'));
    public static final zzg BO = new zzg() {
        public zzg zza(zzg com_google_android_gms_common_internal_zzg) {
            zzac.zzy(com_google_android_gms_common_internal_zzg);
            return this;
        }

        public boolean zzb(CharSequence charSequence) {
            zzac.zzy(charSequence);
            return true;
        }

        public boolean zzd(char c) {
            return true;
        }
    };
    public static final zzg BP = new zzg() {
        public zzg zza(zzg com_google_android_gms_common_internal_zzg) {
            return (zzg) zzac.zzy(com_google_android_gms_common_internal_zzg);
        }

        public boolean zzb(CharSequence charSequence) {
            return charSequence.length() == 0;
        }

        public boolean zzd(char c) {
            return false;
        }
    };

    class AnonymousClass11 extends zzg {
        final /* synthetic */ char BV;

        AnonymousClass11(char c) {
            this.BV = c;
        }

        public zzg zza(zzg com_google_android_gms_common_internal_zzg) {
            return com_google_android_gms_common_internal_zzg.zzd(this.BV) ? com_google_android_gms_common_internal_zzg : super.zza(com_google_android_gms_common_internal_zzg);
        }

        public boolean zzd(char c) {
            return c == this.BV;
        }
    }

    class AnonymousClass2 extends zzg {
        final /* synthetic */ char BQ;
        final /* synthetic */ char BR;

        AnonymousClass2(char c, char c2) {
            this.BQ = c;
            this.BR = c2;
        }

        public boolean zzd(char c) {
            return c == this.BQ || c == this.BR;
        }
    }

    class AnonymousClass3 extends zzg {
        final /* synthetic */ char[] BS;

        AnonymousClass3(char[] cArr) {
            this.BS = cArr;
        }

        public boolean zzd(char c) {
            return Arrays.binarySearch(this.BS, c) >= 0;
        }
    }

    class AnonymousClass4 extends zzg {
        final /* synthetic */ char BT;
        final /* synthetic */ char BU;

        AnonymousClass4(char c, char c2) {
            this.BT = c;
            this.BU = c2;
        }

        public boolean zzd(char c) {
            return this.BT <= c && c <= this.BU;
        }
    }

    private static class zza extends zzg {
        List<zzg> BW;

        zza(List<zzg> list) {
            this.BW = list;
        }

        public zzg zza(zzg com_google_android_gms_common_internal_zzg) {
            List arrayList = new ArrayList(this.BW);
            arrayList.add((zzg) zzac.zzy(com_google_android_gms_common_internal_zzg));
            return new zza(arrayList);
        }

        public boolean zzd(char c) {
            for (zzg zzd : this.BW) {
                if (zzd.zzd(c)) {
                    return true;
                }
            }
            return false;
        }
    }

    static {
        zzg zza = zza('0', '9');
        zzg com_google_android_gms_common_internal_zzg = zza;
        for (char c : "٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０".toCharArray()) {
            com_google_android_gms_common_internal_zzg = com_google_android_gms_common_internal_zzg.zza(zza(c, (char) (c + 9)));
        }
        BE = com_google_android_gms_common_internal_zzg;
    }

    public static zzg zza(char c, char c2) {
        zzac.zzbs(c2 >= c);
        return new AnonymousClass4(c, c2);
    }

    public static zzg zza(CharSequence charSequence) {
        switch (charSequence.length()) {
            case 0:
                return BP;
            case 1:
                return zzc(charSequence.charAt(0));
            case 2:
                return new AnonymousClass2(charSequence.charAt(0), charSequence.charAt(1));
            default:
                char[] toCharArray = charSequence.toString().toCharArray();
                Arrays.sort(toCharArray);
                return new AnonymousClass3(toCharArray);
        }
    }

    public static zzg zzc(char c) {
        return new AnonymousClass11(c);
    }

    public zzg zza(zzg com_google_android_gms_common_internal_zzg) {
        return new zza(Arrays.asList(new zzg[]{this, (zzg) zzac.zzy(com_google_android_gms_common_internal_zzg)}));
    }

    public boolean zzb(CharSequence charSequence) {
        for (int length = charSequence.length() - 1; length >= 0; length--) {
            if (!zzd(charSequence.charAt(length))) {
                return false;
            }
        }
        return true;
    }

    public abstract boolean zzd(char c);
}

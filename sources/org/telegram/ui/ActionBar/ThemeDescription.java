package org.telegram.ui.ActionBar;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.SpannedString;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatBigEmptyView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.GroupCreateCheckBox;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.MessageBackgroundDrawable;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.VideoTimelineView;

public class ThemeDescription {
    public static int FLAG_AB_AM_BACKGROUND = 1048576;
    public static int FLAG_AB_AM_ITEMSCOLOR = 512;
    public static int FLAG_AB_AM_SELECTORCOLOR = 4194304;
    public static int FLAG_AB_AM_TOPBACKGROUND = 2097152;
    public static int FLAG_AB_ITEMSCOLOR = 64;
    public static int FLAG_AB_SEARCH = NUM;
    public static int FLAG_AB_SEARCHPLACEHOLDER = 67108864;
    public static int FLAG_AB_SELECTORCOLOR = 256;
    public static int FLAG_AB_SUBMENUBACKGROUND = Integer.MIN_VALUE;
    public static int FLAG_AB_SUBMENUITEM = NUM;
    public static int FLAG_AB_SUBTITLECOLOR = 1024;
    public static int FLAG_AB_TITLECOLOR = 128;
    public static int FLAG_BACKGROUND = 1;
    public static int FLAG_BACKGROUNDFILTER = 32;
    public static int FLAG_CELLBACKGROUNDCOLOR = 16;
    public static int FLAG_CHECKBOX = 8192;
    public static int FLAG_CHECKBOXCHECK = 16384;
    public static int FLAG_CHECKTAG = 262144;
    public static int FLAG_CURSORCOLOR = 16777216;
    public static int FLAG_DRAWABLESELECTEDSTATE = 65536;
    public static int FLAG_FASTSCROLL = 33554432;
    public static int FLAG_HINTTEXTCOLOR = 8388608;
    public static int FLAG_IMAGECOLOR = 8;
    public static int FLAG_LINKCOLOR = 2;
    public static int FLAG_LISTGLOWCOLOR = 32768;
    public static int FLAG_PROGRESSBAR = 2048;
    public static int FLAG_SECTIONS = 524288;
    public static int FLAG_SELECTOR = 4096;
    public static int FLAG_SELECTORWHITE = NUM;
    public static int FLAG_SERVICEBACKGROUND = NUM;
    public static int FLAG_TEXTCOLOR = 4;
    public static int FLAG_USEBACKGROUNDDRAWABLE = 131072;
    private int alphaOverride;
    private HashMap<String, Field> cachedFields;
    private int changeFlags;
    private int currentColor;
    private String currentKey;
    private int defaultColor;
    private ThemeDescriptionDelegate delegate;
    private Drawable[] drawablesToUpdate;
    private Class[] listClasses;
    private String[] listClassesFieldName;
    private String lottieLayerName;
    private HashMap<String, Boolean> notFoundCachedFields;
    private Paint[] paintToUpdate;
    private int previousColor;
    private boolean[] previousIsDefault;
    private View viewToInvalidate;

    public interface ThemeDescriptionDelegate {
        void didSetColor();
    }

    public ThemeDescription(View view, int i, Class[] clsArr, Paint[] paintArr, Drawable[] drawableArr, ThemeDescriptionDelegate themeDescriptionDelegate, String str, Object obj) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = str;
        this.paintToUpdate = paintArr;
        this.drawablesToUpdate = drawableArr;
        this.viewToInvalidate = view;
        this.changeFlags = i;
        this.listClasses = clsArr;
        this.delegate = themeDescriptionDelegate;
        View view2 = this.viewToInvalidate;
        if (view2 instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view2).getEditText();
        }
    }

    public ThemeDescription(View view, int i, Class[] clsArr, Paint paint, Drawable[] drawableArr, ThemeDescriptionDelegate themeDescriptionDelegate, String str) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = str;
        if (paint != null) {
            this.paintToUpdate = new Paint[]{paint};
        }
        this.drawablesToUpdate = drawableArr;
        this.viewToInvalidate = view;
        this.changeFlags = i;
        this.listClasses = clsArr;
        this.delegate = themeDescriptionDelegate;
        View view2 = this.viewToInvalidate;
        if (view2 instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view2).getEditText();
        }
    }

    public ThemeDescription(View view, int i, Class[] clsArr, RLottieDrawable[] rLottieDrawableArr, String str, String str2) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = str2;
        this.lottieLayerName = str;
        this.drawablesToUpdate = rLottieDrawableArr;
        this.viewToInvalidate = view;
        this.changeFlags = i;
        this.listClasses = clsArr;
        View view2 = this.viewToInvalidate;
        if (view2 instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view2).getEditText();
        }
    }

    public ThemeDescription(View view, int i, Class[] clsArr, String[] strArr, Paint[] paintArr, Drawable[] drawableArr, ThemeDescriptionDelegate themeDescriptionDelegate, String str) {
        this(view, i, clsArr, strArr, paintArr, drawableArr, -1, themeDescriptionDelegate, str);
    }

    public ThemeDescription(View view, int i, Class[] clsArr, String[] strArr, Paint[] paintArr, Drawable[] drawableArr, int i2, ThemeDescriptionDelegate themeDescriptionDelegate, String str) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = str;
        this.paintToUpdate = paintArr;
        this.drawablesToUpdate = drawableArr;
        this.viewToInvalidate = view;
        this.changeFlags = i;
        this.listClasses = clsArr;
        this.listClassesFieldName = strArr;
        this.alphaOverride = i2;
        this.delegate = themeDescriptionDelegate;
        this.cachedFields = new HashMap<>();
        this.notFoundCachedFields = new HashMap<>();
        View view2 = this.viewToInvalidate;
        if (view2 instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view2).getEditText();
        }
    }

    public ThemeDescription(View view, int i, Class[] clsArr, String[] strArr, String str, String str2) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = str2;
        this.lottieLayerName = str;
        this.viewToInvalidate = view;
        this.changeFlags = i;
        this.listClasses = clsArr;
        this.listClassesFieldName = strArr;
        this.cachedFields = new HashMap<>();
        this.notFoundCachedFields = new HashMap<>();
        View view2 = this.viewToInvalidate;
        if (view2 instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view2).getEditText();
        }
    }

    public ThemeDescriptionDelegate setDelegateDisabled() {
        ThemeDescriptionDelegate themeDescriptionDelegate = this.delegate;
        this.delegate = null;
        return themeDescriptionDelegate;
    }

    public void setColor(int i, boolean z) {
        setColor(i, z, true);
    }

    private boolean checkTag(String str, View view) {
        if (!(str == null || view == null)) {
            Object tag = view.getTag();
            if (tag instanceof String) {
                return ((String) tag).contains(str);
            }
        }
        return false;
    }

    public void setColor(int i, boolean z, boolean z2) {
        Class[] clsArr;
        Drawable[] compoundDrawables;
        Drawable background;
        if (z2) {
            Theme.setColor(this.currentKey, i, z);
        }
        this.currentColor = i;
        int i2 = this.alphaOverride;
        if (i2 > 0) {
            i = Color.argb(i2, Color.red(i), Color.green(i), Color.blue(i));
        }
        if (this.paintToUpdate != null) {
            int i3 = 0;
            while (true) {
                Paint[] paintArr = this.paintToUpdate;
                if (i3 >= paintArr.length) {
                    break;
                }
                if ((this.changeFlags & FLAG_LINKCOLOR) == 0 || !(paintArr[i3] instanceof TextPaint)) {
                    this.paintToUpdate[i3].setColor(i);
                } else {
                    ((TextPaint) paintArr[i3]).linkColor = i;
                }
                i3++;
            }
        }
        if (this.drawablesToUpdate != null) {
            int i4 = 0;
            while (true) {
                Drawable[] drawableArr = this.drawablesToUpdate;
                if (i4 >= drawableArr.length) {
                    break;
                }
                if (drawableArr[i4] != null) {
                    if (drawableArr[i4] instanceof ScamDrawable) {
                        ((ScamDrawable) drawableArr[i4]).setColor(i);
                    } else if (drawableArr[i4] instanceof RLottieDrawable) {
                        if (this.lottieLayerName != null) {
                            ((RLottieDrawable) drawableArr[i4]).setLayerColor(this.lottieLayerName + ".**", i);
                        }
                    } else if (drawableArr[i4] instanceof CombinedDrawable) {
                        if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                            ((CombinedDrawable) drawableArr[i4]).getBackground().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                        } else {
                            ((CombinedDrawable) drawableArr[i4]).getIcon().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                        }
                    } else if (drawableArr[i4] instanceof AvatarDrawable) {
                        ((AvatarDrawable) drawableArr[i4]).setColor(i);
                    } else {
                        drawableArr[i4].setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                    }
                }
                i4++;
            }
        }
        View view = this.viewToInvalidate;
        if (view != null && this.listClasses == null && this.listClassesFieldName == null && ((this.changeFlags & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, view))) {
            if ((this.changeFlags & FLAG_BACKGROUND) != 0) {
                Drawable background2 = this.viewToInvalidate.getBackground();
                if (background2 instanceof MessageBackgroundDrawable) {
                    ((MessageBackgroundDrawable) background2).setColor(i);
                } else {
                    this.viewToInvalidate.setBackgroundColor(i);
                }
            }
            int i5 = this.changeFlags;
            if ((FLAG_BACKGROUNDFILTER & i5) != 0) {
                if ((i5 & FLAG_PROGRESSBAR) != 0) {
                    View view2 = this.viewToInvalidate;
                    if (view2 instanceof EditTextBoldCursor) {
                        ((EditTextBoldCursor) view2).setErrorLineColor(i);
                    }
                } else {
                    Drawable background3 = this.viewToInvalidate.getBackground();
                    if (background3 instanceof CombinedDrawable) {
                        if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0) {
                            background3 = ((CombinedDrawable) background3).getBackground();
                        } else {
                            background3 = ((CombinedDrawable) background3).getIcon();
                        }
                    }
                    if (background3 != null) {
                        if ((background3 instanceof StateListDrawable) || (Build.VERSION.SDK_INT >= 21 && (background3 instanceof RippleDrawable))) {
                            Theme.setSelectorDrawableColor(background3, i, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                        } else if (background3 instanceof ShapeDrawable) {
                            ((ShapeDrawable) background3).getPaint().setColor(i);
                        } else {
                            background3.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                        }
                    }
                }
            }
        }
        View view3 = this.viewToInvalidate;
        if (view3 instanceof ActionBar) {
            if ((this.changeFlags & FLAG_AB_ITEMSCOLOR) != 0) {
                ((ActionBar) view3).setItemsColor(i, false);
            }
            if ((this.changeFlags & FLAG_AB_TITLECOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setTitleColor(i);
            }
            if ((this.changeFlags & FLAG_AB_SELECTORCOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setItemsBackgroundColor(i, false);
            }
            if ((this.changeFlags & FLAG_AB_AM_SELECTORCOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setItemsBackgroundColor(i, true);
            }
            if ((this.changeFlags & FLAG_AB_AM_ITEMSCOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setItemsColor(i, true);
            }
            if ((this.changeFlags & FLAG_AB_SUBTITLECOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setSubtitleColor(i);
            }
            if ((this.changeFlags & FLAG_AB_AM_BACKGROUND) != 0) {
                ((ActionBar) this.viewToInvalidate).setActionModeColor(i);
            }
            if ((this.changeFlags & FLAG_AB_AM_TOPBACKGROUND) != 0) {
                ((ActionBar) this.viewToInvalidate).setActionModeTopColor(i);
            }
            if ((this.changeFlags & FLAG_AB_SEARCHPLACEHOLDER) != 0) {
                ((ActionBar) this.viewToInvalidate).setSearchTextColor(i, true);
            }
            if ((this.changeFlags & FLAG_AB_SEARCH) != 0) {
                ((ActionBar) this.viewToInvalidate).setSearchTextColor(i, false);
            }
            int i6 = this.changeFlags;
            if ((FLAG_AB_SUBMENUITEM & i6) != 0) {
                ((ActionBar) this.viewToInvalidate).setPopupItemsColor(i, (i6 & FLAG_IMAGECOLOR) != 0, false);
            }
            if ((this.changeFlags & FLAG_AB_SUBMENUBACKGROUND) != 0) {
                ((ActionBar) this.viewToInvalidate).setPopupBackgroundColor(i, false);
            }
        }
        View view4 = this.viewToInvalidate;
        if (view4 instanceof VideoTimelineView) {
            ((VideoTimelineView) view4).setColor(i);
        }
        View view5 = this.viewToInvalidate;
        if (view5 instanceof EmptyTextProgressView) {
            int i7 = this.changeFlags;
            if ((FLAG_TEXTCOLOR & i7) != 0) {
                ((EmptyTextProgressView) view5).setTextColor(i);
            } else if ((i7 & FLAG_PROGRESSBAR) != 0) {
                ((EmptyTextProgressView) view5).setProgressBarColor(i);
            }
        }
        View view6 = this.viewToInvalidate;
        if (view6 instanceof RadialProgressView) {
            ((RadialProgressView) view6).setProgressColor(i);
        } else if (view6 instanceof LineProgressView) {
            if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
                ((LineProgressView) view6).setProgressColor(i);
            } else {
                ((LineProgressView) view6).setBackColor(i);
            }
        } else if (view6 instanceof ContextProgressView) {
            ((ContextProgressView) view6).updateColors();
        }
        int i8 = this.changeFlags;
        if ((FLAG_TEXTCOLOR & i8) != 0 && ((i8 & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, this.viewToInvalidate))) {
            View view7 = this.viewToInvalidate;
            if (view7 instanceof TextView) {
                ((TextView) view7).setTextColor(i);
            } else if (view7 instanceof NumberTextView) {
                ((NumberTextView) view7).setTextColor(i);
            } else if (view7 instanceof SimpleTextView) {
                ((SimpleTextView) view7).setTextColor(i);
            } else if (view7 instanceof ChatBigEmptyView) {
                ((ChatBigEmptyView) view7).setTextColor(i);
            }
        }
        if ((this.changeFlags & FLAG_CURSORCOLOR) != 0) {
            View view8 = this.viewToInvalidate;
            if (view8 instanceof EditTextBoldCursor) {
                ((EditTextBoldCursor) view8).setCursorColor(i);
            }
        }
        int i9 = this.changeFlags;
        if ((FLAG_HINTTEXTCOLOR & i9) != 0) {
            View view9 = this.viewToInvalidate;
            if (view9 instanceof EditTextBoldCursor) {
                if ((i9 & FLAG_PROGRESSBAR) != 0) {
                    ((EditTextBoldCursor) view9).setHeaderHintColor(i);
                } else {
                    ((EditTextBoldCursor) view9).setHintColor(i);
                }
            } else if (view9 instanceof EditText) {
                ((EditText) view9).setHintTextColor(i);
            }
        }
        View view10 = this.viewToInvalidate;
        if (!(view10 == null || (this.changeFlags & FLAG_SERVICEBACKGROUND) == 0 || (background = view10.getBackground()) == null)) {
            background.setColorFilter(Theme.colorFilter);
        }
        int i10 = this.changeFlags;
        if ((FLAG_IMAGECOLOR & i10) != 0 && ((i10 & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, this.viewToInvalidate))) {
            View view11 = this.viewToInvalidate;
            if (view11 instanceof ImageView) {
                if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0) {
                    Drawable drawable = ((ImageView) view11).getDrawable();
                    if ((drawable instanceof StateListDrawable) || (Build.VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable))) {
                        Theme.setSelectorDrawableColor(drawable, i, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                    }
                } else {
                    ((ImageView) view11).setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                }
            } else if (!(view11 instanceof BackupImageView)) {
                if (view11 instanceof SimpleTextView) {
                    ((SimpleTextView) view11).setSideDrawablesColor(i);
                } else if ((view11 instanceof TextView) && (compoundDrawables = ((TextView) view11).getCompoundDrawables()) != null) {
                    for (int i11 = 0; i11 < compoundDrawables.length; i11++) {
                        if (compoundDrawables[i11] != null) {
                            compoundDrawables[i11].setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                        }
                    }
                }
            }
        }
        View view12 = this.viewToInvalidate;
        if ((view12 instanceof ScrollView) && (this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
            AndroidUtilities.setScrollViewEdgeEffectColor((ScrollView) view12, i);
        }
        View view13 = this.viewToInvalidate;
        if ((view13 instanceof ViewPager) && (this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
            AndroidUtilities.setViewPagerEdgeEffectColor((ViewPager) view13, i);
        }
        View view14 = this.viewToInvalidate;
        if (view14 instanceof RecyclerListView) {
            RecyclerListView recyclerListView = (RecyclerListView) view14;
            if ((this.changeFlags & FLAG_SELECTOR) != 0 && this.currentKey.equals("listSelectorSDK21")) {
                recyclerListView.setListSelectorColor(i);
            }
            if ((this.changeFlags & FLAG_FASTSCROLL) != 0) {
                recyclerListView.updateFastScrollColors();
            }
            if ((this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
                recyclerListView.setGlowColor(i);
            }
            if ((this.changeFlags & FLAG_SECTIONS) != 0) {
                ArrayList<View> headers = recyclerListView.getHeaders();
                if (headers != null) {
                    for (int i12 = 0; i12 < headers.size(); i12++) {
                        processViewColor(headers.get(i12), i);
                    }
                }
                ArrayList<View> headersCache = recyclerListView.getHeadersCache();
                if (headersCache != null) {
                    for (int i13 = 0; i13 < headersCache.size(); i13++) {
                        processViewColor(headersCache.get(i13), i);
                    }
                }
                View pinnedHeader = recyclerListView.getPinnedHeader();
                if (pinnedHeader != null) {
                    processViewColor(pinnedHeader, i);
                }
            }
        } else if (view14 != null && ((clsArr = this.listClasses) == null || clsArr.length == 0)) {
            int i14 = this.changeFlags;
            if ((FLAG_SELECTOR & i14) != 0) {
                this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            } else if ((i14 & FLAG_SELECTORWHITE) != 0) {
                this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            }
        }
        if (this.listClasses != null) {
            View view15 = this.viewToInvalidate;
            if (view15 instanceof RecyclerListView) {
                RecyclerListView recyclerListView2 = (RecyclerListView) view15;
                recyclerListView2.getRecycledViewPool().clear();
                int hiddenChildCount = recyclerListView2.getHiddenChildCount();
                for (int i15 = 0; i15 < hiddenChildCount; i15++) {
                    processViewColor(recyclerListView2.getHiddenChildAt(i15), i);
                }
                int cachedChildCount = recyclerListView2.getCachedChildCount();
                for (int i16 = 0; i16 < cachedChildCount; i16++) {
                    processViewColor(recyclerListView2.getCachedChildAt(i16), i);
                }
                int attachedScrapChildCount = recyclerListView2.getAttachedScrapChildCount();
                for (int i17 = 0; i17 < attachedScrapChildCount; i17++) {
                    processViewColor(recyclerListView2.getAttachedScrapChildAt(i17), i);
                }
            }
            View view16 = this.viewToInvalidate;
            if (view16 instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view16;
                int childCount = viewGroup.getChildCount();
                for (int i18 = 0; i18 < childCount; i18++) {
                    processViewColor(viewGroup.getChildAt(i18), i);
                }
            }
            processViewColor(this.viewToInvalidate, i);
        }
        ThemeDescriptionDelegate themeDescriptionDelegate = this.delegate;
        if (themeDescriptionDelegate != null) {
            themeDescriptionDelegate.didSetColor();
        }
        View view17 = this.viewToInvalidate;
        if (view17 != null) {
            view17.invalidate();
        }
    }

    private void processViewColor(View view, int i) {
        boolean z;
        Object obj;
        TypefaceSpan[] typefaceSpanArr;
        int i2 = 0;
        while (true) {
            Class[] clsArr = this.listClasses;
            if (i2 < clsArr.length) {
                if (clsArr[i2].isInstance(view)) {
                    view.invalidate();
                    if ((this.changeFlags & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, view)) {
                        view.invalidate();
                        if (this.listClassesFieldName != null || (this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                            int i3 = this.changeFlags;
                            if ((FLAG_CELLBACKGROUNDCOLOR & i3) != 0) {
                                view.setBackgroundColor(i);
                            } else if ((FLAG_TEXTCOLOR & i3) != 0) {
                                if (view instanceof TextView) {
                                    ((TextView) view).setTextColor(i);
                                }
                            } else if ((FLAG_SERVICEBACKGROUND & i3) != 0) {
                                Drawable background = view.getBackground();
                                if (background != null) {
                                    background.setColorFilter(Theme.colorFilter);
                                }
                            } else if ((FLAG_SELECTOR & i3) != 0) {
                                view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            } else if ((i3 & FLAG_SELECTORWHITE) != 0) {
                                view.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                            }
                        } else {
                            Drawable background2 = view.getBackground();
                            if (background2 != null) {
                                if ((this.changeFlags & FLAG_CELLBACKGROUNDCOLOR) == 0) {
                                    if (background2 instanceof CombinedDrawable) {
                                        background2 = ((CombinedDrawable) background2).getIcon();
                                    } else if ((background2 instanceof StateListDrawable) || (Build.VERSION.SDK_INT >= 21 && (background2 instanceof RippleDrawable))) {
                                        Theme.setSelectorDrawableColor(background2, i, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                                    }
                                    background2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                } else if (background2 instanceof CombinedDrawable) {
                                    Drawable background3 = ((CombinedDrawable) background2).getBackground();
                                    if (background3 instanceof ColorDrawable) {
                                        ((ColorDrawable) background3).setColor(i);
                                    }
                                }
                            }
                        }
                        z = true;
                    } else {
                        z = false;
                    }
                    if (this.listClassesFieldName != null) {
                        String str = this.listClasses[i2] + "_" + this.listClassesFieldName[i2];
                        HashMap<String, Boolean> hashMap = this.notFoundCachedFields;
                        if (hashMap == null || !hashMap.containsKey(str)) {
                            try {
                                Field field = this.cachedFields.get(str);
                                if (field == null && (field = this.listClasses[i2].getDeclaredField(this.listClassesFieldName[i2])) != null) {
                                    field.setAccessible(true);
                                    this.cachedFields.put(str, field);
                                }
                                if (!(field == null || (obj = field.get(view)) == null)) {
                                    if (z || !(obj instanceof View) || checkTag(this.currentKey, (View) obj)) {
                                        if (obj instanceof View) {
                                            ((View) obj).invalidate();
                                        }
                                        if (this.lottieLayerName != null && (obj instanceof RLottieImageView)) {
                                            ((RLottieImageView) obj).setLayerColor(this.lottieLayerName + ".**", i);
                                        }
                                        Drawable drawable = obj;
                                        if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0) {
                                            boolean z2 = obj instanceof View;
                                            drawable = obj;
                                            if (z2) {
                                                drawable = ((View) obj).getBackground();
                                            }
                                        }
                                        if ((this.changeFlags & FLAG_BACKGROUND) != 0 && (drawable instanceof View)) {
                                            View view2 = (View) drawable;
                                            Drawable background4 = view2.getBackground();
                                            if (background4 instanceof MessageBackgroundDrawable) {
                                                ((MessageBackgroundDrawable) background4).setColor(i);
                                            } else {
                                                view2.setBackgroundColor(i);
                                            }
                                        } else if (drawable instanceof EditTextCaption) {
                                            if ((this.changeFlags & FLAG_HINTTEXTCOLOR) != 0) {
                                                ((EditTextCaption) drawable).setHintColor(i);
                                                ((EditTextCaption) drawable).setHintTextColor(i);
                                            } else if ((this.changeFlags & FLAG_CURSORCOLOR) != 0) {
                                                ((EditTextCaption) drawable).setCursorColor(i);
                                            } else {
                                                ((EditTextCaption) drawable).setTextColor(i);
                                            }
                                        } else if (drawable instanceof SimpleTextView) {
                                            if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
                                                ((SimpleTextView) drawable).setLinkTextColor(i);
                                            } else {
                                                ((SimpleTextView) drawable).setTextColor(i);
                                            }
                                        } else if (drawable instanceof TextView) {
                                            TextView textView = (TextView) drawable;
                                            if ((this.changeFlags & FLAG_IMAGECOLOR) != 0) {
                                                Drawable[] compoundDrawables = textView.getCompoundDrawables();
                                                if (compoundDrawables != null) {
                                                    for (int i4 = 0; i4 < compoundDrawables.length; i4++) {
                                                        if (compoundDrawables[i4] != null) {
                                                            compoundDrawables[i4].setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                                        }
                                                    }
                                                }
                                            } else if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
                                                textView.getPaint().linkColor = i;
                                                textView.invalidate();
                                            } else if ((this.changeFlags & FLAG_FASTSCROLL) != 0) {
                                                CharSequence text = textView.getText();
                                                if ((text instanceof SpannedString) && (typefaceSpanArr = (TypefaceSpan[]) ((SpannedString) text).getSpans(0, text.length(), TypefaceSpan.class)) != null && typefaceSpanArr.length > 0) {
                                                    for (TypefaceSpan color : typefaceSpanArr) {
                                                        color.setColor(i);
                                                    }
                                                }
                                            } else {
                                                textView.setTextColor(i);
                                            }
                                        } else if (drawable instanceof ImageView) {
                                            ImageView imageView = (ImageView) drawable;
                                            Drawable drawable2 = imageView.getDrawable();
                                            if (!(drawable2 instanceof CombinedDrawable)) {
                                                imageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                            } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                                ((CombinedDrawable) drawable2).getBackground().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                            } else {
                                                ((CombinedDrawable) drawable2).getIcon().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                            }
                                        } else if (drawable instanceof BackupImageView) {
                                            Drawable staticThumb = ((BackupImageView) drawable).getImageReceiver().getStaticThumb();
                                            if (staticThumb instanceof CombinedDrawable) {
                                                if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                                    ((CombinedDrawable) staticThumb).getBackground().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                                } else {
                                                    ((CombinedDrawable) staticThumb).getIcon().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                                }
                                            } else if (staticThumb != null) {
                                                staticThumb.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                            }
                                        } else if (drawable instanceof Drawable) {
                                            if (drawable instanceof LetterDrawable) {
                                                if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                                    ((LetterDrawable) drawable).setBackgroundColor(i);
                                                } else {
                                                    ((LetterDrawable) drawable).setColor(i);
                                                }
                                            } else if (!(drawable instanceof CombinedDrawable)) {
                                                if (!(drawable instanceof StateListDrawable)) {
                                                    if (Build.VERSION.SDK_INT < 21 || !(drawable instanceof RippleDrawable)) {
                                                        if (drawable instanceof GradientDrawable) {
                                                            ((GradientDrawable) drawable).setColor(i);
                                                        } else {
                                                            ((Drawable) drawable).setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                                        }
                                                    }
                                                }
                                                Theme.setSelectorDrawableColor((Drawable) drawable, i, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                                            } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                                ((CombinedDrawable) drawable).getBackground().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                            } else {
                                                ((CombinedDrawable) drawable).getIcon().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                            }
                                        } else if (drawable instanceof CheckBox) {
                                            if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                                ((CheckBox) drawable).setBackgroundColor(i);
                                            } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                                ((CheckBox) drawable).setCheckColor(i);
                                            }
                                        } else if (drawable instanceof GroupCreateCheckBox) {
                                            ((GroupCreateCheckBox) drawable).updateColors();
                                        } else if (drawable instanceof Integer) {
                                            field.set(view, Integer.valueOf(i));
                                        } else if (drawable instanceof RadioButton) {
                                            if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                                ((RadioButton) drawable).setBackgroundColor(i);
                                                ((RadioButton) drawable).invalidate();
                                            } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                                ((RadioButton) drawable).setCheckedColor(i);
                                                ((RadioButton) drawable).invalidate();
                                            }
                                        } else if (drawable instanceof TextPaint) {
                                            if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
                                                ((TextPaint) drawable).linkColor = i;
                                            } else {
                                                ((TextPaint) drawable).setColor(i);
                                            }
                                        } else if (drawable instanceof LineProgressView) {
                                            if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
                                                ((LineProgressView) drawable).setProgressColor(i);
                                            } else {
                                                ((LineProgressView) drawable).setBackColor(i);
                                            }
                                        } else if (drawable instanceof RadialProgressView) {
                                            ((RadialProgressView) drawable).setProgressColor(i);
                                        } else if (drawable instanceof Paint) {
                                            ((Paint) drawable).setColor(i);
                                        } else if (drawable instanceof SeekBarView) {
                                            if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
                                                ((SeekBarView) drawable).setOuterColor(i);
                                            } else {
                                                ((SeekBarView) drawable).setInnerColor(i);
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable th) {
                                FileLog.e(th);
                                this.notFoundCachedFields.put(str, true);
                            }
                        }
                    } else if (view instanceof GroupCreateSpan) {
                        ((GroupCreateSpan) view).updateColors();
                    }
                }
                i2++;
            } else {
                return;
            }
        }
    }

    public String getCurrentKey() {
        return this.currentKey;
    }

    public void startEditing() {
        int color = Theme.getColor(this.currentKey, this.previousIsDefault);
        this.previousColor = color;
        this.currentColor = color;
    }

    public int getCurrentColor() {
        return this.currentColor;
    }

    public int getSetColor() {
        return Theme.getColor(this.currentKey);
    }

    public void setDefaultColor() {
        setColor(Theme.getDefaultColor(this.currentKey), true);
    }

    public void setPreviousColor() {
        setColor(this.previousColor, this.previousIsDefault[0]);
    }

    public String getTitle() {
        return this.currentKey;
    }
}

package org.telegram.ui.ActionBar;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
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
        view = this.viewToInvalidate;
        if (view instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view).getEditText();
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
        view = this.viewToInvalidate;
        if (view instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view).getEditText();
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
        view = this.viewToInvalidate;
        if (view instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view).getEditText();
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
        this.cachedFields = new HashMap();
        this.notFoundCachedFields = new HashMap();
        view = this.viewToInvalidate;
        if (view instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view).getEditText();
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
        this.cachedFields = new HashMap();
        this.notFoundCachedFields = new HashMap();
        view = this.viewToInvalidate;
        if (view instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view).getEditText();
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
        Drawable background;
        RecyclerListView recyclerListView;
        int i2;
        if (z2) {
            Theme.setColor(this.currentKey, i, z);
        }
        this.currentColor = i;
        int i3 = this.alphaOverride;
        if (i3 > 0) {
            i = Color.argb(i3, Color.red(i), Color.green(i), Color.blue(i));
        }
        int i4 = 0;
        if (this.paintToUpdate != null) {
            i3 = 0;
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
            i3 = 0;
            while (true) {
                Drawable[] drawableArr = this.drawablesToUpdate;
                if (i3 >= drawableArr.length) {
                    break;
                }
                if (drawableArr[i3] != null) {
                    if (drawableArr[i3] instanceof ScamDrawable) {
                        ((ScamDrawable) drawableArr[i3]).setColor(i);
                    } else if (drawableArr[i3] instanceof RLottieDrawable) {
                        if (this.lottieLayerName != null) {
                            RLottieDrawable rLottieDrawable = (RLottieDrawable) drawableArr[i3];
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(this.lottieLayerName);
                            stringBuilder.append(".**");
                            rLottieDrawable.setLayerColor(stringBuilder.toString(), i);
                        }
                    } else if (drawableArr[i3] instanceof CombinedDrawable) {
                        if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                            ((CombinedDrawable) drawableArr[i3]).getBackground().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        } else {
                            ((CombinedDrawable) drawableArr[i3]).getIcon().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        }
                    } else if (drawableArr[i3] instanceof AvatarDrawable) {
                        ((AvatarDrawable) drawableArr[i3]).setColor(i);
                    } else {
                        drawableArr[i3].setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                    }
                }
                i3++;
            }
        }
        View view = this.viewToInvalidate;
        if (view != null && this.listClasses == null && this.listClassesFieldName == null && ((this.changeFlags & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, view))) {
            if ((this.changeFlags & FLAG_BACKGROUND) != 0) {
                background = this.viewToInvalidate.getBackground();
                if (background instanceof MessageBackgroundDrawable) {
                    ((MessageBackgroundDrawable) background).setColor(i);
                } else {
                    this.viewToInvalidate.setBackgroundColor(i);
                }
            }
            i3 = this.changeFlags;
            if ((FLAG_BACKGROUNDFILTER & i3) != 0) {
                if ((i3 & FLAG_PROGRESSBAR) != 0) {
                    view = this.viewToInvalidate;
                    if (view instanceof EditTextBoldCursor) {
                        ((EditTextBoldCursor) view).setErrorLineColor(i);
                    }
                } else {
                    background = this.viewToInvalidate.getBackground();
                    if (background instanceof CombinedDrawable) {
                        if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0) {
                            background = ((CombinedDrawable) background).getBackground();
                        } else {
                            background = ((CombinedDrawable) background).getIcon();
                        }
                    }
                    if (background != null) {
                        if ((background instanceof StateListDrawable) || (VERSION.SDK_INT >= 21 && (background instanceof RippleDrawable))) {
                            Theme.setSelectorDrawableColor(background, i, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                        } else if (background instanceof ShapeDrawable) {
                            ((ShapeDrawable) background).getPaint().setColor(i);
                        } else {
                            background.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        }
                    }
                }
            }
        }
        view = this.viewToInvalidate;
        if (view instanceof ActionBar) {
            if ((this.changeFlags & FLAG_AB_ITEMSCOLOR) != 0) {
                ((ActionBar) view).setItemsColor(i, false);
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
            i3 = this.changeFlags;
            if ((FLAG_AB_SUBMENUITEM & i3) != 0) {
                ((ActionBar) this.viewToInvalidate).setPopupItemsColor(i, (i3 & FLAG_IMAGECOLOR) != 0, false);
            }
            if ((this.changeFlags & FLAG_AB_SUBMENUBACKGROUND) != 0) {
                ((ActionBar) this.viewToInvalidate).setPopupBackgroundColor(i, false);
            }
        }
        view = this.viewToInvalidate;
        if (view instanceof VideoTimelineView) {
            ((VideoTimelineView) view).setColor(i);
        }
        view = this.viewToInvalidate;
        if (view instanceof EmptyTextProgressView) {
            int i5 = this.changeFlags;
            if ((FLAG_TEXTCOLOR & i5) != 0) {
                ((EmptyTextProgressView) view).setTextColor(i);
            } else if ((i5 & FLAG_PROGRESSBAR) != 0) {
                ((EmptyTextProgressView) view).setProgressBarColor(i);
            }
        }
        view = this.viewToInvalidate;
        if (view instanceof RadialProgressView) {
            ((RadialProgressView) view).setProgressColor(i);
        } else if (view instanceof LineProgressView) {
            if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
                ((LineProgressView) view).setProgressColor(i);
            } else {
                ((LineProgressView) view).setBackColor(i);
            }
        } else if (view instanceof ContextProgressView) {
            ((ContextProgressView) view).updateColors();
        }
        i3 = this.changeFlags;
        if ((FLAG_TEXTCOLOR & i3) != 0 && ((i3 & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, this.viewToInvalidate))) {
            view = this.viewToInvalidate;
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(i);
            } else if (view instanceof NumberTextView) {
                ((NumberTextView) view).setTextColor(i);
            } else if (view instanceof SimpleTextView) {
                ((SimpleTextView) view).setTextColor(i);
            } else if (view instanceof ChatBigEmptyView) {
                ((ChatBigEmptyView) view).setTextColor(i);
            }
        }
        if ((this.changeFlags & FLAG_CURSORCOLOR) != 0) {
            view = this.viewToInvalidate;
            if (view instanceof EditTextBoldCursor) {
                ((EditTextBoldCursor) view).setCursorColor(i);
            }
        }
        i3 = this.changeFlags;
        if ((FLAG_HINTTEXTCOLOR & i3) != 0) {
            View view2 = this.viewToInvalidate;
            if (view2 instanceof EditTextBoldCursor) {
                if ((i3 & FLAG_PROGRESSBAR) != 0) {
                    ((EditTextBoldCursor) view2).setHeaderHintColor(i);
                } else {
                    ((EditTextBoldCursor) view2).setHintColor(i);
                }
            } else if (view2 instanceof EditText) {
                ((EditText) view2).setHintTextColor(i);
            }
        }
        view = this.viewToInvalidate;
        if (!(view == null || (this.changeFlags & FLAG_SERVICEBACKGROUND) == 0)) {
            background = view.getBackground();
            if (background != null) {
                background.setColorFilter(Theme.colorFilter);
            }
        }
        i3 = this.changeFlags;
        if ((FLAG_IMAGECOLOR & i3) != 0 && ((i3 & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, this.viewToInvalidate))) {
            view = this.viewToInvalidate;
            if (view instanceof ImageView) {
                if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0) {
                    background = ((ImageView) view).getDrawable();
                    if ((background instanceof StateListDrawable) || (VERSION.SDK_INT >= 21 && (background instanceof RippleDrawable))) {
                        Theme.setSelectorDrawableColor(background, i, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                    }
                } else {
                    ((ImageView) view).setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                }
            } else if (!(view instanceof BackupImageView) && (view instanceof SimpleTextView)) {
                ((SimpleTextView) view).setSideDrawablesColor(i);
            }
        }
        view = this.viewToInvalidate;
        if ((view instanceof ScrollView) && (this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
            AndroidUtilities.setScrollViewEdgeEffectColor((ScrollView) view, i);
        }
        view = this.viewToInvalidate;
        if ((view instanceof ViewPager) && (this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
            AndroidUtilities.setViewPagerEdgeEffectColor((ViewPager) view, i);
        }
        view = this.viewToInvalidate;
        if (view instanceof RecyclerListView) {
            recyclerListView = (RecyclerListView) view;
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
                ArrayList headers = recyclerListView.getHeaders();
                if (headers != null) {
                    for (i2 = 0; i2 < headers.size(); i2++) {
                        processViewColor((View) headers.get(i2), i);
                    }
                }
                headers = recyclerListView.getHeadersCache();
                if (headers != null) {
                    for (i2 = 0; i2 < headers.size(); i2++) {
                        processViewColor((View) headers.get(i2), i);
                    }
                }
                view = recyclerListView.getPinnedHeader();
                if (view != null) {
                    processViewColor(view, i);
                }
            }
        } else if (view != null) {
            Class[] clsArr = this.listClasses;
            if (clsArr == null || clsArr.length == 0) {
                i3 = this.changeFlags;
                if ((FLAG_SELECTOR & i3) != 0) {
                    this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                } else if ((i3 & FLAG_SELECTORWHITE) != 0) {
                    this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                }
            }
        }
        if (this.listClasses != null) {
            int hiddenChildCount;
            view = this.viewToInvalidate;
            if (view instanceof RecyclerListView) {
                recyclerListView = (RecyclerListView) view;
                recyclerListView.getRecycledViewPool().clear();
                hiddenChildCount = recyclerListView.getHiddenChildCount();
                for (i2 = 0; i2 < hiddenChildCount; i2++) {
                    processViewColor(recyclerListView.getHiddenChildAt(i2), i);
                }
                hiddenChildCount = recyclerListView.getCachedChildCount();
                for (i2 = 0; i2 < hiddenChildCount; i2++) {
                    processViewColor(recyclerListView.getCachedChildAt(i2), i);
                }
                hiddenChildCount = recyclerListView.getAttachedScrapChildCount();
                for (i2 = 0; i2 < hiddenChildCount; i2++) {
                    processViewColor(recyclerListView.getAttachedScrapChildAt(i2), i);
                }
            }
            view = this.viewToInvalidate;
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                hiddenChildCount = viewGroup.getChildCount();
                while (i4 < hiddenChildCount) {
                    processViewColor(viewGroup.getChildAt(i4), i);
                    i4++;
                }
            }
            processViewColor(this.viewToInvalidate, i);
        }
        ThemeDescriptionDelegate themeDescriptionDelegate = this.delegate;
        if (themeDescriptionDelegate != null) {
            themeDescriptionDelegate.didSetColor();
        }
        View view3 = this.viewToInvalidate;
        if (view3 != null) {
            view3.invalidate();
        }
    }

    private void processViewColor(View view, int i) {
        int i2 = 0;
        while (true) {
            Class[] clsArr = this.listClasses;
            if (i2 < clsArr.length) {
                if (clsArr[i2].isInstance(view)) {
                    Drawable background;
                    Object obj;
                    view.invalidate();
                    if ((this.changeFlags & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, view)) {
                        view.invalidate();
                        int i3 = this.changeFlags;
                        if ((FLAG_BACKGROUNDFILTER & i3) != 0) {
                            background = view.getBackground();
                            if (background != null) {
                                if ((this.changeFlags & FLAG_CELLBACKGROUNDCOLOR) == 0) {
                                    if (background instanceof CombinedDrawable) {
                                        background = ((CombinedDrawable) background).getIcon();
                                    } else if ((background instanceof StateListDrawable) || (VERSION.SDK_INT >= 21 && (background instanceof RippleDrawable))) {
                                        Theme.setSelectorDrawableColor(background, i, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                                    }
                                    background.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                } else if (background instanceof CombinedDrawable) {
                                    background = ((CombinedDrawable) background).getBackground();
                                    if (background instanceof ColorDrawable) {
                                        ((ColorDrawable) background).setColor(i);
                                    }
                                }
                            }
                        } else if ((FLAG_CELLBACKGROUNDCOLOR & i3) != 0) {
                            view.setBackgroundColor(i);
                        } else if ((FLAG_TEXTCOLOR & i3) != 0) {
                            if (view instanceof TextView) {
                                ((TextView) view).setTextColor(i);
                            }
                        } else if ((FLAG_SERVICEBACKGROUND & i3) != 0) {
                            background = view.getBackground();
                            if (background != null) {
                                background.setColorFilter(Theme.colorFilter);
                            }
                        } else if ((FLAG_SELECTOR & i3) != 0) {
                            view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        } else if ((i3 & FLAG_SELECTORWHITE) != 0) {
                            view.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        }
                        obj = 1;
                    } else {
                        obj = null;
                    }
                    if (this.listClassesFieldName != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(this.listClasses[i2]);
                        stringBuilder.append("_");
                        stringBuilder.append(this.listClassesFieldName[i2]);
                        String stringBuilder2 = stringBuilder.toString();
                        HashMap hashMap = this.notFoundCachedFields;
                        if (hashMap == null || !hashMap.containsKey(stringBuilder2)) {
                            try {
                                Field field = (Field) this.cachedFields.get(stringBuilder2);
                                if (field == null) {
                                    field = this.listClasses[i2].getDeclaredField(this.listClassesFieldName[i2]);
                                    if (field != null) {
                                        field.setAccessible(true);
                                        this.cachedFields.put(stringBuilder2, field);
                                    }
                                }
                                if (field != null) {
                                    Object obj2 = field.get(view);
                                    if (obj2 != null) {
                                        if (obj != null || !(obj2 instanceof View) || checkTag(this.currentKey, (View) obj2)) {
                                            if (obj2 instanceof View) {
                                                ((View) obj2).invalidate();
                                            }
                                            if (this.lottieLayerName != null && (obj2 instanceof RLottieImageView)) {
                                                RLottieImageView rLottieImageView = (RLottieImageView) obj2;
                                                StringBuilder stringBuilder3 = new StringBuilder();
                                                stringBuilder3.append(this.lottieLayerName);
                                                stringBuilder3.append(".**");
                                                rLottieImageView.setLayerColor(stringBuilder3.toString(), i);
                                            }
                                            if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0 && (obj2 instanceof View)) {
                                                obj2 = ((View) obj2).getBackground();
                                            }
                                            if ((this.changeFlags & FLAG_BACKGROUND) != 0 && (obj2 instanceof View)) {
                                                View view2 = (View) obj2;
                                                background = view2.getBackground();
                                                if (background instanceof MessageBackgroundDrawable) {
                                                    ((MessageBackgroundDrawable) background).setColor(i);
                                                } else {
                                                    view2.setBackgroundColor(i);
                                                }
                                            } else if (obj2 instanceof EditTextCaption) {
                                                if ((this.changeFlags & FLAG_HINTTEXTCOLOR) != 0) {
                                                    ((EditTextCaption) obj2).setHintColor(i);
                                                    ((EditTextCaption) obj2).setHintTextColor(i);
                                                } else if ((this.changeFlags & FLAG_CURSORCOLOR) != 0) {
                                                    ((EditTextCaption) obj2).setCursorColor(i);
                                                } else {
                                                    ((EditTextCaption) obj2).setTextColor(i);
                                                }
                                            } else if (obj2 instanceof SimpleTextView) {
                                                if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
                                                    ((SimpleTextView) obj2).setLinkTextColor(i);
                                                } else {
                                                    ((SimpleTextView) obj2).setTextColor(i);
                                                }
                                            } else if (obj2 instanceof TextView) {
                                                TextView textView = (TextView) obj2;
                                                int i4;
                                                if ((this.changeFlags & FLAG_IMAGECOLOR) != 0) {
                                                    Drawable[] compoundDrawables = textView.getCompoundDrawables();
                                                    if (compoundDrawables != null) {
                                                        for (i4 = 0; i4 < compoundDrawables.length; i4++) {
                                                            if (compoundDrawables[i4] != null) {
                                                                compoundDrawables[i4].setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                            }
                                                        }
                                                    }
                                                } else if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
                                                    textView.getPaint().linkColor = i;
                                                    textView.invalidate();
                                                } else if ((this.changeFlags & FLAG_FASTSCROLL) != 0) {
                                                    CharSequence text = textView.getText();
                                                    if (text instanceof SpannedString) {
                                                        TypefaceSpan[] typefaceSpanArr = (TypefaceSpan[]) ((SpannedString) text).getSpans(0, text.length(), TypefaceSpan.class);
                                                        if (typefaceSpanArr != null && typefaceSpanArr.length > 0) {
                                                            for (TypefaceSpan color : typefaceSpanArr) {
                                                                color.setColor(i);
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    textView.setTextColor(i);
                                                }
                                            } else if (obj2 instanceof ImageView) {
                                                ((ImageView) obj2).setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                            } else if (obj2 instanceof BackupImageView) {
                                                background = ((BackupImageView) obj2).getImageReceiver().getStaticThumb();
                                                if (background instanceof CombinedDrawable) {
                                                    if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                                        ((CombinedDrawable) background).getBackground().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                    } else {
                                                        ((CombinedDrawable) background).getIcon().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                    }
                                                } else if (background != null) {
                                                    background.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                }
                                            } else if (obj2 instanceof Drawable) {
                                                if (obj2 instanceof LetterDrawable) {
                                                    if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                                        ((LetterDrawable) obj2).setBackgroundColor(i);
                                                    } else {
                                                        ((LetterDrawable) obj2).setColor(i);
                                                    }
                                                } else if (!(obj2 instanceof CombinedDrawable)) {
                                                    if (!(obj2 instanceof StateListDrawable)) {
                                                        if (VERSION.SDK_INT < 21 || !(obj2 instanceof RippleDrawable)) {
                                                            if (obj2 instanceof GradientDrawable) {
                                                                ((GradientDrawable) obj2).setColor(i);
                                                            } else {
                                                                ((Drawable) obj2).setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                            }
                                                        }
                                                    }
                                                    Theme.setSelectorDrawableColor((Drawable) obj2, i, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                                                } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                                    ((CombinedDrawable) obj2).getBackground().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                } else {
                                                    ((CombinedDrawable) obj2).getIcon().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                }
                                            } else if (obj2 instanceof CheckBox) {
                                                if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                                    ((CheckBox) obj2).setBackgroundColor(i);
                                                } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                                    ((CheckBox) obj2).setCheckColor(i);
                                                }
                                            } else if (obj2 instanceof GroupCreateCheckBox) {
                                                ((GroupCreateCheckBox) obj2).updateColors();
                                            } else if (obj2 instanceof Integer) {
                                                field.set(view, Integer.valueOf(i));
                                            } else if (obj2 instanceof RadioButton) {
                                                if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                                    ((RadioButton) obj2).setBackgroundColor(i);
                                                    ((RadioButton) obj2).invalidate();
                                                } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                                    ((RadioButton) obj2).setCheckedColor(i);
                                                    ((RadioButton) obj2).invalidate();
                                                }
                                            } else if (obj2 instanceof TextPaint) {
                                                if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
                                                    ((TextPaint) obj2).linkColor = i;
                                                } else {
                                                    ((TextPaint) obj2).setColor(i);
                                                }
                                            } else if (obj2 instanceof LineProgressView) {
                                                if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
                                                    ((LineProgressView) obj2).setProgressColor(i);
                                                } else {
                                                    ((LineProgressView) obj2).setBackColor(i);
                                                }
                                            } else if (obj2 instanceof RadialProgressView) {
                                                ((RadialProgressView) obj2).setProgressColor(i);
                                            } else if (obj2 instanceof Paint) {
                                                ((Paint) obj2).setColor(i);
                                            } else if (obj2 instanceof SeekBarView) {
                                                if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
                                                    ((SeekBarView) obj2).setOuterColor(i);
                                                } else {
                                                    ((SeekBarView) obj2).setInnerColor(i);
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable th) {
                                FileLog.e(th);
                                this.notFoundCachedFields.put(stringBuilder2, Boolean.valueOf(true));
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

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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedArrowDrawable;
import org.telegram.ui.Components.AudioPlayerAlert;
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
    public Theme.ResourcesProvider resourcesProvider;
    private View viewToInvalidate;

    public interface ThemeDescriptionDelegate {
        void didSetColor();

        void onAnimationProgress(float f);

        /* renamed from: org.telegram.ui.ActionBar.ThemeDescription$ThemeDescriptionDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onAnimationProgress(ThemeDescriptionDelegate _this, float progress) {
            }
        }
    }

    public ThemeDescription(View view, int flags, Class[] classes, Paint[] paint, Drawable[] drawables, ThemeDescriptionDelegate themeDescriptionDelegate, String key, Object unused) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = key;
        this.paintToUpdate = paint;
        this.drawablesToUpdate = drawables;
        this.viewToInvalidate = view;
        this.changeFlags = flags;
        this.listClasses = classes;
        this.delegate = themeDescriptionDelegate;
        if (view instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view).getEditText();
        }
    }

    public ThemeDescription(View view, int flags, Class[] classes, Paint paint, Drawable[] drawables, ThemeDescriptionDelegate themeDescriptionDelegate, String key) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = key;
        if (paint != null) {
            this.paintToUpdate = new Paint[]{paint};
        }
        this.drawablesToUpdate = drawables;
        this.viewToInvalidate = view;
        this.changeFlags = flags;
        this.listClasses = classes;
        this.delegate = themeDescriptionDelegate;
        if (view instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view).getEditText();
        }
    }

    public ThemeDescription(View view, int flags, Class[] classes, RLottieDrawable[] drawables, String layerName, String key) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = key;
        this.lottieLayerName = layerName;
        this.drawablesToUpdate = drawables;
        this.viewToInvalidate = view;
        this.changeFlags = flags;
        this.listClasses = classes;
        if (view instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view).getEditText();
        }
    }

    public ThemeDescription(View view, int flags, Class[] classes, String[] classesFields, Paint[] paint, Drawable[] drawables, ThemeDescriptionDelegate themeDescriptionDelegate, String key) {
        this(view, flags, classes, classesFields, paint, drawables, -1, themeDescriptionDelegate, key);
    }

    public ThemeDescription(View view, int flags, Class[] classes, String[] classesFields, Paint[] paint, Drawable[] drawables, int alpha, ThemeDescriptionDelegate themeDescriptionDelegate, String key) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = key;
        this.paintToUpdate = paint;
        this.drawablesToUpdate = drawables;
        this.viewToInvalidate = view;
        this.changeFlags = flags;
        this.listClasses = classes;
        this.listClassesFieldName = classesFields;
        this.alphaOverride = alpha;
        this.delegate = themeDescriptionDelegate;
        this.cachedFields = new HashMap<>();
        this.notFoundCachedFields = new HashMap<>();
        View view2 = this.viewToInvalidate;
        if (view2 instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view2).getEditText();
        }
    }

    public ThemeDescription(View view, int flags, Class[] classes, String[] classesFields, String layerName, String key) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = key;
        this.lottieLayerName = layerName;
        this.viewToInvalidate = view;
        this.changeFlags = flags;
        this.listClasses = classes;
        this.listClassesFieldName = classesFields;
        this.cachedFields = new HashMap<>();
        this.notFoundCachedFields = new HashMap<>();
        View view2 = this.viewToInvalidate;
        if (view2 instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view2).getEditText();
        }
    }

    public ThemeDescriptionDelegate setDelegateDisabled() {
        ThemeDescriptionDelegate oldDelegate = this.delegate;
        this.delegate = null;
        return oldDelegate;
    }

    public void setColor(int color, boolean useDefault) {
        setColor(color, useDefault, true);
    }

    private boolean checkTag(String key, View view) {
        if (key == null || view == null) {
            return false;
        }
        Object viewTag = view.getTag();
        if (viewTag instanceof String) {
            return ((String) viewTag).contains(key);
        }
        return false;
    }

    public void setColor(int color, boolean useDefault, boolean save) {
        Class[] clsArr;
        Drawable[] drawables;
        if (save) {
            Theme.setColor(this.currentKey, color, useDefault);
        }
        this.currentColor = color;
        int i = this.alphaOverride;
        if (i > 0) {
            color = Color.argb(i, Color.red(color), Color.green(color), Color.blue(color));
        }
        if (this.paintToUpdate != null) {
            int a = 0;
            while (true) {
                Paint[] paintArr = this.paintToUpdate;
                if (a >= paintArr.length) {
                    break;
                }
                if ((this.changeFlags & FLAG_LINKCOLOR) == 0 || !(paintArr[a] instanceof TextPaint)) {
                    paintArr[a].setColor(color);
                } else {
                    ((TextPaint) paintArr[a]).linkColor = color;
                }
                a++;
            }
        }
        if (this.drawablesToUpdate != null) {
            int a2 = 0;
            while (true) {
                Drawable[] drawableArr = this.drawablesToUpdate;
                if (a2 >= drawableArr.length) {
                    break;
                }
                if (drawableArr[a2] != null) {
                    if (drawableArr[a2] instanceof BackDrawable) {
                        ((BackDrawable) drawableArr[a2]).setColor(color);
                    } else if (drawableArr[a2] instanceof ScamDrawable) {
                        ((ScamDrawable) drawableArr[a2]).setColor(color);
                    } else if (drawableArr[a2] instanceof RLottieDrawable) {
                        if (this.lottieLayerName != null) {
                            ((RLottieDrawable) drawableArr[a2]).setLayerColor(this.lottieLayerName + ".**", color);
                        }
                    } else if (drawableArr[a2] instanceof CombinedDrawable) {
                        if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                            ((CombinedDrawable) drawableArr[a2]).getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                        } else {
                            ((CombinedDrawable) drawableArr[a2]).getIcon().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                        }
                    } else if (drawableArr[a2] instanceof AvatarDrawable) {
                        ((AvatarDrawable) drawableArr[a2]).setColor(color);
                    } else if (drawableArr[a2] instanceof AnimatedArrowDrawable) {
                        ((AnimatedArrowDrawable) drawableArr[a2]).setColor(color);
                    } else {
                        drawableArr[a2].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    }
                }
                a2++;
            }
        }
        View view = this.viewToInvalidate;
        if (view != null && this.listClasses == null && this.listClassesFieldName == null && ((this.changeFlags & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, view))) {
            if ((this.changeFlags & FLAG_BACKGROUND) != 0) {
                Drawable background = this.viewToInvalidate.getBackground();
                if (background instanceof MessageBackgroundDrawable) {
                    ((MessageBackgroundDrawable) background).setColor(color);
                } else {
                    this.viewToInvalidate.setBackgroundColor(color);
                }
            }
            int i2 = this.changeFlags;
            if ((FLAG_BACKGROUNDFILTER & i2) != 0) {
                if ((i2 & FLAG_PROGRESSBAR) != 0) {
                    View view2 = this.viewToInvalidate;
                    if (view2 instanceof EditTextBoldCursor) {
                        ((EditTextBoldCursor) view2).setErrorLineColor(color);
                    }
                } else {
                    Drawable drawable = this.viewToInvalidate.getBackground();
                    if (drawable instanceof CombinedDrawable) {
                        if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0) {
                            drawable = ((CombinedDrawable) drawable).getBackground();
                        } else {
                            drawable = ((CombinedDrawable) drawable).getIcon();
                        }
                    }
                    if (drawable != null) {
                        if ((drawable instanceof StateListDrawable) || (Build.VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable))) {
                            Theme.setSelectorDrawableColor(drawable, color, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                        } else if (drawable instanceof ShapeDrawable) {
                            ((ShapeDrawable) drawable).getPaint().setColor(color);
                        } else {
                            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                        }
                    }
                }
            }
        }
        View view3 = this.viewToInvalidate;
        if (view3 instanceof ActionBar) {
            if ((this.changeFlags & FLAG_AB_ITEMSCOLOR) != 0) {
                ((ActionBar) view3).setItemsColor(color, false);
            }
            if ((this.changeFlags & FLAG_AB_TITLECOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setTitleColor(color);
            }
            if ((this.changeFlags & FLAG_AB_SELECTORCOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setItemsBackgroundColor(color, false);
            }
            if ((this.changeFlags & FLAG_AB_AM_SELECTORCOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setItemsBackgroundColor(color, true);
            }
            if ((this.changeFlags & FLAG_AB_AM_ITEMSCOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setItemsColor(color, true);
            }
            if ((this.changeFlags & FLAG_AB_SUBTITLECOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setSubtitleColor(color);
            }
            if ((this.changeFlags & FLAG_AB_AM_BACKGROUND) != 0) {
                ((ActionBar) this.viewToInvalidate).setActionModeColor(color);
            }
            if ((this.changeFlags & FLAG_AB_AM_TOPBACKGROUND) != 0) {
                ((ActionBar) this.viewToInvalidate).setActionModeTopColor(color);
            }
            if ((this.changeFlags & FLAG_AB_SEARCHPLACEHOLDER) != 0) {
                ((ActionBar) this.viewToInvalidate).setSearchTextColor(color, true);
            }
            if ((this.changeFlags & FLAG_AB_SEARCH) != 0) {
                ((ActionBar) this.viewToInvalidate).setSearchTextColor(color, false);
            }
            int i3 = this.changeFlags;
            if ((FLAG_AB_SUBMENUITEM & i3) != 0) {
                ((ActionBar) this.viewToInvalidate).setPopupItemsColor(color, (i3 & FLAG_IMAGECOLOR) != 0, false);
            }
            if ((this.changeFlags & FLAG_AB_SUBMENUBACKGROUND) != 0) {
                ((ActionBar) this.viewToInvalidate).setPopupBackgroundColor(color, false);
            }
        }
        View view4 = this.viewToInvalidate;
        if (view4 instanceof VideoTimelineView) {
            ((VideoTimelineView) view4).setColor(color);
        }
        View view5 = this.viewToInvalidate;
        if (view5 instanceof EmptyTextProgressView) {
            int i4 = this.changeFlags;
            if ((FLAG_TEXTCOLOR & i4) != 0) {
                ((EmptyTextProgressView) view5).setTextColor(color);
            } else if ((i4 & FLAG_PROGRESSBAR) != 0) {
                ((EmptyTextProgressView) view5).setProgressBarColor(color);
            }
        }
        View view6 = this.viewToInvalidate;
        if (view6 instanceof RadialProgressView) {
            ((RadialProgressView) view6).setProgressColor(color);
        } else if (view6 instanceof LineProgressView) {
            if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
                ((LineProgressView) view6).setProgressColor(color);
            } else {
                ((LineProgressView) view6).setBackColor(color);
            }
        } else if (view6 instanceof ContextProgressView) {
            ((ContextProgressView) view6).updateColors();
        } else if ((view6 instanceof SeekBarView) && (this.changeFlags & FLAG_PROGRESSBAR) != 0) {
            ((SeekBarView) view6).setOuterColor(color);
        }
        int i5 = this.changeFlags;
        if ((FLAG_TEXTCOLOR & i5) != 0 && ((i5 & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, this.viewToInvalidate))) {
            View view7 = this.viewToInvalidate;
            if (view7 instanceof TextView) {
                ((TextView) view7).setTextColor(color);
            } else if (view7 instanceof NumberTextView) {
                ((NumberTextView) view7).setTextColor(color);
            } else if (view7 instanceof SimpleTextView) {
                ((SimpleTextView) view7).setTextColor(color);
            } else if (view7 instanceof ChatBigEmptyView) {
                ((ChatBigEmptyView) view7).setTextColor(color);
            }
        }
        if ((this.changeFlags & FLAG_CURSORCOLOR) != 0) {
            View view8 = this.viewToInvalidate;
            if (view8 instanceof EditTextBoldCursor) {
                ((EditTextBoldCursor) view8).setCursorColor(color);
            }
        }
        int i6 = this.changeFlags;
        if ((FLAG_HINTTEXTCOLOR & i6) != 0) {
            View view9 = this.viewToInvalidate;
            if (view9 instanceof EditTextBoldCursor) {
                if ((i6 & FLAG_PROGRESSBAR) != 0) {
                    ((EditTextBoldCursor) view9).setHeaderHintColor(color);
                } else {
                    ((EditTextBoldCursor) view9).setHintColor(color);
                }
            } else if (view9 instanceof EditText) {
                ((EditText) view9).setHintTextColor(color);
            }
        }
        View view10 = this.viewToInvalidate;
        int i7 = this.changeFlags;
        if ((FLAG_IMAGECOLOR & i7) != 0 && ((i7 & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, view10))) {
            View view11 = this.viewToInvalidate;
            if (view11 instanceof ImageView) {
                if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0) {
                    Drawable drawable2 = ((ImageView) view11).getDrawable();
                    if ((drawable2 instanceof StateListDrawable) || (Build.VERSION.SDK_INT >= 21 && (drawable2 instanceof RippleDrawable))) {
                        Theme.setSelectorDrawableColor(drawable2, color, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                    }
                } else {
                    ((ImageView) view11).setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                }
            } else if (!(view11 instanceof BackupImageView)) {
                if (view11 instanceof SimpleTextView) {
                    ((SimpleTextView) view11).setSideDrawablesColor(color);
                } else if ((view11 instanceof TextView) && (drawables = ((TextView) view11).getCompoundDrawables()) != null) {
                    for (int a3 = 0; a3 < drawables.length; a3++) {
                        if (drawables[a3] != null) {
                            drawables[a3].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                        }
                    }
                }
            }
        }
        View view12 = this.viewToInvalidate;
        if ((view12 instanceof ScrollView) && (this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
            AndroidUtilities.setScrollViewEdgeEffectColor((ScrollView) view12, color);
        }
        View view13 = this.viewToInvalidate;
        if ((view13 instanceof ViewPager) && (this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
            AndroidUtilities.setViewPagerEdgeEffectColor((ViewPager) view13, color);
        }
        View view14 = this.viewToInvalidate;
        if (view14 instanceof RecyclerListView) {
            RecyclerListView recyclerListView = (RecyclerListView) view14;
            if ((this.changeFlags & FLAG_SELECTOR) != 0) {
                recyclerListView.setListSelectorColor(color);
            }
            if ((this.changeFlags & FLAG_FASTSCROLL) != 0) {
                recyclerListView.updateFastScrollColors();
            }
            if ((this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
                recyclerListView.setGlowColor(color);
            }
            if ((this.changeFlags & FLAG_SECTIONS) != 0) {
                ArrayList<View> headers = recyclerListView.getHeaders();
                if (headers != null) {
                    for (int a4 = 0; a4 < headers.size(); a4++) {
                        processViewColor(headers.get(a4), color);
                    }
                }
                ArrayList<View> headers2 = recyclerListView.getHeadersCache();
                if (headers2 != null) {
                    for (int a5 = 0; a5 < headers2.size(); a5++) {
                        processViewColor(headers2.get(a5), color);
                    }
                }
                View header = recyclerListView.getPinnedHeader();
                if (header != null) {
                    processViewColor(header, color);
                }
            }
        } else if (view14 != null && ((clsArr = this.listClasses) == null || clsArr.length == 0)) {
            int i8 = this.changeFlags;
            if ((FLAG_SELECTOR & i8) != 0) {
                view14.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            } else if ((i8 & FLAG_SELECTORWHITE) != 0) {
                view14.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            }
        }
        if (this.listClasses != null) {
            View view15 = this.viewToInvalidate;
            if (view15 instanceof RecyclerListView) {
                RecyclerListView recyclerListView2 = (RecyclerListView) view15;
                recyclerListView2.getRecycledViewPool().clear();
                int count = recyclerListView2.getHiddenChildCount();
                for (int a6 = 0; a6 < count; a6++) {
                    processViewColor(recyclerListView2.getHiddenChildAt(a6), color);
                }
                int count2 = recyclerListView2.getCachedChildCount();
                for (int a7 = 0; a7 < count2; a7++) {
                    processViewColor(recyclerListView2.getCachedChildAt(a7), color);
                }
                int count3 = recyclerListView2.getAttachedScrapChildCount();
                for (int a8 = 0; a8 < count3; a8++) {
                    processViewColor(recyclerListView2.getAttachedScrapChildAt(a8), color);
                }
            }
            View view16 = this.viewToInvalidate;
            if (view16 instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view16;
                int count4 = viewGroup.getChildCount();
                for (int a9 = 0; a9 < count4; a9++) {
                    processViewColor(viewGroup.getChildAt(a9), color);
                }
            }
            processViewColor(this.viewToInvalidate, color);
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

    private void processViewColor(View child, int color) {
        boolean passedCheck;
        Object object;
        TypefaceSpan[] spans;
        TypefaceSpan[] spans2;
        TypefaceSpan[] spans3;
        View view = child;
        int i = color;
        int b = 0;
        while (true) {
            Class[] clsArr = this.listClasses;
            if (b < clsArr.length) {
                if (clsArr[b].isInstance(view)) {
                    child.invalidate();
                    boolean z = false;
                    if ((this.changeFlags & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, view)) {
                        child.invalidate();
                        if (this.listClassesFieldName != null || (this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                            int i2 = this.changeFlags;
                            if ((FLAG_CELLBACKGROUNDCOLOR & i2) != 0) {
                                child.setBackgroundColor(color);
                            } else if ((FLAG_TEXTCOLOR & i2) != 0) {
                                if (view instanceof TextView) {
                                    ((TextView) view).setTextColor(i);
                                } else if (view instanceof AudioPlayerAlert.ClippingTextViewSwitcher) {
                                    int i3 = 0;
                                    while (i3 < 2) {
                                        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = (AudioPlayerAlert.ClippingTextViewSwitcher) view;
                                        TextView textView = i3 == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
                                        if (textView != null) {
                                            textView.setTextColor(i);
                                        }
                                        i3++;
                                    }
                                }
                            } else if ((FLAG_SERVICEBACKGROUND & i2) == 0) {
                                if ((FLAG_SELECTOR & i2) != 0) {
                                    view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                } else if ((i2 & FLAG_SELECTORWHITE) != 0) {
                                    view.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                                }
                            }
                        } else {
                            Drawable drawable = child.getBackground();
                            if (drawable != null) {
                                if ((this.changeFlags & FLAG_CELLBACKGROUNDCOLOR) == 0) {
                                    if (drawable instanceof CombinedDrawable) {
                                        drawable = ((CombinedDrawable) drawable).getIcon();
                                    } else if ((drawable instanceof StateListDrawable) || (Build.VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable))) {
                                        Theme.setSelectorDrawableColor(drawable, i, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                                    }
                                    drawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                } else if (drawable instanceof CombinedDrawable) {
                                    Drawable back = ((CombinedDrawable) drawable).getBackground();
                                    if (back instanceof ColorDrawable) {
                                        ((ColorDrawable) back).setColor(i);
                                    }
                                }
                            }
                        }
                        passedCheck = true;
                    } else {
                        passedCheck = false;
                    }
                    if (this.listClassesFieldName != null) {
                        String key = this.listClasses[b] + "_" + this.listClassesFieldName[b];
                        HashMap<String, Boolean> hashMap = this.notFoundCachedFields;
                        if (hashMap == null || !hashMap.containsKey(key)) {
                            try {
                                Field field = this.cachedFields.get(key);
                                if (field == null && (field = this.listClasses[b].getDeclaredField(this.listClassesFieldName[b])) != null) {
                                    field.setAccessible(true);
                                    this.cachedFields.put(key, field);
                                }
                                if (!(field == null || (object = field.get(view)) == null)) {
                                    if (passedCheck || !(object instanceof View) || checkTag(this.currentKey, (View) object)) {
                                        if (object instanceof View) {
                                            ((View) object).invalidate();
                                        }
                                        if (this.lottieLayerName != null && (object instanceof RLottieImageView)) {
                                            ((RLottieImageView) object).setLayerColor(this.lottieLayerName + ".**", i);
                                        }
                                        if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0 && (object instanceof View)) {
                                            object = ((View) object).getBackground();
                                        }
                                        int i4 = this.changeFlags;
                                        if ((FLAG_BACKGROUND & i4) != 0 && (object instanceof View)) {
                                            View view2 = (View) object;
                                            Drawable background = view2.getBackground();
                                            if (background instanceof MessageBackgroundDrawable) {
                                                ((MessageBackgroundDrawable) background).setColor(i);
                                            } else {
                                                view2.setBackgroundColor(i);
                                            }
                                        } else if (object instanceof EditTextCaption) {
                                            if ((FLAG_HINTTEXTCOLOR & i4) != 0) {
                                                ((EditTextCaption) object).setHintColor(i);
                                                ((EditTextCaption) object).setHintTextColor(i);
                                            } else if ((FLAG_CURSORCOLOR & i4) != 0) {
                                                ((EditTextCaption) object).setCursorColor(i);
                                            } else {
                                                ((EditTextCaption) object).setTextColor(i);
                                            }
                                        } else if (object instanceof SimpleTextView) {
                                            if ((FLAG_LINKCOLOR & i4) != 0) {
                                                ((SimpleTextView) object).setLinkTextColor(i);
                                            } else {
                                                ((SimpleTextView) object).setTextColor(i);
                                            }
                                        } else if (object instanceof TextView) {
                                            TextView textView2 = (TextView) object;
                                            if ((FLAG_IMAGECOLOR & i4) != 0) {
                                                Drawable[] drawables = textView2.getCompoundDrawables();
                                                if (drawables != null) {
                                                    for (int a = 0; a < drawables.length; a++) {
                                                        if (drawables[a] != null) {
                                                            drawables[a].setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                                        }
                                                    }
                                                }
                                            } else if ((FLAG_LINKCOLOR & i4) != 0) {
                                                textView2.getPaint().linkColor = i;
                                                textView2.invalidate();
                                            } else if ((FLAG_FASTSCROLL & i4) != 0) {
                                                CharSequence text = textView2.getText();
                                                if ((text instanceof SpannedString) && (spans3 = (TypefaceSpan[]) ((SpannedString) text).getSpans(0, text.length(), TypefaceSpan.class)) != null && spans3.length > 0) {
                                                    for (TypefaceSpan color2 : spans3) {
                                                        color2.setColor(i);
                                                    }
                                                }
                                            } else {
                                                textView2.setTextColor(i);
                                            }
                                        } else if (object instanceof ImageView) {
                                            ImageView imageView = (ImageView) object;
                                            Drawable drawable2 = imageView.getDrawable();
                                            if (!(drawable2 instanceof CombinedDrawable)) {
                                                imageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                            } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                                ((CombinedDrawable) drawable2).getBackground().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                            } else {
                                                ((CombinedDrawable) drawable2).getIcon().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                            }
                                        } else if (object instanceof BackupImageView) {
                                            Drawable drawable3 = ((BackupImageView) object).getImageReceiver().getStaticThumb();
                                            if (drawable3 instanceof CombinedDrawable) {
                                                if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                                    ((CombinedDrawable) drawable3).getBackground().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                                } else {
                                                    ((CombinedDrawable) drawable3).getIcon().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                                }
                                            } else if (drawable3 != null) {
                                                drawable3.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                            }
                                        } else if (object instanceof Drawable) {
                                            if (object instanceof LetterDrawable) {
                                                if ((FLAG_BACKGROUNDFILTER & i4) != 0) {
                                                    ((LetterDrawable) object).setBackgroundColor(i);
                                                } else {
                                                    ((LetterDrawable) object).setColor(i);
                                                }
                                            } else if (!(object instanceof CombinedDrawable)) {
                                                if (!(object instanceof StateListDrawable)) {
                                                    if (Build.VERSION.SDK_INT < 21 || !(object instanceof RippleDrawable)) {
                                                        if (object instanceof GradientDrawable) {
                                                            ((GradientDrawable) object).setColor(i);
                                                        } else {
                                                            ((Drawable) object).setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                                        }
                                                    }
                                                }
                                                Drawable drawable4 = (Drawable) object;
                                                if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0) {
                                                    z = true;
                                                }
                                                Theme.setSelectorDrawableColor(drawable4, i, z);
                                            } else if ((FLAG_BACKGROUNDFILTER & i4) != 0) {
                                                ((CombinedDrawable) object).getBackground().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                            } else {
                                                ((CombinedDrawable) object).getIcon().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                                            }
                                        } else if (object instanceof CheckBox) {
                                            if ((FLAG_CHECKBOX & i4) != 0) {
                                                ((CheckBox) object).setBackgroundColor(i);
                                            } else if ((FLAG_CHECKBOXCHECK & i4) != 0) {
                                                ((CheckBox) object).setCheckColor(i);
                                            }
                                        } else if (object instanceof GroupCreateCheckBox) {
                                            ((GroupCreateCheckBox) object).updateColors();
                                        } else if (object instanceof Integer) {
                                            field.set(view, Integer.valueOf(color));
                                        } else if (object instanceof RadioButton) {
                                            if ((FLAG_CHECKBOX & i4) != 0) {
                                                ((RadioButton) object).setBackgroundColor(i);
                                                ((RadioButton) object).invalidate();
                                            } else if ((FLAG_CHECKBOXCHECK & i4) != 0) {
                                                ((RadioButton) object).setCheckedColor(i);
                                                ((RadioButton) object).invalidate();
                                            }
                                        } else if (object instanceof TextPaint) {
                                            if ((FLAG_LINKCOLOR & i4) != 0) {
                                                ((TextPaint) object).linkColor = i;
                                            } else {
                                                ((TextPaint) object).setColor(i);
                                            }
                                        } else if (object instanceof LineProgressView) {
                                            if ((FLAG_PROGRESSBAR & i4) != 0) {
                                                ((LineProgressView) object).setProgressColor(i);
                                            } else {
                                                ((LineProgressView) object).setBackColor(i);
                                            }
                                        } else if (object instanceof RadialProgressView) {
                                            ((RadialProgressView) object).setProgressColor(i);
                                        } else if (object instanceof Paint) {
                                            ((Paint) object).setColor(i);
                                            child.invalidate();
                                        } else if (object instanceof SeekBarView) {
                                            if ((FLAG_PROGRESSBAR & i4) != 0) {
                                                ((SeekBarView) object).setOuterColor(i);
                                            } else {
                                                ((SeekBarView) object).setInnerColor(i);
                                            }
                                        } else if (object instanceof AudioPlayerAlert.ClippingTextViewSwitcher) {
                                            if ((FLAG_FASTSCROLL & i4) != 0) {
                                                int k = 0;
                                                while (k < 2) {
                                                    TextView textView3 = k == 0 ? ((AudioPlayerAlert.ClippingTextViewSwitcher) object).getTextView() : ((AudioPlayerAlert.ClippingTextViewSwitcher) object).getNextTextView();
                                                    if (textView3 != null) {
                                                        CharSequence text2 = textView3.getText();
                                                        if ((text2 instanceof SpannedString) && (spans2 = (TypefaceSpan[]) ((SpannedString) text2).getSpans(0, text2.length(), TypefaceSpan.class)) != null && spans2.length > 0) {
                                                            for (TypefaceSpan color3 : spans2) {
                                                                color3.setColor(i);
                                                            }
                                                        }
                                                    }
                                                    k++;
                                                }
                                            } else if ((FLAG_TEXTCOLOR & i4) != 0 && ((FLAG_CHECKTAG & i4) == 0 || checkTag(this.currentKey, (View) object))) {
                                                int i5 = 0;
                                                while (i5 < 2) {
                                                    TextView textView4 = i5 == 0 ? ((AudioPlayerAlert.ClippingTextViewSwitcher) object).getTextView() : ((AudioPlayerAlert.ClippingTextViewSwitcher) object).getNextTextView();
                                                    if (textView4 != null) {
                                                        textView4.setTextColor(i);
                                                        CharSequence text3 = textView4.getText();
                                                        if ((text3 instanceof SpannedString) && (spans = (TypefaceSpan[]) ((SpannedString) text3).getSpans(0, text3.length(), TypefaceSpan.class)) != null && spans.length > 0) {
                                                            for (TypefaceSpan color4 : spans) {
                                                                color4.setColor(i);
                                                            }
                                                        }
                                                    }
                                                    i5++;
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable e) {
                                FileLog.e(e);
                                this.notFoundCachedFields.put(key, true);
                            }
                        }
                    } else if (view instanceof GroupCreateSpan) {
                        ((GroupCreateSpan) view).updateColors();
                    }
                }
                b++;
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
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(this.currentKey) : null;
        return color != null ? color.intValue() : Theme.getColor(this.currentKey);
    }

    public void setAnimatedColor(int color) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        if (resourcesProvider2 != null) {
            resourcesProvider2.setAnimatedColor(getCurrentKey(), color);
        } else {
            Theme.setAnimatedColor(getCurrentKey(), color);
        }
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

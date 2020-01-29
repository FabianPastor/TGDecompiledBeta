package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.PatternCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ColorPicker;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.WallpaperCheckBoxView;
import org.telegram.ui.Components.WallpaperParallaxEffect;
import org.telegram.ui.ThemePreviewActivity;
import org.telegram.ui.WallpapersListActivity;

public class ThemePreviewActivity extends BaseFragment implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    public static final int SCREEN_TYPE_ACCENT_COLOR = 1;
    public static final int SCREEN_TYPE_CHANGE_BACKGROUND = 2;
    public static final int SCREEN_TYPE_PREVIEW = 0;
    private int TAG;
    /* access modifiers changed from: private */
    public Theme.ThemeAccent accent;
    /* access modifiers changed from: private */
    public ActionBar actionBar2;
    private Runnable applyColorAction;
    private boolean applyColorScheduled;
    /* access modifiers changed from: private */
    public Theme.ThemeInfo applyingTheme;
    /* access modifiers changed from: private */
    public int backgroundColor;
    /* access modifiers changed from: private */
    public int backgroundGradientColor;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    /* access modifiers changed from: private */
    public BackupImageView backgroundImage;
    /* access modifiers changed from: private */
    public int backgroundRotation;
    private int backupAccentColor;
    private long backupBackgroundGradientOverrideColor;
    private long backupBackgroundOverrideColor;
    private int backupBackgroundRotation;
    private int backupMyMessagesAccentColor;
    private int backupMyMessagesGradientAccentColor;
    /* access modifiers changed from: private */
    public final PorterDuff.Mode blendMode;
    private Bitmap blurredBitmap;
    /* access modifiers changed from: private */
    public FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    /* access modifiers changed from: private */
    public FrameLayout buttonsContainer;
    private TextView cancelButton;
    /* access modifiers changed from: private */
    public WallpaperCheckBoxView[] checkBoxView;
    /* access modifiers changed from: private */
    public ColorPicker colorPicker;
    /* access modifiers changed from: private */
    public int colorType;
    /* access modifiers changed from: private */
    public float currentIntensity;
    /* access modifiers changed from: private */
    public Object currentWallpaper;
    private Bitmap currentWallpaperBitmap;
    private WallpaperActivityDelegate delegate;
    private boolean deleteOnCancel;
    private DialogsAdapter dialogsAdapter;
    private TextView doneButton;
    /* access modifiers changed from: private */
    public View dotsContainer;
    /* access modifiers changed from: private */
    public TextView dropDown;
    /* access modifiers changed from: private */
    public ActionBarMenuItem dropDownContainer;
    private boolean editingTheme;
    /* access modifiers changed from: private */
    public ImageView floatingButton;
    private FrameLayout frameLayout;
    private String imageFilter;
    private HeaderCell intensityCell;
    private SeekBarView intensitySeekBar;
    /* access modifiers changed from: private */
    public boolean isBlurred;
    /* access modifiers changed from: private */
    public boolean isMotion;
    private int lastPickedColor;
    private int lastPickedColorNum;
    private TLRPC.TL_wallPaper lastSelectedPattern;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public RecyclerListView listView2;
    private String loadingFile;
    private File loadingFileObject;
    private TLRPC.PhotoSize loadingSize;
    /* access modifiers changed from: private */
    public int maxWallpaperSize;
    private MessagesAdapter messagesAdapter;
    /* access modifiers changed from: private */
    public AnimatorSet motionAnimation;
    /* access modifiers changed from: private */
    public boolean nightTheme;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private Bitmap originalBitmap;
    /* access modifiers changed from: private */
    public FrameLayout page1;
    /* access modifiers changed from: private */
    public FrameLayout page2;
    /* access modifiers changed from: private */
    public WallpaperParallaxEffect parallaxEffect;
    /* access modifiers changed from: private */
    public float parallaxScale;
    /* access modifiers changed from: private */
    public int patternColor;
    /* access modifiers changed from: private */
    public FrameLayout[] patternLayout;
    /* access modifiers changed from: private */
    public AnimatorSet patternViewAnimation;
    /* access modifiers changed from: private */
    public ArrayList<Object> patterns;
    private PatternsAdapter patternsAdapter;
    private FrameLayout[] patternsButtonsContainer;
    private TextView[] patternsCancelButton;
    private LinearLayoutManager patternsLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView patternsListView;
    private TextView[] patternsSaveButton;
    private int previousBackgroundColor;
    private int previousBackgroundGradientColor;
    private int previousBackgroundRotation;
    private float previousIntensity;
    private TLRPC.TL_wallPaper previousSelectedPattern;
    /* access modifiers changed from: private */
    public boolean progressVisible;
    /* access modifiers changed from: private */
    public RadialProgress2 radialProgress;
    /* access modifiers changed from: private */
    public boolean removeBackgroundOverride;
    private FrameLayout saveButtonsContainer;
    private ActionBarMenuItem saveItem;
    /* access modifiers changed from: private */
    public final int screenType;
    /* access modifiers changed from: private */
    public TLRPC.TL_wallPaper selectedPattern;
    /* access modifiers changed from: private */
    public Drawable sheetDrawable;
    private List<ThemeDescription> themeDescriptions;
    private boolean useDefaultThemeForButtons;
    /* access modifiers changed from: private */
    public ViewPager viewPager;
    /* access modifiers changed from: private */
    public long watchForKeyboardEndTime;

    public interface WallpaperActivityDelegate {
        void didSetNewBackground();
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public /* synthetic */ void lambda$new$0$ThemePreviewActivity() {
        this.applyColorScheduled = false;
        applyColor(this.lastPickedColor, this.lastPickedColorNum);
    }

    public ThemePreviewActivity(Object obj, Bitmap bitmap) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.applyColorAction = new Runnable() {
            public final void run() {
                ThemePreviewActivity.this.lambda$new$0$ThemePreviewActivity();
            }
        };
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.currentIntensity = 0.5f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.loadingFile = null;
        this.loadingFileObject = null;
        this.loadingSize = null;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = 2;
        this.currentWallpaper = obj;
        this.currentWallpaperBitmap = bitmap;
        Object obj2 = this.currentWallpaper;
        if (obj2 instanceof WallpapersListActivity.ColorWallpaper) {
            WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj2;
            this.isMotion = colorWallpaper.motion;
            this.selectedPattern = colorWallpaper.pattern;
            if (this.selectedPattern != null) {
                this.currentIntensity = colorWallpaper.intensity;
            }
        }
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo) {
        this(themeInfo, false, 0, false, false);
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo, boolean z, int i, boolean z2, boolean z3) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.applyColorAction = new Runnable() {
            public final void run() {
                ThemePreviewActivity.this.lambda$new$0$ThemePreviewActivity();
            }
        };
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.currentIntensity = 0.5f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.loadingFile = null;
        this.loadingFileObject = null;
        this.loadingSize = null;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = i;
        this.swipeBackEnabled = false;
        this.nightTheme = z3;
        this.applyingTheme = themeInfo;
        this.deleteOnCancel = z;
        this.editingTheme = z2;
        if (i == 1) {
            this.accent = this.applyingTheme.getAccent(!z2);
            this.useDefaultThemeForButtons = false;
            Theme.ThemeAccent themeAccent = this.accent;
            this.backupAccentColor = themeAccent.accentColor;
            this.backupMyMessagesAccentColor = themeAccent.myMessagesAccentColor;
            this.backupMyMessagesGradientAccentColor = themeAccent.myMessagesGradientAccentColor;
            this.backupBackgroundOverrideColor = themeAccent.backgroundOverrideColor;
            this.backupBackgroundGradientOverrideColor = themeAccent.backgroundGradientOverrideColor;
            this.backupBackgroundRotation = themeAccent.backgroundRotation;
        } else {
            this.accent = this.applyingTheme.getAccent(false);
            Theme.ThemeAccent themeAccent2 = this.accent;
            if (themeAccent2 != null) {
                this.selectedPattern = themeAccent2.pattern;
            }
        }
        Theme.ThemeAccent themeAccent3 = this.accent;
        if (themeAccent3 != null) {
            this.isMotion = themeAccent3.patternMotion;
            if (!TextUtils.isEmpty(themeAccent3.patternSlug)) {
                this.currentIntensity = this.accent.patternIntensity;
            }
            Theme.applyThemeTemporary(this.applyingTheme, true);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.goingToPreviewTheme, new Object[0]);
    }

    public void setInitialModes(boolean z, boolean z2) {
        this.isBlurred = z;
        this.isMotion = z2;
    }

    public View createView(Context context) {
        String str;
        int i;
        FrameLayout.LayoutParams layoutParams;
        int i2;
        Context context2 = context;
        this.hasOwnBackground = true;
        this.page1 = new FrameLayout(context2);
        this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public boolean canCollapseSearch() {
                return true;
            }

            public void onSearchCollapse() {
            }

            public void onSearchExpand() {
            }

            public void onTextChanged(EditText editText) {
            }
        }).setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.actionBar.setBackButtonDrawable(new MenuDrawable());
        this.actionBar.setAddToContainer(false);
        this.actionBar.setTitle(LocaleController.getString("ThemePreview", NUM));
        this.page1 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                measureChildWithMargins(ThemePreviewActivity.this.actionBar, i, 0, i2, 0);
                int measuredHeight = ThemePreviewActivity.this.actionBar.getMeasuredHeight();
                if (ThemePreviewActivity.this.actionBar.getVisibility() == 0) {
                    size2 -= measuredHeight;
                }
                ((FrameLayout.LayoutParams) ThemePreviewActivity.this.listView.getLayoutParams()).topMargin = measuredHeight;
                ThemePreviewActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
                measureChildWithMargins(ThemePreviewActivity.this.floatingButton, i, 0, i2, 0);
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == ThemePreviewActivity.this.actionBar && ThemePreviewActivity.this.parentLayout != null) {
                    ThemePreviewActivity.this.parentLayout.drawHeaderShadow(canvas, ThemePreviewActivity.this.actionBar.getVisibility() == 0 ? ThemePreviewActivity.this.actionBar.getMeasuredHeight() : 0);
                }
                return drawChild;
            }
        };
        this.page1.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.page1.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.listView = new RecyclerListView(context2);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        this.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(this.screenType != 0 ? 12.0f : 0.0f));
        this.page1.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.floatingButton = new ImageView(context2);
        this.floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.floatingButton.setImageResource(NUM);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(stateListAnimator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.page1.addView(this.floatingButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        this.dialogsAdapter = new DialogsAdapter(context2);
        this.listView.setAdapter(this.dialogsAdapter);
        this.page2 = new FrameLayout(context2) {
            private boolean ignoreLayout;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                if (ThemePreviewActivity.this.dropDownContainer != null) {
                    this.ignoreLayout = true;
                    if (!AndroidUtilities.isTablet()) {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ThemePreviewActivity.this.dropDownContainer.getLayoutParams();
                        layoutParams.topMargin = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                        ThemePreviewActivity.this.dropDownContainer.setLayoutParams(layoutParams);
                    }
                    if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                        ThemePreviewActivity.this.dropDown.setTextSize(1, 20.0f);
                    } else {
                        ThemePreviewActivity.this.dropDown.setTextSize(1, 18.0f);
                    }
                    this.ignoreLayout = false;
                }
                measureChildWithMargins(ThemePreviewActivity.this.actionBar2, i, 0, i2, 0);
                int measuredHeight = ThemePreviewActivity.this.actionBar2.getMeasuredHeight();
                if (ThemePreviewActivity.this.actionBar2.getVisibility() == 0) {
                    size2 -= measuredHeight;
                }
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) ThemePreviewActivity.this.listView2.getLayoutParams();
                layoutParams2.topMargin = measuredHeight;
                ThemePreviewActivity.this.listView2.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2 - layoutParams2.bottomMargin, NUM));
                ((FrameLayout.LayoutParams) ThemePreviewActivity.this.backgroundImage.getLayoutParams()).topMargin = measuredHeight;
                ThemePreviewActivity.this.backgroundImage.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
                if (ThemePreviewActivity.this.bottomOverlayChat != null) {
                    measureChildWithMargins(ThemePreviewActivity.this.bottomOverlayChat, i, 0, i2, 0);
                }
                for (int i3 = 0; i3 < ThemePreviewActivity.this.patternLayout.length; i3++) {
                    if (ThemePreviewActivity.this.patternLayout[i3] != null) {
                        measureChildWithMargins(ThemePreviewActivity.this.patternLayout[i3], i, 0, i2, 0);
                    }
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == ThemePreviewActivity.this.actionBar2 && ThemePreviewActivity.this.parentLayout != null) {
                    ThemePreviewActivity.this.parentLayout.drawHeaderShadow(canvas, ThemePreviewActivity.this.actionBar2.getVisibility() == 0 ? (int) (((float) ThemePreviewActivity.this.actionBar2.getMeasuredHeight()) + ThemePreviewActivity.this.actionBar2.getTranslationY()) : 0);
                }
                return drawChild;
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.messagesAdapter = new MessagesAdapter(this, context2);
        this.actionBar2 = createActionBar(context);
        this.actionBar2.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar2.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                String url;
                if (i == -1) {
                    if (ThemePreviewActivity.this.checkDiscard()) {
                        ThemePreviewActivity.this.cancelThemeApply(false);
                    }
                } else if (i >= 1 && i <= 3) {
                    ThemePreviewActivity.this.selectColorType(i);
                } else if (i == 4) {
                    if (ThemePreviewActivity.this.removeBackgroundOverride) {
                        Theme.resetCustomWallpaper(false);
                    }
                    File pathToWallpaper = ThemePreviewActivity.this.accent.getPathToWallpaper();
                    if (pathToWallpaper != null) {
                        pathToWallpaper.delete();
                    }
                    ThemePreviewActivity.this.accent.patternSlug = ThemePreviewActivity.this.selectedPattern != null ? ThemePreviewActivity.this.selectedPattern.slug : "";
                    ThemePreviewActivity.this.accent.patternIntensity = ThemePreviewActivity.this.currentIntensity;
                    ThemePreviewActivity.this.accent.patternMotion = ThemePreviewActivity.this.isMotion;
                    ThemePreviewActivity.this.saveAccentWallpaper();
                    NotificationCenter.getGlobalInstance().removeObserver(ThemePreviewActivity.this, NotificationCenter.wallpapersDidLoad);
                    Theme.saveThemeAccents(ThemePreviewActivity.this.applyingTheme, true, false, false, true);
                    Theme.applyPreviousTheme();
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, ThemePreviewActivity.this.applyingTheme, Boolean.valueOf(ThemePreviewActivity.this.nightTheme), null, -1);
                    ThemePreviewActivity.this.finishFragment();
                } else if (i == 5 && ThemePreviewActivity.this.getParentActivity() != null) {
                    StringBuilder sb = new StringBuilder();
                    if (ThemePreviewActivity.this.isBlurred) {
                        sb.append("blur");
                    }
                    if (ThemePreviewActivity.this.isMotion) {
                        if (sb.length() > 0) {
                            sb.append("+");
                        }
                        sb.append("motion");
                    }
                    if (ThemePreviewActivity.this.currentWallpaper instanceof TLRPC.TL_wallPaper) {
                        url = "https://" + MessagesController.getInstance(ThemePreviewActivity.this.currentAccount).linkPrefix + "/bg/" + ((TLRPC.TL_wallPaper) ThemePreviewActivity.this.currentWallpaper).slug;
                        if (sb.length() > 0) {
                            url = url + "?mode=" + sb.toString();
                        }
                    } else if (ThemePreviewActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                        WallpapersListActivity.ColorWallpaper colorWallpaper = new WallpapersListActivity.ColorWallpaper(ThemePreviewActivity.this.selectedPattern != null ? ThemePreviewActivity.this.selectedPattern.slug : "c", ThemePreviewActivity.this.backgroundColor, ThemePreviewActivity.this.backgroundGradientColor, ThemePreviewActivity.this.backgroundRotation, ThemePreviewActivity.this.currentIntensity, ThemePreviewActivity.this.isMotion, (File) null);
                        colorWallpaper.pattern = ThemePreviewActivity.this.selectedPattern;
                        url = colorWallpaper.getUrl();
                    } else {
                        return;
                    }
                    String str = url;
                    ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                    themePreviewActivity.showDialog(new ShareAlert(themePreviewActivity.getParentActivity(), (ArrayList<MessageObject>) null, str, false, str, false));
                }
            }
        });
        this.backgroundImage = new BackupImageView(context2) {
            private Drawable background;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                float unused = themePreviewActivity.parallaxScale = themePreviewActivity.parallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
                if (ThemePreviewActivity.this.isMotion) {
                    setScaleX(ThemePreviewActivity.this.parallaxScale);
                    setScaleY(ThemePreviewActivity.this.parallaxScale);
                }
                if (ThemePreviewActivity.this.radialProgress != null) {
                    int dp = AndroidUtilities.dp(44.0f);
                    int measuredWidth = (getMeasuredWidth() - dp) / 2;
                    int measuredHeight = (getMeasuredHeight() - dp) / 2;
                    ThemePreviewActivity.this.radialProgress.setProgressRect(measuredWidth, measuredHeight, measuredWidth + dp, dp + measuredHeight);
                }
                ThemePreviewActivity themePreviewActivity2 = ThemePreviewActivity.this;
                boolean unused2 = themePreviewActivity2.progressVisible = themePreviewActivity2.screenType == 2 && getMeasuredWidth() <= getMeasuredHeight();
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                Drawable drawable = this.background;
                if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable)) {
                    this.background.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    this.background.draw(canvas);
                } else if (drawable instanceof BitmapDrawable) {
                    if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                        canvas.save();
                        float f = 2.0f / AndroidUtilities.density;
                        canvas.scale(f, f);
                        this.background.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / f)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / f)));
                        this.background.draw(canvas);
                        canvas.restore();
                    } else {
                        int measuredHeight = getMeasuredHeight();
                        float measuredWidth = ((float) getMeasuredWidth()) / ((float) this.background.getIntrinsicWidth());
                        float intrinsicHeight = ((float) measuredHeight) / ((float) this.background.getIntrinsicHeight());
                        if (measuredWidth < intrinsicHeight) {
                            measuredWidth = intrinsicHeight;
                        }
                        int ceil = (int) Math.ceil((double) (((float) this.background.getIntrinsicWidth()) * measuredWidth * ThemePreviewActivity.this.parallaxScale));
                        int ceil2 = (int) Math.ceil((double) (((float) this.background.getIntrinsicHeight()) * measuredWidth * ThemePreviewActivity.this.parallaxScale));
                        int measuredWidth2 = (getMeasuredWidth() - ceil) / 2;
                        int i = (measuredHeight - ceil2) / 2;
                        this.background.setBounds(measuredWidth2, i, ceil + measuredWidth2, ceil2 + i);
                        this.background.draw(canvas);
                    }
                }
                super.onDraw(canvas);
                if (ThemePreviewActivity.this.progressVisible && ThemePreviewActivity.this.radialProgress != null) {
                    ThemePreviewActivity.this.radialProgress.draw(canvas);
                }
            }

            public Drawable getBackground() {
                return this.background;
            }

            public void setBackground(Drawable drawable) {
                this.background = drawable;
            }

            public void setAlpha(float f) {
                if (ThemePreviewActivity.this.radialProgress != null) {
                    ThemePreviewActivity.this.radialProgress.setOverrideAlpha(f);
                }
            }
        };
        int i3 = this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper ? 3 : 2;
        Object obj = this.currentWallpaper;
        if ((obj instanceof WallpapersListActivity.FileWallpaper) && "t".equals(((WallpapersListActivity.FileWallpaper) obj).slug)) {
            i3 = 0;
        }
        this.page2.addView(this.backgroundImage, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        if (this.screenType == 2) {
            this.backgroundImage.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate() {
                public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
                    ThemePreviewActivity.this.lambda$createView$1$ThemePreviewActivity(imageReceiver, z, z2);
                }

                public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                    ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
                }
            });
        }
        String str2 = "fonts/rmedium.ttf";
        if (this.messagesAdapter.showSecretMessages) {
            this.actionBar2.setTitle("Telegram Beta Chat");
            this.actionBar2.setSubtitle(LocaleController.formatPluralString("Members", 505));
        } else {
            int i4 = this.screenType;
            if (i4 == 2) {
                this.actionBar2.setTitle(LocaleController.getString("BackgroundPreview", NUM));
                Object obj2 = this.currentWallpaper;
                if ((obj2 instanceof WallpapersListActivity.ColorWallpaper) || (obj2 instanceof TLRPC.TL_wallPaper)) {
                    this.actionBar2.createMenu().addItem(5, NUM);
                }
            } else if (i4 == 1) {
                ActionBarMenu createMenu = this.actionBar2.createMenu();
                this.saveItem = createMenu.addItem(4, (CharSequence) LocaleController.getString("Save", NUM).toUpperCase());
                this.dropDownContainer = new ActionBarMenuItem(context2, createMenu, 0, 0);
                this.dropDownContainer.setSubMenuOpenSide(1);
                this.dropDownContainer.addSubItem(1, LocaleController.getString("ColorPickerMainColor", NUM));
                this.dropDownContainer.addSubItem(2, LocaleController.getString("ColorPickerBackground", NUM));
                this.dropDownContainer.addSubItem(3, LocaleController.getString("ColorPickerMyMessages", NUM));
                this.dropDownContainer.setAllowCloseAnimation(false);
                this.actionBar2.addView(this.dropDownContainer, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
                this.dropDownContainer.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        ThemePreviewActivity.this.lambda$createView$2$ThemePreviewActivity(view);
                    }
                });
                this.dropDown = new TextView(context2);
                this.dropDown.setGravity(3);
                this.dropDown.setSingleLine(true);
                this.dropDown.setLines(1);
                this.dropDown.setMaxLines(1);
                this.dropDown.setEllipsize(TextUtils.TruncateAt.END);
                this.dropDown.setTextColor(Theme.getColor("actionBarDefaultTitle"));
                this.dropDown.setTypeface(AndroidUtilities.getTypeface(str2));
                this.dropDown.setText(LocaleController.getString("ColorPickerMainColor", NUM));
                Drawable mutate2 = context.getResources().getDrawable(NUM).mutate();
                mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultTitle"), PorterDuff.Mode.MULTIPLY));
                this.dropDown.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, mutate2, (Drawable) null);
                this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
                this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 1.0f));
            } else {
                Theme.ThemeInfo themeInfo = this.applyingTheme;
                TLRPC.TL_theme tL_theme = themeInfo.info;
                String name = tL_theme != null ? tL_theme.title : themeInfo.getName();
                int lastIndexOf = name.lastIndexOf(".attheme");
                if (lastIndexOf >= 0) {
                    name = name.substring(0, lastIndexOf);
                }
                this.actionBar2.setTitle(name);
                TLRPC.TL_theme tL_theme2 = this.applyingTheme.info;
                if (tL_theme2 == null || (i2 = tL_theme2.installs_count) <= 0) {
                    this.actionBar2.setSubtitle(LocaleController.formatDateOnline((System.currentTimeMillis() / 1000) - 3600));
                } else {
                    this.actionBar2.setSubtitle(LocaleController.formatPluralString("ThemeInstallCount", i2));
                }
            }
        }
        this.listView2 = new RecyclerListView(context2) {
            public boolean drawChild(Canvas canvas, View view, long j) {
                RecyclerView.ViewHolder childViewHolder;
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                    chatMessageCell.getMessageObject();
                    ImageReceiver avatarImage = chatMessageCell.getAvatarImage();
                    if (avatarImage != null) {
                        int top = view.getTop();
                        if (chatMessageCell.isPinnedBottom() && (childViewHolder = ThemePreviewActivity.this.listView2.getChildViewHolder(view)) != null) {
                            if (ThemePreviewActivity.this.listView2.findViewHolderForAdapterPosition(childViewHolder.getAdapterPosition() - 1) != null) {
                                avatarImage.setImageY(-AndroidUtilities.dp(1000.0f));
                                avatarImage.draw(canvas);
                                return drawChild;
                            }
                        }
                        float translationX = chatMessageCell.getTranslationX();
                        int top2 = view.getTop() + chatMessageCell.getLayoutHeight();
                        int measuredHeight = ThemePreviewActivity.this.listView2.getMeasuredHeight() - ThemePreviewActivity.this.listView2.getPaddingBottom();
                        if (top2 > measuredHeight) {
                            top2 = measuredHeight;
                        }
                        if (chatMessageCell.isPinnedTop() && (r9 = ThemePreviewActivity.this.listView2.getChildViewHolder(view)) != null) {
                            int i = 0;
                            while (i < 20) {
                                i++;
                                RecyclerView.ViewHolder childViewHolder2 = ThemePreviewActivity.this.listView2.findViewHolderForAdapterPosition(childViewHolder2.getAdapterPosition() + 1);
                                if (childViewHolder2 == null) {
                                    break;
                                }
                                top = childViewHolder2.itemView.getTop();
                                if (top2 - AndroidUtilities.dp(48.0f) < childViewHolder2.itemView.getBottom()) {
                                    translationX = Math.min(childViewHolder2.itemView.getTranslationX(), translationX);
                                }
                                View view2 = childViewHolder2.itemView;
                                if (view2 instanceof ChatMessageCell) {
                                    if (!((ChatMessageCell) view2).isPinnedTop()) {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                        if (top2 - AndroidUtilities.dp(48.0f) < top) {
                            top2 = top + AndroidUtilities.dp(48.0f);
                        }
                        if (translationX != 0.0f) {
                            canvas.save();
                            canvas.translate(translationX, 0.0f);
                        }
                        avatarImage.setImageY(top2 - AndroidUtilities.dp(44.0f));
                        avatarImage.draw(canvas);
                        if (translationX != 0.0f) {
                            canvas.restore();
                        }
                    }
                }
                return drawChild;
            }

            /* access modifiers changed from: protected */
            public void onChildPressed(View view, float f, float f2, boolean z) {
                if (!z || !(view instanceof ChatMessageCell) || ((ChatMessageCell) view).isInsideBackground(f, f2)) {
                    super.onChildPressed(view, f, f2, z);
                }
            }

            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(View view) {
                RecyclerView.ViewHolder findContainingViewHolder = ThemePreviewActivity.this.listView2.findContainingViewHolder(view);
                if (findContainingViewHolder == null || findContainingViewHolder.getItemViewType() != 2) {
                    return super.allowSelectChildAtPosition(view);
                }
                return false;
            }
        };
        ((DefaultItemAnimator) this.listView2.getItemAnimator()).setDelayAnimations(false);
        this.listView2.setVerticalScrollBarEnabled(true);
        this.listView2.setOverScrollMode(2);
        int i5 = this.screenType;
        if (i5 == 2) {
            this.listView2.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(52.0f));
        } else if (i5 == 1) {
            this.listView2.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(16.0f));
        } else {
            this.listView2.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        }
        this.listView2.setClipToPadding(false);
        this.listView2.setLayoutManager(new LinearLayoutManager(context2, 1, true));
        this.listView2.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        if (this.screenType == 1) {
            this.page2.addView(this.listView2, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 294.0f));
            this.listView2.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
                public final void onItemClick(View view, int i, float f, float f2) {
                    ThemePreviewActivity.this.lambda$createView$3$ThemePreviewActivity(view, i, f, f2);
                }
            });
        } else {
            this.page2.addView(this.listView2, LayoutHelper.createFrame(-1, -1, 51));
        }
        this.listView2.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ThemePreviewActivity.this.listView2.invalidateViews();
            }
        });
        this.page2.addView(this.actionBar2, LayoutHelper.createFrame(-1, -2.0f));
        this.parallaxEffect = new WallpaperParallaxEffect(context2);
        this.parallaxEffect.setCallback(new WallpaperParallaxEffect.Callback() {
            public final void onOffsetsChanged(int i, int i2) {
                ThemePreviewActivity.this.lambda$createView$4$ThemePreviewActivity(i, i2);
            }
        });
        int i6 = this.screenType;
        if (i6 == 1 || i6 == 2) {
            this.radialProgress = new RadialProgress2(this.backgroundImage);
            this.radialProgress.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
            if (this.screenType == 2) {
                this.bottomOverlayChat = new FrameLayout(context2) {
                    public void onDraw(Canvas canvas) {
                        int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                        Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                        Theme.chat_composeShadowDrawable.draw(canvas);
                        canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                    }
                };
                this.bottomOverlayChat.setWillNotDraw(false);
                this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                this.page2.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
                this.bottomOverlayChat.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        ThemePreviewActivity.this.lambda$createView$5$ThemePreviewActivity(view);
                    }
                });
                this.bottomOverlayChatText = new TextView(context2);
                this.bottomOverlayChatText.setTextSize(1, 15.0f);
                this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface(str2));
                this.bottomOverlayChatText.setTextColor(Theme.getColor("chat_fieldOverlayText"));
                this.bottomOverlayChatText.setText(LocaleController.getString("SetBackground", NUM));
                this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
            }
            final Rect rect = new Rect();
            this.sheetDrawable = context.getResources().getDrawable(NUM).mutate();
            this.sheetDrawable.getPadding(rect);
            this.sheetDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhite"), PorterDuff.Mode.MULTIPLY));
            String[] strArr = new String[i3];
            int[] iArr = new int[i3];
            this.checkBoxView = new WallpaperCheckBoxView[i3];
            if (i3 != 0) {
                this.buttonsContainer = new FrameLayout(context2);
                if (this.screenType == 1) {
                    strArr[0] = LocaleController.getString("BackgroundMotion", NUM);
                    strArr[1] = LocaleController.getString("BackgroundPattern", NUM);
                } else if (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    strArr[0] = LocaleController.getString("BackgroundColor", NUM);
                    strArr[1] = LocaleController.getString("BackgroundPattern", NUM);
                    strArr[2] = LocaleController.getString("BackgroundMotion", NUM);
                } else {
                    strArr[0] = LocaleController.getString("BackgroundBlurred", NUM);
                    strArr[1] = LocaleController.getString("BackgroundMotion", NUM);
                }
                TextPaint textPaint = new TextPaint(1);
                textPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
                textPaint.setTypeface(AndroidUtilities.getTypeface(str2));
                int i7 = 0;
                i = 0;
                while (i7 < strArr.length) {
                    iArr[i7] = (int) Math.ceil((double) textPaint.measureText(strArr[i7]));
                    i = Math.max(i, iArr[i7]);
                    i7++;
                    str2 = str2;
                }
                str = str2;
            } else {
                str = str2;
                i = 0;
            }
            int i8 = 0;
            while (i8 < i3) {
                this.checkBoxView[i8] = new WallpaperCheckBoxView(context2, this.screenType == 1 || !(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) || i8 != 0);
                this.checkBoxView[i8].setBackgroundColor(this.backgroundColor);
                this.checkBoxView[i8].setText(strArr[i8], iArr[i8], i);
                if (this.screenType != 1) {
                    if (!(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
                        this.checkBoxView[i8].setChecked(i8 == 0 ? this.isBlurred : this.isMotion, false);
                    } else if (i8 == 1) {
                        this.checkBoxView[i8].setChecked(this.selectedPattern != null, false);
                    } else if (i8 == 2) {
                        this.checkBoxView[i8].setChecked(this.isMotion, false);
                    }
                }
                int dp = AndroidUtilities.dp(56.0f) + i;
                FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(dp, -2);
                layoutParams2.gravity = 19;
                layoutParams2.leftMargin = i8 == 1 ? dp + AndroidUtilities.dp(9.0f) : 0;
                this.buttonsContainer.addView(this.checkBoxView[i8], layoutParams2);
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.checkBoxView;
                wallpaperCheckBoxViewArr[i8].setOnClickListener(new View.OnClickListener(i8, wallpaperCheckBoxViewArr[i8]) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ WallpaperCheckBoxView f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(View view) {
                        ThemePreviewActivity.this.lambda$createView$6$ThemePreviewActivity(this.f$1, this.f$2, view);
                    }
                });
                if (i8 == 2) {
                    this.checkBoxView[i8].setAlpha(0.0f);
                    this.checkBoxView[i8].setVisibility(4);
                }
                i8++;
            }
            if (this.screenType == 1) {
                updateCheckboxes();
            }
            if (this.screenType == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
                this.isBlurred = false;
                final int i9 = 0;
                while (i9 < 2) {
                    this.patternLayout[i9] = new FrameLayout(context2) {
                        public void onDraw(Canvas canvas) {
                            if (i9 == 0) {
                                ThemePreviewActivity.this.sheetDrawable.setBounds(ThemePreviewActivity.this.colorPicker.getLeft() - rect.left, 0, ThemePreviewActivity.this.colorPicker.getRight() + rect.right, getMeasuredHeight());
                            } else {
                                ThemePreviewActivity.this.sheetDrawable.setBounds(-rect.left, 0, getMeasuredWidth() + rect.right, getMeasuredHeight());
                            }
                            ThemePreviewActivity.this.sheetDrawable.draw(canvas);
                        }
                    };
                    if (i9 == 1 || this.screenType == 2) {
                        this.patternLayout[i9].setVisibility(4);
                    }
                    this.patternLayout[i9].setWillNotDraw(false);
                    if (this.screenType == 2) {
                        layoutParams = LayoutHelper.createFrame(-1, i9 == 0 ? 342 : 316, 83);
                    } else {
                        layoutParams = LayoutHelper.createFrame(-1, i9 == 0 ? 294 : 316, 83);
                    }
                    if (i9 == 0) {
                        layoutParams.height += AndroidUtilities.dp(12.0f) + rect.top;
                        this.patternLayout[i9].setPadding(0, AndroidUtilities.dp(12.0f) + rect.top, 0, 0);
                    }
                    this.page2.addView(this.patternLayout[i9], layoutParams);
                    if (i9 == 1 || this.screenType == 2) {
                        this.patternsButtonsContainer[i9] = new FrameLayout(context2) {
                            public void onDraw(Canvas canvas) {
                                int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                                Theme.chat_composeShadowDrawable.draw(canvas);
                                canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                            }
                        };
                        this.patternsButtonsContainer[i9].setWillNotDraw(false);
                        this.patternsButtonsContainer[i9].setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                        this.patternsButtonsContainer[i9].setClickable(true);
                        this.patternLayout[i9].addView(this.patternsButtonsContainer[i9], LayoutHelper.createFrame(-1, 51, 80));
                        this.patternsCancelButton[i9] = new TextView(context2);
                        this.patternsCancelButton[i9].setTextSize(1, 15.0f);
                        this.patternsCancelButton[i9].setTypeface(AndroidUtilities.getTypeface(str));
                        this.patternsCancelButton[i9].setTextColor(Theme.getColor("chat_fieldOverlayText"));
                        this.patternsCancelButton[i9].setText(LocaleController.getString("Cancel", NUM).toUpperCase());
                        this.patternsCancelButton[i9].setGravity(17);
                        this.patternsCancelButton[i9].setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                        this.patternsCancelButton[i9].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 0));
                        this.patternsButtonsContainer[i9].addView(this.patternsCancelButton[i9], LayoutHelper.createFrame(-2, -1, 51));
                        this.patternsCancelButton[i9].setOnClickListener(new View.OnClickListener(i9) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(View view) {
                                ThemePreviewActivity.this.lambda$createView$7$ThemePreviewActivity(this.f$1, view);
                            }
                        });
                        this.patternsSaveButton[i9] = new TextView(context2);
                        this.patternsSaveButton[i9].setTextSize(1, 15.0f);
                        this.patternsSaveButton[i9].setTypeface(AndroidUtilities.getTypeface(str));
                        this.patternsSaveButton[i9].setTextColor(Theme.getColor("chat_fieldOverlayText"));
                        this.patternsSaveButton[i9].setText(LocaleController.getString("ApplyTheme", NUM).toUpperCase());
                        this.patternsSaveButton[i9].setGravity(17);
                        this.patternsSaveButton[i9].setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                        this.patternsSaveButton[i9].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 0));
                        this.patternsButtonsContainer[i9].addView(this.patternsSaveButton[i9], LayoutHelper.createFrame(-2, -1, 53));
                        this.patternsSaveButton[i9].setOnClickListener(new View.OnClickListener(i9) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(View view) {
                                ThemePreviewActivity.this.lambda$createView$8$ThemePreviewActivity(this.f$1, view);
                            }
                        });
                    }
                    if (i9 == 1) {
                        TextView textView = new TextView(context2);
                        textView.setLines(1);
                        textView.setSingleLine(true);
                        textView.setText(LocaleController.getString("BackgroundChoosePattern", NUM));
                        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        textView.setTextSize(1, 20.0f);
                        textView.setTypeface(AndroidUtilities.getTypeface(str));
                        textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
                        textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                        textView.setGravity(16);
                        this.patternLayout[i9].addView(textView, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 21.0f, 0.0f, 0.0f));
                        this.patternsListView = new RecyclerListView(context2) {
                            public boolean onTouchEvent(MotionEvent motionEvent) {
                                if (motionEvent.getAction() == 0) {
                                    getParent().requestDisallowInterceptTouchEvent(true);
                                }
                                return super.onTouchEvent(motionEvent);
                            }
                        };
                        RecyclerListView recyclerListView = this.patternsListView;
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 0, false);
                        this.patternsLayoutManager = linearLayoutManager;
                        recyclerListView.setLayoutManager(linearLayoutManager);
                        RecyclerListView recyclerListView2 = this.patternsListView;
                        PatternsAdapter patternsAdapter2 = new PatternsAdapter(context2);
                        this.patternsAdapter = patternsAdapter2;
                        recyclerListView2.setAdapter(patternsAdapter2);
                        this.patternsListView.addItemDecoration(new RecyclerView.ItemDecoration() {
                            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                                int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                                rect.left = AndroidUtilities.dp(12.0f);
                                rect.top = 0;
                                rect.bottom = 0;
                                if (childAdapterPosition == state.getItemCount() - 1) {
                                    rect.right = AndroidUtilities.dp(12.0f);
                                }
                            }
                        });
                        this.patternLayout[i9].addView(this.patternsListView, LayoutHelper.createFrame(-1, 100.0f, 51, 0.0f, 76.0f, 0.0f, 0.0f));
                        this.patternsListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                            public final void onItemClick(View view, int i) {
                                ThemePreviewActivity.this.lambda$createView$9$ThemePreviewActivity(view, i);
                            }
                        });
                        this.intensityCell = new HeaderCell(context2);
                        this.intensityCell.setText(LocaleController.getString("BackgroundIntensity", NUM));
                        this.patternLayout[i9].addView(this.intensityCell, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 175.0f, 0.0f, 0.0f));
                        this.intensitySeekBar = new SeekBarView(context2) {
                            public boolean onTouchEvent(MotionEvent motionEvent) {
                                if (motionEvent.getAction() == 0) {
                                    getParent().requestDisallowInterceptTouchEvent(true);
                                }
                                return super.onTouchEvent(motionEvent);
                            }
                        };
                        this.intensitySeekBar.setProgress(this.currentIntensity);
                        this.intensitySeekBar.setReportChanges(true);
                        this.intensitySeekBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                            public void onSeekBarPressed(boolean z) {
                            }

                            public void onSeekBarDrag(boolean z, float f) {
                                float unused = ThemePreviewActivity.this.currentIntensity = f;
                                ThemePreviewActivity.this.backgroundImage.getImageReceiver().setAlpha(ThemePreviewActivity.this.currentIntensity);
                                ThemePreviewActivity.this.backgroundImage.invalidate();
                                ThemePreviewActivity.this.patternsListView.invalidateViews();
                            }
                        });
                        this.patternLayout[i9].addView(this.intensitySeekBar, LayoutHelper.createFrame(-1, 30.0f, 51, 9.0f, 215.0f, 9.0f, 0.0f));
                    } else {
                        this.colorPicker = new ColorPicker(context2, this.editingTheme, new ColorPicker.ColorPickerDelegate() {
                            public void setColor(int i, int i2, boolean z) {
                                if (ThemePreviewActivity.this.screenType == 2) {
                                    ThemePreviewActivity.this.setBackgroundColor(i, i2, z);
                                } else {
                                    ThemePreviewActivity.this.scheduleApplyColor(i, i2, z);
                                }
                            }

                            public void openThemeCreate(boolean z) {
                                if (!z) {
                                    AlertsCreator.createThemeCreateDialog(ThemePreviewActivity.this, 1, (Theme.ThemeInfo) null, (Theme.ThemeAccent) null);
                                } else if (ThemePreviewActivity.this.accent.info == null) {
                                    ThemePreviewActivity.this.finishFragment();
                                    MessagesController.getInstance(ThemePreviewActivity.this.currentAccount).saveThemeToServer(ThemePreviewActivity.this.accent.parentTheme, ThemePreviewActivity.this.accent);
                                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, ThemePreviewActivity.this.accent.parentTheme, ThemePreviewActivity.this.accent);
                                } else {
                                    String str = "https://" + MessagesController.getInstance(ThemePreviewActivity.this.currentAccount).linkPrefix + "/addtheme/" + ThemePreviewActivity.this.accent.info.slug;
                                    ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                                    themePreviewActivity.showDialog(new ShareAlert(themePreviewActivity.getParentActivity(), (ArrayList<MessageObject>) null, str, false, str, false));
                                }
                            }

                            public void deleteTheme() {
                                if (ThemePreviewActivity.this.getParentActivity() != null) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) ThemePreviewActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("DeleteThemeTitle", NUM));
                                    builder.setMessage(LocaleController.getString("DeleteThemeAlert", NUM));
                                    builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener() {
                                        public final void onClick(DialogInterface dialogInterface, int i) {
                                            ThemePreviewActivity.AnonymousClass16.this.lambda$deleteTheme$0$ThemePreviewActivity$16(dialogInterface, i);
                                        }
                                    });
                                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                                    AlertDialog create = builder.create();
                                    ThemePreviewActivity.this.showDialog(create);
                                    TextView textView = (TextView) create.getButton(-1);
                                    if (textView != null) {
                                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                                    }
                                }
                            }

                            public /* synthetic */ void lambda$deleteTheme$0$ThemePreviewActivity$16(DialogInterface dialogInterface, int i) {
                                Theme.deleteThemeAccent(ThemePreviewActivity.this.applyingTheme, ThemePreviewActivity.this.accent, true);
                                Theme.applyPreviousTheme();
                                Theme.refreshThemeColors();
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, ThemePreviewActivity.this.applyingTheme, Boolean.valueOf(ThemePreviewActivity.this.nightTheme), null, -1);
                                ThemePreviewActivity.this.finishFragment();
                            }

                            public void rotateColors() {
                                if (ThemePreviewActivity.this.screenType == 2) {
                                    ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                                    int unused = themePreviewActivity.backgroundRotation = themePreviewActivity.backgroundRotation + 45;
                                    while (ThemePreviewActivity.this.backgroundRotation >= 360) {
                                        ThemePreviewActivity themePreviewActivity2 = ThemePreviewActivity.this;
                                        int unused2 = themePreviewActivity2.backgroundRotation = themePreviewActivity2.backgroundRotation - 360;
                                    }
                                    ThemePreviewActivity themePreviewActivity3 = ThemePreviewActivity.this;
                                    themePreviewActivity3.setBackgroundColor(themePreviewActivity3.backgroundColor, 0, true);
                                    return;
                                }
                                ThemePreviewActivity.this.accent.backgroundRotation += 45;
                                while (ThemePreviewActivity.this.accent.backgroundRotation >= 360) {
                                    ThemePreviewActivity.this.accent.backgroundRotation -= 360;
                                }
                                Theme.refreshThemeColors();
                            }

                            public int getDefaultColor(int i) {
                                if (ThemePreviewActivity.this.colorType == 3 && ThemePreviewActivity.this.applyingTheme.firstAccentIsDefault && i == 0) {
                                    return ThemePreviewActivity.this.applyingTheme.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID).myMessagesAccentColor;
                                }
                                return 0;
                            }

                            public boolean hasChanges() {
                                ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                                return themePreviewActivity.hasChanges(themePreviewActivity.colorType);
                            }
                        });
                        if (this.screenType == 1) {
                            this.patternLayout[i9].addView(this.colorPicker, LayoutHelper.createFrame(-1, -1, 1));
                            if (this.applyingTheme.isDark()) {
                                this.colorPicker.setMinBrightness(0.2f);
                            } else {
                                this.colorPicker.setMinBrightness(0.05f);
                                this.colorPicker.setMaxBrightness(0.8f);
                            }
                            this.colorPicker.setType(1, hasChanges(1), false, false, false, 0, false);
                            this.colorPicker.setColor(this.accent.accentColor, 0);
                        } else {
                            this.patternLayout[i9].addView(this.colorPicker, LayoutHelper.createFrame(-1, -1.0f, 1, 0.0f, 0.0f, 0.0f, 48.0f));
                        }
                    }
                    i9++;
                }
            }
            updateButtonState(false, false);
            if (!this.backgroundImage.getImageReceiver().hasBitmapImage()) {
                this.page2.setBackgroundColor(-16777216);
            }
            if (this.screenType != 1 && !(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
                this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
                this.backgroundImage.getImageReceiver().setForceCrossfade(true);
            }
        } else {
            str = str2;
        }
        this.listView2.setAdapter(this.messagesAdapter);
        this.frameLayout = new FrameLayout(context2) {
            private int[] loc = new int[2];

            public void invalidate() {
                super.invalidate();
                if (ThemePreviewActivity.this.page2 != null) {
                    ThemePreviewActivity.this.page2.invalidate();
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (!AndroidUtilities.usingHardwareInput) {
                    getLocationInWindow(this.loc);
                    if (Build.VERSION.SDK_INT < 21) {
                        int[] iArr = this.loc;
                        iArr[1] = iArr[1] - AndroidUtilities.statusBarHeight;
                    }
                    if (ThemePreviewActivity.this.actionBar2.getTranslationY() != ((float) this.loc[1])) {
                        ThemePreviewActivity.this.actionBar2.setTranslationY((float) (-this.loc[1]));
                        ThemePreviewActivity.this.page2.invalidate();
                    }
                    if (SystemClock.uptimeMillis() < ThemePreviewActivity.this.watchForKeyboardEndTime) {
                        invalidate();
                    }
                }
            }
        };
        this.frameLayout.setWillNotDraw(false);
        FrameLayout frameLayout2 = this.frameLayout;
        this.fragmentView = frameLayout2;
        ViewTreeObserver viewTreeObserver = frameLayout2.getViewTreeObserver();
        $$Lambda$ThemePreviewActivity$I_g4S1Y0y4VHyo3pgzT98mcFmJQ r6 = new ViewTreeObserver.OnGlobalLayoutListener() {
            public final void onGlobalLayout() {
                ThemePreviewActivity.this.lambda$createView$10$ThemePreviewActivity();
            }
        };
        this.onGlobalLayoutListener = r6;
        viewTreeObserver.addOnGlobalLayoutListener(r6);
        this.viewPager = new ViewPager(context2);
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
            }

            public void onPageSelected(int i) {
                ThemePreviewActivity.this.dotsContainer.invalidate();
            }
        });
        this.viewPager.setAdapter(new PagerAdapter() {
            public int getItemPosition(Object obj) {
                return -1;
            }

            public boolean isViewFromObject(View view, Object obj) {
                return obj == view;
            }

            public int getCount() {
                return ThemePreviewActivity.this.screenType != 0 ? 1 : 2;
            }

            public Object instantiateItem(ViewGroup viewGroup, int i) {
                FrameLayout access$5100 = i == 0 ? ThemePreviewActivity.this.page2 : ThemePreviewActivity.this.page1;
                viewGroup.addView(access$5100);
                return access$5100;
            }

            public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
                viewGroup.removeView((View) obj);
            }

            public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
                if (dataSetObserver != null) {
                    super.unregisterDataSetObserver(dataSetObserver);
                }
            }
        });
        AndroidUtilities.setViewPagerEdgeEffectColor(this.viewPager, Theme.getColor("actionBarDefault"));
        this.frameLayout.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.screenType == 0 ? 48.0f : 0.0f));
        if (this.screenType == 0) {
            View view = new View(context2);
            view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
            FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(-1, 1, 83);
            layoutParams3.bottomMargin = AndroidUtilities.dp(48.0f);
            this.frameLayout.addView(view, layoutParams3);
            this.saveButtonsContainer = new FrameLayout(context2);
            this.saveButtonsContainer.setBackgroundColor(getButtonsColor("windowBackgroundWhite"));
            this.frameLayout.addView(this.saveButtonsContainer, LayoutHelper.createFrame(-1, 48, 83));
            this.dotsContainer = new View(context2) {
                private Paint paint = new Paint(1);

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    int currentItem = ThemePreviewActivity.this.viewPager.getCurrentItem();
                    this.paint.setColor(ThemePreviewActivity.this.getButtonsColor("chat_fieldOverlayText"));
                    int i = 0;
                    while (i < 2) {
                        this.paint.setAlpha(i == currentItem ? 255 : 127);
                        canvas.drawCircle((float) AndroidUtilities.dp((float) ((i * 15) + 3)), (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(3.0f), this.paint);
                        i++;
                    }
                }
            };
            this.saveButtonsContainer.addView(this.dotsContainer, LayoutHelper.createFrame(22, 8, 17));
            this.cancelButton = new TextView(context2);
            this.cancelButton.setTextSize(1, 14.0f);
            this.cancelButton.setTextColor(getButtonsColor("chat_fieldOverlayText"));
            this.cancelButton.setGravity(17);
            this.cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
            this.cancelButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
            this.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
            this.cancelButton.setTypeface(AndroidUtilities.getTypeface(str));
            this.saveButtonsContainer.addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
            this.cancelButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ThemePreviewActivity.this.lambda$createView$11$ThemePreviewActivity(view);
                }
            });
            this.doneButton = new TextView(context2);
            this.doneButton.setTextSize(1, 14.0f);
            this.doneButton.setTextColor(getButtonsColor("chat_fieldOverlayText"));
            this.doneButton.setGravity(17);
            this.doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
            this.doneButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
            this.doneButton.setText(LocaleController.getString("ApplyTheme", NUM).toUpperCase());
            this.doneButton.setTypeface(AndroidUtilities.getTypeface(str));
            this.saveButtonsContainer.addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
            this.doneButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ThemePreviewActivity.this.lambda$createView$12$ThemePreviewActivity(view);
                }
            });
        }
        this.themeDescriptions = getThemeDescriptionsInternal();
        setCurrentImage(true);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$ThemePreviewActivity(ImageReceiver imageReceiver, boolean z, boolean z2) {
        if (!(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            Drawable drawable = imageReceiver.getDrawable();
            if (z && drawable != null) {
                if (!Theme.hasThemeKey("chat_serviceBackground")) {
                    Theme.applyChatServiceMessageColor(AndroidUtilities.calcDrawableColor(drawable));
                }
                this.listView2.invalidateViews();
                FrameLayout frameLayout2 = this.buttonsContainer;
                if (frameLayout2 != null) {
                    int childCount = frameLayout2.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        this.buttonsContainer.getChildAt(i).invalidate();
                    }
                }
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    radialProgress2.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
                }
                if (!z2 && this.isBlurred && this.blurredBitmap == null) {
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(false);
                    updateBlurred();
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
                }
            }
        }
    }

    public /* synthetic */ void lambda$createView$2$ThemePreviewActivity(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    public /* synthetic */ void lambda$createView$3$ThemePreviewActivity(View view, int i, float f, float f2) {
        if (view instanceof ChatMessageCell) {
            ChatMessageCell chatMessageCell = (ChatMessageCell) view;
            if (!chatMessageCell.isInsideBackground(f, f2)) {
                selectColorType(2);
            } else if (chatMessageCell.getMessageObject().isOutOwner()) {
                selectColorType(3);
            } else {
                selectColorType(1);
            }
        }
    }

    public /* synthetic */ void lambda$createView$4$ThemePreviewActivity(int i, int i2) {
        if (this.isMotion) {
            float f = 1.0f;
            if (this.motionAnimation != null) {
                f = (this.backgroundImage.getScaleX() - 1.0f) / (this.parallaxScale - 1.0f);
            }
            this.backgroundImage.setTranslationX(((float) i) * f);
            this.backgroundImage.setTranslationY(((float) i2) * f);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:107:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0220  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x022a  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x026b  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0179 A[SYNTHETIC, Splitter:B:76:0x0179] */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01a2  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x01ac  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$5$ThemePreviewActivity(android.view.View r20) {
        /*
            r19 = this;
            r1 = r19
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r2 = r1.isBlurred
            r3 = 0
            java.lang.String r2 = r0.generateWallpaperName(r3, r2)
            boolean r4 = r1.isBlurred
            r5 = 0
            if (r4 == 0) goto L_0x0018
            java.lang.String r0 = r0.generateWallpaperName(r3, r5)
            r4 = r0
            goto L_0x0019
        L_0x0018:
            r4 = r2
        L_0x0019:
            java.io.File r6 = new java.io.File
            java.io.File r0 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            r6.<init>(r0, r2)
            java.lang.Object r0 = r1.currentWallpaper
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            java.lang.String r8 = "jpg"
            java.lang.String r9 = "t"
            r10 = 87
            r11 = 1
            if (r7 == 0) goto L_0x008e
            android.graphics.Bitmap r0 = r1.originalBitmap
            if (r0 == 0) goto L_0x0049
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0044 }
            r0.<init>(r6)     // Catch:{ Exception -> 0x0044 }
            android.graphics.Bitmap r7 = r1.originalBitmap     // Catch:{ Exception -> 0x0044 }
            android.graphics.Bitmap$CompressFormat r12 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x0044 }
            r7.compress(r12, r10, r0)     // Catch:{ Exception -> 0x0044 }
            r0.close()     // Catch:{ Exception -> 0x0044 }
        L_0x0042:
            r0 = 1
            goto L_0x0075
        L_0x0044:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x005c
        L_0x0049:
            org.telegram.ui.Components.BackupImageView r0 = r1.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            boolean r7 = r0.hasNotThumb()
            if (r7 != 0) goto L_0x005e
            boolean r7 = r0.hasStaticThumb()
            if (r7 == 0) goto L_0x005c
            goto L_0x005e
        L_0x005c:
            r0 = 0
            goto L_0x0075
        L_0x005e:
            android.graphics.Bitmap r0 = r0.getBitmap()
            java.io.FileOutputStream r7 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0070 }
            r7.<init>(r6)     // Catch:{ Exception -> 0x0070 }
            android.graphics.Bitmap$CompressFormat r12 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x0070 }
            r0.compress(r12, r10, r7)     // Catch:{ Exception -> 0x0070 }
            r7.close()     // Catch:{ Exception -> 0x0070 }
            goto L_0x0042
        L_0x0070:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x005c
        L_0x0075:
            if (r0 != 0) goto L_0x0118
            java.lang.Object r0 = r1.currentWallpaper
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r0
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r11)
            boolean r0 = org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r0, (java.io.File) r6)     // Catch:{ Exception -> 0x0087 }
            goto L_0x0118
        L_0x0087:
            r0 = move-exception
            r7 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
            goto L_0x0173
        L_0x008e:
            boolean r7 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r7 == 0) goto L_0x011a
            org.telegram.tgnet.TLRPC$TL_wallPaper r7 = r1.selectedPattern
            if (r7 == 0) goto L_0x0117
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r0     // Catch:{ all -> 0x0112 }
            org.telegram.ui.Components.BackupImageView r0 = r1.backgroundImage     // Catch:{ all -> 0x0112 }
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()     // Catch:{ all -> 0x0112 }
            android.graphics.Bitmap r0 = r0.getBitmap()     // Catch:{ all -> 0x0112 }
            int r7 = r0.getWidth()     // Catch:{ all -> 0x0112 }
            int r12 = r0.getHeight()     // Catch:{ all -> 0x0112 }
            android.graphics.Bitmap$Config r13 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0112 }
            android.graphics.Bitmap r7 = android.graphics.Bitmap.createBitmap(r7, r12, r13)     // Catch:{ all -> 0x0112 }
            android.graphics.Canvas r12 = new android.graphics.Canvas     // Catch:{ all -> 0x0112 }
            r12.<init>(r7)     // Catch:{ all -> 0x0112 }
            int r13 = r1.backgroundGradientColor     // Catch:{ all -> 0x0112 }
            r14 = 2
            if (r13 == 0) goto L_0x00de
            android.graphics.drawable.GradientDrawable r13 = new android.graphics.drawable.GradientDrawable     // Catch:{ all -> 0x0112 }
            int r15 = r1.backgroundRotation     // Catch:{ all -> 0x0112 }
            android.graphics.drawable.GradientDrawable$Orientation r15 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r15)     // Catch:{ all -> 0x0112 }
            int[] r3 = new int[r14]     // Catch:{ all -> 0x0112 }
            int r10 = r1.backgroundColor     // Catch:{ all -> 0x0112 }
            r3[r5] = r10     // Catch:{ all -> 0x0112 }
            int r10 = r1.backgroundGradientColor     // Catch:{ all -> 0x0112 }
            r3[r11] = r10     // Catch:{ all -> 0x0112 }
            r13.<init>(r15, r3)     // Catch:{ all -> 0x0112 }
            int r3 = r7.getWidth()     // Catch:{ all -> 0x0112 }
            int r10 = r7.getHeight()     // Catch:{ all -> 0x0112 }
            r13.setBounds(r5, r5, r3, r10)     // Catch:{ all -> 0x0112 }
            r13.draw(r12)     // Catch:{ all -> 0x0112 }
            goto L_0x00e3
        L_0x00de:
            int r3 = r1.backgroundColor     // Catch:{ all -> 0x0112 }
            r12.drawColor(r3)     // Catch:{ all -> 0x0112 }
        L_0x00e3:
            android.graphics.Paint r3 = new android.graphics.Paint     // Catch:{ all -> 0x0112 }
            r3.<init>(r14)     // Catch:{ all -> 0x0112 }
            android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter     // Catch:{ all -> 0x0112 }
            int r13 = r1.patternColor     // Catch:{ all -> 0x0112 }
            android.graphics.PorterDuff$Mode r14 = r1.blendMode     // Catch:{ all -> 0x0112 }
            r10.<init>(r13, r14)     // Catch:{ all -> 0x0112 }
            r3.setColorFilter(r10)     // Catch:{ all -> 0x0112 }
            r10 = 1132396544(0x437var_, float:255.0)
            float r13 = r1.currentIntensity     // Catch:{ all -> 0x0112 }
            float r13 = r13 * r10
            int r10 = (int) r13     // Catch:{ all -> 0x0112 }
            r3.setAlpha(r10)     // Catch:{ all -> 0x0112 }
            r10 = 0
            r12.drawBitmap(r0, r10, r10, r3)     // Catch:{ all -> 0x0112 }
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x0112 }
            r0.<init>(r6)     // Catch:{ all -> 0x0112 }
            android.graphics.Bitmap$CompressFormat r3 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x0112 }
            r10 = 87
            r7.compress(r3, r10, r0)     // Catch:{ all -> 0x0112 }
            r0.close()     // Catch:{ all -> 0x0112 }
            goto L_0x0117
        L_0x0112:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0173
        L_0x0117:
            r0 = 1
        L_0x0118:
            r3 = 0
            goto L_0x0175
        L_0x011a:
            boolean r3 = r0 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
            if (r3 == 0) goto L_0x014c
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r0
            int r3 = r0.resId
            if (r3 != 0) goto L_0x0117
            java.lang.String r3 = r0.slug
            boolean r3 = r9.equals(r3)
            if (r3 == 0) goto L_0x012d
            goto L_0x0117
        L_0x012d:
            java.io.File r3 = r0.originalPath     // Catch:{ Exception -> 0x0145 }
            if (r3 == 0) goto L_0x0134
            java.io.File r0 = r0.originalPath     // Catch:{ Exception -> 0x0145 }
            goto L_0x0136
        L_0x0134:
            java.io.File r0 = r0.path     // Catch:{ Exception -> 0x0145 }
        L_0x0136:
            boolean r3 = r0.equals(r6)     // Catch:{ Exception -> 0x0145 }
            if (r3 == 0) goto L_0x013e
            r0 = 1
            goto L_0x0175
        L_0x013e:
            boolean r0 = org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r0, (java.io.File) r6)     // Catch:{ Exception -> 0x0143 }
            goto L_0x0175
        L_0x0143:
            r0 = move-exception
            goto L_0x0147
        L_0x0145:
            r0 = move-exception
            r3 = 0
        L_0x0147:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0175
        L_0x014c:
            boolean r3 = r0 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r3 == 0) goto L_0x0173
            org.telegram.messenger.MediaController$SearchImage r0 = (org.telegram.messenger.MediaController.SearchImage) r0
            org.telegram.tgnet.TLRPC$Photo r3 = r0.photo
            if (r3 == 0) goto L_0x0163
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r3.sizes
            int r3 = r1.maxWallpaperSize
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r3, r11)
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r11)
            goto L_0x0169
        L_0x0163:
            java.lang.String r0 = r0.imageUrl
            java.io.File r0 = org.telegram.messenger.ImageLoader.getHttpFilePath(r0, r8)
        L_0x0169:
            boolean r0 = org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r0, (java.io.File) r6)     // Catch:{ Exception -> 0x016e }
            goto L_0x0118
        L_0x016e:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x0173:
            r0 = 0
            goto L_0x0118
        L_0x0175:
            boolean r7 = r1.isBlurred
            if (r7 == 0) goto L_0x019a
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x0195 }
            java.io.File r7 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x0195 }
            r0.<init>(r7, r4)     // Catch:{ all -> 0x0195 }
            java.io.FileOutputStream r7 = new java.io.FileOutputStream     // Catch:{ all -> 0x0195 }
            r7.<init>(r0)     // Catch:{ all -> 0x0195 }
            android.graphics.Bitmap r0 = r1.blurredBitmap     // Catch:{ all -> 0x0195 }
            android.graphics.Bitmap$CompressFormat r10 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x0195 }
            r12 = 87
            r0.compress(r10, r12, r7)     // Catch:{ all -> 0x0195 }
            r7.close()     // Catch:{ all -> 0x0195 }
            r0 = 1
            goto L_0x019a
        L_0x0195:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x019a:
            r7 = 45
            java.lang.Object r10 = r1.currentWallpaper
            boolean r12 = r10 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            if (r12 == 0) goto L_0x01ac
            org.telegram.tgnet.TLRPC$TL_wallPaper r10 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r10
            java.lang.String r8 = r10.slug
        L_0x01a6:
            r7 = 0
            r10 = 0
            r12 = 45
        L_0x01aa:
            r14 = 0
            goto L_0x01f8
        L_0x01ac:
            boolean r12 = r10 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r12 == 0) goto L_0x01c3
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r10 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r10
            org.telegram.tgnet.TLRPC$TL_wallPaper r7 = r1.selectedPattern
            if (r7 == 0) goto L_0x01b9
            java.lang.String r7 = r7.slug
            goto L_0x01bb
        L_0x01b9:
            java.lang.String r7 = "c"
        L_0x01bb:
            r8 = r7
            int r7 = r1.backgroundColor
            int r10 = r1.backgroundGradientColor
            int r12 = r1.backgroundRotation
            goto L_0x01aa
        L_0x01c3:
            boolean r12 = r10 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
            if (r12 == 0) goto L_0x01d3
            org.telegram.ui.WallpapersListActivity$FileWallpaper r10 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r10
            java.lang.String r8 = r10.slug
            java.io.File r10 = r10.path
            r14 = r10
        L_0x01ce:
            r7 = 0
            r10 = 0
            r12 = 45
            goto L_0x01f8
        L_0x01d3:
            boolean r12 = r10 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r12 == 0) goto L_0x01f5
            org.telegram.messenger.MediaController$SearchImage r10 = (org.telegram.messenger.MediaController.SearchImage) r10
            org.telegram.tgnet.TLRPC$Photo r12 = r10.photo
            if (r12 == 0) goto L_0x01ea
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r12.sizes
            int r10 = r1.maxWallpaperSize
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r10, r11)
            java.io.File r8 = org.telegram.messenger.FileLoader.getPathToAttach(r8, r11)
            goto L_0x01f0
        L_0x01ea:
            java.lang.String r10 = r10.imageUrl
            java.io.File r8 = org.telegram.messenger.ImageLoader.getHttpFilePath(r10, r8)
        L_0x01f0:
            java.lang.String r10 = ""
            r14 = r8
            r8 = r10
            goto L_0x01ce
        L_0x01f5:
            java.lang.String r8 = "d"
            goto L_0x01a6
        L_0x01f8:
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r15 = new org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo
            r15.<init>()
            r15.fileName = r4
            r15.originalFileName = r2
            r15.slug = r8
            boolean r2 = r1.isBlurred
            r15.isBlurred = r2
            boolean r2 = r1.isMotion
            r15.isMotion = r2
            r15.color = r7
            r15.gradientColor = r10
            r15.rotation = r12
            float r2 = r1.currentIntensity
            r15.intensity = r2
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r2)
            if (r8 == 0) goto L_0x0220
            r16 = 1
            goto L_0x0222
        L_0x0220:
            r16 = 0
        L_0x0222:
            r17 = 0
            r2 = r15
            r13.saveWallpaperToServer(r14, r15, r16, r17)
            if (r0 == 0) goto L_0x0267
            java.lang.String r0 = "chat_serviceBackground"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            org.telegram.ui.ActionBar.Theme.serviceMessageColorBackup = r0
            java.lang.String r0 = r2.slug
            boolean r0 = r9.equals(r0)
            if (r0 == 0) goto L_0x023b
            r2 = 0
        L_0x023b:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            r0.setOverrideWallpaper(r2)
            org.telegram.ui.ActionBar.Theme.reloadWallpaper()
            if (r3 != 0) goto L_0x0267
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r6.getAbsolutePath()
            java.lang.String r3 = org.telegram.messenger.ImageLoader.getHttpFileName(r3)
            r2.append(r3)
            java.lang.String r3 = "@100_100"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r0.removeImage(r2)
        L_0x0267:
            org.telegram.ui.ThemePreviewActivity$WallpaperActivityDelegate r0 = r1.delegate
            if (r0 == 0) goto L_0x026e
            r0.didSetNewBackground()
        L_0x026e:
            r19.finishFragment()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.lambda$createView$5$ThemePreviewActivity(android.view.View):void");
    }

    public /* synthetic */ void lambda$createView$6$ThemePreviewActivity(int i, WallpaperCheckBoxView wallpaperCheckBoxView, View view) {
        if (this.buttonsContainer.getAlpha() != 1.0f || this.patternViewAnimation != null) {
            return;
        }
        if (!(this.screenType == 1 && i == 0) && (!(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) || i != 2)) {
            boolean z = false;
            if (i == 1 && (this.screenType == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper))) {
                if (this.checkBoxView[1].isChecked()) {
                    this.lastSelectedPattern = this.selectedPattern;
                    this.backgroundImage.setImageDrawable((Drawable) null);
                    this.selectedPattern = null;
                    this.isMotion = false;
                    updateButtonState(false, true);
                    animateMotionChange();
                    if (this.patternLayout[1].getVisibility() == 0) {
                        if (this.screenType == 1) {
                            showPatternsView(0, true);
                        } else {
                            showPatternsView(i, this.patternLayout[i].getVisibility() != 0);
                        }
                    }
                } else {
                    selectPattern(this.lastSelectedPattern != null ? -1 : 0);
                    if (this.screenType == 1) {
                        showPatternsView(1, true);
                    } else {
                        showPatternsView(i, this.patternLayout[i].getVisibility() != 0);
                    }
                }
                WallpaperCheckBoxView wallpaperCheckBoxView2 = this.checkBoxView[1];
                if (this.selectedPattern != null) {
                    z = true;
                }
                wallpaperCheckBoxView2.setChecked(z, true);
                updateSelectedPattern(true);
                this.patternsListView.invalidateViews();
                updateMotionButton();
            } else if (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                if (this.patternLayout[i].getVisibility() != 0) {
                    z = true;
                }
                showPatternsView(i, z);
            } else {
                wallpaperCheckBoxView.setChecked(!wallpaperCheckBoxView.isChecked(), true);
                if (i == 0) {
                    this.isBlurred = wallpaperCheckBoxView.isChecked();
                    updateBlurred();
                    return;
                }
                this.isMotion = wallpaperCheckBoxView.isChecked();
                this.parallaxEffect.setEnabled(this.isMotion);
                animateMotionChange();
            }
        } else {
            wallpaperCheckBoxView.setChecked(!wallpaperCheckBoxView.isChecked(), true);
            this.isMotion = wallpaperCheckBoxView.isChecked();
            this.parallaxEffect.setEnabled(this.isMotion);
            animateMotionChange();
        }
    }

    public /* synthetic */ void lambda$createView$7$ThemePreviewActivity(int i, View view) {
        if (this.patternViewAnimation == null) {
            if (i == 0) {
                this.backgroundRotation = this.previousBackgroundRotation;
                setBackgroundColor(this.previousBackgroundGradientColor, 1, true);
                setBackgroundColor(this.previousBackgroundColor, 0, true);
            } else {
                this.selectedPattern = this.previousSelectedPattern;
                TLRPC.TL_wallPaper tL_wallPaper = this.selectedPattern;
                if (tL_wallPaper == null) {
                    this.backgroundImage.setImageDrawable((Drawable) null);
                } else {
                    BackupImageView backupImageView = this.backgroundImage;
                    ImageLocation forDocument = ImageLocation.getForDocument(tL_wallPaper.document);
                    String str = this.imageFilter;
                    TLRPC.TL_wallPaper tL_wallPaper2 = this.selectedPattern;
                    backupImageView.setImage(forDocument, str, (ImageLocation) null, (String) null, "jpg", tL_wallPaper2.document.size, 1, tL_wallPaper2);
                }
                this.checkBoxView[1].setChecked(this.selectedPattern != null, false);
                this.currentIntensity = this.previousIntensity;
                this.intensitySeekBar.setProgress(this.currentIntensity);
                this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
                updateButtonState(false, true);
                updateSelectedPattern(true);
            }
            if (this.screenType == 2) {
                showPatternsView(i, false);
                return;
            }
            if (this.selectedPattern == null) {
                if (this.isMotion) {
                    this.isMotion = false;
                    this.checkBoxView[0].setChecked(false, true);
                    animateMotionChange();
                }
                updateMotionButton();
            }
            showPatternsView(0, true);
        }
    }

    public /* synthetic */ void lambda$createView$8$ThemePreviewActivity(int i, View view) {
        if (this.patternViewAnimation == null) {
            if (this.screenType == 2) {
                showPatternsView(i, false);
            } else {
                showPatternsView(0, true);
            }
        }
    }

    public /* synthetic */ void lambda$createView$9$ThemePreviewActivity(View view, int i) {
        boolean z = this.selectedPattern != null;
        selectPattern(i);
        if (z == (this.selectedPattern == null)) {
            animateMotionChange();
            updateMotionButton();
        }
        updateSelectedPattern(true);
        this.checkBoxView[1].setChecked(this.selectedPattern != null, true);
        this.patternsListView.invalidateViews();
        int left = view.getLeft();
        int right = view.getRight();
        int dp = AndroidUtilities.dp(52.0f);
        int i2 = left - dp;
        if (i2 < 0) {
            this.patternsListView.smoothScrollBy(i2, 0);
            return;
        }
        int i3 = right + dp;
        if (i3 > this.patternsListView.getMeasuredWidth()) {
            RecyclerListView recyclerListView = this.patternsListView;
            recyclerListView.smoothScrollBy(i3 - recyclerListView.getMeasuredWidth(), 0);
        }
    }

    public /* synthetic */ void lambda$createView$10$ThemePreviewActivity() {
        this.watchForKeyboardEndTime = SystemClock.uptimeMillis() + 1500;
        this.frameLayout.invalidate();
    }

    public /* synthetic */ void lambda$createView$11$ThemePreviewActivity(View view) {
        cancelThemeApply(false);
    }

    public /* synthetic */ void lambda$createView$12$ThemePreviewActivity(View view) {
        Theme.ThemeAccent themeAccent;
        int i;
        Theme.ThemeInfo previousTheme = Theme.getPreviousTheme();
        if (previousTheme == null || (i = previousTheme.prevAccentId) < 0) {
            themeAccent = previousTheme.getAccent(false);
        } else {
            themeAccent = previousTheme.themeAccentsMap.get(i);
        }
        if (this.accent != null) {
            saveAccentWallpaper();
            Theme.saveThemeAccents(this.applyingTheme, true, false, false, false);
            Theme.clearPreviousTheme();
            Theme.applyTheme(this.applyingTheme, this.nightTheme);
            this.parentLayout.rebuildAllFragmentViews(false, false);
        } else {
            this.parentLayout.rebuildAllFragmentViews(false, false);
            File file = new File(this.applyingTheme.pathToFile);
            Theme.ThemeInfo themeInfo = this.applyingTheme;
            Theme.applyThemeFile(file, themeInfo.name, themeInfo.info, false);
            MessagesController.getInstance(this.applyingTheme.account).saveTheme(this.applyingTheme, (Theme.ThemeAccent) null, false, false);
        }
        finishFragment();
        if (this.screenType == 0) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didApplyNewTheme, previousTheme, themeAccent, Boolean.valueOf(this.deleteOnCancel));
        }
    }

    /* access modifiers changed from: private */
    public void selectColorType(int i) {
        int i2 = i;
        if (getParentActivity() != null && this.colorType != i2 && this.patternViewAnimation == null) {
            if (i2 != 2 || (!Theme.hasCustomWallpaper() && this.accent.backgroundOverrideColor != 4294967296L)) {
                int i3 = this.colorType;
                this.colorType = i2;
                if (i2 == 1) {
                    this.dropDown.setText(LocaleController.getString("ColorPickerMainColor", NUM));
                    this.colorPicker.setType(1, hasChanges(1), false, false, false, 0, false);
                    this.colorPicker.setColor(this.accent.accentColor, 0);
                } else if (i2 == 2) {
                    this.dropDown.setText(LocaleController.getString("ColorPickerBackground", NUM));
                    int color = Theme.getColor("chat_wallpaper");
                    int color2 = Theme.hasThemeKey("chat_wallpaper_gradient_to") ? Theme.getColor("chat_wallpaper_gradient_to") : 0;
                    long j = this.accent.backgroundGradientOverrideColor;
                    int i4 = (int) j;
                    if (i4 == 0 && j != 0) {
                        color2 = 0;
                    }
                    int i5 = (int) this.accent.backgroundOverrideColor;
                    this.colorPicker.setType(2, hasChanges(2), true, (i4 == 0 && color2 == 0) ? false : true, false, this.accent.backgroundRotation, false);
                    ColorPicker colorPicker2 = this.colorPicker;
                    if (i4 != 0) {
                        color2 = i4;
                    }
                    colorPicker2.setColor(color2, 1);
                    ColorPicker colorPicker3 = this.colorPicker;
                    if (i5 != 0) {
                        color = i5;
                    }
                    colorPicker3.setColor(color, 0);
                    this.messagesAdapter.notifyItemInserted(0);
                    this.listView2.smoothScrollBy(0, AndroidUtilities.dp(60.0f));
                } else if (i2 == 3) {
                    this.dropDown.setText(LocaleController.getString("ColorPickerMyMessages", NUM));
                    this.colorPicker.setType(2, hasChanges(3), true, this.accent.myMessagesGradientAccentColor != 0, true, 0, false);
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor, 1);
                    ColorPicker colorPicker4 = this.colorPicker;
                    Theme.ThemeAccent themeAccent = this.accent;
                    int i6 = themeAccent.myMessagesAccentColor;
                    if (i6 == 0) {
                        i6 = themeAccent.accentColor;
                    }
                    colorPicker4.setColor(i6, 0);
                }
                if (i2 == 1 || i2 == 3) {
                    if (i3 == 2) {
                        this.messagesAdapter.notifyItemRemoved(0);
                        if (this.patternLayout[1].getVisibility() == 0) {
                            showPatternsView(0, true);
                        }
                    }
                    if (this.applyingTheme.isDark()) {
                        this.colorPicker.setMinBrightness(0.2f);
                        return;
                    }
                    this.colorPicker.setMinBrightness(0.05f);
                    this.colorPicker.setMaxBrightness(0.8f);
                    return;
                }
                this.colorPicker.setMinBrightness(0.0f);
                this.colorPicker.setMaxBrightness(1.0f);
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("ChangeChatBackground", NUM));
            builder.setMessage(LocaleController.getString("ChangeWallpaperToColor", NUM));
            builder.setPositiveButton(LocaleController.getString("Change", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ThemePreviewActivity.this.lambda$selectColorType$13$ThemePreviewActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$selectColorType$13$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent.backgroundOverrideColor == 4294967296L) {
            themeAccent.backgroundOverrideColor = 0;
            themeAccent.backgroundGradientOverrideColor = 0;
            Theme.refreshThemeColors();
        }
        this.removeBackgroundOverride = true;
        Theme.resetCustomWallpaper(true);
        selectColorType(2);
    }

    private void selectPattern(int i) {
        TLRPC.TL_wallPaper tL_wallPaper;
        if (i < 0 || i >= this.patterns.size()) {
            tL_wallPaper = this.lastSelectedPattern;
        } else {
            tL_wallPaper = (TLRPC.TL_wallPaper) this.patterns.get(i);
        }
        if (tL_wallPaper != null) {
            this.backgroundImage.setImage(ImageLocation.getForDocument(tL_wallPaper.document), this.imageFilter, (ImageLocation) null, (String) null, "jpg", tL_wallPaper.document.size, 1, tL_wallPaper);
            this.selectedPattern = tL_wallPaper;
            if (this.screenType == 1) {
                this.isMotion = this.checkBoxView[0].isChecked();
            } else {
                this.isMotion = this.checkBoxView[2].isChecked();
            }
            updateButtonState(false, true);
        }
    }

    private void updateCheckboxes() {
        WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.checkBoxView;
        if (wallpaperCheckBoxViewArr != null) {
            boolean z = true;
            if (this.screenType == 1) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) wallpaperCheckBoxViewArr[1].getLayoutParams();
                this.checkBoxView[1].setChecked(this.selectedPattern != null, false);
                int dp = (layoutParams.width + AndroidUtilities.dp(9.0f)) / 2;
                float f = 0.0f;
                this.checkBoxView[1].setTranslationX(this.selectedPattern != null ? 0.0f : (float) (-dp));
                this.checkBoxView[0].setTranslationX(this.selectedPattern != null ? 0.0f : (float) dp);
                this.checkBoxView[0].setChecked(this.isMotion, false);
                WallpaperCheckBoxView wallpaperCheckBoxView = this.checkBoxView[0];
                if (this.selectedPattern == null) {
                    z = false;
                }
                wallpaperCheckBoxView.setEnabled(z);
                this.checkBoxView[0].setVisibility(this.selectedPattern != null ? 0 : 4);
                WallpaperCheckBoxView wallpaperCheckBoxView2 = this.checkBoxView[0];
                if (this.selectedPattern != null) {
                    f = 1.0f;
                }
                wallpaperCheckBoxView2.setAlpha(f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void saveAccentWallpaper() {
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent != null && !TextUtils.isEmpty(themeAccent.patternSlug)) {
            try {
                File pathToWallpaper = this.accent.getPathToWallpaper();
                Drawable background = this.backgroundImage.getBackground();
                Bitmap bitmap = this.backgroundImage.getImageReceiver().getBitmap();
                Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                background.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                background.draw(canvas);
                Paint paint = new Paint(2);
                paint.setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
                paint.setAlpha((int) (this.currentIntensity * 255.0f));
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                FileOutputStream fileOutputStream = new FileOutputStream(pathToWallpaper);
                createBitmap.compress(Bitmap.CompressFormat.JPEG, 87, fileOutputStream);
                fileOutputStream.close();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean hasChanges(int i) {
        if (this.editingTheme) {
            return false;
        }
        if (i == 1 || i == 2) {
            long j = this.backupBackgroundOverrideColor;
            if (j == 0) {
                int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                int i2 = (int) this.accent.backgroundOverrideColor;
                if (i2 == 0) {
                    i2 = defaultAccentColor;
                }
                if (i2 != defaultAccentColor) {
                    return true;
                }
            } else if (j != this.accent.backgroundOverrideColor) {
                return true;
            }
            long j2 = this.backupBackgroundGradientOverrideColor;
            if (j2 == 0) {
                int defaultAccentColor2 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                long j3 = this.accent.backgroundGradientOverrideColor;
                int i3 = (int) j3;
                if (i3 == 0 && j3 != 0) {
                    i3 = 0;
                } else if (i3 == 0) {
                    i3 = defaultAccentColor2;
                }
                if (i3 != defaultAccentColor2) {
                    return true;
                }
            } else if (j2 != this.accent.backgroundGradientOverrideColor) {
                return true;
            }
            if (this.accent.backgroundRotation != this.backupBackgroundRotation) {
                return true;
            }
        }
        if (i == 1 || i == 3) {
            int i4 = this.backupMyMessagesAccentColor;
            if (i4 == 0) {
                Theme.ThemeAccent themeAccent = this.accent;
                int i5 = themeAccent.myMessagesAccentColor;
                if (!(i5 == 0 || i5 == themeAccent.accentColor)) {
                    return true;
                }
            } else if (i4 != this.accent.myMessagesAccentColor) {
                return true;
            }
            int i6 = this.backupMyMessagesGradientAccentColor;
            if (i6 != 0) {
                if (i6 != this.accent.myMessagesGradientAccentColor) {
                    return true;
                }
            } else if (this.accent.myMessagesGradientAccentColor != 0) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004a, code lost:
        if (r7.accent.patternMotion == r7.isMotion) goto L_0x004c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0058, code lost:
        if (r7.accent.patternIntensity == r7.currentIntensity) goto L_0x00a6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkDiscard() {
        /*
            r7 = this;
            int r0 = r7.screenType
            r1 = 1
            if (r0 != r1) goto L_0x00a6
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r7.accent
            int r2 = r0.accentColor
            int r3 = r7.backupAccentColor
            if (r2 != r3) goto L_0x005a
            int r2 = r0.myMessagesAccentColor
            int r3 = r7.backupMyMessagesAccentColor
            if (r2 != r3) goto L_0x005a
            int r2 = r0.myMessagesGradientAccentColor
            int r3 = r7.backupMyMessagesGradientAccentColor
            if (r2 != r3) goto L_0x005a
            long r2 = r0.backgroundOverrideColor
            long r4 = r7.backupBackgroundOverrideColor
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x005a
            long r2 = r0.backgroundGradientOverrideColor
            long r4 = r7.backupBackgroundGradientOverrideColor
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x005a
            int r2 = r0.backgroundRotation
            int r3 = r7.backupBackgroundRotation
            if (r2 != r3) goto L_0x005a
            java.lang.String r0 = r0.patternSlug
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = r7.selectedPattern
            if (r2 == 0) goto L_0x0038
            java.lang.String r2 = r2.slug
            goto L_0x003a
        L_0x0038:
            java.lang.String r2 = ""
        L_0x003a:
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x005a
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = r7.selectedPattern
            if (r0 == 0) goto L_0x004c
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r7.accent
            boolean r0 = r0.patternMotion
            boolean r2 = r7.isMotion
            if (r0 != r2) goto L_0x005a
        L_0x004c:
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = r7.selectedPattern
            if (r0 == 0) goto L_0x00a6
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r7.accent
            float r0 = r0.patternIntensity
            float r2 = r7.currentIntensity
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x00a6
        L_0x005a:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r7.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r1 = 2131626400(0x7f0e09a0, float:1.8880035E38)
            java.lang.String r2 = "SaveChangesAlertTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131626399(0x7f0e099f, float:1.8880033E38)
            java.lang.String r2 = "SaveChangesAlertText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            r1 = 2131626398(0x7f0e099e, float:1.8880031E38)
            java.lang.String r2 = "Save"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$VhuCSQ0ekK36xL0S9AHA-K57wyo r2 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$VhuCSQ0ekK36xL0S9AHA-K57wyo
            r2.<init>()
            r0.setPositiveButton(r1, r2)
            r1 = 2131625946(0x7f0e07da, float:1.8879114E38)
            java.lang.String r2 = "PassportDiscard"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$6MCYHNXt8AHlTpbo4JBN0Y_054c r2 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$6MCYHNXt8AHlTpbo4JBN0Y_054c
            r2.<init>()
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r7.showDialog(r0)
            r0 = 0
            return r0
        L_0x00a6:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.checkDiscard():boolean");
    }

    public /* synthetic */ void lambda$checkDiscard$14$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        this.actionBar2.getActionBarMenuOnItemClick().onItemClick(4);
    }

    public /* synthetic */ void lambda$checkDiscard$15$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        cancelThemeApply(false);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        if (this.screenType == 1) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        if (this.screenType == 0 && this.accent == null) {
            this.isMotion = Theme.isWallpaperMotion();
        } else {
            if (SharedConfig.getDevicePerfomanceClass() == 0) {
                Point point = AndroidUtilities.displaySize;
                int min = Math.min(point.x, point.y);
                Point point2 = AndroidUtilities.displaySize;
                int max = Math.max(point2.x, point2.y);
                this.imageFilter = ((int) (((float) min) / AndroidUtilities.density)) + "_" + ((int) (((float) max) / AndroidUtilities.density)) + "_f";
            } else {
                this.imageFilter = ((int) (1080.0f / AndroidUtilities.density)) + "_" + ((int) (1920.0f / AndroidUtilities.density)) + "_f";
            }
            Point point3 = AndroidUtilities.displaySize;
            this.maxWallpaperSize = Math.min(1920, Math.max(point3.x, point3.y));
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
            this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
            if (this.patterns == null) {
                this.patterns = new ArrayList<>();
                MessagesStorage.getInstance(this.currentAccount).getWallpapers();
            }
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        FrameLayout frameLayout2 = this.frameLayout;
        if (!(frameLayout2 == null || this.onGlobalLayoutListener == null)) {
            frameLayout2.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
        int i = this.screenType;
        if (i == 2) {
            Bitmap bitmap = this.blurredBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.blurredBitmap = null;
            }
            Theme.applyChatServiceMessageColor();
        } else if (i == 1) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        if (!(this.screenType == 0 && this.accent == null)) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
        }
        super.onFragmentDestroy();
    }

    public void onResume() {
        super.onResume();
        DialogsAdapter dialogsAdapter2 = this.dialogsAdapter;
        if (dialogsAdapter2 != null) {
            dialogsAdapter2.notifyDataSetChanged();
        }
        MessagesAdapter messagesAdapter2 = this.messagesAdapter;
        if (messagesAdapter2 != null) {
            messagesAdapter2.notifyDataSetChanged();
        }
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(true);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(false);
        }
    }

    public void onFailedDownload(String str, boolean z) {
        updateButtonState(true, z);
    }

    public void onSuccessDownload(String str) {
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setProgress(1.0f, this.progressVisible);
        }
        updateButtonState(false, true);
    }

    public void onProgressDownload(String str, float f) {
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setProgress(f, this.progressVisible);
            if (this.radialProgress.getIcon() != 10) {
                updateButtonState(false, true);
            }
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    private void updateBlurred() {
        if (this.isBlurred && this.blurredBitmap == null) {
            Bitmap bitmap = this.currentWallpaperBitmap;
            if (bitmap != null) {
                this.originalBitmap = bitmap;
                this.blurredBitmap = Utilities.blurWallpaper(bitmap);
            } else {
                ImageReceiver imageReceiver = this.backgroundImage.getImageReceiver();
                if (imageReceiver.hasNotThumb() || imageReceiver.hasStaticThumb()) {
                    this.originalBitmap = imageReceiver.getBitmap();
                    this.blurredBitmap = Utilities.blurWallpaper(imageReceiver.getBitmap());
                }
            }
        }
        if (this.isBlurred) {
            Bitmap bitmap2 = this.blurredBitmap;
            if (bitmap2 != null) {
                this.backgroundImage.setImageBitmap(bitmap2);
                return;
            }
            return;
        }
        setCurrentImage(false);
    }

    public boolean onBackPressed() {
        if (!checkDiscard()) {
            return false;
        }
        cancelThemeApply(true);
        return true;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC.TL_wallPaper tL_wallPaper;
        if (i == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                int childCount = recyclerListView.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = this.listView.getChildAt(i3);
                    if (childAt instanceof DialogCell) {
                        ((DialogCell) childAt).update(0);
                    }
                }
            }
        } else if (i == NotificationCenter.didSetNewWallpapper) {
            if (this.page2 != null) {
                setCurrentImage(true);
            }
        } else if (i == NotificationCenter.wallpapersNeedReload) {
            Object obj = this.currentWallpaper;
            if (obj instanceof WallpapersListActivity.FileWallpaper) {
                WallpapersListActivity.FileWallpaper fileWallpaper = (WallpapersListActivity.FileWallpaper) obj;
                if (fileWallpaper.slug == null) {
                    fileWallpaper.slug = objArr[0];
                }
            }
        } else if (i == NotificationCenter.wallpapersDidLoad) {
            ArrayList arrayList = objArr[0];
            this.patterns.clear();
            int size = arrayList.size();
            boolean z = false;
            for (int i4 = 0; i4 < size; i4++) {
                TLRPC.TL_wallPaper tL_wallPaper2 = (TLRPC.TL_wallPaper) arrayList.get(i4);
                if (tL_wallPaper2.pattern) {
                    this.patterns.add(tL_wallPaper2);
                    Theme.ThemeAccent themeAccent = this.accent;
                    if (themeAccent != null && themeAccent.patternSlug.equals(tL_wallPaper2.slug)) {
                        this.selectedPattern = tL_wallPaper2;
                        setCurrentImage(false);
                        updateButtonState(false, false);
                        updateCheckboxes();
                        z = true;
                    }
                }
            }
            if (!z && (tL_wallPaper = this.selectedPattern) != null) {
                this.patterns.add(0, tL_wallPaper);
            }
            PatternsAdapter patternsAdapter2 = this.patternsAdapter;
            if (patternsAdapter2 != null) {
                patternsAdapter2.notifyDataSetChanged();
            }
            long j = 0;
            int size2 = arrayList.size();
            for (int i5 = 0; i5 < size2; i5++) {
                long j2 = ((TLRPC.TL_wallPaper) arrayList.get(i5)).id;
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (j2 >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) j2))) % 2147483648L;
            }
            TLRPC.TL_account_getWallPapers tL_account_getWallPapers = new TLRPC.TL_account_getWallPapers();
            tL_account_getWallPapers.hash = (int) j;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getWallPapers, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ThemePreviewActivity.this.lambda$didReceivedNotification$19$ThemePreviewActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$19$ThemePreviewActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ThemePreviewActivity.this.lambda$null$18$ThemePreviewActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$18$ThemePreviewActivity(TLObject tLObject) {
        Theme.ThemeAccent themeAccent;
        TLRPC.TL_wallPaper tL_wallPaper;
        if (tLObject instanceof TLRPC.TL_account_wallPapers) {
            TLRPC.TL_account_wallPapers tL_account_wallPapers = (TLRPC.TL_account_wallPapers) tLObject;
            this.patterns.clear();
            int size = tL_account_wallPapers.wallpapers.size();
            boolean z = false;
            for (int i = 0; i < size; i++) {
                TLRPC.TL_wallPaper tL_wallPaper2 = (TLRPC.TL_wallPaper) tL_account_wallPapers.wallpapers.get(i);
                if (tL_wallPaper2.pattern) {
                    this.patterns.add(tL_wallPaper2);
                    Theme.ThemeAccent themeAccent2 = this.accent;
                    if (themeAccent2 != null && themeAccent2.patternSlug.equals(tL_wallPaper2.slug)) {
                        this.selectedPattern = tL_wallPaper2;
                        setCurrentImage(false);
                        updateButtonState(false, false);
                        updateCheckboxes();
                        z = true;
                    }
                }
            }
            if (!z && (tL_wallPaper = this.selectedPattern) != null) {
                this.patterns.add(0, tL_wallPaper);
            }
            PatternsAdapter patternsAdapter2 = this.patternsAdapter;
            if (patternsAdapter2 != null) {
                patternsAdapter2.notifyDataSetChanged();
            }
            MessagesStorage.getInstance(this.currentAccount).putWallpapers(tL_account_wallPapers.wallpapers, 1);
        }
        if (this.selectedPattern == null && (themeAccent = this.accent) != null && !TextUtils.isEmpty(themeAccent.patternSlug)) {
            TLRPC.TL_account_getWallPaper tL_account_getWallPaper = new TLRPC.TL_account_getWallPaper();
            TLRPC.TL_inputWallPaperSlug tL_inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
            tL_inputWallPaperSlug.slug = this.accent.patternSlug;
            tL_account_getWallPaper.wallpaper = tL_inputWallPaperSlug;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(getConnectionsManager().sendRequest(tL_account_getWallPaper, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ThemePreviewActivity.this.lambda$null$17$ThemePreviewActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$null$17$ThemePreviewActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ThemePreviewActivity.this.lambda$null$16$ThemePreviewActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$16$ThemePreviewActivity(TLObject tLObject) {
        if (tLObject instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) tLObject;
            if (tL_wallPaper.pattern) {
                this.selectedPattern = tL_wallPaper;
                setCurrentImage(false);
                updateButtonState(false, false);
                updateCheckboxes();
                this.patterns.add(0, this.selectedPattern);
                PatternsAdapter patternsAdapter2 = this.patternsAdapter;
                if (patternsAdapter2 != null) {
                    patternsAdapter2.notifyDataSetChanged();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void cancelThemeApply(boolean z) {
        if (this.screenType != 2) {
            Theme.applyPreviousTheme();
            if (this.screenType == 1) {
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
                if (this.editingTheme) {
                    Theme.ThemeAccent themeAccent = this.accent;
                    themeAccent.accentColor = this.backupAccentColor;
                    themeAccent.myMessagesAccentColor = this.backupMyMessagesAccentColor;
                    themeAccent.myMessagesGradientAccentColor = this.backupMyMessagesGradientAccentColor;
                    themeAccent.backgroundOverrideColor = this.backupBackgroundOverrideColor;
                    themeAccent.backgroundGradientOverrideColor = this.backupBackgroundGradientOverrideColor;
                    themeAccent.backgroundRotation = this.backupBackgroundRotation;
                }
                Theme.saveThemeAccents(this.applyingTheme, false, true, false, false);
            } else {
                if (this.accent != null) {
                    Theme.saveThemeAccents(this.applyingTheme, false, this.deleteOnCancel, false, false);
                }
                this.parentLayout.rebuildAllFragmentViews(false, false);
                if (this.deleteOnCancel) {
                    Theme.ThemeInfo themeInfo = this.applyingTheme;
                    if (themeInfo.pathToFile != null && !Theme.isThemeInstalled(themeInfo)) {
                        new File(this.applyingTheme.pathToFile).delete();
                    }
                }
            }
            if (!z) {
                finishFragment();
            }
        } else if (!z) {
            finishFragment();
        }
    }

    /* access modifiers changed from: private */
    public int getButtonsColor(String str) {
        return this.useDefaultThemeForButtons ? Theme.getDefaultColor(str) : Theme.getColor(str);
    }

    /* access modifiers changed from: private */
    public void scheduleApplyColor(int i, int i2, boolean z) {
        if (i2 == -1) {
            int i3 = this.colorType;
            if (i3 == 1 || i3 == 2) {
                long j = this.backupBackgroundOverrideColor;
                if (j != 0) {
                    this.accent.backgroundOverrideColor = j;
                } else {
                    this.accent.backgroundOverrideColor = 0;
                }
                long j2 = this.backupBackgroundGradientOverrideColor;
                if (j2 != 0) {
                    this.accent.backgroundGradientOverrideColor = j2;
                } else {
                    this.accent.backgroundGradientOverrideColor = 0;
                }
                this.accent.backgroundRotation = this.backupBackgroundRotation;
                if (this.colorType == 2) {
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                    int defaultAccentColor2 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                    Theme.ThemeAccent themeAccent = this.accent;
                    int i4 = (int) themeAccent.backgroundGradientOverrideColor;
                    int i5 = (int) themeAccent.backgroundOverrideColor;
                    ColorPicker colorPicker2 = this.colorPicker;
                    if (i4 != 0) {
                        defaultAccentColor2 = i4;
                    }
                    colorPicker2.setColor(defaultAccentColor2, 1);
                    ColorPicker colorPicker3 = this.colorPicker;
                    if (i5 != 0) {
                        defaultAccentColor = i5;
                    }
                    colorPicker3.setColor(defaultAccentColor, 0);
                }
            }
            int i6 = this.colorType;
            if (i6 == 1 || i6 == 3) {
                int i7 = this.backupMyMessagesAccentColor;
                if (i7 != 0) {
                    this.accent.myMessagesAccentColor = i7;
                } else {
                    this.accent.myMessagesAccentColor = 0;
                }
                int i8 = this.backupMyMessagesGradientAccentColor;
                if (i8 != 0) {
                    this.accent.myMessagesGradientAccentColor = i8;
                } else {
                    this.accent.myMessagesGradientAccentColor = 0;
                }
                if (this.colorType == 3) {
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor, 1);
                    ColorPicker colorPicker4 = this.colorPicker;
                    Theme.ThemeAccent themeAccent2 = this.accent;
                    int i9 = themeAccent2.myMessagesAccentColor;
                    if (i9 == 0) {
                        i9 = themeAccent2.accentColor;
                    }
                    colorPicker4.setColor(i9, 0);
                }
            }
            Theme.refreshThemeColors();
            this.listView2.invalidateViews();
            return;
        }
        this.lastPickedColor = i;
        this.lastPickedColorNum = i2;
        if (z) {
            this.applyColorAction.run();
        } else if (!this.applyColorScheduled) {
            this.applyColorScheduled = true;
            this.fragmentView.postDelayed(this.applyColorAction, 16);
        }
    }

    private void applyColor(int i, int i2) {
        int i3 = this.colorType;
        if (i3 == 1) {
            this.accent.accentColor = i;
            Theme.refreshThemeColors();
        } else if (i3 == 2) {
            if (this.lastPickedColorNum == 0) {
                this.accent.backgroundOverrideColor = (long) i;
            } else {
                int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                if (i != 0 || defaultAccentColor == 0) {
                    this.accent.backgroundGradientOverrideColor = (long) i;
                } else {
                    this.accent.backgroundGradientOverrideColor = 4294967296L;
                }
            }
            Theme.refreshThemeColors();
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
        } else if (i3 == 3) {
            if (this.lastPickedColorNum == 0) {
                this.accent.myMessagesAccentColor = i;
            } else {
                this.accent.myMessagesGradientAccentColor = i;
            }
            Theme.refreshThemeColors();
            this.listView2.invalidateViews();
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
        }
        int size = this.themeDescriptions.size();
        for (int i4 = 0; i4 < size; i4++) {
            ThemeDescription themeDescription = this.themeDescriptions.get(i4);
            themeDescription.setColor(Theme.getColor(themeDescription.getCurrentKey()), false, false);
        }
        this.listView.invalidateViews();
        this.listView2.invalidateViews();
        View view = this.dotsContainer;
        if (view != null) {
            view.invalidate();
        }
    }

    private void updateButtonState(boolean z, boolean z2) {
        File file;
        String str;
        int i;
        FrameLayout frameLayout2;
        String str2;
        File file2;
        Object obj = this.selectedPattern;
        if (obj == null) {
            obj = this.currentWallpaper;
        }
        boolean z3 = obj instanceof TLRPC.TL_wallPaper;
        if (z3 || (obj instanceof MediaController.SearchImage)) {
            if (z2 && !this.progressVisible) {
                z2 = false;
            }
            if (z3) {
                TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) obj;
                str = FileLoader.getAttachFileName(tL_wallPaper.document);
                if (!TextUtils.isEmpty(str)) {
                    file = FileLoader.getPathToAttach(tL_wallPaper.document, true);
                    i = tL_wallPaper.document.size;
                } else {
                    return;
                }
            } else {
                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                TLRPC.Photo photo = searchImage.photo;
                if (photo != null) {
                    TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, this.maxWallpaperSize, true);
                    file2 = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
                    str2 = FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                    i = closestPhotoSizeWithSize.size;
                } else {
                    file2 = ImageLoader.getHttpFilePath(searchImage.imageUrl, "jpg");
                    str2 = file2.getName();
                    i = searchImage.size;
                }
                String str3 = str2;
                file = file2;
                str = str3;
                if (TextUtils.isEmpty(str)) {
                    return;
                }
            }
            boolean exists = file.exists();
            if (exists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    radialProgress2.setProgress(1.0f, z2);
                    this.radialProgress.setIcon(4, z, z2);
                }
                this.backgroundImage.invalidate();
                if (this.screenType == 2) {
                    if (i != 0) {
                        this.actionBar2.setSubtitle(AndroidUtilities.formatFileSize((long) i));
                    } else {
                        this.actionBar2.setSubtitle((CharSequence) null);
                    }
                }
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(str, (MessageObject) null, this);
                if (this.radialProgress != null) {
                    FileLoader.getInstance(this.currentAccount).isLoadingFile(str);
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(str);
                    if (fileProgress != null) {
                        this.radialProgress.setProgress(fileProgress.floatValue(), z2);
                    } else {
                        this.radialProgress.setProgress(0.0f, z2);
                    }
                    this.radialProgress.setIcon(10, z, z2);
                }
                if (this.screenType == 2) {
                    this.actionBar2.setSubtitle(LocaleController.getString("LoadingFullImage", NUM));
                }
                this.backgroundImage.invalidate();
            }
            float f = 0.5f;
            if (this.selectedPattern == null && (frameLayout2 = this.buttonsContainer) != null) {
                frameLayout2.setAlpha(exists ? 1.0f : 0.5f);
            }
            int i2 = this.screenType;
            if (i2 == 0) {
                this.doneButton.setEnabled(exists);
                TextView textView = this.doneButton;
                if (exists) {
                    f = 1.0f;
                }
                textView.setAlpha(f);
            } else if (i2 == 2) {
                this.bottomOverlayChat.setEnabled(exists);
                TextView textView2 = this.bottomOverlayChatText;
                if (exists) {
                    f = 1.0f;
                }
                textView2.setAlpha(f);
            } else {
                this.saveItem.setEnabled(exists);
                ActionBarMenuItem actionBarMenuItem = this.saveItem;
                if (exists) {
                    f = 1.0f;
                }
                actionBarMenuItem.setAlpha(f);
            }
        } else {
            RadialProgress2 radialProgress22 = this.radialProgress;
            if (radialProgress22 != null) {
                radialProgress22.setIcon(4, z, z2);
            }
        }
    }

    public void setDelegate(WallpaperActivityDelegate wallpaperActivityDelegate) {
        this.delegate = wallpaperActivityDelegate;
    }

    public void setPatterns(ArrayList<Object> arrayList) {
        this.patterns = arrayList;
        if (this.screenType == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) this.currentWallpaper;
            if (colorWallpaper.patternId != 0) {
                int i = 0;
                int size = this.patterns.size();
                while (true) {
                    if (i >= size) {
                        break;
                    }
                    TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) this.patterns.get(i);
                    if (tL_wallPaper.id == colorWallpaper.patternId) {
                        this.selectedPattern = tL_wallPaper;
                        break;
                    }
                    i++;
                }
                this.currentIntensity = colorWallpaper.intensity;
            }
        }
    }

    private void updateSelectedPattern(boolean z) {
        int childCount = this.patternsListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.patternsListView.getChildAt(i);
            if (childAt instanceof PatternCell) {
                ((PatternCell) childAt).updateSelected(z);
            }
        }
    }

    private void updateMotionButton() {
        float f = 1.0f;
        float f2 = 0.0f;
        if (this.screenType == 2) {
            this.checkBoxView[this.selectedPattern != null ? (char) 2 : 0].setVisibility(0);
            if (this.selectedPattern == null && (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
                this.checkBoxView[2].setChecked(false, true);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[2];
            WallpaperCheckBoxView wallpaperCheckBoxView = this.checkBoxView[2];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = this.selectedPattern != null ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView, property, fArr);
            WallpaperCheckBoxView wallpaperCheckBoxView2 = this.checkBoxView[0];
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (this.selectedPattern != null) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(wallpaperCheckBoxView2, property2, fArr2);
            animatorSet.playTogether(animatorArr);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ThemePreviewActivity.this.checkBoxView[ThemePreviewActivity.this.selectedPattern != null ? (char) 0 : 2].setVisibility(4);
                }
            });
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.setDuration(200);
            animatorSet.start();
            return;
        }
        if (this.checkBoxView[0].isEnabled() != (this.selectedPattern != null)) {
            if (this.selectedPattern == null) {
                this.checkBoxView[0].setChecked(false, true);
            }
            this.checkBoxView[0].setEnabled(this.selectedPattern != null);
            if (this.selectedPattern != null) {
                this.checkBoxView[0].setVisibility(0);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            int dp = (((FrameLayout.LayoutParams) this.checkBoxView[1].getLayoutParams()).width + AndroidUtilities.dp(9.0f)) / 2;
            Animator[] animatorArr2 = new Animator[1];
            WallpaperCheckBoxView wallpaperCheckBoxView3 = this.checkBoxView[0];
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            if (this.selectedPattern == null) {
                f = 0.0f;
            }
            fArr3[0] = f;
            animatorArr2[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView3, property3, fArr3);
            animatorSet2.playTogether(animatorArr2);
            Animator[] animatorArr3 = new Animator[1];
            WallpaperCheckBoxView wallpaperCheckBoxView4 = this.checkBoxView[0];
            Property property4 = View.TRANSLATION_X;
            float[] fArr4 = new float[1];
            fArr4[0] = this.selectedPattern != null ? 0.0f : (float) dp;
            animatorArr3[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView4, property4, fArr4);
            animatorSet2.playTogether(animatorArr3);
            Animator[] animatorArr4 = new Animator[1];
            WallpaperCheckBoxView wallpaperCheckBoxView5 = this.checkBoxView[1];
            Property property5 = View.TRANSLATION_X;
            float[] fArr5 = new float[1];
            if (this.selectedPattern == null) {
                f2 = (float) (-dp);
            }
            fArr5[0] = f2;
            animatorArr4[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView5, property5, fArr5);
            animatorSet2.playTogether(animatorArr4);
            animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet2.setDuration(200);
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ThemePreviewActivity.this.selectedPattern == null) {
                        ThemePreviewActivity.this.checkBoxView[0].setVisibility(4);
                    }
                }
            });
            animatorSet2.start();
        }
    }

    private void showPatternsView(int i, boolean z) {
        int i2;
        int i3 = i;
        final boolean z2 = z && i3 == 1 && this.selectedPattern != null;
        if (z) {
            if (i3 != 0) {
                this.previousSelectedPattern = this.selectedPattern;
                this.previousIntensity = this.currentIntensity;
                this.patternsAdapter.notifyDataSetChanged();
                ArrayList<Object> arrayList = this.patterns;
                if (arrayList != null) {
                    TLRPC.TL_wallPaper tL_wallPaper = this.selectedPattern;
                    if (tL_wallPaper == null) {
                        i2 = 0;
                    } else {
                        i2 = arrayList.indexOf(tL_wallPaper) + (this.screenType == 2 ? 1 : 0);
                    }
                    this.patternsLayoutManager.scrollToPositionWithOffset(i2, (this.patternsListView.getMeasuredWidth() - AndroidUtilities.dp(124.0f)) / 2);
                }
            } else if (this.screenType == 2) {
                this.previousBackgroundColor = this.backgroundColor;
                this.previousBackgroundGradientColor = this.backgroundGradientColor;
                this.previousBackgroundRotation = this.backupBackgroundRotation;
                this.colorPicker.setType(0, false, true, this.previousBackgroundGradientColor != 0, false, this.previousBackgroundRotation, false);
                this.colorPicker.setColor(this.backgroundGradientColor, 1);
                this.colorPicker.setColor(this.backgroundColor, 0);
            }
        }
        if (this.screenType == 2) {
            this.checkBoxView[z2 ? (char) 2 : 0].setVisibility(0);
        }
        this.patternViewAnimation = new AnimatorSet();
        ArrayList arrayList2 = new ArrayList();
        int i4 = i3 == 0 ? 1 : 0;
        float f = 1.0f;
        if (z) {
            this.patternLayout[i3].setVisibility(0);
            if (this.screenType == 2) {
                arrayList2.add(ObjectAnimator.ofFloat(this.listView2, View.TRANSLATION_Y, new float[]{(float) ((-this.patternLayout[i3].getMeasuredHeight()) + AndroidUtilities.dp(48.0f))}));
                WallpaperCheckBoxView wallpaperCheckBoxView = this.checkBoxView[2];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z2 ? 1.0f : 0.0f;
                arrayList2.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView, property, fArr));
                WallpaperCheckBoxView wallpaperCheckBoxView2 = this.checkBoxView[0];
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (z2) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                arrayList2.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView2, property2, fArr2));
                arrayList2.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{0.0f}));
                if (this.patternLayout[i4].getVisibility() == 0) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i4], View.ALPHA, new float[]{0.0f}));
                    arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i3], View.ALPHA, new float[]{0.0f, 1.0f}));
                    this.patternLayout[i3].setTranslationY(0.0f);
                } else {
                    FrameLayout[] frameLayoutArr = this.patternLayout;
                    arrayList2.add(ObjectAnimator.ofFloat(frameLayoutArr[i3], View.TRANSLATION_Y, new float[]{(float) frameLayoutArr[i3].getMeasuredHeight(), 0.0f}));
                }
            } else {
                if (i3 == 1) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i3], View.ALPHA, new float[]{0.0f, 1.0f}));
                } else {
                    this.patternLayout[i3].setAlpha(1.0f);
                    arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i4], View.ALPHA, new float[]{0.0f}));
                }
                this.colorPicker.hideKeyboard();
            }
        } else {
            arrayList2.add(ObjectAnimator.ofFloat(this.listView2, View.TRANSLATION_Y, new float[]{0.0f}));
            FrameLayout[] frameLayoutArr2 = this.patternLayout;
            arrayList2.add(ObjectAnimator.ofFloat(frameLayoutArr2[i3], View.TRANSLATION_Y, new float[]{(float) frameLayoutArr2[i3].getMeasuredHeight()}));
            arrayList2.add(ObjectAnimator.ofFloat(this.checkBoxView[0], View.ALPHA, new float[]{1.0f}));
            arrayList2.add(ObjectAnimator.ofFloat(this.checkBoxView[2], View.ALPHA, new float[]{0.0f}));
            arrayList2.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{1.0f}));
        }
        this.patternViewAnimation.playTogether(arrayList2);
        final boolean z3 = z;
        final int i5 = i4;
        final int i6 = i;
        this.patternViewAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AnimatorSet unused = ThemePreviewActivity.this.patternViewAnimation = null;
                if (z3 && ThemePreviewActivity.this.patternLayout[i5].getVisibility() == 0) {
                    ThemePreviewActivity.this.patternLayout[i5].setAlpha(1.0f);
                    ThemePreviewActivity.this.patternLayout[i5].setVisibility(4);
                } else if (!z3) {
                    ThemePreviewActivity.this.patternLayout[i6].setVisibility(4);
                }
                char c = 2;
                if (ThemePreviewActivity.this.screenType == 2) {
                    WallpaperCheckBoxView[] access$5700 = ThemePreviewActivity.this.checkBoxView;
                    if (z2) {
                        c = 0;
                    }
                    access$5700[c].setVisibility(4);
                } else if (i6 == 1) {
                    ThemePreviewActivity.this.patternLayout[i5].setAlpha(0.0f);
                }
            }
        });
        this.patternViewAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.patternViewAnimation.setDuration(200);
        this.patternViewAnimation.start();
    }

    private void animateMotionChange() {
        AnimatorSet animatorSet = this.motionAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.motionAnimation = new AnimatorSet();
        if (this.isMotion) {
            this.motionAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{this.parallaxScale}), ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{this.parallaxScale})});
        } else {
            this.motionAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_Y, new float[]{0.0f})});
        }
        this.motionAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.motionAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AnimatorSet unused = ThemePreviewActivity.this.motionAnimation = null;
            }
        });
        this.motionAnimation.start();
    }

    /* access modifiers changed from: private */
    public void setBackgroundColor(int i, int i2, boolean z) {
        if (i2 == 0) {
            this.backgroundColor = i;
        } else {
            this.backgroundGradientColor = i;
        }
        if (this.checkBoxView != null) {
            int i3 = 0;
            while (true) {
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.checkBoxView;
                if (i3 >= wallpaperCheckBoxViewArr.length) {
                    break;
                }
                if (wallpaperCheckBoxViewArr[i3] != null) {
                    if (i2 == 0) {
                        wallpaperCheckBoxViewArr[i3].setBackgroundColor(i);
                    } else {
                        wallpaperCheckBoxViewArr[i3].setBackgroundGradientColor(i);
                    }
                }
                i3++;
            }
        }
        if (this.backgroundGradientColor != 0) {
            this.backgroundImage.setBackground(new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.backgroundRotation), new int[]{this.backgroundColor, this.backgroundGradientColor}));
            this.patternColor = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(this.backgroundColor, this.backgroundGradientColor));
        } else {
            this.backgroundImage.setBackgroundColor(this.backgroundColor);
            this.patternColor = AndroidUtilities.getPatternColor(this.backgroundColor);
        }
        if (!Theme.hasThemeKey("chat_serviceBackground")) {
            int i4 = this.patternColor;
            Theme.applyChatServiceMessageColor(new int[]{i4, i4, i4, i4});
        }
        BackupImageView backupImageView = this.backgroundImage;
        if (backupImageView != null) {
            backupImageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
            this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
            this.backgroundImage.invalidate();
        }
        RecyclerListView recyclerListView = this.listView2;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
        FrameLayout frameLayout2 = this.buttonsContainer;
        if (frameLayout2 != null) {
            int childCount = frameLayout2.getChildCount();
            for (int i5 = 0; i5 < childCount; i5++) {
                this.buttonsContainer.getChildAt(i5).invalidate();
            }
        }
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v0, resolved type: android.graphics.drawable.ColorDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: org.telegram.ui.Components.BackgroundGradientDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v7, resolved type: android.graphics.drawable.ColorDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v8, resolved type: android.graphics.drawable.ColorDrawable} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setCurrentImage(boolean r17) {
        /*
            r16 = this;
            r0 = r16
            int r1 = r0.screenType
            if (r1 != 0) goto L_0x0015
            org.telegram.ui.ActionBar.Theme$ThemeAccent r1 = r0.accent
            if (r1 != 0) goto L_0x0015
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            r1.setBackground(r2)
            goto L_0x0203
        L_0x0015:
            int r1 = r0.screenType
            r2 = 2
            r3 = 0
            r4 = 1
            r5 = 0
            if (r1 != r2) goto L_0x0125
            java.lang.Object r1 = r0.currentWallpaper
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            r6 = 100
            if (r2 == 0) goto L_0x0050
            r15 = r1
            org.telegram.tgnet.TLRPC$TL_wallPaper r15 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r15
            if (r17 == 0) goto L_0x0032
            org.telegram.tgnet.TLRPC$Document r1 = r15.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r6)
        L_0x0032:
            org.telegram.ui.Components.BackupImageView r7 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Document r1 = r15.document
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.String r9 = r0.imageFilter
            org.telegram.tgnet.TLRPC$Document r1 = r15.document
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r3, r1)
            org.telegram.tgnet.TLRPC$Document r1 = r15.document
            int r13 = r1.size
            r14 = 1
            java.lang.String r11 = "100_100_b"
            java.lang.String r12 = "jpg"
            r7.setImage(r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x0203
        L_0x0050:
            boolean r2 = r1 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r2 == 0) goto L_0x0084
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r1
            int r2 = r1.gradientRotation
            r0.backgroundRotation = r2
            int r2 = r1.color
            r0.setBackgroundColor(r2, r5, r4)
            int r1 = r1.gradientColor
            if (r1 == 0) goto L_0x0066
            r0.setBackgroundColor(r1, r4, r4)
        L_0x0066:
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = r0.selectedPattern
            if (r1 == 0) goto L_0x0203
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.String r4 = r0.imageFilter
            r5 = 0
            r6 = 0
            org.telegram.tgnet.TLRPC$TL_wallPaper r10 = r0.selectedPattern
            org.telegram.tgnet.TLRPC$Document r1 = r10.document
            int r8 = r1.size
            r9 = 1
            java.lang.String r7 = "jpg"
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x0203
        L_0x0084:
            boolean r2 = r1 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
            if (r2 == 0) goto L_0x00d7
            android.graphics.Bitmap r2 = r0.currentWallpaperBitmap
            if (r2 == 0) goto L_0x0093
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            r1.setImageBitmap(r2)
            goto L_0x0203
        L_0x0093:
            org.telegram.ui.WallpapersListActivity$FileWallpaper r1 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r1
            java.io.File r2 = r1.originalPath
            if (r2 == 0) goto L_0x00a6
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            java.lang.String r2 = r2.getAbsolutePath()
            java.lang.String r4 = r0.imageFilter
            r1.setImage(r2, r4, r3)
            goto L_0x0203
        L_0x00a6:
            java.io.File r2 = r1.path
            if (r2 == 0) goto L_0x00b7
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            java.lang.String r2 = r2.getAbsolutePath()
            java.lang.String r4 = r0.imageFilter
            r1.setImage(r2, r4, r3)
            goto L_0x0203
        L_0x00b7:
            java.lang.String r2 = r1.slug
            java.lang.String r3 = "t"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x00cc
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedWallpaper(r5, r1)
            r1.setImageDrawable(r2)
            goto L_0x0203
        L_0x00cc:
            int r1 = r1.resId
            if (r1 == 0) goto L_0x0203
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            r2.setImageResource(r1)
            goto L_0x0203
        L_0x00d7:
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r2 == 0) goto L_0x0203
            r15 = r1
            org.telegram.messenger.MediaController$SearchImage r15 = (org.telegram.messenger.MediaController.SearchImage) r15
            org.telegram.tgnet.TLRPC$Photo r1 = r15.photo
            if (r1 == 0) goto L_0x0116
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r6)
            org.telegram.tgnet.TLRPC$Photo r2 = r15.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            int r6 = r0.maxWallpaperSize
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6, r4)
            if (r2 != r1) goto L_0x00f5
            r2 = r3
        L_0x00f5:
            if (r2 == 0) goto L_0x00fb
            int r5 = r2.size
            r13 = r5
            goto L_0x00fc
        L_0x00fb:
            r13 = 0
        L_0x00fc:
            org.telegram.ui.Components.BackupImageView r7 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Photo r3 = r15.photo
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForPhoto(r2, r3)
            java.lang.String r9 = r0.imageFilter
            org.telegram.tgnet.TLRPC$Photo r2 = r15.photo
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForPhoto(r1, r2)
            r14 = 1
            java.lang.String r11 = "100_100_b"
            java.lang.String r12 = "jpg"
            r7.setImage(r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x0203
        L_0x0116:
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            java.lang.String r2 = r15.imageUrl
            java.lang.String r3 = r0.imageFilter
            java.lang.String r4 = r15.thumbUrl
            java.lang.String r5 = "100_100_b"
            r1.setImage((java.lang.String) r2, (java.lang.String) r3, (java.lang.String) r4, (java.lang.String) r5)
            goto L_0x0203
        L_0x0125:
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r1 = r0.backgroundGradientDisposable
            if (r1 == 0) goto L_0x012e
            r1.dispose()
            r0.backgroundGradientDisposable = r3
        L_0x012e:
            java.lang.String r1 = "chat_wallpaper"
            int r1 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r1)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r3 = r0.accent
            long r6 = r3.backgroundOverrideColor
            int r3 = (int) r6
            if (r3 == 0) goto L_0x013c
            r1 = r3
        L_0x013c:
            java.lang.String r3 = "chat_wallpaper_gradient_to"
            int r3 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r3)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r6 = r0.accent
            long r6 = r6.backgroundGradientOverrideColor
            int r8 = (int) r6
            if (r8 != 0) goto L_0x0151
            r9 = 0
            int r11 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r11 == 0) goto L_0x0151
            r3 = 0
            goto L_0x0154
        L_0x0151:
            if (r8 == 0) goto L_0x0154
            r3 = r8
        L_0x0154:
            org.telegram.ui.ActionBar.Theme$ThemeAccent r6 = r0.accent
            java.lang.String r6 = r6.patternSlug
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x01b2
            boolean r6 = org.telegram.ui.ActionBar.Theme.hasCustomWallpaper()
            if (r6 != 0) goto L_0x01b2
            if (r3 == 0) goto L_0x018b
            org.telegram.ui.ActionBar.Theme$ThemeAccent r6 = r0.accent
            int r6 = r6.backgroundRotation
            android.graphics.drawable.GradientDrawable$Orientation r6 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r6)
            org.telegram.ui.Components.BackgroundGradientDrawable r7 = new org.telegram.ui.Components.BackgroundGradientDrawable
            int[] r2 = new int[r2]
            r2[r5] = r1
            r2[r4] = r3
            r7.<init>(r6, r2)
            org.telegram.ui.ThemePreviewActivity$25 r2 = new org.telegram.ui.ThemePreviewActivity$25
            r2.<init>()
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r4 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()
            r8 = 100
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r2 = r7.startDithering(r4, r2, r8)
            r0.backgroundGradientDisposable = r2
            goto L_0x0190
        L_0x018b:
            android.graphics.drawable.ColorDrawable r7 = new android.graphics.drawable.ColorDrawable
            r7.<init>(r1)
        L_0x0190:
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            r2.setBackground(r7)
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = r0.selectedPattern
            if (r2 == 0) goto L_0x01bb
            org.telegram.ui.Components.BackupImageView r6 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            java.lang.String r8 = r0.imageFilter
            r9 = 0
            r10 = 0
            org.telegram.tgnet.TLRPC$TL_wallPaper r14 = r0.selectedPattern
            org.telegram.tgnet.TLRPC$Document r2 = r14.document
            int r12 = r2.size
            r13 = 1
            java.lang.String r11 = "jpg"
            r6.setImage(r7, r8, r9, r10, r11, r12, r13, r14)
            goto L_0x01bb
        L_0x01b2:
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            r2.setBackground(r4)
        L_0x01bb:
            if (r3 != 0) goto L_0x01c4
            int r2 = org.telegram.messenger.AndroidUtilities.getPatternColor(r1)
            r0.patternColor = r2
            goto L_0x01ce
        L_0x01c4:
            int r2 = org.telegram.messenger.AndroidUtilities.getAverageColor(r1, r3)
            int r2 = org.telegram.messenger.AndroidUtilities.getPatternColor(r2)
            r0.patternColor = r2
        L_0x01ce:
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            if (r2 == 0) goto L_0x01f2
            org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r4 = r0.patternColor
            android.graphics.PorterDuff$Mode r6 = r0.blendMode
            r3.<init>(r4, r6)
            r2.setColorFilter(r3)
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
            float r3 = r0.currentIntensity
            r2.setAlpha(r3)
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            r2.invalidate()
        L_0x01f2:
            org.telegram.ui.Components.WallpaperCheckBoxView[] r2 = r0.checkBoxView
            if (r2 == 0) goto L_0x0203
        L_0x01f6:
            org.telegram.ui.Components.WallpaperCheckBoxView[] r2 = r0.checkBoxView
            int r3 = r2.length
            if (r5 >= r3) goto L_0x0203
            r2 = r2[r5]
            r2.setBackgroundColor(r1)
            int r5 = r5 + 1
            goto L_0x01f6
        L_0x0203:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.setCurrentImage(boolean):void");
    }

    public class DialogsAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<DialogCell.CustomDialog> dialogs = new ArrayList<>();
        private Context mContext;

        public DialogsAdapter(Context context) {
            this.mContext = context;
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            DialogCell.CustomDialog customDialog = new DialogCell.CustomDialog();
            customDialog.name = LocaleController.getString("ThemePreviewDialog1", NUM);
            customDialog.message = LocaleController.getString("ThemePreviewDialogMessage1", NUM);
            customDialog.id = 0;
            customDialog.unread_count = 0;
            customDialog.pinned = true;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = currentTimeMillis;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = true;
            this.dialogs.add(customDialog);
            DialogCell.CustomDialog customDialog2 = new DialogCell.CustomDialog();
            customDialog2.name = LocaleController.getString("ThemePreviewDialog2", NUM);
            customDialog2.message = LocaleController.getString("ThemePreviewDialogMessage2", NUM);
            customDialog2.id = 1;
            customDialog2.unread_count = 2;
            customDialog2.pinned = false;
            customDialog2.muted = false;
            customDialog2.type = 0;
            customDialog2.date = currentTimeMillis - 3600;
            customDialog2.verified = false;
            customDialog2.isMedia = false;
            customDialog2.sent = false;
            this.dialogs.add(customDialog2);
            DialogCell.CustomDialog customDialog3 = new DialogCell.CustomDialog();
            customDialog3.name = LocaleController.getString("ThemePreviewDialog3", NUM);
            customDialog3.message = LocaleController.getString("ThemePreviewDialogMessage3", NUM);
            customDialog3.id = 2;
            customDialog3.unread_count = 3;
            customDialog3.pinned = false;
            customDialog3.muted = true;
            customDialog3.type = 0;
            customDialog3.date = currentTimeMillis - 7200;
            customDialog3.verified = false;
            customDialog3.isMedia = true;
            customDialog3.sent = false;
            this.dialogs.add(customDialog3);
            DialogCell.CustomDialog customDialog4 = new DialogCell.CustomDialog();
            customDialog4.name = LocaleController.getString("ThemePreviewDialog4", NUM);
            customDialog4.message = LocaleController.getString("ThemePreviewDialogMessage4", NUM);
            customDialog4.id = 3;
            customDialog4.unread_count = 0;
            customDialog4.pinned = false;
            customDialog4.muted = false;
            customDialog4.type = 2;
            customDialog4.date = currentTimeMillis - 10800;
            customDialog4.verified = false;
            customDialog4.isMedia = false;
            customDialog4.sent = false;
            this.dialogs.add(customDialog4);
            DialogCell.CustomDialog customDialog5 = new DialogCell.CustomDialog();
            customDialog5.name = LocaleController.getString("ThemePreviewDialog5", NUM);
            customDialog5.message = LocaleController.getString("ThemePreviewDialogMessage5", NUM);
            customDialog5.id = 4;
            customDialog5.unread_count = 0;
            customDialog5.pinned = false;
            customDialog5.muted = false;
            customDialog5.type = 1;
            customDialog5.date = currentTimeMillis - 14400;
            customDialog5.verified = false;
            customDialog5.isMedia = false;
            customDialog5.sent = true;
            this.dialogs.add(customDialog5);
            DialogCell.CustomDialog customDialog6 = new DialogCell.CustomDialog();
            customDialog6.name = LocaleController.getString("ThemePreviewDialog6", NUM);
            customDialog6.message = LocaleController.getString("ThemePreviewDialogMessage6", NUM);
            customDialog6.id = 5;
            customDialog6.unread_count = 0;
            customDialog6.pinned = false;
            customDialog6.muted = false;
            customDialog6.type = 0;
            customDialog6.date = currentTimeMillis - 18000;
            customDialog6.verified = false;
            customDialog6.isMedia = false;
            customDialog6.sent = false;
            this.dialogs.add(customDialog6);
            DialogCell.CustomDialog customDialog7 = new DialogCell.CustomDialog();
            customDialog7.name = LocaleController.getString("ThemePreviewDialog7", NUM);
            customDialog7.message = LocaleController.getString("ThemePreviewDialogMessage7", NUM);
            customDialog7.id = 6;
            customDialog7.unread_count = 0;
            customDialog7.pinned = false;
            customDialog7.muted = false;
            customDialog7.type = 0;
            customDialog7.date = currentTimeMillis - 21600;
            customDialog7.verified = true;
            customDialog7.isMedia = false;
            customDialog7.sent = false;
            this.dialogs.add(customDialog7);
            DialogCell.CustomDialog customDialog8 = new DialogCell.CustomDialog();
            customDialog8.name = LocaleController.getString("ThemePreviewDialog8", NUM);
            customDialog8.message = LocaleController.getString("ThemePreviewDialogMessage8", NUM);
            customDialog8.id = 0;
            customDialog8.unread_count = 0;
            customDialog8.pinned = false;
            customDialog8.muted = false;
            customDialog8.type = 0;
            customDialog8.date = currentTimeMillis - 25200;
            customDialog8.verified = true;
            customDialog8.isMedia = false;
            customDialog8.sent = false;
            this.dialogs.add(customDialog8);
        }

        public int getItemCount() {
            return this.dialogs.size();
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new DialogCell(this.mContext, false, false);
            } else {
                view = i == 1 ? new LoadingCell(this.mContext) : null;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                boolean z = true;
                if (i == getItemCount() - 1) {
                    z = false;
                }
                dialogCell.useSeparator = z;
                dialogCell.setDialog(this.dialogs.get(i));
            }
        }

        public int getItemViewType(int i) {
            return i == this.dialogs.size() ? 1 : 0;
        }
    }

    public class MessagesAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList<MessageObject> messages;
        /* access modifiers changed from: private */
        public boolean showSecretMessages;
        final /* synthetic */ ThemePreviewActivity this$0;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public MessagesAdapter(ThemePreviewActivity themePreviewActivity, Context context) {
            this.this$0 = themePreviewActivity;
            this.showSecretMessages = this.this$0.screenType == 0 && Utilities.random.nextInt(100) <= 1;
            this.mContext = context;
            this.messages = new ArrayList<>();
            int currentTimeMillis = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            if (themePreviewActivity.screenType == 2) {
                TLRPC.TL_message tL_message = new TLRPC.TL_message();
                if (themePreviewActivity.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    tL_message.message = LocaleController.getString("BackgroundColorSinglePreviewLine2", NUM);
                } else {
                    tL_message.message = LocaleController.getString("BackgroundPreviewLine2", NUM);
                }
                int i = currentTimeMillis + 60;
                tL_message.date = i;
                tL_message.dialog_id = 1;
                tL_message.flags = 259;
                tL_message.from_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                tL_message.id = 1;
                tL_message.media = new TLRPC.TL_messageMediaEmpty();
                tL_message.out = true;
                tL_message.to_id = new TLRPC.TL_peerUser();
                tL_message.to_id.user_id = 0;
                MessageObject messageObject = new MessageObject(themePreviewActivity.currentAccount, tL_message, true);
                messageObject.eventId = 1;
                messageObject.resetLayout();
                this.messages.add(messageObject);
                TLRPC.TL_message tL_message2 = new TLRPC.TL_message();
                if (themePreviewActivity.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    tL_message2.message = LocaleController.getString("BackgroundColorSinglePreviewLine1", NUM);
                } else {
                    tL_message2.message = LocaleController.getString("BackgroundPreviewLine1", NUM);
                }
                tL_message2.date = i;
                tL_message2.dialog_id = 1;
                tL_message2.flags = 265;
                tL_message2.from_id = 0;
                tL_message2.id = 1;
                tL_message2.media = new TLRPC.TL_messageMediaEmpty();
                tL_message2.out = false;
                tL_message2.to_id = new TLRPC.TL_peerUser();
                tL_message2.to_id.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                MessageObject messageObject2 = new MessageObject(themePreviewActivity.currentAccount, tL_message2, true);
                messageObject2.eventId = 1;
                messageObject2.resetLayout();
                this.messages.add(messageObject2);
            } else if (themePreviewActivity.screenType == 1) {
                TLRPC.TL_message tL_message3 = new TLRPC.TL_message();
                tL_message3.media = new TLRPC.TL_messageMediaDocument();
                tL_message3.media.document = new TLRPC.TL_document();
                TLRPC.Document document = tL_message3.media.document;
                document.mime_type = "audio/mp3";
                document.file_reference = new byte[0];
                document.id = -2147483648L;
                document.size = 2621440;
                document.dc_id = Integer.MIN_VALUE;
                TLRPC.TL_documentAttributeFilename tL_documentAttributeFilename = new TLRPC.TL_documentAttributeFilename();
                tL_documentAttributeFilename.file_name = LocaleController.getString("NewThemePreviewReply2", NUM) + ".mp3";
                tL_message3.media.document.attributes.add(tL_documentAttributeFilename);
                int i2 = currentTimeMillis + 60;
                tL_message3.date = i2;
                tL_message3.dialog_id = 1;
                tL_message3.flags = 259;
                tL_message3.from_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                tL_message3.id = 1;
                tL_message3.out = true;
                tL_message3.to_id = new TLRPC.TL_peerUser();
                tL_message3.to_id.user_id = 0;
                MessageObject messageObject3 = new MessageObject(UserConfig.selectedAccount, tL_message3, true);
                TLRPC.TL_message tL_message4 = new TLRPC.TL_message();
                String string = LocaleController.getString("NewThemePreviewLine3", NUM);
                StringBuilder sb = new StringBuilder(string);
                int indexOf = string.indexOf(42);
                int lastIndexOf = string.lastIndexOf(42);
                if (!(indexOf == -1 || lastIndexOf == -1)) {
                    sb.replace(lastIndexOf, lastIndexOf + 1, "");
                    sb.replace(indexOf, indexOf + 1, "");
                    TLRPC.TL_messageEntityTextUrl tL_messageEntityTextUrl = new TLRPC.TL_messageEntityTextUrl();
                    tL_messageEntityTextUrl.offset = indexOf;
                    tL_messageEntityTextUrl.length = (lastIndexOf - indexOf) - 1;
                    tL_messageEntityTextUrl.url = "https://telegram.org";
                    tL_message4.entities.add(tL_messageEntityTextUrl);
                }
                tL_message4.message = sb.toString();
                tL_message4.date = currentTimeMillis + 960;
                tL_message4.dialog_id = 1;
                tL_message4.flags = 259;
                tL_message4.from_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                tL_message4.id = 1;
                tL_message4.media = new TLRPC.TL_messageMediaEmpty();
                tL_message4.out = true;
                tL_message4.to_id = new TLRPC.TL_peerUser();
                tL_message4.to_id.user_id = 0;
                MessageObject messageObject4 = new MessageObject(UserConfig.selectedAccount, tL_message4, true);
                messageObject4.resetLayout();
                messageObject4.eventId = 1;
                this.messages.add(messageObject4);
                TLRPC.TL_message tL_message5 = new TLRPC.TL_message();
                tL_message5.message = LocaleController.getString("NewThemePreviewLine1", NUM);
                tL_message5.date = i2;
                tL_message5.dialog_id = 1;
                tL_message5.flags = 265;
                tL_message5.from_id = 0;
                tL_message5.id = 1;
                tL_message5.reply_to_msg_id = 5;
                tL_message5.media = new TLRPC.TL_messageMediaEmpty();
                tL_message5.out = false;
                tL_message5.to_id = new TLRPC.TL_peerUser();
                tL_message5.to_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                MessageObject messageObject5 = new MessageObject(UserConfig.selectedAccount, tL_message5, true);
                messageObject5.customReplyName = LocaleController.getString("NewThemePreviewName", NUM);
                messageObject5.eventId = 1;
                messageObject5.resetLayout();
                messageObject5.replyMessageObject = messageObject3;
                this.messages.add(messageObject5);
                this.messages.add(messageObject3);
                TLRPC.TL_message tL_message6 = new TLRPC.TL_message();
                tL_message6.date = currentTimeMillis + 120;
                tL_message6.dialog_id = 1;
                tL_message6.flags = 259;
                tL_message6.out = false;
                tL_message6.from_id = 0;
                tL_message6.id = 1;
                tL_message6.media = new TLRPC.TL_messageMediaDocument();
                TLRPC.MessageMedia messageMedia = tL_message6.media;
                messageMedia.flags |= 3;
                messageMedia.document = new TLRPC.TL_document();
                TLRPC.Document document2 = tL_message6.media.document;
                document2.mime_type = "audio/ogg";
                document2.file_reference = new byte[0];
                TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio = new TLRPC.TL_documentAttributeAudio();
                tL_documentAttributeAudio.flags = 1028;
                tL_documentAttributeAudio.duration = 3;
                tL_documentAttributeAudio.voice = true;
                tL_documentAttributeAudio.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                tL_message6.media.document.attributes.add(tL_documentAttributeAudio);
                tL_message6.out = true;
                tL_message6.to_id = new TLRPC.TL_peerUser();
                tL_message6.to_id.user_id = 0;
                MessageObject messageObject6 = new MessageObject(themePreviewActivity.currentAccount, tL_message6, true);
                messageObject6.audioProgressSec = 1;
                messageObject6.audioProgress = 0.3f;
                messageObject6.useCustomPhoto = true;
                this.messages.add(messageObject6);
            } else if (this.showSecretMessages) {
                TLRPC.TL_user tL_user = new TLRPC.TL_user();
                tL_user.id = Integer.MAX_VALUE;
                tL_user.first_name = "Me";
                TLRPC.TL_user tL_user2 = new TLRPC.TL_user();
                tL_user2.id = NUM;
                tL_user2.first_name = "Serj";
                ArrayList arrayList = new ArrayList();
                arrayList.add(tL_user);
                arrayList.add(tL_user2);
                MessagesController.getInstance(themePreviewActivity.currentAccount).putUsers(arrayList, true);
                TLRPC.TL_message tL_message7 = new TLRPC.TL_message();
                tL_message7.message = "Guess why Half-Life 3 was never released.";
                int i3 = currentTimeMillis + 960;
                tL_message7.date = i3;
                tL_message7.dialog_id = -1;
                tL_message7.flags = 259;
                tL_message7.id = NUM;
                tL_message7.media = new TLRPC.TL_messageMediaEmpty();
                tL_message7.out = false;
                tL_message7.to_id = new TLRPC.TL_peerChat();
                tL_message7.to_id.chat_id = 1;
                tL_message7.from_id = tL_user2.id;
                this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tL_message7, true));
                TLRPC.TL_message tL_message8 = new TLRPC.TL_message();
                tL_message8.message = "No.\nAnd every unnecessary ping of the dev delays the release for 10 days.\nEvery request for ETA delays the release for 2 weeks.";
                tL_message8.date = i3;
                tL_message8.dialog_id = -1;
                tL_message8.flags = 259;
                tL_message8.id = 1;
                tL_message8.media = new TLRPC.TL_messageMediaEmpty();
                tL_message8.out = false;
                tL_message8.to_id = new TLRPC.TL_peerChat();
                tL_message8.to_id.chat_id = 1;
                tL_message8.from_id = tL_user2.id;
                this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tL_message8, true));
                TLRPC.TL_message tL_message9 = new TLRPC.TL_message();
                tL_message9.message = "Is source code for Android coming anytime soon?";
                tL_message9.date = currentTimeMillis + 600;
                tL_message9.dialog_id = -1;
                tL_message9.flags = 259;
                tL_message9.id = 1;
                tL_message9.media = new TLRPC.TL_messageMediaEmpty();
                tL_message9.out = false;
                tL_message9.to_id = new TLRPC.TL_peerChat();
                tL_message9.to_id.chat_id = 1;
                tL_message9.from_id = tL_user.id;
                this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tL_message9, true));
            } else {
                TLRPC.TL_message tL_message10 = new TLRPC.TL_message();
                tL_message10.message = LocaleController.getString("ThemePreviewLine1", NUM);
                int i4 = currentTimeMillis + 60;
                tL_message10.date = i4;
                tL_message10.dialog_id = 1;
                tL_message10.flags = 259;
                tL_message10.from_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                tL_message10.id = 1;
                tL_message10.media = new TLRPC.TL_messageMediaEmpty();
                tL_message10.out = true;
                tL_message10.to_id = new TLRPC.TL_peerUser();
                tL_message10.to_id.user_id = 0;
                MessageObject messageObject7 = new MessageObject(themePreviewActivity.currentAccount, tL_message10, true);
                TLRPC.TL_message tL_message11 = new TLRPC.TL_message();
                tL_message11.message = LocaleController.getString("ThemePreviewLine2", NUM);
                tL_message11.date = currentTimeMillis + 960;
                tL_message11.dialog_id = 1;
                tL_message11.flags = 259;
                tL_message11.from_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                tL_message11.id = 1;
                tL_message11.media = new TLRPC.TL_messageMediaEmpty();
                tL_message11.out = true;
                tL_message11.to_id = new TLRPC.TL_peerUser();
                tL_message11.to_id.user_id = 0;
                this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tL_message11, true));
                TLRPC.TL_message tL_message12 = new TLRPC.TL_message();
                tL_message12.date = currentTimeMillis + 130;
                tL_message12.dialog_id = 1;
                tL_message12.flags = 259;
                tL_message12.from_id = 0;
                tL_message12.id = 5;
                tL_message12.media = new TLRPC.TL_messageMediaDocument();
                TLRPC.MessageMedia messageMedia2 = tL_message12.media;
                messageMedia2.flags |= 3;
                messageMedia2.document = new TLRPC.TL_document();
                TLRPC.Document document3 = tL_message12.media.document;
                document3.mime_type = "audio/mp4";
                document3.file_reference = new byte[0];
                TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio2 = new TLRPC.TL_documentAttributeAudio();
                tL_documentAttributeAudio2.duration = 243;
                tL_documentAttributeAudio2.performer = LocaleController.getString("ThemePreviewSongPerformer", NUM);
                tL_documentAttributeAudio2.title = LocaleController.getString("ThemePreviewSongTitle", NUM);
                tL_message12.media.document.attributes.add(tL_documentAttributeAudio2);
                tL_message12.out = false;
                tL_message12.to_id = new TLRPC.TL_peerUser();
                tL_message12.to_id.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tL_message12, true));
                TLRPC.TL_message tL_message13 = new TLRPC.TL_message();
                tL_message13.message = LocaleController.getString("ThemePreviewLine3", NUM);
                tL_message13.date = i4;
                tL_message13.dialog_id = 1;
                tL_message13.flags = 265;
                tL_message13.from_id = 0;
                tL_message13.id = 1;
                tL_message13.reply_to_msg_id = 5;
                tL_message13.media = new TLRPC.TL_messageMediaEmpty();
                tL_message13.out = false;
                tL_message13.to_id = new TLRPC.TL_peerUser();
                tL_message13.to_id.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                MessageObject messageObject8 = new MessageObject(themePreviewActivity.currentAccount, tL_message13, true);
                messageObject8.customReplyName = LocaleController.getString("ThemePreviewLine3Reply", NUM);
                messageObject8.replyMessageObject = messageObject7;
                this.messages.add(messageObject8);
                TLRPC.TL_message tL_message14 = new TLRPC.TL_message();
                tL_message14.date = currentTimeMillis + 120;
                tL_message14.dialog_id = 1;
                tL_message14.flags = 259;
                tL_message14.from_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                tL_message14.id = 1;
                tL_message14.media = new TLRPC.TL_messageMediaDocument();
                TLRPC.MessageMedia messageMedia3 = tL_message14.media;
                messageMedia3.flags |= 3;
                messageMedia3.document = new TLRPC.TL_document();
                TLRPC.Document document4 = tL_message14.media.document;
                document4.mime_type = "audio/ogg";
                document4.file_reference = new byte[0];
                TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio3 = new TLRPC.TL_documentAttributeAudio();
                tL_documentAttributeAudio3.flags = 1028;
                tL_documentAttributeAudio3.duration = 3;
                tL_documentAttributeAudio3.voice = true;
                tL_documentAttributeAudio3.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                tL_message14.media.document.attributes.add(tL_documentAttributeAudio3);
                tL_message14.out = true;
                tL_message14.to_id = new TLRPC.TL_peerUser();
                tL_message14.to_id.user_id = 0;
                MessageObject messageObject9 = new MessageObject(themePreviewActivity.currentAccount, tL_message14, true);
                messageObject9.audioProgressSec = 1;
                messageObject9.audioProgress = 0.3f;
                messageObject9.useCustomPhoto = true;
                this.messages.add(messageObject9);
                this.messages.add(messageObject7);
                TLRPC.TL_message tL_message15 = new TLRPC.TL_message();
                tL_message15.date = currentTimeMillis + 10;
                tL_message15.dialog_id = 1;
                tL_message15.flags = 257;
                tL_message15.from_id = 0;
                tL_message15.id = 1;
                tL_message15.media = new TLRPC.TL_messageMediaPhoto();
                TLRPC.MessageMedia messageMedia4 = tL_message15.media;
                messageMedia4.flags |= 3;
                messageMedia4.photo = new TLRPC.TL_photo();
                TLRPC.Photo photo = tL_message15.media.photo;
                photo.file_reference = new byte[0];
                photo.has_stickers = false;
                photo.id = 1;
                photo.access_hash = 0;
                photo.date = currentTimeMillis;
                TLRPC.TL_photoSize tL_photoSize = new TLRPC.TL_photoSize();
                tL_photoSize.size = 0;
                tL_photoSize.w = 500;
                tL_photoSize.h = 302;
                tL_photoSize.type = "s";
                tL_photoSize.location = new TLRPC.TL_fileLocationUnavailable();
                tL_message15.media.photo.sizes.add(tL_photoSize);
                tL_message15.message = LocaleController.getString("ThemePreviewLine4", NUM);
                tL_message15.out = false;
                tL_message15.to_id = new TLRPC.TL_peerUser();
                tL_message15.to_id.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                MessageObject messageObject10 = new MessageObject(themePreviewActivity.currentAccount, tL_message15, true);
                messageObject10.useCustomPhoto = true;
                this.messages.add(messageObject10);
            }
            TLRPC.TL_message tL_message16 = new TLRPC.TL_message();
            tL_message16.message = LocaleController.formatDateChat((long) currentTimeMillis);
            tL_message16.id = 0;
            tL_message16.date = currentTimeMillis;
            MessageObject messageObject11 = new MessageObject(themePreviewActivity.currentAccount, tL_message16, false);
            messageObject11.type = 10;
            messageObject11.contentType = 1;
            messageObject11.isDateObject = true;
            this.messages.add(messageObject11);
        }

        private boolean hasButtons() {
            if (this.this$0.buttonsContainer != null) {
                if (this.this$0.screenType != 2) {
                    return this.this$0.screenType == 1 && this.this$0.colorType == 2;
                }
                return true;
            }
        }

        public int getItemCount() {
            int size = this.messages.size();
            return hasButtons() ? size + 1 : size;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: org.telegram.ui.Cells.ChatActionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: org.telegram.ui.Cells.ChatMessageCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v14, resolved type: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r4, int r5) {
            /*
                r3 = this;
                r4 = -2
                if (r5 != 0) goto L_0x0013
                org.telegram.ui.Cells.ChatMessageCell r5 = new org.telegram.ui.Cells.ChatMessageCell
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
                org.telegram.ui.ThemePreviewActivity$MessagesAdapter$1 r0 = new org.telegram.ui.ThemePreviewActivity$MessagesAdapter$1
                r0.<init>()
                r5.setDelegate(r0)
                goto L_0x0064
            L_0x0013:
                r0 = 1
                if (r5 != r0) goto L_0x0026
                org.telegram.ui.Cells.ChatActionCell r5 = new org.telegram.ui.Cells.ChatActionCell
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
                org.telegram.ui.ThemePreviewActivity$MessagesAdapter$2 r0 = new org.telegram.ui.ThemePreviewActivity$MessagesAdapter$2
                r0.<init>()
                r5.setDelegate(r0)
                goto L_0x0064
            L_0x0026:
                r0 = 2
                if (r5 != r0) goto L_0x0063
                org.telegram.ui.ThemePreviewActivity r5 = r3.this$0
                android.widget.FrameLayout r5 = r5.buttonsContainer
                android.view.ViewParent r5 = r5.getParent()
                if (r5 == 0) goto L_0x004a
                org.telegram.ui.ThemePreviewActivity r5 = r3.this$0
                android.widget.FrameLayout r5 = r5.buttonsContainer
                android.view.ViewParent r5 = r5.getParent()
                android.view.ViewGroup r5 = (android.view.ViewGroup) r5
                org.telegram.ui.ThemePreviewActivity r0 = r3.this$0
                android.widget.FrameLayout r0 = r0.buttonsContainer
                r5.removeView(r0)
            L_0x004a:
                org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3 r5 = new org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
                org.telegram.ui.ThemePreviewActivity r0 = r3.this$0
                android.widget.FrameLayout r0 = r0.buttonsContainer
                r1 = 34
                r2 = 17
                android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r1, (int) r2)
                r5.addView(r0, r1)
                goto L_0x0064
            L_0x0063:
                r5 = 0
            L_0x0064:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1 = -1
                r0.<init>((int) r1, (int) r4)
                r5.setLayoutParams(r0)
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r5)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.MessagesAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX WARNING: Removed duplicated region for block: B:19:0x0068  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
            /*
                r9 = this;
                int r0 = r10.getItemViewType()
                r1 = 2
                if (r0 == r1) goto L_0x00ac
                boolean r0 = r9.hasButtons()
                if (r0 == 0) goto L_0x000f
                int r11 = r11 + -1
            L_0x000f:
                java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r9.messages
                java.lang.Object r0 = r0.get(r11)
                org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
                android.view.View r1 = r10.itemView
                boolean r2 = r1 instanceof org.telegram.ui.Cells.ChatMessageCell
                if (r2 == 0) goto L_0x009e
                org.telegram.ui.Cells.ChatMessageCell r1 = (org.telegram.ui.Cells.ChatMessageCell) r1
                r2 = 0
                r1.isChat = r2
                int r3 = r11 + -1
                int r4 = r9.getItemViewType(r3)
                r5 = 1
                int r11 = r11 + r5
                int r6 = r9.getItemViewType(r11)
                org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
                org.telegram.tgnet.TLRPC$ReplyMarkup r7 = r7.reply_markup
                boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup
                r8 = 300(0x12c, float:4.2E-43)
                if (r7 != 0) goto L_0x0061
                int r7 = r10.getItemViewType()
                if (r4 != r7) goto L_0x0061
                java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r9.messages
                java.lang.Object r3 = r4.get(r3)
                org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
                boolean r4 = r3.isOutOwner()
                boolean r7 = r0.isOutOwner()
                if (r4 != r7) goto L_0x0061
                org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
                int r3 = r3.date
                org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
                int r4 = r4.date
                int r3 = r3 - r4
                int r3 = java.lang.Math.abs(r3)
                if (r3 > r8) goto L_0x0061
                r3 = 1
                goto L_0x0062
            L_0x0061:
                r3 = 0
            L_0x0062:
                int r10 = r10.getItemViewType()
                if (r6 != r10) goto L_0x0092
                java.util.ArrayList<org.telegram.messenger.MessageObject> r10 = r9.messages
                java.lang.Object r10 = r10.get(r11)
                org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
                org.telegram.tgnet.TLRPC$Message r11 = r10.messageOwner
                org.telegram.tgnet.TLRPC$ReplyMarkup r11 = r11.reply_markup
                boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup
                if (r11 != 0) goto L_0x0092
                boolean r11 = r10.isOutOwner()
                boolean r4 = r0.isOutOwner()
                if (r11 != r4) goto L_0x0092
                org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
                int r10 = r10.date
                org.telegram.tgnet.TLRPC$Message r11 = r0.messageOwner
                int r11 = r11.date
                int r10 = r10 - r11
                int r10 = java.lang.Math.abs(r10)
                if (r10 > r8) goto L_0x0092
                r2 = 1
            L_0x0092:
                boolean r10 = r9.showSecretMessages
                r1.isChat = r10
                r1.setFullyDraw(r5)
                r10 = 0
                r1.setMessageObject(r0, r10, r3, r2)
                goto L_0x00ac
            L_0x009e:
                boolean r10 = r1 instanceof org.telegram.ui.Cells.ChatActionCell
                if (r10 == 0) goto L_0x00ac
                org.telegram.ui.Cells.ChatActionCell r1 = (org.telegram.ui.Cells.ChatActionCell) r1
                r1.setMessageObject(r0)
                r10 = 1065353216(0x3var_, float:1.0)
                r1.setAlpha(r10)
            L_0x00ac:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.MessagesAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (hasButtons()) {
                if (i == 0) {
                    return 2;
                }
                i--;
            }
            if (i < 0 || i >= this.messages.size()) {
                return 4;
            }
            return this.messages.get(i).contentType;
        }
    }

    private class PatternsAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public PatternsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            if (ThemePreviewActivity.this.patterns != null) {
                return ThemePreviewActivity.this.patterns.size();
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new PatternCell(this.mContext, ThemePreviewActivity.this.maxWallpaperSize, new PatternCell.PatternCellDelegate() {
                public TLRPC.TL_wallPaper getSelectedPattern() {
                    return ThemePreviewActivity.this.selectedPattern;
                }

                public int getPatternColor() {
                    return ThemePreviewActivity.this.patternColor;
                }

                public int getBackgroundColor() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundColor;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                    int i = (int) ThemePreviewActivity.this.accent.backgroundOverrideColor;
                    return i != 0 ? i : defaultAccentColor;
                }

                public int getBackgroundGradientColor() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                    int i = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor;
                    return i != 0 ? i : defaultAccentColor;
                }

                public int getBackgroundGradientAngle() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundRotation;
                    }
                    return ThemePreviewActivity.this.accent.backgroundRotation;
                }
            }));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            PatternCell patternCell = (PatternCell) viewHolder.itemView;
            patternCell.setPattern((TLRPC.TL_wallPaper) ThemePreviewActivity.this.patterns.get(i));
            patternCell.getImageReceiver().setColorFilter(new PorterDuffColorFilter(ThemePreviewActivity.this.patternColor, ThemePreviewActivity.this.blendMode));
        }
    }

    private List<ThemeDescription> getThemeDescriptionsInternal() {
        $$Lambda$ThemePreviewActivity$x1lSdUb2JICwuHjfag8JNNqwc r9 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ThemePreviewActivity.this.lambda$getThemeDescriptionsInternal$20$ThemePreviewActivity();
            }
        };
        ArrayList arrayList = new ArrayList();
        $$Lambda$ThemePreviewActivity$x1lSdUb2JICwuHjfag8JNNqwc r7 = r9;
        arrayList.add(new ThemeDescription(this.page1, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.viewPager, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBTITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubtitle"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView2, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionPressedBackground"));
        if (!this.useDefaultThemeForButtons) {
            arrayList.add(new ThemeDescription(this.saveButtonsContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
            arrayList.add(new ThemeDescription(this.doneButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
        }
        ColorPicker colorPicker2 = this.colorPicker;
        if (colorPicker2 != null) {
            colorPicker2.provideThemeDescriptions(arrayList);
        }
        if (this.patternLayout != null) {
            int i = 0;
            while (true) {
                FrameLayout[] frameLayoutArr = this.patternLayout;
                if (i >= frameLayoutArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription(frameLayoutArr[i], 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
                arrayList.add(new ThemeDescription(this.patternLayout[i], 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
                i++;
            }
            int i2 = 0;
            while (true) {
                FrameLayout[] frameLayoutArr2 = this.patternsButtonsContainer;
                if (i2 >= frameLayoutArr2.length) {
                    break;
                }
                arrayList.add(new ThemeDescription(frameLayoutArr2[i2], 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
                arrayList.add(new ThemeDescription(this.patternsButtonsContainer[i2], 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
                i2++;
            }
            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
            arrayList.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
            int i3 = 0;
            while (true) {
                TextView[] textViewArr = this.patternsSaveButton;
                if (i3 >= textViewArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription(textViewArr[i3], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
                i3++;
            }
            int i4 = 0;
            while (true) {
                TextView[] textViewArr2 = this.patternsCancelButton;
                if (i4 >= textViewArr2.length) {
                    break;
                }
                arrayList.add(new ThemeDescription(textViewArr2[i4], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
                i4++;
            }
            arrayList.add(new ThemeDescription((View) this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"innerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
            arrayList.add(new ThemeDescription((View) this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"outerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
            arrayList.add(new ThemeDescription((View) this.intensityCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextIn"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextOut"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheck"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckRead"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckReadSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentCheck"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyLine"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyLine"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyNameText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyNameText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMessageText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMessageText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageSelectedText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageSelectedText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeSelectedText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeSelectedText"));
        }
        return arrayList;
    }

    public /* synthetic */ void lambda$getThemeDescriptionsInternal$20$ThemePreviewActivity() {
        ActionBarMenuItem actionBarMenuItem = this.dropDownContainer;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
            this.dropDownContainer.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        }
        Drawable drawable = this.sheetDrawable;
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhite"), PorterDuff.Mode.MULTIPLY));
        }
    }
}

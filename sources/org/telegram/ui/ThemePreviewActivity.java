package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_account_getWallPaper;
import org.telegram.tgnet.TLRPC$TL_account_getWallPapers;
import org.telegram.tgnet.TLRPC$TL_account_wallPapers;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC$TL_user;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.PatternCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ColorPicker;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.WallpaperCheckBoxView;
import org.telegram.ui.Components.WallpaperParallaxEffect;
import org.telegram.ui.WallpapersListActivity;

public class ThemePreviewActivity extends BaseFragment implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
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
    public int backgroundGradientColor1;
    /* access modifiers changed from: private */
    public int backgroundGradientColor2;
    /* access modifiers changed from: private */
    public int backgroundGradientColor3;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    /* access modifiers changed from: private */
    public BackupImageView backgroundImage;
    /* access modifiers changed from: private */
    public int backgroundRotation;
    private int backupAccentColor;
    private long backupBackgroundGradientOverrideColor1;
    private long backupBackgroundGradientOverrideColor2;
    private long backupBackgroundGradientOverrideColor3;
    private long backupBackgroundOverrideColor;
    private int backupBackgroundRotation;
    private float backupIntensity;
    private int backupMyMessagesAccentColor;
    private int backupMyMessagesGradientAccentColor;
    private String backupSlug;
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
    public int checkColor;
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
    private TLRPC$TL_wallPaper lastSelectedPattern;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public RecyclerListView listView2;
    private String loadingFile;
    private File loadingFileObject;
    private TLRPC$PhotoSize loadingSize;
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
    private HashMap<Long, Object> patternsDict;
    private LinearLayoutManager patternsLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView patternsListView;
    private TextView[] patternsSaveButton;
    /* access modifiers changed from: private */
    public ImageView playAnimationImageView;
    /* access modifiers changed from: private */
    public FrameLayout playAnimationView;
    /* access modifiers changed from: private */
    public AnimatorSet playViewAnimator;
    private int previousBackgroundColor;
    private int previousBackgroundGradientColor1;
    private int previousBackgroundGradientColor2;
    private int previousBackgroundGradientColor3;
    private int previousBackgroundRotation;
    private float previousIntensity;
    private TLRPC$TL_wallPaper previousSelectedPattern;
    /* access modifiers changed from: private */
    public boolean progressVisible;
    /* access modifiers changed from: private */
    public RadialProgress2 radialProgress;
    /* access modifiers changed from: private */
    public boolean removeBackgroundOverride;
    private boolean rotatePreview;
    private FrameLayout saveButtonsContainer;
    private ActionBarMenuItem saveItem;
    /* access modifiers changed from: private */
    public final int screenType;
    /* access modifiers changed from: private */
    public TLRPC$TL_wallPaper selectedPattern;
    /* access modifiers changed from: private */
    public Drawable sheetDrawable;
    private boolean showColor;
    private List<ThemeDescription> themeDescriptions;
    /* access modifiers changed from: private */
    public UndoView undoView;
    private boolean useDefaultThemeForButtons;
    /* access modifiers changed from: private */
    public ViewPager viewPager;
    /* access modifiers changed from: private */
    public boolean wasScroll;
    /* access modifiers changed from: private */
    public long watchForKeyboardEndTime;

    public interface WallpaperActivityDelegate {
        void didSetNewBackground();
    }

    static /* synthetic */ void lambda$createView$1(View view, int i) {
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return false;
    }

    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ThemePreviewActivity() {
        this.applyColorScheduled = false;
        applyColor(this.lastPickedColor, this.lastPickedColorNum);
        this.lastPickedColorNum = -1;
    }

    public ThemePreviewActivity(Object obj, Bitmap bitmap) {
        this(obj, bitmap, false, false);
    }

    public ThemePreviewActivity(Object obj, Bitmap bitmap, boolean z, boolean z2) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.lastPickedColorNum = -1;
        this.applyColorAction = new Runnable() {
            public final void run() {
                ThemePreviewActivity.this.lambda$new$0$ThemePreviewActivity();
            }
        };
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.patternsDict = new HashMap<>();
        this.currentIntensity = 0.5f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.loadingFile = null;
        this.loadingFileObject = null;
        this.loadingSize = null;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = 2;
        this.showColor = z2;
        this.currentWallpaper = obj;
        this.currentWallpaperBitmap = bitmap;
        this.rotatePreview = z;
        if (obj instanceof WallpapersListActivity.ColorWallpaper) {
            WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj;
            this.isMotion = colorWallpaper.motion;
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = colorWallpaper.pattern;
            this.selectedPattern = tLRPC$TL_wallPaper;
            if (tLRPC$TL_wallPaper != null) {
                float f = colorWallpaper.intensity;
                this.currentIntensity = f;
                if (f < 0.0f && !Theme.getActiveTheme().isDark()) {
                    this.currentIntensity *= -1.0f;
                }
            }
        }
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo) {
        this(themeInfo, false, 0, false, false);
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo, boolean z, int i, boolean z2, boolean z3) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.lastPickedColorNum = -1;
        this.applyColorAction = new Runnable() {
            public final void run() {
                ThemePreviewActivity.this.lambda$new$0$ThemePreviewActivity();
            }
        };
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.patternsDict = new HashMap<>();
        this.currentIntensity = 0.5f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.loadingFile = null;
        this.loadingFileObject = null;
        this.loadingSize = null;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = i;
        this.nightTheme = z3;
        this.applyingTheme = themeInfo;
        this.deleteOnCancel = z;
        this.editingTheme = z2;
        if (i == 1) {
            Theme.ThemeAccent accent2 = themeInfo.getAccent(!z2);
            this.accent = accent2;
            this.useDefaultThemeForButtons = false;
            this.backupAccentColor = accent2.accentColor;
            this.backupMyMessagesAccentColor = accent2.myMessagesAccentColor;
            this.backupMyMessagesGradientAccentColor = accent2.myMessagesGradientAccentColor;
            this.backupBackgroundOverrideColor = accent2.backgroundOverrideColor;
            this.backupBackgroundGradientOverrideColor1 = accent2.backgroundGradientOverrideColor1;
            this.backupBackgroundGradientOverrideColor2 = accent2.backgroundGradientOverrideColor2;
            this.backupBackgroundGradientOverrideColor3 = accent2.backgroundGradientOverrideColor3;
            this.backupIntensity = accent2.patternIntensity;
            this.backupSlug = accent2.patternSlug;
            this.backupBackgroundRotation = accent2.backgroundRotation;
        } else {
            Theme.ThemeAccent accent3 = themeInfo.getAccent(false);
            this.accent = accent3;
            if (accent3 != null) {
                this.selectedPattern = accent3.pattern;
            }
        }
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent != null) {
            this.isMotion = themeAccent.patternMotion;
            if (!TextUtils.isEmpty(themeAccent.patternSlug)) {
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

    /* JADX WARNING: Code restructure failed: missing block: B:70:0x02d9, code lost:
        if ("d".equals(((org.telegram.ui.WallpapersListActivity.ColorWallpaper) r0).slug) == false) goto L_0x02e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x02df, code lost:
        if ((r6.currentWallpaper instanceof org.telegram.tgnet.TLRPC$TL_wallPaper) != false) goto L_0x02e1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x04ca  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x04cc  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x04d4  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x04fa  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0551  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x05ea  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x06f7  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x06fb  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x07f1  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0b30  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x0ba5  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x0baa  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x0bdf  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0d31  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0277  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0292  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x02a9  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0482  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0494  */
    @android.annotation.SuppressLint({"Recycle"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r36) {
        /*
            r35 = this;
            r6 = r35
            r7 = r36
            r8 = 1
            r6.hasOwnBackground = r8
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            r9 = 0
            if (r0 == 0) goto L_0x0013
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r0.setOccupyStatusBar(r9)
        L_0x0013:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.page1 = r0
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            r1 = 2131165473(0x7var_, float:1.7945164E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItem((int) r9, (int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.setIsSearchField(r8)
            org.telegram.ui.ThemePreviewActivity$1 r1 = new org.telegram.ui.ThemePreviewActivity$1
            r1.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.setActionBarMenuItemSearchListener(r1)
            r1 = 2131627349(0x7f0e0d55, float:1.888196E38)
            java.lang.String r2 = "Search"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setSearchFieldHint(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.MenuDrawable r1 = new org.telegram.ui.ActionBar.MenuDrawable
            r1.<init>()
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r0.setAddToContainer(r9)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r1 = 2131627806(0x7f0e0f1e, float:1.8882887E38)
            java.lang.String r2 = "ThemePreview"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            org.telegram.ui.ThemePreviewActivity$2 r0 = new org.telegram.ui.ThemePreviewActivity$2
            r0.<init>(r7)
            r6.page1 = r0
            java.lang.String r10 = "windowBackgroundWhite"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout r0 = r6.page1
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r11 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r2)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = new org.telegram.ui.Components.RecyclerListView
            r0.<init>(r7)
            r6.listView = r0
            r0.setVerticalScrollBarEnabled(r8)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            r12 = 0
            r0.setItemAnimator(r12)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            r0.setLayoutAnimation(r12)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            androidx.recyclerview.widget.LinearLayoutManager r1 = new androidx.recyclerview.widget.LinearLayoutManager
            r1.<init>(r7, r8, r9)
            r0.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            r13 = 2
            if (r1 == 0) goto L_0x00a3
            r1 = 1
            goto L_0x00a4
        L_0x00a3:
            r1 = 2
        L_0x00a4:
            r0.setVerticalScrollbarPosition(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            int r1 = r6.screenType
            if (r1 == 0) goto L_0x00b0
            r1 = 1094713344(0x41400000, float:12.0)
            goto L_0x00b1
        L_0x00b0:
            r1 = 0
        L_0x00b1:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setPadding(r9, r9, r9, r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$NB93XeUo1-bjdI0j3PI-RhsfBuw r1 = org.telegram.ui.$$Lambda$ThemePreviewActivity$NB93XeUo1bjdI0j3PIRhsfBuw.INSTANCE
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            android.widget.FrameLayout r0 = r6.page1
            org.telegram.ui.Components.RecyclerListView r1 = r6.listView
            r15 = 51
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r11, r15)
            r0.addView(r1, r2)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r7)
            r6.floatingButton = r0
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            r16 = 1113587712(0x42600000, float:56.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            java.lang.String r1 = "chats_actionBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            java.lang.String r2 = "chats_actionPressedBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r0, r1, r2)
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 21
            if (r1 >= r2) goto L_0x0120
            android.content.res.Resources r3 = r36.getResources()
            r4 = 2131165413(0x7var_e5, float:1.7945042E38)
            android.graphics.drawable.Drawable r3 = r3.getDrawable(r4)
            android.graphics.drawable.Drawable r3 = r3.mutate()
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r14)
            r3.setColorFilter(r4)
            org.telegram.ui.Components.CombinedDrawable r4 = new org.telegram.ui.Components.CombinedDrawable
            r4.<init>(r3, r0, r9, r9)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r4.setIconSize(r0, r3)
            r0 = r4
        L_0x0120:
            android.widget.ImageView r3 = r6.floatingButton
            r3.setBackgroundDrawable(r0)
            android.widget.ImageView r0 = r6.floatingButton
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            java.lang.String r4 = "chats_actionIcon"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r5)
            r0.setColorFilter(r3)
            android.widget.ImageView r0 = r6.floatingButton
            r3 = 2131165412(0x7var_e4, float:1.794504E38)
            r0.setImageResource(r3)
            r14 = 1082130432(0x40800000, float:4.0)
            if (r1 < r2) goto L_0x01a6
            android.animation.StateListAnimator r0 = new android.animation.StateListAnimator
            r0.<init>()
            int[] r3 = new int[r8]
            r4 = 16842919(0x10100a7, float:2.3694026E-38)
            r3[r9] = r4
            android.widget.ImageView r4 = r6.floatingButton
            android.util.Property r5 = android.view.View.TRANSLATION_Z
            float[] r11 = new float[r13]
            r18 = 1073741824(0x40000000, float:2.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r15 = (float) r15
            r11[r9] = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r15 = (float) r15
            r11[r8] = r15
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r11)
            r14 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r4 = r4.setDuration(r14)
            r0.addState(r3, r4)
            int[] r3 = new int[r9]
            android.widget.ImageView r4 = r6.floatingButton
            float[] r14 = new float[r13]
            r11 = 1082130432(0x40800000, float:4.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r15 = (float) r15
            r14[r9] = r15
            r15 = 1073741824(0x40000000, float:2.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r14[r8] = r15
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r14)
            r14 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r4 = r4.setDuration(r14)
            r0.addState(r3, r4)
            android.widget.ImageView r3 = r6.floatingButton
            r3.setStateListAnimator(r0)
            android.widget.ImageView r0 = r6.floatingButton
            org.telegram.ui.ThemePreviewActivity$3 r3 = new org.telegram.ui.ThemePreviewActivity$3
            r3.<init>()
            r0.setOutlineProvider(r3)
        L_0x01a6:
            android.widget.FrameLayout r0 = r6.page1
            android.widget.ImageView r3 = r6.floatingButton
            if (r1 < r2) goto L_0x01b1
            r4 = 56
            r19 = 56
            goto L_0x01b5
        L_0x01b1:
            r4 = 60
            r19 = 60
        L_0x01b5:
            if (r1 < r2) goto L_0x01ba
            r20 = 1113587712(0x42600000, float:56.0)
            goto L_0x01be
        L_0x01ba:
            r1 = 1114636288(0x42700000, float:60.0)
            r20 = 1114636288(0x42700000, float:60.0)
        L_0x01be:
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            r14 = 3
            if (r1 == 0) goto L_0x01c5
            r2 = 3
            goto L_0x01c6
        L_0x01c5:
            r2 = 5
        L_0x01c6:
            r21 = r2 | 80
            if (r1 == 0) goto L_0x01cd
            r22 = 1096810496(0x41600000, float:14.0)
            goto L_0x01cf
        L_0x01cd:
            r22 = 0
        L_0x01cf:
            r23 = 0
            if (r1 == 0) goto L_0x01d6
            r24 = 0
            goto L_0x01d8
        L_0x01d6:
            r24 = 1096810496(0x41600000, float:14.0)
        L_0x01d8:
            r25 = 1096810496(0x41600000, float:14.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r3, r1)
            org.telegram.ui.ThemePreviewActivity$DialogsAdapter r0 = new org.telegram.ui.ThemePreviewActivity$DialogsAdapter
            r0.<init>(r7)
            r6.dialogsAdapter = r0
            org.telegram.ui.Components.RecyclerListView r1 = r6.listView
            r1.setAdapter(r0)
            org.telegram.ui.ThemePreviewActivity$4 r0 = new org.telegram.ui.ThemePreviewActivity$4
            r0.<init>(r7)
            r6.page2 = r0
            org.telegram.ui.ThemePreviewActivity$MessagesAdapter r0 = new org.telegram.ui.ThemePreviewActivity$MessagesAdapter
            r0.<init>(r6, r7)
            r6.messagesAdapter = r0
            org.telegram.ui.ActionBar.ActionBar r0 = r35.createActionBar(r36)
            r6.actionBar2 = r0
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x020c
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            r0.setOccupyStatusBar(r9)
        L_0x020c:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            org.telegram.ui.ActionBar.BackDrawable r1 = new org.telegram.ui.ActionBar.BackDrawable
            r1.<init>(r9)
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            org.telegram.ui.ThemePreviewActivity$5 r1 = new org.telegram.ui.ThemePreviewActivity$5
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.ThemePreviewActivity$6 r0 = new org.telegram.ui.ThemePreviewActivity$6
            r0.<init>(r7)
            r6.backgroundImage = r0
            int r0 = r6.screenType
            if (r0 == r8) goto L_0x0245
            java.lang.Object r0 = r6.currentWallpaper
            boolean r1 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r1 == 0) goto L_0x0232
            goto L_0x0245
        L_0x0232:
            boolean r1 = r0 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
            if (r1 == 0) goto L_0x0243
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r0
            java.lang.String r0 = r0.slug
            java.lang.String r1 = "t"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0243
            goto L_0x0257
        L_0x0243:
            r5 = 2
            goto L_0x025a
        L_0x0245:
            java.lang.Object r0 = r6.currentWallpaper
            boolean r1 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r1 == 0) goto L_0x0259
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r0
            java.lang.String r0 = r0.slug
            java.lang.String r1 = "d"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0259
        L_0x0257:
            r5 = 0
            goto L_0x025a
        L_0x0259:
            r5 = 3
        L_0x025a:
            android.widget.FrameLayout r0 = r6.page2
            org.telegram.ui.Components.BackupImageView r1 = r6.backgroundImage
            r19 = -1
            r20 = -1082130432(0xffffffffbvar_, float:-1.0)
            r21 = 51
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r1, r2)
            int r0 = r6.screenType
            if (r0 != r13) goto L_0x0285
            org.telegram.ui.Components.BackupImageView r0 = r6.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$pU8TdyH3kQzj0OtT5ZJodkaFnzc r1 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$pU8TdyH3kQzj0OtT5ZJodkaFnzc
            r1.<init>()
            r0.setDelegate(r1)
        L_0x0285:
            org.telegram.ui.ThemePreviewActivity$MessagesAdapter r0 = r6.messagesAdapter
            boolean r0 = r0.showSecretMessages
            r4 = 4
            r18 = 1092616192(0x41200000, float:10.0)
            java.lang.String r19 = "fonts/rmedium.ttf"
            if (r0 == 0) goto L_0x02a9
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            java.lang.String r1 = "Telegram Beta Chat"
            r0.setTitle(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            r1 = 505(0x1f9, float:7.08E-43)
            java.lang.String r2 = "Members"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            r0.setSubtitle(r1)
        L_0x02a6:
            r15 = r5
            goto L_0x0464
        L_0x02a9:
            int r0 = r6.screenType
            if (r0 != r13) goto L_0x02ef
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            r1 = 2131624536(0x7f0e0258, float:1.8876254E38)
            java.lang.String r2 = "BackgroundPreview"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r0 == 0) goto L_0x02c9
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r0.getAccent(r9)
            if (r0 != 0) goto L_0x02e1
        L_0x02c9:
            java.lang.Object r0 = r6.currentWallpaper
            boolean r1 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r1 == 0) goto L_0x02db
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r0
            java.lang.String r0 = r0.slug
            java.lang.String r1 = "d"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x02e1
        L_0x02db:
            java.lang.Object r0 = r6.currentWallpaper
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r0 == 0) goto L_0x02a6
        L_0x02e1:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            r1 = 5
            r2 = 2131165825(0x7var_, float:1.7945878E38)
            r0.addItem((int) r1, (int) r2)
            goto L_0x02a6
        L_0x02ef:
            if (r0 != r8) goto L_0x041c
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            org.telegram.ui.ActionBar.ActionBarMenu r3 = r0.createMenu()
            r0 = 2131627327(0x7f0e0d3f, float:1.8881915E38)
            java.lang.String r1 = "Save"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.String r0 = r0.toUpperCase()
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r3.addItem((int) r4, (java.lang.CharSequence) r0)
            r6.saveItem = r0
            org.telegram.ui.ThemePreviewActivity$7 r2 = new org.telegram.ui.ThemePreviewActivity$7
            r20 = 0
            r21 = 0
            r0 = r2
            r1 = r35
            r11 = r2
            r2 = r36
            r4 = r20
            r15 = r5
            r5 = r21
            r0.<init>(r2, r3, r4, r5)
            r6.dropDownContainer = r11
            r11.setSubMenuOpenSide(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            r1 = 2131624957(0x7f0e03fd, float:1.8877108E38)
            java.lang.String r2 = "ColorPickerBackground"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.addSubItem(r13, r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            r1 = 2131624958(0x7f0e03fe, float:1.887711E38)
            java.lang.String r2 = "ColorPickerMainColor"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.addSubItem(r8, r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            r1 = 2131624959(0x7f0e03ff, float:1.8877112E38)
            java.lang.String r2 = "ColorPickerMyMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.addSubItem(r14, r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            r0.setAllowCloseAnimation(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            r0.setForceSmoothKeyboard(r8)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.dropDownContainer
            r26 = -2
            r27 = -1082130432(0xffffffffbvar_, float:-1.0)
            r28 = 51
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x036c
            r2 = 1115684864(0x42800000, float:64.0)
            r29 = 1115684864(0x42800000, float:64.0)
            goto L_0x036e
        L_0x036c:
            r29 = 1113587712(0x42600000, float:56.0)
        L_0x036e:
            r30 = 0
            r31 = 1109393408(0x42200000, float:40.0)
            r32 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r0.addView(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$gY31pKcksQNaOAf5d5dnfUM8s-k r1 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$gY31pKcksQNaOAf5d5dnfUM8s-k
            r1.<init>()
            r0.setOnClickListener(r1)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.dropDown = r0
            r0.setImportantForAccessibility(r13)
            android.widget.TextView r0 = r6.dropDown
            r0.setGravity(r14)
            android.widget.TextView r0 = r6.dropDown
            r0.setSingleLine(r8)
            android.widget.TextView r0 = r6.dropDown
            r0.setLines(r8)
            android.widget.TextView r0 = r6.dropDown
            r0.setMaxLines(r8)
            android.widget.TextView r0 = r6.dropDown
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
            r0.setEllipsize(r1)
            android.widget.TextView r0 = r6.dropDown
            java.lang.String r1 = "actionBarDefaultTitle"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.dropDown
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r6.dropDown
            r1 = 2131624958(0x7f0e03fe, float:1.887711E38)
            java.lang.String r2 = "ColorPickerMainColor"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            android.content.res.Resources r0 = r36.getResources()
            r1 = 2131165481(0x7var_, float:1.794518E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r2 = "actionBarDefaultTitle"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r3)
            r0.setColorFilter(r1)
            android.widget.TextView r1 = r6.dropDown
            r1.setCompoundDrawablesWithIntrinsicBounds(r12, r12, r0, r12)
            android.widget.TextView r0 = r6.dropDown
            r1 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setCompoundDrawablePadding(r2)
            android.widget.TextView r0 = r6.dropDown
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r0.setPadding(r9, r9, r1, r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            android.widget.TextView r1 = r6.dropDown
            r26 = -2
            r27 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r28 = 16
            r29 = 1098907648(0x41800000, float:16.0)
            r31 = 0
            r32 = 1065353216(0x3var_, float:1.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r0.addView(r1, r2)
            goto L_0x0464
        L_0x041c:
            r15 = r5
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r6.applyingTheme
            org.telegram.tgnet.TLRPC$TL_theme r1 = r0.info
            if (r1 == 0) goto L_0x0426
            java.lang.String r0 = r1.title
            goto L_0x042a
        L_0x0426:
            java.lang.String r0 = r0.getName()
        L_0x042a:
            java.lang.String r1 = ".attheme"
            int r1 = r0.lastIndexOf(r1)
            if (r1 < 0) goto L_0x0436
            java.lang.String r0 = r0.substring(r9, r1)
        L_0x0436:
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar2
            r1.setTitle(r0)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r6.applyingTheme
            org.telegram.tgnet.TLRPC$TL_theme r0 = r0.info
            if (r0 == 0) goto L_0x0451
            int r0 = r0.installs_count
            if (r0 <= 0) goto L_0x0451
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar2
            java.lang.String r2 = "ThemeInstallCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            r1.setSubtitle(r0)
            goto L_0x0464
        L_0x0451:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            long r1 = java.lang.System.currentTimeMillis()
            r3 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 / r3
            r3 = 3600(0xe10, double:1.7786E-320)
            long r1 = r1 - r3
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatDateOnline(r1)
            r0.setSubtitle(r1)
        L_0x0464:
            org.telegram.ui.ThemePreviewActivity$8 r0 = new org.telegram.ui.ThemePreviewActivity$8
            r0.<init>(r7)
            r6.listView2 = r0
            androidx.recyclerview.widget.RecyclerView$ItemAnimator r0 = r0.getItemAnimator()
            androidx.recyclerview.widget.DefaultItemAnimator r0 = (androidx.recyclerview.widget.DefaultItemAnimator) r0
            r0.setDelayAnimations(r9)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            r0.setVerticalScrollBarEnabled(r8)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            r0.setOverScrollMode(r13)
            int r0 = r6.screenType
            if (r0 != r13) goto L_0x0494
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            r1 = 1082130432(0x40800000, float:4.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 1112539136(0x42500000, float:52.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r9, r1, r9, r2)
            goto L_0x04b5
        L_0x0494:
            r1 = 1082130432(0x40800000, float:4.0)
            if (r0 != r8) goto L_0x04a8
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r9, r1, r9, r2)
            goto L_0x04b5
        L_0x04a8:
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setPadding(r9, r2, r9, r1)
        L_0x04b5:
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            r0.setClipToPadding(r9)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            androidx.recyclerview.widget.LinearLayoutManager r1 = new androidx.recyclerview.widget.LinearLayoutManager
            r1.<init>(r7, r8, r8)
            r0.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x04cc
            r1 = 1
            goto L_0x04cd
        L_0x04cc:
            r1 = 2
        L_0x04cd:
            r0.setVerticalScrollbarPosition(r1)
            int r0 = r6.screenType
            if (r0 != r8) goto L_0x04fa
            android.widget.FrameLayout r0 = r6.page2
            org.telegram.ui.Components.RecyclerListView r1 = r6.listView2
            r26 = -1
            r27 = -1082130432(0xffffffffbvar_, float:-1.0)
            r28 = 51
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 1133019136(0x43888000, float:273.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$csbs872778iVXA3fkD5CoCFveV0 r1 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$csbs872778iVXA3fkD5CoCFveV0
            r1.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r1)
            r3 = -1
            goto L_0x0508
        L_0x04fa:
            android.widget.FrameLayout r0 = r6.page2
            org.telegram.ui.Components.RecyclerListView r1 = r6.listView2
            r2 = 51
            r3 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r3, r2)
            r0.addView(r1, r4)
        L_0x0508:
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            org.telegram.ui.ThemePreviewActivity$9 r1 = new org.telegram.ui.ThemePreviewActivity$9
            r1.<init>()
            r0.setOnScrollListener(r1)
            android.widget.FrameLayout r0 = r6.page2
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar2
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r0.addView(r1, r2)
            org.telegram.ui.Components.WallpaperParallaxEffect r0 = new org.telegram.ui.Components.WallpaperParallaxEffect
            r0.<init>(r7)
            r6.parallaxEffect = r0
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$rcqVmzH0BWIhl7XYa_Y--x9TFTs r1 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$rcqVmzH0BWIhl7XYa_Y--x9TFTs
            r1.<init>()
            r0.setCallback(r1)
            int r0 = r6.screenType
            java.lang.String r2 = "chat_fieldOverlayText"
            r3 = 17
            r4 = -2
            if (r0 == r8) goto L_0x0539
            if (r0 != r13) goto L_0x0b4a
        L_0x0539:
            org.telegram.ui.Components.RadialProgress2 r0 = new org.telegram.ui.Components.RadialProgress2
            org.telegram.ui.Components.BackupImageView r5 = r6.backgroundImage
            r0.<init>(r5)
            r6.radialProgress = r0
            java.lang.String r5 = "chat_serviceBackground"
            java.lang.String r11 = "chat_serviceBackground"
            java.lang.String r12 = "chat_serviceText"
            java.lang.String r1 = "chat_serviceText"
            r0.setColors((java.lang.String) r5, (java.lang.String) r11, (java.lang.String) r12, (java.lang.String) r1)
            int r0 = r6.screenType
            if (r0 != r13) goto L_0x05b7
            org.telegram.ui.ThemePreviewActivity$10 r0 = new org.telegram.ui.ThemePreviewActivity$10
            r0.<init>(r7)
            r6.bottomOverlayChat = r0
            r0.setWillNotDraw(r9)
            android.widget.FrameLayout r0 = r6.bottomOverlayChat
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setPadding(r9, r1, r9, r9)
            android.widget.FrameLayout r0 = r6.page2
            android.widget.FrameLayout r1 = r6.bottomOverlayChat
            r5 = 80
            r11 = 51
            r12 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r11, r5)
            r0.addView(r1, r5)
            android.widget.FrameLayout r0 = r6.bottomOverlayChat
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$T_zGbEPOsEZO6ky-CTvYyflA0qE r1 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$T_zGbEPOsEZO6ky-CTvYyflA0qE
            r1.<init>()
            r0.setOnClickListener(r1)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.bottomOverlayChatText = r0
            r1 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r8, r1)
            android.widget.TextView r0 = r6.bottomOverlayChatText
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r6.bottomOverlayChatText
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.bottomOverlayChatText
            r1 = 2131627486(0x7f0e0dde, float:1.8882238E38)
            java.lang.String r5 = "SetBackground"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r0.setText(r1)
            android.widget.FrameLayout r0 = r6.bottomOverlayChat
            android.widget.TextView r1 = r6.bottomOverlayChatText
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r4, r3)
            r0.addView(r1, r5)
        L_0x05b7:
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            android.content.res.Resources r1 = r36.getResources()
            r5 = 2131166036(0x7var_, float:1.7946306E38)
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r5)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            r6.sheetDrawable = r1
            r1.getPadding(r0)
            android.graphics.drawable.Drawable r1 = r6.sheetDrawable
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r11, r12)
            r1.setColorFilter(r5)
            java.lang.String[] r1 = new java.lang.String[r15]
            int[] r5 = new int[r15]
            org.telegram.ui.Components.WallpaperCheckBoxView[] r11 = new org.telegram.ui.Components.WallpaperCheckBoxView[r15]
            r6.checkBoxView = r11
            if (r15 == 0) goto L_0x06f7
            android.widget.FrameLayout r11 = new android.widget.FrameLayout
            r11.<init>(r7)
            r6.buttonsContainer = r11
            int r11 = r6.screenType
            if (r11 == r8) goto L_0x0613
            java.lang.Object r11 = r6.currentWallpaper
            boolean r11 = r11 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r11 == 0) goto L_0x05fc
            goto L_0x0613
        L_0x05fc:
            r11 = 2131624524(0x7f0e024c, float:1.887623E38)
            java.lang.String r12 = "BackgroundBlurred"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r1[r9] = r11
            r11 = 2131624534(0x7f0e0256, float:1.887625E38)
            java.lang.String r12 = "BackgroundMotion"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r1[r8] = r11
            goto L_0x0634
        L_0x0613:
            r11 = 2131624531(0x7f0e0253, float:1.8876244E38)
            java.lang.String r12 = "BackgroundColors"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r1[r9] = r11
            r11 = 2131624535(0x7f0e0257, float:1.8876252E38)
            java.lang.String r12 = "BackgroundPattern"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r1[r8] = r11
            r11 = 2131624534(0x7f0e0256, float:1.887625E38)
            java.lang.String r12 = "BackgroundMotion"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r1[r13] = r11
        L_0x0634:
            android.text.TextPaint r11 = new android.text.TextPaint
            r11.<init>(r8)
            r12 = 1096810496(0x41600000, float:14.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r14
            r11.setTextSize(r12)
            android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r11.setTypeface(r12)
            r12 = 0
            r14 = 0
        L_0x064c:
            if (r12 >= r15) goto L_0x0669
            r13 = r1[r12]
            float r13 = r11.measureText(r13)
            double r3 = (double) r13
            double r3 = java.lang.Math.ceil(r3)
            int r3 = (int) r3
            r5[r12] = r3
            r3 = r5[r12]
            int r14 = java.lang.Math.max(r14, r3)
            int r12 = r12 + 1
            r3 = 17
            r4 = -2
            r13 = 2
            goto L_0x064c
        L_0x0669:
            org.telegram.ui.ThemePreviewActivity$11 r3 = new org.telegram.ui.ThemePreviewActivity$11
            r3.<init>(r7)
            r6.playAnimationView = r3
            r3.setWillNotDraw(r9)
            android.widget.FrameLayout r3 = r6.playAnimationView
            int r4 = r6.backgroundGradientColor1
            if (r4 == 0) goto L_0x067b
            r4 = 0
            goto L_0x067c
        L_0x067b:
            r4 = 4
        L_0x067c:
            r3.setVisibility(r4)
            android.widget.FrameLayout r3 = r6.playAnimationView
            int r4 = r6.backgroundGradientColor1
            if (r4 == 0) goto L_0x0688
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x068b
        L_0x0688:
            r4 = 1036831949(0x3dcccccd, float:0.1)
        L_0x068b:
            r3.setScaleX(r4)
            android.widget.FrameLayout r3 = r6.playAnimationView
            int r4 = r6.backgroundGradientColor1
            if (r4 == 0) goto L_0x0697
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x069a
        L_0x0697:
            r4 = 1036831949(0x3dcccccd, float:0.1)
        L_0x069a:
            r3.setScaleY(r4)
            android.widget.FrameLayout r3 = r6.playAnimationView
            int r4 = r6.backgroundGradientColor1
            if (r4 == 0) goto L_0x06a6
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x06a7
        L_0x06a6:
            r4 = 0
        L_0x06a7:
            r3.setAlpha(r4)
            android.widget.FrameLayout r3 = r6.playAnimationView
            int r4 = r6.backgroundGradientColor1
            if (r4 == 0) goto L_0x06b5
            java.lang.Integer r12 = java.lang.Integer.valueOf(r8)
            goto L_0x06b6
        L_0x06b5:
            r12 = 0
        L_0x06b6:
            r3.setTag(r12)
            android.widget.FrameLayout r3 = r6.buttonsContainer
            android.widget.FrameLayout r4 = r6.playAnimationView
            r11 = 48
            r12 = 48
            r13 = 17
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13)
            r3.addView(r4, r11)
            android.widget.FrameLayout r3 = r6.playAnimationView
            org.telegram.ui.ThemePreviewActivity$12 r4 = new org.telegram.ui.ThemePreviewActivity$12
            r4.<init>()
            r3.setOnClickListener(r4)
            android.widget.ImageView r3 = new android.widget.ImageView
            r3.<init>(r7)
            r6.playAnimationImageView = r3
            android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER
            r3.setScaleType(r4)
            android.widget.ImageView r3 = r6.playAnimationImageView
            r4 = 2131165280(0x7var_, float:1.7944773E38)
            r3.setImageResource(r4)
            android.widget.FrameLayout r3 = r6.playAnimationView
            android.widget.ImageView r4 = r6.playAnimationImageView
            r11 = 17
            r12 = -2
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r12, r11)
            r3.addView(r4, r13)
            goto L_0x06f8
        L_0x06f7:
            r14 = 0
        L_0x06f8:
            r3 = 0
        L_0x06f9:
            if (r3 >= r15) goto L_0x07df
            org.telegram.ui.Components.WallpaperCheckBoxView[] r4 = r6.checkBoxView
            org.telegram.ui.Components.WallpaperCheckBoxView r11 = new org.telegram.ui.Components.WallpaperCheckBoxView
            int r12 = r6.screenType
            if (r12 == r8) goto L_0x0709
            java.lang.Object r12 = r6.currentWallpaper
            boolean r12 = r12 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r12 == 0) goto L_0x070b
        L_0x0709:
            if (r3 == 0) goto L_0x070d
        L_0x070b:
            r12 = 1
            goto L_0x070e
        L_0x070d:
            r12 = 0
        L_0x070e:
            org.telegram.ui.Components.BackupImageView r13 = r6.backgroundImage
            r11.<init>(r7, r12, r13)
            r4[r3] = r11
            org.telegram.ui.Components.WallpaperCheckBoxView[] r4 = r6.checkBoxView
            r4 = r4[r3]
            int r11 = r6.backgroundColor
            r4.setBackgroundColor(r11)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r4 = r6.checkBoxView
            r4 = r4[r3]
            r11 = r1[r3]
            r12 = r5[r3]
            r4.setText(r11, r12, r14)
            int r4 = r6.screenType
            if (r4 == r8) goto L_0x0743
            java.lang.Object r4 = r6.currentWallpaper
            boolean r4 = r4 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r4 == 0) goto L_0x0734
            goto L_0x0743
        L_0x0734:
            org.telegram.ui.Components.WallpaperCheckBoxView[] r4 = r6.checkBoxView
            r4 = r4[r3]
            if (r3 != 0) goto L_0x073d
            boolean r11 = r6.isBlurred
            goto L_0x073f
        L_0x073d:
            boolean r11 = r6.isMotion
        L_0x073f:
            r4.setChecked(r11, r9)
            goto L_0x076d
        L_0x0743:
            if (r3 != r8) goto L_0x0761
            org.telegram.ui.Components.WallpaperCheckBoxView[] r4 = r6.checkBoxView
            r4 = r4[r3]
            org.telegram.tgnet.TLRPC$TL_wallPaper r11 = r6.selectedPattern
            if (r11 != 0) goto L_0x075c
            org.telegram.ui.ActionBar.Theme$ThemeAccent r11 = r6.accent
            if (r11 == 0) goto L_0x075a
            java.lang.String r11 = r11.patternSlug
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            if (r11 != 0) goto L_0x075a
            goto L_0x075c
        L_0x075a:
            r11 = 0
            goto L_0x075d
        L_0x075c:
            r11 = 1
        L_0x075d:
            r4.setChecked(r11, r9)
            goto L_0x076d
        L_0x0761:
            r4 = 2
            if (r3 != r4) goto L_0x076d
            org.telegram.ui.Components.WallpaperCheckBoxView[] r4 = r6.checkBoxView
            r4 = r4[r3]
            boolean r11 = r6.isMotion
            r4.setChecked(r11, r9)
        L_0x076d:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r4 = r4 + r14
            android.widget.FrameLayout$LayoutParams r11 = new android.widget.FrameLayout$LayoutParams
            r12 = -2
            r11.<init>(r4, r12)
            r12 = 17
            r11.gravity = r12
            r12 = 3
            if (r15 != r12) goto L_0x0799
            if (r3 == 0) goto L_0x078f
            r13 = 2
            if (r3 != r13) goto L_0x0785
            goto L_0x078f
        L_0x0785:
            int r4 = r4 / 2
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r4 = r4 + r13
            r11.rightMargin = r4
            goto L_0x07ae
        L_0x078f:
            int r4 = r4 / 2
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r4 = r4 + r13
            r11.leftMargin = r4
            goto L_0x07ae
        L_0x0799:
            if (r3 != r8) goto L_0x07a5
            int r4 = r4 / 2
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r4 = r4 + r13
            r11.leftMargin = r4
            goto L_0x07ae
        L_0x07a5:
            int r4 = r4 / 2
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r4 = r4 + r13
            r11.rightMargin = r4
        L_0x07ae:
            android.widget.FrameLayout r4 = r6.buttonsContainer
            org.telegram.ui.Components.WallpaperCheckBoxView[] r13 = r6.checkBoxView
            r13 = r13[r3]
            r4.addView(r13, r11)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r4 = r6.checkBoxView
            r11 = r4[r3]
            r4 = r4[r3]
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$4NqfvFau_z6mUClUT3uRgJanGrs r13 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$4NqfvFau_z6mUClUT3uRgJanGrs
            r13.<init>(r3, r11)
            r4.setOnClickListener(r13)
            r4 = 2
            if (r3 != r4) goto L_0x07d9
            org.telegram.ui.Components.WallpaperCheckBoxView[] r4 = r6.checkBoxView
            r4 = r4[r3]
            r11 = 0
            r4.setAlpha(r11)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r4 = r6.checkBoxView
            r4 = r4[r3]
            r13 = 4
            r4.setVisibility(r13)
            goto L_0x07db
        L_0x07d9:
            r11 = 0
            r13 = 4
        L_0x07db:
            int r3 = r3 + 1
            goto L_0x06f9
        L_0x07df:
            r11 = 0
            r13 = 4
            int r1 = r6.screenType
            if (r1 == r8) goto L_0x07eb
            java.lang.Object r1 = r6.currentWallpaper
            boolean r1 = r1 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r1 == 0) goto L_0x0b21
        L_0x07eb:
            r6.isBlurred = r9
            r1 = 0
            r3 = 2
        L_0x07ef:
            if (r1 >= r3) goto L_0x0b21
            android.widget.FrameLayout[] r4 = r6.patternLayout
            org.telegram.ui.ThemePreviewActivity$13 r5 = new org.telegram.ui.ThemePreviewActivity$13
            r5.<init>(r7, r1, r0)
            r4[r1] = r5
            if (r1 == r8) goto L_0x0800
            int r4 = r6.screenType
            if (r4 != r3) goto L_0x0807
        L_0x0800:
            android.widget.FrameLayout[] r3 = r6.patternLayout
            r3 = r3[r1]
            r3.setVisibility(r13)
        L_0x0807:
            android.widget.FrameLayout[] r3 = r6.patternLayout
            r3 = r3[r1]
            r3.setWillNotDraw(r9)
            int r3 = r6.screenType
            r4 = 2
            if (r3 != r4) goto L_0x0822
            if (r1 != 0) goto L_0x0818
            r3 = 321(0x141, float:4.5E-43)
            goto L_0x081a
        L_0x0818:
            r3 = 316(0x13c, float:4.43E-43)
        L_0x081a:
            r4 = 83
            r5 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3, r4)
            goto L_0x0830
        L_0x0822:
            r4 = 83
            r5 = -1
            if (r1 != 0) goto L_0x082a
            r3 = 273(0x111, float:3.83E-43)
            goto L_0x082c
        L_0x082a:
            r3 = 316(0x13c, float:4.43E-43)
        L_0x082c:
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3, r4)
        L_0x0830:
            if (r1 != 0) goto L_0x0850
            int r4 = r3.height
            r5 = 1094713344(0x41400000, float:12.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r12 = r0.top
            int r5 = r5 + r12
            int r4 = r4 + r5
            r3.height = r4
            android.widget.FrameLayout[] r4 = r6.patternLayout
            r4 = r4[r1]
            r5 = 1094713344(0x41400000, float:12.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r12 = r0.top
            int r5 = r5 + r12
            r4.setPadding(r9, r5, r9, r9)
        L_0x0850:
            android.widget.FrameLayout r4 = r6.page2
            android.widget.FrameLayout[] r5 = r6.patternLayout
            r5 = r5[r1]
            r4.addView(r5, r3)
            r3 = 1101529088(0x41a80000, float:21.0)
            if (r1 == r8) goto L_0x0862
            int r4 = r6.screenType
            r5 = 2
            if (r4 != r5) goto L_0x09a2
        L_0x0862:
            android.widget.FrameLayout[] r4 = r6.patternsButtonsContainer
            org.telegram.ui.ThemePreviewActivity$14 r5 = new org.telegram.ui.ThemePreviewActivity$14
            r5.<init>(r7)
            r4[r1] = r5
            android.widget.FrameLayout[] r4 = r6.patternsButtonsContainer
            r4 = r4[r1]
            r4.setWillNotDraw(r9)
            android.widget.FrameLayout[] r4 = r6.patternsButtonsContainer
            r4 = r4[r1]
            r5 = 1077936128(0x40400000, float:3.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r4.setPadding(r9, r5, r9, r9)
            android.widget.FrameLayout[] r4 = r6.patternsButtonsContainer
            r4 = r4[r1]
            r4.setClickable(r8)
            android.widget.FrameLayout[] r4 = r6.patternLayout
            r4 = r4[r1]
            android.widget.FrameLayout[] r5 = r6.patternsButtonsContainer
            r5 = r5[r1]
            r12 = 80
            r14 = 51
            r15 = -1
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r14, r12)
            r4.addView(r5, r12)
            android.widget.TextView[] r4 = r6.patternsCancelButton
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r7)
            r4[r1] = r5
            android.widget.TextView[] r4 = r6.patternsCancelButton
            r4 = r4[r1]
            r5 = 1097859072(0x41700000, float:15.0)
            r4.setTextSize(r8, r5)
            android.widget.TextView[] r4 = r6.patternsCancelButton
            r4 = r4[r1]
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r4.setTypeface(r5)
            android.widget.TextView[] r4 = r6.patternsCancelButton
            r4 = r4[r1]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r4.setTextColor(r5)
            android.widget.TextView[] r4 = r6.patternsCancelButton
            r4 = r4[r1]
            r5 = 2131624658(0x7f0e02d2, float:1.8876502E38)
            java.lang.String r12 = "Cancel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r12, r5)
            java.lang.String r5 = r5.toUpperCase()
            r4.setText(r5)
            android.widget.TextView[] r4 = r6.patternsCancelButton
            r4 = r4[r1]
            r5 = 17
            r4.setGravity(r5)
            android.widget.TextView[] r4 = r6.patternsCancelButton
            r4 = r4[r1]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4.setPadding(r5, r9, r12, r9)
            android.widget.TextView[] r4 = r6.patternsCancelButton
            r4 = r4[r1]
            java.lang.String r5 = "listSelectorSDK21"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5, r9)
            r4.setBackgroundDrawable(r5)
            android.widget.FrameLayout[] r4 = r6.patternsButtonsContainer
            r4 = r4[r1]
            android.widget.TextView[] r5 = r6.patternsCancelButton
            r5 = r5[r1]
            r12 = 51
            r14 = -2
            r15 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r12)
            r4.addView(r5, r11)
            android.widget.TextView[] r4 = r6.patternsCancelButton
            r4 = r4[r1]
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$RFrQ-_KR8FyrO3Hmtb8A-thk6yU r5 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$RFrQ-_KR8FyrO3Hmtb8A-thk6yU
            r5.<init>(r1)
            r4.setOnClickListener(r5)
            android.widget.TextView[] r4 = r6.patternsSaveButton
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r7)
            r4[r1] = r5
            android.widget.TextView[] r4 = r6.patternsSaveButton
            r4 = r4[r1]
            r5 = 1097859072(0x41700000, float:15.0)
            r4.setTextSize(r8, r5)
            android.widget.TextView[] r4 = r6.patternsSaveButton
            r4 = r4[r1]
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r4.setTypeface(r5)
            android.widget.TextView[] r4 = r6.patternsSaveButton
            r4 = r4[r1]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r4.setTextColor(r5)
            android.widget.TextView[] r4 = r6.patternsSaveButton
            r4 = r4[r1]
            r5 = 2131624296(0x7f0e0168, float:1.8875768E38)
            java.lang.String r11 = "ApplyTheme"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            java.lang.String r5 = r5.toUpperCase()
            r4.setText(r5)
            android.widget.TextView[] r4 = r6.patternsSaveButton
            r4 = r4[r1]
            r5 = 17
            r4.setGravity(r5)
            android.widget.TextView[] r4 = r6.patternsSaveButton
            r4 = r4[r1]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4.setPadding(r5, r9, r11, r9)
            android.widget.TextView[] r4 = r6.patternsSaveButton
            r4 = r4[r1]
            java.lang.String r5 = "listSelectorSDK21"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5, r9)
            r4.setBackgroundDrawable(r5)
            android.widget.FrameLayout[] r4 = r6.patternsButtonsContainer
            r4 = r4[r1]
            android.widget.TextView[] r5 = r6.patternsSaveButton
            r5 = r5[r1]
            r11 = 53
            r12 = -2
            r14 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r14, r11)
            r4.addView(r5, r11)
            android.widget.TextView[] r4 = r6.patternsSaveButton
            r4 = r4[r1]
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$n8VLGXY5s1btl0O191oGVcc5S9I r5 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$n8VLGXY5s1btl0O191oGVcc5S9I
            r5.<init>(r1)
            r4.setOnClickListener(r5)
        L_0x09a2:
            if (r1 != r8) goto L_0x0aa2
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r7)
            r4.setLines(r8)
            r4.setSingleLine(r8)
            r5 = 2131624526(0x7f0e024e, float:1.8876234E38)
            java.lang.String r11 = "BackgroundChoosePattern"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r4.setText(r5)
            java.lang.String r5 = "windowBackgroundWhiteBlackText"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r5)
            r5 = 1101004800(0x41a00000, float:20.0)
            r4.setTextSize(r8, r5)
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r4.setTypeface(r5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r11 = 1086324736(0x40CLASSNAME, float:6.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r12 = 1090519040(0x41000000, float:8.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r4.setPadding(r5, r11, r3, r12)
            android.text.TextUtils$TruncateAt r3 = android.text.TextUtils.TruncateAt.MIDDLE
            r4.setEllipsize(r3)
            r3 = 16
            r4.setGravity(r3)
            android.widget.FrameLayout[] r3 = r6.patternLayout
            r3 = r3[r1]
            r27 = -1
            r28 = 1111490560(0x42400000, float:48.0)
            r29 = 51
            r30 = 0
            r31 = 1101529088(0x41a80000, float:21.0)
            r32 = 0
            r33 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r3.addView(r4, r5)
            org.telegram.ui.ThemePreviewActivity$15 r3 = new org.telegram.ui.ThemePreviewActivity$15
            r3.<init>(r7)
            r6.patternsListView = r3
            androidx.recyclerview.widget.LinearLayoutManager r4 = new androidx.recyclerview.widget.LinearLayoutManager
            r4.<init>(r7, r9, r9)
            r6.patternsLayoutManager = r4
            r3.setLayoutManager(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r6.patternsListView
            org.telegram.ui.ThemePreviewActivity$PatternsAdapter r4 = new org.telegram.ui.ThemePreviewActivity$PatternsAdapter
            r4.<init>(r7)
            r6.patternsAdapter = r4
            r3.setAdapter(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r6.patternsListView
            org.telegram.ui.ThemePreviewActivity$16 r4 = new org.telegram.ui.ThemePreviewActivity$16
            r4.<init>()
            r3.addItemDecoration(r4)
            android.widget.FrameLayout[] r3 = r6.patternLayout
            r3 = r3[r1]
            org.telegram.ui.Components.RecyclerListView r4 = r6.patternsListView
            r28 = 1120403456(0x42CLASSNAME, float:100.0)
            r31 = 1117257728(0x42980000, float:76.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r3.addView(r4, r5)
            org.telegram.ui.Components.RecyclerListView r3 = r6.patternsListView
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$cbk9PyodDDvOeF-SsxAHr45Qxy8 r4 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$cbk9PyodDDvOeF-SsxAHr45Qxy8
            r4.<init>()
            r3.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r4)
            org.telegram.ui.Cells.HeaderCell r3 = new org.telegram.ui.Cells.HeaderCell
            r3.<init>(r7)
            r6.intensityCell = r3
            r4 = 2131624533(0x7f0e0255, float:1.8876248E38)
            java.lang.String r5 = "BackgroundIntensity"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setText(r4)
            android.widget.FrameLayout[] r3 = r6.patternLayout
            r3 = r3[r1]
            org.telegram.ui.Cells.HeaderCell r4 = r6.intensityCell
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r31 = 1127153664(0x432var_, float:175.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r3.addView(r4, r5)
            org.telegram.ui.ThemePreviewActivity$17 r3 = new org.telegram.ui.ThemePreviewActivity$17
            r3.<init>(r7)
            r6.intensitySeekBar = r3
            float r4 = r6.currentIntensity
            r3.setProgress(r4)
            org.telegram.ui.Components.SeekBarView r3 = r6.intensitySeekBar
            r3.setReportChanges(r8)
            org.telegram.ui.Components.SeekBarView r3 = r6.intensitySeekBar
            org.telegram.ui.ThemePreviewActivity$18 r4 = new org.telegram.ui.ThemePreviewActivity$18
            r4.<init>()
            r3.setDelegate(r4)
            android.widget.FrameLayout[] r3 = r6.patternLayout
            r3 = r3[r1]
            org.telegram.ui.Components.SeekBarView r4 = r6.intensitySeekBar
            r28 = 1108869120(0x42180000, float:38.0)
            r30 = 1084227584(0x40a00000, float:5.0)
            r31 = 1129512960(0x43530000, float:211.0)
            r32 = 1084227584(0x40a00000, float:5.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r3.addView(r4, r5)
            goto L_0x0b1b
        L_0x0aa2:
            org.telegram.ui.Components.ColorPicker r3 = new org.telegram.ui.Components.ColorPicker
            boolean r4 = r6.editingTheme
            org.telegram.ui.ThemePreviewActivity$19 r5 = new org.telegram.ui.ThemePreviewActivity$19
            r5.<init>()
            r3.<init>(r7, r4, r5)
            r6.colorPicker = r3
            int r4 = r6.screenType
            if (r4 != r8) goto L_0x0b02
            android.widget.FrameLayout[] r4 = r6.patternLayout
            r4 = r4[r1]
            r5 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r5, r8)
            r4.addView(r3, r11)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r6.applyingTheme
            boolean r3 = r3.isDark()
            if (r3 == 0) goto L_0x0ad1
            org.telegram.ui.Components.ColorPicker r3 = r6.colorPicker
            r4 = 1045220557(0x3e4ccccd, float:0.2)
            r3.setMinBrightness(r4)
            goto L_0x0ae1
        L_0x0ad1:
            org.telegram.ui.Components.ColorPicker r3 = r6.colorPicker
            r4 = 1028443341(0x3d4ccccd, float:0.05)
            r3.setMinBrightness(r4)
            org.telegram.ui.Components.ColorPicker r3 = r6.colorPicker
            r4 = 1061997773(0x3f4ccccd, float:0.8)
            r3.setMaxBrightness(r4)
        L_0x0ae1:
            org.telegram.ui.Components.ColorPicker r3 = r6.colorPicker
            r28 = 1
            boolean r29 = r6.hasChanges(r8)
            r30 = 0
            r31 = 1
            r32 = 0
            r33 = 0
            r34 = 0
            r27 = r3
            r27.setType(r28, r29, r30, r31, r32, r33, r34)
            org.telegram.ui.Components.ColorPicker r3 = r6.colorPicker
            org.telegram.ui.ActionBar.Theme$ThemeAccent r4 = r6.accent
            int r4 = r4.accentColor
            r3.setColor(r4, r9)
            goto L_0x0b1b
        L_0x0b02:
            android.widget.FrameLayout[] r4 = r6.patternLayout
            r4 = r4[r1]
            r27 = -1
            r28 = -1082130432(0xffffffffbvar_, float:-1.0)
            r29 = 1
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r4.addView(r3, r5)
        L_0x0b1b:
            int r1 = r1 + 1
            r3 = 2
            r11 = 0
            goto L_0x07ef
        L_0x0b21:
            r6.updateButtonState(r9, r9)
            org.telegram.ui.Components.BackupImageView r0 = r6.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            boolean r0 = r0.hasBitmapImage()
            if (r0 != 0) goto L_0x0b37
            android.widget.FrameLayout r0 = r6.page2
            r1 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0.setBackgroundColor(r1)
        L_0x0b37:
            int r0 = r6.screenType
            if (r0 == r8) goto L_0x0b4a
            java.lang.Object r0 = r6.currentWallpaper
            boolean r0 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r0 != 0) goto L_0x0b4a
            org.telegram.ui.Components.BackupImageView r0 = r6.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            r0.setCrossfadeWithOldImage(r8)
        L_0x0b4a:
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            org.telegram.ui.ThemePreviewActivity$MessagesAdapter r1 = r6.messagesAdapter
            r0.setAdapter(r1)
            org.telegram.ui.ThemePreviewActivity$20 r0 = new org.telegram.ui.ThemePreviewActivity$20
            r0.<init>(r7)
            r6.frameLayout = r0
            r0.setWillNotDraw(r9)
            android.widget.FrameLayout r0 = r6.frameLayout
            r6.fragmentView = r0
            android.view.ViewTreeObserver r0 = r0.getViewTreeObserver()
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$Ac0T7shONTFDwcsKjBLA-OuMA3A r1 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$Ac0T7shONTFDwcsKjBLA-OuMA3A
            r1.<init>()
            r6.onGlobalLayoutListener = r1
            r0.addOnGlobalLayoutListener(r1)
            androidx.viewpager.widget.ViewPager r0 = new androidx.viewpager.widget.ViewPager
            r0.<init>(r7)
            r6.viewPager = r0
            org.telegram.ui.ThemePreviewActivity$21 r1 = new org.telegram.ui.ThemePreviewActivity$21
            r1.<init>()
            r0.addOnPageChangeListener(r1)
            androidx.viewpager.widget.ViewPager r0 = r6.viewPager
            org.telegram.ui.ThemePreviewActivity$22 r1 = new org.telegram.ui.ThemePreviewActivity$22
            r1.<init>()
            r0.setAdapter(r1)
            androidx.viewpager.widget.ViewPager r0 = r6.viewPager
            java.lang.String r1 = "actionBarDefault"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            org.telegram.messenger.AndroidUtilities.setViewPagerEdgeEffectColor(r0, r1)
            android.widget.FrameLayout r0 = r6.frameLayout
            androidx.viewpager.widget.ViewPager r1 = r6.viewPager
            r27 = -1
            r28 = -1082130432(0xffffffffbvar_, float:-1.0)
            r29 = 51
            r30 = 0
            r31 = 0
            r32 = 0
            int r3 = r6.screenType
            if (r3 != 0) goto L_0x0baa
            r14 = 1111490560(0x42400000, float:48.0)
            r33 = 1111490560(0x42400000, float:48.0)
            goto L_0x0bac
        L_0x0baa:
            r33 = 0
        L_0x0bac:
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r0.addView(r1, r3)
            org.telegram.ui.Components.UndoView r0 = new org.telegram.ui.Components.UndoView
            r0.<init>(r7, r6)
            r6.undoView = r0
            r1 = 1112276992(0x424CLASSNAME, float:51.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setAdditionalTranslationY(r1)
            android.widget.FrameLayout r0 = r6.frameLayout
            org.telegram.ui.Components.UndoView r1 = r6.undoView
            r11 = -1
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r13 = 83
            r14 = 1090519040(0x41000000, float:8.0)
            r15 = 0
            r16 = 1090519040(0x41000000, float:8.0)
            r17 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r0.addView(r1, r3)
            int r0 = r6.screenType
            if (r0 != 0) goto L_0x0d06
            android.view.View r0 = new android.view.View
            r0.<init>(r7)
            java.lang.String r1 = "dialogShadowLine"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout$LayoutParams r1 = new android.widget.FrameLayout$LayoutParams
            r3 = 83
            r4 = -1
            r1.<init>(r4, r8, r3)
            r3 = 1111490560(0x42400000, float:48.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.bottomMargin = r3
            android.widget.FrameLayout r3 = r6.frameLayout
            r3.addView(r0, r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.saveButtonsContainer = r0
            int r1 = r6.getButtonsColor(r10)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout r0 = r6.frameLayout
            android.widget.FrameLayout r1 = r6.saveButtonsContainer
            r3 = 48
            r4 = 83
            r5 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3, r4)
            r0.addView(r1, r3)
            org.telegram.ui.ThemePreviewActivity$23 r0 = new org.telegram.ui.ThemePreviewActivity$23
            r0.<init>(r7)
            r6.dotsContainer = r0
            android.widget.FrameLayout r1 = r6.saveButtonsContainer
            r3 = 22
            r4 = 8
            r5 = 17
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5)
            r1.addView(r0, r3)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.cancelButton = r0
            r1 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r8, r1)
            android.widget.TextView r0 = r6.cancelButton
            int r1 = r6.getButtonsColor(r2)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.cancelButton
            r0.setGravity(r5)
            android.widget.TextView r0 = r6.cancelButton
            r1 = 251658240(0xvar_, float:6.3108872E-30)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r9)
            r0.setBackgroundDrawable(r1)
            android.widget.TextView r0 = r6.cancelButton
            r1 = 1105723392(0x41e80000, float:29.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setPadding(r3, r9, r4, r9)
            android.widget.TextView r0 = r6.cancelButton
            r3 = 2131624658(0x7f0e02d2, float:1.8876502E38)
            java.lang.String r4 = "Cancel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r3 = r3.toUpperCase()
            r0.setText(r3)
            android.widget.TextView r0 = r6.cancelButton
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r0.setTypeface(r3)
            android.widget.FrameLayout r0 = r6.saveButtonsContainer
            android.widget.TextView r3 = r6.cancelButton
            r4 = 51
            r5 = -2
            r10 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r10, r4)
            r0.addView(r3, r4)
            android.widget.TextView r0 = r6.cancelButton
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$SU8HGXYz76CywS0ZuqdTe39JJ3o r3 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$SU8HGXYz76CywS0ZuqdTe39JJ3o
            r3.<init>()
            r0.setOnClickListener(r3)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.doneButton = r0
            r3 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r8, r3)
            android.widget.TextView r0 = r6.doneButton
            int r2 = r6.getButtonsColor(r2)
            r0.setTextColor(r2)
            android.widget.TextView r0 = r6.doneButton
            r2 = 17
            r0.setGravity(r2)
            android.widget.TextView r0 = r6.doneButton
            r2 = 251658240(0xvar_, float:6.3108872E-30)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r9)
            r0.setBackgroundDrawable(r2)
            android.widget.TextView r0 = r6.doneButton
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setPadding(r2, r9, r1, r9)
            android.widget.TextView r0 = r6.doneButton
            r1 = 2131624296(0x7f0e0168, float:1.8875768E38)
            java.lang.String r2 = "ApplyTheme"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
            android.widget.TextView r0 = r6.doneButton
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r0.setTypeface(r1)
            android.widget.FrameLayout r0 = r6.saveButtonsContainer
            android.widget.TextView r1 = r6.doneButton
            r2 = 53
            r3 = -2
            r4 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r2)
            r0.addView(r1, r2)
            android.widget.TextView r0 = r6.doneButton
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$0sFc0_7hGKkmzhfAlk51WF-hwgc r1 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$0sFc0_7hGKkmzhfAlk51WF-hwgc
            r1.<init>()
            r0.setOnClickListener(r1)
        L_0x0d06:
            int r0 = r6.screenType
            if (r0 != r8) goto L_0x0d21
            boolean r0 = org.telegram.ui.ActionBar.Theme.hasCustomWallpaper()
            if (r0 != 0) goto L_0x0d21
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            long r0 = r0.backgroundOverrideColor
            r2 = 4294967296(0xNUM, double:2.121995791E-314)
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0d21
            r0 = 2
            r6.selectColorType(r0)
        L_0x0d21:
            java.util.List r0 = r35.getThemeDescriptionsInternal()
            r6.themeDescriptions = r0
            r6.setCurrentImage(r8)
            r6.updatePlayAnimationView(r9)
            boolean r0 = r6.showColor
            if (r0 == 0) goto L_0x0d34
            r6.showPatternsView(r9, r8, r9)
        L_0x0d34:
            android.view.View r0 = r6.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$ThemePreviewActivity(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        if (!(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            Drawable drawable = imageReceiver.getDrawable();
            if (z && drawable != null) {
                if (!Theme.hasThemeKey("chat_serviceBackground") || (this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
                    Theme.applyChatServiceMessageColor(AndroidUtilities.calcDrawableColor(drawable), drawable);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$ThemePreviewActivity(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$4 */
    public /* synthetic */ void lambda$createView$4$ThemePreviewActivity(View view, int i, float f, float f2) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$ThemePreviewActivity(int i, int i2, float f) {
        if (this.isMotion) {
            this.backgroundImage.getBackground();
            float f2 = 1.0f;
            if (this.motionAnimation != null) {
                f2 = (this.backgroundImage.getScaleX() - 1.0f) / (this.parallaxScale - 1.0f);
            }
            this.backgroundImage.setTranslationX(((float) i) * f2);
            this.backgroundImage.setTranslationY(((float) i2) * f2);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0248  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02b8  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x02bb  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x02c8  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x030b  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x018c A[SYNTHETIC, Splitter:B:83:0x018c] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x01b9  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01c4  */
    /* renamed from: lambda$createView$6 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$6$ThemePreviewActivity(android.view.View r24) {
        /*
            r23 = this;
            r1 = r23
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
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            java.lang.String r8 = "jpg"
            java.lang.String r10 = "t"
            r11 = 87
            r12 = 1
            if (r7 == 0) goto L_0x008e
            android.graphics.Bitmap r0 = r1.originalBitmap
            if (r0 == 0) goto L_0x0049
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0044 }
            r0.<init>(r6)     // Catch:{ Exception -> 0x0044 }
            android.graphics.Bitmap r7 = r1.originalBitmap     // Catch:{ Exception -> 0x0044 }
            android.graphics.Bitmap$CompressFormat r13 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x0044 }
            r7.compress(r13, r11, r0)     // Catch:{ Exception -> 0x0044 }
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
            android.graphics.Bitmap$CompressFormat r13 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x0070 }
            r0.compress(r13, r11, r7)     // Catch:{ Exception -> 0x0070 }
            r7.close()     // Catch:{ Exception -> 0x0070 }
            goto L_0x0042
        L_0x0070:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x005c
        L_0x0075:
            if (r0 != 0) goto L_0x012d
            java.lang.Object r0 = r1.currentWallpaper
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r0
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r12)
            boolean r0 = org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r0, (java.io.File) r6)     // Catch:{ Exception -> 0x0087 }
            goto L_0x012d
        L_0x0087:
            r0 = move-exception
            r7 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
            goto L_0x0186
        L_0x008e:
            boolean r7 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r7 == 0) goto L_0x012f
            org.telegram.tgnet.TLRPC$TL_wallPaper r7 = r1.selectedPattern
            if (r7 == 0) goto L_0x012c
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r0     // Catch:{ all -> 0x0127 }
            org.telegram.ui.Components.BackupImageView r0 = r1.backgroundImage     // Catch:{ all -> 0x0127 }
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()     // Catch:{ all -> 0x0127 }
            android.graphics.Bitmap r0 = r0.getBitmap()     // Catch:{ all -> 0x0127 }
            int r7 = r0.getWidth()     // Catch:{ all -> 0x0127 }
            int r13 = r0.getHeight()     // Catch:{ all -> 0x0127 }
            android.graphics.Bitmap$Config r14 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0127 }
            android.graphics.Bitmap r7 = android.graphics.Bitmap.createBitmap(r7, r13, r14)     // Catch:{ all -> 0x0127 }
            android.graphics.Canvas r13 = new android.graphics.Canvas     // Catch:{ all -> 0x0127 }
            r13.<init>(r7)     // Catch:{ all -> 0x0127 }
            int r14 = r1.backgroundGradientColor2     // Catch:{ all -> 0x0127 }
            r15 = 2
            if (r14 == 0) goto L_0x00bb
            goto L_0x00e8
        L_0x00bb:
            int r14 = r1.backgroundGradientColor1     // Catch:{ all -> 0x0127 }
            if (r14 == 0) goto L_0x00e3
            android.graphics.drawable.GradientDrawable r14 = new android.graphics.drawable.GradientDrawable     // Catch:{ all -> 0x0127 }
            int r3 = r1.backgroundRotation     // Catch:{ all -> 0x0127 }
            android.graphics.drawable.GradientDrawable$Orientation r3 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r3)     // Catch:{ all -> 0x0127 }
            int[] r11 = new int[r15]     // Catch:{ all -> 0x0127 }
            int r9 = r1.backgroundColor     // Catch:{ all -> 0x0127 }
            r11[r5] = r9     // Catch:{ all -> 0x0127 }
            int r9 = r1.backgroundGradientColor1     // Catch:{ all -> 0x0127 }
            r11[r12] = r9     // Catch:{ all -> 0x0127 }
            r14.<init>(r3, r11)     // Catch:{ all -> 0x0127 }
            int r3 = r7.getWidth()     // Catch:{ all -> 0x0127 }
            int r9 = r7.getHeight()     // Catch:{ all -> 0x0127 }
            r14.setBounds(r5, r5, r3, r9)     // Catch:{ all -> 0x0127 }
            r14.draw(r13)     // Catch:{ all -> 0x0127 }
            goto L_0x00e8
        L_0x00e3:
            int r3 = r1.backgroundColor     // Catch:{ all -> 0x0127 }
            r13.drawColor(r3)     // Catch:{ all -> 0x0127 }
        L_0x00e8:
            android.graphics.Paint r3 = new android.graphics.Paint     // Catch:{ all -> 0x0127 }
            r3.<init>(r15)     // Catch:{ all -> 0x0127 }
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter     // Catch:{ all -> 0x0127 }
            int r11 = r1.patternColor     // Catch:{ all -> 0x0127 }
            android.graphics.PorterDuff$Mode r14 = r1.blendMode     // Catch:{ all -> 0x0127 }
            r9.<init>(r11, r14)     // Catch:{ all -> 0x0127 }
            r3.setColorFilter(r9)     // Catch:{ all -> 0x0127 }
            r9 = 1132396544(0x437var_, float:255.0)
            float r11 = r1.currentIntensity     // Catch:{ all -> 0x0127 }
            float r11 = java.lang.Math.abs(r11)     // Catch:{ all -> 0x0127 }
            float r11 = r11 * r9
            int r9 = (int) r11     // Catch:{ all -> 0x0127 }
            r3.setAlpha(r9)     // Catch:{ all -> 0x0127 }
            r9 = 0
            r13.drawBitmap(r0, r9, r9, r3)     // Catch:{ all -> 0x0127 }
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x0127 }
            r0.<init>(r6)     // Catch:{ all -> 0x0127 }
            int r3 = r1.backgroundGradientColor2     // Catch:{ all -> 0x0127 }
            if (r3 == 0) goto L_0x011c
            android.graphics.Bitmap$CompressFormat r3 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ all -> 0x0127 }
            r9 = 100
            r7.compress(r3, r9, r0)     // Catch:{ all -> 0x0127 }
            goto L_0x0123
        L_0x011c:
            android.graphics.Bitmap$CompressFormat r3 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x0127 }
            r9 = 87
            r7.compress(r3, r9, r0)     // Catch:{ all -> 0x0127 }
        L_0x0123:
            r0.close()     // Catch:{ all -> 0x0127 }
            goto L_0x012c
        L_0x0127:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0186
        L_0x012c:
            r0 = 1
        L_0x012d:
            r7 = 0
            goto L_0x0188
        L_0x012f:
            boolean r3 = r0 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
            if (r3 == 0) goto L_0x015f
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r0
            int r3 = r0.resId
            if (r3 != 0) goto L_0x012c
            java.lang.String r3 = r0.slug
            boolean r3 = r10.equals(r3)
            if (r3 == 0) goto L_0x0142
            goto L_0x012c
        L_0x0142:
            java.io.File r3 = r0.originalPath     // Catch:{ Exception -> 0x0158 }
            if (r3 == 0) goto L_0x0147
            goto L_0x0149
        L_0x0147:
            java.io.File r3 = r0.path     // Catch:{ Exception -> 0x0158 }
        L_0x0149:
            boolean r7 = r3.equals(r6)     // Catch:{ Exception -> 0x0158 }
            if (r7 == 0) goto L_0x0151
            r0 = 1
            goto L_0x0188
        L_0x0151:
            boolean r0 = org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r3, (java.io.File) r6)     // Catch:{ Exception -> 0x0156 }
            goto L_0x0188
        L_0x0156:
            r0 = move-exception
            goto L_0x015a
        L_0x0158:
            r0 = move-exception
            r7 = 0
        L_0x015a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0188
        L_0x015f:
            boolean r3 = r0 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r3 == 0) goto L_0x0186
            org.telegram.messenger.MediaController$SearchImage r0 = (org.telegram.messenger.MediaController.SearchImage) r0
            org.telegram.tgnet.TLRPC$Photo r3 = r0.photo
            if (r3 == 0) goto L_0x0176
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r3.sizes
            int r3 = r1.maxWallpaperSize
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r3, r12)
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r12)
            goto L_0x017c
        L_0x0176:
            java.lang.String r0 = r0.imageUrl
            java.io.File r0 = org.telegram.messenger.ImageLoader.getHttpFilePath(r0, r8)
        L_0x017c:
            boolean r0 = org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r0, (java.io.File) r6)     // Catch:{ Exception -> 0x0181 }
            goto L_0x012d
        L_0x0181:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x0186:
            r0 = 0
            goto L_0x012d
        L_0x0188:
            boolean r3 = r1.isBlurred
            if (r3 == 0) goto L_0x01ad
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x01a8 }
            java.io.File r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x01a8 }
            r0.<init>(r3, r4)     // Catch:{ all -> 0x01a8 }
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ all -> 0x01a8 }
            r3.<init>(r0)     // Catch:{ all -> 0x01a8 }
            android.graphics.Bitmap r0 = r1.blurredBitmap     // Catch:{ all -> 0x01a8 }
            android.graphics.Bitmap$CompressFormat r9 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x01a8 }
            r11 = 87
            r0.compress(r9, r11, r3)     // Catch:{ all -> 0x01a8 }
            r3.close()     // Catch:{ all -> 0x01a8 }
            r0 = 1
            goto L_0x01ad
        L_0x01a8:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x01ad:
            r3 = 45
            java.lang.Object r9 = r1.currentWallpaper
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            java.lang.String r13 = "c"
            java.lang.String r14 = "d"
            if (r11 == 0) goto L_0x01c4
            org.telegram.tgnet.TLRPC$TL_wallPaper r9 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r9
            java.lang.String r8 = r9.slug
        L_0x01bd:
            r9 = 0
            r11 = 0
            r15 = 0
        L_0x01c0:
            r17 = 0
            goto L_0x0221
        L_0x01c4:
            boolean r11 = r9 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r11 == 0) goto L_0x01ed
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r9 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r9
            java.lang.String r8 = r9.slug
            boolean r8 = r14.equals(r8)
            if (r8 == 0) goto L_0x01d4
            r8 = r14
            goto L_0x01bd
        L_0x01d4:
            org.telegram.tgnet.TLRPC$TL_wallPaper r3 = r1.selectedPattern
            if (r3 == 0) goto L_0x01db
            java.lang.String r3 = r3.slug
            goto L_0x01dc
        L_0x01db:
            r3 = r13
        L_0x01dc:
            int r8 = r1.backgroundColor
            int r9 = r1.backgroundGradientColor1
            int r11 = r1.backgroundGradientColor2
            int r15 = r1.backgroundGradientColor3
            int r5 = r1.backgroundRotation
            r22 = r8
            r8 = r3
            r3 = r5
            r5 = r22
            goto L_0x01c0
        L_0x01ed:
            boolean r5 = r9 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
            if (r5 == 0) goto L_0x01fe
            org.telegram.ui.WallpapersListActivity$FileWallpaper r9 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r9
            java.lang.String r8 = r9.slug
            java.io.File r5 = r9.path
        L_0x01f7:
            r17 = r5
            r5 = 0
            r9 = 0
            r11 = 0
            r15 = 0
            goto L_0x0221
        L_0x01fe:
            boolean r5 = r9 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r5 == 0) goto L_0x021e
            org.telegram.messenger.MediaController$SearchImage r9 = (org.telegram.messenger.MediaController.SearchImage) r9
            org.telegram.tgnet.TLRPC$Photo r5 = r9.photo
            if (r5 == 0) goto L_0x0215
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            int r8 = r1.maxWallpaperSize
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r8, r12)
            java.io.File r5 = org.telegram.messenger.FileLoader.getPathToAttach(r5, r12)
            goto L_0x021b
        L_0x0215:
            java.lang.String r5 = r9.imageUrl
            java.io.File r5 = org.telegram.messenger.ImageLoader.getHttpFilePath(r5, r8)
        L_0x021b:
            java.lang.String r8 = ""
            goto L_0x01f7
        L_0x021e:
            r8 = r14
            r5 = 0
            goto L_0x01bd
        L_0x0221:
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r12 = new org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo
            r12.<init>()
            r12.fileName = r4
            r12.originalFileName = r2
            r12.slug = r8
            boolean r2 = r1.isBlurred
            r12.isBlurred = r2
            boolean r2 = r1.isMotion
            r12.isMotion = r2
            r12.color = r5
            r12.gradientColor1 = r9
            r12.gradientColor2 = r11
            r12.gradientColor3 = r15
            r12.rotation = r3
            float r2 = r1.currentIntensity
            r12.intensity = r2
            java.lang.Object r2 = r1.currentWallpaper
            boolean r4 = r2 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r4 == 0) goto L_0x02b0
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r2 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r2
            boolean r4 = r13.equals(r8)
            if (r4 != 0) goto L_0x025e
            boolean r4 = r10.equals(r8)
            if (r4 != 0) goto L_0x025e
            boolean r4 = r14.equals(r8)
            if (r4 != 0) goto L_0x025e
            r4 = r8
            goto L_0x025f
        L_0x025e:
            r4 = 0
        L_0x025f:
            float r13 = r2.intensity
            r14 = 0
            int r14 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
            if (r14 >= 0) goto L_0x0274
            org.telegram.ui.ActionBar.Theme$ThemeInfo r14 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r14 = r14.isDark()
            if (r14 != 0) goto L_0x0274
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            float r13 = r13 * r14
        L_0x0274:
            org.telegram.tgnet.TLRPC$WallPaper r14 = r2.parentWallpaper
            if (r14 == 0) goto L_0x02b0
            int r14 = r2.color
            if (r14 != r5) goto L_0x02b0
            int r5 = r2.gradientColor1
            if (r5 != r9) goto L_0x02b0
            int r5 = r2.gradientColor2
            if (r5 != r11) goto L_0x02b0
            int r5 = r2.gradientColor3
            if (r5 != r15) goto L_0x02b0
            java.lang.String r5 = r2.slug
            boolean r4 = android.text.TextUtils.equals(r5, r4)
            if (r4 == 0) goto L_0x02b0
            int r4 = r2.gradientRotation
            if (r4 != r3) goto L_0x02b0
            org.telegram.tgnet.TLRPC$TL_wallPaper r3 = r1.selectedPattern
            if (r3 == 0) goto L_0x02a6
            float r3 = r1.currentIntensity
            float r13 = r13 - r3
            float r3 = java.lang.Math.abs(r13)
            r4 = 981668463(0x3a83126f, float:0.001)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x02b0
        L_0x02a6:
            org.telegram.tgnet.TLRPC$WallPaper r2 = r2.parentWallpaper
            long r3 = r2.id
            r12.wallpaperId = r3
            long r2 = r2.access_hash
            r12.accessHash = r2
        L_0x02b0:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            if (r8 == 0) goto L_0x02bb
            r19 = 1
            goto L_0x02bd
        L_0x02bb:
            r19 = 0
        L_0x02bd:
            r20 = 0
            r16 = r2
            r18 = r12
            r16.saveWallpaperToServer(r17, r18, r19, r20)
            if (r0 == 0) goto L_0x0307
            java.lang.String r0 = "chat_serviceBackground"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            org.telegram.ui.ActionBar.Theme.serviceMessageColorBackup = r0
            java.lang.String r0 = r12.slug
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x02da
            r3 = 0
            goto L_0x02db
        L_0x02da:
            r3 = r12
        L_0x02db:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            r0.setOverrideWallpaper(r3)
            org.telegram.ui.ActionBar.Theme.reloadWallpaper()
            if (r7 != 0) goto L_0x0307
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
        L_0x0307:
            org.telegram.ui.ThemePreviewActivity$WallpaperActivityDelegate r0 = r1.delegate
            if (r0 == 0) goto L_0x030e
            r0.didSetNewBackground()
        L_0x030e:
            r23.finishFragment()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.lambda$createView$6$ThemePreviewActivity(android.view.View):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$7 */
    public /* synthetic */ void lambda$createView$7$ThemePreviewActivity(int i, WallpaperCheckBoxView wallpaperCheckBoxView, View view) {
        if (this.buttonsContainer.getAlpha() == 1.0f && this.patternViewAnimation == null) {
            int i2 = this.screenType;
            if ((i2 == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) && i == 2) {
                wallpaperCheckBoxView.setChecked(!wallpaperCheckBoxView.isChecked(), true);
                boolean isChecked = wallpaperCheckBoxView.isChecked();
                this.isMotion = isChecked;
                this.parallaxEffect.setEnabled(isChecked);
                animateMotionChange();
                return;
            }
            boolean z = false;
            if (i == 1 && (i2 == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper))) {
                if (this.checkBoxView[1].isChecked()) {
                    this.lastSelectedPattern = this.selectedPattern;
                    this.backgroundImage.setImageDrawable((Drawable) null);
                    this.selectedPattern = null;
                    this.isMotion = false;
                    updateButtonState(false, true);
                    animateMotionChange();
                    if (this.patternLayout[1].getVisibility() == 0) {
                        if (this.screenType == 1) {
                            showPatternsView(0, true, true);
                        } else {
                            showPatternsView(i, this.patternLayout[i].getVisibility() != 0, true);
                        }
                    }
                } else {
                    selectPattern(this.lastSelectedPattern != null ? -1 : 0);
                    if (this.screenType == 1) {
                        showPatternsView(1, true, true);
                    } else {
                        showPatternsView(i, this.patternLayout[i].getVisibility() != 0, true);
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
                showPatternsView(i, z, true);
            } else if (i2 != 1) {
                wallpaperCheckBoxView.setChecked(!wallpaperCheckBoxView.isChecked(), true);
                if (i == 0) {
                    boolean isChecked2 = wallpaperCheckBoxView.isChecked();
                    this.isBlurred = isChecked2;
                    if (isChecked2) {
                        this.backgroundImage.getImageReceiver().setForceCrossfade(true);
                    }
                    updateBlurred();
                    return;
                }
                boolean isChecked3 = wallpaperCheckBoxView.isChecked();
                this.isMotion = isChecked3;
                this.parallaxEffect.setEnabled(isChecked3);
                animateMotionChange();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$8 */
    public /* synthetic */ void lambda$createView$8$ThemePreviewActivity(int i, View view) {
        if (this.patternViewAnimation == null) {
            if (i == 0) {
                this.backgroundRotation = this.previousBackgroundRotation;
                setBackgroundColor(this.previousBackgroundGradientColor3, 3, true, true);
                setBackgroundColor(this.previousBackgroundGradientColor2, 2, true, true);
                setBackgroundColor(this.previousBackgroundGradientColor1, 1, true, true);
                setBackgroundColor(this.previousBackgroundColor, 0, true, true);
            } else {
                TLRPC$TL_wallPaper tLRPC$TL_wallPaper = this.previousSelectedPattern;
                this.selectedPattern = tLRPC$TL_wallPaper;
                if (tLRPC$TL_wallPaper == null) {
                    this.backgroundImage.setImageDrawable((Drawable) null);
                } else {
                    BackupImageView backupImageView = this.backgroundImage;
                    ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$TL_wallPaper.document);
                    String str = this.imageFilter;
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper2 = this.selectedPattern;
                    backupImageView.setImage(forDocument, str, (ImageLocation) null, (String) null, "jpg", tLRPC$TL_wallPaper2.document.size, 1, tLRPC$TL_wallPaper2);
                }
                this.checkBoxView[1].setChecked(this.selectedPattern != null, false);
                float f = this.previousIntensity;
                this.currentIntensity = f;
                this.intensitySeekBar.setProgress(f);
                this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
                updateButtonState(false, true);
                updateSelectedPattern(true);
            }
            if (this.screenType == 2) {
                showPatternsView(i, false, true);
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
            showPatternsView(0, true, true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$9 */
    public /* synthetic */ void lambda$createView$9$ThemePreviewActivity(int i, View view) {
        if (this.patternViewAnimation == null) {
            if (this.screenType == 2) {
                showPatternsView(i, false, true);
            } else {
                showPatternsView(0, true, true);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$10 */
    public /* synthetic */ void lambda$createView$10$ThemePreviewActivity(View view, int i) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$11 */
    public /* synthetic */ void lambda$createView$11$ThemePreviewActivity() {
        this.watchForKeyboardEndTime = SystemClock.elapsedRealtime() + 1500;
        this.frameLayout.invalidate();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$12 */
    public /* synthetic */ void lambda$createView$12$ThemePreviewActivity(View view) {
        cancelThemeApply(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$13 */
    public /* synthetic */ void lambda$createView$13$ThemePreviewActivity(View view) {
        Theme.ThemeAccent themeAccent;
        Theme.ThemeInfo previousTheme = Theme.getPreviousTheme();
        if (previousTheme != null) {
            int i = previousTheme.prevAccentId;
            if (i >= 0) {
                themeAccent = previousTheme.themeAccentsMap.get(i);
            } else {
                themeAccent = previousTheme.getAccent(false);
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
                SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
                edit.putString("lastDayTheme", this.applyingTheme.getKey());
                edit.commit();
            }
            finishFragment();
            if (this.screenType == 0) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didApplyNewTheme, previousTheme, themeAccent, Boolean.valueOf(this.deleteOnCancel));
            }
        }
    }

    /* access modifiers changed from: private */
    public void onColorsRotate() {
        if (this.screenType == 2) {
            this.backgroundRotation += 45;
            while (true) {
                int i = this.backgroundRotation;
                if (i >= 360) {
                    this.backgroundRotation = i - 360;
                } else {
                    setBackgroundColor(this.backgroundColor, 0, true, true);
                    return;
                }
            }
        } else {
            this.accent.backgroundRotation += 45;
            while (true) {
                Theme.ThemeAccent themeAccent = this.accent;
                int i2 = themeAccent.backgroundRotation;
                if (i2 >= 360) {
                    themeAccent.backgroundRotation = i2 - 360;
                } else {
                    Theme.refreshThemeColors();
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0206  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0222  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x022b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void selectColorType(int r28) {
        /*
            r27 = this;
            r0 = r27
            r1 = r28
            android.app.Activity r2 = r27.getParentActivity()
            if (r2 == 0) goto L_0x023b
            int r2 = r0.colorType
            if (r2 == r1) goto L_0x023b
            android.animation.AnimatorSet r2 = r0.patternViewAnimation
            if (r2 == 0) goto L_0x0014
            goto L_0x023b
        L_0x0014:
            r2 = 2
            if (r1 != r2) goto L_0x00ad
            boolean r3 = org.telegram.ui.ActionBar.Theme.hasCustomWallpaper()
            if (r3 != 0) goto L_0x002a
            org.telegram.ui.ActionBar.Theme$ThemeAccent r3 = r0.accent
            long r3 = r3.backgroundOverrideColor
            r5 = 4294967296(0xNUM, double:2.121995791E-314)
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 != 0) goto L_0x00ad
        L_0x002a:
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r2 = r27.getParentActivity()
            r1.<init>((android.content.Context) r2)
            r2 = 2131624682(0x7f0e02ea, float:1.887655E38)
            java.lang.String r3 = "ChangeChatBackground"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setTitle(r2)
            boolean r2 = org.telegram.ui.ActionBar.Theme.hasCustomWallpaper()
            if (r2 == 0) goto L_0x0077
            boolean r2 = org.telegram.ui.ActionBar.Theme.isCustomWallpaperColor()
            if (r2 == 0) goto L_0x004c
            goto L_0x0077
        L_0x004c:
            r2 = 2131624696(0x7f0e02f8, float:1.887658E38)
            java.lang.String r3 = "ChangeWallpaperToColor"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
            r2 = 2131624681(0x7f0e02e9, float:1.8876549E38)
            java.lang.String r3 = "Change"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$XLyTGpLgnZzSkgWkkwhsVFXEA-s r3 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$XLyTGpLgnZzSkgWkkwhsVFXEA-s
            r3.<init>()
            r1.setPositiveButton(r2, r3)
            r2 = 2131624658(0x7f0e02d2, float:1.8876502E38)
            java.lang.String r3 = "Cancel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 0
            r1.setNegativeButton(r2, r3)
            goto L_0x00a5
        L_0x0077:
            r2 = 2131624683(0x7f0e02eb, float:1.8876553E38)
            java.lang.String r3 = "ChangeColorToColor"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
            r2 = 2131627263(0x7f0e0cff, float:1.8881785E38)
            java.lang.String r3 = "Reset"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$6XXtprTekWzgh9AWfmkn_htebec r3 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$6XXtprTekWzgh9AWfmkn_htebec
            r3.<init>()
            r1.setPositiveButton(r2, r3)
            r2 = 2131625021(0x7f0e043d, float:1.8877238E38)
            java.lang.String r3 = "Continue"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$tUxs8w-e1HszHkqf6WLCXTyh-20 r3 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$tUxs8w-e1HszHkqf6WLCXTyh-20
            r3.<init>()
            r1.setNegativeButton(r2, r3)
        L_0x00a5:
            org.telegram.ui.ActionBar.AlertDialog r1 = r1.create()
            r0.showDialog(r1)
            return
        L_0x00ad:
            int r3 = r0.colorType
            r0.colorType = r1
            r4 = 3
            r5 = 1
            r6 = 0
            if (r1 == r5) goto L_0x01c8
            if (r1 == r2) goto L_0x00fb
            if (r1 == r4) goto L_0x00bc
            goto L_0x01f1
        L_0x00bc:
            android.widget.TextView r7 = r0.dropDown
            r8 = 2131624959(0x7f0e03ff, float:1.8877112E38)
            java.lang.String r9 = "ColorPickerMyMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r7.setText(r8)
            org.telegram.ui.Components.ColorPicker r9 = r0.colorPicker
            r10 = 2
            boolean r11 = r0.hasChanges(r4)
            r12 = 1
            org.telegram.ui.ActionBar.Theme$ThemeAccent r7 = r0.accent
            int r7 = r7.myMessagesGradientAccentColor
            if (r7 == 0) goto L_0x00da
            r13 = 2
            goto L_0x00db
        L_0x00da:
            r13 = 1
        L_0x00db:
            r14 = 1
            r15 = 0
            r16 = 0
            r9.setType(r10, r11, r12, r13, r14, r15, r16)
            org.telegram.ui.Components.ColorPicker r7 = r0.colorPicker
            org.telegram.ui.ActionBar.Theme$ThemeAccent r8 = r0.accent
            int r8 = r8.myMessagesGradientAccentColor
            r7.setColor(r8, r5)
            org.telegram.ui.Components.ColorPicker r7 = r0.colorPicker
            org.telegram.ui.ActionBar.Theme$ThemeAccent r8 = r0.accent
            int r9 = r8.myMessagesAccentColor
            if (r9 == 0) goto L_0x00f4
            goto L_0x00f6
        L_0x00f4:
            int r9 = r8.accentColor
        L_0x00f6:
            r7.setColor(r9, r6)
            goto L_0x01f1
        L_0x00fb:
            android.widget.TextView r7 = r0.dropDown
            r8 = 2131624957(0x7f0e03fd, float:1.8877108E38)
            java.lang.String r9 = "ColorPickerBackground"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r7.setText(r8)
            java.lang.String r7 = "chat_wallpaper"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            java.lang.String r8 = "chat_wallpaper_gradient_to"
            boolean r9 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r8)
            if (r9 == 0) goto L_0x011c
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            goto L_0x011d
        L_0x011c:
            r8 = 0
        L_0x011d:
            java.lang.String r9 = "key_chat_wallpaper_gradient_to2"
            boolean r10 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r9)
            if (r10 == 0) goto L_0x012a
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            goto L_0x012b
        L_0x012a:
            r9 = 0
        L_0x012b:
            java.lang.String r10 = "key_chat_wallpaper_gradient_to3"
            boolean r11 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r10)
            if (r11 == 0) goto L_0x0138
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            goto L_0x0139
        L_0x0138:
            r10 = 0
        L_0x0139:
            org.telegram.ui.ActionBar.Theme$ThemeAccent r11 = r0.accent
            long r12 = r11.backgroundGradientOverrideColor1
            int r14 = (int) r12
            r15 = 0
            if (r14 != 0) goto L_0x0147
            int r17 = (r12 > r15 ? 1 : (r12 == r15 ? 0 : -1))
            if (r17 == 0) goto L_0x0147
            r8 = 0
        L_0x0147:
            long r12 = r11.backgroundGradientOverrideColor2
            int r6 = (int) r12
            if (r6 != 0) goto L_0x0151
            int r18 = (r12 > r15 ? 1 : (r12 == r15 ? 0 : -1))
            if (r18 == 0) goto L_0x0151
            r9 = 0
        L_0x0151:
            long r12 = r11.backgroundGradientOverrideColor3
            int r5 = (int) r12
            if (r5 != 0) goto L_0x015b
            int r19 = (r12 > r15 ? 1 : (r12 == r15 ? 0 : -1))
            if (r19 == 0) goto L_0x015b
            r10 = 0
        L_0x015b:
            long r11 = r11.backgroundOverrideColor
            int r12 = (int) r11
            if (r14 != 0) goto L_0x0166
            if (r8 == 0) goto L_0x0163
            goto L_0x0166
        L_0x0163:
            r23 = 1
            goto L_0x0179
        L_0x0166:
            if (r5 != 0) goto L_0x0176
            if (r10 == 0) goto L_0x016b
            goto L_0x0176
        L_0x016b:
            if (r6 != 0) goto L_0x0173
            if (r9 == 0) goto L_0x0170
            goto L_0x0173
        L_0x0170:
            r23 = 2
            goto L_0x0179
        L_0x0173:
            r23 = 3
            goto L_0x0179
        L_0x0176:
            r11 = 4
            r23 = 4
        L_0x0179:
            org.telegram.ui.Components.ColorPicker r11 = r0.colorPicker
            r20 = 2
            boolean r21 = r0.hasChanges(r2)
            r22 = 1
            r24 = 0
            org.telegram.ui.ActionBar.Theme$ThemeAccent r13 = r0.accent
            int r13 = r13.backgroundRotation
            r26 = 0
            r19 = r11
            r25 = r13
            r19.setType(r20, r21, r22, r23, r24, r25, r26)
            org.telegram.ui.Components.ColorPicker r11 = r0.colorPicker
            if (r5 == 0) goto L_0x0197
            goto L_0x0198
        L_0x0197:
            r5 = r10
        L_0x0198:
            r11.setColor(r5, r4)
            org.telegram.ui.Components.ColorPicker r5 = r0.colorPicker
            if (r6 == 0) goto L_0x01a0
            goto L_0x01a1
        L_0x01a0:
            r6 = r9
        L_0x01a1:
            r5.setColor(r6, r2)
            org.telegram.ui.Components.ColorPicker r5 = r0.colorPicker
            if (r14 == 0) goto L_0x01a9
            goto L_0x01aa
        L_0x01a9:
            r14 = r8
        L_0x01aa:
            r6 = 1
            r5.setColor(r14, r6)
            org.telegram.ui.Components.ColorPicker r5 = r0.colorPicker
            if (r12 == 0) goto L_0x01b3
            r7 = r12
        L_0x01b3:
            r6 = 0
            r5.setColor(r7, r6)
            org.telegram.ui.ThemePreviewActivity$MessagesAdapter r5 = r0.messagesAdapter
            r5.notifyItemInserted(r6)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView2
            r7 = 1114636288(0x42700000, float:60.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r5.smoothScrollBy(r6, r7)
            goto L_0x01f0
        L_0x01c8:
            android.widget.TextView r5 = r0.dropDown
            r6 = 2131624958(0x7f0e03fe, float:1.887711E38)
            java.lang.String r7 = "ColorPickerMainColor"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setText(r6)
            org.telegram.ui.Components.ColorPicker r7 = r0.colorPicker
            r8 = 1
            r5 = 1
            boolean r9 = r0.hasChanges(r5)
            r10 = 0
            r11 = 1
            r12 = 0
            r13 = 0
            r14 = 0
            r7.setType(r8, r9, r10, r11, r12, r13, r14)
            org.telegram.ui.Components.ColorPicker r5 = r0.colorPicker
            org.telegram.ui.ActionBar.Theme$ThemeAccent r6 = r0.accent
            int r6 = r6.accentColor
            r7 = 0
            r5.setColor(r6, r7)
        L_0x01f0:
            r5 = 1
        L_0x01f1:
            if (r1 == r5) goto L_0x0204
            if (r1 != r4) goto L_0x01f6
            goto L_0x0204
        L_0x01f6:
            org.telegram.ui.Components.ColorPicker r1 = r0.colorPicker
            r2 = 0
            r1.setMinBrightness(r2)
            org.telegram.ui.Components.ColorPicker r1 = r0.colorPicker
            r2 = 1065353216(0x3var_, float:1.0)
            r1.setMaxBrightness(r2)
            goto L_0x023b
        L_0x0204:
            if (r3 != r2) goto L_0x021a
            org.telegram.ui.ThemePreviewActivity$MessagesAdapter r1 = r0.messagesAdapter
            r2 = 0
            r1.notifyItemRemoved(r2)
            android.widget.FrameLayout[] r1 = r0.patternLayout
            r3 = 1
            r1 = r1[r3]
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x021a
            r0.showPatternsView(r2, r3, r3)
        L_0x021a:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = r0.applyingTheme
            boolean r1 = r1.isDark()
            if (r1 == 0) goto L_0x022b
            org.telegram.ui.Components.ColorPicker r1 = r0.colorPicker
            r2 = 1045220557(0x3e4ccccd, float:0.2)
            r1.setMinBrightness(r2)
            goto L_0x023b
        L_0x022b:
            org.telegram.ui.Components.ColorPicker r1 = r0.colorPicker
            r2 = 1028443341(0x3d4ccccd, float:0.05)
            r1.setMinBrightness(r2)
            org.telegram.ui.Components.ColorPicker r1 = r0.colorPicker
            r2 = 1061997773(0x3f4ccccd, float:0.8)
            r1.setMaxBrightness(r2)
        L_0x023b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.selectColorType(int):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$selectColorType$14 */
    public /* synthetic */ void lambda$selectColorType$14$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent.backgroundOverrideColor == 4294967296L) {
            themeAccent.backgroundOverrideColor = 0;
            themeAccent.backgroundGradientOverrideColor1 = 0;
            themeAccent.backgroundGradientOverrideColor2 = 0;
            themeAccent.backgroundGradientOverrideColor3 = 0;
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        this.removeBackgroundOverride = true;
        Theme.resetCustomWallpaper(true);
        selectColorType(2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$selectColorType$15 */
    public /* synthetic */ void lambda$selectColorType$15$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        if (Theme.isCustomWallpaperColor()) {
            Theme.ThemeAccent themeAccent = this.accent;
            Theme.OverrideWallpaperInfo overrideWallpaperInfo = themeAccent.overrideWallpaper;
            themeAccent.backgroundOverrideColor = (long) overrideWallpaperInfo.color;
            themeAccent.backgroundGradientOverrideColor1 = (long) overrideWallpaperInfo.gradientColor1;
            themeAccent.backgroundGradientOverrideColor2 = (long) overrideWallpaperInfo.gradientColor2;
            themeAccent.backgroundGradientOverrideColor3 = (long) overrideWallpaperInfo.gradientColor3;
            themeAccent.backgroundRotation = overrideWallpaperInfo.rotation;
            String str = overrideWallpaperInfo.slug;
            themeAccent.patternSlug = str;
            float f = overrideWallpaperInfo.intensity;
            themeAccent.patternIntensity = f;
            this.currentIntensity = f;
            if (str != null && !"c".equals(str)) {
                int size = this.patterns.size();
                int i2 = 0;
                while (true) {
                    if (i2 >= size) {
                        break;
                    }
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) this.patterns.get(i2);
                    if (tLRPC$TL_wallPaper.pattern && this.accent.patternSlug.equals(tLRPC$TL_wallPaper.slug)) {
                        this.selectedPattern = tLRPC$TL_wallPaper;
                        break;
                    }
                    i2++;
                }
            } else {
                this.selectedPattern = null;
            }
            this.removeBackgroundOverride = true;
            this.checkBoxView[1].setChecked(this.selectedPattern != null, true);
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        Drawable background = this.backgroundImage.getBackground();
        if (background instanceof MotionBackgroundDrawable) {
            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) background;
            motionBackgroundDrawable.setPatternBitmap(100, (Bitmap) null);
            if (Theme.getActiveTheme().isDark()) {
                if (this.currentIntensity < 0.0f) {
                    this.backgroundImage.getImageReceiver().setGradientBitmap(motionBackgroundDrawable.getBitmap());
                }
                SeekBarView seekBarView = this.intensitySeekBar;
                if (seekBarView != null) {
                    seekBarView.setTwoSided(true);
                }
            } else {
                float f2 = this.currentIntensity;
                if (f2 < 0.0f) {
                    this.currentIntensity = -f2;
                }
            }
        }
        SeekBarView seekBarView2 = this.intensitySeekBar;
        if (seekBarView2 != null) {
            seekBarView2.setProgress(this.currentIntensity);
        }
        Theme.resetCustomWallpaper(true);
        selectColorType(2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$selectColorType$16 */
    public /* synthetic */ void lambda$selectColorType$16$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent.backgroundOverrideColor == 4294967296L) {
            themeAccent.backgroundOverrideColor = 0;
            themeAccent.backgroundGradientOverrideColor1 = 0;
            themeAccent.backgroundGradientOverrideColor2 = 0;
            themeAccent.backgroundGradientOverrideColor3 = 0;
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        this.removeBackgroundOverride = true;
        Theme.resetCustomWallpaper(true);
        selectColorType(2);
    }

    private void selectPattern(int i) {
        TLRPC$TL_wallPaper tLRPC$TL_wallPaper;
        if (i < 0 || i >= this.patterns.size()) {
            tLRPC$TL_wallPaper = this.lastSelectedPattern;
        } else {
            tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) this.patterns.get(i);
        }
        if (tLRPC$TL_wallPaper != null) {
            this.backgroundImage.setImage(ImageLocation.getForDocument(tLRPC$TL_wallPaper.document), this.imageFilter, (ImageLocation) null, (String) null, "jpg", tLRPC$TL_wallPaper.document.size, 1, tLRPC$TL_wallPaper);
            this.selectedPattern = tLRPC$TL_wallPaper;
            this.isMotion = this.checkBoxView[2].isChecked();
            updateButtonState(false, true);
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
                if (!(background instanceof MotionBackgroundDrawable)) {
                    background.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    background.draw(canvas);
                }
                Paint paint = new Paint(2);
                paint.setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
                paint.setAlpha((int) (Math.abs(this.currentIntensity) * 255.0f));
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                FileOutputStream fileOutputStream = new FileOutputStream(pathToWallpaper);
                createBitmap.compress(background instanceof MotionBackgroundDrawable ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 87, fileOutputStream);
                fileOutputStream.close();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    private boolean hasChanges(int i) {
        long j;
        int i2;
        if (this.editingTheme) {
            return false;
        }
        if (i == 1 || i == 2) {
            long j2 = this.backupBackgroundOverrideColor;
            if (j2 == 0) {
                int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                int i3 = (int) this.accent.backgroundOverrideColor;
                if (i3 == 0) {
                    i3 = defaultAccentColor;
                }
                if (i3 != defaultAccentColor) {
                    return true;
                }
            } else if (j2 != this.accent.backgroundOverrideColor) {
                return true;
            }
            long j3 = this.backupBackgroundGradientOverrideColor1;
            if (j3 == 0 && this.backupBackgroundGradientOverrideColor2 == 0 && this.backupBackgroundGradientOverrideColor3 == 0) {
                for (int i4 = 0; i4 < 3; i4++) {
                    if (i4 == 0) {
                        i2 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                        j = this.accent.backgroundGradientOverrideColor1;
                    } else if (i4 == 1) {
                        i2 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                        j = this.accent.backgroundGradientOverrideColor2;
                    } else {
                        i2 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to3");
                        j = this.accent.backgroundGradientOverrideColor3;
                    }
                    int i5 = (int) j;
                    if (i5 == 0 && j != 0) {
                        i5 = 0;
                    } else if (i5 == 0) {
                        i5 = i2;
                    }
                    if (i5 != i2) {
                        return true;
                    }
                }
            } else {
                Theme.ThemeAccent themeAccent = this.accent;
                if (!(j3 == themeAccent.backgroundGradientOverrideColor1 && this.backupBackgroundGradientOverrideColor2 == themeAccent.backgroundGradientOverrideColor2 && this.backupBackgroundGradientOverrideColor3 == themeAccent.backgroundGradientOverrideColor3)) {
                    return true;
                }
            }
            if (this.accent.backgroundRotation != this.backupBackgroundRotation) {
                return true;
            }
        }
        if (i == 1 || i == 3) {
            int i6 = this.backupMyMessagesAccentColor;
            if (i6 == 0) {
                Theme.ThemeAccent themeAccent2 = this.accent;
                int i7 = themeAccent2.myMessagesAccentColor;
                if (!(i7 == 0 || i7 == themeAccent2.accentColor)) {
                    return true;
                }
            } else if (i6 != this.accent.myMessagesAccentColor) {
                return true;
            }
            int i8 = this.backupMyMessagesGradientAccentColor;
            if (i8 != 0) {
                if (i8 != this.accent.myMessagesGradientAccentColor) {
                    return true;
                }
            } else if (this.accent.myMessagesGradientAccentColor != 0) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x006c, code lost:
        if (r7.accent.patternMotion == r7.isMotion) goto L_0x006e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0078, code lost:
        if (r7.accent.patternIntensity == r7.currentIntensity) goto L_0x00c6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkDiscard() {
        /*
            r7 = this;
            int r0 = r7.screenType
            r1 = 1
            if (r0 != r1) goto L_0x00c6
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r7.accent
            int r2 = r0.accentColor
            int r3 = r7.backupAccentColor
            if (r2 != r3) goto L_0x007a
            int r2 = r0.myMessagesAccentColor
            int r3 = r7.backupMyMessagesAccentColor
            if (r2 != r3) goto L_0x007a
            int r2 = r0.myMessagesGradientAccentColor
            int r3 = r7.backupMyMessagesGradientAccentColor
            if (r2 != r3) goto L_0x007a
            long r2 = r0.backgroundOverrideColor
            long r4 = r7.backupBackgroundOverrideColor
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x007a
            long r2 = r0.backgroundGradientOverrideColor1
            long r4 = r7.backupBackgroundGradientOverrideColor1
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x007a
            long r2 = r0.backgroundGradientOverrideColor2
            long r4 = r7.backupBackgroundGradientOverrideColor2
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x007a
            long r2 = r0.backgroundGradientOverrideColor3
            long r4 = r7.backupBackgroundGradientOverrideColor3
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x007a
            float r0 = r0.patternIntensity
            float r2 = r7.backupIntensity
            float r0 = r0 - r2
            float r0 = java.lang.Math.abs(r0)
            r2 = 981668463(0x3a83126f, float:0.001)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 > 0) goto L_0x007a
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r7.accent
            int r2 = r0.backgroundRotation
            int r3 = r7.backupBackgroundRotation
            if (r2 != r3) goto L_0x007a
            java.lang.String r0 = r0.patternSlug
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = r7.selectedPattern
            if (r2 == 0) goto L_0x005a
            java.lang.String r2 = r2.slug
            goto L_0x005c
        L_0x005a:
            java.lang.String r2 = ""
        L_0x005c:
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x007a
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = r7.selectedPattern
            if (r0 == 0) goto L_0x006e
            org.telegram.ui.ActionBar.Theme$ThemeAccent r2 = r7.accent
            boolean r2 = r2.patternMotion
            boolean r3 = r7.isMotion
            if (r2 != r3) goto L_0x007a
        L_0x006e:
            if (r0 == 0) goto L_0x00c6
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r7.accent
            float r0 = r0.patternIntensity
            float r2 = r7.currentIntensity
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x00c6
        L_0x007a:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r7.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r1 = 2131627329(0x7f0e0d41, float:1.888192E38)
            java.lang.String r2 = "SaveChangesAlertTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131627328(0x7f0e0d40, float:1.8881917E38)
            java.lang.String r2 = "SaveChangesAlertText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            r1 = 2131627327(0x7f0e0d3f, float:1.8881915E38)
            java.lang.String r2 = "Save"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$Ie0bTS0KbrLvEpTfsynRafHgBec r2 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$Ie0bTS0KbrLvEpTfsynRafHgBec
            r2.<init>()
            r0.setPositiveButton(r1, r2)
            r1 = 2131626708(0x7f0e0ad4, float:1.888066E38)
            java.lang.String r2 = "PassportDiscard"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$CgB0kD7fqURrqVCmbEa49m0V4UI r2 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$CgB0kD7fqURrqVCmbEa49m0V4UI
            r2.<init>()
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r7.showDialog(r0)
            r0 = 0
            return r0
        L_0x00c6:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.checkDiscard():boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkDiscard$17 */
    public /* synthetic */ void lambda$checkDiscard$17$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        this.actionBar2.getActionBarMenuOnItemClick().onItemClick(4);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkDiscard$18 */
    public /* synthetic */ void lambda$checkDiscard$18$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        cancelThemeApply(false);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        int i = this.screenType;
        if (i == 1 || i == 0) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        int i2 = this.screenType;
        if (i2 == 2 || i2 == 1) {
            Theme.setChangingWallpaper(true);
        }
        if (this.screenType == 0 && this.accent == null) {
            this.isMotion = Theme.isWallpaperMotion();
        } else {
            if (SharedConfig.getDevicePerformanceClass() == 0) {
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        FrameLayout frameLayout2 = this.frameLayout;
        if (!(frameLayout2 == null || this.onGlobalLayoutListener == null)) {
            frameLayout2.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
        int i = this.screenType;
        if (i == 2 || i == 1) {
            AndroidUtilities.runOnUIThread($$Lambda$ThemePreviewActivity$Sy23SUTWTAYQ0SrTqRjkKtp9KBY.INSTANCE);
        }
        int i2 = this.screenType;
        if (i2 == 2) {
            Bitmap bitmap = this.blurredBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.blurredBitmap = null;
            }
            Theme.applyChatServiceMessageColor();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
        } else if (i2 == 1 || i2 == 0) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        if (!(this.screenType == 0 && this.accent == null)) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
        }
        super.onFragmentDestroy();
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        super.onTransitionAnimationStart(z, z2);
        if (!z && this.screenType == 2) {
            Theme.applyChatServiceMessageColor();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
        }
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

    public void onProgressDownload(String str, long j, long j2) {
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), this.progressVisible);
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
        TLRPC$TL_wallPaper tLRPC$TL_wallPaper;
        if (i == NotificationCenter.emojiLoaded) {
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
            this.patternsDict.clear();
            int size = arrayList.size();
            boolean z = false;
            for (int i4 = 0; i4 < size; i4++) {
                TLRPC$WallPaper tLRPC$WallPaper = (TLRPC$WallPaper) arrayList.get(i4);
                if ((tLRPC$WallPaper instanceof TLRPC$TL_wallPaper) && tLRPC$WallPaper.pattern) {
                    TLRPC$Document tLRPC$Document = tLRPC$WallPaper.document;
                    if (tLRPC$Document != null && !this.patternsDict.containsKey(Long.valueOf(tLRPC$Document.id))) {
                        this.patterns.add(tLRPC$WallPaper);
                        this.patternsDict.put(Long.valueOf(tLRPC$WallPaper.document.id), tLRPC$WallPaper);
                    }
                    Theme.ThemeAccent themeAccent = this.accent;
                    if (themeAccent != null && themeAccent.patternSlug.equals(tLRPC$WallPaper.slug)) {
                        this.selectedPattern = (TLRPC$TL_wallPaper) tLRPC$WallPaper;
                        setCurrentImage(false);
                        updateButtonState(false, false);
                    } else if (this.accent == null) {
                        TLRPC$TL_wallPaper tLRPC$TL_wallPaper2 = this.selectedPattern;
                        if (tLRPC$TL_wallPaper2 != null) {
                            if (!tLRPC$TL_wallPaper2.slug.equals(tLRPC$WallPaper.slug)) {
                            }
                        }
                    }
                    z = true;
                }
            }
            if (!z && (tLRPC$TL_wallPaper = this.selectedPattern) != null) {
                this.patterns.add(0, tLRPC$TL_wallPaper);
            }
            PatternsAdapter patternsAdapter2 = this.patternsAdapter;
            if (patternsAdapter2 != null) {
                patternsAdapter2.notifyDataSetChanged();
            }
            long j = 0;
            int size2 = arrayList.size();
            for (int i5 = 0; i5 < size2; i5++) {
                TLRPC$WallPaper tLRPC$WallPaper2 = (TLRPC$WallPaper) arrayList.get(i5);
                if (tLRPC$WallPaper2 instanceof TLRPC$TL_wallPaper) {
                    long j2 = tLRPC$WallPaper2.id;
                    j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (j2 >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) j2))) % 2147483648L;
                }
            }
            TLRPC$TL_account_getWallPapers tLRPC$TL_account_getWallPapers = new TLRPC$TL_account_getWallPapers();
            tLRPC$TL_account_getWallPapers.hash = (int) j;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getWallPapers, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ThemePreviewActivity.this.lambda$didReceivedNotification$23$ThemePreviewActivity(tLObject, tLRPC$TL_error);
                }
            }), this.classGuid);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$23 */
    public /* synthetic */ void lambda$didReceivedNotification$23$ThemePreviewActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            public final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ThemePreviewActivity.this.lambda$didReceivedNotification$22$ThemePreviewActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$22 */
    public /* synthetic */ void lambda$didReceivedNotification$22$ThemePreviewActivity(TLObject tLObject) {
        Theme.ThemeAccent themeAccent;
        TLRPC$TL_wallPaper tLRPC$TL_wallPaper;
        if (tLObject instanceof TLRPC$TL_account_wallPapers) {
            TLRPC$TL_account_wallPapers tLRPC$TL_account_wallPapers = (TLRPC$TL_account_wallPapers) tLObject;
            this.patterns.clear();
            this.patternsDict.clear();
            int size = tLRPC$TL_account_wallPapers.wallpapers.size();
            boolean z = false;
            for (int i = 0; i < size; i++) {
                if (tLRPC$TL_account_wallPapers.wallpapers.get(i) instanceof TLRPC$TL_wallPaper) {
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper2 = (TLRPC$TL_wallPaper) tLRPC$TL_account_wallPapers.wallpapers.get(i);
                    if (tLRPC$TL_wallPaper2.pattern) {
                        TLRPC$Document tLRPC$Document = tLRPC$TL_wallPaper2.document;
                        if (tLRPC$Document != null && !this.patternsDict.containsKey(Long.valueOf(tLRPC$Document.id))) {
                            this.patterns.add(tLRPC$TL_wallPaper2);
                            this.patternsDict.put(Long.valueOf(tLRPC$TL_wallPaper2.document.id), tLRPC$TL_wallPaper2);
                        }
                        Theme.ThemeAccent themeAccent2 = this.accent;
                        if (themeAccent2 != null && themeAccent2.patternSlug.equals(tLRPC$TL_wallPaper2.slug)) {
                            this.selectedPattern = tLRPC$TL_wallPaper2;
                            setCurrentImage(false);
                            updateButtonState(false, false);
                        } else if (this.accent == null) {
                            TLRPC$TL_wallPaper tLRPC$TL_wallPaper3 = this.selectedPattern;
                            if (tLRPC$TL_wallPaper3 != null) {
                                if (!tLRPC$TL_wallPaper3.slug.equals(tLRPC$TL_wallPaper2.slug)) {
                                }
                            }
                        }
                        z = true;
                    }
                }
            }
            if (!z && (tLRPC$TL_wallPaper = this.selectedPattern) != null) {
                this.patterns.add(0, tLRPC$TL_wallPaper);
            }
            PatternsAdapter patternsAdapter2 = this.patternsAdapter;
            if (patternsAdapter2 != null) {
                patternsAdapter2.notifyDataSetChanged();
            }
            MessagesStorage.getInstance(this.currentAccount).putWallpapers(tLRPC$TL_account_wallPapers.wallpapers, 1);
        }
        if (this.selectedPattern == null && (themeAccent = this.accent) != null && !TextUtils.isEmpty(themeAccent.patternSlug)) {
            TLRPC$TL_account_getWallPaper tLRPC$TL_account_getWallPaper = new TLRPC$TL_account_getWallPaper();
            TLRPC$TL_inputWallPaperSlug tLRPC$TL_inputWallPaperSlug = new TLRPC$TL_inputWallPaperSlug();
            tLRPC$TL_inputWallPaperSlug.slug = this.accent.patternSlug;
            tLRPC$TL_account_getWallPaper.wallpaper = tLRPC$TL_inputWallPaperSlug;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_account_getWallPaper, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ThemePreviewActivity.this.lambda$didReceivedNotification$21$ThemePreviewActivity(tLObject, tLRPC$TL_error);
                }
            }), this.classGuid);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$21 */
    public /* synthetic */ void lambda$didReceivedNotification$21$ThemePreviewActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            public final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ThemePreviewActivity.this.lambda$didReceivedNotification$20$ThemePreviewActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$20 */
    public /* synthetic */ void lambda$didReceivedNotification$20$ThemePreviewActivity(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_wallPaper) {
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) tLObject;
            if (tLRPC$TL_wallPaper.pattern) {
                this.selectedPattern = tLRPC$TL_wallPaper;
                setCurrentImage(false);
                updateButtonState(false, false);
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
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
            if (this.screenType == 1) {
                if (this.editingTheme) {
                    Theme.ThemeAccent themeAccent = this.accent;
                    themeAccent.accentColor = this.backupAccentColor;
                    themeAccent.myMessagesAccentColor = this.backupMyMessagesAccentColor;
                    themeAccent.myMessagesGradientAccentColor = this.backupMyMessagesGradientAccentColor;
                    themeAccent.backgroundOverrideColor = this.backupBackgroundOverrideColor;
                    themeAccent.backgroundGradientOverrideColor1 = this.backupBackgroundGradientOverrideColor1;
                    themeAccent.backgroundGradientOverrideColor2 = this.backupBackgroundGradientOverrideColor2;
                    themeAccent.backgroundGradientOverrideColor3 = this.backupBackgroundGradientOverrideColor3;
                    themeAccent.backgroundRotation = this.backupBackgroundRotation;
                    themeAccent.patternSlug = this.backupSlug;
                    themeAccent.patternIntensity = this.backupIntensity;
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
                long j2 = this.backupBackgroundGradientOverrideColor1;
                if (j2 != 0) {
                    this.accent.backgroundGradientOverrideColor1 = j2;
                } else {
                    this.accent.backgroundGradientOverrideColor1 = 0;
                }
                long j3 = this.backupBackgroundGradientOverrideColor2;
                if (j3 != 0) {
                    this.accent.backgroundGradientOverrideColor2 = j3;
                } else {
                    this.accent.backgroundGradientOverrideColor2 = 0;
                }
                long j4 = this.backupBackgroundGradientOverrideColor3;
                if (j4 != 0) {
                    this.accent.backgroundGradientOverrideColor3 = j4;
                } else {
                    this.accent.backgroundGradientOverrideColor3 = 0;
                }
                this.accent.backgroundRotation = this.backupBackgroundRotation;
                if (i3 == 2) {
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                    int defaultAccentColor2 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                    int defaultAccentColor3 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                    int defaultAccentColor4 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to3");
                    Theme.ThemeAccent themeAccent = this.accent;
                    int i4 = (int) themeAccent.backgroundGradientOverrideColor1;
                    int i5 = (int) themeAccent.backgroundGradientOverrideColor2;
                    int i6 = (int) themeAccent.backgroundGradientOverrideColor3;
                    int i7 = (int) themeAccent.backgroundOverrideColor;
                    ColorPicker colorPicker2 = this.colorPicker;
                    if (i6 != 0) {
                        defaultAccentColor4 = i6;
                    }
                    colorPicker2.setColor(defaultAccentColor4, 3);
                    ColorPicker colorPicker3 = this.colorPicker;
                    if (i5 != 0) {
                        defaultAccentColor3 = i5;
                    }
                    colorPicker3.setColor(defaultAccentColor3, 2);
                    ColorPicker colorPicker4 = this.colorPicker;
                    if (i4 != 0) {
                        defaultAccentColor2 = i4;
                    }
                    colorPicker4.setColor(defaultAccentColor2, 1);
                    ColorPicker colorPicker5 = this.colorPicker;
                    if (i7 != 0) {
                        defaultAccentColor = i7;
                    }
                    colorPicker5.setColor(defaultAccentColor, 0);
                }
            }
            int i8 = this.colorType;
            if (i8 == 1 || i8 == 3) {
                int i9 = this.backupMyMessagesAccentColor;
                if (i9 != 0) {
                    this.accent.myMessagesAccentColor = i9;
                } else {
                    this.accent.myMessagesAccentColor = 0;
                }
                int i10 = this.backupMyMessagesGradientAccentColor;
                if (i10 != 0) {
                    this.accent.myMessagesGradientAccentColor = i10;
                } else {
                    this.accent.myMessagesGradientAccentColor = 0;
                }
                if (i8 == 3) {
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor, 1);
                    ColorPicker colorPicker6 = this.colorPicker;
                    Theme.ThemeAccent themeAccent2 = this.accent;
                    int i11 = themeAccent2.myMessagesAccentColor;
                    if (i11 == 0) {
                        i11 = themeAccent2.accentColor;
                    }
                    colorPicker6.setColor(i11, 0);
                }
            }
            Theme.refreshThemeColors();
            this.listView2.invalidateViews();
            return;
        }
        int i12 = this.lastPickedColorNum;
        if (!(i12 == -1 || i12 == i2)) {
            this.applyColorAction.run();
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
            } else if (i2 == 1) {
                int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                if (i != 0 || defaultAccentColor == 0) {
                    this.accent.backgroundGradientOverrideColor1 = (long) i;
                } else {
                    this.accent.backgroundGradientOverrideColor1 = 4294967296L;
                }
            } else if (i2 == 2) {
                int defaultAccentColor2 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                if (i != 0 || defaultAccentColor2 == 0) {
                    this.accent.backgroundGradientOverrideColor2 = (long) i;
                } else {
                    this.accent.backgroundGradientOverrideColor2 = 4294967296L;
                }
            } else if (i2 == 3) {
                int defaultAccentColor3 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to3");
                if (i != 0 || defaultAccentColor3 == 0) {
                    this.accent.backgroundGradientOverrideColor3 = (long) i;
                } else {
                    this.accent.backgroundGradientOverrideColor3 = 4294967296L;
                }
            }
            Theme.refreshThemeColors(true);
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
            updatePlayAnimationView(true);
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
        boolean z3 = obj instanceof TLRPC$TL_wallPaper;
        if (z3 || (obj instanceof MediaController.SearchImage)) {
            if (z2 && !this.progressVisible) {
                z2 = false;
            }
            if (z3) {
                TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) obj;
                str = FileLoader.getAttachFileName(tLRPC$TL_wallPaper.document);
                if (!TextUtils.isEmpty(str)) {
                    file = FileLoader.getPathToAttach(tLRPC$TL_wallPaper.document, true);
                    i = tLRPC$TL_wallPaper.document.size;
                } else {
                    return;
                }
            } else {
                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                TLRPC$Photo tLRPC$Photo = searchImage.photo;
                if (tLRPC$Photo != null) {
                    TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, this.maxWallpaperSize, true);
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
            float f = 1.0f;
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
            if (this.selectedPattern == null && (frameLayout2 = this.buttonsContainer) != null) {
                frameLayout2.setAlpha(exists ? 1.0f : 0.5f);
            }
            int i2 = this.screenType;
            if (i2 == 0) {
                this.doneButton.setEnabled(exists);
                TextView textView = this.doneButton;
                if (!exists) {
                    f = 0.5f;
                }
                textView.setAlpha(f);
            } else if (i2 == 2) {
                this.bottomOverlayChat.setEnabled(exists);
                TextView textView2 = this.bottomOverlayChatText;
                if (!exists) {
                    f = 0.5f;
                }
                textView2.setAlpha(f);
            } else {
                this.saveItem.setEnabled(exists);
                ActionBarMenuItem actionBarMenuItem = this.saveItem;
                if (!exists) {
                    f = 0.5f;
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
                int size = arrayList.size();
                while (true) {
                    if (i >= size) {
                        break;
                    }
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) this.patterns.get(i);
                    if (tLRPC$TL_wallPaper.id == colorWallpaper.patternId) {
                        this.selectedPattern = tLRPC$TL_wallPaper;
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
        int i = this.screenType;
        float f = 1.0f;
        float f2 = 0.0f;
        if (i == 1 || i == 2) {
            if (this.selectedPattern == null && (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
                this.checkBoxView[2].setChecked(false, true);
            }
            this.checkBoxView[this.selectedPattern != null ? (char) 2 : 0].setVisibility(0);
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
        boolean isEnabled = this.checkBoxView[0].isEnabled();
        TLRPC$TL_wallPaper tLRPC$TL_wallPaper = this.selectedPattern;
        if (isEnabled != (tLRPC$TL_wallPaper != null)) {
            if (tLRPC$TL_wallPaper == null) {
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

    /* access modifiers changed from: private */
    public void showPatternsView(int i, boolean z, boolean z2) {
        int i2;
        int i3 = i;
        char c = 0;
        final boolean z3 = z && i3 == 1 && this.selectedPattern != null;
        if (z) {
            if (i3 != 0) {
                this.previousSelectedPattern = this.selectedPattern;
                this.previousIntensity = this.currentIntensity;
                this.patternsAdapter.notifyDataSetChanged();
                ArrayList<Object> arrayList = this.patterns;
                if (arrayList != null) {
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper = this.selectedPattern;
                    if (tLRPC$TL_wallPaper == null) {
                        i2 = 0;
                    } else {
                        i2 = arrayList.indexOf(tLRPC$TL_wallPaper) + (this.screenType == 2 ? 1 : 0);
                    }
                    this.patternsLayoutManager.scrollToPositionWithOffset(i2, (this.patternsListView.getMeasuredWidth() - AndroidUtilities.dp(124.0f)) / 2);
                }
            } else if (this.screenType == 2) {
                this.previousBackgroundColor = this.backgroundColor;
                int i4 = this.backgroundGradientColor1;
                this.previousBackgroundGradientColor1 = i4;
                int i5 = this.backgroundGradientColor2;
                this.previousBackgroundGradientColor2 = i5;
                int i6 = this.backgroundGradientColor3;
                this.previousBackgroundGradientColor3 = i6;
                int i7 = this.backupBackgroundRotation;
                this.previousBackgroundRotation = i7;
                this.colorPicker.setType(0, false, true, i6 != 0 ? 4 : i5 != 0 ? 3 : i4 != 0 ? 2 : 1, false, i7, false);
                this.colorPicker.setColor(this.backgroundGradientColor3, 3);
                this.colorPicker.setColor(this.backgroundGradientColor2, 2);
                this.colorPicker.setColor(this.backgroundGradientColor1, 1);
                this.colorPicker.setColor(this.backgroundColor, 0);
            }
        }
        int i8 = this.screenType;
        if (i8 == 1 || i8 == 2) {
            this.checkBoxView[z3 ? (char) 2 : 0].setVisibility(0);
        }
        if (i3 == 1 && !this.intensitySeekBar.isTwoSided()) {
            float f = this.currentIntensity;
            if (f < 0.0f) {
                float f2 = -f;
                this.currentIntensity = f2;
                this.intensitySeekBar.setProgress(f2);
            }
        }
        float f3 = 1.0f;
        if (z2) {
            this.patternViewAnimation = new AnimatorSet();
            ArrayList arrayList2 = new ArrayList();
            int i9 = i3 == 0 ? 1 : 0;
            if (z) {
                this.patternLayout[i3].setVisibility(0);
                int i10 = this.screenType;
                if (i10 == 1) {
                    RecyclerListView recyclerListView = this.listView2;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    fArr[0] = i3 == 1 ? (float) (-AndroidUtilities.dp(21.0f)) : 0.0f;
                    arrayList2.add(ObjectAnimator.ofFloat(recyclerListView, property, fArr));
                    WallpaperCheckBoxView wallpaperCheckBoxView = this.checkBoxView[2];
                    Property property2 = View.ALPHA;
                    float[] fArr2 = new float[1];
                    fArr2[0] = z3 ? 1.0f : 0.0f;
                    arrayList2.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView, property2, fArr2));
                    WallpaperCheckBoxView wallpaperCheckBoxView2 = this.checkBoxView[0];
                    Property property3 = View.ALPHA;
                    float[] fArr3 = new float[1];
                    fArr3[0] = z3 ? 0.0f : 1.0f;
                    arrayList2.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView2, property3, fArr3));
                    if (i3 == 1) {
                        arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i3], View.ALPHA, new float[]{0.0f, 1.0f}));
                    } else {
                        this.patternLayout[i3].setAlpha(1.0f);
                        arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i9], View.ALPHA, new float[]{0.0f}));
                    }
                    this.colorPicker.hideKeyboard();
                } else if (i10 == 2) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.listView2, View.TRANSLATION_Y, new float[]{(float) ((-this.patternLayout[i3].getMeasuredHeight()) + AndroidUtilities.dp(48.0f))}));
                    WallpaperCheckBoxView wallpaperCheckBoxView3 = this.checkBoxView[2];
                    Property property4 = View.ALPHA;
                    float[] fArr4 = new float[1];
                    fArr4[0] = z3 ? 1.0f : 0.0f;
                    arrayList2.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView3, property4, fArr4));
                    WallpaperCheckBoxView wallpaperCheckBoxView4 = this.checkBoxView[0];
                    Property property5 = View.ALPHA;
                    float[] fArr5 = new float[1];
                    if (z3) {
                        f3 = 0.0f;
                    }
                    fArr5[0] = f3;
                    arrayList2.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView4, property5, fArr5));
                    arrayList2.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{0.0f}));
                    if (this.patternLayout[i9].getVisibility() == 0) {
                        arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i9], View.ALPHA, new float[]{0.0f}));
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
                        arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i9], View.ALPHA, new float[]{0.0f}));
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
            final boolean z4 = z;
            final int i11 = i9;
            final int i12 = i;
            this.patternViewAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = ThemePreviewActivity.this.patternViewAnimation = null;
                    if (z4 && ThemePreviewActivity.this.patternLayout[i11].getVisibility() == 0) {
                        ThemePreviewActivity.this.patternLayout[i11].setAlpha(1.0f);
                        ThemePreviewActivity.this.patternLayout[i11].setVisibility(4);
                    } else if (!z4) {
                        ThemePreviewActivity.this.patternLayout[i12].setVisibility(4);
                    }
                    char c = 2;
                    if (ThemePreviewActivity.this.screenType == 1 || ThemePreviewActivity.this.screenType == 2) {
                        WallpaperCheckBoxView[] access$4600 = ThemePreviewActivity.this.checkBoxView;
                        if (z3) {
                            c = 0;
                        }
                        access$4600[c].setVisibility(4);
                    } else if (i12 == 1) {
                        ThemePreviewActivity.this.patternLayout[i11].setAlpha(0.0f);
                    }
                }
            });
            this.patternViewAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.patternViewAnimation.setDuration(200);
            this.patternViewAnimation.start();
            return;
        }
        char c2 = i3 == 0 ? (char) 1 : 0;
        if (z) {
            this.patternLayout[i3].setVisibility(0);
            int i13 = this.screenType;
            if (i13 == 1) {
                this.listView2.setTranslationY(i3 == 1 ? (float) (-AndroidUtilities.dp(21.0f)) : 0.0f);
                this.checkBoxView[2].setAlpha(z3 ? 1.0f : 0.0f);
                this.checkBoxView[0].setAlpha(z3 ? 0.0f : 1.0f);
                if (i3 == 1) {
                    this.patternLayout[i3].setAlpha(1.0f);
                } else {
                    this.patternLayout[i3].setAlpha(1.0f);
                    this.patternLayout[c2].setAlpha(0.0f);
                }
                this.colorPicker.hideKeyboard();
            } else if (i13 == 2) {
                this.listView2.setTranslationY((float) ((-AndroidUtilities.dp(i3 == 0 ? 343.0f : 316.0f)) + AndroidUtilities.dp(48.0f)));
                this.checkBoxView[2].setAlpha(z3 ? 1.0f : 0.0f);
                this.checkBoxView[0].setAlpha(z3 ? 0.0f : 1.0f);
                this.backgroundImage.setAlpha(0.0f);
                if (this.patternLayout[c2].getVisibility() == 0) {
                    this.patternLayout[c2].setAlpha(0.0f);
                    this.patternLayout[i3].setAlpha(1.0f);
                    this.patternLayout[i3].setTranslationY(0.0f);
                } else {
                    this.patternLayout[i3].setTranslationY(0.0f);
                }
            } else {
                if (i3 == 1) {
                    this.patternLayout[i3].setAlpha(1.0f);
                } else {
                    this.patternLayout[i3].setAlpha(1.0f);
                    this.patternLayout[c2].setAlpha(0.0f);
                }
                this.colorPicker.hideKeyboard();
            }
        } else {
            this.listView2.setTranslationY(0.0f);
            FrameLayout[] frameLayoutArr3 = this.patternLayout;
            frameLayoutArr3[i3].setTranslationY((float) frameLayoutArr3[i3].getMeasuredHeight());
            this.checkBoxView[0].setAlpha(1.0f);
            this.checkBoxView[2].setAlpha(1.0f);
            this.backgroundImage.setAlpha(1.0f);
        }
        if (z && this.patternLayout[c2].getVisibility() == 0) {
            this.patternLayout[c2].setAlpha(1.0f);
            this.patternLayout[c2].setVisibility(4);
        } else if (!z) {
            this.patternLayout[i3].setVisibility(4);
        }
        int i14 = this.screenType;
        if (i14 == 1 || i14 == 2) {
            WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.checkBoxView;
            if (!z3) {
                c = 2;
            }
            wallpaperCheckBoxViewArr[c].setVisibility(4);
        } else if (i3 == 1) {
            this.patternLayout[c2].setAlpha(0.0f);
        }
    }

    private void animateMotionChange() {
        AnimatorSet animatorSet = this.motionAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.motionAnimation = animatorSet2;
        if (this.isMotion) {
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{this.parallaxScale}), ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{this.parallaxScale})});
        } else {
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_Y, new float[]{0.0f})});
        }
        this.motionAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.motionAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AnimatorSet unused = ThemePreviewActivity.this.motionAnimation = null;
            }
        });
        this.motionAnimation.start();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x006a, code lost:
        if (r12.backgroundGradientColor1 != 0) goto L_0x006c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0086, code lost:
        if (r0 != 0) goto L_0x006c;
     */
    /* JADX WARNING: Removed duplicated region for block: B:105:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00a2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updatePlayAnimationView(boolean r13) {
        /*
            r12 = this;
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 1
            r6 = 0
            r7 = 29
            if (r0 < r7) goto L_0x005e
            int r0 = r12.screenType
            java.lang.String r7 = "key_chat_wallpaper_gradient_to2"
            if (r0 != 0) goto L_0x001f
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r12.accent
            if (r0 == 0) goto L_0x001a
            long r7 = r0.backgroundGradientOverrideColor2
            int r0 = (int) r7
            goto L_0x0041
        L_0x001a:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            goto L_0x0041
        L_0x001f:
            if (r0 != r5) goto L_0x0035
            int r0 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r7)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r7 = r12.accent
            long r7 = r7.backgroundGradientOverrideColor2
            int r9 = (int) r7
            if (r9 != 0) goto L_0x0031
            int r10 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r10 == 0) goto L_0x0031
            goto L_0x0040
        L_0x0031:
            if (r9 == 0) goto L_0x0041
            r0 = r9
            goto L_0x0041
        L_0x0035:
            java.lang.Object r0 = r12.currentWallpaper
            boolean r7 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r7 == 0) goto L_0x0040
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r0
            int r0 = r12.backgroundGradientColor2
            goto L_0x0041
        L_0x0040:
            r0 = 0
        L_0x0041:
            if (r0 == 0) goto L_0x0055
            float r0 = r12.currentIntensity
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 < 0) goto L_0x0055
            org.telegram.ui.Components.BackupImageView r0 = r12.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            android.graphics.BlendMode r7 = android.graphics.BlendMode.SOFT_LIGHT
            r0.setBlendMode(r7)
            goto L_0x005e
        L_0x0055:
            org.telegram.ui.Components.BackupImageView r0 = r12.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            r0.setBlendMode(r1)
        L_0x005e:
            android.widget.FrameLayout r0 = r12.playAnimationView
            if (r0 != 0) goto L_0x0063
            return
        L_0x0063:
            int r0 = r12.screenType
            r7 = 2
            if (r0 != r7) goto L_0x006e
            int r0 = r12.backgroundGradientColor1
            if (r0 == 0) goto L_0x0089
        L_0x006c:
            r0 = 1
            goto L_0x008a
        L_0x006e:
            if (r0 != r5) goto L_0x0089
            java.lang.String r0 = "chat_wallpaper_gradient_to"
            int r0 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r0)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r8 = r12.accent
            long r8 = r8.backgroundGradientOverrideColor1
            int r10 = (int) r8
            if (r10 != 0) goto L_0x0083
            int r11 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r11 == 0) goto L_0x0083
            r0 = 0
            goto L_0x0086
        L_0x0083:
            if (r10 == 0) goto L_0x0086
            r0 = r10
        L_0x0086:
            if (r0 == 0) goto L_0x0089
            goto L_0x006c
        L_0x0089:
            r0 = 0
        L_0x008a:
            android.widget.FrameLayout r2 = r12.playAnimationView
            java.lang.Object r2 = r2.getTag()
            if (r2 == 0) goto L_0x0094
            r2 = 1
            goto L_0x0095
        L_0x0094:
            r2 = 0
        L_0x0095:
            android.widget.FrameLayout r3 = r12.playAnimationView
            if (r0 == 0) goto L_0x009d
            java.lang.Integer r1 = java.lang.Integer.valueOf(r5)
        L_0x009d:
            r3.setTag(r1)
            if (r2 == r0) goto L_0x01b6
            if (r0 == 0) goto L_0x00a9
            android.widget.FrameLayout r1 = r12.playAnimationView
            r1.setVisibility(r6)
        L_0x00a9:
            android.animation.AnimatorSet r1 = r12.playViewAnimator
            if (r1 == 0) goto L_0x00b0
            r1.cancel()
        L_0x00b0:
            r1 = 1107820544(0x42080000, float:34.0)
            r2 = 1065353216(0x3var_, float:1.0)
            if (r13 == 0) goto L_0x0168
            android.animation.AnimatorSet r13 = new android.animation.AnimatorSet
            r13.<init>()
            r12.playViewAnimator = r13
            r3 = 6
            android.animation.Animator[] r3 = new android.animation.Animator[r3]
            android.widget.FrameLayout r8 = r12.playAnimationView
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r5]
            if (r0 == 0) goto L_0x00cb
            r11 = 1065353216(0x3var_, float:1.0)
            goto L_0x00cc
        L_0x00cb:
            r11 = 0
        L_0x00cc:
            r10[r6] = r11
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r10)
            r3[r6] = r8
            android.widget.FrameLayout r8 = r12.playAnimationView
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r10 = new float[r5]
            if (r0 == 0) goto L_0x00df
            r11 = 1065353216(0x3var_, float:1.0)
            goto L_0x00e0
        L_0x00df:
            r11 = 0
        L_0x00e0:
            r10[r6] = r11
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r10)
            r3[r5] = r8
            android.widget.FrameLayout r8 = r12.playAnimationView
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r10 = new float[r5]
            if (r0 == 0) goto L_0x00f1
            goto L_0x00f2
        L_0x00f1:
            r2 = 0
        L_0x00f2:
            r10[r6] = r2
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r8, r9, r10)
            r3[r7] = r2
            r2 = 3
            org.telegram.ui.Components.WallpaperCheckBoxView[] r8 = r12.checkBoxView
            r8 = r8[r6]
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r10 = new float[r5]
            if (r0 == 0) goto L_0x010b
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r11 = (float) r11
            goto L_0x010c
        L_0x010b:
            r11 = 0
        L_0x010c:
            r10[r6] = r11
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r10)
            r3[r2] = r8
            r2 = 4
            org.telegram.ui.Components.WallpaperCheckBoxView[] r8 = r12.checkBoxView
            r8 = r8[r5]
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r10 = new float[r5]
            if (r0 == 0) goto L_0x0126
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r11 = -r11
            float r11 = (float) r11
            goto L_0x0127
        L_0x0126:
            r11 = 0
        L_0x0127:
            r10[r6] = r11
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r10)
            r3[r2] = r8
            r2 = 5
            org.telegram.ui.Components.WallpaperCheckBoxView[] r8 = r12.checkBoxView
            r7 = r8[r7]
            android.util.Property r8 = android.view.View.TRANSLATION_X
            float[] r5 = new float[r5]
            if (r0 == 0) goto L_0x013f
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r4 = (float) r0
        L_0x013f:
            r5[r6] = r4
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r7, r8, r5)
            r3[r2] = r0
            r13.playTogether(r3)
            android.animation.AnimatorSet r13 = r12.playViewAnimator
            r0 = 180(0xb4, double:8.9E-322)
            r13.setDuration(r0)
            android.animation.AnimatorSet r13 = r12.playViewAnimator
            org.telegram.ui.ThemePreviewActivity$28 r0 = new org.telegram.ui.ThemePreviewActivity$28
            r0.<init>()
            r13.addListener(r0)
            android.animation.AnimatorSet r13 = r12.playViewAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            r13.setInterpolator(r0)
            android.animation.AnimatorSet r13 = r12.playViewAnimator
            r13.start()
            goto L_0x01b6
        L_0x0168:
            android.widget.FrameLayout r13 = r12.playAnimationView
            if (r0 == 0) goto L_0x016f
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x0170
        L_0x016f:
            r3 = 0
        L_0x0170:
            r13.setAlpha(r3)
            android.widget.FrameLayout r13 = r12.playAnimationView
            if (r0 == 0) goto L_0x017a
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x017b
        L_0x017a:
            r3 = 0
        L_0x017b:
            r13.setScaleX(r3)
            android.widget.FrameLayout r13 = r12.playAnimationView
            if (r0 == 0) goto L_0x0183
            goto L_0x0184
        L_0x0183:
            r2 = 0
        L_0x0184:
            r13.setScaleY(r2)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r13 = r12.checkBoxView
            r13 = r13[r6]
            if (r0 == 0) goto L_0x0193
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r2 = (float) r2
            goto L_0x0194
        L_0x0193:
            r2 = 0
        L_0x0194:
            r13.setTranslationX(r2)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r13 = r12.checkBoxView
            r13 = r13[r5]
            if (r0 == 0) goto L_0x01a4
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = -r2
            float r2 = (float) r2
            goto L_0x01a5
        L_0x01a4:
            r2 = 0
        L_0x01a5:
            r13.setTranslationX(r2)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r13 = r12.checkBoxView
            r13 = r13[r7]
            if (r0 == 0) goto L_0x01b3
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r4 = (float) r0
        L_0x01b3:
            r13.setTranslationX(r4)
        L_0x01b6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.updatePlayAnimationView(boolean):void");
    }

    /* access modifiers changed from: private */
    public void setBackgroundColor(int i, int i2, boolean z, boolean z2) {
        MotionBackgroundDrawable motionBackgroundDrawable;
        if (i2 == 0) {
            this.backgroundColor = i;
        } else if (i2 == 1) {
            this.backgroundGradientColor1 = i;
        } else if (i2 == 2) {
            this.backgroundGradientColor2 = i;
        } else if (i2 == 3) {
            this.backgroundGradientColor3 = i;
        }
        updatePlayAnimationView(z2);
        if (this.checkBoxView != null) {
            int i3 = 0;
            while (true) {
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.checkBoxView;
                if (i3 >= wallpaperCheckBoxViewArr.length) {
                    break;
                }
                if (wallpaperCheckBoxViewArr[i3] != null) {
                    wallpaperCheckBoxViewArr[i3].setColor(i2, i);
                }
                i3++;
            }
        }
        if (this.backgroundGradientColor2 != 0) {
            if (this.intensitySeekBar != null && Theme.getActiveTheme().isDark()) {
                this.intensitySeekBar.setTwoSided(true);
            }
            Drawable background = this.backgroundImage.getBackground();
            if (background instanceof MotionBackgroundDrawable) {
                motionBackgroundDrawable = (MotionBackgroundDrawable) background;
            } else {
                motionBackgroundDrawable = new MotionBackgroundDrawable();
                motionBackgroundDrawable.setParentView(this.backgroundImage);
                if (this.rotatePreview) {
                    motionBackgroundDrawable.rotatePreview(false);
                }
            }
            motionBackgroundDrawable.setColors(this.backgroundColor, this.backgroundGradientColor1, this.backgroundGradientColor2, this.backgroundGradientColor3);
            this.backgroundImage.setBackground(motionBackgroundDrawable);
            this.patternColor = motionBackgroundDrawable.getPatternColor();
            this.checkColor = NUM;
        } else if (this.backgroundGradientColor1 != 0) {
            this.backgroundImage.setBackground(new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.backgroundRotation), new int[]{this.backgroundColor, this.backgroundGradientColor1}));
            int patternColor2 = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(this.backgroundColor, this.backgroundGradientColor1));
            this.checkColor = patternColor2;
            this.patternColor = patternColor2;
        } else {
            this.backgroundImage.setBackgroundColor(this.backgroundColor);
            int patternColor3 = AndroidUtilities.getPatternColor(this.backgroundColor);
            this.checkColor = patternColor3;
            this.patternColor = patternColor3;
        }
        if (!Theme.hasThemeKey("chat_serviceBackground") || (this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
            int i4 = this.checkColor;
            Theme.applyChatServiceMessageColor(new int[]{i4, i4, i4, i4}, this.backgroundImage.getBackground());
        } else if (Theme.getCachedWallpaper() instanceof MotionBackgroundDrawable) {
            int color = Theme.getColor("chat_serviceBackground");
            Theme.applyChatServiceMessageColor(new int[]{color, color, color, color}, this.backgroundImage.getBackground());
        }
        ImageView imageView = this.playAnimationImageView;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_serviceText"), PorterDuff.Mode.MULTIPLY));
        }
        BackupImageView backupImageView = this.backgroundImage;
        if (backupImageView != null) {
            backupImageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
            this.backgroundImage.getImageReceiver().setAlpha(Math.abs(this.currentIntensity));
            this.backgroundImage.invalidate();
            if (!Theme.getActiveTheme().isDark() || !(this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
                this.backgroundImage.getImageReceiver().setGradientBitmap((Bitmap) null);
                SeekBarView seekBarView = this.intensitySeekBar;
                if (seekBarView != null) {
                    seekBarView.setTwoSided(false);
                }
            } else {
                SeekBarView seekBarView2 = this.intensitySeekBar;
                if (seekBarView2 != null) {
                    seekBarView2.setTwoSided(true);
                }
                if (this.currentIntensity < 0.0f) {
                    this.backgroundImage.getImageReceiver().setGradientBitmap(((MotionBackgroundDrawable) this.backgroundImage.getBackground()).getBitmap());
                }
            }
            SeekBarView seekBarView3 = this.intensitySeekBar;
            if (seekBarView3 != null) {
                seekBarView3.setProgress(this.currentIntensity);
            }
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v14, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: org.telegram.ui.Components.BackgroundGradientDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v24, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v33, resolved type: android.graphics.drawable.ColorDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v34, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setCurrentImage(boolean r21) {
        /*
            r20 = this;
            r0 = r20
            int r1 = r0.screenType
            r2 = 0
            if (r1 != 0) goto L_0x0016
            org.telegram.ui.ActionBar.Theme$ThemeAccent r3 = r0.accent
            if (r3 != 0) goto L_0x0016
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            r1.setBackground(r3)
            goto L_0x035c
        L_0x0016:
            r3 = 3
            r4 = 2
            r5 = 0
            r6 = 1
            if (r1 != r4) goto L_0x0174
            java.lang.Object r1 = r0.currentWallpaper
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            r8 = 100
            if (r7 == 0) goto L_0x0051
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r1
            if (r21 == 0) goto L_0x0030
            org.telegram.tgnet.TLRPC$Document r3 = r1.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r8)
        L_0x0030:
            org.telegram.ui.Components.BackupImageView r9 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Document r3 = r1.document
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            java.lang.String r11 = r0.imageFilter
            org.telegram.tgnet.TLRPC$Document r3 = r1.document
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r5, (org.telegram.tgnet.TLRPC$Document) r3)
            org.telegram.tgnet.TLRPC$Document r3 = r1.document
            int r15 = r3.size
            r16 = 1
            java.lang.String r13 = "100_100_b"
            java.lang.String r14 = "jpg"
            r17 = r1
            r9.setImage(r10, r11, r12, r13, r14, r15, r16, r17)
            goto L_0x035c
        L_0x0051:
            boolean r7 = r1 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r7 == 0) goto L_0x00d0
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r1
            int r5 = r1.gradientRotation
            r0.backgroundRotation = r5
            int r5 = r1.color
            r0.setBackgroundColor(r5, r2, r6, r2)
            int r5 = r1.gradientColor1
            if (r5 == 0) goto L_0x0067
            r0.setBackgroundColor(r5, r6, r6, r2)
        L_0x0067:
            int r5 = r1.gradientColor2
            r0.setBackgroundColor(r5, r4, r6, r2)
            int r4 = r1.gradientColor3
            r0.setBackgroundColor(r4, r3, r6, r2)
            org.telegram.tgnet.TLRPC$TL_wallPaper r3 = r0.selectedPattern
            if (r3 == 0) goto L_0x008f
            org.telegram.ui.Components.BackupImageView r4 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Document r1 = r3.document
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.String r6 = r0.imageFilter
            r7 = 0
            r8 = 0
            org.telegram.tgnet.TLRPC$TL_wallPaper r12 = r0.selectedPattern
            org.telegram.tgnet.TLRPC$Document r1 = r12.document
            int r10 = r1.size
            r11 = 1
            java.lang.String r9 = "jpg"
            r4.setImage(r5, r6, r7, r8, r9, r10, r11, r12)
            goto L_0x035c
        L_0x008f:
            java.lang.String r3 = r1.slug
            java.lang.String r4 = "d"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x035c
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
            int r3 = r3.y
            int r3 = java.lang.Math.min(r4, r3)
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r5 = r4.x
            int r4 = r4.y
            int r4 = java.lang.Math.max(r5, r4)
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 29
            if (r5 < r6) goto L_0x00b6
            r1 = 1459617792(0x57000000, float:1.40737488E14)
            goto L_0x00c2
        L_0x00b6:
            int r5 = r1.color
            int r6 = r1.gradientColor1
            int r7 = r1.gradientColor2
            int r1 = r1.gradientColor3
            int r1 = org.telegram.ui.Components.MotionBackgroundDrawable.getPatternColor(r5, r6, r7, r1)
        L_0x00c2:
            org.telegram.ui.Components.BackupImageView r5 = r0.backgroundImage
            r6 = 2131558427(0x7f0d001b, float:1.874217E38)
            android.graphics.Bitmap r1 = org.telegram.messenger.SvgHelper.getBitmap((int) r6, (int) r3, (int) r4, (int) r1)
            r5.setImageBitmap(r1)
            goto L_0x035c
        L_0x00d0:
            boolean r3 = r1 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
            if (r3 == 0) goto L_0x0123
            android.graphics.Bitmap r3 = r0.currentWallpaperBitmap
            if (r3 == 0) goto L_0x00df
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            r1.setImageBitmap(r3)
            goto L_0x035c
        L_0x00df:
            org.telegram.ui.WallpapersListActivity$FileWallpaper r1 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r1
            java.io.File r3 = r1.originalPath
            if (r3 == 0) goto L_0x00f2
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            java.lang.String r3 = r3.getAbsolutePath()
            java.lang.String r4 = r0.imageFilter
            r1.setImage(r3, r4, r5)
            goto L_0x035c
        L_0x00f2:
            java.io.File r3 = r1.path
            if (r3 == 0) goto L_0x0103
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            java.lang.String r3 = r3.getAbsolutePath()
            java.lang.String r4 = r0.imageFilter
            r1.setImage(r3, r4, r5)
            goto L_0x035c
        L_0x0103:
            java.lang.String r3 = r1.slug
            java.lang.String r4 = "t"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x0118
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedWallpaper(r2, r1)
            r1.setImageDrawable(r3)
            goto L_0x035c
        L_0x0118:
            int r1 = r1.resId
            if (r1 == 0) goto L_0x035c
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            r3.setImageResource(r1)
            goto L_0x035c
        L_0x0123:
            boolean r3 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r3 == 0) goto L_0x035c
            org.telegram.messenger.MediaController$SearchImage r1 = (org.telegram.messenger.MediaController.SearchImage) r1
            org.telegram.tgnet.TLRPC$Photo r3 = r1.photo
            if (r3 == 0) goto L_0x0165
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r8)
            org.telegram.tgnet.TLRPC$Photo r4 = r1.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r4.sizes
            int r7 = r0.maxWallpaperSize
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r7, r6)
            if (r4 != r3) goto L_0x0140
            goto L_0x0141
        L_0x0140:
            r5 = r4
        L_0x0141:
            if (r5 == 0) goto L_0x0147
            int r4 = r5.size
            r15 = r4
            goto L_0x0148
        L_0x0147:
            r15 = 0
        L_0x0148:
            org.telegram.ui.Components.BackupImageView r9 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Photo r4 = r1.photo
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r5, (org.telegram.tgnet.TLRPC$Photo) r4)
            java.lang.String r11 = r0.imageFilter
            org.telegram.tgnet.TLRPC$Photo r4 = r1.photo
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r3, (org.telegram.tgnet.TLRPC$Photo) r4)
            r16 = 1
            java.lang.String r13 = "100_100_b"
            java.lang.String r14 = "jpg"
            r17 = r1
            r9.setImage(r10, r11, r12, r13, r14, r15, r16, r17)
            goto L_0x035c
        L_0x0165:
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            java.lang.String r4 = r1.imageUrl
            java.lang.String r5 = r0.imageFilter
            java.lang.String r1 = r1.thumbUrl
            java.lang.String r6 = "100_100_b"
            r3.setImage((java.lang.String) r4, (java.lang.String) r5, (java.lang.String) r1, (java.lang.String) r6)
            goto L_0x035c
        L_0x0174:
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r1 = r0.backgroundGradientDisposable
            if (r1 == 0) goto L_0x017d
            r1.dispose()
            r0.backgroundGradientDisposable = r5
        L_0x017d:
            java.lang.String r1 = "chat_wallpaper"
            int r1 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r1)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r7 = r0.accent
            long r7 = r7.backgroundOverrideColor
            int r8 = (int) r7
            if (r8 == 0) goto L_0x018b
            r1 = r8
        L_0x018b:
            java.lang.String r7 = "chat_wallpaper_gradient_to"
            int r7 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r7)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r8 = r0.accent
            long r8 = r8.backgroundGradientOverrideColor1
            int r10 = (int) r8
            r11 = 0
            if (r10 != 0) goto L_0x01a0
            int r13 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r13 == 0) goto L_0x01a0
            r7 = 0
            goto L_0x01a3
        L_0x01a0:
            if (r10 == 0) goto L_0x01a3
            r7 = r10
        L_0x01a3:
            java.lang.String r8 = "key_chat_wallpaper_gradient_to2"
            int r8 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r8)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r9 = r0.accent
            long r9 = r9.backgroundGradientOverrideColor2
            int r13 = (int) r9
            if (r13 != 0) goto L_0x01b6
            int r14 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r14 == 0) goto L_0x01b6
            r8 = 0
            goto L_0x01b9
        L_0x01b6:
            if (r13 == 0) goto L_0x01b9
            r8 = r13
        L_0x01b9:
            java.lang.String r9 = "key_chat_wallpaper_gradient_to3"
            int r9 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r9)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r10 = r0.accent
            long r13 = r10.backgroundGradientOverrideColor3
            int r15 = (int) r13
            if (r15 != 0) goto L_0x01cc
            int r16 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
            if (r16 == 0) goto L_0x01cc
            r9 = 0
            goto L_0x01cf
        L_0x01cc:
            if (r15 == 0) goto L_0x01cf
            r9 = r15
        L_0x01cf:
            java.lang.String r10 = r10.patternSlug
            boolean r10 = android.text.TextUtils.isEmpty(r10)
            if (r10 != 0) goto L_0x0255
            boolean r10 = org.telegram.ui.ActionBar.Theme.hasCustomWallpaper()
            if (r10 != 0) goto L_0x0255
            if (r8 == 0) goto L_0x0201
            org.telegram.ui.Components.BackupImageView r10 = r0.backgroundImage
            android.graphics.drawable.Drawable r10 = r10.getBackground()
            boolean r11 = r10 instanceof org.telegram.ui.Components.MotionBackgroundDrawable
            if (r11 == 0) goto L_0x01ec
            org.telegram.ui.Components.MotionBackgroundDrawable r10 = (org.telegram.ui.Components.MotionBackgroundDrawable) r10
            goto L_0x01fd
        L_0x01ec:
            org.telegram.ui.Components.MotionBackgroundDrawable r10 = new org.telegram.ui.Components.MotionBackgroundDrawable
            r10.<init>()
            org.telegram.ui.Components.BackupImageView r11 = r0.backgroundImage
            r10.setParentView(r11)
            boolean r11 = r0.rotatePreview
            if (r11 == 0) goto L_0x01fd
            r10.rotatePreview(r2)
        L_0x01fd:
            r10.setColors(r1, r7, r8, r9)
            goto L_0x022e
        L_0x0201:
            if (r7 == 0) goto L_0x0229
            org.telegram.ui.ActionBar.Theme$ThemeAccent r10 = r0.accent
            int r10 = r10.backgroundRotation
            android.graphics.drawable.GradientDrawable$Orientation r10 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r10)
            org.telegram.ui.Components.BackgroundGradientDrawable r11 = new org.telegram.ui.Components.BackgroundGradientDrawable
            int[] r12 = new int[r4]
            r12[r2] = r1
            r12[r6] = r7
            r11.<init>(r10, r12)
            org.telegram.ui.ThemePreviewActivity$29 r10 = new org.telegram.ui.ThemePreviewActivity$29
            r10.<init>()
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r12 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()
            r13 = 100
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r10 = r11.startDithering(r12, r10, r13)
            r0.backgroundGradientDisposable = r10
            r10 = r11
            goto L_0x022e
        L_0x0229:
            android.graphics.drawable.ColorDrawable r10 = new android.graphics.drawable.ColorDrawable
            r10.<init>(r1)
        L_0x022e:
            org.telegram.ui.Components.BackupImageView r11 = r0.backgroundImage
            r11.setBackground(r10)
            org.telegram.tgnet.TLRPC$TL_wallPaper r10 = r0.selectedPattern
            if (r10 == 0) goto L_0x026c
            org.telegram.ui.Components.BackupImageView r11 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Document r10 = r10.document
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForDocument(r10)
            java.lang.String r13 = r0.imageFilter
            r14 = 0
            r15 = 0
            org.telegram.tgnet.TLRPC$TL_wallPaper r10 = r0.selectedPattern
            org.telegram.tgnet.TLRPC$Document r3 = r10.document
            int r3 = r3.size
            r18 = 1
            java.lang.String r16 = "jpg"
            r17 = r3
            r19 = r10
            r11.setImage(r12, r13, r14, r15, r16, r17, r18, r19)
            goto L_0x026c
        L_0x0255:
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            if (r3 == 0) goto L_0x026c
            boolean r10 = r3 instanceof org.telegram.ui.Components.MotionBackgroundDrawable
            if (r10 == 0) goto L_0x0267
            r10 = r3
            org.telegram.ui.Components.MotionBackgroundDrawable r10 = (org.telegram.ui.Components.MotionBackgroundDrawable) r10
            org.telegram.ui.Components.BackupImageView r11 = r0.backgroundImage
            r10.setParentView(r11)
        L_0x0267:
            org.telegram.ui.Components.BackupImageView r10 = r0.backgroundImage
            r10.setBackground(r3)
        L_0x026c:
            if (r7 != 0) goto L_0x0277
            int r3 = org.telegram.messenger.AndroidUtilities.getPatternColor(r1)
            r0.checkColor = r3
            r0.patternColor = r3
            goto L_0x0290
        L_0x0277:
            if (r8 == 0) goto L_0x0284
            int r3 = org.telegram.ui.Components.MotionBackgroundDrawable.getPatternColor(r1, r7, r8, r9)
            r0.patternColor = r3
            r3 = 754974720(0x2d000000, float:7.2759576E-12)
            r0.checkColor = r3
            goto L_0x0290
        L_0x0284:
            int r3 = org.telegram.messenger.AndroidUtilities.getAverageColor(r1, r7)
            int r3 = org.telegram.messenger.AndroidUtilities.getPatternColor(r3)
            r0.checkColor = r3
            r0.patternColor = r3
        L_0x0290:
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            if (r3 == 0) goto L_0x0309
            org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
            android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter
            int r11 = r0.patternColor
            android.graphics.PorterDuff$Mode r12 = r0.blendMode
            r10.<init>(r11, r12)
            r3.setColorFilter(r10)
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
            float r10 = r0.currentIntensity
            float r10 = java.lang.Math.abs(r10)
            r3.setAlpha(r10)
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            r3.invalidate()
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r3 = r3.isDark()
            if (r3 == 0) goto L_0x02f0
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            android.graphics.drawable.Drawable r3 = r3.getBackground()
            boolean r3 = r3 instanceof org.telegram.ui.Components.MotionBackgroundDrawable
            if (r3 == 0) goto L_0x02f0
            org.telegram.ui.Components.SeekBarView r3 = r0.intensitySeekBar
            if (r3 == 0) goto L_0x02d3
            r3.setTwoSided(r6)
        L_0x02d3:
            float r3 = r0.currentIntensity
            r5 = 0
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 >= 0) goto L_0x0300
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
            org.telegram.ui.Components.BackupImageView r5 = r0.backgroundImage
            android.graphics.drawable.Drawable r5 = r5.getBackground()
            org.telegram.ui.Components.MotionBackgroundDrawable r5 = (org.telegram.ui.Components.MotionBackgroundDrawable) r5
            android.graphics.Bitmap r5 = r5.getBitmap()
            r3.setGradientBitmap(r5)
            goto L_0x0300
        L_0x02f0:
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
            r3.setGradientBitmap(r5)
            org.telegram.ui.Components.SeekBarView r3 = r0.intensitySeekBar
            if (r3 == 0) goto L_0x0300
            r3.setTwoSided(r2)
        L_0x0300:
            org.telegram.ui.Components.SeekBarView r3 = r0.intensitySeekBar
            if (r3 == 0) goto L_0x0309
            float r5 = r0.currentIntensity
            r3.setProgress(r5)
        L_0x0309:
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r0.checkBoxView
            if (r3 == 0) goto L_0x0331
            r3 = 0
        L_0x030e:
            org.telegram.ui.Components.WallpaperCheckBoxView[] r5 = r0.checkBoxView
            int r10 = r5.length
            if (r3 >= r10) goto L_0x0331
            r5 = r5[r3]
            r5.setColor(r2, r1)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r5 = r0.checkBoxView
            r5 = r5[r3]
            r5.setColor(r6, r7)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r5 = r0.checkBoxView
            r5 = r5[r3]
            r5.setColor(r4, r8)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r5 = r0.checkBoxView
            r5 = r5[r3]
            r10 = 3
            r5.setColor(r10, r9)
            int r3 = r3 + 1
            goto L_0x030e
        L_0x0331:
            android.widget.ImageView r1 = r0.playAnimationImageView
            if (r1 == 0) goto L_0x0345
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            java.lang.String r4 = "chat_serviceText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r5)
            r1.setColorFilter(r3)
        L_0x0345:
            android.widget.FrameLayout r1 = r0.buttonsContainer
            if (r1 == 0) goto L_0x035c
            int r1 = r1.getChildCount()
            r3 = 0
        L_0x034e:
            if (r3 >= r1) goto L_0x035c
            android.widget.FrameLayout r4 = r0.buttonsContainer
            android.view.View r4 = r4.getChildAt(r3)
            r4.invalidate()
            int r3 = r3 + 1
            goto L_0x034e
        L_0x035c:
            r0.rotatePreview = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.setCurrentImage(boolean):void");
    }

    public static class DialogsAdapter extends RecyclerListView.SelectionAdapter {
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
            View view = null;
            if (i == 0) {
                view = new DialogCell((DialogsActivity) null, this.mContext, false, false);
            } else if (i == 1) {
                view = new LoadingCell(this.mContext);
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
            this.showSecretMessages = themePreviewActivity.screenType == 0 && Utilities.random.nextInt(100) <= 1;
            this.mContext = context;
            this.messages = new ArrayList<>();
            int currentTimeMillis = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            if (themePreviewActivity.screenType == 2) {
                TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
                if (themePreviewActivity.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    tLRPC$TL_message.message = LocaleController.getString("BackgroundColorSinglePreviewLine2", NUM);
                } else {
                    tLRPC$TL_message.message = LocaleController.getString("BackgroundPreviewLine2", NUM);
                }
                int i = currentTimeMillis + 60;
                tLRPC$TL_message.date = i;
                tLRPC$TL_message.dialog_id = 1;
                tLRPC$TL_message.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$TL_message.from_id = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                tLRPC$TL_message.id = 1;
                tLRPC$TL_message.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser2 = new TLRPC$TL_peerUser();
                tLRPC$TL_message.peer_id = tLRPC$TL_peerUser2;
                tLRPC$TL_peerUser2.user_id = 0;
                MessageObject messageObject = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message, true, false);
                messageObject.eventId = 1;
                messageObject.resetLayout();
                this.messages.add(messageObject);
                TLRPC$TL_message tLRPC$TL_message2 = new TLRPC$TL_message();
                if (themePreviewActivity.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    tLRPC$TL_message2.message = LocaleController.getString("BackgroundColorSinglePreviewLine1", NUM);
                } else {
                    tLRPC$TL_message2.message = LocaleController.getString("BackgroundPreviewLine1", NUM);
                }
                tLRPC$TL_message2.date = i;
                tLRPC$TL_message2.dialog_id = 1;
                tLRPC$TL_message2.flags = 265;
                tLRPC$TL_message2.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message2.id = 1;
                tLRPC$TL_message2.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message2.out = false;
                TLRPC$TL_peerUser tLRPC$TL_peerUser3 = new TLRPC$TL_peerUser();
                tLRPC$TL_message2.peer_id = tLRPC$TL_peerUser3;
                tLRPC$TL_peerUser3.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                MessageObject messageObject2 = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message2, true, false);
                messageObject2.eventId = 1;
                messageObject2.resetLayout();
                this.messages.add(messageObject2);
            } else if (themePreviewActivity.screenType == 1) {
                TLRPC$TL_message tLRPC$TL_message3 = new TLRPC$TL_message();
                TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument = new TLRPC$TL_messageMediaDocument();
                tLRPC$TL_message3.media = tLRPC$TL_messageMediaDocument;
                tLRPC$TL_messageMediaDocument.document = new TLRPC$TL_document();
                TLRPC$Document tLRPC$Document = tLRPC$TL_message3.media.document;
                tLRPC$Document.mime_type = "audio/mp3";
                tLRPC$Document.file_reference = new byte[0];
                String str = "audio/ogg";
                tLRPC$Document.id = -2147483648L;
                tLRPC$Document.size = 2621440;
                tLRPC$Document.dc_id = Integer.MIN_VALUE;
                TLRPC$TL_documentAttributeFilename tLRPC$TL_documentAttributeFilename = new TLRPC$TL_documentAttributeFilename();
                tLRPC$TL_documentAttributeFilename.file_name = LocaleController.getString("NewThemePreviewReply2", NUM) + ".mp3";
                tLRPC$TL_message3.media.document.attributes.add(tLRPC$TL_documentAttributeFilename);
                int i2 = currentTimeMillis + 60;
                tLRPC$TL_message3.date = i2;
                tLRPC$TL_message3.dialog_id = 1;
                tLRPC$TL_message3.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser4 = new TLRPC$TL_peerUser();
                tLRPC$TL_message3.from_id = tLRPC$TL_peerUser4;
                tLRPC$TL_peerUser4.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                tLRPC$TL_message3.id = 1;
                tLRPC$TL_message3.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser5 = new TLRPC$TL_peerUser();
                tLRPC$TL_message3.peer_id = tLRPC$TL_peerUser5;
                tLRPC$TL_peerUser5.user_id = 0;
                MessageObject messageObject3 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message3, true, false);
                TLRPC$TL_message tLRPC$TL_message4 = new TLRPC$TL_message();
                String string = LocaleController.getString("NewThemePreviewLine3", NUM);
                StringBuilder sb = new StringBuilder(string);
                int indexOf = string.indexOf(42);
                int lastIndexOf = string.lastIndexOf(42);
                if (!(indexOf == -1 || lastIndexOf == -1)) {
                    sb.replace(lastIndexOf, lastIndexOf + 1, "");
                    sb.replace(indexOf, indexOf + 1, "");
                    TLRPC$TL_messageEntityTextUrl tLRPC$TL_messageEntityTextUrl = new TLRPC$TL_messageEntityTextUrl();
                    tLRPC$TL_messageEntityTextUrl.offset = indexOf;
                    tLRPC$TL_messageEntityTextUrl.length = (lastIndexOf - indexOf) - 1;
                    tLRPC$TL_messageEntityTextUrl.url = "https://telegram.org";
                    tLRPC$TL_message4.entities.add(tLRPC$TL_messageEntityTextUrl);
                }
                tLRPC$TL_message4.message = sb.toString();
                tLRPC$TL_message4.date = currentTimeMillis + 960;
                tLRPC$TL_message4.dialog_id = 1;
                tLRPC$TL_message4.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser6 = new TLRPC$TL_peerUser();
                tLRPC$TL_message4.from_id = tLRPC$TL_peerUser6;
                tLRPC$TL_peerUser6.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                tLRPC$TL_message4.id = 1;
                tLRPC$TL_message4.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message4.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser7 = new TLRPC$TL_peerUser();
                tLRPC$TL_message4.peer_id = tLRPC$TL_peerUser7;
                tLRPC$TL_peerUser7.user_id = 0;
                MessageObject messageObject4 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message4, true, false);
                messageObject4.resetLayout();
                messageObject4.eventId = 1;
                this.messages.add(messageObject4);
                TLRPC$TL_message tLRPC$TL_message5 = new TLRPC$TL_message();
                tLRPC$TL_message5.message = LocaleController.getString("NewThemePreviewLine1", NUM);
                tLRPC$TL_message5.date = i2;
                tLRPC$TL_message5.dialog_id = 1;
                tLRPC$TL_message5.flags = 265;
                tLRPC$TL_message5.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message5.id = 1;
                TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = new TLRPC$TL_messageReplyHeader();
                tLRPC$TL_message5.reply_to = tLRPC$TL_messageReplyHeader;
                tLRPC$TL_messageReplyHeader.reply_to_msg_id = 5;
                tLRPC$TL_message5.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message5.out = false;
                TLRPC$TL_peerUser tLRPC$TL_peerUser8 = new TLRPC$TL_peerUser();
                tLRPC$TL_message5.peer_id = tLRPC$TL_peerUser8;
                tLRPC$TL_peerUser8.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                MessageObject messageObject5 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message5, true, false);
                messageObject5.customReplyName = LocaleController.getString("NewThemePreviewName", NUM);
                messageObject5.eventId = 1;
                messageObject5.resetLayout();
                messageObject5.replyMessageObject = messageObject3;
                this.messages.add(messageObject5);
                this.messages.add(messageObject3);
                TLRPC$TL_message tLRPC$TL_message6 = new TLRPC$TL_message();
                tLRPC$TL_message6.date = currentTimeMillis + 120;
                tLRPC$TL_message6.dialog_id = 1;
                tLRPC$TL_message6.flags = 259;
                tLRPC$TL_message6.out = false;
                tLRPC$TL_message6.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message6.id = 1;
                TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument2 = new TLRPC$TL_messageMediaDocument();
                tLRPC$TL_message6.media = tLRPC$TL_messageMediaDocument2;
                tLRPC$TL_messageMediaDocument2.flags |= 3;
                tLRPC$TL_messageMediaDocument2.document = new TLRPC$TL_document();
                TLRPC$Document tLRPC$Document2 = tLRPC$TL_message6.media.document;
                tLRPC$Document2.mime_type = str;
                tLRPC$Document2.file_reference = new byte[0];
                TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio = new TLRPC$TL_documentAttributeAudio();
                tLRPC$TL_documentAttributeAudio.flags = 1028;
                tLRPC$TL_documentAttributeAudio.duration = 3;
                tLRPC$TL_documentAttributeAudio.voice = true;
                tLRPC$TL_documentAttributeAudio.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                tLRPC$TL_message6.media.document.attributes.add(tLRPC$TL_documentAttributeAudio);
                tLRPC$TL_message6.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser9 = new TLRPC$TL_peerUser();
                tLRPC$TL_message6.peer_id = tLRPC$TL_peerUser9;
                tLRPC$TL_peerUser9.user_id = 0;
                MessageObject messageObject6 = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message6, true, false);
                messageObject6.audioProgressSec = 1;
                messageObject6.audioProgress = 0.3f;
                messageObject6.useCustomPhoto = true;
                this.messages.add(messageObject6);
            } else {
                String str2 = "audio/ogg";
                if (this.showSecretMessages) {
                    TLRPC$TL_user tLRPC$TL_user = new TLRPC$TL_user();
                    tLRPC$TL_user.id = Integer.MAX_VALUE;
                    tLRPC$TL_user.first_name = "Me";
                    TLRPC$TL_user tLRPC$TL_user2 = new TLRPC$TL_user();
                    tLRPC$TL_user2.id = NUM;
                    tLRPC$TL_user2.first_name = "Serj";
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(tLRPC$TL_user);
                    arrayList.add(tLRPC$TL_user2);
                    MessagesController.getInstance(themePreviewActivity.currentAccount).putUsers(arrayList, true);
                    TLRPC$TL_message tLRPC$TL_message7 = new TLRPC$TL_message();
                    tLRPC$TL_message7.message = "Guess why Half-Life 3 was never released.";
                    int i3 = currentTimeMillis + 960;
                    tLRPC$TL_message7.date = i3;
                    tLRPC$TL_message7.dialog_id = -1;
                    tLRPC$TL_message7.flags = 259;
                    tLRPC$TL_message7.id = NUM;
                    tLRPC$TL_message7.media = new TLRPC$TL_messageMediaEmpty();
                    tLRPC$TL_message7.out = false;
                    TLRPC$TL_peerChat tLRPC$TL_peerChat = new TLRPC$TL_peerChat();
                    tLRPC$TL_message7.peer_id = tLRPC$TL_peerChat;
                    tLRPC$TL_peerChat.chat_id = 1;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser10 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message7.from_id = tLRPC$TL_peerUser10;
                    tLRPC$TL_peerUser10.user_id = tLRPC$TL_user2.id;
                    this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message7, true, false));
                    TLRPC$TL_message tLRPC$TL_message8 = new TLRPC$TL_message();
                    tLRPC$TL_message8.message = "No.\nAnd every unnecessary ping of the dev delays the release for 10 days.\nEvery request for ETA delays the release for 2 weeks.";
                    tLRPC$TL_message8.date = i3;
                    tLRPC$TL_message8.dialog_id = -1;
                    tLRPC$TL_message8.flags = 259;
                    tLRPC$TL_message8.id = 1;
                    tLRPC$TL_message8.media = new TLRPC$TL_messageMediaEmpty();
                    tLRPC$TL_message8.out = false;
                    TLRPC$TL_peerChat tLRPC$TL_peerChat2 = new TLRPC$TL_peerChat();
                    tLRPC$TL_message8.peer_id = tLRPC$TL_peerChat2;
                    tLRPC$TL_peerChat2.chat_id = 1;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser11 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message8.from_id = tLRPC$TL_peerUser11;
                    tLRPC$TL_peerUser11.user_id = tLRPC$TL_user2.id;
                    this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message8, true, false));
                    TLRPC$TL_message tLRPC$TL_message9 = new TLRPC$TL_message();
                    tLRPC$TL_message9.message = "Is source code for Android coming anytime soon?";
                    tLRPC$TL_message9.date = currentTimeMillis + 600;
                    tLRPC$TL_message9.dialog_id = -1;
                    tLRPC$TL_message9.flags = 259;
                    tLRPC$TL_message9.id = 1;
                    tLRPC$TL_message9.media = new TLRPC$TL_messageMediaEmpty();
                    tLRPC$TL_message9.out = false;
                    TLRPC$TL_peerChat tLRPC$TL_peerChat3 = new TLRPC$TL_peerChat();
                    tLRPC$TL_message9.peer_id = tLRPC$TL_peerChat3;
                    tLRPC$TL_peerChat3.chat_id = 1;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser12 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message9.from_id = tLRPC$TL_peerUser12;
                    tLRPC$TL_peerUser12.user_id = tLRPC$TL_user.id;
                    this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message9, true, false));
                    return;
                }
                TLRPC$TL_message tLRPC$TL_message10 = new TLRPC$TL_message();
                tLRPC$TL_message10.message = LocaleController.getString("ThemePreviewLine1", NUM);
                int i4 = currentTimeMillis + 60;
                tLRPC$TL_message10.date = i4;
                tLRPC$TL_message10.dialog_id = 1;
                tLRPC$TL_message10.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser13 = new TLRPC$TL_peerUser();
                tLRPC$TL_message10.from_id = tLRPC$TL_peerUser13;
                tLRPC$TL_peerUser13.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                tLRPC$TL_message10.id = 1;
                tLRPC$TL_message10.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message10.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser14 = new TLRPC$TL_peerUser();
                tLRPC$TL_message10.peer_id = tLRPC$TL_peerUser14;
                tLRPC$TL_peerUser14.user_id = 0;
                MessageObject messageObject7 = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message10, true, false);
                TLRPC$TL_message tLRPC$TL_message11 = new TLRPC$TL_message();
                tLRPC$TL_message11.message = LocaleController.getString("ThemePreviewLine2", NUM);
                tLRPC$TL_message11.date = currentTimeMillis + 960;
                tLRPC$TL_message11.dialog_id = 1;
                tLRPC$TL_message11.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser15 = new TLRPC$TL_peerUser();
                tLRPC$TL_message11.from_id = tLRPC$TL_peerUser15;
                tLRPC$TL_peerUser15.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                tLRPC$TL_message11.id = 1;
                tLRPC$TL_message11.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message11.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser16 = new TLRPC$TL_peerUser();
                tLRPC$TL_message11.peer_id = tLRPC$TL_peerUser16;
                tLRPC$TL_peerUser16.user_id = 0;
                this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message11, true, false));
                TLRPC$TL_message tLRPC$TL_message12 = new TLRPC$TL_message();
                tLRPC$TL_message12.date = currentTimeMillis + 130;
                tLRPC$TL_message12.dialog_id = 1;
                tLRPC$TL_message12.flags = 259;
                tLRPC$TL_message12.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message12.id = 5;
                TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument3 = new TLRPC$TL_messageMediaDocument();
                tLRPC$TL_message12.media = tLRPC$TL_messageMediaDocument3;
                tLRPC$TL_messageMediaDocument3.flags |= 3;
                tLRPC$TL_messageMediaDocument3.document = new TLRPC$TL_document();
                TLRPC$Document tLRPC$Document3 = tLRPC$TL_message12.media.document;
                tLRPC$Document3.mime_type = "audio/mp4";
                tLRPC$Document3.file_reference = new byte[0];
                TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio2 = new TLRPC$TL_documentAttributeAudio();
                tLRPC$TL_documentAttributeAudio2.duration = 243;
                tLRPC$TL_documentAttributeAudio2.performer = LocaleController.getString("ThemePreviewSongPerformer", NUM);
                tLRPC$TL_documentAttributeAudio2.title = LocaleController.getString("ThemePreviewSongTitle", NUM);
                tLRPC$TL_message12.media.document.attributes.add(tLRPC$TL_documentAttributeAudio2);
                tLRPC$TL_message12.out = false;
                TLRPC$TL_peerUser tLRPC$TL_peerUser17 = new TLRPC$TL_peerUser();
                tLRPC$TL_message12.peer_id = tLRPC$TL_peerUser17;
                tLRPC$TL_peerUser17.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message12, true, false));
                TLRPC$TL_message tLRPC$TL_message13 = new TLRPC$TL_message();
                tLRPC$TL_message13.message = LocaleController.getString("ThemePreviewLine3", NUM);
                tLRPC$TL_message13.date = i4;
                tLRPC$TL_message13.dialog_id = 1;
                tLRPC$TL_message13.flags = 265;
                tLRPC$TL_message13.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message13.id = 1;
                TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader2 = new TLRPC$TL_messageReplyHeader();
                tLRPC$TL_message13.reply_to = tLRPC$TL_messageReplyHeader2;
                tLRPC$TL_messageReplyHeader2.reply_to_msg_id = 5;
                tLRPC$TL_message13.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message13.out = false;
                TLRPC$TL_peerUser tLRPC$TL_peerUser18 = new TLRPC$TL_peerUser();
                tLRPC$TL_message13.peer_id = tLRPC$TL_peerUser18;
                tLRPC$TL_peerUser18.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                MessageObject messageObject8 = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message13, true, false);
                messageObject8.customReplyName = LocaleController.getString("ThemePreviewLine3Reply", NUM);
                messageObject8.replyMessageObject = messageObject7;
                this.messages.add(messageObject8);
                TLRPC$TL_message tLRPC$TL_message14 = new TLRPC$TL_message();
                tLRPC$TL_message14.date = currentTimeMillis + 120;
                tLRPC$TL_message14.dialog_id = 1;
                tLRPC$TL_message14.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser19 = new TLRPC$TL_peerUser();
                tLRPC$TL_message14.from_id = tLRPC$TL_peerUser19;
                tLRPC$TL_peerUser19.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                tLRPC$TL_message14.id = 1;
                TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument4 = new TLRPC$TL_messageMediaDocument();
                tLRPC$TL_message14.media = tLRPC$TL_messageMediaDocument4;
                tLRPC$TL_messageMediaDocument4.flags |= 3;
                tLRPC$TL_messageMediaDocument4.document = new TLRPC$TL_document();
                TLRPC$Document tLRPC$Document4 = tLRPC$TL_message14.media.document;
                tLRPC$Document4.mime_type = str2;
                tLRPC$Document4.file_reference = new byte[0];
                TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio3 = new TLRPC$TL_documentAttributeAudio();
                tLRPC$TL_documentAttributeAudio3.flags = 1028;
                tLRPC$TL_documentAttributeAudio3.duration = 3;
                tLRPC$TL_documentAttributeAudio3.voice = true;
                tLRPC$TL_documentAttributeAudio3.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                tLRPC$TL_message14.media.document.attributes.add(tLRPC$TL_documentAttributeAudio3);
                tLRPC$TL_message14.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser20 = new TLRPC$TL_peerUser();
                tLRPC$TL_message14.peer_id = tLRPC$TL_peerUser20;
                tLRPC$TL_peerUser20.user_id = 0;
                MessageObject messageObject9 = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message14, true, false);
                messageObject9.audioProgressSec = 1;
                messageObject9.audioProgress = 0.3f;
                messageObject9.useCustomPhoto = true;
                this.messages.add(messageObject9);
                this.messages.add(messageObject7);
                TLRPC$TL_message tLRPC$TL_message15 = new TLRPC$TL_message();
                tLRPC$TL_message15.date = currentTimeMillis + 10;
                tLRPC$TL_message15.dialog_id = 1;
                tLRPC$TL_message15.flags = 257;
                tLRPC$TL_message15.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message15.id = 1;
                TLRPC$TL_messageMediaPhoto tLRPC$TL_messageMediaPhoto = new TLRPC$TL_messageMediaPhoto();
                tLRPC$TL_message15.media = tLRPC$TL_messageMediaPhoto;
                tLRPC$TL_messageMediaPhoto.flags |= 3;
                tLRPC$TL_messageMediaPhoto.photo = new TLRPC$TL_photo();
                TLRPC$Photo tLRPC$Photo = tLRPC$TL_message15.media.photo;
                tLRPC$Photo.file_reference = new byte[0];
                tLRPC$Photo.has_stickers = false;
                tLRPC$Photo.id = 1;
                tLRPC$Photo.access_hash = 0;
                tLRPC$Photo.date = currentTimeMillis;
                TLRPC$TL_photoSize tLRPC$TL_photoSize = new TLRPC$TL_photoSize();
                tLRPC$TL_photoSize.size = 0;
                tLRPC$TL_photoSize.w = 500;
                tLRPC$TL_photoSize.h = 302;
                tLRPC$TL_photoSize.type = "s";
                tLRPC$TL_photoSize.location = new TLRPC$TL_fileLocationUnavailable();
                tLRPC$TL_message15.media.photo.sizes.add(tLRPC$TL_photoSize);
                tLRPC$TL_message15.message = LocaleController.getString("ThemePreviewLine4", NUM);
                tLRPC$TL_message15.out = false;
                TLRPC$TL_peerUser tLRPC$TL_peerUser21 = new TLRPC$TL_peerUser();
                tLRPC$TL_message15.peer_id = tLRPC$TL_peerUser21;
                tLRPC$TL_peerUser21.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                MessageObject messageObject10 = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message15, true, false);
                messageObject10.useCustomPhoto = true;
                this.messages.add(messageObject10);
            }
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
                r4 = -1
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
                r1 = 76
                r2 = 17
                android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r1, r2)
                r5.addView(r0, r1)
                goto L_0x0064
            L_0x0063:
                r5 = 0
            L_0x0064:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1 = -2
                r0.<init>((int) r4, (int) r1)
                r5.setLayoutParams(r0)
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r5)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.MessagesAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            boolean z;
            if (viewHolder.getItemViewType() != 2) {
                if (hasButtons()) {
                    i--;
                }
                MessageObject messageObject = this.messages.get(i);
                View view = viewHolder.itemView;
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                    boolean z2 = false;
                    chatMessageCell.isChat = false;
                    int i2 = i - 1;
                    int itemViewType = getItemViewType(i2);
                    int i3 = i + 1;
                    int itemViewType2 = getItemViewType(i3);
                    if (!(messageObject.messageOwner.reply_markup instanceof TLRPC$TL_replyInlineMarkup) && itemViewType == viewHolder.getItemViewType()) {
                        MessageObject messageObject2 = this.messages.get(i2);
                        if (messageObject2.isOutOwner() == messageObject.isOutOwner() && Math.abs(messageObject2.messageOwner.date - messageObject.messageOwner.date) <= 300) {
                            z = true;
                            if (itemViewType2 == viewHolder.getItemViewType() && i3 < this.messages.size()) {
                                MessageObject messageObject3 = this.messages.get(i3);
                                if (!(messageObject3.messageOwner.reply_markup instanceof TLRPC$TL_replyInlineMarkup) && messageObject3.isOutOwner() == messageObject.isOutOwner() && Math.abs(messageObject3.messageOwner.date - messageObject.messageOwner.date) <= 300) {
                                    z2 = true;
                                }
                            }
                            chatMessageCell.isChat = this.showSecretMessages;
                            chatMessageCell.setFullyDraw(true);
                            chatMessageCell.setMessageObject(messageObject, (MessageObject.GroupedMessages) null, z, z2);
                        }
                    }
                    z = false;
                    MessageObject messageObject32 = this.messages.get(i3);
                    z2 = true;
                    chatMessageCell.isChat = this.showSecretMessages;
                    chatMessageCell.setFullyDraw(true);
                    chatMessageCell.setMessageObject(messageObject, (MessageObject.GroupedMessages) null, z, z2);
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell chatActionCell = (ChatActionCell) view;
                    chatActionCell.setMessageObject(messageObject);
                    chatActionCell.setAlpha(1.0f);
                }
            }
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
                public TLRPC$TL_wallPaper getSelectedPattern() {
                    return ThemePreviewActivity.this.selectedPattern;
                }

                public int getCheckColor() {
                    return ThemePreviewActivity.this.checkColor;
                }

                public int getBackgroundColor() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundColor;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                    int i = (int) ThemePreviewActivity.this.accent.backgroundOverrideColor;
                    return i != 0 ? i : defaultAccentColor;
                }

                public int getBackgroundGradientColor1() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor1;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                    int i = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor1;
                    return i != 0 ? i : defaultAccentColor;
                }

                public int getBackgroundGradientColor2() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor2;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                    int i = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2;
                    return i != 0 ? i : defaultAccentColor;
                }

                public int getBackgroundGradientColor3() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor3;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to3");
                    int i = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor3;
                    return i != 0 ? i : defaultAccentColor;
                }

                public int getBackgroundGradientAngle() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundRotation;
                    }
                    return ThemePreviewActivity.this.accent.backgroundRotation;
                }

                public float getIntensity() {
                    return ThemePreviewActivity.this.currentIntensity;
                }

                public int getPatternColor() {
                    return ThemePreviewActivity.this.patternColor;
                }
            }));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            PatternCell patternCell = (PatternCell) viewHolder.itemView;
            patternCell.setPattern((TLRPC$TL_wallPaper) ThemePreviewActivity.this.patterns.get(i));
            patternCell.getImageReceiver().setColorFilter(new PorterDuffColorFilter(ThemePreviewActivity.this.patternColor, ThemePreviewActivity.this.blendMode));
            if (Build.VERSION.SDK_INT >= 29) {
                int i2 = 0;
                if (ThemePreviewActivity.this.screenType == 1) {
                    int defaultAccentColor = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                    int i3 = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2;
                    if (i3 != 0 || ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2 == 0) {
                        if (i3 != 0) {
                            defaultAccentColor = i3;
                        }
                        i2 = defaultAccentColor;
                    }
                } else if (ThemePreviewActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    i2 = ThemePreviewActivity.this.backgroundGradientColor2;
                }
                if (i2 == 0 || ThemePreviewActivity.this.currentIntensity < 0.0f) {
                    patternCell.getImageReceiver().setBlendMode((Object) null);
                } else {
                    ThemePreviewActivity.this.backgroundImage.getImageReceiver().setBlendMode(BlendMode.SOFT_LIGHT);
                }
            }
        }
    }

    private List<ThemeDescription> getThemeDescriptionsInternal() {
        $$Lambda$ThemePreviewActivity$yuwNlny2doG22ePX4MmjzWXG0 r9 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ThemePreviewActivity.this.lambda$getThemeDescriptionsInternal$24$ThemePreviewActivity();
            }
        };
        ArrayList arrayList = new ArrayList();
        $$Lambda$ThemePreviewActivity$yuwNlny2doG22ePX4MmjzWXG0 r7 = r9;
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
            for (int i = 0; i < this.patternLayout.length; i++) {
                arrayList.add(new ThemeDescription(this.patternLayout[i], 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
                arrayList.add(new ThemeDescription(this.patternLayout[i], 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
            }
            for (int i2 = 0; i2 < this.patternsButtonsContainer.length; i2++) {
                arrayList.add(new ThemeDescription(this.patternsButtonsContainer[i2], 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
                arrayList.add(new ThemeDescription(this.patternsButtonsContainer[i2], 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
            }
            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
            arrayList.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
            for (TextView themeDescription : this.patternsSaveButton) {
                arrayList.add(new ThemeDescription(themeDescription, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
            }
            for (TextView themeDescription2 : this.patternsCancelButton) {
                arrayList.add(new ThemeDescription(themeDescription2, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
            }
            arrayList.add(new ThemeDescription((View) this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"innerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
            arrayList.add(new ThemeDescription((View) this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"outerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
            arrayList.add(new ThemeDescription((View) this.intensityCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptionsInternal$24 */
    public /* synthetic */ void lambda$getThemeDescriptionsInternal$24$ThemePreviewActivity() {
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

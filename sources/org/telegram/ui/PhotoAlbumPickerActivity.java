package org.telegram.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.PhotoPickerAlbumsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.PhotoAlbumPickerActivity;
import org.telegram.ui.PhotoPickerActivity;

public class PhotoAlbumPickerActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public ArrayList<MediaController.AlbumEntry> albumsSorted = null;
    private boolean allowCaption;
    private boolean allowGifs;
    private boolean allowOrder = true;
    private boolean allowSearchImages = true;
    /* access modifiers changed from: private */
    public CharSequence caption;
    private ChatActivity chatActivity;
    /* access modifiers changed from: private */
    public int columnsCount = 2;
    /* access modifiers changed from: private */
    public EditTextEmoji commentTextView;
    /* access modifiers changed from: private */
    public PhotoAlbumPickerActivityDelegate delegate;
    private TextView emptyView;
    private FrameLayout frameLayout2;
    private ActionBarMenuSubItem[] itemCells;
    private ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private boolean loading = false;
    private int maxSelectedPhotos;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    private FrameLayout progressView;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    private int selectPhotoType;
    private View selectedCountView;
    /* access modifiers changed from: private */
    public HashMap<Object, Object> selectedPhotos = new HashMap<>();
    /* access modifiers changed from: private */
    public ArrayList<Object> selectedPhotosOrder = new ArrayList<>();
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    private boolean sendPressed;
    private View shadow;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    /* access modifiers changed from: private */
    public TextPaint textPaint = new TextPaint(1);
    private ImageView writeButton;
    private FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;

    public interface PhotoAlbumPickerActivityDelegate {
        void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i);

        void startPhotoSelectActivity();
    }

    static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ boolean lambda$createView$1(View view, MotionEvent motionEvent) {
        return true;
    }

    public PhotoAlbumPickerActivity(int i, boolean z, boolean z2, ChatActivity chatActivity2) {
        this.chatActivity = chatActivity2;
        this.selectPhotoType = i;
        this.allowGifs = z;
        this.allowCaption = z2;
    }

    public boolean onFragmentCreate() {
        if (this.selectPhotoType != 0 || !this.allowSearchImages) {
            this.albumsSorted = MediaController.allPhotoAlbums;
        } else {
            this.albumsSorted = MediaController.allMediaAlbums;
        }
        this.loading = this.albumsSorted == null;
        MediaController.loadGalleryPhotosAlbums(this.classGuid);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        ArrayList<MediaController.AlbumEntry> arrayList;
        Context context2 = context;
        this.actionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.actionBar.setTitleColor(Theme.getColor("dialogTextBlack"));
        this.actionBar.setItemsColor(Theme.getColor("dialogTextBlack"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PhotoAlbumPickerActivity.this.finishFragment();
                } else if (i == 1) {
                    if (PhotoAlbumPickerActivity.this.delegate != null) {
                        PhotoAlbumPickerActivity.this.finishFragment(false);
                        PhotoAlbumPickerActivity.this.delegate.startPhotoSelectActivity();
                    }
                } else if (i == 2) {
                    PhotoAlbumPickerActivity.this.openPhotoPicker((MediaController.AlbumEntry) null, 0);
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.allowSearchImages) {
            createMenu.addItem(2, NUM).setContentDescription(LocaleController.getString("Search", NUM));
        }
        ActionBarMenuItem addItem = createMenu.addItem(0, NUM);
        addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        addItem.addSubItem(1, NUM, (CharSequence) LocaleController.getString("OpenInExternalApp", NUM));
        this.sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;
            private int lastNotifyWidth;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                if (getKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    PhotoAlbumPickerActivity.this.commentTextView.hideEmojiView();
                    this.ignoreLayout = false;
                } else if (!AndroidUtilities.isInMultiwindow) {
                    size2 -= PhotoAlbumPickerActivity.this.commentTextView.getEmojiPadding();
                    i2 = View.MeasureSpec.makeMeasureSpec(size2, NUM);
                }
                int childCount = getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8)) {
                        if (PhotoAlbumPickerActivity.this.commentTextView == null || !PhotoAlbumPickerActivity.this.commentTextView.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((size2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x009f  */
            /* JADX WARNING: Removed duplicated region for block: B:39:0x00b9  */
            /* JADX WARNING: Removed duplicated region for block: B:47:0x00e0  */
            /* JADX WARNING: Removed duplicated region for block: B:48:0x00e9  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onLayout(boolean r9, int r10, int r11, int r12, int r13) {
                /*
                    r8 = this;
                    int r9 = r8.lastNotifyWidth
                    int r12 = r12 - r10
                    if (r9 == r12) goto L_0x0024
                    r8.lastNotifyWidth = r12
                    org.telegram.ui.PhotoAlbumPickerActivity r9 = org.telegram.ui.PhotoAlbumPickerActivity.this
                    org.telegram.ui.ActionBar.ActionBarPopupWindow r9 = r9.sendPopupWindow
                    if (r9 == 0) goto L_0x0024
                    org.telegram.ui.PhotoAlbumPickerActivity r9 = org.telegram.ui.PhotoAlbumPickerActivity.this
                    org.telegram.ui.ActionBar.ActionBarPopupWindow r9 = r9.sendPopupWindow
                    boolean r9 = r9.isShowing()
                    if (r9 == 0) goto L_0x0024
                    org.telegram.ui.PhotoAlbumPickerActivity r9 = org.telegram.ui.PhotoAlbumPickerActivity.this
                    org.telegram.ui.ActionBar.ActionBarPopupWindow r9 = r9.sendPopupWindow
                    r9.dismiss()
                L_0x0024:
                    int r9 = r8.getChildCount()
                    int r10 = r8.getKeyboardHeight()
                    r0 = 1101004800(0x41a00000, float:20.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                    r1 = 0
                    if (r10 > r0) goto L_0x004a
                    boolean r10 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r10 != 0) goto L_0x004a
                    boolean r10 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r10 != 0) goto L_0x004a
                    org.telegram.ui.PhotoAlbumPickerActivity r10 = org.telegram.ui.PhotoAlbumPickerActivity.this
                    org.telegram.ui.Components.EditTextEmoji r10 = r10.commentTextView
                    int r10 = r10.getEmojiPadding()
                    goto L_0x004b
                L_0x004a:
                    r10 = 0
                L_0x004b:
                    r8.setBottomClip(r10)
                L_0x004e:
                    if (r1 >= r9) goto L_0x0100
                    android.view.View r0 = r8.getChildAt(r1)
                    int r2 = r0.getVisibility()
                    r3 = 8
                    if (r2 != r3) goto L_0x005e
                    goto L_0x00fc
                L_0x005e:
                    android.view.ViewGroup$LayoutParams r2 = r0.getLayoutParams()
                    android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
                    int r3 = r0.getMeasuredWidth()
                    int r4 = r0.getMeasuredHeight()
                    int r5 = r2.gravity
                    r6 = -1
                    if (r5 != r6) goto L_0x0073
                    r5 = 51
                L_0x0073:
                    r6 = r5 & 7
                    r5 = r5 & 112(0x70, float:1.57E-43)
                    r6 = r6 & 7
                    r7 = 1
                    if (r6 == r7) goto L_0x0091
                    r7 = 5
                    if (r6 == r7) goto L_0x0087
                    int r6 = r2.leftMargin
                    int r7 = r8.getPaddingLeft()
                    int r6 = r6 + r7
                    goto L_0x009b
                L_0x0087:
                    int r6 = r12 - r3
                    int r7 = r2.rightMargin
                    int r6 = r6 - r7
                    int r7 = r8.getPaddingRight()
                    goto L_0x009a
                L_0x0091:
                    int r6 = r12 - r3
                    int r6 = r6 / 2
                    int r7 = r2.leftMargin
                    int r6 = r6 + r7
                    int r7 = r2.rightMargin
                L_0x009a:
                    int r6 = r6 - r7
                L_0x009b:
                    r7 = 16
                    if (r5 == r7) goto L_0x00b9
                    r7 = 48
                    if (r5 == r7) goto L_0x00b1
                    r7 = 80
                    if (r5 == r7) goto L_0x00aa
                    int r2 = r2.topMargin
                    goto L_0x00c6
                L_0x00aa:
                    int r5 = r13 - r10
                    int r5 = r5 - r11
                    int r5 = r5 - r4
                    int r2 = r2.bottomMargin
                    goto L_0x00c4
                L_0x00b1:
                    int r2 = r2.topMargin
                    int r5 = r8.getPaddingTop()
                    int r2 = r2 + r5
                    goto L_0x00c6
                L_0x00b9:
                    int r5 = r13 - r10
                    int r5 = r5 - r11
                    int r5 = r5 - r4
                    int r5 = r5 / 2
                    int r7 = r2.topMargin
                    int r5 = r5 + r7
                    int r2 = r2.bottomMargin
                L_0x00c4:
                    int r2 = r5 - r2
                L_0x00c6:
                    org.telegram.ui.PhotoAlbumPickerActivity r5 = org.telegram.ui.PhotoAlbumPickerActivity.this
                    org.telegram.ui.Components.EditTextEmoji r5 = r5.commentTextView
                    if (r5 == 0) goto L_0x00f7
                    org.telegram.ui.PhotoAlbumPickerActivity r5 = org.telegram.ui.PhotoAlbumPickerActivity.this
                    org.telegram.ui.Components.EditTextEmoji r5 = r5.commentTextView
                    boolean r5 = r5.isPopupView(r0)
                    if (r5 == 0) goto L_0x00f7
                    boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r2 == 0) goto L_0x00e9
                    int r2 = r8.getMeasuredHeight()
                    int r5 = r0.getMeasuredHeight()
                    goto L_0x00f6
                L_0x00e9:
                    int r2 = r8.getMeasuredHeight()
                    int r5 = r8.getKeyboardHeight()
                    int r2 = r2 + r5
                    int r5 = r0.getMeasuredHeight()
                L_0x00f6:
                    int r2 = r2 - r5
                L_0x00f7:
                    int r3 = r3 + r6
                    int r4 = r4 + r2
                    r0.layout(r6, r2, r3, r4)
                L_0x00fc:
                    int r1 = r1 + 1
                    goto L_0x004e
                L_0x0100:
                    r8.notifyHeightChanged()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoAlbumPickerActivity.AnonymousClass2.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.sizeNotifierFrameLayout.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.fragmentView = this.sizeNotifierFrameLayout;
        this.actionBar.setTitle(LocaleController.getString("Gallery", NUM));
        this.listView = new RecyclerListView(context2);
        this.listView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(54.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.listView.setDrawingCacheEnabled(false);
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        recyclerListView.setAdapter(listAdapter2);
        this.listView.setGlowColor(Theme.getColor("dialogBackground"));
        this.emptyView = new TextView(context2);
        this.emptyView.setTextColor(-8355712);
        this.emptyView.setTextSize(20.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.emptyView.setText(LocaleController.getString("NoPhotos", NUM));
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.emptyView.setOnTouchListener($$Lambda$PhotoAlbumPickerActivity$2ZdkXHoPXptp2wpUszGZ5G4bMiQ.INSTANCE);
        this.progressView = new FrameLayout(context2);
        this.progressView.setVisibility(8);
        this.sizeNotifierFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context2);
        radialProgressView.setProgressColor(-11371101);
        this.progressView.addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        this.shadow = new View(context2);
        this.shadow.setBackgroundResource(NUM);
        this.shadow.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.frameLayout2 = new FrameLayout(context2);
        this.frameLayout2.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        this.frameLayout2.setOnTouchListener($$Lambda$PhotoAlbumPickerActivity$qmrmGuRZuSx5NpHh8Dd6TzQejJo.INSTANCE);
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.commentTextView = new EditTextEmoji(context2, this.sizeNotifierFrameLayout, (BaseFragment) null, 1);
        this.commentTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
        this.commentTextView.setHint(LocaleController.getString("AddCaption", NUM));
        EditTextBoldCursor editText = this.commentTextView.getEditText();
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
        CharSequence charSequence = this.caption;
        if (charSequence != null) {
            this.commentTextView.setText(charSequence);
        }
        this.writeButtonContainer = new FrameLayout(context2);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.writeButtonContainer.setContentDescription(LocaleController.getString("Send", NUM));
        this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 12.0f, 10.0f));
        this.writeButton = new ImageView(context2);
        this.writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("dialogFloatingButton"), Theme.getColor(Build.VERSION.SDK_INT >= 21 ? "dialogFloatingButtonPressed" : "dialogFloatingButton"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, this.writeButtonDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            this.writeButtonDrawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(this.writeButtonDrawable);
        this.writeButton.setImageResource(NUM);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
        this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
        if (Build.VERSION.SDK_INT >= 21) {
            this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, Build.VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        this.writeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoAlbumPickerActivity.this.lambda$createView$3$PhotoAlbumPickerActivity(view);
            }
        });
        this.writeButton.setOnLongClickListener(new View.OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return PhotoAlbumPickerActivity.this.lambda$createView$7$PhotoAlbumPickerActivity(view);
            }
        });
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedCountView = new View(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, PhotoAlbumPickerActivity.this.selectedPhotosOrder.size()))});
                int ceil = (int) Math.ceil((double) PhotoAlbumPickerActivity.this.textPaint.measureText(format));
                int max = Math.max(AndroidUtilities.dp(16.0f) + ceil, AndroidUtilities.dp(24.0f));
                int measuredWidth = getMeasuredWidth() / 2;
                int measuredHeight = getMeasuredHeight() / 2;
                PhotoAlbumPickerActivity.this.textPaint.setColor(Theme.getColor("dialogRoundCheckBoxCheck"));
                PhotoAlbumPickerActivity.this.paint.setColor(Theme.getColor("dialogBackground"));
                int i = max / 2;
                int i2 = measuredWidth - i;
                int i3 = i + measuredWidth;
                PhotoAlbumPickerActivity.this.rect.set((float) i2, 0.0f, (float) i3, (float) getMeasuredHeight());
                canvas.drawRoundRect(PhotoAlbumPickerActivity.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), PhotoAlbumPickerActivity.this.paint);
                PhotoAlbumPickerActivity.this.paint.setColor(Theme.getColor("dialogRoundCheckBox"));
                PhotoAlbumPickerActivity.this.rect.set((float) (i2 + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (i3 - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                canvas.drawRoundRect(PhotoAlbumPickerActivity.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), PhotoAlbumPickerActivity.this.paint);
                canvas.drawText(format, (float) (measuredWidth - (ceil / 2)), (float) AndroidUtilities.dp(16.2f), PhotoAlbumPickerActivity.this.textPaint);
            }
        };
        this.selectedCountView.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        this.sizeNotifierFrameLayout.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -2.0f, 9.0f));
        if (this.selectPhotoType != 0) {
            this.commentTextView.setVisibility(8);
        }
        if (!this.loading || ((arrayList = this.albumsSorted) != null && (arrayList == null || !arrayList.isEmpty()))) {
            this.progressView.setVisibility(8);
            this.listView.setEmptyView(this.emptyView);
        } else {
            this.progressView.setVisibility(0);
            this.listView.setEmptyView((View) null);
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$3$PhotoAlbumPickerActivity(View view) {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 == null || !chatActivity2.isInScheduleMode()) {
            sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, true, 0);
            finishFragment();
            return;
        }
        AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
            public final void didSelectDate(boolean z, int i) {
                PhotoAlbumPickerActivity.this.lambda$null$2$PhotoAlbumPickerActivity(z, i);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$PhotoAlbumPickerActivity(boolean z, int i) {
        sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, z, i);
        finishFragment();
    }

    public /* synthetic */ boolean lambda$createView$7$PhotoAlbumPickerActivity(View view) {
        View view2 = view;
        ChatActivity chatActivity2 = this.chatActivity;
        if (!(chatActivity2 == null || this.maxSelectedPhotos == 1)) {
            chatActivity2.getCurrentChat();
            TLRPC.User currentUser = this.chatActivity.getCurrentUser();
            if (this.chatActivity.getCurrentEncryptedChat() != null) {
                return false;
            }
            if (this.sendPopupLayout == null) {
                this.sendPopupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity());
                this.sendPopupLayout.setAnimationEnabled(false);
                this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                    private Rect popupRect = new Rect();

                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() != 0 || PhotoAlbumPickerActivity.this.sendPopupWindow == null || !PhotoAlbumPickerActivity.this.sendPopupWindow.isShowing()) {
                            return false;
                        }
                        view.getHitRect(this.popupRect);
                        if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            return false;
                        }
                        PhotoAlbumPickerActivity.this.sendPopupWindow.dismiss();
                        return false;
                    }
                });
                this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                    public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                        PhotoAlbumPickerActivity.this.lambda$null$4$PhotoAlbumPickerActivity(keyEvent);
                    }
                });
                this.sendPopupLayout.setShowedFromBotton(false);
                this.itemCells = new ActionBarMenuSubItem[2];
                for (int i = 0; i < 2; i++) {
                    if (i != 1 || !UserObject.isUserSelf(currentUser)) {
                        this.itemCells[i] = new ActionBarMenuSubItem(getParentActivity());
                        if (i == 0) {
                            if (UserObject.isUserSelf(currentUser)) {
                                this.itemCells[i].setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                            } else {
                                this.itemCells[i].setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                            }
                        } else if (i == 1) {
                            this.itemCells[i].setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                        }
                        this.itemCells[i].setMinimumWidth(AndroidUtilities.dp(196.0f));
                        this.sendPopupLayout.addView(this.itemCells[i], LayoutHelper.createFrame(-1, 48.0f, LocaleController.isRTL ? 5 : 3, 0.0f, (float) (i * 48), 0.0f, 0.0f));
                        this.itemCells[i].setOnClickListener(new View.OnClickListener(i) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(View view) {
                                PhotoAlbumPickerActivity.this.lambda$null$6$PhotoAlbumPickerActivity(this.f$1, view);
                            }
                        });
                    }
                }
                this.sendPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
                this.sendPopupWindow.setAnimationEnabled(false);
                this.sendPopupWindow.setAnimationStyle(NUM);
                this.sendPopupWindow.setOutsideTouchable(true);
                this.sendPopupWindow.setClippingEnabled(true);
                this.sendPopupWindow.setInputMethodMode(2);
                this.sendPopupWindow.setSoftInputMode(0);
                this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
            }
            this.sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.sendPopupWindow.setFocusable(true);
            int[] iArr = new int[2];
            view2.getLocationInWindow(iArr);
            this.sendPopupWindow.showAtLocation(view2, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (iArr[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
            this.sendPopupWindow.dimBehind();
            view2.performHapticFeedback(3, 2);
        }
        return false;
    }

    public /* synthetic */ void lambda$null$4$PhotoAlbumPickerActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$null$6$PhotoAlbumPickerActivity(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    PhotoAlbumPickerActivity.this.lambda$null$5$PhotoAlbumPickerActivity(z, i);
                }
            });
        } else if (i == 1) {
            sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, true, 0);
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$null$5$PhotoAlbumPickerActivity(boolean z, int i) {
        sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, z, i);
        finishFragment();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        fixLayout();
    }

    public void onPause() {
        super.onPause();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.albumsDidLoad) {
            if (this.classGuid == objArr[0].intValue()) {
                if (this.selectPhotoType != 0 || !this.allowSearchImages) {
                    this.albumsSorted = objArr[2];
                } else {
                    this.albumsSorted = objArr[1];
                }
                FrameLayout frameLayout = this.progressView;
                if (frameLayout != null) {
                    frameLayout.setVisibility(8);
                }
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView != null && recyclerListView.getEmptyView() == null) {
                    this.listView.setEmptyView(this.emptyView);
                }
                ListAdapter listAdapter2 = this.listAdapter;
                if (listAdapter2 != null) {
                    listAdapter2.notifyDataSetChanged();
                }
                this.loading = false;
            }
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return super.onBackPressed();
        }
        this.commentTextView.hidePopup(true);
        return false;
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        this.maxSelectedPhotos = i;
        this.allowOrder = z;
    }

    public void setAllowSearchImages(boolean z) {
        this.allowSearchImages = z;
    }

    public void setDelegate(PhotoAlbumPickerActivityDelegate photoAlbumPickerActivityDelegate) {
        this.delegate = photoAlbumPickerActivityDelegate;
    }

    /* access modifiers changed from: private */
    public void sendSelectedPhotos(HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, boolean z, int i) {
        if (!hashMap.isEmpty() && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList arrayList2 = new ArrayList();
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                Object obj = hashMap.get(arrayList.get(i2));
                SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                arrayList2.add(sendingMediaInfo);
                ArrayList<TLRPC.InputDocument> arrayList3 = null;
                if (obj instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) obj;
                    if (photoEntry.isVideo) {
                        sendingMediaInfo.path = photoEntry.path;
                        sendingMediaInfo.videoEditedInfo = photoEntry.editedInfo;
                    } else {
                        String str = photoEntry.imagePath;
                        if (str != null) {
                            sendingMediaInfo.path = str;
                        } else {
                            String str2 = photoEntry.path;
                            if (str2 != null) {
                                sendingMediaInfo.path = str2;
                            }
                        }
                    }
                    sendingMediaInfo.isVideo = photoEntry.isVideo;
                    CharSequence charSequence = photoEntry.caption;
                    sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                    sendingMediaInfo.entities = photoEntry.entities;
                    if (!photoEntry.stickers.isEmpty()) {
                        arrayList3 = new ArrayList<>(photoEntry.stickers);
                    }
                    sendingMediaInfo.masks = arrayList3;
                    sendingMediaInfo.ttl = photoEntry.ttl;
                } else if (obj instanceof MediaController.SearchImage) {
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                    String str3 = searchImage.imagePath;
                    if (str3 != null) {
                        sendingMediaInfo.path = str3;
                    } else {
                        sendingMediaInfo.searchImage = searchImage;
                    }
                    CharSequence charSequence2 = searchImage.caption;
                    sendingMediaInfo.caption = charSequence2 != null ? charSequence2.toString() : null;
                    sendingMediaInfo.entities = searchImage.entities;
                    if (!searchImage.stickers.isEmpty()) {
                        arrayList3 = new ArrayList<>(searchImage.stickers);
                    }
                    sendingMediaInfo.masks = arrayList3;
                    sendingMediaInfo.ttl = searchImage.ttl;
                    TLRPC.BotInlineResult botInlineResult = searchImage.inlineResult;
                    if (botInlineResult != null && searchImage.type == 1) {
                        sendingMediaInfo.inlineResult = botInlineResult;
                        sendingMediaInfo.params = searchImage.params;
                    }
                    searchImage.date = (int) (System.currentTimeMillis() / 1000);
                }
            }
            this.delegate.didSelectPhotos(arrayList2, z, i);
        }
    }

    private void fixLayout() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    PhotoAlbumPickerActivity.this.fixLayoutInternal();
                    if (PhotoAlbumPickerActivity.this.listView == null) {
                        return true;
                    }
                    PhotoAlbumPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    private void applyCaption() {
        if (this.commentTextView.length() > 0) {
            Object obj = this.selectedPhotos.get(Integer.valueOf(((Integer) this.selectedPhotosOrder.get(0)).intValue()));
            if (obj instanceof MediaController.PhotoEntry) {
                ((MediaController.PhotoEntry) obj).caption = this.commentTextView.getText().toString();
            } else if (obj instanceof MediaController.SearchImage) {
                ((MediaController.SearchImage) obj).caption = this.commentTextView.getText().toString();
            }
        }
    }

    /* access modifiers changed from: private */
    public void fixLayoutInternal() {
        if (getParentActivity() != null) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            this.columnsCount = 2;
            if (!AndroidUtilities.isTablet() && (rotation == 3 || rotation == 1)) {
                this.columnsCount = 4;
            }
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private boolean showCommentTextView(boolean z) {
        if (z == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        this.frameLayout2.setTag(z ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (z) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        } else {
            this.frameLayout2.setVisibility(4);
            this.writeButtonContainer.setVisibility(4);
        }
        float f = 0.2f;
        float f2 = 1.0f;
        this.writeButtonContainer.setScaleX(z ? 1.0f : 0.2f);
        this.writeButtonContainer.setScaleY(z ? 1.0f : 0.2f);
        float f3 = 0.0f;
        this.writeButtonContainer.setAlpha(z ? 1.0f : 0.0f);
        this.selectedCountView.setScaleX(z ? 1.0f : 0.2f);
        View view = this.selectedCountView;
        if (z) {
            f = 1.0f;
        }
        view.setScaleY(f);
        View view2 = this.selectedCountView;
        if (!z) {
            f2 = 0.0f;
        }
        view2.setAlpha(f2);
        this.frameLayout2.setTranslationY(z ? 0.0f : (float) AndroidUtilities.dp(48.0f));
        View view3 = this.shadow;
        if (!z) {
            f3 = (float) AndroidUtilities.dp(48.0f);
        }
        view3.setTranslationY(f3);
        return true;
    }

    /* access modifiers changed from: private */
    public void updatePhotosButton() {
        if (this.selectedPhotos.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            showCommentTextView(false);
            return;
        }
        this.selectedCountView.invalidate();
        showCommentTextView(true);
    }

    /* access modifiers changed from: private */
    public void openPhotoPicker(MediaController.AlbumEntry albumEntry, int i) {
        if (albumEntry != null) {
            PhotoPickerActivity photoPickerActivity = new PhotoPickerActivity(i, albumEntry, this.selectedPhotos, this.selectedPhotosOrder, this.selectPhotoType, this.allowCaption, this.chatActivity);
            Editable text = this.commentTextView.getText();
            this.caption = text;
            photoPickerActivity.setCaption(text);
            photoPickerActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                public /* synthetic */ void onOpenInPressed() {
                    PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
                }

                public void selectedPhotosChanged() {
                    PhotoAlbumPickerActivity.this.updatePhotosButton();
                }

                public void actionButtonPressed(boolean z, boolean z2, int i) {
                    PhotoAlbumPickerActivity.this.removeSelfFromStack();
                    if (!z) {
                        PhotoAlbumPickerActivity photoAlbumPickerActivity = PhotoAlbumPickerActivity.this;
                        photoAlbumPickerActivity.sendSelectedPhotos(photoAlbumPickerActivity.selectedPhotos, PhotoAlbumPickerActivity.this.selectedPhotosOrder, z2, i);
                    }
                }

                public void onCaptionChanged(CharSequence charSequence) {
                    PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = charSequence);
                }
            });
            photoPickerActivity.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            presentFragment(photoPickerActivity);
            return;
        }
        final HashMap hashMap = new HashMap();
        final ArrayList arrayList = new ArrayList();
        if (this.allowGifs) {
            PhotoPickerSearchActivity photoPickerSearchActivity = new PhotoPickerSearchActivity(hashMap, arrayList, this.selectPhotoType, this.allowCaption, this.chatActivity);
            Editable text2 = this.commentTextView.getText();
            this.caption = text2;
            photoPickerSearchActivity.setCaption(text2);
            photoPickerSearchActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                public /* synthetic */ void onOpenInPressed() {
                    PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
                }

                public void selectedPhotosChanged() {
                }

                public void actionButtonPressed(boolean z, boolean z2, int i) {
                    PhotoAlbumPickerActivity.this.removeSelfFromStack();
                    if (!z) {
                        PhotoAlbumPickerActivity.this.sendSelectedPhotos(hashMap, arrayList, z2, i);
                    }
                }

                public void onCaptionChanged(CharSequence charSequence) {
                    PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = charSequence);
                }
            });
            photoPickerSearchActivity.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            presentFragment(photoPickerSearchActivity);
            return;
        }
        PhotoPickerActivity photoPickerActivity2 = new PhotoPickerActivity(0, albumEntry, hashMap, arrayList, this.selectPhotoType, this.allowCaption, this.chatActivity);
        Editable text3 = this.commentTextView.getText();
        this.caption = text3;
        photoPickerActivity2.setCaption(text3);
        photoPickerActivity2.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
            public /* synthetic */ void onOpenInPressed() {
                PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
            }

            public void selectedPhotosChanged() {
            }

            public void actionButtonPressed(boolean z, boolean z2, int i) {
                PhotoAlbumPickerActivity.this.removeSelfFromStack();
                if (!z) {
                    PhotoAlbumPickerActivity.this.sendSelectedPhotos(hashMap, arrayList, z2, i);
                }
            }

            public void onCaptionChanged(CharSequence charSequence) {
                PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = charSequence);
            }
        });
        photoPickerActivity2.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
        presentFragment(photoPickerActivity2);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
                return (int) Math.ceil((double) (((float) PhotoAlbumPickerActivity.this.albumsSorted.size()) / ((float) PhotoAlbumPickerActivity.this.columnsCount)));
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            PhotoPickerAlbumsCell photoPickerAlbumsCell = new PhotoPickerAlbumsCell(this.mContext);
            photoPickerAlbumsCell.setDelegate(new PhotoPickerAlbumsCell.PhotoPickerAlbumsCellDelegate() {
                public final void didSelectAlbum(MediaController.AlbumEntry albumEntry) {
                    PhotoAlbumPickerActivity.ListAdapter.this.lambda$onCreateViewHolder$0$PhotoAlbumPickerActivity$ListAdapter(albumEntry);
                }
            });
            return new RecyclerListView.Holder(photoPickerAlbumsCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$PhotoAlbumPickerActivity$ListAdapter(MediaController.AlbumEntry albumEntry) {
            PhotoAlbumPickerActivity.this.openPhotoPicker(albumEntry, 0);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            PhotoPickerAlbumsCell photoPickerAlbumsCell = (PhotoPickerAlbumsCell) viewHolder.itemView;
            photoPickerAlbumsCell.setAlbumsCount(PhotoAlbumPickerActivity.this.columnsCount);
            for (int i2 = 0; i2 < PhotoAlbumPickerActivity.this.columnsCount; i2++) {
                int access$1500 = (PhotoAlbumPickerActivity.this.columnsCount * i) + i2;
                if (access$1500 < PhotoAlbumPickerActivity.this.albumsSorted.size()) {
                    photoPickerAlbumsCell.setAlbum(i2, (MediaController.AlbumEntry) PhotoAlbumPickerActivity.this.albumsSorted.get(access$1500));
                } else {
                    photoPickerAlbumsCell.setAlbum(i2, (MediaController.AlbumEntry) null);
                }
            }
            photoPickerAlbumsCell.requestLayout();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{Theme.chat_attachEmptyDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_attachEmptyImage"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_attachPhotoBackground")};
    }
}

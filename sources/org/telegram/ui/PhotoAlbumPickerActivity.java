package org.telegram.ui;

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
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
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
import org.telegram.messenger.SharedConfig;
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
import org.telegram.ui.PhotoPickerActivity;

public class PhotoAlbumPickerActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static int SELECT_TYPE_ALL = 0;
    public static int SELECT_TYPE_AVATAR = 1;
    public static int SELECT_TYPE_AVATAR_VIDEO = 3;
    public static int SELECT_TYPE_QR = 10;
    public static int SELECT_TYPE_WALLPAPER = 2;
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

    public PhotoAlbumPickerActivity(int selectPhotoType2, boolean allowGifs2, boolean allowCaption2, ChatActivity chatActivity2) {
        this.chatActivity = chatActivity2;
        this.selectPhotoType = selectPhotoType2;
        this.allowGifs = allowGifs2;
        this.allowCaption = allowCaption2;
    }

    public boolean onFragmentCreate() {
        int i = this.selectPhotoType;
        if (i == SELECT_TYPE_AVATAR || i == SELECT_TYPE_WALLPAPER || i == SELECT_TYPE_QR || !this.allowSearchImages) {
            this.albumsSorted = MediaController.allPhotoAlbums;
        } else {
            this.albumsSorted = MediaController.allMediaAlbums;
        }
        this.loading = this.albumsSorted == null;
        MediaController.loadGalleryPhotosAlbums(this.classGuid);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.albumsDidLoad);
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
            public void onItemClick(int id) {
                if (id == -1) {
                    PhotoAlbumPickerActivity.this.finishFragment();
                } else if (id == 1) {
                    if (PhotoAlbumPickerActivity.this.delegate != null) {
                        PhotoAlbumPickerActivity.this.finishFragment(false);
                        PhotoAlbumPickerActivity.this.delegate.startPhotoSelectActivity();
                    }
                } else if (id == 2) {
                    PhotoAlbumPickerActivity.this.openPhotoPicker((MediaController.AlbumEntry) null, 0);
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.allowSearchImages) {
            menu.addItem(2, NUM).setContentDescription(LocaleController.getString("Search", NUM));
        }
        ActionBarMenuItem menuItem = menu.addItem(0, NUM);
        menuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        menuItem.addSubItem(1, NUM, (CharSequence) LocaleController.getString("OpenInExternalApp", NUM));
        AnonymousClass2 r7 = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;
            private int lastNotifyWidth;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                if ((SharedConfig.smoothKeyboard ? 0 : measureKeyboardHeight()) > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    PhotoAlbumPickerActivity.this.commentTextView.hideEmojiView();
                    this.ignoreLayout = false;
                } else if (!AndroidUtilities.isInMultiwindow) {
                    heightSize -= PhotoAlbumPickerActivity.this.commentTextView.getEmojiPadding();
                    heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(heightSize, NUM);
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8)) {
                        if (PhotoAlbumPickerActivity.this.commentTextView == null || !PhotoAlbumPickerActivity.this.commentTextView.isPopupView(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec((heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                if (this.lastNotifyWidth != r - l) {
                    this.lastNotifyWidth = r - l;
                    if (PhotoAlbumPickerActivity.this.sendPopupWindow != null && PhotoAlbumPickerActivity.this.sendPopupWindow.isShowing()) {
                        PhotoAlbumPickerActivity.this.sendPopupWindow.dismiss();
                    }
                }
                int count = getChildCount();
                int paddingBottom = 0;
                int keyboardSize = SharedConfig.smoothKeyboard ? 0 : measureKeyboardHeight();
                if (keyboardSize <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                    paddingBottom = PhotoAlbumPickerActivity.this.commentTextView.getEmojiPadding();
                }
                setBottomClip(paddingBottom);
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        switch (gravity & 7 & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (((r - l) - width) - lp.rightMargin) - getPaddingRight();
                                break;
                            default:
                                childLeft = lp.leftMargin + getPaddingLeft();
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin + getPaddingTop();
                                break;
                            case 80:
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (PhotoAlbumPickerActivity.this.commentTextView != null && PhotoAlbumPickerActivity.this.commentTextView.isPopupView(child)) {
                            if (AndroidUtilities.isTablet()) {
                                childTop = getMeasuredHeight() - child.getMeasuredHeight();
                            } else {
                                childTop = (getMeasuredHeight() + keyboardSize) - child.getMeasuredHeight();
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.sizeNotifierFrameLayout = r7;
        r7.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.fragmentView = this.sizeNotifierFrameLayout;
        this.actionBar.setTitle(LocaleController.getString("Gallery", NUM));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(54.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.listView.setDrawingCacheEnabled(false);
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        recyclerListView2.setAdapter(listAdapter2);
        this.listView.setGlowColor(Theme.getColor("dialogBackground"));
        TextView textView = new TextView(context2);
        this.emptyView = textView;
        textView.setTextColor(-8355712);
        this.emptyView.setTextSize(1, 20.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.emptyView.setText(LocaleController.getString("NoPhotos", NUM));
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.emptyView.setOnTouchListener(PhotoAlbumPickerActivity$$ExternalSyntheticLambda3.INSTANCE);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.progressView = frameLayout;
        frameLayout.setVisibility(8);
        this.sizeNotifierFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        RadialProgressView progressBar = new RadialProgressView(context2);
        progressBar.setProgressColor(-11371101);
        this.progressView.addView(progressBar, LayoutHelper.createFrame(-2, -2, 17));
        View view = new View(context2);
        this.shadow = view;
        view.setBackgroundResource(NUM);
        this.shadow.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        FrameLayout frameLayout3 = new FrameLayout(context2);
        this.frameLayout2 = frameLayout3;
        frameLayout3.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        this.frameLayout2.setOnTouchListener(PhotoAlbumPickerActivity$$ExternalSyntheticLambda4.INSTANCE);
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
        AnonymousClass3 r13 = new FrameLayout(context2) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                info.setText(LocaleController.formatPluralString("AccDescrSendPhotos", PhotoAlbumPickerActivity.this.selectedPhotos.size(), new Object[0]));
                info.setClassName(Button.class.getName());
                info.setLongClickable(true);
                info.setClickable(true);
            }
        };
        this.writeButtonContainer = r13;
        r13.setFocusable(true);
        this.writeButtonContainer.setFocusableInTouchMode(true);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 12.0f, 10.0f));
        this.writeButton = new ImageView(context2);
        int dp = AndroidUtilities.dp(56.0f);
        String str = "dialogFloatingButton";
        int color = Theme.getColor(str);
        if (Build.VERSION.SDK_INT >= 21) {
            str = "dialogFloatingButtonPressed";
        }
        this.writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(dp, color, Theme.getColor(str));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, this.writeButtonDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            this.writeButtonDrawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(this.writeButtonDrawable);
        this.writeButton.setImageResource(NUM);
        this.writeButton.setImportantForAccessibility(2);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
        this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
        if (Build.VERSION.SDK_INT >= 21) {
            this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, Build.VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        this.writeButton.setOnClickListener(new PhotoAlbumPickerActivity$$ExternalSyntheticLambda0(this));
        this.writeButton.setOnLongClickListener(new PhotoAlbumPickerActivity$$ExternalSyntheticLambda2(this));
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        AnonymousClass6 r6 = new View(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                String text = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, PhotoAlbumPickerActivity.this.selectedPhotosOrder.size()))});
                int textSize = (int) Math.ceil((double) PhotoAlbumPickerActivity.this.textPaint.measureText(text));
                int size = Math.max(AndroidUtilities.dp(16.0f) + textSize, AndroidUtilities.dp(24.0f));
                int cx = getMeasuredWidth() / 2;
                int measuredHeight = getMeasuredHeight() / 2;
                PhotoAlbumPickerActivity.this.textPaint.setColor(Theme.getColor("dialogRoundCheckBoxCheck"));
                PhotoAlbumPickerActivity.this.paint.setColor(Theme.getColor("dialogBackground"));
                PhotoAlbumPickerActivity.this.rect.set((float) (cx - (size / 2)), 0.0f, (float) ((size / 2) + cx), (float) getMeasuredHeight());
                canvas.drawRoundRect(PhotoAlbumPickerActivity.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), PhotoAlbumPickerActivity.this.paint);
                PhotoAlbumPickerActivity.this.paint.setColor(Theme.getColor("dialogRoundCheckBox"));
                PhotoAlbumPickerActivity.this.rect.set((float) ((cx - (size / 2)) + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (((size / 2) + cx) - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                canvas.drawRoundRect(PhotoAlbumPickerActivity.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), PhotoAlbumPickerActivity.this.paint);
                canvas.drawText(text, (float) (cx - (textSize / 2)), (float) AndroidUtilities.dp(16.2f), PhotoAlbumPickerActivity.this.textPaint);
            }
        };
        this.selectedCountView = r6;
        r6.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        this.sizeNotifierFrameLayout.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -2.0f, 9.0f));
        if (this.selectPhotoType != SELECT_TYPE_ALL) {
            this.commentTextView.setVisibility(8);
        }
        if (!this.loading || ((arrayList = this.albumsSorted) != null && !arrayList.isEmpty())) {
            this.progressView.setVisibility(8);
            this.listView.setEmptyView(this.emptyView);
        } else {
            this.progressView.setVisibility(0);
            this.listView.setEmptyView((View) null);
        }
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    static /* synthetic */ boolean lambda$createView$1(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-PhotoAlbumPickerActivity  reason: not valid java name */
    public /* synthetic */ void m4210lambda$createView$3$orgtelegramuiPhotoAlbumPickerActivity(View v) {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 == null || !chatActivity2.isInScheduleMode()) {
            sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, true, 0);
            finishFragment();
            return;
        }
        AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new PhotoAlbumPickerActivity$$ExternalSyntheticLambda6(this));
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PhotoAlbumPickerActivity  reason: not valid java name */
    public /* synthetic */ void m4209lambda$createView$2$orgtelegramuiPhotoAlbumPickerActivity(boolean notify, int scheduleDate) {
        sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, notify, scheduleDate);
        finishFragment();
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-PhotoAlbumPickerActivity  reason: not valid java name */
    public /* synthetic */ boolean m4214lambda$createView$7$orgtelegramuiPhotoAlbumPickerActivity(View view) {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 == null || this.maxSelectedPhotos == 1) {
            return false;
        }
        TLRPC.Chat currentChat = chatActivity2.getCurrentChat();
        TLRPC.User user = this.chatActivity.getCurrentUser();
        if (this.sendPopupLayout == null) {
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity());
            this.sendPopupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setAnimationEnabled(false);
            this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                private Rect popupRect = new Rect();

                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() != 0 || PhotoAlbumPickerActivity.this.sendPopupWindow == null || !PhotoAlbumPickerActivity.this.sendPopupWindow.isShowing()) {
                        return false;
                    }
                    v.getHitRect(this.popupRect);
                    if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                        return false;
                    }
                    PhotoAlbumPickerActivity.this.sendPopupWindow.dismiss();
                    return false;
                }
            });
            this.sendPopupLayout.setDispatchKeyEventListener(new PhotoAlbumPickerActivity$$ExternalSyntheticLambda5(this));
            this.sendPopupLayout.setShownFromBottom(false);
            this.itemCells = new ActionBarMenuSubItem[2];
            int a = 0;
            while (a < 2) {
                if ((a != 0 || this.chatActivity.canScheduleMessage()) && (a != 1 || !UserObject.isUserSelf(user))) {
                    int num = a;
                    this.itemCells[a] = new ActionBarMenuSubItem(getParentActivity(), a == 0, a == 1);
                    if (num != 0) {
                        this.itemCells[a].setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                    } else if (UserObject.isUserSelf(user)) {
                        this.itemCells[a].setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                    } else {
                        this.itemCells[a].setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                    }
                    this.itemCells[a].setMinimumWidth(AndroidUtilities.dp(196.0f));
                    this.sendPopupLayout.addView(this.itemCells[a], LayoutHelper.createLinear(-1, 48));
                    this.itemCells[a].setOnClickListener(new PhotoAlbumPickerActivity$$ExternalSyntheticLambda1(this, num));
                }
                a++;
            }
            this.sendPopupLayout.setupRadialSelectors(Theme.getColor("dialogButtonSelector"));
            ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
            this.sendPopupWindow = actionBarPopupWindow;
            actionBarPopupWindow.setAnimationEnabled(false);
            this.sendPopupWindow.setAnimationStyle(NUM);
            this.sendPopupWindow.setOutsideTouchable(true);
            this.sendPopupWindow.setClippingEnabled(true);
            this.sendPopupWindow.setInputMethodMode(2);
            this.sendPopupWindow.setSoftInputMode(0);
            this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        }
        this.sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        this.sendPopupWindow.showAtLocation(view, 51, ((location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (location[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
        this.sendPopupWindow.dimBehind();
        view.performHapticFeedback(3, 2);
        return false;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-PhotoAlbumPickerActivity  reason: not valid java name */
    public /* synthetic */ void m4211lambda$createView$4$orgtelegramuiPhotoAlbumPickerActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-PhotoAlbumPickerActivity  reason: not valid java name */
    public /* synthetic */ void m4213lambda$createView$6$orgtelegramuiPhotoAlbumPickerActivity(int num, View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (num == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new PhotoAlbumPickerActivity$$ExternalSyntheticLambda7(this));
            return;
        }
        sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, true, 0);
        finishFragment();
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-PhotoAlbumPickerActivity  reason: not valid java name */
    public /* synthetic */ void m4212lambda$createView$5$orgtelegramuiPhotoAlbumPickerActivity(boolean notify, int scheduleDate) {
        sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, notify, scheduleDate);
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

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.albumsDidLoad) {
            if (this.classGuid == args[0].intValue()) {
                int i = this.selectPhotoType;
                if (i == SELECT_TYPE_AVATAR || i == SELECT_TYPE_WALLPAPER || i == SELECT_TYPE_QR || !this.allowSearchImages) {
                    this.albumsSorted = args[2];
                } else {
                    this.albumsSorted = args[1];
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
        } else if (id == NotificationCenter.closeChats) {
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

    public void setMaxSelectedPhotos(int value, boolean order) {
        this.maxSelectedPhotos = value;
        this.allowOrder = order;
    }

    public void setAllowSearchImages(boolean value) {
        this.allowSearchImages = value;
    }

    public void setDelegate(PhotoAlbumPickerActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    /* access modifiers changed from: private */
    public void sendSelectedPhotos(HashMap<Object, Object> photos, ArrayList<Object> order, boolean notify, int scheduleDate) {
        if (!photos.isEmpty() && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList<SendMessagesHelper.SendingMediaInfo> media = new ArrayList<>();
            for (int a = 0; a < order.size(); a++) {
                Object object = photos.get(order.get(a));
                SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                media.add(info);
                String str = null;
                if (object instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                    if (photoEntry.imagePath != null) {
                        info.path = photoEntry.imagePath;
                    } else {
                        info.path = photoEntry.path;
                    }
                    info.thumbPath = photoEntry.thumbPath;
                    info.videoEditedInfo = photoEntry.editedInfo;
                    info.isVideo = photoEntry.isVideo;
                    if (photoEntry.caption != null) {
                        str = photoEntry.caption.toString();
                    }
                    info.caption = str;
                    info.entities = photoEntry.entities;
                    info.masks = photoEntry.stickers;
                    info.ttl = photoEntry.ttl;
                } else if (object instanceof MediaController.SearchImage) {
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) object;
                    if (searchImage.imagePath != null) {
                        info.path = searchImage.imagePath;
                    } else {
                        info.searchImage = searchImage;
                    }
                    info.thumbPath = searchImage.thumbPath;
                    info.videoEditedInfo = searchImage.editedInfo;
                    if (searchImage.caption != null) {
                        str = searchImage.caption.toString();
                    }
                    info.caption = str;
                    info.entities = searchImage.entities;
                    info.masks = searchImage.stickers;
                    info.ttl = searchImage.ttl;
                    if (searchImage.inlineResult != null && searchImage.type == 1) {
                        info.inlineResult = searchImage.inlineResult;
                        info.params = searchImage.params;
                    }
                    searchImage.date = (int) (System.currentTimeMillis() / 1000);
                }
            }
            this.delegate.didSelectPhotos(media, notify, scheduleDate);
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
            Object entry = this.selectedPhotos.get(Integer.valueOf(((Integer) this.selectedPhotosOrder.get(0)).intValue()));
            if (entry instanceof MediaController.PhotoEntry) {
                ((MediaController.PhotoEntry) entry).caption = this.commentTextView.getText().toString();
            } else if (entry instanceof MediaController.SearchImage) {
                ((MediaController.SearchImage) entry).caption = this.commentTextView.getText().toString();
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

    private boolean showCommentTextView(boolean show) {
        if (show == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        this.frameLayout2.setTag(show ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (show) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        } else {
            this.frameLayout2.setVisibility(4);
            this.writeButtonContainer.setVisibility(4);
        }
        float f = 0.2f;
        float f2 = 1.0f;
        this.writeButtonContainer.setScaleX(show ? 1.0f : 0.2f);
        this.writeButtonContainer.setScaleY(show ? 1.0f : 0.2f);
        float f3 = 0.0f;
        this.writeButtonContainer.setAlpha(show ? 1.0f : 0.0f);
        this.selectedCountView.setScaleX(show ? 1.0f : 0.2f);
        View view = this.selectedCountView;
        if (show) {
            f = 1.0f;
        }
        view.setScaleY(f);
        View view2 = this.selectedCountView;
        if (!show) {
            f2 = 0.0f;
        }
        view2.setAlpha(f2);
        this.frameLayout2.setTranslationY(show ? 0.0f : (float) AndroidUtilities.dp(48.0f));
        View view3 = this.shadow;
        if (!show) {
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
    public void openPhotoPicker(MediaController.AlbumEntry albumEntry, int type) {
        if (albumEntry != null) {
            PhotoPickerActivity fragment = new PhotoPickerActivity(type, albumEntry, this.selectedPhotos, this.selectedPhotosOrder, this.selectPhotoType, this.allowCaption, this.chatActivity, false);
            Editable text = this.commentTextView.getText();
            this.caption = text;
            fragment.setCaption(text);
            fragment.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                public /* synthetic */ void onOpenInPressed() {
                    PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
                }

                public void selectedPhotosChanged() {
                    PhotoAlbumPickerActivity.this.updatePhotosButton();
                }

                public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate) {
                    PhotoAlbumPickerActivity.this.removeSelfFromStack();
                    if (!canceled) {
                        PhotoAlbumPickerActivity photoAlbumPickerActivity = PhotoAlbumPickerActivity.this;
                        photoAlbumPickerActivity.sendSelectedPhotos(photoAlbumPickerActivity.selectedPhotos, PhotoAlbumPickerActivity.this.selectedPhotosOrder, notify, scheduleDate);
                    }
                }

                public void onCaptionChanged(CharSequence text) {
                    PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = text);
                }
            });
            fragment.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            presentFragment(fragment);
            return;
        }
        final HashMap<Object, Object> photos = new HashMap<>();
        final ArrayList<Object> order = new ArrayList<>();
        if (this.allowGifs) {
            PhotoPickerSearchActivity fragment2 = new PhotoPickerSearchActivity(photos, order, this.selectPhotoType, this.allowCaption, this.chatActivity);
            Editable text2 = this.commentTextView.getText();
            this.caption = text2;
            fragment2.setCaption(text2);
            fragment2.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                public /* synthetic */ void onOpenInPressed() {
                    PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
                }

                public void selectedPhotosChanged() {
                }

                public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate) {
                    PhotoAlbumPickerActivity.this.removeSelfFromStack();
                    if (!canceled) {
                        PhotoAlbumPickerActivity.this.sendSelectedPhotos(photos, order, notify, scheduleDate);
                    }
                }

                public void onCaptionChanged(CharSequence text) {
                    PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = text);
                }
            });
            fragment2.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            presentFragment(fragment2);
            return;
        }
        PhotoPickerActivity fragment3 = new PhotoPickerActivity(0, albumEntry, photos, order, this.selectPhotoType, this.allowCaption, this.chatActivity, false);
        Editable text3 = this.commentTextView.getText();
        this.caption = text3;
        fragment3.setCaption(text3);
        fragment3.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
            public /* synthetic */ void onOpenInPressed() {
                PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
            }

            public void selectedPhotosChanged() {
            }

            public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate) {
                PhotoAlbumPickerActivity.this.removeSelfFromStack();
                if (!canceled) {
                    PhotoAlbumPickerActivity.this.sendSelectedPhotos(photos, order, notify, scheduleDate);
                }
            }

            public void onCaptionChanged(CharSequence text) {
                PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = text);
            }
        });
        fragment3.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
        presentFragment(fragment3);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
                return (int) Math.ceil((double) (((float) PhotoAlbumPickerActivity.this.albumsSorted.size()) / ((float) PhotoAlbumPickerActivity.this.columnsCount)));
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PhotoPickerAlbumsCell cell = new PhotoPickerAlbumsCell(this.mContext);
            cell.setDelegate(new PhotoAlbumPickerActivity$ListAdapter$$ExternalSyntheticLambda0(this));
            return new RecyclerListView.Holder(cell);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-PhotoAlbumPickerActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m4215x4evar_c(MediaController.AlbumEntry albumEntry) {
            PhotoAlbumPickerActivity.this.openPhotoPicker(albumEntry, 0);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PhotoPickerAlbumsCell photoPickerAlbumsCell = (PhotoPickerAlbumsCell) holder.itemView;
            photoPickerAlbumsCell.setAlbumsCount(PhotoAlbumPickerActivity.this.columnsCount);
            for (int a = 0; a < PhotoAlbumPickerActivity.this.columnsCount; a++) {
                int index = (PhotoAlbumPickerActivity.this.columnsCount * position) + a;
                if (index < PhotoAlbumPickerActivity.this.albumsSorted.size()) {
                    photoPickerAlbumsCell.setAlbum(a, (MediaController.AlbumEntry) PhotoAlbumPickerActivity.this.albumsSorted.get(index));
                } else {
                    photoPickerAlbumsCell.setAlbum(a, (MediaController.AlbumEntry) null);
                }
            }
            photoPickerAlbumsCell.requestLayout();
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{Theme.chat_attachEmptyDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_attachEmptyImage"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_attachPhotoBackground"));
        return themeDescriptions;
    }
}

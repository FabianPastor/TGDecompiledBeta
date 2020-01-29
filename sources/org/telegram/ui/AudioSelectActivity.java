package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.AudioSelectActivity;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class AudioSelectActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public ArrayList<MediaController.AudioEntry> audioEntries = new ArrayList<>();
    private ChatActivity chatActivity;
    /* access modifiers changed from: private */
    public EditTextEmoji commentTextView;
    private AudioSelectActivityDelegate delegate;
    private ImageView emptyImageView;
    /* access modifiers changed from: private */
    public TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    private LinearLayout emptyView;
    /* access modifiers changed from: private */
    public FrameLayout frameLayout2;
    private ActionBarMenuSubItem[] itemCells;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private boolean loadingAudio;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    /* access modifiers changed from: private */
    public MessageObject playingAudio;
    private EmptyTextProgressView progressView;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public LongSparseArray<MediaController.AudioEntry> selectedAudios = new LongSparseArray<>();
    private View selectedCountView;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    private boolean sendPressed;
    private View shadow;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    /* access modifiers changed from: private */
    public TextPaint textPaint = new TextPaint(1);
    private ImageView writeButton;
    /* access modifiers changed from: private */
    public FrameLayout writeButtonContainer;

    public interface AudioSelectActivityDelegate {
        void didSelectAudio(ArrayList<MessageObject> arrayList, CharSequence charSequence, boolean z, int i);
    }

    static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ boolean lambda$createView$3(View view, MotionEvent motionEvent) {
        return true;
    }

    public AudioSelectActivity(ChatActivity chatActivity2) {
        this.chatActivity = chatActivity2;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        loadAudio();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        if (this.playingAudio != null && MediaController.getInstance().isPlayingMessage(this.playingAudio)) {
            MediaController.getInstance().cleanupPlayer(true, true);
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.searchWas = false;
        this.searching = false;
        this.actionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.actionBar.setTitleColor(Theme.getColor("dialogTextBlack"));
        this.actionBar.setItemsColor(Theme.getColor("dialogTextBlack"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AttachMusic", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    AudioSelectActivity.this.finishFragment();
                }
            }
        });
        this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = AudioSelectActivity.this.searching = true;
            }

            public void onSearchCollapse() {
                boolean unused = AudioSelectActivity.this.searchWas = false;
                boolean unused2 = AudioSelectActivity.this.searching = false;
                if (AudioSelectActivity.this.listView.getAdapter() != AudioSelectActivity.this.listAdapter) {
                    AudioSelectActivity.this.listView.setAdapter(AudioSelectActivity.this.listAdapter);
                }
                AudioSelectActivity.this.updateEmptyView();
                AudioSelectActivity.this.searchAdapter.search((String) null);
            }

            public void onTextChanged(EditText editText) {
                AudioSelectActivity.this.searchAdapter.search(editText.getText().toString());
            }
        });
        this.searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(Theme.getColor("dialogTextBlack"));
        searchField.setCursorColor(Theme.getColor("dialogTextBlack"));
        searchField.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        this.sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;
            private int lastItemSize;
            private int lastNotifyWidth;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                if (getKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                    if (!AndroidUtilities.isInMultiwindow && AudioSelectActivity.this.commentTextView != null && AudioSelectActivity.this.frameLayout2.getParent() == this) {
                        size2 -= AudioSelectActivity.this.commentTextView.getEmojiPadding();
                        i2 = View.MeasureSpec.makeMeasureSpec(size2, NUM);
                    }
                } else if (AudioSelectActivity.this.commentTextView != null) {
                    this.ignoreLayout = true;
                    AudioSelectActivity.this.commentTextView.hideEmojiView();
                    this.ignoreLayout = false;
                }
                int childCount = getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8)) {
                        if (AudioSelectActivity.this.commentTextView == null || !AudioSelectActivity.this.commentTextView.isPopupView(childAt)) {
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
            /* JADX WARNING: Removed duplicated region for block: B:36:0x00b3  */
            /* JADX WARNING: Removed duplicated region for block: B:43:0x00cd  */
            /* JADX WARNING: Removed duplicated region for block: B:51:0x00f4  */
            /* JADX WARNING: Removed duplicated region for block: B:52:0x00fd  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onLayout(boolean r9, int r10, int r11, int r12, int r13) {
                /*
                    r8 = this;
                    int r9 = r8.lastNotifyWidth
                    int r12 = r12 - r10
                    if (r9 == r12) goto L_0x0024
                    r8.lastNotifyWidth = r12
                    org.telegram.ui.AudioSelectActivity r9 = org.telegram.ui.AudioSelectActivity.this
                    org.telegram.ui.ActionBar.ActionBarPopupWindow r9 = r9.sendPopupWindow
                    if (r9 == 0) goto L_0x0024
                    org.telegram.ui.AudioSelectActivity r9 = org.telegram.ui.AudioSelectActivity.this
                    org.telegram.ui.ActionBar.ActionBarPopupWindow r9 = r9.sendPopupWindow
                    boolean r9 = r9.isShowing()
                    if (r9 == 0) goto L_0x0024
                    org.telegram.ui.AudioSelectActivity r9 = org.telegram.ui.AudioSelectActivity.this
                    org.telegram.ui.ActionBar.ActionBarPopupWindow r9 = r9.sendPopupWindow
                    r9.dismiss()
                L_0x0024:
                    int r9 = r8.getChildCount()
                    org.telegram.ui.AudioSelectActivity r10 = org.telegram.ui.AudioSelectActivity.this
                    org.telegram.ui.Components.EditTextEmoji r10 = r10.commentTextView
                    r0 = 0
                    if (r10 == 0) goto L_0x005e
                    org.telegram.ui.AudioSelectActivity r10 = org.telegram.ui.AudioSelectActivity.this
                    android.widget.FrameLayout r10 = r10.frameLayout2
                    android.view.ViewParent r10 = r10.getParent()
                    if (r10 != r8) goto L_0x005e
                    int r10 = r8.getKeyboardHeight()
                    r1 = 1101004800(0x41a00000, float:20.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    if (r10 > r1) goto L_0x005e
                    boolean r10 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r10 != 0) goto L_0x005e
                    boolean r10 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r10 != 0) goto L_0x005e
                    org.telegram.ui.AudioSelectActivity r10 = org.telegram.ui.AudioSelectActivity.this
                    org.telegram.ui.Components.EditTextEmoji r10 = r10.commentTextView
                    int r10 = r10.getEmojiPadding()
                    goto L_0x005f
                L_0x005e:
                    r10 = 0
                L_0x005f:
                    r8.setBottomClip(r10)
                L_0x0062:
                    if (r0 >= r9) goto L_0x0114
                    android.view.View r1 = r8.getChildAt(r0)
                    int r2 = r1.getVisibility()
                    r3 = 8
                    if (r2 != r3) goto L_0x0072
                    goto L_0x0110
                L_0x0072:
                    android.view.ViewGroup$LayoutParams r2 = r1.getLayoutParams()
                    android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
                    int r3 = r1.getMeasuredWidth()
                    int r4 = r1.getMeasuredHeight()
                    int r5 = r2.gravity
                    r6 = -1
                    if (r5 != r6) goto L_0x0087
                    r5 = 51
                L_0x0087:
                    r6 = r5 & 7
                    r5 = r5 & 112(0x70, float:1.57E-43)
                    r6 = r6 & 7
                    r7 = 1
                    if (r6 == r7) goto L_0x00a5
                    r7 = 5
                    if (r6 == r7) goto L_0x009b
                    int r6 = r2.leftMargin
                    int r7 = r8.getPaddingLeft()
                    int r6 = r6 + r7
                    goto L_0x00af
                L_0x009b:
                    int r6 = r12 - r3
                    int r7 = r2.rightMargin
                    int r6 = r6 - r7
                    int r7 = r8.getPaddingRight()
                    goto L_0x00ae
                L_0x00a5:
                    int r6 = r12 - r3
                    int r6 = r6 / 2
                    int r7 = r2.leftMargin
                    int r6 = r6 + r7
                    int r7 = r2.rightMargin
                L_0x00ae:
                    int r6 = r6 - r7
                L_0x00af:
                    r7 = 16
                    if (r5 == r7) goto L_0x00cd
                    r7 = 48
                    if (r5 == r7) goto L_0x00c5
                    r7 = 80
                    if (r5 == r7) goto L_0x00be
                    int r2 = r2.topMargin
                    goto L_0x00da
                L_0x00be:
                    int r5 = r13 - r10
                    int r5 = r5 - r11
                    int r5 = r5 - r4
                    int r2 = r2.bottomMargin
                    goto L_0x00d8
                L_0x00c5:
                    int r2 = r2.topMargin
                    int r5 = r8.getPaddingTop()
                    int r2 = r2 + r5
                    goto L_0x00da
                L_0x00cd:
                    int r5 = r13 - r10
                    int r5 = r5 - r11
                    int r5 = r5 - r4
                    int r5 = r5 / 2
                    int r7 = r2.topMargin
                    int r5 = r5 + r7
                    int r2 = r2.bottomMargin
                L_0x00d8:
                    int r2 = r5 - r2
                L_0x00da:
                    org.telegram.ui.AudioSelectActivity r5 = org.telegram.ui.AudioSelectActivity.this
                    org.telegram.ui.Components.EditTextEmoji r5 = r5.commentTextView
                    if (r5 == 0) goto L_0x010b
                    org.telegram.ui.AudioSelectActivity r5 = org.telegram.ui.AudioSelectActivity.this
                    org.telegram.ui.Components.EditTextEmoji r5 = r5.commentTextView
                    boolean r5 = r5.isPopupView(r1)
                    if (r5 == 0) goto L_0x010b
                    boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r2 == 0) goto L_0x00fd
                    int r2 = r8.getMeasuredHeight()
                    int r5 = r1.getMeasuredHeight()
                    goto L_0x010a
                L_0x00fd:
                    int r2 = r8.getMeasuredHeight()
                    int r5 = r8.getKeyboardHeight()
                    int r2 = r2 + r5
                    int r5 = r1.getMeasuredHeight()
                L_0x010a:
                    int r2 = r2 - r5
                L_0x010b:
                    int r3 = r3 + r6
                    int r4 = r4 + r2
                    r1.layout(r6, r2, r3, r4)
                L_0x0110:
                    int r0 = r0 + 1
                    goto L_0x0062
                L_0x0114:
                    r8.notifyHeightChanged()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.AudioSelectActivity.AnonymousClass3.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.sizeNotifierFrameLayout.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.fragmentView = this.sizeNotifierFrameLayout;
        this.progressView = new EmptyTextProgressView(context2);
        this.progressView.showProgress();
        this.sizeNotifierFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView = new LinearLayout(context2);
        this.emptyView.setOrientation(1);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener($$Lambda$AudioSelectActivity$TsXMP3OMn_MrWzh_GvC6AvDPzsY.INSTANCE);
        this.emptyImageView = new ImageView(context2);
        this.emptyImageView.setImageResource(NUM);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogEmptyImage"), PorterDuff.Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        this.emptyTitleTextView = new TextView(context2);
        this.emptyTitleTextView.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        this.emptySubtitleTextView = new TextView(context2);
        this.emptySubtitleTextView.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        this.listView = new RecyclerListView(context2);
        this.listView.setClipToPadding(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        recyclerListView.setAdapter(listAdapter2);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.searchAdapter = new SearchAdapter(context2);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                AudioSelectActivity.this.lambda$createView$1$AudioSelectActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return AudioSelectActivity.this.lambda$createView$2$AudioSelectActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && AudioSelectActivity.this.searching && AudioSelectActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(AudioSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        this.shadow = new View(context2);
        this.shadow.setBackgroundResource(NUM);
        this.shadow.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.frameLayout2 = new FrameLayout(context2);
        this.frameLayout2.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        this.frameLayout2.setOnTouchListener($$Lambda$AudioSelectActivity$70c_2QVPUHQ3blJpxsqysqHDZJ0.INSTANCE);
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.commentTextView = new EditTextEmoji(context2, this.sizeNotifierFrameLayout, (BaseFragment) null, 1);
        this.commentTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
        this.commentTextView.setHint(LocaleController.getString("AddCaption", NUM));
        this.commentTextView.onResume();
        EditTextBoldCursor editText = this.commentTextView.getEditText();
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
        this.writeButtonContainer = new FrameLayout(context2);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.writeButtonContainer.setContentDescription(LocaleController.getString("Send", NUM));
        this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 12.0f, 10.0f));
        this.writeButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("dialogFloatingButton"), Theme.getColor(Build.VERSION.SDK_INT >= 21 ? "dialogFloatingButtonPressed" : "dialogFloatingButton"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
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
                AudioSelectActivity.this.lambda$createView$4$AudioSelectActivity(view);
            }
        });
        this.writeButton.setOnLongClickListener(new View.OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return AudioSelectActivity.this.lambda$createView$7$AudioSelectActivity(view);
            }
        });
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedCountView = new View(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, AudioSelectActivity.this.selectedAudios.size()))});
                int ceil = (int) Math.ceil((double) AudioSelectActivity.this.textPaint.measureText(format));
                int max = Math.max(AndroidUtilities.dp(16.0f) + ceil, AndroidUtilities.dp(24.0f));
                int measuredWidth = getMeasuredWidth() / 2;
                int measuredHeight = getMeasuredHeight() / 2;
                AudioSelectActivity.this.textPaint.setColor(Theme.getColor("dialogRoundCheckBoxCheck"));
                AudioSelectActivity.this.paint.setColor(Theme.getColor("dialogBackground"));
                int i = max / 2;
                int i2 = measuredWidth - i;
                int i3 = i + measuredWidth;
                AudioSelectActivity.this.rect.set((float) i2, 0.0f, (float) i3, (float) getMeasuredHeight());
                canvas.drawRoundRect(AudioSelectActivity.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), AudioSelectActivity.this.paint);
                AudioSelectActivity.this.paint.setColor(Theme.getColor("dialogRoundCheckBox"));
                AudioSelectActivity.this.rect.set((float) (i2 + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (i3 - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                canvas.drawRoundRect(AudioSelectActivity.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), AudioSelectActivity.this.paint);
                canvas.drawText(format, (float) (measuredWidth - (ceil / 2)), (float) AndroidUtilities.dp(16.2f), AudioSelectActivity.this.textPaint);
            }
        };
        this.selectedCountView.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        this.sizeNotifierFrameLayout.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -2.0f, 9.0f));
        updateEmptyView();
        updateCountButton(0);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$AudioSelectActivity(View view, int i) {
        onItemClick(view);
    }

    public /* synthetic */ boolean lambda$createView$2$AudioSelectActivity(View view, int i) {
        onItemClick(view);
        return true;
    }

    public /* synthetic */ void lambda$createView$4$AudioSelectActivity(View view) {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 == null || !chatActivity2.isInScheduleMode()) {
            sendSelectedAudios(true, 0);
        } else {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    AudioSelectActivity.this.sendSelectedAudios(z, i);
                }
            });
        }
    }

    public /* synthetic */ boolean lambda$createView$7$AudioSelectActivity(View view) {
        View view2 = view;
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 == null) {
            return false;
        }
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
                    if (motionEvent.getActionMasked() != 0 || AudioSelectActivity.this.sendPopupWindow == null || !AudioSelectActivity.this.sendPopupWindow.isShowing()) {
                        return false;
                    }
                    view.getHitRect(this.popupRect);
                    if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                        return false;
                    }
                    AudioSelectActivity.this.sendPopupWindow.dismiss();
                    return false;
                }
            });
            this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    AudioSelectActivity.this.lambda$null$5$AudioSelectActivity(keyEvent);
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
                            AudioSelectActivity.this.lambda$null$6$AudioSelectActivity(this.f$1, view);
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
        return false;
    }

    public /* synthetic */ void lambda$null$5$AudioSelectActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$null$6$AudioSelectActivity(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    AudioSelectActivity.this.sendSelectedAudios(z, i);
                }
            });
        } else if (i == 1) {
            sendSelectedAudios(true, 0);
        }
    }

    /* access modifiers changed from: private */
    public void updateEmptyView() {
        if (this.loadingAudio) {
            this.listView.setEmptyView(this.progressView);
            this.emptyView.setVisibility(8);
            return;
        }
        if (this.searching) {
            this.emptyTitleTextView.setText(LocaleController.getString("NoAudioFound", NUM));
            this.emptyView.setGravity(1);
            this.emptyView.setPadding(0, AndroidUtilities.dp(60.0f), 0, 0);
            this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        } else {
            this.emptyTitleTextView.setText(LocaleController.getString("NoAudioFiles", NUM));
            this.emptySubtitleTextView.setText(LocaleController.getString("NoAudioFilesInfo", NUM));
            this.emptyView.setGravity(17);
            this.emptyView.setPadding(0, 0, 0, 0);
            this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        }
        this.listView.setEmptyView(this.emptyView);
        this.progressView.setVisibility(8);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i != NotificationCenter.messagePlayingDidReset && i != NotificationCenter.messagePlayingDidStart && i != NotificationCenter.messagePlayingPlayStateChanged) {
        } else {
            if (i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.messagePlayingPlayStateChanged) {
                int childCount = this.listView.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = this.listView.getChildAt(i3);
                    if (childAt instanceof SharedAudioCell) {
                        SharedAudioCell sharedAudioCell = (SharedAudioCell) childAt;
                        if (sharedAudioCell.getMessage() != null) {
                            sharedAudioCell.updateButtonState(false, true);
                        }
                    }
                }
            } else if (i == NotificationCenter.messagePlayingDidStart && objArr[0].eventId == 0) {
                int childCount2 = this.listView.getChildCount();
                for (int i4 = 0; i4 < childCount2; i4++) {
                    View childAt2 = this.listView.getChildAt(i4);
                    if (childAt2 instanceof SharedAudioCell) {
                        SharedAudioCell sharedAudioCell2 = (SharedAudioCell) childAt2;
                        if (sharedAudioCell2.getMessage() != null) {
                            sharedAudioCell2.updateButtonState(false, true);
                        }
                    }
                }
            }
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

    private void onItemClick(View view) {
        SharedAudioCell sharedAudioCell = (SharedAudioCell) view;
        MediaController.AudioEntry audioEntry = (MediaController.AudioEntry) sharedAudioCell.getTag();
        boolean z = false;
        int i = 1;
        if (this.selectedAudios.indexOfKey(audioEntry.id) >= 0) {
            this.selectedAudios.remove(audioEntry.id);
            sharedAudioCell.setChecked(false, true);
        } else {
            this.selectedAudios.put(audioEntry.id, audioEntry);
            sharedAudioCell.setChecked(true, true);
            z = true;
        }
        if (!z) {
            i = 2;
        }
        updateCountButton(i);
    }

    private boolean showCommentTextView(final boolean z, boolean z2) {
        if (this.commentTextView == null) {
            return false;
        }
        if (z == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.frameLayout2.setTag(z ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (z) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        float f = 0.0f;
        float f2 = 0.2f;
        float f3 = 1.0f;
        if (z2) {
            this.animatorSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            FrameLayout frameLayout = this.writeButtonContainer;
            Property property = View.SCALE_X;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            FrameLayout frameLayout3 = this.writeButtonContainer;
            Property property2 = View.SCALE_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout3, property2, fArr2));
            FrameLayout frameLayout4 = this.writeButtonContainer;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout4, property3, fArr3));
            View view = this.selectedCountView;
            Property property4 = View.SCALE_X;
            float[] fArr4 = new float[1];
            fArr4[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(view, property4, fArr4));
            View view2 = this.selectedCountView;
            Property property5 = View.SCALE_Y;
            float[] fArr5 = new float[1];
            if (z) {
                f2 = 1.0f;
            }
            fArr5[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(view2, property5, fArr5));
            View view3 = this.selectedCountView;
            Property property6 = View.ALPHA;
            float[] fArr6 = new float[1];
            if (!z) {
                f3 = 0.0f;
            }
            fArr6[0] = f3;
            arrayList.add(ObjectAnimator.ofFloat(view3, property6, fArr6));
            FrameLayout frameLayout5 = this.frameLayout2;
            Property property7 = View.TRANSLATION_Y;
            float[] fArr7 = new float[1];
            fArr7[0] = z ? 0.0f : (float) AndroidUtilities.dp(48.0f);
            arrayList.add(ObjectAnimator.ofFloat(frameLayout5, property7, fArr7));
            View view4 = this.shadow;
            Property property8 = View.TRANSLATION_Y;
            float[] fArr8 = new float[1];
            if (!z) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            fArr8[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(view4, property8, fArr8));
            this.animatorSet.playTogether(arrayList);
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(AudioSelectActivity.this.animatorSet)) {
                        if (!z) {
                            AudioSelectActivity.this.frameLayout2.setVisibility(4);
                            AudioSelectActivity.this.writeButtonContainer.setVisibility(4);
                        }
                        AnimatorSet unused = AudioSelectActivity.this.animatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(AudioSelectActivity.this.animatorSet)) {
                        AnimatorSet unused = AudioSelectActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        } else {
            this.writeButtonContainer.setScaleX(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(z ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(z ? 1.0f : 0.2f);
            View view5 = this.selectedCountView;
            if (z) {
                f2 = 1.0f;
            }
            view5.setScaleY(f2);
            View view6 = this.selectedCountView;
            if (!z) {
                f3 = 0.0f;
            }
            view6.setAlpha(f3);
            this.frameLayout2.setTranslationY(z ? 0.0f : (float) AndroidUtilities.dp(48.0f));
            View view7 = this.shadow;
            if (!z) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            view7.setTranslationY(f);
            if (!z) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
            }
        }
        return true;
    }

    public void updateCountButton(int i) {
        boolean z = true;
        if (this.selectedAudios.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            if (i == 0) {
                z = false;
            }
            showCommentTextView(false, z);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true, i != 0) || i == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            return;
        }
        this.selectedCountView.setPivotX((float) AndroidUtilities.dp(21.0f));
        this.selectedCountView.setPivotY((float) AndroidUtilities.dp(12.0f));
        AnimatorSet animatorSet2 = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        View view = this.selectedCountView;
        Property property = View.SCALE_X;
        float[] fArr = new float[2];
        float f = 1.1f;
        fArr[0] = i == 1 ? 1.1f : 0.9f;
        fArr[1] = 1.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        View view2 = this.selectedCountView;
        Property property2 = View.SCALE_Y;
        float[] fArr2 = new float[2];
        if (i != 1) {
            f = 0.9f;
        }
        fArr2[0] = f;
        fArr2[1] = 1.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(view2, property2, fArr2);
        animatorSet2.playTogether(animatorArr);
        animatorSet2.setInterpolator(new OvershootInterpolator());
        animatorSet2.setDuration(180);
        animatorSet2.start();
    }

    /* access modifiers changed from: private */
    public void sendSelectedAudios(boolean z, int i) {
        if (this.selectedAudios.size() != 0 && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < this.selectedAudios.size(); i2++) {
                arrayList.add(this.selectedAudios.valueAt(i2).messageObject);
            }
            this.delegate.didSelectAudio(arrayList, this.commentTextView.getText().toString(), z, i);
            finishFragment();
        }
    }

    public void setDelegate(AudioSelectActivityDelegate audioSelectActivityDelegate) {
        this.delegate = audioSelectActivityDelegate;
    }

    private void loadAudio() {
        this.loadingAudio = true;
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                AudioSelectActivity.this.lambda$loadAudio$9$AudioSelectActivity();
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0163, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0164, code lost:
        r3 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0165, code lost:
        if (r2 != null) goto L_0x0167;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:24:0x016a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadAudio$9$AudioSelectActivity() {
        /*
            r16 = this;
            r1 = r16
            r0 = 6
            java.lang.String[] r4 = new java.lang.String[r0]
            r0 = 0
            java.lang.String r2 = "_id"
            r4[r0] = r2
            r8 = 1
            java.lang.String r2 = "artist"
            r4[r8] = r2
            r9 = 2
            java.lang.String r2 = "title"
            r4[r9] = r2
            r10 = 3
            java.lang.String r2 = "_data"
            r4[r10] = r2
            r11 = 4
            java.lang.String r2 = "duration"
            r4[r11] = r2
            r12 = 5
            java.lang.String r2 = "album"
            r4[r12] = r2
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x016b }
            android.content.ContentResolver r2 = r2.getContentResolver()     // Catch:{ Exception -> 0x016b }
            android.net.Uri r3 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x016b }
            java.lang.String r5 = "is_music != 0"
            r6 = 0
            java.lang.String r7 = "title"
            android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x016b }
            r3 = -2000000000(0xfffffffvar_ca6CLASSNAME, float:-1.2182823E-33)
        L_0x003c:
            boolean r4 = r2.moveToNext()     // Catch:{ all -> 0x0161 }
            if (r4 == 0) goto L_0x015b
            org.telegram.messenger.MediaController$AudioEntry r4 = new org.telegram.messenger.MediaController$AudioEntry     // Catch:{ all -> 0x0161 }
            r4.<init>()     // Catch:{ all -> 0x0161 }
            int r5 = r2.getInt(r0)     // Catch:{ all -> 0x0161 }
            long r5 = (long) r5     // Catch:{ all -> 0x0161 }
            r4.id = r5     // Catch:{ all -> 0x0161 }
            java.lang.String r5 = r2.getString(r8)     // Catch:{ all -> 0x0161 }
            r4.author = r5     // Catch:{ all -> 0x0161 }
            java.lang.String r5 = r2.getString(r9)     // Catch:{ all -> 0x0161 }
            r4.title = r5     // Catch:{ all -> 0x0161 }
            java.lang.String r5 = r2.getString(r10)     // Catch:{ all -> 0x0161 }
            r4.path = r5     // Catch:{ all -> 0x0161 }
            long r5 = r2.getLong(r11)     // Catch:{ all -> 0x0161 }
            r14 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 / r14
            int r6 = (int) r5     // Catch:{ all -> 0x0161 }
            r4.duration = r6     // Catch:{ all -> 0x0161 }
            java.lang.String r5 = r2.getString(r12)     // Catch:{ all -> 0x0161 }
            r4.genre = r5     // Catch:{ all -> 0x0161 }
            java.io.File r5 = new java.io.File     // Catch:{ all -> 0x0161 }
            java.lang.String r6 = r4.path     // Catch:{ all -> 0x0161 }
            r5.<init>(r6)     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x0161 }
            r6.<init>()     // Catch:{ all -> 0x0161 }
            r6.out = r8     // Catch:{ all -> 0x0161 }
            r6.id = r3     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$TL_peerUser r7 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x0161 }
            r7.<init>()     // Catch:{ all -> 0x0161 }
            r6.to_id = r7     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$Peer r7 = r6.to_id     // Catch:{ all -> 0x0161 }
            int r8 = r1.currentAccount     // Catch:{ all -> 0x0161 }
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)     // Catch:{ all -> 0x0161 }
            int r8 = r8.getClientUserId()     // Catch:{ all -> 0x0161 }
            r6.from_id = r8     // Catch:{ all -> 0x0161 }
            r7.user_id = r8     // Catch:{ all -> 0x0161 }
            long r7 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0161 }
            long r7 = r7 / r14
            int r8 = (int) r7     // Catch:{ all -> 0x0161 }
            r6.date = r8     // Catch:{ all -> 0x0161 }
            java.lang.String r7 = ""
            r6.message = r7     // Catch:{ all -> 0x0161 }
            java.lang.String r7 = r4.path     // Catch:{ all -> 0x0161 }
            r6.attachPath = r7     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r7 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ all -> 0x0161 }
            r7.<init>()     // Catch:{ all -> 0x0161 }
            r6.media = r7     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r6.media     // Catch:{ all -> 0x0161 }
            int r8 = r7.flags     // Catch:{ all -> 0x0161 }
            r8 = r8 | r10
            r7.flags = r8     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r6.media     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$TL_document r8 = new org.telegram.tgnet.TLRPC$TL_document     // Catch:{ all -> 0x0161 }
            r8.<init>()     // Catch:{ all -> 0x0161 }
            r7.document = r8     // Catch:{ all -> 0x0161 }
            int r7 = r6.flags     // Catch:{ all -> 0x0161 }
            r7 = r7 | 768(0x300, float:1.076E-42)
            r6.flags = r7     // Catch:{ all -> 0x0161 }
            java.lang.String r7 = org.telegram.messenger.FileLoader.getFileExtension(r5)     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r6.media     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$Document r8 = r8.document     // Catch:{ all -> 0x0161 }
            r14 = 0
            r8.id = r14     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r6.media     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$Document r8 = r8.document     // Catch:{ all -> 0x0161 }
            r8.access_hash = r14     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r6.media     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$Document r8 = r8.document     // Catch:{ all -> 0x0161 }
            byte[] r14 = new byte[r0]     // Catch:{ all -> 0x0161 }
            r8.file_reference = r14     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r6.media     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$Document r8 = r8.document     // Catch:{ all -> 0x0161 }
            int r14 = r6.date     // Catch:{ all -> 0x0161 }
            r8.date = r14     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r6.media     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$Document r8 = r8.document     // Catch:{ all -> 0x0161 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x0161 }
            r14.<init>()     // Catch:{ all -> 0x0161 }
            java.lang.String r15 = "audio/"
            r14.append(r15)     // Catch:{ all -> 0x0161 }
            int r15 = r7.length()     // Catch:{ all -> 0x0161 }
            if (r15 <= 0) goto L_0x00fb
            goto L_0x00fd
        L_0x00fb:
            java.lang.String r7 = "mp3"
        L_0x00fd:
            r14.append(r7)     // Catch:{ all -> 0x0161 }
            java.lang.String r7 = r14.toString()     // Catch:{ all -> 0x0161 }
            r8.mime_type = r7     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r6.media     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$Document r7 = r7.document     // Catch:{ all -> 0x0161 }
            long r14 = r5.length()     // Catch:{ all -> 0x0161 }
            int r8 = (int) r14     // Catch:{ all -> 0x0161 }
            r7.size = r8     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r6.media     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$Document r7 = r7.document     // Catch:{ all -> 0x0161 }
            r7.dc_id = r0     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio     // Catch:{ all -> 0x0161 }
            r7.<init>()     // Catch:{ all -> 0x0161 }
            int r8 = r4.duration     // Catch:{ all -> 0x0161 }
            r7.duration = r8     // Catch:{ all -> 0x0161 }
            java.lang.String r8 = r4.title     // Catch:{ all -> 0x0161 }
            r7.title = r8     // Catch:{ all -> 0x0161 }
            java.lang.String r8 = r4.author     // Catch:{ all -> 0x0161 }
            r7.performer = r8     // Catch:{ all -> 0x0161 }
            int r8 = r7.flags     // Catch:{ all -> 0x0161 }
            r8 = r8 | r10
            r7.flags = r8     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r6.media     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$Document r8 = r8.document     // Catch:{ all -> 0x0161 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r8.attributes     // Catch:{ all -> 0x0161 }
            r8.add(r7)     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename     // Catch:{ all -> 0x0161 }
            r7.<init>()     // Catch:{ all -> 0x0161 }
            java.lang.String r5 = r5.getName()     // Catch:{ all -> 0x0161 }
            r7.file_name = r5     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r6.media     // Catch:{ all -> 0x0161 }
            org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ all -> 0x0161 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r5.attributes     // Catch:{ all -> 0x0161 }
            r5.add(r7)     // Catch:{ all -> 0x0161 }
            org.telegram.messenger.MessageObject r5 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x0161 }
            int r7 = r1.currentAccount     // Catch:{ all -> 0x0161 }
            r5.<init>(r7, r6, r0)     // Catch:{ all -> 0x0161 }
            r4.messageObject = r5     // Catch:{ all -> 0x0161 }
            r13.add(r4)     // Catch:{ all -> 0x0161 }
            int r3 = r3 + -1
            r8 = 1
            goto L_0x003c
        L_0x015b:
            if (r2 == 0) goto L_0x016f
            r2.close()     // Catch:{ Exception -> 0x016b }
            goto L_0x016f
        L_0x0161:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0163 }
        L_0x0163:
            r0 = move-exception
            r3 = r0
            if (r2 == 0) goto L_0x016a
            r2.close()     // Catch:{ all -> 0x016a }
        L_0x016a:
            throw r3     // Catch:{ Exception -> 0x016b }
        L_0x016b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x016f:
            org.telegram.ui.-$$Lambda$AudioSelectActivity$qHu3jZT6Csp1zx-1DQDOfjLHr4c r0 = new org.telegram.ui.-$$Lambda$AudioSelectActivity$qHu3jZT6Csp1zx-1DQDOfjLHr4c
            r0.<init>(r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.AudioSelectActivity.lambda$loadAudio$9$AudioSelectActivity():void");
    }

    public /* synthetic */ void lambda$null$8$AudioSelectActivity(ArrayList arrayList) {
        this.loadingAudio = false;
        this.audioEntries = arrayList;
        if (this.audioEntries.isEmpty()) {
            this.searchItem.setVisibility(8);
        }
        updateEmptyView();
        this.listAdapter.notifyDataSetChanged();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public long getItemId(int i) {
            return (long) i;
        }

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
            return AudioSelectActivity.this.audioEntries.size();
        }

        public Object getItem(int i) {
            return AudioSelectActivity.this.audioEntries.get(i);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            AnonymousClass1 r1 = new SharedAudioCell(this.mContext) {
                public boolean needPlayMessage(MessageObject messageObject) {
                    MessageObject unused = AudioSelectActivity.this.playingAudio = messageObject;
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(messageObject);
                    return MediaController.getInstance().setPlaylist(arrayList, messageObject);
                }
            };
            r1.setCheckForButtonPress(true);
            return new RecyclerListView.Holder(r1);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            MediaController.AudioEntry audioEntry = (MediaController.AudioEntry) AudioSelectActivity.this.audioEntries.get(i);
            SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
            sharedAudioCell.setTag(audioEntry);
            boolean z = true;
            sharedAudioCell.setMessageObject(audioEntry.messageObject, i != AudioSelectActivity.this.audioEntries.size() - 1);
            if (AudioSelectActivity.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                z = false;
            }
            sharedAudioCell.setChecked(z, false);
        }
    }

    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int lastReqId;
        private Context mContext;
        private int reqId = 0;
        private ArrayList<MediaController.AudioEntry> searchResult = new ArrayList<>();
        private Runnable searchRunnable;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void search(String str) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                if (!this.searchResult.isEmpty()) {
                    this.searchResult.clear();
                }
                if (AudioSelectActivity.this.listView.getAdapter() != AudioSelectActivity.this.listAdapter) {
                    AudioSelectActivity.this.listView.setAdapter(AudioSelectActivity.this.listAdapter);
                }
                notifyDataSetChanged();
                return;
            }
            $$Lambda$AudioSelectActivity$SearchAdapter$oB5vontvK6nB1LUEbL62PoV4_BY r0 = new Runnable(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AudioSelectActivity.SearchAdapter.this.lambda$search$1$AudioSelectActivity$SearchAdapter(this.f$1);
                }
            };
            this.searchRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 300);
        }

        public /* synthetic */ void lambda$search$1$AudioSelectActivity$SearchAdapter(String str) {
            Utilities.searchQueue.postRunnable(new Runnable(str, new ArrayList(AudioSelectActivity.this.audioEntries)) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    AudioSelectActivity.SearchAdapter.this.lambda$null$0$AudioSelectActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$AudioSelectActivity$SearchAdapter(String str, ArrayList arrayList) {
            String str2;
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.length() == 0) {
                updateSearchResults(new ArrayList(), str);
                return;
            }
            String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
            if (lowerCase.equals(translitString) || translitString.length() == 0) {
                translitString = null;
            }
            String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
            strArr[0] = lowerCase;
            if (translitString != null) {
                strArr[1] = translitString;
            }
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < arrayList.size(); i++) {
                MediaController.AudioEntry audioEntry = (MediaController.AudioEntry) arrayList.get(i);
                int i2 = 0;
                while (true) {
                    if (i2 >= strArr.length) {
                        break;
                    }
                    String str3 = strArr[i2];
                    String str4 = audioEntry.author;
                    boolean contains = str4 != null ? str4.toLowerCase().contains(str3) : false;
                    if (!contains && (str2 = audioEntry.title) != null) {
                        contains = str2.toLowerCase().contains(str3);
                    }
                    if (contains) {
                        arrayList2.add(audioEntry);
                        break;
                    }
                    i2++;
                }
            }
            updateSearchResults(arrayList2, str);
        }

        private void updateSearchResults(ArrayList<MediaController.AudioEntry> arrayList, String str) {
            AndroidUtilities.runOnUIThread(new Runnable(str, arrayList) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    AudioSelectActivity.SearchAdapter.this.lambda$updateSearchResults$2$AudioSelectActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$2$AudioSelectActivity$SearchAdapter(String str, ArrayList arrayList) {
            if (AudioSelectActivity.this.searching) {
                if (AudioSelectActivity.this.listView.getAdapter() != AudioSelectActivity.this.searchAdapter) {
                    AudioSelectActivity.this.listView.setAdapter(AudioSelectActivity.this.searchAdapter);
                    AudioSelectActivity.this.updateEmptyView();
                }
                AudioSelectActivity.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoAudioFoundInfo", NUM, str)));
            }
            boolean unused = AudioSelectActivity.this.searchWas = true;
            this.searchResult = arrayList;
            notifyDataSetChanged();
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public MediaController.AudioEntry getItem(int i) {
            return this.searchResult.get(i);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            AnonymousClass1 r1 = new SharedAudioCell(this.mContext) {
                public boolean needPlayMessage(MessageObject messageObject) {
                    MessageObject unused = AudioSelectActivity.this.playingAudio = messageObject;
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(messageObject);
                    return MediaController.getInstance().setPlaylist(arrayList, messageObject);
                }
            };
            r1.setCheckForButtonPress(true);
            return new RecyclerListView.Holder(r1);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            MediaController.AudioEntry audioEntry = this.searchResult.get(i);
            SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
            sharedAudioCell.setTag(audioEntry);
            boolean z = true;
            sharedAudioCell.setMessageObject(audioEntry.messageObject, i != this.searchResult.size() - 1);
            if (AudioSelectActivity.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                z = false;
            }
            sharedAudioCell.setChecked(z, false);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[23];
        themeDescriptionArr[0] = new ThemeDescription(this.sizeNotifierFrameLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelHint");
        themeDescriptionArr[7] = new ThemeDescription(this.searchItem.getSearchField(), ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack");
        themeDescriptionArr[8] = new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage");
        themeDescriptionArr[9] = new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText");
        themeDescriptionArr[10] = new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider");
        themeDescriptionArr[14] = new ThemeDescription(this.progressView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder");
        themeDescriptionArr[15] = new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle");
        themeDescriptionArr[16] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox");
        themeDescriptionArr[17] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[20] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogFloatingIcon");
        themeDescriptionArr[21] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogFloatingButton");
        themeDescriptionArr[22] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Build.VERSION.SDK_INT >= 21 ? "dialogFloatingButtonPressed" : "dialogFloatingButton");
        return themeDescriptionArr;
    }
}

package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_exportedMessageLink;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.MessageStatisticActivity;

public class ShareAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public float captionEditTextTopOffset;
    /* access modifiers changed from: private */
    public float chatActivityEnterViewAnimateFromTop;
    /* access modifiers changed from: private */
    public EditTextEmoji commentTextView;
    private boolean copyLinkOnEnd;
    /* access modifiers changed from: private */
    public float currentPanTranslationY;
    /* access modifiers changed from: private */
    public boolean darkTheme;
    private ShareAlertDelegate delegate;
    private TLRPC$TL_exportedMessageLink exportedMessageLink;
    /* access modifiers changed from: private */
    public FrameLayout frameLayout;
    /* access modifiers changed from: private */
    public FrameLayout frameLayout2;
    /* access modifiers changed from: private */
    public RecyclerListView gridView;
    private int hasPoll;
    private boolean isChannel;
    int lastOffset;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public String[] linkToCopy;
    /* access modifiers changed from: private */
    public ShareDialogsAdapter listAdapter;
    private boolean loadingLink;
    /* access modifiers changed from: private */
    public Paint paint;
    /* access modifiers changed from: private */
    public boolean panTranslationMoveLayout;
    private Activity parentActivity;
    private ChatActivity parentFragment;
    /* access modifiers changed from: private */
    public TextView pickerBottomLayout;
    /* access modifiers changed from: private */
    public int previousScrollOffsetY;
    /* access modifiers changed from: private */
    public ArrayList<DialogsSearchAdapter.RecentSearchObject> recentSearchObjects;
    /* access modifiers changed from: private */
    public LongSparseArray<DialogsSearchAdapter.RecentSearchObject> recentSearchObjectsById;
    /* access modifiers changed from: private */
    public RectF rect;
    RecyclerItemsEnterAnimator recyclerItemsEnterAnimator;
    /* access modifiers changed from: private */
    public final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public ShareSearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public EmptyTextProgressView searchEmptyView;
    /* access modifiers changed from: private */
    public RecyclerListView searchGridView;
    private boolean searchIsVisible;
    /* access modifiers changed from: private */
    public FillLastGridLayoutManager searchLayoutManager;
    SearchField searchView;
    /* access modifiers changed from: private */
    public View selectedCountView;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC$Dialog> selectedDialogs;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    private ArrayList<MessageObject> sendingMessageObjects;
    private String[] sendingText;
    /* access modifiers changed from: private */
    public View[] shadow;
    /* access modifiers changed from: private */
    public AnimatorSet[] shadowAnimation;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    /* access modifiers changed from: private */
    public LinearLayout sharesCountLayout;
    private boolean showSendersName;
    private SwitchView switchView;
    /* access modifiers changed from: private */
    public TextPaint textPaint;
    /* access modifiers changed from: private */
    public ValueAnimator topBackgroundAnimator;
    /* access modifiers changed from: private */
    public int topBeforeSwitch;
    /* access modifiers changed from: private */
    public boolean updateSearchAdapter;
    /* access modifiers changed from: private */
    public FrameLayout writeButtonContainer;

    public static class DialogSearchResult {
        public int date;
        public TLRPC$Dialog dialog = new TLRPC$TL_dialog();
        public CharSequence name;
        public TLObject object;
    }

    public interface ShareAlertDelegate {

        /* renamed from: org.telegram.ui.Components.ShareAlert$ShareAlertDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didShare(ShareAlertDelegate shareAlertDelegate) {
            }
        }

        boolean didCopy();

        void didShare();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$6(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onSend(LongSparseArray<TLRPC$Dialog> longSparseArray, int i) {
    }

    private class SwitchView extends FrameLayout {
        /* access modifiers changed from: private */
        public AnimatorSet animator;
        /* access modifiers changed from: private */
        public int currentTab;
        /* access modifiers changed from: private */
        public int lastColor;
        private SimpleTextView leftTab;
        /* access modifiers changed from: private */
        public LinearGradient linearGradient;
        /* access modifiers changed from: private */
        public Paint paint = new Paint(1);
        /* access modifiers changed from: private */
        public RectF rect = new RectF();
        private SimpleTextView rightTab;
        private View searchBackground;
        private View slidingView;

        /* access modifiers changed from: protected */
        public void onTabSwitch(int i) {
            throw null;
        }

        public SwitchView(ShareAlert shareAlert, Context context) {
            super(context);
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), shareAlert.getThemedColor(shareAlert.darkTheme ? "voipgroup_searchBackground" : "dialogSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 0.0f, 14.0f, 0.0f));
            AnonymousClass1 r0 = new View(context, shareAlert) {
                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    invalidate();
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    int offsetColor = AndroidUtilities.getOffsetColor(-9057429, -10513163, getTranslationX() / ((float) getMeasuredWidth()), 1.0f);
                    int offsetColor2 = AndroidUtilities.getOffsetColor(-11554882, -4629871, getTranslationX() / ((float) getMeasuredWidth()), 1.0f);
                    if (offsetColor != SwitchView.this.lastColor) {
                        LinearGradient unused = SwitchView.this.linearGradient = new LinearGradient(0.0f, 0.0f, (float) getMeasuredWidth(), 0.0f, new int[]{offsetColor, offsetColor2}, (float[]) null, Shader.TileMode.CLAMP);
                        SwitchView.this.paint.setShader(SwitchView.this.linearGradient);
                    }
                    SwitchView.this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                    canvas.drawRoundRect(SwitchView.this.rect, (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(18.0f), SwitchView.this.paint);
                }
            };
            this.slidingView = r0;
            addView(r0, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 0.0f, 14.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.leftTab = simpleTextView;
            simpleTextView.setTextColor(shareAlert.getThemedColor("voipgroup_nameText"));
            this.leftTab.setTextSize(13);
            this.leftTab.setLeftDrawable(NUM);
            this.leftTab.setText(LocaleController.getString("VoipGroupInviteCanSpeak", NUM));
            this.leftTab.setGravity(17);
            addView(this.leftTab, LayoutHelper.createFrame(-1, -1.0f, 51, 14.0f, 0.0f, 0.0f, 0.0f));
            this.leftTab.setOnClickListener(new ShareAlert$SwitchView$$ExternalSyntheticLambda1(this));
            SimpleTextView simpleTextView2 = new SimpleTextView(context);
            this.rightTab = simpleTextView2;
            simpleTextView2.setTextColor(shareAlert.getThemedColor("voipgroup_nameText"));
            this.rightTab.setTextSize(13);
            this.rightTab.setLeftDrawable(NUM);
            this.rightTab.setText(LocaleController.getString("VoipGroupInviteListenOnly", NUM));
            this.rightTab.setGravity(17);
            addView(this.rightTab, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 14.0f, 0.0f));
            this.rightTab.setOnClickListener(new ShareAlert$SwitchView$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            switchToTab(0);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            switchToTab(1);
        }

        private void switchToTab(int i) {
            if (this.currentTab != i) {
                this.currentTab = i;
                AnimatorSet animatorSet = this.animator;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animator = animatorSet2;
                Animator[] animatorArr = new Animator[1];
                View view = this.slidingView;
                Property property = View.TRANSLATION_X;
                float[] fArr = new float[1];
                fArr[0] = this.currentTab == 0 ? 0.0f : (float) view.getMeasuredWidth();
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorSet2.playTogether(animatorArr);
                this.animator.setDuration(180);
                this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = SwitchView.this.animator = null;
                    }
                });
                this.animator.start();
                onTabSwitch(this.currentTab);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int size = (View.MeasureSpec.getSize(i) - AndroidUtilities.dp(28.0f)) / 2;
            ((FrameLayout.LayoutParams) this.leftTab.getLayoutParams()).width = size;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.rightTab.getLayoutParams();
            layoutParams.width = size;
            layoutParams.leftMargin = AndroidUtilities.dp(14.0f) + size;
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.slidingView.getLayoutParams();
            layoutParams2.width = size;
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.slidingView.setTranslationX(this.currentTab == 0 ? 0.0f : (float) layoutParams2.width);
            super.onMeasure(i, i2);
        }
    }

    private class SearchField extends FrameLayout {
        /* access modifiers changed from: private */
        public ImageView clearSearchImageView;
        private CloseProgressDrawable2 progressDrawable;
        private View searchBackground;
        /* access modifiers changed from: private */
        public EditTextBoldCursor searchEditText;
        private ImageView searchIconImageView;

        public SearchField(Context context) {
            super(context);
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), ShareAlert.this.getThemedColor(ShareAlert.this.darkTheme ? "voipgroup_searchBackground" : "dialogSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(NUM);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(ShareAlert.this.getThemedColor(ShareAlert.this.darkTheme ? "voipgroup_mutedIcon" : "dialogSearchIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            AnonymousClass1 r1 = new CloseProgressDrawable2(ShareAlert.this) {
                /* access modifiers changed from: protected */
                public int getCurrentColor() {
                    ShareAlert shareAlert = ShareAlert.this;
                    return shareAlert.getThemedColor(shareAlert.darkTheme ? "voipgroup_searchPlaceholder" : "dialogSearchIcon");
                }
            };
            this.progressDrawable = r1;
            imageView3.setImageDrawable(r1);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new ShareAlert$SearchField$$ExternalSyntheticLambda0(this));
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
            this.searchEditText = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(ShareAlert.this.getThemedColor(ShareAlert.this.darkTheme ? "voipgroup_searchPlaceholder" : "dialogSearchHint"));
            String str = "voipgroup_searchText";
            this.searchEditText.setTextColor(ShareAlert.this.getThemedColor(ShareAlert.this.darkTheme ? str : "dialogSearchText"));
            this.searchEditText.setBackgroundDrawable((Drawable) null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(NUM);
            this.searchEditText.setHint(LocaleController.getString("ShareSendTo", NUM));
            this.searchEditText.setCursorColor(ShareAlert.this.getThemedColor(!ShareAlert.this.darkTheme ? "featuredStickers_addedIcon" : str));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(ShareAlert.this) {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    boolean z = true;
                    boolean z2 = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                        z = false;
                    }
                    if (z2 != z) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (z2) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(z2 ? 1.0f : 0.1f);
                        if (!z2) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    if (!TextUtils.isEmpty(SearchField.this.searchEditText.getText())) {
                        ShareAlert.this.checkCurrentList(false);
                    }
                    if (ShareAlert.this.updateSearchAdapter) {
                        String obj = SearchField.this.searchEditText.getText().toString();
                        if (obj.length() != 0) {
                            if (ShareAlert.this.searchEmptyView != null) {
                                ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", NUM));
                            }
                        } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                            int access$2200 = ShareAlert.this.getCurrentTop();
                            ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", NUM));
                            ShareAlert.this.searchEmptyView.showTextView();
                            ShareAlert.this.checkCurrentList(false);
                            ShareAlert.this.listAdapter.notifyDataSetChanged();
                            if (access$2200 > 0) {
                                ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -access$2200);
                            }
                        }
                        if (ShareAlert.this.searchAdapter != null) {
                            ShareAlert.this.searchAdapter.searchDialogs(obj);
                        }
                    }
                }
            });
            this.searchEditText.setOnEditorActionListener(new ShareAlert$SearchField$$ExternalSyntheticLambda1(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            boolean unused = ShareAlert.this.updateSearchAdapter = true;
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$1(TextView textView, int i, KeyEvent keyEvent) {
            if (keyEvent == null) {
                return false;
            }
            if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
                return false;
            }
            AndroidUtilities.hideKeyboard(this.searchEditText);
            return false;
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }
    }

    public static ShareAlert createShareAlert(Context context, MessageObject messageObject, String str, boolean z, String str2, boolean z2) {
        ArrayList arrayList;
        if (messageObject != null) {
            arrayList = new ArrayList();
            arrayList.add(messageObject);
        } else {
            arrayList = null;
        }
        return new ShareAlert(context, (ChatActivity) null, arrayList, str, (String) null, z, str2, (String) null, z2, false);
    }

    public ShareAlert(Context context, ArrayList<MessageObject> arrayList, String str, boolean z, String str2, boolean z2) {
        this(context, arrayList, str, z, str2, z2, (Theme.ResourcesProvider) null);
    }

    public ShareAlert(Context context, ArrayList<MessageObject> arrayList, String str, boolean z, String str2, boolean z2, Theme.ResourcesProvider resourcesProvider2) {
        this(context, (ChatActivity) null, arrayList, str, (String) null, z, str2, (String) null, z2, false, resourcesProvider2);
    }

    public ShareAlert(Context context, ChatActivity chatActivity, ArrayList<MessageObject> arrayList, String str, String str2, boolean z, String str3, String str4, boolean z2, boolean z3) {
        this(context, chatActivity, arrayList, str, str2, z, str3, str4, z2, z3, (Theme.ResourcesProvider) null);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ShareAlert(android.content.Context r27, org.telegram.ui.ChatActivity r28, java.util.ArrayList<org.telegram.messenger.MessageObject> r29, java.lang.String r30, java.lang.String r31, boolean r32, java.lang.String r33, java.lang.String r34, boolean r35, boolean r36, org.telegram.ui.ActionBar.Theme.ResourcesProvider r37) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r29
            r3 = r32
            r4 = r36
            r5 = r37
            r6 = 1
            r0.<init>(r1, r6, r5)
            r7 = 2
            java.lang.String[] r8 = new java.lang.String[r7]
            r0.sendingText = r8
            android.view.View[] r8 = new android.view.View[r7]
            r0.shadow = r8
            android.animation.AnimatorSet[] r8 = new android.animation.AnimatorSet[r7]
            r0.shadowAnimation = r8
            androidx.collection.LongSparseArray r8 = new androidx.collection.LongSparseArray
            r8.<init>()
            r0.selectedDialogs = r8
            android.graphics.RectF r8 = new android.graphics.RectF
            r8.<init>()
            r0.rect = r8
            android.graphics.Paint r8 = new android.graphics.Paint
            r8.<init>(r6)
            r0.paint = r8
            android.text.TextPaint r8 = new android.text.TextPaint
            r8.<init>(r6)
            r0.textPaint = r8
            java.lang.String[] r8 = new java.lang.String[r7]
            r0.linkToCopy = r8
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r0.recentSearchObjects = r8
            androidx.collection.LongSparseArray r8 = new androidx.collection.LongSparseArray
            r8.<init>()
            r0.showSendersName = r6
            r8 = 2147483647(0x7fffffff, float:NaN)
            r0.lastOffset = r8
            r0.resourcesProvider = r5
            boolean r8 = r1 instanceof android.app.Activity
            if (r8 == 0) goto L_0x005b
            r8 = r1
            android.app.Activity r8 = (android.app.Activity) r8
            r0.parentActivity = r8
        L_0x005b:
            r0.darkTheme = r4
            r8 = r28
            r0.parentFragment = r8
            android.content.res.Resources r8 = r27.getResources()
            r9 = 2131166129(0x7var_b1, float:1.7946495E38)
            android.graphics.drawable.Drawable r8 = r8.getDrawable(r9)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r0.shadowDrawable = r8
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            boolean r10 = r0.darkTheme
            java.lang.String r11 = "dialogBackground"
            java.lang.String r12 = "voipgroup_inviteMembersBackground"
            if (r10 == 0) goto L_0x007e
            r10 = r12
            goto L_0x007f
        L_0x007e:
            r10 = r11
        L_0x007f:
            int r10 = r0.getThemedColor(r10)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r10, r13)
            r8.setColorFilter(r9)
            r8 = r35
            r0.isFullscreen = r8
            java.lang.String[] r8 = r0.linkToCopy
            r9 = 0
            r8[r9] = r33
            r8[r6] = r34
            r0.sendingMessageObjects = r2
            org.telegram.ui.Components.ShareAlert$ShareSearchAdapter r8 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter
            r8.<init>(r1)
            r0.searchAdapter = r8
            r0.isChannel = r3
            java.lang.String[] r8 = r0.sendingText
            r8[r9] = r30
            r8[r6] = r31
            r0.useSmoothKeyboard = r6
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r0.sendingMessageObjects
            if (r8 == 0) goto L_0x00d3
            int r8 = r8.size()
            r10 = 0
        L_0x00b2:
            if (r10 >= r8) goto L_0x00d3
            java.util.ArrayList<org.telegram.messenger.MessageObject> r13 = r0.sendingMessageObjects
            java.lang.Object r13 = r13.get(r10)
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            boolean r14 = r13.isPoll()
            if (r14 == 0) goto L_0x00d0
            boolean r13 = r13.isPublicPoll()
            if (r13 == 0) goto L_0x00ca
            r13 = 2
            goto L_0x00cb
        L_0x00ca:
            r13 = 1
        L_0x00cb:
            r0.hasPoll = r13
            if (r13 != r7) goto L_0x00d0
            goto L_0x00d3
        L_0x00d0:
            int r10 = r10 + 1
            goto L_0x00b2
        L_0x00d3:
            if (r3 == 0) goto L_0x010e
            r0.loadingLink = r6
            org.telegram.tgnet.TLRPC$TL_channels_exportMessageLink r3 = new org.telegram.tgnet.TLRPC$TL_channels_exportMessageLink
            r3.<init>()
            java.lang.Object r8 = r2.get(r9)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            int r8 = r8.getId()
            r3.id = r8
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            java.lang.Object r2 = r2.get(r9)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer_id
            long r13 = r2.channel_id
            org.telegram.tgnet.TLRPC$InputChannel r2 = r8.getInputChannel((long) r13)
            r3.channel = r2
            int r2 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda11 r8 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda11
            r8.<init>(r0, r1)
            r2.sendRequest(r3, r8)
        L_0x010e:
            org.telegram.ui.Components.ShareAlert$1 r2 = new org.telegram.ui.Components.ShareAlert$1
            r2.<init>(r1)
            r0.containerView = r2
            r2.setWillNotDraw(r9)
            android.view.ViewGroup r3 = r0.containerView
            r3.setClipChildren(r9)
            android.view.ViewGroup r3 = r0.containerView
            int r8 = r0.backgroundPaddingLeft
            r3.setPadding(r8, r9, r8, r9)
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r1)
            r0.frameLayout = r3
            boolean r8 = r0.darkTheme
            if (r8 == 0) goto L_0x0131
            r8 = r12
            goto L_0x0132
        L_0x0131:
            r8 = r11
        L_0x0132:
            int r8 = r0.getThemedColor(r8)
            r3.setBackgroundColor(r8)
            boolean r3 = r0.darkTheme
            if (r3 == 0) goto L_0x016d
            java.lang.String[] r3 = r0.linkToCopy
            r3 = r3[r6]
            if (r3 == 0) goto L_0x016d
            org.telegram.ui.Components.ShareAlert$2 r3 = new org.telegram.ui.Components.ShareAlert$2
            r3.<init>(r1)
            r0.switchView = r3
            android.widget.FrameLayout r8 = r0.frameLayout
            r10 = -1
            r13 = 1108344832(0x42100000, float:36.0)
            r14 = 51
            r15 = 0
            r16 = 1093664768(0x41300000, float:11.0)
            r17 = 0
            r18 = 0
            r28 = r10
            r29 = r13
            r30 = r14
            r31 = r15
            r32 = r16
            r33 = r17
            r34 = r18
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r8.addView(r3, r10)
        L_0x016d:
            org.telegram.ui.Components.ShareAlert$SearchField r3 = new org.telegram.ui.Components.ShareAlert$SearchField
            r3.<init>(r1)
            r0.searchView = r3
            android.widget.FrameLayout r8 = r0.frameLayout
            r10 = -1
            r13 = 58
            r14 = 83
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r13, (int) r14)
            r8.addView(r3, r15)
            org.telegram.ui.Components.ShareAlert$3 r3 = new org.telegram.ui.Components.ShareAlert$3
            r3.<init>(r1, r5)
            r0.gridView = r3
            r3.setSelectorDrawableColor(r9)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            r8 = 1111490560(0x42400000, float:48.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r3.setPadding(r9, r9, r9, r15)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            r3.setClipToPadding(r9)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            androidx.recyclerview.widget.GridLayoutManager r15 = new androidx.recyclerview.widget.GridLayoutManager
            android.content.Context r13 = r26.getContext()
            r7 = 4
            r15.<init>(r13, r7)
            r0.layoutManager = r15
            r3.setLayoutManager(r15)
            androidx.recyclerview.widget.GridLayoutManager r3 = r0.layoutManager
            org.telegram.ui.Components.ShareAlert$4 r13 = new org.telegram.ui.Components.ShareAlert$4
            r13.<init>()
            r3.setSpanSizeLookup(r13)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            r3.setHorizontalScrollBarEnabled(r9)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            r3.setVerticalScrollBarEnabled(r9)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            org.telegram.ui.Components.ShareAlert$5 r13 = new org.telegram.ui.Components.ShareAlert$5
            r13.<init>(r0)
            r3.addItemDecoration(r13)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r13 = r0.gridView
            r15 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r29 = r15
            r30 = r17
            r31 = r18
            r32 = r19
            r33 = r20
            r34 = r21
            r35 = r22
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r3.addView(r13, r15)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            org.telegram.ui.Components.ShareAlert$ShareDialogsAdapter r13 = new org.telegram.ui.Components.ShareAlert$ShareDialogsAdapter
            r13.<init>(r1)
            r0.listAdapter = r13
            r3.setAdapter(r13)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            boolean r13 = r0.darkTheme
            java.lang.String r15 = "dialogScrollGlow"
            if (r13 == 0) goto L_0x0207
            r13 = r12
            goto L_0x0208
        L_0x0207:
            r13 = r15
        L_0x0208:
            int r13 = r0.getThemedColor(r13)
            r3.setGlowColor(r13)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda14 r13 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda14
            r13.<init>(r0)
            r3.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r13)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            org.telegram.ui.Components.ShareAlert$6 r13 = new org.telegram.ui.Components.ShareAlert$6
            r13.<init>()
            r3.setOnScrollListener(r13)
            org.telegram.ui.Components.ShareAlert$7 r3 = new org.telegram.ui.Components.ShareAlert$7
            r3.<init>(r1, r5)
            r0.searchGridView = r3
            r3.setSelectorDrawableColor(r9)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r3.setPadding(r9, r9, r9, r13)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            r3.setClipToPadding(r9)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            org.telegram.ui.Components.FillLastGridLayoutManager r13 = new org.telegram.ui.Components.FillLastGridLayoutManager
            android.content.Context r8 = r26.getContext()
            org.telegram.ui.Components.RecyclerListView r14 = r0.searchGridView
            r13.<init>(r8, r7, r9, r14)
            r0.searchLayoutManager = r13
            r3.setLayoutManager(r13)
            org.telegram.ui.Components.FillLastGridLayoutManager r3 = r0.searchLayoutManager
            org.telegram.ui.Components.ShareAlert$8 r8 = new org.telegram.ui.Components.ShareAlert$8
            r8.<init>()
            r3.setSpanSizeLookup(r8)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda15 r8 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda15
            r8.<init>(r0)
            r3.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r8)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            r3.setHasFixedSize(r6)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            r8 = 0
            r3.setItemAnimator(r8)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            r3.setHorizontalScrollBarEnabled(r9)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            r3.setVerticalScrollBarEnabled(r9)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            org.telegram.ui.Components.ShareAlert$9 r8 = new org.telegram.ui.Components.ShareAlert$9
            r8.<init>()
            r3.setOnScrollListener(r8)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            org.telegram.ui.Components.ShareAlert$10 r8 = new org.telegram.ui.Components.ShareAlert$10
            r8.<init>(r0)
            r3.addItemDecoration(r8)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            org.telegram.ui.Components.ShareAlert$ShareSearchAdapter r8 = r0.searchAdapter
            r3.setAdapter(r8)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            boolean r8 = r0.darkTheme
            if (r8 == 0) goto L_0x0298
            r15 = r12
        L_0x0298:
            int r8 = r0.getThemedColor(r15)
            r3.setGlowColor(r8)
            org.telegram.ui.Components.RecyclerItemsEnterAnimator r3 = new org.telegram.ui.Components.RecyclerItemsEnterAnimator
            org.telegram.ui.Components.RecyclerListView r8 = r0.searchGridView
            r3.<init>(r8, r6)
            r0.recyclerItemsEnterAnimator = r3
            org.telegram.ui.Components.FlickerLoadingView r3 = new org.telegram.ui.Components.FlickerLoadingView
            r3.<init>(r1, r5)
            r8 = 12
            r3.setViewType(r8)
            org.telegram.ui.Components.EmptyTextProgressView r8 = new org.telegram.ui.Components.EmptyTextProgressView
            r8.<init>(r1, r3, r5)
            r0.searchEmptyView = r8
            r8.setShowAtCenter(r6)
            org.telegram.ui.Components.EmptyTextProgressView r3 = r0.searchEmptyView
            r3.showTextView()
            org.telegram.ui.Components.EmptyTextProgressView r3 = r0.searchEmptyView
            r8 = 2131626658(0x7f0e0aa2, float:1.8880558E38)
            java.lang.String r13 = "NoChats"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r13, r8)
            r3.setText(r8)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            org.telegram.ui.Components.EmptyTextProgressView r8 = r0.searchEmptyView
            r3.setEmptyView(r8)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            r3.setHideIfEmpty(r9)
            org.telegram.ui.Components.RecyclerListView r3 = r0.searchGridView
            r3.setAnimateEmptyView(r6, r9)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.EmptyTextProgressView r8 = r0.searchEmptyView
            r17 = -1
            r18 = -1082130432(0xffffffffbvar_, float:-1.0)
            r19 = 51
            r20 = 0
            r21 = 1112539136(0x42500000, float:52.0)
            r22 = 0
            r23 = 0
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r3.addView(r8, r13)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r8 = r0.searchGridView
            r21 = 0
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r3.addView(r8, r13)
            android.widget.FrameLayout$LayoutParams r3 = new android.widget.FrameLayout$LayoutParams
            int r8 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r13 = 51
            r3.<init>(r10, r8, r13)
            boolean r8 = r0.darkTheme
            if (r8 == 0) goto L_0x031e
            java.lang.String[] r8 = r0.linkToCopy
            r8 = r8[r6]
            if (r8 == 0) goto L_0x031e
            r8 = 1121845248(0x42de0000, float:111.0)
            goto L_0x0320
        L_0x031e:
            r8 = 1114112000(0x42680000, float:58.0)
        L_0x0320:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r3.topMargin = r8
            android.view.View[] r8 = r0.shadow
            android.view.View r14 = new android.view.View
            r14.<init>(r1)
            r8[r9] = r14
            android.view.View[] r8 = r0.shadow
            r8 = r8[r9]
            java.lang.String r14 = "dialogShadowLine"
            int r15 = r0.getThemedColor(r14)
            r8.setBackgroundColor(r15)
            android.view.View[] r8 = r0.shadow
            r8 = r8[r9]
            r15 = 0
            r8.setAlpha(r15)
            android.view.View[] r8 = r0.shadow
            r8 = r8[r9]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)
            r8.setTag(r7)
            android.view.ViewGroup r7 = r0.containerView
            android.view.View[] r8 = r0.shadow
            r8 = r8[r9]
            r7.addView(r8, r3)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.FrameLayout r7 = r0.frameLayout
            boolean r8 = r0.darkTheme
            if (r8 == 0) goto L_0x0369
            java.lang.String[] r8 = r0.linkToCopy
            r8 = r8[r6]
            if (r8 == 0) goto L_0x0369
            r8 = 111(0x6f, float:1.56E-43)
            goto L_0x036b
        L_0x0369:
            r8 = 58
        L_0x036b:
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r8, (int) r13)
            r3.addView(r7, r8)
            android.widget.FrameLayout$LayoutParams r3 = new android.widget.FrameLayout$LayoutParams
            int r7 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r8 = 83
            r3.<init>(r10, r7, r8)
            r7 = 1111490560(0x42400000, float:48.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r3.bottomMargin = r7
            android.view.View[] r7 = r0.shadow
            android.view.View r8 = new android.view.View
            r8.<init>(r1)
            r7[r6] = r8
            android.view.View[] r7 = r0.shadow
            r7 = r7[r6]
            int r8 = r0.getThemedColor(r14)
            r7.setBackgroundColor(r8)
            android.view.ViewGroup r7 = r0.containerView
            android.view.View[] r8 = r0.shadow
            r8 = r8[r6]
            r7.addView(r8, r3)
            boolean r3 = r0.isChannel
            java.lang.String r7 = "fonts/rmedium.ttf"
            if (r3 != 0) goto L_0x03b8
            java.lang.String[] r3 = r0.linkToCopy
            r3 = r3[r9]
            if (r3 == 0) goto L_0x03af
            goto L_0x03b8
        L_0x03af:
            android.view.View[] r3 = r0.shadow
            r3 = r3[r6]
            r3.setAlpha(r15)
            goto L_0x0563
        L_0x03b8:
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r1)
            r0.pickerBottomLayout = r3
            boolean r8 = r0.darkTheme
            if (r8 == 0) goto L_0x03c4
            r11 = r12
        L_0x03c4:
            int r8 = r0.getThemedColor(r11)
            boolean r11 = r0.darkTheme
            java.lang.String r12 = "voipgroup_listSelector"
            java.lang.String r13 = "listSelectorSDK21"
            if (r11 == 0) goto L_0x03d2
            r11 = r12
            goto L_0x03d3
        L_0x03d2:
            r11 = r13
        L_0x03d3:
            int r11 = r0.getThemedColor(r11)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorWithBackgroundDrawable(r8, r11)
            r3.setBackgroundDrawable(r8)
            android.widget.TextView r3 = r0.pickerBottomLayout
            boolean r8 = r0.darkTheme
            java.lang.String r11 = "voipgroup_listeningText"
            java.lang.String r14 = "dialogTextBlue2"
            if (r8 == 0) goto L_0x03ea
            r8 = r11
            goto L_0x03eb
        L_0x03ea:
            r8 = r14
        L_0x03eb:
            int r8 = r0.getThemedColor(r8)
            r3.setTextColor(r8)
            android.widget.TextView r3 = r0.pickerBottomLayout
            r8 = 1096810496(0x41600000, float:14.0)
            r3.setTextSize(r6, r8)
            android.widget.TextView r3 = r0.pickerBottomLayout
            r8 = 1099956224(0x41900000, float:18.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r17 = 1099956224(0x41900000, float:18.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r3.setPadding(r8, r9, r15, r9)
            android.widget.TextView r3 = r0.pickerBottomLayout
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r3.setTypeface(r8)
            android.widget.TextView r3 = r0.pickerBottomLayout
            r8 = 17
            r3.setGravity(r8)
            boolean r3 = r0.darkTheme
            if (r3 == 0) goto L_0x0437
            java.lang.String[] r3 = r0.linkToCopy
            r3 = r3[r6]
            if (r3 == 0) goto L_0x0437
            android.widget.TextView r3 = r0.pickerBottomLayout
            r8 = 2131628811(0x7f0e130b, float:1.8884925E38)
            java.lang.String r15 = "VoipGroupCopySpeakerLink"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r15, r8)
            java.lang.String r8 = r8.toUpperCase()
            r3.setText(r8)
            goto L_0x0449
        L_0x0437:
            android.widget.TextView r3 = r0.pickerBottomLayout
            r8 = 2131625165(0x7f0e04cd, float:1.887753E38)
            java.lang.String r15 = "CopyLink"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r15, r8)
            java.lang.String r8 = r8.toUpperCase()
            r3.setText(r8)
        L_0x0449:
            android.widget.TextView r3 = r0.pickerBottomLayout
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda2 r8 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda2
            r8.<init>(r0)
            r3.setOnClickListener(r8)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.TextView r8 = r0.pickerBottomLayout
            r15 = 48
            r6 = 83
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r15, (int) r6)
            r3.addView(r8, r15)
            org.telegram.ui.ChatActivity r3 = r0.parentFragment
            if (r3 == 0) goto L_0x0563
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getCurrentChat()
            boolean r3 = org.telegram.messenger.ChatObject.hasAdminRights(r3)
            if (r3 == 0) goto L_0x0563
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.sendingMessageObjects
            int r3 = r3.size()
            if (r3 <= 0) goto L_0x0563
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.sendingMessageObjects
            java.lang.Object r3 = r3.get(r9)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            int r3 = r3.forwards
            if (r3 <= 0) goto L_0x0563
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.sendingMessageObjects
            java.lang.Object r3 = r3.get(r9)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            boolean r6 = r3.isForwarded()
            if (r6 != 0) goto L_0x0563
            android.widget.LinearLayout r6 = new android.widget.LinearLayout
            r6.<init>(r1)
            r0.sharesCountLayout = r6
            r6.setOrientation(r9)
            android.widget.LinearLayout r6 = r0.sharesCountLayout
            r8 = 16
            r6.setGravity(r8)
            android.widget.LinearLayout r6 = r0.sharesCountLayout
            boolean r8 = r0.darkTheme
            if (r8 == 0) goto L_0x04ac
            goto L_0x04ad
        L_0x04ac:
            r12 = r13
        L_0x04ad:
            int r8 = r0.getThemedColor(r12)
            r12 = 2
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8, r12)
            r6.setBackgroundDrawable(r8)
            android.view.ViewGroup r6 = r0.containerView
            android.widget.LinearLayout r8 = r0.sharesCountLayout
            r19 = -2
            r20 = 1111490560(0x42400000, float:48.0)
            r21 = 85
            r22 = 1086324736(0x40CLASSNAME, float:6.0)
            r23 = 0
            r24 = -1061158912(0xffffffffc0CLASSNAME, float:-6.0)
            r25 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r6.addView(r8, r12)
            android.widget.LinearLayout r6 = r0.sharesCountLayout
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda4 r8 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda4
            r8.<init>(r0, r3)
            r6.setOnClickListener(r8)
            android.widget.ImageView r6 = new android.widget.ImageView
            r6.<init>(r1)
            r8 = 2131166127(0x7var_af, float:1.794649E38)
            r6.setImageResource(r8)
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            boolean r12 = r0.darkTheme
            if (r12 == 0) goto L_0x04ef
            r12 = r11
            goto L_0x04f0
        L_0x04ef:
            r12 = r14
        L_0x04f0:
            int r12 = r0.getThemedColor(r12)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r12, r13)
            r6.setColorFilter(r8)
            android.widget.LinearLayout r8 = r0.sharesCountLayout
            r19 = -2
            r20 = -1
            r21 = 16
            r22 = 20
            r23 = 0
            r24 = 0
            r25 = 0
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
            r8.addView(r6, r12)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r8 = 1
            java.lang.Object[] r12 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            int r3 = r3.forwards
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r12[r9] = r3
            java.lang.String r3 = "%d"
            java.lang.String r3 = java.lang.String.format(r3, r12)
            r6.setText(r3)
            r3 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r8, r3)
            boolean r3 = r0.darkTheme
            if (r3 == 0) goto L_0x0538
            goto L_0x0539
        L_0x0538:
            r11 = r14
        L_0x0539:
            int r3 = r0.getThemedColor(r11)
            r6.setTextColor(r3)
            r3 = 16
            r6.setGravity(r3)
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r6.setTypeface(r3)
            android.widget.LinearLayout r3 = r0.sharesCountLayout
            r19 = -2
            r20 = -1
            r21 = 16
            r22 = 8
            r23 = 0
            r24 = 20
            r25 = 0
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
            r3.addView(r6, r8)
        L_0x0563:
            org.telegram.ui.Components.ShareAlert$11 r3 = new org.telegram.ui.Components.ShareAlert$11
            r3.<init>(r1)
            r0.frameLayout2 = r3
            r3.setWillNotDraw(r9)
            android.widget.FrameLayout r3 = r0.frameLayout2
            r6 = 0
            r3.setAlpha(r6)
            android.widget.FrameLayout r3 = r0.frameLayout2
            r6 = 4
            r3.setVisibility(r6)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.FrameLayout r6 = r0.frameLayout2
            r8 = -2
            r11 = 83
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r8, (int) r11)
            r3.addView(r6, r8)
            android.widget.FrameLayout r3 = r0.frameLayout2
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda8 r6 = org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda8.INSTANCE
            r3.setOnTouchListener(r6)
            org.telegram.ui.Components.ShareAlert$12 r3 = new org.telegram.ui.Components.ShareAlert$12
            r6 = 0
            r8 = 1
            r28 = r3
            r29 = r26
            r30 = r27
            r31 = r2
            r32 = r6
            r33 = r8
            r34 = r37
            r28.<init>(r30, r31, r32, r33, r34)
            r0.commentTextView = r3
            if (r4 == 0) goto L_0x05b4
            org.telegram.ui.Components.EditTextCaption r2 = r3.getEditText()
            java.lang.String r3 = "voipgroup_nameText"
            int r3 = r0.getThemedColor(r3)
            r2.setTextColor(r3)
        L_0x05b4:
            org.telegram.ui.Components.EditTextEmoji r2 = r0.commentTextView
            r3 = 2131628058(0x7f0e101a, float:1.8883398E38)
            java.lang.String r4 = "ShareComment"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setHint(r3)
            org.telegram.ui.Components.EditTextEmoji r2 = r0.commentTextView
            r2.onResume()
            android.widget.FrameLayout r2 = r0.frameLayout2
            org.telegram.ui.Components.EditTextEmoji r3 = r0.commentTextView
            r4 = -1
            r5 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r6 = 51
            r8 = 0
            r10 = 0
            r11 = 1118306304(0x42a80000, float:84.0)
            r12 = 0
            r28 = r4
            r29 = r5
            r30 = r6
            r31 = r8
            r32 = r10
            r33 = r11
            r34 = r12
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r2.addView(r3, r4)
            android.widget.FrameLayout r2 = r0.frameLayout2
            r2.setClipChildren(r9)
            org.telegram.ui.Components.EditTextEmoji r2 = r0.commentTextView
            r2.setClipChildren(r9)
            org.telegram.ui.Components.ShareAlert$13 r2 = new org.telegram.ui.Components.ShareAlert$13
            r2.<init>(r1)
            r0.writeButtonContainer = r2
            r3 = 1
            r2.setFocusable(r3)
            android.widget.FrameLayout r2 = r0.writeButtonContainer
            r2.setFocusableInTouchMode(r3)
            android.widget.FrameLayout r2 = r0.writeButtonContainer
            r3 = 4
            r2.setVisibility(r3)
            android.widget.FrameLayout r2 = r0.writeButtonContainer
            r3 = 1045220557(0x3e4ccccd, float:0.2)
            r2.setScaleX(r3)
            android.widget.FrameLayout r2 = r0.writeButtonContainer
            r2.setScaleY(r3)
            android.widget.FrameLayout r2 = r0.writeButtonContainer
            r4 = 0
            r2.setAlpha(r4)
            android.view.ViewGroup r2 = r0.containerView
            android.widget.FrameLayout r4 = r0.writeButtonContainer
            r5 = 60
            r6 = 1114636288(0x42700000, float:60.0)
            r8 = 85
            r11 = 0
            r12 = 1086324736(0x40CLASSNAME, float:6.0)
            r13 = 1092616192(0x41200000, float:10.0)
            r28 = r5
            r29 = r6
            r30 = r8
            r31 = r10
            r32 = r11
            r33 = r12
            r34 = r13
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r2.addView(r4, r5)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r4 = 1113587712(0x42600000, float:56.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            java.lang.String r6 = "dialogFloatingButton"
            int r6 = r0.getThemedColor(r6)
            int r8 = android.os.Build.VERSION.SDK_INT
            r10 = 21
            if (r8 < r10) goto L_0x065b
            java.lang.String r11 = "dialogFloatingButtonPressed"
            goto L_0x065d
        L_0x065b:
            java.lang.String r11 = "dialogFloatingButton"
        L_0x065d:
            int r11 = r0.getThemedColor(r11)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r5, r6, r11)
            if (r8 >= r10) goto L_0x0693
            android.content.res.Resources r6 = r27.getResources()
            r11 = 2131165452(0x7var_c, float:1.7945122E38)
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r11)
            android.graphics.drawable.Drawable r6 = r6.mutate()
            android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
            r12 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r11.<init>(r12, r13)
            r6.setColorFilter(r11)
            org.telegram.ui.Components.CombinedDrawable r11 = new org.telegram.ui.Components.CombinedDrawable
            r11.<init>(r6, r5, r9, r9)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r11.setIconSize(r5, r6)
            r5 = r11
        L_0x0693:
            r2.setBackgroundDrawable(r5)
            r5 = 2131165282(0x7var_, float:1.7944777E38)
            r2.setImageResource(r5)
            r5 = 2
            r2.setImportantForAccessibility(r5)
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            java.lang.String r6 = "dialogFloatingIcon"
            int r6 = r0.getThemedColor(r6)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r6, r11)
            r2.setColorFilter(r5)
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r5)
            if (r8 < r10) goto L_0x06bf
            org.telegram.ui.Components.ShareAlert$14 r5 = new org.telegram.ui.Components.ShareAlert$14
            r5.<init>(r0)
            r2.setOutlineProvider(r5)
        L_0x06bf:
            android.widget.FrameLayout r5 = r0.writeButtonContainer
            if (r8 < r10) goto L_0x06c6
            r6 = 56
            goto L_0x06c8
        L_0x06c6:
            r6 = 60
        L_0x06c8:
            if (r8 < r10) goto L_0x06cb
            goto L_0x06cd
        L_0x06cb:
            r4 = 1114636288(0x42700000, float:60.0)
        L_0x06cd:
            r11 = 51
            if (r8 < r10) goto L_0x06d4
            r8 = 1073741824(0x40000000, float:2.0)
            goto L_0x06d5
        L_0x06d4:
            r8 = 0
        L_0x06d5:
            r10 = 0
            r12 = 0
            r13 = 0
            r28 = r6
            r29 = r4
            r30 = r11
            r31 = r8
            r32 = r10
            r33 = r12
            r34 = r13
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r5.addView(r2, r4)
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda3 r4 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda3
            r4.<init>(r0)
            r2.setOnClickListener(r4)
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda7 r4 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda7
            r4.<init>(r0, r2)
            r2.setOnLongClickListener(r4)
            android.text.TextPaint r2 = r0.textPaint
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.setTextSize(r4)
            android.text.TextPaint r2 = r0.textPaint
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r2.setTypeface(r4)
            org.telegram.ui.Components.ShareAlert$15 r2 = new org.telegram.ui.Components.ShareAlert$15
            r2.<init>(r1)
            r0.selectedCountView = r2
            r1 = 0
            r2.setAlpha(r1)
            android.view.View r1 = r0.selectedCountView
            r1.setScaleX(r3)
            android.view.View r1 = r0.selectedCountView
            r1.setScaleY(r3)
            android.view.ViewGroup r1 = r0.containerView
            android.view.View r2 = r0.selectedCountView
            r3 = 42
            r4 = 1103101952(0x41CLASSNAME, float:24.0)
            r5 = 85
            r6 = 0
            r7 = 0
            r8 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r10 = 1091567616(0x41100000, float:9.0)
            r27 = r3
            r28 = r4
            r29 = r5
            r30 = r6
            r31 = r7
            r32 = r8
            r33 = r10
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r1.addView(r2, r3)
            r0.updateSelectedCount(r9)
            int r1 = r0.currentAccount
            org.telegram.messenger.AccountInstance r1 = org.telegram.messenger.AccountInstance.getInstance(r1)
            org.telegram.ui.DialogsActivity.loadDialogs(r1)
            org.telegram.ui.Components.ShareAlert$ShareDialogsAdapter r1 = r0.listAdapter
            java.util.ArrayList r1 = r1.dialogs
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x076f
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            r1.addObserver(r0, r2)
        L_0x076f:
            int r1 = r0.currentAccount
            org.telegram.ui.Components.ShareAlert$16 r2 = new org.telegram.ui.Components.ShareAlert$16
            r2.<init>()
            org.telegram.ui.Adapters.DialogsSearchAdapter.loadRecentSearch(r1, r9, r2)
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r2 = 1
            r1.loadHints(r2)
            org.telegram.ui.Components.RecyclerListView r1 = r0.gridView
            r3 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r1, r2, r3, r9)
            org.telegram.ui.Components.RecyclerListView r1 = r0.searchGridView
            r2 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r1, r9, r2, r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.<init>(android.content.Context, org.telegram.ui.ChatActivity, java.util.ArrayList, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String, boolean, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(Context context, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ShareAlert$$ExternalSyntheticLambda10(this, tLObject, context));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(TLObject tLObject, Context context) {
        if (tLObject != null) {
            this.exportedMessageLink = (TLRPC$TL_exportedMessageLink) tLObject;
            if (this.copyLinkOnEnd) {
                copyLink(context);
            }
        }
        this.loadingLink = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view, int i) {
        TLRPC$Dialog item;
        if (i >= 0 && (item = this.listAdapter.getItem(i)) != null) {
            selectDialog((ShareDialogCell) view, item);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view, int i) {
        TLRPC$Dialog item;
        if (i >= 0 && (item = this.searchAdapter.getItem(i)) != null) {
            selectDialog((ShareDialogCell) view, item);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(View view) {
        if (this.selectedDialogs.size() != 0) {
            return;
        }
        if (this.isChannel || this.linkToCopy[0] != null) {
            dismiss();
            if (this.linkToCopy[0] != null || !this.loadingLink) {
                copyLink(getContext());
                return;
            }
            this.copyLinkOnEnd = true;
            Toast.makeText(getContext(), LocaleController.getString("Loading", NUM), 0).show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(MessageObject messageObject, View view) {
        this.parentFragment.presentFragment(new MessageStatisticActivity(messageObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(View view) {
        sendInternal(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$8(ImageView imageView, View view) {
        onSendLongClick(imageView);
        return true;
    }

    /* access modifiers changed from: private */
    public void selectDialog(ShareDialogCell shareDialogCell, TLRPC$Dialog tLRPC$Dialog) {
        if (this.hasPoll != 0 && DialogObject.isChatDialog(tLRPC$Dialog.id)) {
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-tLRPC$Dialog.id));
            boolean z = ChatObject.isChannel(chat) && this.hasPoll == 2 && !chat.megagroup;
            if (z || !ChatObject.canSendPolls(chat)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), this.resourcesProvider);
                builder.setTitle(LocaleController.getString("SendMessageTitle", NUM));
                if (z) {
                    builder.setMessage(LocaleController.getString("PublicPollCantForward", NUM));
                } else if (ChatObject.isActionBannedByDefault(chat, 10)) {
                    builder.setMessage(LocaleController.getString("ErrorSendRestrictedPollsAll", NUM));
                } else {
                    builder.setMessage(LocaleController.getString("ErrorSendRestrictedPolls", NUM));
                }
                builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                builder.show();
                return;
            }
        }
        if (this.selectedDialogs.indexOfKey(tLRPC$Dialog.id) >= 0) {
            this.selectedDialogs.remove(tLRPC$Dialog.id);
            if (shareDialogCell != null) {
                shareDialogCell.setChecked(false, true);
            }
            updateSelectedCount(1);
        } else {
            this.selectedDialogs.put(tLRPC$Dialog.id, tLRPC$Dialog);
            if (shareDialogCell != null) {
                shareDialogCell.setChecked(true, true);
            }
            updateSelectedCount(2);
            long j = UserConfig.getInstance(this.currentAccount).clientUserId;
            if (this.searchIsVisible) {
                TLRPC$Dialog tLRPC$Dialog2 = (TLRPC$Dialog) this.listAdapter.dialogsMap.get(tLRPC$Dialog.id);
                if (tLRPC$Dialog2 == null) {
                    this.listAdapter.dialogsMap.put(tLRPC$Dialog.id, tLRPC$Dialog);
                    this.listAdapter.dialogs.add(this.listAdapter.dialogs.isEmpty() ^ true ? 1 : 0, tLRPC$Dialog);
                } else if (tLRPC$Dialog2.id != j) {
                    this.listAdapter.dialogs.remove(tLRPC$Dialog2);
                    this.listAdapter.dialogs.add(this.listAdapter.dialogs.isEmpty() ^ true ? 1 : 0, tLRPC$Dialog2);
                }
                this.listAdapter.notifyDataSetChanged();
                this.updateSearchAdapter = false;
                this.searchView.searchEditText.setText("");
                checkCurrentList(false);
                this.searchView.hideKeyboard();
            }
        }
        DialogsSearchAdapter.CategoryAdapterRecycler categoryAdapterRecycler = this.searchAdapter.categoryAdapter;
        if (categoryAdapterRecycler != null) {
            categoryAdapterRecycler.notifyItemRangeChanged(0, categoryAdapterRecycler.getItemCount());
        }
    }

    private boolean onSendLongClick(View view) {
        int i;
        View view2 = view;
        if (this.parentFragment == null) {
            return false;
        }
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        if (this.sendingMessageObjects != null) {
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity, this.resourcesProvider);
            actionBarPopupWindowLayout.setAnimationEnabled(false);
            actionBarPopupWindowLayout.setOnTouchListener(new View.OnTouchListener() {
                private Rect popupRect = new Rect();

                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getActionMasked() != 0 || ShareAlert.this.sendPopupWindow == null || !ShareAlert.this.sendPopupWindow.isShowing()) {
                        return false;
                    }
                    view.getHitRect(this.popupRect);
                    if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                        return false;
                    }
                    ShareAlert.this.sendPopupWindow.dismiss();
                    return false;
                }
            });
            actionBarPopupWindowLayout.setDispatchKeyEventListener(new ShareAlert$$ExternalSyntheticLambda12(this));
            actionBarPopupWindowLayout.setShownFromBotton(false);
            actionBarPopupWindowLayout.setupRadialSelectors(getThemedColor("dialogButtonSelector"));
            ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), true, true, false, this.resourcesProvider);
            actionBarPopupWindowLayout.addView(actionBarMenuSubItem, LayoutHelper.createLinear(-1, 48));
            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("ShowSendersName", NUM), 0);
            this.showSendersName = true;
            actionBarMenuSubItem.setChecked(true);
            ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(getContext(), true, false, true, this.resourcesProvider);
            actionBarPopupWindowLayout.addView(actionBarMenuSubItem2, LayoutHelper.createLinear(-1, 48));
            actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString("HideSendersName", NUM), 0);
            actionBarMenuSubItem2.setChecked(!this.showSendersName);
            actionBarMenuSubItem.setOnClickListener(new ShareAlert$$ExternalSyntheticLambda6(this, actionBarMenuSubItem, actionBarMenuSubItem2));
            actionBarMenuSubItem2.setOnClickListener(new ShareAlert$$ExternalSyntheticLambda5(this, actionBarMenuSubItem, actionBarMenuSubItem2));
            linearLayout.addView(actionBarPopupWindowLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, -8.0f));
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity, this.resourcesProvider);
        actionBarPopupWindowLayout2.setAnimationEnabled(false);
        actionBarPopupWindowLayout2.setOnTouchListener(new View.OnTouchListener() {
            private Rect popupRect = new Rect();

            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() != 0 || ShareAlert.this.sendPopupWindow == null || !ShareAlert.this.sendPopupWindow.isShowing()) {
                    return false;
                }
                view.getHitRect(this.popupRect);
                if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    return false;
                }
                ShareAlert.this.sendPopupWindow.dismiss();
                return false;
            }
        });
        actionBarPopupWindowLayout2.setDispatchKeyEventListener(new ShareAlert$$ExternalSyntheticLambda13(this));
        actionBarPopupWindowLayout2.setShownFromBotton(false);
        actionBarPopupWindowLayout2.setupRadialSelectors(getThemedColor("dialogButtonSelector"));
        ActionBarMenuSubItem actionBarMenuSubItem3 = new ActionBarMenuSubItem(getContext(), true, true, this.resourcesProvider);
        actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
        actionBarMenuSubItem3.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarPopupWindowLayout2.addView(actionBarMenuSubItem3, LayoutHelper.createLinear(-1, 48));
        actionBarMenuSubItem3.setOnClickListener(new ShareAlert$$ExternalSyntheticLambda1(this));
        ActionBarMenuSubItem actionBarMenuSubItem4 = new ActionBarMenuSubItem(getContext(), true, true, this.resourcesProvider);
        actionBarMenuSubItem4.setTextAndIcon(LocaleController.getString("SendMessage", NUM), NUM);
        actionBarMenuSubItem4.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarPopupWindowLayout2.addView(actionBarMenuSubItem4, LayoutHelper.createLinear(-1, 48));
        actionBarMenuSubItem4.setOnClickListener(new ShareAlert$$ExternalSyntheticLambda0(this));
        linearLayout.addView(actionBarPopupWindowLayout2, LayoutHelper.createLinear(-1, -2));
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(linearLayout, -2, -2);
        this.sendPopupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setAnimationEnabled(false);
        this.sendPopupWindow.setAnimationStyle(NUM);
        this.sendPopupWindow.setOutsideTouchable(true);
        this.sendPopupWindow.setClippingEnabled(true);
        this.sendPopupWindow.setInputMethodMode(2);
        this.sendPopupWindow.setSoftInputMode(0);
        this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        SharedConfig.removeScheduledOrNoSuoundHint();
        linearLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] iArr = new int[2];
        view2.getLocationInWindow(iArr);
        if (!this.keyboardVisible || this.parentFragment.contentView.getMeasuredHeight() <= AndroidUtilities.dp(58.0f)) {
            i = (iArr[1] - linearLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f);
        } else {
            i = iArr[1] + view.getMeasuredHeight();
        }
        this.sendPopupWindow.showAtLocation(view2, 51, ((iArr[0] + view.getMeasuredWidth()) - linearLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), i);
        this.sendPopupWindow.dimBehind();
        view2.performHapticFeedback(3, 2);
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSendLongClick$9(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSendLongClick$10(ActionBarMenuSubItem actionBarMenuSubItem, ActionBarMenuSubItem actionBarMenuSubItem2, View view) {
        this.showSendersName = true;
        actionBarMenuSubItem.setChecked(true);
        actionBarMenuSubItem2.setChecked(!this.showSendersName);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSendLongClick$11(ActionBarMenuSubItem actionBarMenuSubItem, ActionBarMenuSubItem actionBarMenuSubItem2, View view) {
        this.showSendersName = false;
        actionBarMenuSubItem.setChecked(false);
        actionBarMenuSubItem2.setChecked(!this.showSendersName);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSendLongClick$12(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSendLongClick$13(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        sendInternal(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSendLongClick$14(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        sendInternal(true);
    }

    private void sendInternal(boolean z) {
        int i = 0;
        int i2 = 0;
        while (true) {
            boolean z2 = true;
            if (i2 < this.selectedDialogs.size()) {
                long keyAt = this.selectedDialogs.keyAt(i2);
                Context context = getContext();
                int i3 = this.currentAccount;
                if (this.frameLayout2.getTag() == null || this.commentTextView.length() <= 0) {
                    z2 = false;
                }
                if (!AlertsCreator.checkSlowMode(context, i3, keyAt, z2)) {
                    i2++;
                } else {
                    return;
                }
            } else {
                if (this.sendingMessageObjects != null) {
                    while (i < this.selectedDialogs.size()) {
                        long keyAt2 = this.selectedDialogs.keyAt(i);
                        if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), keyAt2, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, 0, (MessageObject.SendAnimationData) null);
                        }
                        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingMessageObjects, keyAt2, !this.showSendersName, false, z, 0);
                        i++;
                    }
                    onSend(this.selectedDialogs, this.sendingMessageObjects.size());
                } else {
                    SwitchView switchView2 = this.switchView;
                    int access$10100 = switchView2 != null ? switchView2.currentTab : 0;
                    if (this.sendingText[access$10100] != null) {
                        while (i < this.selectedDialogs.size()) {
                            long keyAt3 = this.selectedDialogs.keyAt(i);
                            if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), keyAt3, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, 0, (MessageObject.SendAnimationData) null);
                            }
                            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingText[access$10100], keyAt3, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, 0, (MessageObject.SendAnimationData) null);
                            i++;
                        }
                    }
                    onSend(this.selectedDialogs, 1);
                }
                ShareAlertDelegate shareAlertDelegate = this.delegate;
                if (shareAlertDelegate != null) {
                    shareAlertDelegate.didShare();
                }
                dismiss();
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public int getCurrentTop() {
        if (this.gridView.getChildCount() == 0) {
            return -1000;
        }
        int i = 0;
        View childAt = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(childAt);
        if (holder == null) {
            return -1000;
        }
        int paddingTop = this.gridView.getPaddingTop();
        if (holder.getLayoutPosition() == 0 && childAt.getTop() >= 0) {
            i = childAt.getTop();
        }
        return paddingTop - i;
    }

    public void setDelegate(ShareAlertDelegate shareAlertDelegate) {
        this.delegate = shareAlertDelegate;
    }

    public void dismissInternal() {
        super.dismissInternal();
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    public void onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            super.onBackPressed();
        } else {
            this.commentTextView.hidePopup(true);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = NotificationCenter.dialogsNeedReload;
        if (i == i3) {
            ShareDialogsAdapter shareDialogsAdapter = this.listAdapter;
            if (shareDialogsAdapter != null) {
                shareDialogsAdapter.fetchDialogs();
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, i3);
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void updateLayout() {
        if (!this.panTranslationMoveLayout) {
            RecyclerListView recyclerListView = this.searchIsVisible ? this.searchGridView : this.gridView;
            if (recyclerListView.getChildCount() > 0) {
                View childAt = recyclerListView.getChildAt(0);
                for (int i = 0; i < recyclerListView.getChildCount(); i++) {
                    if (recyclerListView.getChildAt(i).getTop() < childAt.getTop()) {
                        childAt = recyclerListView.getChildAt(i);
                    }
                }
                RecyclerListView.Holder holder = (RecyclerListView.Holder) recyclerListView.findContainingViewHolder(childAt);
                int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
                int i2 = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
                if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
                    this.lastOffset = Integer.MAX_VALUE;
                    runShadowAnimation(0, true);
                    top = i2;
                } else {
                    this.lastOffset = childAt.getTop();
                    runShadowAnimation(0, false);
                }
                int i3 = this.scrollOffsetY;
                if (i3 != top) {
                    this.previousScrollOffsetY = i3;
                    RecyclerListView recyclerListView2 = this.gridView;
                    float f = (float) top;
                    int i4 = (int) (this.currentPanTranslationY + f);
                    this.scrollOffsetY = i4;
                    recyclerListView2.setTopGlowOffset(i4);
                    RecyclerListView recyclerListView3 = this.searchGridView;
                    int i5 = (int) (f + this.currentPanTranslationY);
                    this.scrollOffsetY = i5;
                    recyclerListView3.setTopGlowOffset(i5);
                    this.frameLayout.setTranslationY(((float) this.scrollOffsetY) + this.currentPanTranslationY);
                    this.searchEmptyView.setTranslationY(((float) this.scrollOffsetY) + this.currentPanTranslationY);
                    this.containerView.invalidate();
                }
            }
        }
    }

    private void runShadowAnimation(final int i, final boolean z) {
        if ((z && this.shadow[i].getTag() != null) || (!z && this.shadow[i].getTag() == null)) {
            this.shadow[i].setTag(z ? null : 1);
            if (z) {
                this.shadow[i].setVisibility(0);
            }
            AnimatorSet[] animatorSetArr = this.shadowAnimation;
            if (animatorSetArr[i] != null) {
                animatorSetArr[i].cancel();
            }
            this.shadowAnimation[i] = new AnimatorSet();
            AnimatorSet animatorSet2 = this.shadowAnimation[i];
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow[i];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation[i].setDuration(150);
            this.shadowAnimation[i].addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ShareAlert.this.shadowAnimation[i] != null && ShareAlert.this.shadowAnimation[i].equals(animator)) {
                        if (!z) {
                            ShareAlert.this.shadow[i].setVisibility(4);
                        }
                        ShareAlert.this.shadowAnimation[i] = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ShareAlert.this.shadowAnimation[i] != null && ShareAlert.this.shadowAnimation[i].equals(animator)) {
                        ShareAlert.this.shadowAnimation[i] = null;
                    }
                }
            });
            this.shadowAnimation[i].start();
        }
    }

    private void copyLink(Context context) {
        String str;
        boolean z = false;
        if (this.exportedMessageLink != null || this.linkToCopy[0] != null) {
            try {
                SwitchView switchView2 = this.switchView;
                if (switchView2 != null) {
                    str = this.linkToCopy[switchView2.currentTab];
                } else {
                    str = this.linkToCopy[0];
                }
                ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                if (str == null) {
                    str = this.exportedMessageLink.link;
                }
                clipboardManager.setPrimaryClip(ClipData.newPlainText("label", str));
                ShareAlertDelegate shareAlertDelegate = this.delegate;
                if ((shareAlertDelegate == null || !shareAlertDelegate.didCopy()) && (this.parentActivity instanceof LaunchActivity)) {
                    TLRPC$TL_exportedMessageLink tLRPC$TL_exportedMessageLink = this.exportedMessageLink;
                    if (tLRPC$TL_exportedMessageLink != null && tLRPC$TL_exportedMessageLink.link.contains("/c/")) {
                        z = true;
                    }
                    ((LaunchActivity) this.parentActivity).showBulletin(new ShareAlert$$ExternalSyntheticLambda9(this, z));
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Bulletin lambda$copyLink$15(boolean z, BulletinFactory bulletinFactory) {
        return bulletinFactory.createCopyLinkBulletin(z, this.resourcesProvider);
    }

    private boolean showCommentTextView(final boolean z) {
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
        TextView textView = this.pickerBottomLayout;
        int i = 4;
        if (textView != null) {
            ViewCompat.setImportantForAccessibility(textView, z ? 4 : 1);
        }
        LinearLayout linearLayout = this.sharesCountLayout;
        if (linearLayout != null) {
            if (!z) {
                i = 1;
            }
            ViewCompat.setImportantForAccessibility(linearLayout, i);
        }
        this.animatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        FrameLayout frameLayout3 = this.frameLayout2;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 0.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout3, property, fArr));
        FrameLayout frameLayout4 = this.writeButtonContainer;
        Property property2 = View.SCALE_X;
        float[] fArr2 = new float[1];
        float f2 = 0.2f;
        fArr2[0] = z ? 1.0f : 0.2f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout4, property2, fArr2));
        FrameLayout frameLayout5 = this.writeButtonContainer;
        Property property3 = View.SCALE_Y;
        float[] fArr3 = new float[1];
        fArr3[0] = z ? 1.0f : 0.2f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout5, property3, fArr3));
        FrameLayout frameLayout6 = this.writeButtonContainer;
        Property property4 = View.ALPHA;
        float[] fArr4 = new float[1];
        fArr4[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout6, property4, fArr4));
        View view = this.selectedCountView;
        Property property5 = View.SCALE_X;
        float[] fArr5 = new float[1];
        fArr5[0] = z ? 1.0f : 0.2f;
        arrayList.add(ObjectAnimator.ofFloat(view, property5, fArr5));
        View view2 = this.selectedCountView;
        Property property6 = View.SCALE_Y;
        float[] fArr6 = new float[1];
        if (z) {
            f2 = 1.0f;
        }
        fArr6[0] = f2;
        arrayList.add(ObjectAnimator.ofFloat(view2, property6, fArr6));
        View view3 = this.selectedCountView;
        Property property7 = View.ALPHA;
        float[] fArr7 = new float[1];
        fArr7[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(view3, property7, fArr7));
        TextView textView2 = this.pickerBottomLayout;
        if (textView2 == null || textView2.getVisibility() != 0) {
            View view4 = this.shadow[1];
            Property property8 = View.ALPHA;
            float[] fArr8 = new float[1];
            if (z) {
                f = 1.0f;
            }
            fArr8[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(view4, property8, fArr8));
        }
        this.animatorSet.playTogether(arrayList);
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.setDuration(180);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(ShareAlert.this.animatorSet)) {
                    if (!z) {
                        ShareAlert.this.frameLayout2.setVisibility(4);
                        ShareAlert.this.writeButtonContainer.setVisibility(4);
                    }
                    AnimatorSet unused = ShareAlert.this.animatorSet = null;
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (animator.equals(ShareAlert.this.animatorSet)) {
                    AnimatorSet unused = ShareAlert.this.animatorSet = null;
                }
            }
        });
        this.animatorSet.start();
        return true;
    }

    public void updateSelectedCount(int i) {
        if (this.selectedDialogs.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            showCommentTextView(false);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true) || i == 0) {
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

    public void dismiss() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
        }
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
    }

    private class ShareDialogsAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        /* access modifiers changed from: private */
        public ArrayList<TLRPC$Dialog> dialogs = new ArrayList<>();
        /* access modifiers changed from: private */
        public LongSparseArray<TLRPC$Dialog> dialogsMap = new LongSparseArray<>();

        public int getItemViewType(int i) {
            return i == 0 ? 1 : 0;
        }

        public ShareDialogsAdapter(Context context2) {
            this.context = context2;
            fetchDialogs();
        }

        public void fetchDialogs() {
            TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
            this.dialogs.clear();
            this.dialogsMap.clear();
            long j = UserConfig.getInstance(ShareAlert.this.currentAccount).clientUserId;
            if (!MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.isEmpty()) {
                TLRPC$Dialog tLRPC$Dialog = MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.get(0);
                this.dialogs.add(tLRPC$Dialog);
                this.dialogsMap.put(tLRPC$Dialog.id, tLRPC$Dialog);
            }
            ArrayList arrayList = new ArrayList();
            ArrayList<TLRPC$Dialog> allDialogs = MessagesController.getInstance(ShareAlert.this.currentAccount).getAllDialogs();
            for (int i = 0; i < allDialogs.size(); i++) {
                TLRPC$Dialog tLRPC$Dialog2 = allDialogs.get(i);
                if (tLRPC$Dialog2 instanceof TLRPC$TL_dialog) {
                    long j2 = tLRPC$Dialog2.id;
                    if (j2 != j && !DialogObject.isEncryptedDialog(j2)) {
                        if (DialogObject.isUserDialog(tLRPC$Dialog2.id)) {
                            if (tLRPC$Dialog2.folder_id == 1) {
                                arrayList.add(tLRPC$Dialog2);
                            } else {
                                this.dialogs.add(tLRPC$Dialog2);
                            }
                            this.dialogsMap.put(tLRPC$Dialog2.id, tLRPC$Dialog2);
                        } else {
                            TLRPC$Chat chat = MessagesController.getInstance(ShareAlert.this.currentAccount).getChat(Long.valueOf(-tLRPC$Dialog2.id));
                            if (chat != null && !ChatObject.isNotInChat(chat) && ((!chat.gigagroup || ChatObject.hasAdminRights(chat)) && (!ChatObject.isChannel(chat) || chat.creator || (((tLRPC$TL_chatAdminRights = chat.admin_rights) != null && tLRPC$TL_chatAdminRights.post_messages) || chat.megagroup)))) {
                                if (tLRPC$Dialog2.folder_id == 1) {
                                    arrayList.add(tLRPC$Dialog2);
                                } else {
                                    this.dialogs.add(tLRPC$Dialog2);
                                }
                                this.dialogsMap.put(tLRPC$Dialog2.id, tLRPC$Dialog2);
                            }
                        }
                    }
                }
            }
            this.dialogs.addAll(arrayList);
            notifyDataSetChanged();
        }

        public int getItemCount() {
            int size = this.dialogs.size();
            return size != 0 ? size + 1 : size;
        }

        public TLRPC$Dialog getItem(int i) {
            int i2 = i - 1;
            if (i2 < 0 || i2 >= this.dialogs.size()) {
                return null;
            }
            return this.dialogs.get(i2);
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new View(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 56.0f : 109.0f)));
            } else {
                view = new ShareDialogCell(this.context, ShareAlert.this.darkTheme ? 1 : 0, ShareAlert.this.resourcesProvider);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ShareDialogCell shareDialogCell = (ShareDialogCell) viewHolder.itemView;
                TLRPC$Dialog item = getItem(i);
                shareDialogCell.setDialog(item.id, ShareAlert.this.selectedDialogs.indexOfKey(item.id) >= 0, (CharSequence) null);
            }
        }
    }

    public class ShareSearchAdapter extends RecyclerListView.SelectionAdapter {
        DialogsSearchAdapter.CategoryAdapterRecycler categoryAdapter;
        private Context context;
        int firstEmptyViewCell = -1;
        int hintsCell = -1;
        boolean internalDialogsIsSearching = false;
        int itemsCount;
        int lastFilledItem = -1;
        /* access modifiers changed from: private */
        public int lastGlobalSearchId;
        int lastItemCont;
        /* access modifiers changed from: private */
        public int lastLocalSearchId;
        /* access modifiers changed from: private */
        public int lastSearchId;
        private String lastSearchText;
        int recentDialogsStartRow = -1;
        int resentTitleCell = -1;
        /* access modifiers changed from: private */
        public SearchAdapterHelper searchAdapterHelper;
        /* access modifiers changed from: private */
        public ArrayList<Object> searchResult = new ArrayList<>();
        private Runnable searchRunnable;
        private Runnable searchRunnable2;

        public ShareSearchAdapter(Context context2) {
            this.context = context2;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate(ShareAlert.this) {
                public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                public /* synthetic */ LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }

                public void onDataSetChanged(int i) {
                    int unused = ShareSearchAdapter.this.lastGlobalSearchId = i;
                    if (ShareSearchAdapter.this.lastLocalSearchId != i) {
                        ShareSearchAdapter.this.searchResult.clear();
                    }
                    ShareSearchAdapter shareSearchAdapter = ShareSearchAdapter.this;
                    int i2 = shareSearchAdapter.lastItemCont;
                    if (shareSearchAdapter.getItemCount() == 0 && !ShareSearchAdapter.this.searchAdapterHelper.isSearchInProgress()) {
                        ShareSearchAdapter shareSearchAdapter2 = ShareSearchAdapter.this;
                        if (!shareSearchAdapter2.internalDialogsIsSearching) {
                            ShareAlert.this.searchEmptyView.showTextView();
                            ShareSearchAdapter.this.notifyDataSetChanged();
                            ShareAlert.this.checkCurrentList(true);
                        }
                    }
                    ShareAlert.this.recyclerItemsEnterAnimator.showItemsAnimated(i2);
                    ShareSearchAdapter.this.notifyDataSetChanged();
                    ShareAlert.this.checkCurrentList(true);
                }

                public boolean canApplySearchResults(int i) {
                    return i == ShareSearchAdapter.this.lastSearchId;
                }
            });
        }

        private void searchDialogsInternal(String str, int i) {
            MessagesStorage.getInstance(ShareAlert.this.currentAccount).getStorageQueue().postRunnable(new ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda2(this, str, i));
        }

        /* JADX WARNING: type inference failed for: r11v23 */
        /* JADX WARNING: type inference failed for: r11v25 */
        /* JADX WARNING: type inference failed for: r11v29 */
        /* JADX WARNING: type inference failed for: r11v31 */
        /* access modifiers changed from: private */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:173:0x03f8 A[Catch:{ Exception -> 0x0414 }, LOOP:7: B:145:0x0346->B:173:0x03f8, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:190:0x0164 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:211:0x0390 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:75:0x01cd A[Catch:{ Exception -> 0x0414 }, LOOP:2: B:46:0x0111->B:75:0x01cd, LOOP_END] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$searchDialogsInternal$1(java.lang.String r21, int r22) {
            /*
                r20 = this;
                r1 = r20
                java.lang.String r0 = r21.trim()     // Catch:{ Exception -> 0x0414 }
                java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0414 }
                int r2 = r0.length()     // Catch:{ Exception -> 0x0414 }
                r3 = -1
                if (r2 != 0) goto L_0x001e
                r1.lastSearchId = r3     // Catch:{ Exception -> 0x0414 }
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0414 }
                r0.<init>()     // Catch:{ Exception -> 0x0414 }
                int r2 = r1.lastSearchId     // Catch:{ Exception -> 0x0414 }
                r1.updateSearchResults(r0, r2)     // Catch:{ Exception -> 0x0414 }
                return
            L_0x001e:
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0414 }
                java.lang.String r2 = r2.getTranslitString(r0)     // Catch:{ Exception -> 0x0414 }
                boolean r4 = r0.equals(r2)     // Catch:{ Exception -> 0x0414 }
                if (r4 != 0) goto L_0x0032
                int r4 = r2.length()     // Catch:{ Exception -> 0x0414 }
                if (r4 != 0) goto L_0x0033
            L_0x0032:
                r2 = 0
            L_0x0033:
                r4 = 1
                r6 = 0
                if (r2 == 0) goto L_0x0039
                r7 = 1
                goto L_0x003a
            L_0x0039:
                r7 = 0
            L_0x003a:
                int r7 = r7 + r4
                java.lang.String[] r8 = new java.lang.String[r7]     // Catch:{ Exception -> 0x0414 }
                r8[r6] = r0     // Catch:{ Exception -> 0x0414 }
                if (r2 == 0) goto L_0x0043
                r8[r4] = r2     // Catch:{ Exception -> 0x0414 }
            L_0x0043:
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0414 }
                r0.<init>()     // Catch:{ Exception -> 0x0414 }
                java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x0414 }
                r2.<init>()     // Catch:{ Exception -> 0x0414 }
                androidx.collection.LongSparseArray r9 = new androidx.collection.LongSparseArray     // Catch:{ Exception -> 0x0414 }
                r9.<init>()     // Catch:{ Exception -> 0x0414 }
                org.telegram.ui.Components.ShareAlert r10 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x0414 }
                int r10 = r10.currentAccount     // Catch:{ Exception -> 0x0414 }
                org.telegram.messenger.MessagesStorage r10 = org.telegram.messenger.MessagesStorage.getInstance(r10)     // Catch:{ Exception -> 0x0414 }
                org.telegram.SQLite.SQLiteDatabase r10 = r10.getDatabase()     // Catch:{ Exception -> 0x0414 }
                java.lang.String r11 = "SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400"
                java.lang.Object[] r12 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0414 }
                org.telegram.SQLite.SQLiteCursor r10 = r10.queryFinalized(r11, r12)     // Catch:{ Exception -> 0x0414 }
            L_0x0068:
                boolean r11 = r10.next()     // Catch:{ Exception -> 0x0414 }
                if (r11 == 0) goto L_0x00b1
                long r11 = r10.longValue(r6)     // Catch:{ Exception -> 0x0414 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r13 = new org.telegram.ui.Components.ShareAlert$DialogSearchResult     // Catch:{ Exception -> 0x0414 }
                r13.<init>()     // Catch:{ Exception -> 0x0414 }
                int r14 = r10.intValue(r4)     // Catch:{ Exception -> 0x0414 }
                r13.date = r14     // Catch:{ Exception -> 0x0414 }
                r9.put(r11, r13)     // Catch:{ Exception -> 0x0414 }
                boolean r13 = org.telegram.messenger.DialogObject.isUserDialog(r11)     // Catch:{ Exception -> 0x0414 }
                if (r13 == 0) goto L_0x0098
                java.lang.Long r13 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x0414 }
                boolean r13 = r0.contains(r13)     // Catch:{ Exception -> 0x0414 }
                if (r13 != 0) goto L_0x0068
                java.lang.Long r11 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x0414 }
                r0.add(r11)     // Catch:{ Exception -> 0x0414 }
                goto L_0x0068
            L_0x0098:
                boolean r13 = org.telegram.messenger.DialogObject.isChatDialog(r11)     // Catch:{ Exception -> 0x0414 }
                if (r13 == 0) goto L_0x0068
                long r11 = -r11
                java.lang.Long r13 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x0414 }
                boolean r13 = r2.contains(r13)     // Catch:{ Exception -> 0x0414 }
                if (r13 != 0) goto L_0x0068
                java.lang.Long r11 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x0414 }
                r2.add(r11)     // Catch:{ Exception -> 0x0414 }
                goto L_0x0068
            L_0x00b1:
                r10.dispose()     // Catch:{ Exception -> 0x0414 }
                boolean r10 = r0.isEmpty()     // Catch:{ Exception -> 0x0414 }
                java.lang.String r11 = ";;;"
                java.lang.String r12 = ","
                java.lang.String r13 = "@"
                java.lang.String r15 = " "
                if (r10 != 0) goto L_0x01ec
                org.telegram.ui.Components.ShareAlert r10 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x0414 }
                int r10 = r10.currentAccount     // Catch:{ Exception -> 0x0414 }
                org.telegram.messenger.MessagesStorage r10 = org.telegram.messenger.MessagesStorage.getInstance(r10)     // Catch:{ Exception -> 0x0414 }
                org.telegram.SQLite.SQLiteDatabase r10 = r10.getDatabase()     // Catch:{ Exception -> 0x0414 }
                java.util.Locale r5 = java.util.Locale.US     // Catch:{ Exception -> 0x0414 }
                java.lang.String r3 = "SELECT data, status, name FROM users WHERE uid IN(%s)"
                java.lang.Object[] r14 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x0414 }
                java.lang.String r0 = android.text.TextUtils.join(r12, r0)     // Catch:{ Exception -> 0x0414 }
                r14[r6] = r0     // Catch:{ Exception -> 0x0414 }
                java.lang.String r0 = java.lang.String.format(r5, r3, r14)     // Catch:{ Exception -> 0x0414 }
                java.lang.Object[] r3 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0414 }
                org.telegram.SQLite.SQLiteCursor r0 = r10.queryFinalized(r0, r3)     // Catch:{ Exception -> 0x0414 }
                r3 = 0
            L_0x00e7:
                boolean r5 = r0.next()     // Catch:{ Exception -> 0x0414 }
                if (r5 == 0) goto L_0x01e4
                r5 = 2
                java.lang.String r10 = r0.stringValue(r5)     // Catch:{ Exception -> 0x0414 }
                org.telegram.messenger.LocaleController r5 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0414 }
                java.lang.String r5 = r5.getTranslitString(r10)     // Catch:{ Exception -> 0x0414 }
                boolean r14 = r10.equals(r5)     // Catch:{ Exception -> 0x0414 }
                if (r14 == 0) goto L_0x0101
                r5 = 0
            L_0x0101:
                int r14 = r10.lastIndexOf(r11)     // Catch:{ Exception -> 0x0414 }
                r4 = -1
                if (r14 == r4) goto L_0x010f
                int r14 = r14 + 3
                java.lang.String r4 = r10.substring(r14)     // Catch:{ Exception -> 0x0414 }
                goto L_0x0110
            L_0x010f:
                r4 = 0
            L_0x0110:
                r14 = 0
            L_0x0111:
                if (r6 >= r7) goto L_0x01d8
                r17 = r14
                r14 = r8[r6]     // Catch:{ Exception -> 0x0414 }
                boolean r18 = r10.startsWith(r14)     // Catch:{ Exception -> 0x0414 }
                if (r18 != 0) goto L_0x015f
                r18 = r11
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0414 }
                r11.<init>()     // Catch:{ Exception -> 0x0414 }
                r11.append(r15)     // Catch:{ Exception -> 0x0414 }
                r11.append(r14)     // Catch:{ Exception -> 0x0414 }
                java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0414 }
                boolean r11 = r10.contains(r11)     // Catch:{ Exception -> 0x0414 }
                if (r11 != 0) goto L_0x0161
                if (r5 == 0) goto L_0x0152
                boolean r11 = r5.startsWith(r14)     // Catch:{ Exception -> 0x0414 }
                if (r11 != 0) goto L_0x0161
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0414 }
                r11.<init>()     // Catch:{ Exception -> 0x0414 }
                r11.append(r15)     // Catch:{ Exception -> 0x0414 }
                r11.append(r14)     // Catch:{ Exception -> 0x0414 }
                java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0414 }
                boolean r11 = r5.contains(r11)     // Catch:{ Exception -> 0x0414 }
                if (r11 == 0) goto L_0x0152
                goto L_0x0161
            L_0x0152:
                if (r4 == 0) goto L_0x015c
                boolean r11 = r4.startsWith(r14)     // Catch:{ Exception -> 0x0414 }
                if (r11 == 0) goto L_0x015c
                r11 = 2
                goto L_0x0162
            L_0x015c:
                r11 = r17
                goto L_0x0162
            L_0x015f:
                r18 = r11
            L_0x0161:
                r11 = 1
            L_0x0162:
                if (r11 == 0) goto L_0x01cd
                r4 = 0
                org.telegram.tgnet.NativeByteBuffer r5 = r0.byteBufferValue(r4)     // Catch:{ Exception -> 0x0414 }
                if (r5 == 0) goto L_0x01ca
                int r6 = r5.readInt32(r4)     // Catch:{ Exception -> 0x0414 }
                org.telegram.tgnet.TLRPC$User r6 = org.telegram.tgnet.TLRPC$User.TLdeserialize(r5, r6, r4)     // Catch:{ Exception -> 0x0414 }
                r5.reuse()     // Catch:{ Exception -> 0x0414 }
                long r4 = r6.id     // Catch:{ Exception -> 0x0414 }
                java.lang.Object r4 = r9.get(r4)     // Catch:{ Exception -> 0x0414 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r4 = (org.telegram.ui.Components.ShareAlert.DialogSearchResult) r4     // Catch:{ Exception -> 0x0414 }
                org.telegram.tgnet.TLRPC$UserStatus r5 = r6.status     // Catch:{ Exception -> 0x0414 }
                r19 = r9
                r10 = 1
                if (r5 == 0) goto L_0x018b
                int r9 = r0.intValue(r10)     // Catch:{ Exception -> 0x0414 }
                r5.expires = r9     // Catch:{ Exception -> 0x0414 }
            L_0x018b:
                if (r11 != r10) goto L_0x0198
                java.lang.String r5 = r6.first_name     // Catch:{ Exception -> 0x0414 }
                java.lang.String r9 = r6.last_name     // Catch:{ Exception -> 0x0414 }
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r9, r14)     // Catch:{ Exception -> 0x0414 }
                r4.name = r5     // Catch:{ Exception -> 0x0414 }
                goto L_0x01bf
            L_0x0198:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0414 }
                r5.<init>()     // Catch:{ Exception -> 0x0414 }
                r5.append(r13)     // Catch:{ Exception -> 0x0414 }
                java.lang.String r9 = r6.username     // Catch:{ Exception -> 0x0414 }
                r5.append(r9)     // Catch:{ Exception -> 0x0414 }
                java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0414 }
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0414 }
                r9.<init>()     // Catch:{ Exception -> 0x0414 }
                r9.append(r13)     // Catch:{ Exception -> 0x0414 }
                r9.append(r14)     // Catch:{ Exception -> 0x0414 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0414 }
                r10 = 0
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r10, r9)     // Catch:{ Exception -> 0x0414 }
                r4.name = r5     // Catch:{ Exception -> 0x0414 }
            L_0x01bf:
                r4.object = r6     // Catch:{ Exception -> 0x0414 }
                org.telegram.tgnet.TLRPC$Dialog r4 = r4.dialog     // Catch:{ Exception -> 0x0414 }
                long r5 = r6.id     // Catch:{ Exception -> 0x0414 }
                r4.id = r5     // Catch:{ Exception -> 0x0414 }
                int r3 = r3 + 1
                goto L_0x01dc
            L_0x01ca:
                r19 = r9
                goto L_0x01dc
            L_0x01cd:
                r17 = r4
                r19 = r9
                int r6 = r6 + 1
                r14 = r11
                r11 = r18
                goto L_0x0111
            L_0x01d8:
                r19 = r9
                r18 = r11
            L_0x01dc:
                r11 = r18
                r9 = r19
                r4 = 1
                r6 = 0
                goto L_0x00e7
            L_0x01e4:
                r19 = r9
                r18 = r11
                r0.dispose()     // Catch:{ Exception -> 0x0414 }
                goto L_0x01f1
            L_0x01ec:
                r19 = r9
                r18 = r11
                r3 = 0
            L_0x01f1:
                boolean r0 = r2.isEmpty()     // Catch:{ Exception -> 0x0414 }
                if (r0 != 0) goto L_0x02d4
                org.telegram.ui.Components.ShareAlert r0 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x0414 }
                int r0 = r0.currentAccount     // Catch:{ Exception -> 0x0414 }
                org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x0414 }
                org.telegram.SQLite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x0414 }
                java.util.Locale r4 = java.util.Locale.US     // Catch:{ Exception -> 0x0414 }
                java.lang.String r5 = "SELECT data, name FROM chats WHERE uid IN(%s)"
                r6 = 1
                java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0414 }
                java.lang.String r2 = android.text.TextUtils.join(r12, r2)     // Catch:{ Exception -> 0x0414 }
                r6 = 0
                r9[r6] = r2     // Catch:{ Exception -> 0x0414 }
                java.lang.String r2 = java.lang.String.format(r4, r5, r9)     // Catch:{ Exception -> 0x0414 }
                java.lang.Object[] r4 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0414 }
                org.telegram.SQLite.SQLiteCursor r0 = r0.queryFinalized(r2, r4)     // Catch:{ Exception -> 0x0414 }
            L_0x021d:
                boolean r2 = r0.next()     // Catch:{ Exception -> 0x0414 }
                if (r2 == 0) goto L_0x02ce
                r2 = 1
                java.lang.String r4 = r0.stringValue(r2)     // Catch:{ Exception -> 0x0414 }
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0414 }
                java.lang.String r2 = r2.getTranslitString(r4)     // Catch:{ Exception -> 0x0414 }
                boolean r5 = r4.equals(r2)     // Catch:{ Exception -> 0x0414 }
                if (r5 == 0) goto L_0x0237
                r2 = 0
            L_0x0237:
                r5 = 0
            L_0x0238:
                if (r5 >= r7) goto L_0x02c8
                r6 = r8[r5]     // Catch:{ Exception -> 0x0414 }
                boolean r9 = r4.startsWith(r6)     // Catch:{ Exception -> 0x0414 }
                if (r9 != 0) goto L_0x0278
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0414 }
                r9.<init>()     // Catch:{ Exception -> 0x0414 }
                r9.append(r15)     // Catch:{ Exception -> 0x0414 }
                r9.append(r6)     // Catch:{ Exception -> 0x0414 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0414 }
                boolean r9 = r4.contains(r9)     // Catch:{ Exception -> 0x0414 }
                if (r9 != 0) goto L_0x0278
                if (r2 == 0) goto L_0x0275
                boolean r9 = r2.startsWith(r6)     // Catch:{ Exception -> 0x0414 }
                if (r9 != 0) goto L_0x0278
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0414 }
                r9.<init>()     // Catch:{ Exception -> 0x0414 }
                r9.append(r15)     // Catch:{ Exception -> 0x0414 }
                r9.append(r6)     // Catch:{ Exception -> 0x0414 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0414 }
                boolean r9 = r2.contains(r9)     // Catch:{ Exception -> 0x0414 }
                if (r9 == 0) goto L_0x0275
                goto L_0x0278
            L_0x0275:
                int r5 = r5 + 1
                goto L_0x0238
            L_0x0278:
                r2 = 0
                org.telegram.tgnet.NativeByteBuffer r4 = r0.byteBufferValue(r2)     // Catch:{ Exception -> 0x0414 }
                if (r4 == 0) goto L_0x02c8
                int r5 = r4.readInt32(r2)     // Catch:{ Exception -> 0x0414 }
                org.telegram.tgnet.TLRPC$Chat r5 = org.telegram.tgnet.TLRPC$Chat.TLdeserialize(r4, r5, r2)     // Catch:{ Exception -> 0x0414 }
                r4.reuse()     // Catch:{ Exception -> 0x0414 }
                if (r5 == 0) goto L_0x02c8
                boolean r2 = org.telegram.messenger.ChatObject.isNotInChat(r5)     // Catch:{ Exception -> 0x0414 }
                if (r2 != 0) goto L_0x02c8
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r5)     // Catch:{ Exception -> 0x0414 }
                if (r2 == 0) goto L_0x02a8
                boolean r2 = r5.creator     // Catch:{ Exception -> 0x0414 }
                if (r2 != 0) goto L_0x02a8
                org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r5.admin_rights     // Catch:{ Exception -> 0x0414 }
                if (r2 == 0) goto L_0x02a4
                boolean r2 = r2.post_messages     // Catch:{ Exception -> 0x0414 }
                if (r2 != 0) goto L_0x02a8
            L_0x02a4:
                boolean r2 = r5.megagroup     // Catch:{ Exception -> 0x0414 }
                if (r2 == 0) goto L_0x02c8
            L_0x02a8:
                long r9 = r5.id     // Catch:{ Exception -> 0x0414 }
                long r9 = -r9
                r2 = r19
                java.lang.Object r4 = r2.get(r9)     // Catch:{ Exception -> 0x0414 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r4 = (org.telegram.ui.Components.ShareAlert.DialogSearchResult) r4     // Catch:{ Exception -> 0x0414 }
                java.lang.String r9 = r5.title     // Catch:{ Exception -> 0x0414 }
                r10 = 0
                java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.generateSearchName(r9, r10, r6)     // Catch:{ Exception -> 0x0414 }
                r4.name = r6     // Catch:{ Exception -> 0x0414 }
                r4.object = r5     // Catch:{ Exception -> 0x0414 }
                org.telegram.tgnet.TLRPC$Dialog r4 = r4.dialog     // Catch:{ Exception -> 0x0414 }
                long r5 = r5.id     // Catch:{ Exception -> 0x0414 }
                long r5 = -r5
                r4.id = r5     // Catch:{ Exception -> 0x0414 }
                int r3 = r3 + 1
                goto L_0x02ca
            L_0x02c8:
                r2 = r19
            L_0x02ca:
                r19 = r2
                goto L_0x021d
            L_0x02ce:
                r2 = r19
                r0.dispose()     // Catch:{ Exception -> 0x0414 }
                goto L_0x02d6
            L_0x02d4:
                r2 = r19
            L_0x02d6:
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0414 }
                r0.<init>(r3)     // Catch:{ Exception -> 0x0414 }
                r3 = 0
            L_0x02dc:
                int r4 = r2.size()     // Catch:{ Exception -> 0x0414 }
                if (r3 >= r4) goto L_0x02f6
                java.lang.Object r4 = r2.valueAt(r3)     // Catch:{ Exception -> 0x0414 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r4 = (org.telegram.ui.Components.ShareAlert.DialogSearchResult) r4     // Catch:{ Exception -> 0x0414 }
                org.telegram.tgnet.TLObject r5 = r4.object     // Catch:{ Exception -> 0x0414 }
                if (r5 == 0) goto L_0x02f3
                java.lang.CharSequence r5 = r4.name     // Catch:{ Exception -> 0x0414 }
                if (r5 == 0) goto L_0x02f3
                r0.add(r4)     // Catch:{ Exception -> 0x0414 }
            L_0x02f3:
                int r3 = r3 + 1
                goto L_0x02dc
            L_0x02f6:
                org.telegram.ui.Components.ShareAlert r3 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x0414 }
                int r3 = r3.currentAccount     // Catch:{ Exception -> 0x0414 }
                org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r3)     // Catch:{ Exception -> 0x0414 }
                org.telegram.SQLite.SQLiteDatabase r3 = r3.getDatabase()     // Catch:{ Exception -> 0x0414 }
                java.lang.String r4 = "SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid"
                r5 = 0
                java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0414 }
                org.telegram.SQLite.SQLiteCursor r3 = r3.queryFinalized(r4, r6)     // Catch:{ Exception -> 0x0414 }
            L_0x030d:
                boolean r4 = r3.next()     // Catch:{ Exception -> 0x0414 }
                if (r4 == 0) goto L_0x0406
                r4 = 3
                long r4 = r3.longValue(r4)     // Catch:{ Exception -> 0x0414 }
                int r4 = r2.indexOfKey(r4)     // Catch:{ Exception -> 0x0414 }
                if (r4 < 0) goto L_0x031f
                goto L_0x030d
            L_0x031f:
                r5 = 2
                java.lang.String r4 = r3.stringValue(r5)     // Catch:{ Exception -> 0x0414 }
                org.telegram.messenger.LocaleController r6 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0414 }
                java.lang.String r10 = r6.getTranslitString(r4)     // Catch:{ Exception -> 0x0414 }
                boolean r6 = r4.equals(r10)     // Catch:{ Exception -> 0x0414 }
                if (r6 == 0) goto L_0x0333
                r10 = 0
            L_0x0333:
                r6 = r18
                int r9 = r4.lastIndexOf(r6)     // Catch:{ Exception -> 0x0414 }
                r11 = -1
                if (r9 == r11) goto L_0x0343
                int r9 = r9 + 3
                java.lang.String r9 = r4.substring(r9)     // Catch:{ Exception -> 0x0414 }
                goto L_0x0344
            L_0x0343:
                r9 = 0
            L_0x0344:
                r12 = 0
                r14 = 0
            L_0x0346:
                if (r12 >= r7) goto L_0x0400
                r5 = r8[r12]     // Catch:{ Exception -> 0x0414 }
                boolean r16 = r4.startsWith(r5)     // Catch:{ Exception -> 0x0414 }
                if (r16 != 0) goto L_0x038d
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0414 }
                r11.<init>()     // Catch:{ Exception -> 0x0414 }
                r11.append(r15)     // Catch:{ Exception -> 0x0414 }
                r11.append(r5)     // Catch:{ Exception -> 0x0414 }
                java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0414 }
                boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x0414 }
                if (r11 != 0) goto L_0x038d
                if (r10 == 0) goto L_0x0383
                boolean r11 = r10.startsWith(r5)     // Catch:{ Exception -> 0x0414 }
                if (r11 != 0) goto L_0x038d
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0414 }
                r11.<init>()     // Catch:{ Exception -> 0x0414 }
                r11.append(r15)     // Catch:{ Exception -> 0x0414 }
                r11.append(r5)     // Catch:{ Exception -> 0x0414 }
                java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0414 }
                boolean r11 = r10.contains(r11)     // Catch:{ Exception -> 0x0414 }
                if (r11 == 0) goto L_0x0383
                goto L_0x038d
            L_0x0383:
                if (r9 == 0) goto L_0x038e
                boolean r11 = r9.startsWith(r5)     // Catch:{ Exception -> 0x0414 }
                if (r11 == 0) goto L_0x038e
                r14 = 2
                goto L_0x038e
            L_0x038d:
                r14 = 1
            L_0x038e:
                if (r14 == 0) goto L_0x03f8
                r11 = 0
                org.telegram.tgnet.NativeByteBuffer r4 = r3.byteBufferValue(r11)     // Catch:{ Exception -> 0x0414 }
                if (r4 == 0) goto L_0x03f5
                int r9 = r4.readInt32(r11)     // Catch:{ Exception -> 0x0414 }
                org.telegram.tgnet.TLRPC$User r9 = org.telegram.tgnet.TLRPC$User.TLdeserialize(r4, r9, r11)     // Catch:{ Exception -> 0x0414 }
                r4.reuse()     // Catch:{ Exception -> 0x0414 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r4 = new org.telegram.ui.Components.ShareAlert$DialogSearchResult     // Catch:{ Exception -> 0x0414 }
                r4.<init>()     // Catch:{ Exception -> 0x0414 }
                org.telegram.tgnet.TLRPC$UserStatus r10 = r9.status     // Catch:{ Exception -> 0x0414 }
                if (r10 == 0) goto L_0x03b2
                r12 = 1
                int r11 = r3.intValue(r12)     // Catch:{ Exception -> 0x0414 }
                r10.expires = r11     // Catch:{ Exception -> 0x0414 }
            L_0x03b2:
                org.telegram.tgnet.TLRPC$Dialog r10 = r4.dialog     // Catch:{ Exception -> 0x0414 }
                long r11 = r9.id     // Catch:{ Exception -> 0x0414 }
                r10.id = r11     // Catch:{ Exception -> 0x0414 }
                r4.object = r9     // Catch:{ Exception -> 0x0414 }
                r11 = 1
                if (r14 != r11) goto L_0x03c9
                java.lang.String r10 = r9.first_name     // Catch:{ Exception -> 0x0414 }
                java.lang.String r9 = r9.last_name     // Catch:{ Exception -> 0x0414 }
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r10, r9, r5)     // Catch:{ Exception -> 0x0414 }
                r4.name = r5     // Catch:{ Exception -> 0x0414 }
                r10 = 0
                goto L_0x03f0
            L_0x03c9:
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0414 }
                r10.<init>()     // Catch:{ Exception -> 0x0414 }
                r10.append(r13)     // Catch:{ Exception -> 0x0414 }
                java.lang.String r9 = r9.username     // Catch:{ Exception -> 0x0414 }
                r10.append(r9)     // Catch:{ Exception -> 0x0414 }
                java.lang.String r9 = r10.toString()     // Catch:{ Exception -> 0x0414 }
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0414 }
                r10.<init>()     // Catch:{ Exception -> 0x0414 }
                r10.append(r13)     // Catch:{ Exception -> 0x0414 }
                r10.append(r5)     // Catch:{ Exception -> 0x0414 }
                java.lang.String r5 = r10.toString()     // Catch:{ Exception -> 0x0414 }
                r10 = 0
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r9, r10, r5)     // Catch:{ Exception -> 0x0414 }
                r4.name = r5     // Catch:{ Exception -> 0x0414 }
            L_0x03f0:
                r0.add(r4)     // Catch:{ Exception -> 0x0414 }
                r5 = r10
                goto L_0x0402
            L_0x03f5:
                r11 = 1
                r5 = 0
                goto L_0x0402
            L_0x03f8:
                r5 = 0
                r11 = 1
                int r12 = r12 + 1
                r5 = 2
                r11 = -1
                goto L_0x0346
            L_0x0400:
                r5 = 0
                r11 = 1
            L_0x0402:
                r18 = r6
                goto L_0x030d
            L_0x0406:
                r3.dispose()     // Catch:{ Exception -> 0x0414 }
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda4 r2 = org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda4.INSTANCE     // Catch:{ Exception -> 0x0414 }
                java.util.Collections.sort(r0, r2)     // Catch:{ Exception -> 0x0414 }
                r2 = r22
                r1.updateSearchResults(r0, r2)     // Catch:{ Exception -> 0x0414 }
                goto L_0x0418
            L_0x0414:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0418:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.lambda$searchDialogsInternal$1(java.lang.String, int):void");
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ int lambda$searchDialogsInternal$0(Object obj, Object obj2) {
            int i = ((DialogSearchResult) obj).date;
            int i2 = ((DialogSearchResult) obj2).date;
            if (i < i2) {
                return 1;
            }
            return i > i2 ? -1 : 0;
        }

        private void updateSearchResults(ArrayList<Object> arrayList, int i) {
            AndroidUtilities.runOnUIThread(new ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda1(this, i, arrayList));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$2(int i, ArrayList arrayList) {
            if (i == this.lastSearchId) {
                getItemCount();
                boolean z = false;
                this.internalDialogsIsSearching = false;
                this.lastLocalSearchId = i;
                if (this.lastGlobalSearchId != i) {
                    this.searchAdapterHelper.clear();
                }
                if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
                    ShareAlert shareAlert = ShareAlert.this;
                    int unused = shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    ShareAlert.this.searchAdapter.notifyDataSetChanged();
                }
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    TLObject tLObject = ((DialogSearchResult) arrayList.get(i2)).object;
                    if (tLObject instanceof TLRPC$User) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putUser((TLRPC$User) tLObject, true);
                    } else if (tLObject instanceof TLRPC$Chat) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putChat((TLRPC$Chat) tLObject, true);
                    }
                }
                if (!this.searchResult.isEmpty() && arrayList.isEmpty()) {
                    z = true;
                }
                if (this.searchResult.isEmpty()) {
                    boolean isEmpty = arrayList.isEmpty();
                }
                if (z) {
                    ShareAlert shareAlert2 = ShareAlert.this;
                    int unused2 = shareAlert2.topBeforeSwitch = shareAlert2.getCurrentTop();
                }
                this.searchResult = arrayList;
                this.searchAdapterHelper.mergeResults(arrayList);
                int i3 = this.lastItemCont;
                if (getItemCount() != 0 || this.searchAdapterHelper.isSearchInProgress() || this.internalDialogsIsSearching) {
                    ShareAlert.this.recyclerItemsEnterAnimator.showItemsAnimated(i3);
                } else {
                    ShareAlert.this.searchEmptyView.showTextView();
                }
                notifyDataSetChanged();
                ShareAlert.this.checkCurrentList(true);
            }
        }

        public void searchDialogs(String str) {
            if (str == null || !str.equals(this.lastSearchText)) {
                this.lastSearchText = str;
                if (this.searchRunnable != null) {
                    Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                    this.searchRunnable = null;
                }
                Runnable runnable = this.searchRunnable2;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.searchRunnable2 = null;
                }
                this.searchResult.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
                this.searchAdapterHelper.queryServerSearch((String) null, true, true, true, true, false, 0, false, 0, 0);
                notifyDataSetChanged();
                ShareAlert.this.checkCurrentList(true);
                if (TextUtils.isEmpty(str)) {
                    ShareAlert shareAlert = ShareAlert.this;
                    int unused = shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    this.lastSearchId = -1;
                    this.internalDialogsIsSearching = false;
                } else {
                    this.internalDialogsIsSearching = true;
                    int i = this.lastSearchId + 1;
                    this.lastSearchId = i;
                    ShareAlert.this.searchEmptyView.showProgress(false);
                    DispatchQueue dispatchQueue = Utilities.searchQueue;
                    ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda3 shareAlert$ShareSearchAdapter$$ExternalSyntheticLambda3 = new ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda3(this, str, i);
                    this.searchRunnable = shareAlert$ShareSearchAdapter$$ExternalSyntheticLambda3;
                    dispatchQueue.postRunnable(shareAlert$ShareSearchAdapter$$ExternalSyntheticLambda3, 300);
                }
                ShareAlert.this.checkCurrentList(false);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchDialogs$4(String str, int i) {
            this.searchRunnable = null;
            searchDialogsInternal(str, i);
            ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda0 shareAlert$ShareSearchAdapter$$ExternalSyntheticLambda0 = new ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda0(this, i, str);
            this.searchRunnable2 = shareAlert$ShareSearchAdapter$$ExternalSyntheticLambda0;
            AndroidUtilities.runOnUIThread(shareAlert$ShareSearchAdapter$$ExternalSyntheticLambda0);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchDialogs$3(int i, String str) {
            this.searchRunnable2 = null;
            if (i == this.lastSearchId) {
                this.searchAdapterHelper.queryServerSearch(str, true, true, true, true, false, 0, false, 0, i);
            }
        }

        public int getItemCount() {
            this.itemsCount = 0;
            this.hintsCell = -1;
            this.resentTitleCell = -1;
            this.recentDialogsStartRow = -1;
            this.lastFilledItem = -1;
            if (TextUtils.isEmpty(this.lastSearchText)) {
                int i = this.itemsCount;
                int i2 = i + 1;
                this.itemsCount = i2;
                this.firstEmptyViewCell = i;
                this.itemsCount = i2 + 1;
                this.hintsCell = i2;
                if (ShareAlert.this.recentSearchObjects.size() > 0) {
                    int i3 = this.itemsCount;
                    int i4 = i3 + 1;
                    this.itemsCount = i4;
                    this.resentTitleCell = i3;
                    this.recentDialogsStartRow = i4;
                    this.itemsCount = i4 + ShareAlert.this.recentSearchObjects.size();
                }
                int i5 = this.itemsCount;
                int i6 = i5 + 1;
                this.itemsCount = i6;
                this.lastFilledItem = i5;
                this.lastItemCont = i6;
                return i6;
            }
            int i7 = this.itemsCount;
            int i8 = i7 + 1;
            this.itemsCount = i8;
            this.firstEmptyViewCell = i7;
            int size = i8 + this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size();
            this.itemsCount = size;
            if (size == 1) {
                this.firstEmptyViewCell = -1;
                this.itemsCount = 0;
                this.lastItemCont = 0;
                return 0;
            }
            int i9 = size + 1;
            this.itemsCount = i9;
            this.lastFilledItem = size;
            this.lastItemCont = i9;
            return i9;
        }

        public TLRPC$Dialog getItem(int i) {
            int i2 = this.recentDialogsStartRow;
            if (i < i2 || i2 < 0) {
                int i3 = i - 1;
                TLRPC$TL_dialog tLRPC$TL_dialog = null;
                if (i3 < 0) {
                    return null;
                }
                if (i3 < this.searchResult.size()) {
                    return ((DialogSearchResult) this.searchResult.get(i3)).dialog;
                }
                int size = i3 - this.searchResult.size();
                ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
                if (size < localServerSearch.size()) {
                    TLObject tLObject = localServerSearch.get(size);
                    tLRPC$TL_dialog = new TLRPC$TL_dialog();
                    if (tLObject instanceof TLRPC$User) {
                        tLRPC$TL_dialog.id = ((TLRPC$User) tLObject).id;
                    } else {
                        tLRPC$TL_dialog.id = -((TLRPC$Chat) tLObject).id;
                    }
                }
                return tLRPC$TL_dialog;
            }
            TLObject tLObject2 = ((DialogsSearchAdapter.RecentSearchObject) ShareAlert.this.recentSearchObjects.get(i - this.recentDialogsStartRow)).object;
            TLRPC$TL_dialog tLRPC$TL_dialog2 = new TLRPC$TL_dialog();
            if (tLObject2 instanceof TLRPC$User) {
                tLRPC$TL_dialog2.id = ((TLRPC$User) tLObject2).id;
            } else {
                tLRPC$TL_dialog2.id = -((TLRPC$Chat) tLObject2).id;
            }
            return tLRPC$TL_dialog2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return (viewHolder.getItemViewType() == 1 || viewHolder.getItemViewType() == 4) ? false : true;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: org.telegram.ui.Cells.ShareDialogCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v7, resolved type: org.telegram.ui.Cells.ShareDialogCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: org.telegram.ui.Cells.ShareDialogCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: org.telegram.ui.Cells.ShareDialogCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$5} */
        /* JADX WARNING: type inference failed for: r5v3, types: [org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$2, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, org.telegram.ui.Components.RecyclerListView] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r4, int r5) {
            /*
                r3 = this;
                r4 = -1
                if (r5 == 0) goto L_0x0098
                r0 = 2
                r1 = 1
                if (r5 == r0) goto L_0x005b
                r0 = 3
                if (r5 == r0) goto L_0x0041
                r0 = 4
                if (r5 == r0) goto L_0x0039
                android.view.View r5 = new android.view.View
                android.content.Context r0 = r3.context
                r5.<init>(r0)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                org.telegram.ui.Components.ShareAlert r2 = org.telegram.ui.Components.ShareAlert.this
                boolean r2 = r2.darkTheme
                if (r2 == 0) goto L_0x002b
                org.telegram.ui.Components.ShareAlert r2 = org.telegram.ui.Components.ShareAlert.this
                java.lang.String[] r2 = r2.linkToCopy
                r1 = r2[r1]
                if (r1 == 0) goto L_0x002b
                r1 = 1121583104(0x42da0000, float:109.0)
                goto L_0x002d
            L_0x002b:
                r1 = 1113587712(0x42600000, float:56.0)
            L_0x002d:
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r0.<init>((int) r4, (int) r1)
                r5.setLayoutParams(r0)
                goto L_0x00b9
            L_0x0039:
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$5 r5 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$5
                android.content.Context r4 = r3.context
                r5.<init>(r4)
                goto L_0x00b9
            L_0x0041:
                org.telegram.ui.Cells.GraySectionCell r5 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r4 = r3.context
                org.telegram.ui.Components.ShareAlert r0 = org.telegram.ui.Components.ShareAlert.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r0.resourcesProvider
                r5.<init>(r4, r0)
                r4 = 2131627664(0x7f0e0e90, float:1.8882599E38)
                java.lang.String r0 = "Recent"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r0, r4)
                r5.setText(r4)
                goto L_0x00b9
            L_0x005b:
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$2 r5 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$2
                android.content.Context r4 = r3.context
                org.telegram.ui.Components.ShareAlert r0 = org.telegram.ui.Components.ShareAlert.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r0.resourcesProvider
                r5.<init>(r3, r4, r0)
                r4 = 0
                r5.setItemAnimator(r4)
                r5.setLayoutAnimation(r4)
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$3 r4 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$3
                android.content.Context r0 = r3.context
                r4.<init>(r3, r0)
                r0 = 0
                r4.setOrientation(r0)
                r5.setLayoutManager(r4)
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$4 r4 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$4
                android.content.Context r0 = r3.context
                org.telegram.ui.Components.ShareAlert r2 = org.telegram.ui.Components.ShareAlert.this
                int r2 = r2.currentAccount
                r4.<init>(r0, r2, r1)
                r3.categoryAdapter = r4
                r5.setAdapter(r4)
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda5 r4 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda5
                r4.<init>(r3)
                r5.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r4)
                goto L_0x00b9
            L_0x0098:
                org.telegram.ui.Cells.ShareDialogCell r5 = new org.telegram.ui.Cells.ShareDialogCell
                android.content.Context r0 = r3.context
                org.telegram.ui.Components.ShareAlert r1 = org.telegram.ui.Components.ShareAlert.this
                boolean r1 = r1.darkTheme
                org.telegram.ui.Components.ShareAlert r2 = org.telegram.ui.Components.ShareAlert.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r2.resourcesProvider
                r5.<init>(r0, r1, r2)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1 = 1120403456(0x42CLASSNAME, float:100.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r0.<init>((int) r4, (int) r1)
                r5.setLayoutParams(r0)
            L_0x00b9:
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r5)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$5(View view, int i) {
            TLRPC$TL_dialog tLRPC$TL_dialog = new TLRPC$TL_dialog();
            TLRPC$Peer tLRPC$Peer = MediaDataController.getInstance(ShareAlert.this.currentAccount).hints.get(i).peer;
            long j = tLRPC$Peer.user_id;
            if (j == 0) {
                long j2 = tLRPC$Peer.channel_id;
                if (j2 == 0) {
                    j2 = tLRPC$Peer.chat_id;
                    if (j2 == 0) {
                        j = 0;
                    }
                }
                j = -j2;
            }
            tLRPC$TL_dialog.id = j;
            ShareAlert.this.selectDialog((ShareDialogCell) null, tLRPC$TL_dialog);
            ((HintDialogCell) view).setChecked(ShareAlert.this.selectedDialogs.indexOfKey(j) >= 0, true);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v17, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v18, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v20, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v22, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v24, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v26, resolved type: java.lang.String} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r12, int r13) {
            /*
                r11 = this;
                int r0 = r12.getItemViewType()
                if (r0 != 0) goto L_0x013e
                android.view.View r12 = r12.itemView
                org.telegram.ui.Cells.ShareDialogCell r12 = (org.telegram.ui.Cells.ShareDialogCell) r12
                r0 = 0
                r1 = 0
                java.lang.String r3 = r11.lastSearchText
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                r4 = 33
                java.lang.String r5 = "windowBackgroundWhiteBlueText4"
                r6 = 1
                r7 = 0
                r8 = -1
                if (r3 == 0) goto L_0x00b9
                int r3 = r11.recentDialogsStartRow
                if (r3 < 0) goto L_0x00a5
                if (r13 < r3) goto L_0x00a5
                int r13 = r13 - r3
                org.telegram.ui.Components.ShareAlert r3 = org.telegram.ui.Components.ShareAlert.this
                java.util.ArrayList r3 = r3.recentSearchObjects
                java.lang.Object r13 = r3.get(r13)
                org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r13 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r13
                org.telegram.tgnet.TLObject r13 = r13.object
                boolean r3 = r13 instanceof org.telegram.tgnet.TLRPC$User
                if (r3 == 0) goto L_0x0042
                org.telegram.tgnet.TLRPC$User r13 = (org.telegram.tgnet.TLRPC$User) r13
                long r1 = r13.id
                java.lang.String r0 = r13.first_name
                java.lang.String r13 = r13.last_name
                java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r13)
                goto L_0x0074
            L_0x0042:
                boolean r3 = r13 instanceof org.telegram.tgnet.TLRPC$Chat
                if (r3 == 0) goto L_0x004e
                org.telegram.tgnet.TLRPC$Chat r13 = (org.telegram.tgnet.TLRPC$Chat) r13
                long r0 = r13.id
                long r1 = -r0
                java.lang.String r0 = r13.title
                goto L_0x0074
            L_0x004e:
                boolean r3 = r13 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
                if (r3 == 0) goto L_0x0074
                org.telegram.tgnet.TLRPC$TL_encryptedChat r13 = (org.telegram.tgnet.TLRPC$TL_encryptedChat) r13
                org.telegram.ui.Components.ShareAlert r3 = org.telegram.ui.Components.ShareAlert.this
                int r3 = r3.currentAccount
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
                long r9 = r13.user_id
                java.lang.Long r13 = java.lang.Long.valueOf(r9)
                org.telegram.tgnet.TLRPC$User r13 = r3.getUser(r13)
                if (r13 == 0) goto L_0x0074
                long r1 = r13.id
                java.lang.String r0 = r13.first_name
                java.lang.String r13 = r13.last_name
                java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r13)
            L_0x0074:
                org.telegram.ui.Adapters.SearchAdapterHelper r13 = r11.searchAdapterHelper
                java.lang.String r13 = r13.getLastFoundUsername()
                boolean r3 = android.text.TextUtils.isEmpty(r13)
                if (r3 != 0) goto L_0x00a5
                if (r0 == 0) goto L_0x00a5
                java.lang.String r3 = r0.toString()
                int r3 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r3, r13)
                if (r3 == r8) goto L_0x00a5
                android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
                r8.<init>(r0)
                org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable
                org.telegram.ui.Components.ShareAlert r9 = org.telegram.ui.Components.ShareAlert.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r9.resourcesProvider
                r0.<init>(r5, r9)
                int r13 = r13.length()
                int r13 = r13 + r3
                r8.setSpan(r0, r3, r13, r4)
                r0 = r8
            L_0x00a5:
                int r13 = (int) r1
                long r3 = (long) r13
                org.telegram.ui.Components.ShareAlert r13 = org.telegram.ui.Components.ShareAlert.this
                androidx.collection.LongSparseArray r13 = r13.selectedDialogs
                int r13 = r13.indexOfKey(r1)
                if (r13 < 0) goto L_0x00b4
                goto L_0x00b5
            L_0x00b4:
                r6 = 0
            L_0x00b5:
                r12.setDialog(r3, r6, r0)
                return
            L_0x00b9:
                int r13 = r13 + r8
                java.util.ArrayList<java.lang.Object> r0 = r11.searchResult
                int r0 = r0.size()
                if (r13 >= r0) goto L_0x00d1
                java.util.ArrayList<java.lang.Object> r0 = r11.searchResult
                java.lang.Object r13 = r0.get(r13)
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r13 = (org.telegram.ui.Components.ShareAlert.DialogSearchResult) r13
                org.telegram.tgnet.TLRPC$Dialog r0 = r13.dialog
                long r0 = r0.id
                java.lang.CharSequence r13 = r13.name
                goto L_0x012d
            L_0x00d1:
                java.util.ArrayList<java.lang.Object> r0 = r11.searchResult
                int r0 = r0.size()
                int r13 = r13 - r0
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r11.searchAdapterHelper
                java.util.ArrayList r0 = r0.getLocalServerSearch()
                java.lang.Object r13 = r0.get(r13)
                org.telegram.tgnet.TLObject r13 = (org.telegram.tgnet.TLObject) r13
                boolean r0 = r13 instanceof org.telegram.tgnet.TLRPC$User
                if (r0 == 0) goto L_0x00f5
                org.telegram.tgnet.TLRPC$User r13 = (org.telegram.tgnet.TLRPC$User) r13
                long r0 = r13.id
                java.lang.String r2 = r13.first_name
                java.lang.String r13 = r13.last_name
                java.lang.String r13 = org.telegram.messenger.ContactsController.formatName(r2, r13)
                goto L_0x00fc
            L_0x00f5:
                org.telegram.tgnet.TLRPC$Chat r13 = (org.telegram.tgnet.TLRPC$Chat) r13
                long r0 = r13.id
                long r0 = -r0
                java.lang.String r13 = r13.title
            L_0x00fc:
                org.telegram.ui.Adapters.SearchAdapterHelper r2 = r11.searchAdapterHelper
                java.lang.String r2 = r2.getLastFoundUsername()
                boolean r3 = android.text.TextUtils.isEmpty(r2)
                if (r3 != 0) goto L_0x012d
                if (r13 == 0) goto L_0x012d
                java.lang.String r3 = r13.toString()
                int r3 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r3, r2)
                if (r3 == r8) goto L_0x012d
                android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
                r8.<init>(r13)
                org.telegram.ui.Components.ForegroundColorSpanThemable r13 = new org.telegram.ui.Components.ForegroundColorSpanThemable
                org.telegram.ui.Components.ShareAlert r9 = org.telegram.ui.Components.ShareAlert.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r9.resourcesProvider
                r13.<init>(r5, r9)
                int r2 = r2.length()
                int r2 = r2 + r3
                r8.setSpan(r13, r3, r2, r4)
                r13 = r8
            L_0x012d:
                org.telegram.ui.Components.ShareAlert r2 = org.telegram.ui.Components.ShareAlert.this
                androidx.collection.LongSparseArray r2 = r2.selectedDialogs
                int r2 = r2.indexOfKey(r0)
                if (r2 < 0) goto L_0x013a
                goto L_0x013b
            L_0x013a:
                r6 = 0
            L_0x013b:
                r12.setDialog(r0, r6, r13)
            L_0x013e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (i == this.lastFilledItem) {
                return 4;
            }
            if (i == this.firstEmptyViewCell) {
                return 1;
            }
            if (i == this.hintsCell) {
                return 2;
            }
            return i == this.resentTitleCell ? 3 : 0;
        }

        public int getSpanSize(int i, int i2) {
            if (i2 == this.hintsCell || i2 == this.resentTitleCell || i2 == this.firstEmptyViewCell || i2 == this.lastFilledItem) {
                return i;
            }
            return 1;
        }
    }

    /* access modifiers changed from: private */
    public void checkCurrentList(boolean z) {
        boolean z2 = true;
        if (!TextUtils.isEmpty(this.searchView.searchEditText.getText()) || (this.keyboardVisible && this.searchView.searchEditText.hasFocus())) {
            this.updateSearchAdapter = true;
            AndroidUtilities.updateViewVisibilityAnimated(this.gridView, false, 0.98f, true);
            AndroidUtilities.updateViewVisibilityAnimated(this.searchGridView, true);
        } else {
            AndroidUtilities.updateViewVisibilityAnimated(this.gridView, true, 0.98f, true);
            AndroidUtilities.updateViewVisibilityAnimated(this.searchGridView, false);
            z2 = false;
        }
        if (this.searchIsVisible != z2 || z) {
            this.searchIsVisible = z2;
            this.searchAdapter.notifyDataSetChanged();
            this.listAdapter.notifyDataSetChanged();
            if (this.searchIsVisible) {
                if (this.lastOffset == Integer.MAX_VALUE) {
                    ((LinearLayoutManager) this.searchGridView.getLayoutManager()).scrollToPositionWithOffset(0, -this.searchGridView.getPaddingTop());
                } else {
                    ((LinearLayoutManager) this.searchGridView.getLayoutManager()).scrollToPositionWithOffset(0, this.lastOffset - this.searchGridView.getPaddingTop());
                }
                this.searchAdapter.searchDialogs(this.searchView.searchEditText.getText().toString());
            } else if (this.lastOffset == Integer.MAX_VALUE) {
                this.layoutManager.scrollToPositionWithOffset(0, 0);
            } else {
                this.layoutManager.scrollToPositionWithOffset(0, 0);
            }
        }
    }
}

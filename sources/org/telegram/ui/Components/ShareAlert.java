package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import org.telegram.tgnet.TLRPC;
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
    private TLRPC.TL_exportedMessageLink exportedMessageLink;
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
    public LongSparseArray<TLRPC.Dialog> selectedDialogs;
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
        public TLRPC.Dialog dialog = new TLRPC.TL_dialog();
        public CharSequence name;
        public TLObject object;
    }

    public interface ShareAlertDelegate {
        boolean didCopy();

        void didShare();

        /* renamed from: org.telegram.ui.Components.ShareAlert$ShareAlertDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didShare(ShareAlertDelegate _this) {
            }

            public static boolean $default$didCopy(ShareAlertDelegate _this) {
                return false;
            }
        }
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
        private float tabSwitchProgress;

        public SwitchView(Context context) {
            super(context);
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), ShareAlert.this.getThemedColor(ShareAlert.this.darkTheme ? "voipgroup_searchBackground" : "dialogSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 0.0f, 14.0f, 0.0f));
            AnonymousClass1 r0 = new View(context, ShareAlert.this) {
                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    invalidate();
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    int color0 = AndroidUtilities.getOffsetColor(-9057429, -10513163, getTranslationX() / ((float) getMeasuredWidth()), 1.0f);
                    int color1 = AndroidUtilities.getOffsetColor(-11554882, -4629871, getTranslationX() / ((float) getMeasuredWidth()), 1.0f);
                    if (color0 != SwitchView.this.lastColor) {
                        SwitchView switchView = SwitchView.this;
                        LinearGradient linearGradient = r8;
                        LinearGradient linearGradient2 = new LinearGradient(0.0f, 0.0f, (float) getMeasuredWidth(), 0.0f, new int[]{color0, color1}, (float[]) null, Shader.TileMode.CLAMP);
                        LinearGradient unused = switchView.linearGradient = linearGradient;
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
            simpleTextView.setTextColor(ShareAlert.this.getThemedColor("voipgroup_nameText"));
            this.leftTab.setTextSize(13);
            this.leftTab.setLeftDrawable(NUM);
            this.leftTab.setText(LocaleController.getString("VoipGroupInviteCanSpeak", NUM));
            this.leftTab.setGravity(17);
            addView(this.leftTab, LayoutHelper.createFrame(-1, -1.0f, 51, 14.0f, 0.0f, 0.0f, 0.0f));
            this.leftTab.setOnClickListener(new ShareAlert$SwitchView$$ExternalSyntheticLambda0(this));
            SimpleTextView simpleTextView2 = new SimpleTextView(context);
            this.rightTab = simpleTextView2;
            simpleTextView2.setTextColor(ShareAlert.this.getThemedColor("voipgroup_nameText"));
            this.rightTab.setTextSize(13);
            this.rightTab.setLeftDrawable(NUM);
            this.rightTab.setText(LocaleController.getString("VoipGroupInviteListenOnly", NUM));
            this.rightTab.setGravity(17);
            addView(this.rightTab, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 14.0f, 0.0f));
            this.rightTab.setOnClickListener(new ShareAlert$SwitchView$$ExternalSyntheticLambda1(this));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-ShareAlert$SwitchView  reason: not valid java name */
        public /* synthetic */ void m4356lambda$new$0$orgtelegramuiComponentsShareAlert$SwitchView(View v) {
            switchToTab(0);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-Components-ShareAlert$SwitchView  reason: not valid java name */
        public /* synthetic */ void m4357lambda$new$1$orgtelegramuiComponentsShareAlert$SwitchView(View v) {
            switchToTab(1);
        }

        /* access modifiers changed from: protected */
        public void onTabSwitch(int num) {
        }

        private void switchToTab(int tab) {
            if (this.currentTab != tab) {
                this.currentTab = tab;
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
                fArr[0] = this.currentTab == 0 ? 0.0f : (float) this.slidingView.getMeasuredWidth();
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorSet2.playTogether(animatorArr);
                this.animator.setDuration(180);
                this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AnimatorSet unused = SwitchView.this.animator = null;
                    }
                });
                this.animator.start();
                onTabSwitch(this.currentTab);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(28.0f);
            ((FrameLayout.LayoutParams) this.leftTab.getLayoutParams()).width = width / 2;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.rightTab.getLayoutParams();
            layoutParams.width = width / 2;
            layoutParams.leftMargin = (width / 2) + AndroidUtilities.dp(14.0f);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.slidingView.getLayoutParams();
            layoutParams2.width = width / 2;
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.slidingView.setTranslationX(this.currentTab == 0 ? 0.0f : (float) layoutParams2.width);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private class SearchField extends FrameLayout {
        private View backgroundView;
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
                    return ShareAlert.this.getThemedColor(ShareAlert.this.darkTheme ? "voipgroup_searchPlaceholder" : "dialogSearchIcon");
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
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    boolean showed = true;
                    boolean show = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                        showed = false;
                    }
                    if (show != showed) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (show) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(show ? 1.0f : 0.1f);
                        if (!show) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    if (!TextUtils.isEmpty(SearchField.this.searchEditText.getText())) {
                        ShareAlert.this.checkCurrentList(false);
                    }
                    if (ShareAlert.this.updateSearchAdapter) {
                        String text = SearchField.this.searchEditText.getText().toString();
                        if (text.length() != 0) {
                            if (ShareAlert.this.searchEmptyView != null) {
                                ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", NUM));
                            }
                        } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                            int top = ShareAlert.this.getCurrentTop();
                            ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", NUM));
                            ShareAlert.this.searchEmptyView.showTextView();
                            ShareAlert.this.checkCurrentList(false);
                            ShareAlert.this.listAdapter.notifyDataSetChanged();
                            if (top > 0) {
                                ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -top);
                            }
                        }
                        if (ShareAlert.this.searchAdapter != null) {
                            ShareAlert.this.searchAdapter.searchDialogs(text);
                        }
                    }
                }
            });
            this.searchEditText.setOnEditorActionListener(new ShareAlert$SearchField$$ExternalSyntheticLambda1(this));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-ShareAlert$SearchField  reason: not valid java name */
        public /* synthetic */ void m4349lambda$new$0$orgtelegramuiComponentsShareAlert$SearchField(View v) {
            boolean unused = ShareAlert.this.updateSearchAdapter = true;
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-Components-ShareAlert$SearchField  reason: not valid java name */
        public /* synthetic */ boolean m4350lambda$new$1$orgtelegramuiComponentsShareAlert$SearchField(TextView v, int actionId, KeyEvent event) {
            if (event == null) {
                return false;
            }
            if ((event.getAction() != 1 || event.getKeyCode() != 84) && (event.getAction() != 0 || event.getKeyCode() != 66)) {
                return false;
            }
            AndroidUtilities.hideKeyboard(this.searchEditText);
            return false;
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }
    }

    public static ShareAlert createShareAlert(Context context, MessageObject messageObject, String text, boolean channel, String copyLink, boolean fullScreen) {
        ArrayList<MessageObject> arrayList;
        if (messageObject != null) {
            arrayList = new ArrayList<>();
            arrayList.add(messageObject);
        } else {
            arrayList = null;
        }
        return new ShareAlert(context, (ChatActivity) null, arrayList, text, (String) null, channel, copyLink, (String) null, fullScreen, false);
    }

    public ShareAlert(Context context, ArrayList<MessageObject> messages, String text, boolean channel, String copyLink, boolean fullScreen) {
        this(context, messages, text, channel, copyLink, fullScreen, (Theme.ResourcesProvider) null);
    }

    public ShareAlert(Context context, ArrayList<MessageObject> messages, String text, boolean channel, String copyLink, boolean fullScreen, Theme.ResourcesProvider resourcesProvider2) {
        this(context, (ChatActivity) null, messages, text, (String) null, channel, copyLink, (String) null, fullScreen, false, resourcesProvider2);
    }

    public ShareAlert(Context context, ChatActivity fragment, ArrayList<MessageObject> messages, String text, String text2, boolean channel, String copyLink, String copyLink2, boolean fullScreen, boolean forCall) {
        this(context, fragment, messages, text, text2, channel, copyLink, copyLink2, fullScreen, forCall, (Theme.ResourcesProvider) null);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ShareAlert(android.content.Context r33, org.telegram.ui.ChatActivity r34, java.util.ArrayList<org.telegram.messenger.MessageObject> r35, java.lang.String r36, java.lang.String r37, boolean r38, java.lang.String r39, java.lang.String r40, boolean r41, boolean r42, org.telegram.ui.ActionBar.Theme.ResourcesProvider r43) {
        /*
            r32 = this;
            r7 = r32
            r8 = r33
            r9 = r35
            r10 = r38
            r11 = r42
            r12 = r43
            r13 = 1
            r7.<init>(r8, r13, r12)
            r14 = 2
            java.lang.String[] r0 = new java.lang.String[r14]
            r7.sendingText = r0
            android.view.View[] r0 = new android.view.View[r14]
            r7.shadow = r0
            android.animation.AnimatorSet[] r0 = new android.animation.AnimatorSet[r14]
            r7.shadowAnimation = r0
            androidx.collection.LongSparseArray r0 = new androidx.collection.LongSparseArray
            r0.<init>()
            r7.selectedDialogs = r0
            android.graphics.RectF r0 = new android.graphics.RectF
            r0.<init>()
            r7.rect = r0
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>(r13)
            r7.paint = r0
            android.text.TextPaint r0 = new android.text.TextPaint
            r0.<init>(r13)
            r7.textPaint = r0
            java.lang.String[] r0 = new java.lang.String[r14]
            r7.linkToCopy = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.recentSearchObjects = r0
            androidx.collection.LongSparseArray r0 = new androidx.collection.LongSparseArray
            r0.<init>()
            r7.recentSearchObjectsById = r0
            r7.showSendersName = r13
            r0 = 2147483647(0x7fffffff, float:NaN)
            r7.lastOffset = r0
            r7.resourcesProvider = r12
            boolean r0 = r8 instanceof android.app.Activity
            if (r0 == 0) goto L_0x005d
            r0 = r8
            android.app.Activity r0 = (android.app.Activity) r0
            r7.parentActivity = r0
        L_0x005d:
            r7.darkTheme = r11
            r15 = r34
            r7.parentFragment = r15
            android.content.res.Resources r0 = r33.getResources()
            r1 = 2131166129(0x7var_b1, float:1.7946495E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.shadowDrawable = r0
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            boolean r2 = r7.darkTheme
            java.lang.String r3 = "dialogBackground"
            java.lang.String r4 = "voipgroup_inviteMembersBackground"
            if (r2 == 0) goto L_0x0080
            r2 = r4
            goto L_0x0081
        L_0x0080:
            r2 = r3
        L_0x0081:
            int r2 = r7.getThemedColor(r2)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r5)
            r0.setColorFilter(r1)
            r6 = r41
            r7.isFullscreen = r6
            java.lang.String[] r0 = r7.linkToCopy
            r5 = 0
            r0[r5] = r39
            r0[r13] = r40
            r7.sendingMessageObjects = r9
            org.telegram.ui.Components.ShareAlert$ShareSearchAdapter r0 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter
            r0.<init>(r8)
            r7.searchAdapter = r0
            r7.isChannel = r10
            java.lang.String[] r0 = r7.sendingText
            r0[r5] = r36
            r0[r13] = r37
            r7.useSmoothKeyboard = r13
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r7.sendingMessageObjects
            if (r0 == 0) goto L_0x00d6
            r1 = 0
            int r0 = r0.size()
        L_0x00b4:
            if (r1 >= r0) goto L_0x00d6
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r7.sendingMessageObjects
            java.lang.Object r2 = r2.get(r1)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            boolean r16 = r2.isPoll()
            if (r16 == 0) goto L_0x00d2
            boolean r16 = r2.isPublicPoll()
            if (r16 == 0) goto L_0x00cc
            r5 = 2
            goto L_0x00cd
        L_0x00cc:
            r5 = 1
        L_0x00cd:
            r7.hasPoll = r5
            if (r5 != r14) goto L_0x00d2
            goto L_0x00d6
        L_0x00d2:
            int r1 = r1 + 1
            r5 = 0
            goto L_0x00b4
        L_0x00d6:
            if (r10 == 0) goto L_0x0112
            r7.loadingLink = r13
            org.telegram.tgnet.TLRPC$TL_channels_exportMessageLink r0 = new org.telegram.tgnet.TLRPC$TL_channels_exportMessageLink
            r0.<init>()
            r1 = 0
            java.lang.Object r2 = r9.get(r1)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            int r2 = r2.getId()
            r0.id = r2
            int r2 = r7.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Object r5 = r9.get(r1)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r14 = r1.channel_id
            org.telegram.tgnet.TLRPC$InputChannel r1 = r2.getInputChannel((long) r14)
            r0.channel = r1
            int r1 = r7.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda2
            r2.<init>(r7, r8)
            r1.sendRequest(r0, r2)
        L_0x0112:
            org.telegram.ui.Components.ShareAlert$1 r0 = new org.telegram.ui.Components.ShareAlert$1
            r0.<init>(r8)
            r14 = r0
            r7.containerView = r14
            android.view.ViewGroup r0 = r7.containerView
            r1 = 0
            r0.setWillNotDraw(r1)
            android.view.ViewGroup r0 = r7.containerView
            r0.setClipChildren(r1)
            android.view.ViewGroup r0 = r7.containerView
            int r2 = r7.backgroundPaddingLeft
            int r5 = r7.backgroundPaddingLeft
            r0.setPadding(r2, r1, r5, r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.frameLayout = r0
            boolean r1 = r7.darkTheme
            if (r1 == 0) goto L_0x013b
            r1 = r4
            goto L_0x013c
        L_0x013b:
            r1 = r3
        L_0x013c:
            int r1 = r7.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            boolean r0 = r7.darkTheme
            if (r0 == 0) goto L_0x016b
            java.lang.String[] r0 = r7.linkToCopy
            r0 = r0[r13]
            if (r0 == 0) goto L_0x016b
            org.telegram.ui.Components.ShareAlert$2 r0 = new org.telegram.ui.Components.ShareAlert$2
            r0.<init>(r8)
            r7.switchView = r0
            android.widget.FrameLayout r1 = r7.frameLayout
            r17 = -1
            r18 = 1108344832(0x42100000, float:36.0)
            r19 = 51
            r20 = 0
            r21 = 1093664768(0x41300000, float:11.0)
            r22 = 0
            r23 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r1.addView(r0, r2)
        L_0x016b:
            org.telegram.ui.Components.ShareAlert$SearchField r0 = new org.telegram.ui.Components.ShareAlert$SearchField
            r0.<init>(r8)
            r7.searchView = r0
            android.widget.FrameLayout r1 = r7.frameLayout
            r2 = -1
            r5 = 58
            r15 = 83
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r5, (int) r15)
            r1.addView(r0, r13)
            org.telegram.ui.Components.ShareAlert$3 r0 = new org.telegram.ui.Components.ShareAlert$3
            r0.<init>(r8, r12)
            r7.gridView = r0
            r1 = 0
            r0.setSelectorDrawableColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            r13 = 1111490560(0x42400000, float:48.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r0.setPadding(r1, r1, r1, r5)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            r0.setClipToPadding(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            androidx.recyclerview.widget.GridLayoutManager r1 = new androidx.recyclerview.widget.GridLayoutManager
            android.content.Context r5 = r32.getContext()
            r19 = r4
            r4 = 4
            r1.<init>(r5, r4)
            r7.layoutManager = r1
            r0.setLayoutManager(r1)
            androidx.recyclerview.widget.GridLayoutManager r0 = r7.layoutManager
            org.telegram.ui.Components.ShareAlert$4 r1 = new org.telegram.ui.Components.ShareAlert$4
            r1.<init>()
            r0.setSpanSizeLookup(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            r1 = 0
            r0.setHorizontalScrollBarEnabled(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            r0.setVerticalScrollBarEnabled(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            org.telegram.ui.Components.ShareAlert$5 r1 = new org.telegram.ui.Components.ShareAlert$5
            r1.<init>()
            r0.addItemDecoration(r1)
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.Components.RecyclerListView r1 = r7.gridView
            r20 = -1
            r21 = -1082130432(0xffffffffbvar_, float:-1.0)
            r22 = 51
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r1, r5)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            org.telegram.ui.Components.ShareAlert$ShareDialogsAdapter r1 = new org.telegram.ui.Components.ShareAlert$ShareDialogsAdapter
            r1.<init>(r8)
            r7.listAdapter = r1
            r0.setAdapter(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            boolean r1 = r7.darkTheme
            java.lang.String r5 = "dialogScrollGlow"
            if (r1 == 0) goto L_0x01fd
            r1 = r19
            goto L_0x01fe
        L_0x01fd:
            r1 = r5
        L_0x01fe:
            int r1 = r7.getThemedColor(r1)
            r0.setGlowColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda5 r1 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda5
            r1.<init>(r7)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            org.telegram.ui.Components.ShareAlert$6 r1 = new org.telegram.ui.Components.ShareAlert$6
            r1.<init>()
            r0.setOnScrollListener(r1)
            org.telegram.ui.Components.ShareAlert$7 r0 = new org.telegram.ui.Components.ShareAlert$7
            r0.<init>(r8, r12)
            r7.searchGridView = r0
            r1 = 0
            r0.setSelectorDrawableColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r0.setPadding(r1, r1, r1, r15)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            r0.setClipToPadding(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            org.telegram.ui.Components.FillLastGridLayoutManager r15 = new org.telegram.ui.Components.FillLastGridLayoutManager
            android.content.Context r13 = r32.getContext()
            org.telegram.ui.Components.RecyclerListView r2 = r7.searchGridView
            r15.<init>(r13, r4, r1, r2)
            r7.searchLayoutManager = r15
            r0.setLayoutManager(r15)
            org.telegram.ui.Components.FillLastGridLayoutManager r0 = r7.searchLayoutManager
            org.telegram.ui.Components.ShareAlert$8 r1 = new org.telegram.ui.Components.ShareAlert$8
            r1.<init>()
            r0.setSpanSizeLookup(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda6 r1 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda6
            r1.<init>(r7)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            r1 = 1
            r0.setHasFixedSize(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            r1 = 0
            r0.setItemAnimator(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            r1 = 0
            r0.setHorizontalScrollBarEnabled(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            r0.setVerticalScrollBarEnabled(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            org.telegram.ui.Components.ShareAlert$9 r1 = new org.telegram.ui.Components.ShareAlert$9
            r1.<init>()
            r0.setOnScrollListener(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            org.telegram.ui.Components.ShareAlert$10 r1 = new org.telegram.ui.Components.ShareAlert$10
            r1.<init>()
            r0.addItemDecoration(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            org.telegram.ui.Components.ShareAlert$ShareSearchAdapter r1 = r7.searchAdapter
            r0.setAdapter(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            boolean r1 = r7.darkTheme
            if (r1 == 0) goto L_0x0292
            r5 = r19
        L_0x0292:
            int r1 = r7.getThemedColor(r5)
            r0.setGlowColor(r1)
            org.telegram.ui.Components.RecyclerItemsEnterAnimator r0 = new org.telegram.ui.Components.RecyclerItemsEnterAnimator
            org.telegram.ui.Components.RecyclerListView r1 = r7.searchGridView
            r2 = 1
            r0.<init>(r1, r2)
            r7.recyclerItemsEnterAnimator = r0
            org.telegram.ui.Components.FlickerLoadingView r0 = new org.telegram.ui.Components.FlickerLoadingView
            r0.<init>(r8, r12)
            r13 = r0
            r0 = 12
            r13.setViewType(r0)
            org.telegram.ui.Components.EmptyTextProgressView r0 = new org.telegram.ui.Components.EmptyTextProgressView
            r0.<init>(r8, r13, r12)
            r7.searchEmptyView = r0
            r1 = 1
            r0.setShowAtCenter(r1)
            org.telegram.ui.Components.EmptyTextProgressView r0 = r7.searchEmptyView
            r0.showTextView()
            org.telegram.ui.Components.EmptyTextProgressView r0 = r7.searchEmptyView
            r1 = 2131626658(0x7f0e0aa2, float:1.8880558E38)
            java.lang.String r2 = "NoChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            org.telegram.ui.Components.EmptyTextProgressView r1 = r7.searchEmptyView
            r0.setEmptyView(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            r1 = 0
            r0.setHideIfEmpty(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.searchGridView
            r2 = 1
            r0.setAnimateEmptyView(r2, r1)
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.Components.EmptyTextProgressView r1 = r7.searchEmptyView
            r23 = -1
            r24 = -1082130432(0xffffffffbvar_, float:-1.0)
            r25 = 51
            r26 = 0
            r27 = 1112539136(0x42500000, float:52.0)
            r28 = 0
            r29 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r0.addView(r1, r2)
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.Components.RecyclerListView r1 = r7.searchGridView
            r27 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r0.addView(r1, r2)
            android.widget.FrameLayout$LayoutParams r0 = new android.widget.FrameLayout$LayoutParams
            int r1 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r2 = 51
            r5 = -1
            r0.<init>(r5, r1, r2)
            boolean r1 = r7.darkTheme
            if (r1 == 0) goto L_0x031f
            java.lang.String[] r1 = r7.linkToCopy
            r5 = 1
            r1 = r1[r5]
            if (r1 == 0) goto L_0x031f
            r1 = 1121845248(0x42de0000, float:111.0)
            goto L_0x0321
        L_0x031f:
            r1 = 1114112000(0x42680000, float:58.0)
        L_0x0321:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.topMargin = r1
            android.view.View[] r1 = r7.shadow
            android.view.View r5 = new android.view.View
            r5.<init>(r8)
            r15 = 0
            r1[r15] = r5
            android.view.View[] r1 = r7.shadow
            r1 = r1[r15]
            java.lang.String r5 = "dialogShadowLine"
            int r4 = r7.getThemedColor(r5)
            r1.setBackgroundColor(r4)
            android.view.View[] r1 = r7.shadow
            r1 = r1[r15]
            r4 = 0
            r1.setAlpha(r4)
            android.view.View[] r1 = r7.shadow
            r1 = r1[r15]
            r16 = 1
            java.lang.Integer r4 = java.lang.Integer.valueOf(r16)
            r1.setTag(r4)
            android.view.ViewGroup r1 = r7.containerView
            android.view.View[] r4 = r7.shadow
            r4 = r4[r15]
            r1.addView(r4, r0)
            android.view.ViewGroup r1 = r7.containerView
            android.widget.FrameLayout r4 = r7.frameLayout
            boolean r15 = r7.darkTheme
            if (r15 == 0) goto L_0x036f
            java.lang.String[] r15 = r7.linkToCopy
            r17 = 1
            r15 = r15[r17]
            if (r15 == 0) goto L_0x036f
            r15 = 111(0x6f, float:1.56E-43)
            goto L_0x0371
        L_0x036f:
            r15 = 58
        L_0x0371:
            r18 = r0
            r0 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r0, (int) r15, (int) r2)
            r1.addView(r4, r2)
            android.widget.FrameLayout$LayoutParams r1 = new android.widget.FrameLayout$LayoutParams
            int r2 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r4 = 83
            r1.<init>(r0, r2, r4)
            r15 = r1
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r15.bottomMargin = r0
            android.view.View[] r0 = r7.shadow
            android.view.View r1 = new android.view.View
            r1.<init>(r8)
            r2 = 1
            r0[r2] = r1
            android.view.View[] r0 = r7.shadow
            r0 = r0[r2]
            int r1 = r7.getThemedColor(r5)
            r0.setBackgroundColor(r1)
            android.view.ViewGroup r0 = r7.containerView
            android.view.View[] r1 = r7.shadow
            r1 = r1[r2]
            r0.addView(r1, r15)
            boolean r0 = r7.isChannel
            java.lang.String r18 = "fonts/rmedium.ttf"
            if (r0 != 0) goto L_0x03c6
            java.lang.String[] r0 = r7.linkToCopy
            r1 = 0
            r0 = r0[r1]
            if (r0 == 0) goto L_0x03bb
            goto L_0x03c6
        L_0x03bb:
            android.view.View[] r0 = r7.shadow
            r1 = 1
            r0 = r0[r1]
            r1 = 0
            r0.setAlpha(r1)
            goto L_0x0584
        L_0x03c6:
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r8)
            r7.pickerBottomLayout = r0
            boolean r1 = r7.darkTheme
            if (r1 == 0) goto L_0x03d3
            r3 = r19
        L_0x03d3:
            int r1 = r7.getThemedColor(r3)
            boolean r2 = r7.darkTheme
            java.lang.String r3 = "voipgroup_listSelector"
            java.lang.String r4 = "listSelectorSDK21"
            if (r2 == 0) goto L_0x03e1
            r2 = r3
            goto L_0x03e2
        L_0x03e1:
            r2 = r4
        L_0x03e2:
            int r2 = r7.getThemedColor(r2)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorWithBackgroundDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            android.widget.TextView r0 = r7.pickerBottomLayout
            boolean r1 = r7.darkTheme
            java.lang.String r2 = "voipgroup_listeningText"
            java.lang.String r5 = "dialogTextBlue2"
            if (r1 == 0) goto L_0x03f9
            r1 = r2
            goto L_0x03fa
        L_0x03f9:
            r1 = r5
        L_0x03fa:
            int r1 = r7.getThemedColor(r1)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r7.pickerBottomLayout
            r1 = 1096810496(0x41600000, float:14.0)
            r19 = r2
            r2 = 1
            r0.setTextSize(r2, r1)
            android.widget.TextView r0 = r7.pickerBottomLayout
            r1 = 1099956224(0x41900000, float:18.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 1099956224(0x41900000, float:18.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r21 = r3
            r3 = 0
            r0.setPadding(r1, r3, r2, r3)
            android.widget.TextView r0 = r7.pickerBottomLayout
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r18)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r7.pickerBottomLayout
            r1 = 17
            r0.setGravity(r1)
            boolean r0 = r7.darkTheme
            if (r0 == 0) goto L_0x044d
            java.lang.String[] r0 = r7.linkToCopy
            r1 = 1
            r0 = r0[r1]
            if (r0 == 0) goto L_0x044d
            android.widget.TextView r0 = r7.pickerBottomLayout
            r1 = 2131628811(0x7f0e130b, float:1.8884925E38)
            java.lang.String r2 = "VoipGroupCopySpeakerLink"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
            goto L_0x045f
        L_0x044d:
            android.widget.TextView r0 = r7.pickerBottomLayout
            r1 = 2131625165(0x7f0e04cd, float:1.887753E38)
            java.lang.String r2 = "CopyLink"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
        L_0x045f:
            android.widget.TextView r0 = r7.pickerBottomLayout
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda0 r1 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda0
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            android.view.ViewGroup r0 = r7.containerView
            android.widget.TextView r1 = r7.pickerBottomLayout
            r2 = 48
            r25 = r4
            r3 = 83
            r4 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r2, (int) r3)
            r0.addView(r1, r2)
            org.telegram.ui.ChatActivity r0 = r7.parentFragment
            if (r0 == 0) goto L_0x0584
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getCurrentChat()
            boolean r0 = org.telegram.messenger.ChatObject.hasAdminRights(r0)
            if (r0 == 0) goto L_0x0584
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r7.sendingMessageObjects
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x0584
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r7.sendingMessageObjects
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            int r0 = r0.forwards
            if (r0 <= 0) goto L_0x0584
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r7.sendingMessageObjects
            java.lang.Object r0 = r0.get(r1)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            boolean r2 = r0.isForwarded()
            if (r2 != 0) goto L_0x0584
            android.widget.LinearLayout r2 = new android.widget.LinearLayout
            r2.<init>(r8)
            r7.sharesCountLayout = r2
            r2.setOrientation(r1)
            android.widget.LinearLayout r1 = r7.sharesCountLayout
            r2 = 16
            r1.setGravity(r2)
            android.widget.LinearLayout r1 = r7.sharesCountLayout
            boolean r2 = r7.darkTheme
            if (r2 == 0) goto L_0x04c8
            r3 = r21
            goto L_0x04ca
        L_0x04c8:
            r3 = r25
        L_0x04ca:
            int r2 = r7.getThemedColor(r3)
            r3 = 2
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r3)
            r1.setBackgroundDrawable(r2)
            android.view.ViewGroup r1 = r7.containerView
            android.widget.LinearLayout r2 = r7.sharesCountLayout
            r25 = -2
            r26 = 1111490560(0x42400000, float:48.0)
            r27 = 85
            r28 = 1086324736(0x40CLASSNAME, float:6.0)
            r29 = 0
            r30 = -1061158912(0xffffffffc0CLASSNAME, float:-6.0)
            r31 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r1.addView(r2, r3)
            android.widget.LinearLayout r1 = r7.sharesCountLayout
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda10 r2 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda10
            r2.<init>(r7, r0)
            r1.setOnClickListener(r2)
            android.widget.ImageView r1 = new android.widget.ImageView
            r1.<init>(r8)
            r2 = 2131166127(0x7var_af, float:1.794649E38)
            r1.setImageResource(r2)
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            boolean r3 = r7.darkTheme
            if (r3 == 0) goto L_0x050d
            r3 = r19
            goto L_0x050e
        L_0x050d:
            r3 = r5
        L_0x050e:
            int r3 = r7.getThemedColor(r3)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r4)
            r1.setColorFilter(r2)
            android.widget.LinearLayout r2 = r7.sharesCountLayout
            r25 = -2
            r26 = -1
            r27 = 16
            r28 = 20
            r29 = 0
            r30 = 0
            r31 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30, (int) r31)
            r2.addView(r1, r3)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r8)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            int r3 = r3.forwards
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r16 = 0
            r4[r16] = r3
            java.lang.String r3 = "%d"
            java.lang.String r3 = java.lang.String.format(r3, r4)
            r2.setText(r3)
            r3 = 1096810496(0x41600000, float:14.0)
            r4 = 1
            r2.setTextSize(r4, r3)
            boolean r3 = r7.darkTheme
            if (r3 == 0) goto L_0x055a
            r5 = r19
        L_0x055a:
            int r3 = r7.getThemedColor(r5)
            r2.setTextColor(r3)
            r3 = 16
            r2.setGravity(r3)
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r18)
            r2.setTypeface(r3)
            android.widget.LinearLayout r3 = r7.sharesCountLayout
            r25 = -2
            r26 = -1
            r27 = 16
            r28 = 8
            r29 = 0
            r30 = 20
            r31 = 0
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30, (int) r31)
            r3.addView(r2, r4)
        L_0x0584:
            org.telegram.ui.Components.ShareAlert$11 r0 = new org.telegram.ui.Components.ShareAlert$11
            r0.<init>(r8)
            r7.frameLayout2 = r0
            r5 = 0
            r0.setWillNotDraw(r5)
            android.widget.FrameLayout r0 = r7.frameLayout2
            r4 = 0
            r0.setAlpha(r4)
            android.widget.FrameLayout r0 = r7.frameLayout2
            r3 = 4
            r0.setVisibility(r3)
            android.view.ViewGroup r0 = r7.containerView
            android.widget.FrameLayout r1 = r7.frameLayout2
            r2 = -2
            r3 = 83
            r4 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r2, (int) r3)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = r7.frameLayout2
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda14 r1 = org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda14.INSTANCE
            r0.setOnTouchListener(r1)
            org.telegram.ui.Components.ShareAlert$12 r4 = new org.telegram.ui.Components.ShareAlert$12
            r16 = 0
            r19 = 1
            r0 = r4
            r1 = r32
            r2 = r33
            r20 = 4
            r3 = r14
            r9 = r4
            r10 = 4
            r4 = r16
            r10 = 0
            r5 = r19
            r6 = r43
            r0.<init>(r2, r3, r4, r5, r6)
            r7.commentTextView = r9
            if (r11 == 0) goto L_0x05dc
            org.telegram.ui.Components.EditTextCaption r0 = r9.getEditText()
            java.lang.String r1 = "voipgroup_nameText"
            int r1 = r7.getThemedColor(r1)
            r0.setTextColor(r1)
        L_0x05dc:
            org.telegram.ui.Components.EditTextEmoji r0 = r7.commentTextView
            r1 = 2131628058(0x7f0e101a, float:1.8883398E38)
            java.lang.String r2 = "ShareComment"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setHint(r1)
            org.telegram.ui.Components.EditTextEmoji r0 = r7.commentTextView
            r0.onResume()
            android.widget.FrameLayout r0 = r7.frameLayout2
            org.telegram.ui.Components.EditTextEmoji r1 = r7.commentTextView
            r25 = -1
            r26 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r27 = 51
            r28 = 0
            r29 = 0
            r30 = 1118306304(0x42a80000, float:84.0)
            r31 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = r7.frameLayout2
            r0.setClipChildren(r10)
            org.telegram.ui.Components.EditTextEmoji r0 = r7.commentTextView
            r0.setClipChildren(r10)
            org.telegram.ui.Components.ShareAlert$13 r0 = new org.telegram.ui.Components.ShareAlert$13
            r0.<init>(r8)
            r7.writeButtonContainer = r0
            r1 = 1
            r0.setFocusable(r1)
            android.widget.FrameLayout r0 = r7.writeButtonContainer
            r0.setFocusableInTouchMode(r1)
            android.widget.FrameLayout r0 = r7.writeButtonContainer
            r1 = 4
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r7.writeButtonContainer
            r1 = 1045220557(0x3e4ccccd, float:0.2)
            r0.setScaleX(r1)
            android.widget.FrameLayout r0 = r7.writeButtonContainer
            r0.setScaleY(r1)
            android.widget.FrameLayout r0 = r7.writeButtonContainer
            r2 = 0
            r0.setAlpha(r2)
            android.view.ViewGroup r0 = r7.containerView
            android.widget.FrameLayout r2 = r7.writeButtonContainer
            r25 = 60
            r26 = 1114636288(0x42700000, float:60.0)
            r27 = 85
            r30 = 1086324736(0x40CLASSNAME, float:6.0)
            r31 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r0.addView(r2, r3)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r2 = 1113587712(0x42600000, float:56.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            java.lang.String r4 = "dialogFloatingButton"
            int r4 = r7.getThemedColor(r4)
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 21
            if (r5 < r6) goto L_0x066a
            java.lang.String r5 = "dialogFloatingButtonPressed"
            goto L_0x066c
        L_0x066a:
            java.lang.String r5 = "dialogFloatingButton"
        L_0x066c:
            int r5 = r7.getThemedColor(r5)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r3, r4, r5)
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r6) goto L_0x06a4
            android.content.res.Resources r4 = r33.getResources()
            r5 = 2131165452(0x7var_c, float:1.7945122E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r4 = r4.mutate()
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r1 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r9, r1)
            r4.setColorFilter(r5)
            org.telegram.ui.Components.CombinedDrawable r1 = new org.telegram.ui.Components.CombinedDrawable
            r1.<init>(r4, r3, r10, r10)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setIconSize(r5, r9)
            r3 = r1
        L_0x06a4:
            r0.setBackgroundDrawable(r3)
            r1 = 2131165282(0x7var_, float:1.7944777E38)
            r0.setImageResource(r1)
            r1 = 2
            r0.setImportantForAccessibility(r1)
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r4 = "dialogFloatingIcon"
            int r4 = r7.getThemedColor(r4)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r4, r5)
            r0.setColorFilter(r1)
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r6) goto L_0x06d2
            org.telegram.ui.Components.ShareAlert$14 r1 = new org.telegram.ui.Components.ShareAlert$14
            r1.<init>()
            r0.setOutlineProvider(r1)
        L_0x06d2:
            android.widget.FrameLayout r1 = r7.writeButtonContainer
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r6) goto L_0x06dd
            r4 = 56
            r25 = 56
            goto L_0x06e1
        L_0x06dd:
            r4 = 60
            r25 = 60
        L_0x06e1:
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r6) goto L_0x06e8
            r26 = 1113587712(0x42600000, float:56.0)
            goto L_0x06ec
        L_0x06e8:
            r2 = 1114636288(0x42700000, float:60.0)
            r26 = 1114636288(0x42700000, float:60.0)
        L_0x06ec:
            r27 = 51
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r6) goto L_0x06f7
            r4 = 1073741824(0x40000000, float:2.0)
            r28 = 1073741824(0x40000000, float:2.0)
            goto L_0x06f9
        L_0x06f7:
            r28 = 0
        L_0x06f9:
            r29 = 0
            r30 = 0
            r31 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r1.addView(r0, r2)
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda7 r1 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda7
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda13 r1 = new org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda13
            r1.<init>(r7, r0)
            r0.setOnLongClickListener(r1)
            android.text.TextPaint r1 = r7.textPaint
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r1.setTextSize(r2)
            android.text.TextPaint r1 = r7.textPaint
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r18)
            r1.setTypeface(r2)
            org.telegram.ui.Components.ShareAlert$15 r1 = new org.telegram.ui.Components.ShareAlert$15
            r1.<init>(r8)
            r7.selectedCountView = r1
            r2 = 0
            r1.setAlpha(r2)
            android.view.View r1 = r7.selectedCountView
            r2 = 1045220557(0x3e4ccccd, float:0.2)
            r1.setScaleX(r2)
            android.view.View r1 = r7.selectedCountView
            r1.setScaleY(r2)
            android.view.ViewGroup r1 = r7.containerView
            android.view.View r2 = r7.selectedCountView
            r18 = 42
            r19 = 1103101952(0x41CLASSNAME, float:24.0)
            r20 = 85
            r21 = 0
            r22 = 0
            r23 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r24 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r1.addView(r2, r4)
            r7.updateSelectedCount(r10)
            int r1 = r7.currentAccount
            org.telegram.messenger.AccountInstance r1 = org.telegram.messenger.AccountInstance.getInstance(r1)
            org.telegram.ui.DialogsActivity.loadDialogs(r1)
            org.telegram.ui.Components.ShareAlert$ShareDialogsAdapter r1 = r7.listAdapter
            java.util.ArrayList r1 = r1.dialogs
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x077f
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            r1.addObserver(r7, r2)
        L_0x077f:
            int r1 = r7.currentAccount
            org.telegram.ui.Components.ShareAlert$16 r2 = new org.telegram.ui.Components.ShareAlert$16
            r2.<init>()
            org.telegram.ui.Adapters.DialogsSearchAdapter.loadRecentSearch(r1, r10, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r2 = 1
            r1.loadHints(r2)
            org.telegram.ui.Components.RecyclerListView r1 = r7.gridView
            r4 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r1, r2, r4, r10)
            org.telegram.ui.Components.RecyclerListView r1 = r7.searchGridView
            r2 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r1, r10, r2, r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.<init>(android.content.Context, org.telegram.ui.ChatActivity, java.util.ArrayList, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String, boolean, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ void m4335lambda$new$1$orgtelegramuiComponentsShareAlert(Context context, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ShareAlert$$ExternalSyntheticLambda1(this, response, context));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ void m4334lambda$new$0$orgtelegramuiComponentsShareAlert(TLObject response, Context context) {
        if (response != null) {
            this.exportedMessageLink = (TLRPC.TL_exportedMessageLink) response;
            if (this.copyLinkOnEnd) {
                copyLink(context);
            }
        }
        this.loadingLink = false;
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ void m4336lambda$new$2$orgtelegramuiComponentsShareAlert(View view, int position) {
        TLRPC.Dialog dialog;
        if (position >= 0 && (dialog = this.listAdapter.getItem(position)) != null) {
            selectDialog((ShareDialogCell) view, dialog);
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ void m4337lambda$new$3$orgtelegramuiComponentsShareAlert(View view, int position) {
        TLRPC.Dialog dialog;
        if (position >= 0 && (dialog = this.searchAdapter.getItem(position)) != null) {
            selectDialog((ShareDialogCell) view, dialog);
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ void m4338lambda$new$4$orgtelegramuiComponentsShareAlert(View v) {
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

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ void m4339lambda$new$5$orgtelegramuiComponentsShareAlert(MessageObject messageObject, View view) {
        this.parentFragment.presentFragment(new MessageStatisticActivity(messageObject));
    }

    static /* synthetic */ boolean lambda$new$6(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ void m4340lambda$new$7$orgtelegramuiComponentsShareAlert(View v) {
        sendInternal(true);
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ boolean m4341lambda$new$8$orgtelegramuiComponentsShareAlert(ImageView writeButton, View v) {
        onSendLongClick(writeButton);
        return true;
    }

    /* access modifiers changed from: private */
    public void selectDialog(ShareDialogCell cell, TLRPC.Dialog dialog) {
        if (this.hasPoll != 0 && DialogObject.isChatDialog(dialog.id)) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialog.id));
            boolean isChannel2 = ChatObject.isChannel(chat) && this.hasPoll == 2 && !chat.megagroup;
            if (isChannel2 || !ChatObject.canSendPolls(chat)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), this.resourcesProvider);
                builder.setTitle(LocaleController.getString("SendMessageTitle", NUM));
                if (isChannel2) {
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
        if (this.selectedDialogs.indexOfKey(dialog.id) >= 0) {
            this.selectedDialogs.remove(dialog.id);
            if (cell != null) {
                cell.setChecked(false, true);
            }
            updateSelectedCount(1);
        } else {
            this.selectedDialogs.put(dialog.id, dialog);
            if (cell != null) {
                cell.setChecked(true, true);
            }
            updateSelectedCount(2);
            long selfUserId = UserConfig.getInstance(this.currentAccount).clientUserId;
            if (this.searchIsVisible) {
                TLRPC.Dialog existingDialog = (TLRPC.Dialog) this.listAdapter.dialogsMap.get(dialog.id);
                if (existingDialog == null) {
                    this.listAdapter.dialogsMap.put(dialog.id, dialog);
                    this.listAdapter.dialogs.add(true ^ this.listAdapter.dialogs.isEmpty() ? 1 : 0, dialog);
                } else if (existingDialog.id != selfUserId) {
                    this.listAdapter.dialogs.remove(existingDialog);
                    this.listAdapter.dialogs.add(true ^ this.listAdapter.dialogs.isEmpty() ? 1 : 0, existingDialog);
                }
                this.listAdapter.notifyDataSetChanged();
                this.updateSearchAdapter = false;
                this.searchView.searchEditText.setText("");
                checkCurrentList(false);
                this.searchView.hideKeyboard();
            }
        }
        if (this.searchAdapter.categoryAdapter != null) {
            this.searchAdapter.categoryAdapter.notifyItemRangeChanged(0, this.searchAdapter.categoryAdapter.getItemCount());
        }
    }

    private boolean onSendLongClick(View view) {
        int y;
        View view2 = view;
        if (this.parentFragment == null) {
            return false;
        }
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(1);
        if (this.sendingMessageObjects != null) {
            ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout1 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity, this.resourcesProvider);
            sendPopupLayout1.setAnimationEnabled(false);
            sendPopupLayout1.setOnTouchListener(new View.OnTouchListener() {
                private Rect popupRect = new Rect();

                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() != 0 || ShareAlert.this.sendPopupWindow == null || !ShareAlert.this.sendPopupWindow.isShowing()) {
                        return false;
                    }
                    v.getHitRect(this.popupRect);
                    if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                        return false;
                    }
                    ShareAlert.this.sendPopupWindow.dismiss();
                    return false;
                }
            });
            sendPopupLayout1.setDispatchKeyEventListener(new ShareAlert$$ExternalSyntheticLambda4(this));
            sendPopupLayout1.setShownFromBotton(false);
            sendPopupLayout1.setupRadialSelectors(getThemedColor("dialogButtonSelector"));
            ActionBarMenuSubItem hideSendersNameView = new ActionBarMenuSubItem(getContext(), true, true, false, this.resourcesProvider);
            sendPopupLayout1.addView(hideSendersNameView, LayoutHelper.createLinear(-1, 48));
            hideSendersNameView.setTextAndIcon(LocaleController.getString("ShowSendersName", NUM), 0);
            this.showSendersName = true;
            hideSendersNameView.setChecked(true);
            ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), true, false, true, this.resourcesProvider);
            sendPopupLayout1.addView(actionBarMenuSubItem, LayoutHelper.createLinear(-1, 48));
            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("HideSendersName", NUM), 0);
            actionBarMenuSubItem.setChecked(!this.showSendersName);
            hideSendersNameView.setOnClickListener(new ShareAlert$$ExternalSyntheticLambda11(this, hideSendersNameView, actionBarMenuSubItem));
            actionBarMenuSubItem.setOnClickListener(new ShareAlert$$ExternalSyntheticLambda12(this, hideSendersNameView, actionBarMenuSubItem));
            layout.addView(sendPopupLayout1, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, -8.0f));
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout2 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity, this.resourcesProvider);
        sendPopupLayout2.setAnimationEnabled(false);
        sendPopupLayout2.setOnTouchListener(new View.OnTouchListener() {
            private Rect popupRect = new Rect();

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() != 0 || ShareAlert.this.sendPopupWindow == null || !ShareAlert.this.sendPopupWindow.isShowing()) {
                    return false;
                }
                v.getHitRect(this.popupRect);
                if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                    return false;
                }
                ShareAlert.this.sendPopupWindow.dismiss();
                return false;
            }
        });
        sendPopupLayout2.setDispatchKeyEventListener(new ShareAlert$$ExternalSyntheticLambda3(this));
        sendPopupLayout2.setShownFromBotton(false);
        sendPopupLayout2.setupRadialSelectors(getThemedColor("dialogButtonSelector"));
        ActionBarMenuSubItem sendWithoutSound = new ActionBarMenuSubItem(getContext(), true, true, this.resourcesProvider);
        sendWithoutSound.setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
        sendWithoutSound.setMinimumWidth(AndroidUtilities.dp(196.0f));
        sendPopupLayout2.addView(sendWithoutSound, LayoutHelper.createLinear(-1, 48));
        sendWithoutSound.setOnClickListener(new ShareAlert$$ExternalSyntheticLambda8(this));
        ActionBarMenuSubItem sendMessage = new ActionBarMenuSubItem(getContext(), true, true, this.resourcesProvider);
        sendMessage.setTextAndIcon(LocaleController.getString("SendMessage", NUM), NUM);
        sendMessage.setMinimumWidth(AndroidUtilities.dp(196.0f));
        sendPopupLayout2.addView(sendMessage, LayoutHelper.createLinear(-1, 48));
        sendMessage.setOnClickListener(new ShareAlert$$ExternalSyntheticLambda9(this));
        layout.addView(sendPopupLayout2, LayoutHelper.createLinear(-1, -2));
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(layout, -2, -2);
        this.sendPopupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setAnimationEnabled(false);
        this.sendPopupWindow.setAnimationStyle(NUM);
        this.sendPopupWindow.setOutsideTouchable(true);
        this.sendPopupWindow.setClippingEnabled(true);
        this.sendPopupWindow.setInputMethodMode(2);
        this.sendPopupWindow.setSoftInputMode(0);
        this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        SharedConfig.removeScheduledOrNoSuoundHint();
        layout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] location = new int[2];
        view2.getLocationInWindow(location);
        if (!this.keyboardVisible || this.parentFragment.contentView.getMeasuredHeight() <= AndroidUtilities.dp(58.0f)) {
            y = (location[1] - layout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f);
        } else {
            y = location[1] + view.getMeasuredHeight();
        }
        this.sendPopupWindow.showAtLocation(view2, 51, ((location[0] + view.getMeasuredWidth()) - layout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), y);
        this.sendPopupWindow.dimBehind();
        view2.performHapticFeedback(3, 2);
        return false;
    }

    /* renamed from: lambda$onSendLongClick$9$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ void m4347lambda$onSendLongClick$9$orgtelegramuiComponentsShareAlert(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$onSendLongClick$10$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ void m4342lambda$onSendLongClick$10$orgtelegramuiComponentsShareAlert(ActionBarMenuSubItem showSendersNameView, ActionBarMenuSubItem hideSendersNameView, View e) {
        this.showSendersName = true;
        showSendersNameView.setChecked(true);
        hideSendersNameView.setChecked(true ^ this.showSendersName);
    }

    /* renamed from: lambda$onSendLongClick$11$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ void m4343lambda$onSendLongClick$11$orgtelegramuiComponentsShareAlert(ActionBarMenuSubItem showSendersNameView, ActionBarMenuSubItem hideSendersNameView, View e) {
        this.showSendersName = false;
        showSendersNameView.setChecked(false);
        hideSendersNameView.setChecked(!this.showSendersName);
    }

    /* renamed from: lambda$onSendLongClick$12$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ void m4344lambda$onSendLongClick$12$orgtelegramuiComponentsShareAlert(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$onSendLongClick$13$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ void m4345lambda$onSendLongClick$13$orgtelegramuiComponentsShareAlert(View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        sendInternal(false);
    }

    /* renamed from: lambda$onSendLongClick$14$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ void m4346lambda$onSendLongClick$14$orgtelegramuiComponentsShareAlert(View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        sendInternal(true);
    }

    private void sendInternal(boolean withSound) {
        int num;
        int a = 0;
        while (true) {
            boolean z = true;
            if (a < this.selectedDialogs.size()) {
                long key = this.selectedDialogs.keyAt(a);
                Context context = getContext();
                int i = this.currentAccount;
                if (this.frameLayout2.getTag() == null || this.commentTextView.length() <= 0) {
                    z = false;
                }
                if (!AlertsCreator.checkSlowMode(context, i, key, z)) {
                    a++;
                } else {
                    return;
                }
            } else {
                if (this.sendingMessageObjects != null) {
                    for (int a2 = 0; a2 < this.selectedDialogs.size(); a2++) {
                        long key2 = this.selectedDialogs.keyAt(a2);
                        if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), key2, (MessageObject) null, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, withSound, 0, (MessageObject.SendAnimationData) null);
                        }
                        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingMessageObjects, key2, !this.showSendersName, false, withSound, 0);
                    }
                    onSend(this.selectedDialogs, this.sendingMessageObjects.size());
                } else {
                    SwitchView switchView2 = this.switchView;
                    if (switchView2 != null) {
                        num = switchView2.currentTab;
                    } else {
                        num = 0;
                    }
                    if (this.sendingText[num] != null) {
                        for (int a3 = 0; a3 < this.selectedDialogs.size(); a3++) {
                            long key3 = this.selectedDialogs.keyAt(a3);
                            if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), key3, (MessageObject) null, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, withSound, 0, (MessageObject.SendAnimationData) null);
                            }
                            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingText[num], key3, (MessageObject) null, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, withSound, 0, (MessageObject.SendAnimationData) null);
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

    /* access modifiers changed from: protected */
    public void onSend(LongSparseArray<TLRPC.Dialog> longSparseArray, int count) {
    }

    /* access modifiers changed from: private */
    public int getCurrentTop() {
        if (this.gridView.getChildCount() == 0) {
            return -1000;
        }
        int i = 0;
        View child = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
        if (holder == null) {
            return -1000;
        }
        int paddingTop = this.gridView.getPaddingTop();
        if (holder.getLayoutPosition() == 0 && child.getTop() >= 0) {
            i = child.getTop();
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload) {
            ShareDialogsAdapter shareDialogsAdapter = this.listAdapter;
            if (shareDialogsAdapter != null) {
                shareDialogsAdapter.fetchDialogs();
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (!this.panTranslationMoveLayout) {
            RecyclerListView listView = this.searchIsVisible ? this.searchGridView : this.gridView;
            if (listView.getChildCount() > 0) {
                View child = listView.getChildAt(0);
                for (int i = 0; i < listView.getChildCount(); i++) {
                    if (listView.getChildAt(i).getTop() < child.getTop()) {
                        child = listView.getChildAt(i);
                    }
                }
                RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findContainingViewHolder(child);
                int top = child.getTop() - AndroidUtilities.dp(8.0f);
                int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
                if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
                    this.lastOffset = Integer.MAX_VALUE;
                    runShadowAnimation(0, true);
                } else {
                    this.lastOffset = child.getTop();
                    newOffset = top;
                    runShadowAnimation(0, false);
                }
                int i2 = this.scrollOffsetY;
                if (i2 != newOffset) {
                    this.previousScrollOffsetY = i2;
                    RecyclerListView recyclerListView = this.gridView;
                    int i3 = (int) (((float) newOffset) + this.currentPanTranslationY);
                    this.scrollOffsetY = i3;
                    recyclerListView.setTopGlowOffset(i3);
                    RecyclerListView recyclerListView2 = this.searchGridView;
                    int i4 = (int) (((float) newOffset) + this.currentPanTranslationY);
                    this.scrollOffsetY = i4;
                    recyclerListView2.setTopGlowOffset(i4);
                    this.frameLayout.setTranslationY(((float) this.scrollOffsetY) + this.currentPanTranslationY);
                    this.searchEmptyView.setTranslationY(((float) this.scrollOffsetY) + this.currentPanTranslationY);
                    this.containerView.invalidate();
                }
            }
        }
    }

    private void runShadowAnimation(final int num, final boolean show) {
        if ((show && this.shadow[num].getTag() != null) || (!show && this.shadow[num].getTag() == null)) {
            this.shadow[num].setTag(show ? null : 1);
            if (show) {
                this.shadow[num].setVisibility(0);
            }
            AnimatorSet[] animatorSetArr = this.shadowAnimation;
            if (animatorSetArr[num] != null) {
                animatorSetArr[num].cancel();
            }
            this.shadowAnimation[num] = new AnimatorSet();
            AnimatorSet animatorSet2 = this.shadowAnimation[num];
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow[num];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation[num].setDuration(150);
            this.shadowAnimation[num].addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ShareAlert.this.shadowAnimation[num] != null && ShareAlert.this.shadowAnimation[num].equals(animation)) {
                        if (!show) {
                            ShareAlert.this.shadow[num].setVisibility(4);
                        }
                        ShareAlert.this.shadowAnimation[num] = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ShareAlert.this.shadowAnimation[num] != null && ShareAlert.this.shadowAnimation[num].equals(animation)) {
                        ShareAlert.this.shadowAnimation[num] = null;
                    }
                }
            });
            this.shadowAnimation[num].start();
        }
    }

    private void copyLink(Context context) {
        String link;
        boolean isPrivate = false;
        if (this.exportedMessageLink != null || this.linkToCopy[0] != null) {
            try {
                SwitchView switchView2 = this.switchView;
                if (switchView2 != null) {
                    link = this.linkToCopy[switchView2.currentTab];
                } else {
                    link = this.linkToCopy[0];
                }
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", link != null ? link : this.exportedMessageLink.link));
                ShareAlertDelegate shareAlertDelegate = this.delegate;
                if ((shareAlertDelegate == null || !shareAlertDelegate.didCopy()) && (this.parentActivity instanceof LaunchActivity)) {
                    TLRPC.TL_exportedMessageLink tL_exportedMessageLink = this.exportedMessageLink;
                    if (tL_exportedMessageLink != null && tL_exportedMessageLink.link.contains("/c/")) {
                        isPrivate = true;
                    }
                    ((LaunchActivity) this.parentActivity).showBulletin(new ShareAlert$$ExternalSyntheticLambda15(this, isPrivate));
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* renamed from: lambda$copyLink$15$org-telegram-ui-Components-ShareAlert  reason: not valid java name */
    public /* synthetic */ Bulletin m4333lambda$copyLink$15$orgtelegramuiComponentsShareAlert(boolean isPrivate, BulletinFactory factory) {
        return factory.createCopyLinkBulletin(isPrivate, this.resourcesProvider);
    }

    private boolean showCommentTextView(final boolean show) {
        if (show == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.frameLayout2.setTag(show ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (show) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        TextView textView = this.pickerBottomLayout;
        int i = 4;
        if (textView != null) {
            ViewCompat.setImportantForAccessibility(textView, show ? 4 : 1);
        }
        LinearLayout linearLayout = this.sharesCountLayout;
        if (linearLayout != null) {
            if (!show) {
                i = 1;
            }
            ViewCompat.setImportantForAccessibility(linearLayout, i);
        }
        this.animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        FrameLayout frameLayout3 = this.frameLayout2;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 0.0f;
        fArr[0] = show ? 1.0f : 0.0f;
        animators.add(ObjectAnimator.ofFloat(frameLayout3, property, fArr));
        FrameLayout frameLayout4 = this.writeButtonContainer;
        Property property2 = View.SCALE_X;
        float[] fArr2 = new float[1];
        float f2 = 0.2f;
        fArr2[0] = show ? 1.0f : 0.2f;
        animators.add(ObjectAnimator.ofFloat(frameLayout4, property2, fArr2));
        FrameLayout frameLayout5 = this.writeButtonContainer;
        Property property3 = View.SCALE_Y;
        float[] fArr3 = new float[1];
        fArr3[0] = show ? 1.0f : 0.2f;
        animators.add(ObjectAnimator.ofFloat(frameLayout5, property3, fArr3));
        FrameLayout frameLayout6 = this.writeButtonContainer;
        Property property4 = View.ALPHA;
        float[] fArr4 = new float[1];
        fArr4[0] = show ? 1.0f : 0.0f;
        animators.add(ObjectAnimator.ofFloat(frameLayout6, property4, fArr4));
        View view = this.selectedCountView;
        Property property5 = View.SCALE_X;
        float[] fArr5 = new float[1];
        fArr5[0] = show ? 1.0f : 0.2f;
        animators.add(ObjectAnimator.ofFloat(view, property5, fArr5));
        View view2 = this.selectedCountView;
        Property property6 = View.SCALE_Y;
        float[] fArr6 = new float[1];
        if (show) {
            f2 = 1.0f;
        }
        fArr6[0] = f2;
        animators.add(ObjectAnimator.ofFloat(view2, property6, fArr6));
        View view3 = this.selectedCountView;
        Property property7 = View.ALPHA;
        float[] fArr7 = new float[1];
        fArr7[0] = show ? 1.0f : 0.0f;
        animators.add(ObjectAnimator.ofFloat(view3, property7, fArr7));
        TextView textView2 = this.pickerBottomLayout;
        if (textView2 == null || textView2.getVisibility() != 0) {
            View view4 = this.shadow[1];
            Property property8 = View.ALPHA;
            float[] fArr8 = new float[1];
            if (show) {
                f = 1.0f;
            }
            fArr8[0] = f;
            animators.add(ObjectAnimator.ofFloat(view4, property8, fArr8));
        }
        this.animatorSet.playTogether(animators);
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.setDuration(180);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(ShareAlert.this.animatorSet)) {
                    if (!show) {
                        ShareAlert.this.frameLayout2.setVisibility(4);
                        ShareAlert.this.writeButtonContainer.setVisibility(4);
                    }
                    AnimatorSet unused = ShareAlert.this.animatorSet = null;
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (animation.equals(ShareAlert.this.animatorSet)) {
                    AnimatorSet unused = ShareAlert.this.animatorSet = null;
                }
            }
        });
        this.animatorSet.start();
        return true;
    }

    public void updateSelectedCount(int animated) {
        if (this.selectedDialogs.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            showCommentTextView(false);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true) || animated == 0) {
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
        fArr[0] = animated == 1 ? 1.1f : 0.9f;
        fArr[1] = 1.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        View view2 = this.selectedCountView;
        Property property2 = View.SCALE_Y;
        float[] fArr2 = new float[2];
        if (animated != 1) {
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
        private int currentCount;
        /* access modifiers changed from: private */
        public ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>();
        /* access modifiers changed from: private */
        public LongSparseArray<TLRPC.Dialog> dialogsMap = new LongSparseArray<>();

        public ShareDialogsAdapter(Context context2) {
            this.context = context2;
            fetchDialogs();
        }

        public void fetchDialogs() {
            this.dialogs.clear();
            this.dialogsMap.clear();
            long selfUserId = UserConfig.getInstance(ShareAlert.this.currentAccount).clientUserId;
            if (!MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.isEmpty()) {
                TLRPC.Dialog dialog = MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.get(0);
                this.dialogs.add(dialog);
                this.dialogsMap.put(dialog.id, dialog);
            }
            ArrayList<TLRPC.Dialog> archivedDialogs = new ArrayList<>();
            ArrayList<TLRPC.Dialog> allDialogs = MessagesController.getInstance(ShareAlert.this.currentAccount).getAllDialogs();
            for (int a = 0; a < allDialogs.size(); a++) {
                TLRPC.Dialog dialog2 = allDialogs.get(a);
                if ((dialog2 instanceof TLRPC.TL_dialog) && dialog2.id != selfUserId && !DialogObject.isEncryptedDialog(dialog2.id)) {
                    if (DialogObject.isUserDialog(dialog2.id)) {
                        if (dialog2.folder_id == 1) {
                            archivedDialogs.add(dialog2);
                        } else {
                            this.dialogs.add(dialog2);
                        }
                        this.dialogsMap.put(dialog2.id, dialog2);
                    } else {
                        TLRPC.Chat chat = MessagesController.getInstance(ShareAlert.this.currentAccount).getChat(Long.valueOf(-dialog2.id));
                        if (chat != null && !ChatObject.isNotInChat(chat) && ((!chat.gigagroup || ChatObject.hasAdminRights(chat)) && (!ChatObject.isChannel(chat) || chat.creator || ((chat.admin_rights != null && chat.admin_rights.post_messages) || chat.megagroup)))) {
                            if (dialog2.folder_id == 1) {
                                archivedDialogs.add(dialog2);
                            } else {
                                this.dialogs.add(dialog2);
                            }
                            this.dialogsMap.put(dialog2.id, dialog2);
                        }
                    }
                }
            }
            this.dialogs.addAll(archivedDialogs);
            notifyDataSetChanged();
        }

        public int getItemCount() {
            int count = this.dialogs.size();
            if (count != 0) {
                return count + 1;
            }
            return count;
        }

        public TLRPC.Dialog getItem(int position) {
            int position2 = position - 1;
            if (position2 < 0 || position2 >= this.dialogs.size()) {
                return null;
            }
            return this.dialogs.get(position2);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 1) {
                return false;
            }
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            float f;
            switch (viewType) {
                case 0:
                    Context context2 = this.context;
                    boolean access$000 = ShareAlert.this.darkTheme;
                    view = new ShareDialogCell(context2, access$000 ? 1 : 0, ShareAlert.this.resourcesProvider);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f)));
                    break;
                default:
                    view = new View(this.context);
                    if (!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) {
                        f = 56.0f;
                    } else {
                        f = 109.0f;
                    }
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(f)));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                ShareDialogCell cell = (ShareDialogCell) holder.itemView;
                TLRPC.Dialog dialog = getItem(position);
                cell.setDialog(dialog.id, ShareAlert.this.selectedDialogs.indexOfKey(dialog.id) >= 0, (CharSequence) null);
            }
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            return 0;
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
        private int lastReqId;
        /* access modifiers changed from: private */
        public int lastSearchId;
        private String lastSearchText;
        int recentDialogsStartRow = -1;
        private int reqId;
        int resentTitleCell = -1;
        /* access modifiers changed from: private */
        public SearchAdapterHelper searchAdapterHelper;
        /* access modifiers changed from: private */
        public ArrayList<Object> searchResult = new ArrayList<>();
        int searchResultsStartRow = -1;
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

                public void onDataSetChanged(int searchId) {
                    int unused = ShareSearchAdapter.this.lastGlobalSearchId = searchId;
                    if (ShareSearchAdapter.this.lastLocalSearchId != searchId) {
                        ShareSearchAdapter.this.searchResult.clear();
                    }
                    int oldItemsCount = ShareSearchAdapter.this.lastItemCont;
                    if (ShareSearchAdapter.this.getItemCount() != 0 || ShareSearchAdapter.this.searchAdapterHelper.isSearchInProgress() || ShareSearchAdapter.this.internalDialogsIsSearching) {
                        ShareAlert.this.recyclerItemsEnterAnimator.showItemsAnimated(oldItemsCount);
                    } else {
                        ShareAlert.this.searchEmptyView.showTextView();
                    }
                    ShareSearchAdapter.this.notifyDataSetChanged();
                    ShareAlert.this.checkCurrentList(true);
                }

                public boolean canApplySearchResults(int searchId) {
                    return searchId == ShareSearchAdapter.this.lastSearchId;
                }
            });
        }

        private void searchDialogsInternal(String query, int searchId) {
            MessagesStorage.getInstance(ShareAlert.this.currentAccount).getStorageQueue().postRunnable(new ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda3(this, query, searchId));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:77:?, code lost:
            r7 = r11.byteBufferValue(0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:78:0x0190, code lost:
            if (r7 == null) goto L_0x0203;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:79:0x0192, code lost:
            r17 = r0;
            r24 = r3;
            r3 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r7, r7.readInt32(false), false);
            r7.reuse();
            r0 = r10.get(r3.id);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:80:0x01ac, code lost:
            if (r3.status == null) goto L_0x01bc;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:81:0x01ae, code lost:
            r26 = r5;
            r16 = r7;
            r3.status.expires = r11.intValue(1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:82:0x01bc, code lost:
            r26 = r5;
            r16 = r7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:84:0x01c1, code lost:
            if (r12 != 1) goto L_0x01ce;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:85:0x01c3, code lost:
            r0.name = org.telegram.messenger.AndroidUtilities.generateSearchName(r3.first_name, r3.last_name, r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:86:0x01ce, code lost:
            r0.name = org.telegram.messenger.AndroidUtilities.generateSearchName("@" + r3.username, (java.lang.String) null, "@" + r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:87:0x01f5, code lost:
            r0.object = r3;
            r27 = r4;
            r0.dialog.id = r3.id;
            r9 = r9 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:88:0x0203, code lost:
            r17 = r0;
            r24 = r3;
            r27 = r4;
            r26 = r5;
            r16 = r7;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:198:0x04a9 A[Catch:{ Exception -> 0x04f1 }, LOOP:7: B:167:0x03c6->B:198:0x04a9, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:221:0x0187 A[EDGE_INSN: B:221:0x0187->B:75:0x0187 ?: BREAK  , SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:243:0x041d A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:89:0x020e A[Catch:{ Exception -> 0x04ed }, LOOP:2: B:54:0x0130->B:89:0x020e, LOOP_END] */
        /* renamed from: lambda$searchDialogsInternal$1$org-telegram-ui-Components-ShareAlert$ShareSearchAdapter  reason: not valid java name */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void m4354xba04a7a2(java.lang.String r31, int r32) {
            /*
                r30 = this;
                r1 = r30
                java.lang.String r0 = r31.trim()     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x04f1 }
                int r2 = r0.length()     // Catch:{ Exception -> 0x04f1 }
                r3 = -1
                if (r2 != 0) goto L_0x001e
                r1.lastSearchId = r3     // Catch:{ Exception -> 0x04f1 }
                java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x04f1 }
                r2.<init>()     // Catch:{ Exception -> 0x04f1 }
                int r3 = r1.lastSearchId     // Catch:{ Exception -> 0x04f1 }
                r1.updateSearchResults(r2, r3)     // Catch:{ Exception -> 0x04f1 }
                return
            L_0x001e:
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r2 = r2.getTranslitString(r0)     // Catch:{ Exception -> 0x04f1 }
                boolean r4 = r0.equals(r2)     // Catch:{ Exception -> 0x04f1 }
                if (r4 != 0) goto L_0x0032
                int r4 = r2.length()     // Catch:{ Exception -> 0x04f1 }
                if (r4 != 0) goto L_0x0033
            L_0x0032:
                r2 = 0
            L_0x0033:
                r4 = 1
                r5 = 0
                if (r2 == 0) goto L_0x0039
                r6 = 1
                goto L_0x003a
            L_0x0039:
                r6 = 0
            L_0x003a:
                int r6 = r6 + r4
                java.lang.String[] r6 = new java.lang.String[r6]     // Catch:{ Exception -> 0x04f1 }
                r6[r5] = r0     // Catch:{ Exception -> 0x04f1 }
                if (r2 == 0) goto L_0x0043
                r6[r4] = r2     // Catch:{ Exception -> 0x04f1 }
            L_0x0043:
                java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x04f1 }
                r7.<init>()     // Catch:{ Exception -> 0x04f1 }
                java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ Exception -> 0x04f1 }
                r8.<init>()     // Catch:{ Exception -> 0x04f1 }
                r9 = 0
                androidx.collection.LongSparseArray r10 = new androidx.collection.LongSparseArray     // Catch:{ Exception -> 0x04f1 }
                r10.<init>()     // Catch:{ Exception -> 0x04f1 }
                org.telegram.ui.Components.ShareAlert r11 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x04f1 }
                int r11 = r11.currentAccount     // Catch:{ Exception -> 0x04f1 }
                org.telegram.messenger.MessagesStorage r11 = org.telegram.messenger.MessagesStorage.getInstance(r11)     // Catch:{ Exception -> 0x04f1 }
                org.telegram.SQLite.SQLiteDatabase r11 = r11.getDatabase()     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r12 = "SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400"
                java.lang.Object[] r13 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x04f1 }
                org.telegram.SQLite.SQLiteCursor r11 = r11.queryFinalized(r12, r13)     // Catch:{ Exception -> 0x04f1 }
            L_0x0069:
                boolean r12 = r11.next()     // Catch:{ Exception -> 0x04f1 }
                if (r12 == 0) goto L_0x00c0
                long r12 = r11.longValue(r5)     // Catch:{ Exception -> 0x04f1 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r14 = new org.telegram.ui.Components.ShareAlert$DialogSearchResult     // Catch:{ Exception -> 0x04f1 }
                r14.<init>()     // Catch:{ Exception -> 0x04f1 }
                int r15 = r11.intValue(r4)     // Catch:{ Exception -> 0x04f1 }
                r14.date = r15     // Catch:{ Exception -> 0x04f1 }
                r10.put(r12, r14)     // Catch:{ Exception -> 0x04f1 }
                boolean r15 = org.telegram.messenger.DialogObject.isUserDialog(r12)     // Catch:{ Exception -> 0x04f1 }
                if (r15 == 0) goto L_0x009e
                java.lang.Long r15 = java.lang.Long.valueOf(r12)     // Catch:{ Exception -> 0x04f1 }
                boolean r15 = r7.contains(r15)     // Catch:{ Exception -> 0x04f1 }
                if (r15 != 0) goto L_0x009b
                java.lang.Long r15 = java.lang.Long.valueOf(r12)     // Catch:{ Exception -> 0x04f1 }
                r7.add(r15)     // Catch:{ Exception -> 0x04f1 }
                r16 = r6
                goto L_0x00bc
            L_0x009b:
                r16 = r6
                goto L_0x00bc
            L_0x009e:
                boolean r15 = org.telegram.messenger.DialogObject.isChatDialog(r12)     // Catch:{ Exception -> 0x04f1 }
                if (r15 == 0) goto L_0x00ba
                r16 = r6
                long r5 = -r12
                java.lang.Long r5 = java.lang.Long.valueOf(r5)     // Catch:{ Exception -> 0x04f1 }
                boolean r5 = r8.contains(r5)     // Catch:{ Exception -> 0x04f1 }
                if (r5 != 0) goto L_0x00bc
                long r5 = -r12
                java.lang.Long r5 = java.lang.Long.valueOf(r5)     // Catch:{ Exception -> 0x04f1 }
                r8.add(r5)     // Catch:{ Exception -> 0x04f1 }
                goto L_0x00bc
            L_0x00ba:
                r16 = r6
            L_0x00bc:
                r6 = r16
                r5 = 0
                goto L_0x0069
            L_0x00c0:
                r16 = r6
                r11.dispose()     // Catch:{ Exception -> 0x04f1 }
                boolean r5 = r7.isEmpty()     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r6 = ";;;"
                java.lang.String r13 = ","
                java.lang.String r15 = "@"
                java.lang.String r14 = " "
                if (r5 != 0) goto L_0x023e
                org.telegram.ui.Components.ShareAlert r5 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x04ed }
                int r5 = r5.currentAccount     // Catch:{ Exception -> 0x04ed }
                org.telegram.messenger.MessagesStorage r5 = org.telegram.messenger.MessagesStorage.getInstance(r5)     // Catch:{ Exception -> 0x04ed }
                org.telegram.SQLite.SQLiteDatabase r5 = r5.getDatabase()     // Catch:{ Exception -> 0x04ed }
                java.util.Locale r3 = java.util.Locale.US     // Catch:{ Exception -> 0x04ed }
                java.lang.String r12 = "SELECT data, status, name FROM users WHERE uid IN(%s)"
                r19 = r0
                java.lang.Object[] r0 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x04ed }
                java.lang.String r20 = android.text.TextUtils.join(r13, r7)     // Catch:{ Exception -> 0x04ed }
                r4 = 0
                r0[r4] = r20     // Catch:{ Exception -> 0x04ed }
                java.lang.String r0 = java.lang.String.format(r3, r12, r0)     // Catch:{ Exception -> 0x04ed }
                java.lang.Object[] r3 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x04ed }
                org.telegram.SQLite.SQLiteCursor r0 = r5.queryFinalized(r0, r3)     // Catch:{ Exception -> 0x04ed }
                r11 = r0
            L_0x00fb:
                boolean r0 = r11.next()     // Catch:{ Exception -> 0x04ed }
                if (r0 == 0) goto L_0x0234
                r0 = 2
                java.lang.String r3 = r11.stringValue(r0)     // Catch:{ Exception -> 0x04ed }
                r0 = r3
                org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x04ed }
                java.lang.String r3 = r3.getTranslitString(r0)     // Catch:{ Exception -> 0x04ed }
                boolean r5 = r0.equals(r3)     // Catch:{ Exception -> 0x04ed }
                if (r5 == 0) goto L_0x0116
                r3 = 0
            L_0x0116:
                r5 = 0
                int r12 = r0.lastIndexOf(r6)     // Catch:{ Exception -> 0x04ed }
                r4 = -1
                if (r12 == r4) goto L_0x0125
                int r4 = r12 + 3
                java.lang.String r4 = r0.substring(r4)     // Catch:{ Exception -> 0x04f1 }
                r5 = r4
            L_0x0125:
                r4 = 0
                r20 = r2
                r2 = r16
                r16 = r4
                int r4 = r2.length     // Catch:{ Exception -> 0x04ed }
                r21 = r7
                r7 = 0
            L_0x0130:
                if (r7 >= r4) goto L_0x0222
                r22 = r2[r7]     // Catch:{ Exception -> 0x04ed }
                r23 = r22
                r22 = r4
                r4 = r23
                boolean r23 = r0.startsWith(r4)     // Catch:{ Exception -> 0x04ed }
                if (r23 != 0) goto L_0x0182
                r23 = r12
                java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04f1 }
                r12.<init>()     // Catch:{ Exception -> 0x04f1 }
                r12.append(r14)     // Catch:{ Exception -> 0x04f1 }
                r12.append(r4)     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x04f1 }
                boolean r12 = r0.contains(r12)     // Catch:{ Exception -> 0x04f1 }
                if (r12 != 0) goto L_0x0184
                if (r3 == 0) goto L_0x0175
                boolean r12 = r3.startsWith(r4)     // Catch:{ Exception -> 0x04f1 }
                if (r12 != 0) goto L_0x0184
                java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04f1 }
                r12.<init>()     // Catch:{ Exception -> 0x04f1 }
                r12.append(r14)     // Catch:{ Exception -> 0x04f1 }
                r12.append(r4)     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x04f1 }
                boolean r12 = r3.contains(r12)     // Catch:{ Exception -> 0x04f1 }
                if (r12 == 0) goto L_0x0175
                goto L_0x0184
            L_0x0175:
                if (r5 == 0) goto L_0x017f
                boolean r12 = r5.startsWith(r4)     // Catch:{ Exception -> 0x04f1 }
                if (r12 == 0) goto L_0x017f
                r12 = 2
                goto L_0x0185
            L_0x017f:
                r12 = r16
                goto L_0x0185
            L_0x0182:
                r23 = r12
            L_0x0184:
                r12 = 1
            L_0x0185:
                if (r12 == 0) goto L_0x020e
                r7 = 0
                org.telegram.tgnet.NativeByteBuffer r16 = r11.byteBufferValue(r7)     // Catch:{ Exception -> 0x04ed }
                r17 = r16
                r7 = r17
                if (r7 == 0) goto L_0x0203
                r17 = r0
                r24 = r3
                r0 = 0
                int r3 = r7.readInt32(r0)     // Catch:{ Exception -> 0x04ed }
                org.telegram.tgnet.TLRPC$User r3 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r7, r3, r0)     // Catch:{ Exception -> 0x04ed }
                r7.reuse()     // Catch:{ Exception -> 0x04ed }
                long r0 = r3.id     // Catch:{ Exception -> 0x04ed }
                java.lang.Object r0 = r10.get(r0)     // Catch:{ Exception -> 0x04ed }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r0 = (org.telegram.ui.Components.ShareAlert.DialogSearchResult) r0     // Catch:{ Exception -> 0x04ed }
                org.telegram.tgnet.TLRPC$UserStatus r1 = r3.status     // Catch:{ Exception -> 0x04ed }
                if (r1 == 0) goto L_0x01bc
                org.telegram.tgnet.TLRPC$UserStatus r1 = r3.status     // Catch:{ Exception -> 0x04ed }
                r26 = r5
                r16 = r7
                r5 = 1
                int r7 = r11.intValue(r5)     // Catch:{ Exception -> 0x04ed }
                r1.expires = r7     // Catch:{ Exception -> 0x04ed }
                goto L_0x01c0
            L_0x01bc:
                r26 = r5
                r16 = r7
            L_0x01c0:
                r1 = 1
                if (r12 != r1) goto L_0x01ce
                java.lang.String r1 = r3.first_name     // Catch:{ Exception -> 0x04ed }
                java.lang.String r5 = r3.last_name     // Catch:{ Exception -> 0x04ed }
                java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r1, r5, r4)     // Catch:{ Exception -> 0x04ed }
                r0.name = r1     // Catch:{ Exception -> 0x04ed }
                goto L_0x01f5
            L_0x01ce:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ed }
                r1.<init>()     // Catch:{ Exception -> 0x04ed }
                r1.append(r15)     // Catch:{ Exception -> 0x04ed }
                java.lang.String r5 = r3.username     // Catch:{ Exception -> 0x04ed }
                r1.append(r5)     // Catch:{ Exception -> 0x04ed }
                java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x04ed }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ed }
                r5.<init>()     // Catch:{ Exception -> 0x04ed }
                r5.append(r15)     // Catch:{ Exception -> 0x04ed }
                r5.append(r4)     // Catch:{ Exception -> 0x04ed }
                java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x04ed }
                r7 = 0
                java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r1, r7, r5)     // Catch:{ Exception -> 0x04ed }
                r0.name = r1     // Catch:{ Exception -> 0x04ed }
            L_0x01f5:
                r0.object = r3     // Catch:{ Exception -> 0x04ed }
                org.telegram.tgnet.TLRPC$Dialog r1 = r0.dialog     // Catch:{ Exception -> 0x04ed }
                r27 = r4
                long r4 = r3.id     // Catch:{ Exception -> 0x04ed }
                r1.id = r4     // Catch:{ Exception -> 0x04ed }
                int r9 = r9 + 1
                goto L_0x022a
            L_0x0203:
                r17 = r0
                r24 = r3
                r27 = r4
                r26 = r5
                r16 = r7
                goto L_0x022a
            L_0x020e:
                r17 = r0
                r24 = r3
                r27 = r4
                r26 = r5
                int r7 = r7 + 1
                r1 = r30
                r16 = r12
                r4 = r22
                r12 = r23
                goto L_0x0130
            L_0x0222:
                r17 = r0
                r24 = r3
                r26 = r5
                r23 = r12
            L_0x022a:
                r1 = r30
                r16 = r2
                r2 = r20
                r7 = r21
                goto L_0x00fb
            L_0x0234:
                r20 = r2
                r21 = r7
                r2 = r16
                r11.dispose()     // Catch:{ Exception -> 0x04ed }
                goto L_0x0246
            L_0x023e:
                r19 = r0
                r20 = r2
                r21 = r7
                r2 = r16
            L_0x0246:
                boolean r0 = r8.isEmpty()     // Catch:{ Exception -> 0x04ed }
                if (r0 != 0) goto L_0x0347
                r1 = r30
                org.telegram.ui.Components.ShareAlert r0 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x04f1 }
                int r0 = r0.currentAccount     // Catch:{ Exception -> 0x04f1 }
                org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x04f1 }
                org.telegram.SQLite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x04f1 }
                java.util.Locale r3 = java.util.Locale.US     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r4 = "SELECT data, name FROM chats WHERE uid IN(%s)"
                r5 = 1
                java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r5 = android.text.TextUtils.join(r13, r8)     // Catch:{ Exception -> 0x04f1 }
                r12 = 0
                r7[r12] = r5     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r3 = java.lang.String.format(r3, r4, r7)     // Catch:{ Exception -> 0x04f1 }
                java.lang.Object[] r4 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x04f1 }
                org.telegram.SQLite.SQLiteCursor r0 = r0.queryFinalized(r3, r4)     // Catch:{ Exception -> 0x04f1 }
                r11 = r0
            L_0x0275:
                boolean r0 = r11.next()     // Catch:{ Exception -> 0x04f1 }
                if (r0 == 0) goto L_0x0343
                r0 = 1
                java.lang.String r3 = r11.stringValue(r0)     // Catch:{ Exception -> 0x04f1 }
                r0 = r3
                org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r3 = r3.getTranslitString(r0)     // Catch:{ Exception -> 0x04f1 }
                boolean r4 = r0.equals(r3)     // Catch:{ Exception -> 0x04f1 }
                if (r4 == 0) goto L_0x0290
                r3 = 0
            L_0x0290:
                r4 = 0
            L_0x0291:
                int r7 = r2.length     // Catch:{ Exception -> 0x04f1 }
                if (r4 >= r7) goto L_0x033c
                r7 = r2[r4]     // Catch:{ Exception -> 0x04f1 }
                boolean r12 = r0.startsWith(r7)     // Catch:{ Exception -> 0x04f1 }
                if (r12 != 0) goto L_0x02d2
                java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04f1 }
                r12.<init>()     // Catch:{ Exception -> 0x04f1 }
                r12.append(r14)     // Catch:{ Exception -> 0x04f1 }
                r12.append(r7)     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x04f1 }
                boolean r12 = r0.contains(r12)     // Catch:{ Exception -> 0x04f1 }
                if (r12 != 0) goto L_0x02d2
                if (r3 == 0) goto L_0x02cf
                boolean r12 = r3.startsWith(r7)     // Catch:{ Exception -> 0x04f1 }
                if (r12 != 0) goto L_0x02d2
                java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04f1 }
                r12.<init>()     // Catch:{ Exception -> 0x04f1 }
                r12.append(r14)     // Catch:{ Exception -> 0x04f1 }
                r12.append(r7)     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x04f1 }
                boolean r12 = r3.contains(r12)     // Catch:{ Exception -> 0x04f1 }
                if (r12 == 0) goto L_0x02cf
                goto L_0x02d2
            L_0x02cf:
                int r4 = r4 + 1
                goto L_0x0291
            L_0x02d2:
                r5 = 0
                org.telegram.tgnet.NativeByteBuffer r12 = r11.byteBufferValue(r5)     // Catch:{ Exception -> 0x04f1 }
                if (r12 == 0) goto L_0x0336
                int r13 = r12.readInt32(r5)     // Catch:{ Exception -> 0x04f1 }
                org.telegram.tgnet.TLRPC$Chat r13 = org.telegram.tgnet.TLRPC.Chat.TLdeserialize(r12, r13, r5)     // Catch:{ Exception -> 0x04f1 }
                r12.reuse()     // Catch:{ Exception -> 0x04f1 }
                if (r13 == 0) goto L_0x0330
                boolean r16 = org.telegram.messenger.ChatObject.isNotInChat(r13)     // Catch:{ Exception -> 0x04f1 }
                if (r16 != 0) goto L_0x0330
                boolean r16 = org.telegram.messenger.ChatObject.isChannel(r13)     // Catch:{ Exception -> 0x04f1 }
                if (r16 == 0) goto L_0x030b
                boolean r5 = r13.creator     // Catch:{ Exception -> 0x04f1 }
                if (r5 != 0) goto L_0x030b
                org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = r13.admin_rights     // Catch:{ Exception -> 0x04f1 }
                if (r5 == 0) goto L_0x0300
                org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = r13.admin_rights     // Catch:{ Exception -> 0x04f1 }
                boolean r5 = r5.post_messages     // Catch:{ Exception -> 0x04f1 }
                if (r5 != 0) goto L_0x030b
            L_0x0300:
                boolean r5 = r13.megagroup     // Catch:{ Exception -> 0x04f1 }
                if (r5 == 0) goto L_0x0305
                goto L_0x030b
            L_0x0305:
                r22 = r0
                r5 = r3
                r16 = r4
                goto L_0x0335
            L_0x030b:
                r5 = r3
                r16 = r4
                long r3 = r13.id     // Catch:{ Exception -> 0x04f1 }
                long r3 = -r3
                java.lang.Object r3 = r10.get(r3)     // Catch:{ Exception -> 0x04f1 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r3 = (org.telegram.ui.Components.ShareAlert.DialogSearchResult) r3     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r4 = r13.title     // Catch:{ Exception -> 0x04f1 }
                r22 = r0
                r0 = 0
                java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r0, r7)     // Catch:{ Exception -> 0x04f1 }
                r3.name = r4     // Catch:{ Exception -> 0x04f1 }
                r3.object = r13     // Catch:{ Exception -> 0x04f1 }
                org.telegram.tgnet.TLRPC$Dialog r0 = r3.dialog     // Catch:{ Exception -> 0x04f1 }
                r23 = r3
                long r3 = r13.id     // Catch:{ Exception -> 0x04f1 }
                long r3 = -r3
                r0.id = r3     // Catch:{ Exception -> 0x04f1 }
                int r9 = r9 + 1
                goto L_0x0335
            L_0x0330:
                r22 = r0
                r5 = r3
                r16 = r4
            L_0x0335:
                goto L_0x0341
            L_0x0336:
                r22 = r0
                r5 = r3
                r16 = r4
                goto L_0x0341
            L_0x033c:
                r22 = r0
                r5 = r3
                r16 = r4
            L_0x0341:
                goto L_0x0275
            L_0x0343:
                r11.dispose()     // Catch:{ Exception -> 0x04f1 }
                goto L_0x0349
            L_0x0347:
                r1 = r30
            L_0x0349:
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04f1 }
                r0.<init>(r9)     // Catch:{ Exception -> 0x04f1 }
                r3 = 0
            L_0x034f:
                int r4 = r10.size()     // Catch:{ Exception -> 0x04f1 }
                if (r3 >= r4) goto L_0x0369
                java.lang.Object r4 = r10.valueAt(r3)     // Catch:{ Exception -> 0x04f1 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r4 = (org.telegram.ui.Components.ShareAlert.DialogSearchResult) r4     // Catch:{ Exception -> 0x04f1 }
                org.telegram.tgnet.TLObject r5 = r4.object     // Catch:{ Exception -> 0x04f1 }
                if (r5 == 0) goto L_0x0366
                java.lang.CharSequence r5 = r4.name     // Catch:{ Exception -> 0x04f1 }
                if (r5 == 0) goto L_0x0366
                r0.add(r4)     // Catch:{ Exception -> 0x04f1 }
            L_0x0366:
                int r3 = r3 + 1
                goto L_0x034f
            L_0x0369:
                org.telegram.ui.Components.ShareAlert r3 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x04f1 }
                int r3 = r3.currentAccount     // Catch:{ Exception -> 0x04f1 }
                org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r3)     // Catch:{ Exception -> 0x04f1 }
                org.telegram.SQLite.SQLiteDatabase r3 = r3.getDatabase()     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r4 = "SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid"
                r5 = 0
                java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x04f1 }
                org.telegram.SQLite.SQLiteCursor r3 = r3.queryFinalized(r4, r7)     // Catch:{ Exception -> 0x04f1 }
            L_0x0380:
                boolean r4 = r3.next()     // Catch:{ Exception -> 0x04f1 }
                if (r4 == 0) goto L_0x04d7
                r4 = 3
                long r11 = r3.longValue(r4)     // Catch:{ Exception -> 0x04f1 }
                int r4 = r10.indexOfKey(r11)     // Catch:{ Exception -> 0x04f1 }
                if (r4 < 0) goto L_0x0392
                goto L_0x0380
            L_0x0392:
                r4 = 2
                java.lang.String r7 = r3.stringValue(r4)     // Catch:{ Exception -> 0x04f1 }
                org.telegram.messenger.LocaleController r13 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r13 = r13.getTranslitString(r7)     // Catch:{ Exception -> 0x04f1 }
                boolean r16 = r7.equals(r13)     // Catch:{ Exception -> 0x04f1 }
                if (r16 == 0) goto L_0x03a6
                r13 = 0
            L_0x03a6:
                r16 = 0
                int r17 = r7.lastIndexOf(r6)     // Catch:{ Exception -> 0x04f1 }
                r18 = r17
                r4 = r18
                r5 = -1
                if (r4 == r5) goto L_0x03bc
                int r5 = r4 + 3
                java.lang.String r5 = r7.substring(r5)     // Catch:{ Exception -> 0x04f1 }
                r16 = r5
                goto L_0x03be
            L_0x03bc:
                r5 = r16
            L_0x03be:
                r16 = 0
                r18 = r4
                int r4 = r2.length     // Catch:{ Exception -> 0x04f1 }
                r22 = r6
                r6 = 0
            L_0x03c6:
                if (r6 >= r4) goto L_0x04c1
                r23 = r2[r6]     // Catch:{ Exception -> 0x04f1 }
                r24 = r23
                r23 = r2
                r2 = r24
                boolean r24 = r7.startsWith(r2)     // Catch:{ Exception -> 0x04f1 }
                if (r24 != 0) goto L_0x0418
                r24 = r4
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04f1 }
                r4.<init>()     // Catch:{ Exception -> 0x04f1 }
                r4.append(r14)     // Catch:{ Exception -> 0x04f1 }
                r4.append(r2)     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04f1 }
                boolean r4 = r7.contains(r4)     // Catch:{ Exception -> 0x04f1 }
                if (r4 != 0) goto L_0x041a
                if (r13 == 0) goto L_0x040b
                boolean r4 = r13.startsWith(r2)     // Catch:{ Exception -> 0x04f1 }
                if (r4 != 0) goto L_0x041a
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04f1 }
                r4.<init>()     // Catch:{ Exception -> 0x04f1 }
                r4.append(r14)     // Catch:{ Exception -> 0x04f1 }
                r4.append(r2)     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04f1 }
                boolean r4 = r13.contains(r4)     // Catch:{ Exception -> 0x04f1 }
                if (r4 == 0) goto L_0x040b
                goto L_0x041a
            L_0x040b:
                if (r5 == 0) goto L_0x0415
                boolean r4 = r5.startsWith(r2)     // Catch:{ Exception -> 0x04f1 }
                if (r4 == 0) goto L_0x0415
                r4 = 2
                goto L_0x041b
            L_0x0415:
                r4 = r16
                goto L_0x041b
            L_0x0418:
                r24 = r4
            L_0x041a:
                r4 = 1
            L_0x041b:
                if (r4 == 0) goto L_0x04a9
                r6 = 0
                org.telegram.tgnet.NativeByteBuffer r16 = r3.byteBufferValue(r6)     // Catch:{ Exception -> 0x04f1 }
                r24 = r16
                r6 = r24
                if (r6 == 0) goto L_0x049c
                r25 = r5
                r26 = r7
                r5 = 0
                int r7 = r6.readInt32(r5)     // Catch:{ Exception -> 0x04f1 }
                org.telegram.tgnet.TLRPC$User r7 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r6, r7, r5)     // Catch:{ Exception -> 0x04f1 }
                r6.reuse()     // Catch:{ Exception -> 0x04f1 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r16 = new org.telegram.ui.Components.ShareAlert$DialogSearchResult     // Catch:{ Exception -> 0x04f1 }
                r16.<init>()     // Catch:{ Exception -> 0x04f1 }
                r24 = r16
                org.telegram.tgnet.TLRPC$UserStatus r5 = r7.status     // Catch:{ Exception -> 0x04f1 }
                if (r5 == 0) goto L_0x0451
                org.telegram.tgnet.TLRPC$UserStatus r5 = r7.status     // Catch:{ Exception -> 0x04f1 }
                r27 = r6
                r28 = r8
                r6 = 1
                int r8 = r3.intValue(r6)     // Catch:{ Exception -> 0x04f1 }
                r5.expires = r8     // Catch:{ Exception -> 0x04f1 }
                goto L_0x0455
            L_0x0451:
                r27 = r6
                r28 = r8
            L_0x0455:
                r5 = r24
                org.telegram.tgnet.TLRPC$Dialog r6 = r5.dialog     // Catch:{ Exception -> 0x04f1 }
                r29 = r9
                long r8 = r7.id     // Catch:{ Exception -> 0x04f1 }
                r6.id = r8     // Catch:{ Exception -> 0x04f1 }
                r5.object = r7     // Catch:{ Exception -> 0x04f1 }
                r8 = 1
                if (r4 != r8) goto L_0x0470
                java.lang.String r6 = r7.first_name     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r9 = r7.last_name     // Catch:{ Exception -> 0x04f1 }
                java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.generateSearchName(r6, r9, r2)     // Catch:{ Exception -> 0x04f1 }
                r5.name = r6     // Catch:{ Exception -> 0x04f1 }
                r8 = 0
                goto L_0x0497
            L_0x0470:
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04f1 }
                r6.<init>()     // Catch:{ Exception -> 0x04f1 }
                r6.append(r15)     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r9 = r7.username     // Catch:{ Exception -> 0x04f1 }
                r6.append(r9)     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x04f1 }
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04f1 }
                r9.<init>()     // Catch:{ Exception -> 0x04f1 }
                r9.append(r15)     // Catch:{ Exception -> 0x04f1 }
                r9.append(r2)     // Catch:{ Exception -> 0x04f1 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x04f1 }
                r8 = 0
                java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.generateSearchName(r6, r8, r9)     // Catch:{ Exception -> 0x04f1 }
                r5.name = r6     // Catch:{ Exception -> 0x04f1 }
            L_0x0497:
                r0.add(r5)     // Catch:{ Exception -> 0x04f1 }
                r5 = 0
                goto L_0x04cd
            L_0x049c:
                r25 = r5
                r27 = r6
                r26 = r7
                r28 = r8
                r29 = r9
                r8 = 0
                r5 = 0
                goto L_0x04cd
            L_0x04a9:
                r25 = r5
                r26 = r7
                r28 = r8
                r29 = r9
                r5 = 0
                r8 = 0
                int r6 = r6 + 1
                r16 = r4
                r2 = r23
                r4 = r24
                r5 = r25
                r8 = r28
                goto L_0x03c6
            L_0x04c1:
                r23 = r2
                r25 = r5
                r26 = r7
                r28 = r8
                r29 = r9
                r5 = 0
                r8 = 0
            L_0x04cd:
                r6 = r22
                r2 = r23
                r8 = r28
                r9 = r29
                goto L_0x0380
            L_0x04d7:
                r23 = r2
                r28 = r8
                r29 = r9
                r3.dispose()     // Catch:{ Exception -> 0x04f1 }
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda4 r2 = org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda4.INSTANCE     // Catch:{ Exception -> 0x04f1 }
                java.util.Collections.sort(r0, r2)     // Catch:{ Exception -> 0x04f1 }
                r2 = r32
                r1.updateSearchResults(r0, r2)     // Catch:{ Exception -> 0x04eb }
                goto L_0x04f7
            L_0x04eb:
                r0 = move-exception
                goto L_0x04f4
            L_0x04ed:
                r0 = move-exception
                r1 = r30
                goto L_0x04f2
            L_0x04f1:
                r0 = move-exception
            L_0x04f2:
                r2 = r32
            L_0x04f4:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x04f7:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.m4354xba04a7a2(java.lang.String, int):void");
        }

        static /* synthetic */ int lambda$searchDialogsInternal$0(Object lhs, Object rhs) {
            DialogSearchResult res1 = (DialogSearchResult) lhs;
            DialogSearchResult res2 = (DialogSearchResult) rhs;
            if (res1.date < res2.date) {
                return 1;
            }
            if (res1.date > res2.date) {
                return -1;
            }
            return 0;
        }

        private void updateSearchResults(ArrayList<Object> result, int searchId) {
            AndroidUtilities.runOnUIThread(new ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda1(this, searchId, result));
        }

        /* renamed from: lambda$updateSearchResults$2$org-telegram-ui-Components-ShareAlert$ShareSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m4355xde10a13c(int searchId, ArrayList result) {
            if (searchId == this.lastSearchId) {
                int itemCount = getItemCount();
                this.internalDialogsIsSearching = false;
                this.lastLocalSearchId = searchId;
                if (this.lastGlobalSearchId != searchId) {
                    this.searchAdapterHelper.clear();
                }
                if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
                    ShareAlert shareAlert = ShareAlert.this;
                    int unused = shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    ShareAlert.this.searchAdapter.notifyDataSetChanged();
                }
                for (int a = 0; a < result.size(); a++) {
                    DialogSearchResult obj = (DialogSearchResult) result.get(a);
                    if (obj.object instanceof TLRPC.User) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putUser((TLRPC.User) obj.object, true);
                    } else if (obj.object instanceof TLRPC.Chat) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putChat((TLRPC.Chat) obj.object, true);
                    }
                }
                boolean becomeEmpty = !this.searchResult.isEmpty() && result.isEmpty();
                if (this.searchResult.isEmpty() && result.isEmpty()) {
                }
                if (becomeEmpty) {
                    ShareAlert shareAlert2 = ShareAlert.this;
                    int unused2 = shareAlert2.topBeforeSwitch = shareAlert2.getCurrentTop();
                }
                this.searchResult = result;
                this.searchAdapterHelper.mergeResults(result);
                int oldItemsCount = this.lastItemCont;
                if (getItemCount() != 0 || this.searchAdapterHelper.isSearchInProgress() || this.internalDialogsIsSearching) {
                    ShareAlert.this.recyclerItemsEnterAnimator.showItemsAnimated(oldItemsCount);
                } else {
                    ShareAlert.this.searchEmptyView.showTextView();
                }
                notifyDataSetChanged();
                ShareAlert.this.checkCurrentList(true);
            }
        }

        public void searchDialogs(String query) {
            if (query == null || !query.equals(this.lastSearchText)) {
                this.lastSearchText = query;
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
                if (TextUtils.isEmpty(query)) {
                    ShareAlert shareAlert = ShareAlert.this;
                    int unused = shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    this.lastSearchId = -1;
                    this.internalDialogsIsSearching = false;
                } else {
                    this.internalDialogsIsSearching = true;
                    int searchId = this.lastSearchId + 1;
                    this.lastSearchId = searchId;
                    ShareAlert.this.searchEmptyView.showProgress(false);
                    DispatchQueue dispatchQueue = Utilities.searchQueue;
                    ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda2 shareAlert$ShareSearchAdapter$$ExternalSyntheticLambda2 = new ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda2(this, query, searchId);
                    this.searchRunnable = shareAlert$ShareSearchAdapter$$ExternalSyntheticLambda2;
                    dispatchQueue.postRunnable(shareAlert$ShareSearchAdapter$$ExternalSyntheticLambda2, 300);
                }
                ShareAlert.this.checkCurrentList(false);
            }
        }

        /* renamed from: lambda$searchDialogs$4$org-telegram-ui-Components-ShareAlert$ShareSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m4353x47315ffc(String query, int searchId) {
            this.searchRunnable = null;
            searchDialogsInternal(query, searchId);
            ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda0 shareAlert$ShareSearchAdapter$$ExternalSyntheticLambda0 = new ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda0(this, searchId, query);
            this.searchRunnable2 = shareAlert$ShareSearchAdapter$$ExternalSyntheticLambda0;
            AndroidUtilities.runOnUIThread(shareAlert$ShareSearchAdapter$$ExternalSyntheticLambda0);
        }

        /* renamed from: lambda$searchDialogs$3$org-telegram-ui-Components-ShareAlert$ShareSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m4352xba4448dd(int searchId, String query) {
            this.searchRunnable2 = null;
            if (searchId == this.lastSearchId) {
                this.searchAdapterHelper.queryServerSearch(query, true, true, true, true, false, 0, false, 0, searchId);
            }
        }

        public int getItemCount() {
            this.itemsCount = 0;
            this.hintsCell = -1;
            this.resentTitleCell = -1;
            this.recentDialogsStartRow = -1;
            this.searchResultsStartRow = -1;
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
            this.searchResultsStartRow = i8;
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

        public TLRPC.Dialog getItem(int position) {
            int i = this.recentDialogsStartRow;
            if (position < i || i < 0) {
                int position2 = position - 1;
                if (position2 < 0) {
                    return null;
                }
                if (position2 < this.searchResult.size()) {
                    return ((DialogSearchResult) this.searchResult.get(position2)).dialog;
                }
                int position3 = position2 - this.searchResult.size();
                ArrayList<TLObject> arrayList = this.searchAdapterHelper.getLocalServerSearch();
                if (position3 >= arrayList.size()) {
                    return null;
                }
                TLObject object = arrayList.get(position3);
                TLRPC.Dialog dialog = new TLRPC.TL_dialog();
                if (object instanceof TLRPC.User) {
                    dialog.id = ((TLRPC.User) object).id;
                } else {
                    dialog.id = -((TLRPC.Chat) object).id;
                }
                return dialog;
            }
            TLObject object2 = ((DialogsSearchAdapter.RecentSearchObject) ShareAlert.this.recentSearchObjects.get(position - this.recentDialogsStartRow)).object;
            TLRPC.Dialog dialog2 = new TLRPC.TL_dialog();
            if (object2 instanceof TLRPC.User) {
                dialog2.id = ((TLRPC.User) object2).id;
            } else {
                dialog2.id = -((TLRPC.Chat) object2).id;
            }
            return dialog2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 1 || holder.getItemViewType() == 4) {
                return false;
            }
            return true;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$5} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$5} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: org.telegram.ui.Cells.ShareDialogCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v14, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v15, resolved type: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$5} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r7, int r8) {
            /*
                r6 = this;
                r0 = 1
                r1 = -1
                switch(r8) {
                    case 0: goto L_0x0086;
                    case 1: goto L_0x0005;
                    case 2: goto L_0x0048;
                    case 3: goto L_0x002d;
                    case 4: goto L_0x0024;
                    default: goto L_0x0005;
                }
            L_0x0005:
                android.view.View r2 = new android.view.View
                android.content.Context r3 = r6.context
                r2.<init>(r3)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r3 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                org.telegram.ui.Components.ShareAlert r4 = org.telegram.ui.Components.ShareAlert.this
                boolean r4 = r4.darkTheme
                if (r4 == 0) goto L_0x00a8
                org.telegram.ui.Components.ShareAlert r4 = org.telegram.ui.Components.ShareAlert.this
                java.lang.String[] r4 = r4.linkToCopy
                r0 = r4[r0]
                if (r0 == 0) goto L_0x00a8
                r0 = 1121583104(0x42da0000, float:109.0)
                goto L_0x00aa
            L_0x0024:
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$5 r0 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$5
                android.content.Context r1 = r6.context
                r0.<init>(r1)
                goto L_0x00b5
            L_0x002d:
                org.telegram.ui.Cells.GraySectionCell r0 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r1 = r6.context
                org.telegram.ui.Components.ShareAlert r2 = org.telegram.ui.Components.ShareAlert.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r2.resourcesProvider
                r0.<init>(r1, r2)
                r1 = 2131627664(0x7f0e0e90, float:1.8882599E38)
                java.lang.String r2 = "Recent"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                r1 = r0
                goto L_0x00b5
            L_0x0048:
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$2 r1 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$2
                android.content.Context r2 = r6.context
                org.telegram.ui.Components.ShareAlert r3 = org.telegram.ui.Components.ShareAlert.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r3.resourcesProvider
                r1.<init>(r2, r3)
                r2 = 0
                r1.setItemAnimator(r2)
                r1.setLayoutAnimation(r2)
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$3 r2 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$3
                android.content.Context r3 = r6.context
                r2.<init>(r3)
                r3 = 0
                r2.setOrientation(r3)
                r1.setLayoutManager(r2)
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$4 r3 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$4
                android.content.Context r4 = r6.context
                org.telegram.ui.Components.ShareAlert r5 = org.telegram.ui.Components.ShareAlert.this
                int r5 = r5.currentAccount
                r3.<init>(r4, r5, r0)
                r6.categoryAdapter = r3
                r1.setAdapter(r3)
                org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda5 r0 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda5
                r0.<init>(r6)
                r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r0)
                r0 = r1
                goto L_0x00b5
            L_0x0086:
                org.telegram.ui.Cells.ShareDialogCell r0 = new org.telegram.ui.Cells.ShareDialogCell
                android.content.Context r2 = r6.context
                org.telegram.ui.Components.ShareAlert r3 = org.telegram.ui.Components.ShareAlert.this
                boolean r3 = r3.darkTheme
                org.telegram.ui.Components.ShareAlert r4 = org.telegram.ui.Components.ShareAlert.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r4.resourcesProvider
                r0.<init>(r2, r3, r4)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r2 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r3 = 1120403456(0x42CLASSNAME, float:100.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r2.<init>((int) r1, (int) r3)
                r0.setLayoutParams(r2)
                goto L_0x00b5
            L_0x00a8:
                r0 = 1113587712(0x42600000, float:56.0)
            L_0x00aa:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r3.<init>((int) r1, (int) r0)
                r2.setLayoutParams(r3)
                r0 = r2
            L_0x00b5:
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* renamed from: lambda$onCreateViewHolder$5$org-telegram-ui-Components-ShareAlert$ShareSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m4351x933a65b8(View view1, int position) {
            TLRPC.TL_topPeer peer = MediaDataController.getInstance(ShareAlert.this.currentAccount).hints.get(position);
            TLRPC.Dialog dialog = new TLRPC.TL_dialog();
            long did = 0;
            if (peer.peer.user_id != 0) {
                did = peer.peer.user_id;
            } else if (peer.peer.channel_id != 0) {
                did = -peer.peer.channel_id;
            } else if (peer.peer.chat_id != 0) {
                did = -peer.peer.chat_id;
            }
            dialog.id = did;
            ShareAlert.this.selectDialog((ShareDialogCell) null, dialog);
            ((HintDialogCell) view1).setChecked(ShareAlert.this.selectedDialogs.indexOfKey(did) >= 0, true);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v0, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v9, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v10, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v11, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v12, resolved type: java.lang.String} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r17, int r18) {
            /*
                r16 = this;
                r0 = r16
                r1 = r18
                int r2 = r17.getItemViewType()
                if (r2 != 0) goto L_0x0155
                r2 = r17
                android.view.View r3 = r2.itemView
                org.telegram.ui.Cells.ShareDialogCell r3 = (org.telegram.ui.Cells.ShareDialogCell) r3
                r4 = 0
                r5 = 0
                java.lang.String r7 = r0.lastSearchText
                boolean r7 = android.text.TextUtils.isEmpty(r7)
                java.lang.String r9 = "windowBackgroundWhiteBlueText4"
                if (r7 == 0) goto L_0x00c6
                int r7 = r0.recentDialogsStartRow
                if (r7 < 0) goto L_0x00b1
                if (r1 < r7) goto L_0x00b1
                int r7 = r1 - r7
                org.telegram.ui.Components.ShareAlert r13 = org.telegram.ui.Components.ShareAlert.this
                java.util.ArrayList r13 = r13.recentSearchObjects
                java.lang.Object r13 = r13.get(r7)
                org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r13 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r13
                org.telegram.tgnet.TLObject r14 = r13.object
                boolean r15 = r14 instanceof org.telegram.tgnet.TLRPC.User
                if (r15 == 0) goto L_0x0046
                r15 = r14
                org.telegram.tgnet.TLRPC$User r15 = (org.telegram.tgnet.TLRPC.User) r15
                long r5 = r15.id
                java.lang.String r10 = r15.first_name
                java.lang.String r11 = r15.last_name
                java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r10, r11)
                r15 = r9
                goto L_0x007c
            L_0x0046:
                boolean r10 = r14 instanceof org.telegram.tgnet.TLRPC.Chat
                if (r10 == 0) goto L_0x0054
                r10 = r14
                org.telegram.tgnet.TLRPC$Chat r10 = (org.telegram.tgnet.TLRPC.Chat) r10
                r15 = r9
                long r8 = r10.id
                long r5 = -r8
                java.lang.String r4 = r10.title
            L_0x0053:
                goto L_0x007c
            L_0x0054:
                r15 = r9
                boolean r8 = r14 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat
                if (r8 == 0) goto L_0x0053
                r8 = r14
                org.telegram.tgnet.TLRPC$TL_encryptedChat r8 = (org.telegram.tgnet.TLRPC.TL_encryptedChat) r8
                org.telegram.ui.Components.ShareAlert r9 = org.telegram.ui.Components.ShareAlert.this
                int r9 = r9.currentAccount
                org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
                long r11 = r8.user_id
                java.lang.Long r11 = java.lang.Long.valueOf(r11)
                org.telegram.tgnet.TLRPC$User r9 = r9.getUser(r11)
                if (r9 == 0) goto L_0x007c
                long r5 = r9.id
                java.lang.String r11 = r9.first_name
                java.lang.String r12 = r9.last_name
                java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r11, r12)
            L_0x007c:
                org.telegram.ui.Adapters.SearchAdapterHelper r8 = r0.searchAdapterHelper
                java.lang.String r8 = r8.getLastFoundUsername()
                boolean r9 = android.text.TextUtils.isEmpty(r8)
                if (r9 != 0) goto L_0x00b1
                if (r4 == 0) goto L_0x00b1
                java.lang.String r9 = r4.toString()
                int r9 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r9, r8)
                r11 = r9
                r10 = -1
                if (r9 == r10) goto L_0x00b1
                android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
                r9.<init>(r4)
                org.telegram.ui.Components.ForegroundColorSpanThemable r10 = new org.telegram.ui.Components.ForegroundColorSpanThemable
                org.telegram.ui.Components.ShareAlert r12 = org.telegram.ui.Components.ShareAlert.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r12.resourcesProvider
                r10.<init>(r15, r12)
                int r12 = r8.length()
                int r12 = r12 + r11
                r15 = 33
                r9.setSpan(r10, r11, r12, r15)
                r4 = r9
            L_0x00b1:
                int r7 = (int) r5
                long r7 = (long) r7
                org.telegram.ui.Components.ShareAlert r9 = org.telegram.ui.Components.ShareAlert.this
                androidx.collection.LongSparseArray r9 = r9.selectedDialogs
                int r9 = r9.indexOfKey(r5)
                if (r9 < 0) goto L_0x00c1
                r10 = 1
                goto L_0x00c2
            L_0x00c1:
                r10 = 0
            L_0x00c2:
                r3.setDialog(r7, r10, r4)
                return
            L_0x00c6:
                r15 = r9
                int r1 = r1 + -1
                java.util.ArrayList<java.lang.Object> r7 = r0.searchResult
                int r7 = r7.size()
                if (r1 >= r7) goto L_0x00e0
                java.util.ArrayList<java.lang.Object> r7 = r0.searchResult
                java.lang.Object r7 = r7.get(r1)
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r7 = (org.telegram.ui.Components.ShareAlert.DialogSearchResult) r7
                org.telegram.tgnet.TLRPC$Dialog r8 = r7.dialog
                long r5 = r8.id
                java.lang.CharSequence r4 = r7.name
                goto L_0x0142
            L_0x00e0:
                java.util.ArrayList<java.lang.Object> r7 = r0.searchResult
                int r7 = r7.size()
                int r1 = r1 - r7
                org.telegram.ui.Adapters.SearchAdapterHelper r7 = r0.searchAdapterHelper
                java.util.ArrayList r7 = r7.getLocalServerSearch()
                java.lang.Object r8 = r7.get(r1)
                org.telegram.tgnet.TLObject r8 = (org.telegram.tgnet.TLObject) r8
                boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC.User
                if (r9 == 0) goto L_0x0105
                r9 = r8
                org.telegram.tgnet.TLRPC$User r9 = (org.telegram.tgnet.TLRPC.User) r9
                long r5 = r9.id
                java.lang.String r12 = r9.first_name
                java.lang.String r13 = r9.last_name
                java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r12, r13)
                goto L_0x010d
            L_0x0105:
                r9 = r8
                org.telegram.tgnet.TLRPC$Chat r9 = (org.telegram.tgnet.TLRPC.Chat) r9
                long r12 = r9.id
                long r5 = -r12
                java.lang.String r4 = r9.title
            L_0x010d:
                org.telegram.ui.Adapters.SearchAdapterHelper r9 = r0.searchAdapterHelper
                java.lang.String r9 = r9.getLastFoundUsername()
                boolean r12 = android.text.TextUtils.isEmpty(r9)
                if (r12 != 0) goto L_0x0142
                if (r4 == 0) goto L_0x0142
                java.lang.String r12 = r4.toString()
                int r12 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r12, r9)
                r13 = r12
                r10 = -1
                if (r12 == r10) goto L_0x0142
                android.text.SpannableStringBuilder r10 = new android.text.SpannableStringBuilder
                r10.<init>(r4)
                org.telegram.ui.Components.ForegroundColorSpanThemable r12 = new org.telegram.ui.Components.ForegroundColorSpanThemable
                org.telegram.ui.Components.ShareAlert r14 = org.telegram.ui.Components.ShareAlert.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r14 = r14.resourcesProvider
                r12.<init>(r15, r14)
                int r14 = r9.length()
                int r14 = r14 + r13
                r11 = 33
                r10.setSpan(r12, r13, r14, r11)
                r4 = r10
            L_0x0142:
                org.telegram.ui.Components.ShareAlert r7 = org.telegram.ui.Components.ShareAlert.this
                androidx.collection.LongSparseArray r7 = r7.selectedDialogs
                int r7 = r7.indexOfKey(r5)
                if (r7 < 0) goto L_0x0150
                r10 = 1
                goto L_0x0151
            L_0x0150:
                r10 = 0
            L_0x0151:
                r3.setDialog(r5, r10, r4)
                goto L_0x0157
            L_0x0155:
                r2 = r17
            L_0x0157:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int position) {
            if (position == this.lastFilledItem) {
                return 4;
            }
            if (position == this.firstEmptyViewCell) {
                return 1;
            }
            if (position == this.hintsCell) {
                return 2;
            }
            if (position == this.resentTitleCell) {
                return 3;
            }
            return 0;
        }

        public boolean isSearching() {
            return !TextUtils.isEmpty(this.lastSearchText);
        }

        public int getSpanSize(int spanCount, int position) {
            if (position == this.hintsCell || position == this.resentTitleCell || position == this.firstEmptyViewCell || position == this.lastFilledItem) {
                return spanCount;
            }
            return 1;
        }
    }

    /* access modifiers changed from: private */
    public void checkCurrentList(boolean force) {
        boolean searchVisibleLocal = false;
        if (!TextUtils.isEmpty(this.searchView.searchEditText.getText()) || (this.keyboardVisible && this.searchView.searchEditText.hasFocus())) {
            searchVisibleLocal = true;
            this.updateSearchAdapter = true;
            AndroidUtilities.updateViewVisibilityAnimated(this.gridView, false, 0.98f, true);
            AndroidUtilities.updateViewVisibilityAnimated(this.searchGridView, true);
        } else {
            AndroidUtilities.updateViewVisibilityAnimated(this.gridView, true, 0.98f, true);
            AndroidUtilities.updateViewVisibilityAnimated(this.searchGridView, false);
        }
        if (this.searchIsVisible != searchVisibleLocal || force) {
            this.searchIsVisible = searchVisibleLocal;
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

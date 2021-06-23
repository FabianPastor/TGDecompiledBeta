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
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
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
import androidx.arch.core.util.Function;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_exportedMessageLink;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
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
    public RectF rect;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public ShareSearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public EmptyTextProgressView searchEmptyView;
    /* access modifiers changed from: private */
    public View selectedCountView;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC$Dialog> selectedDialogs;
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
    private SwitchView switchView;
    /* access modifiers changed from: private */
    public TextPaint textPaint;
    /* access modifiers changed from: private */
    public ValueAnimator topBackgroundAnimator;
    /* access modifiers changed from: private */
    public int topBeforeSwitch;
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

    static /* synthetic */ boolean lambda$new$5(View view, MotionEvent motionEvent) {
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

        public SwitchView(Context context) {
            super(context);
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor(ShareAlert.this.darkTheme ? "voipgroup_searchBackground" : "dialogSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 0.0f, 14.0f, 0.0f));
            AnonymousClass1 r0 = new View(context, ShareAlert.this) {
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
            simpleTextView.setTextColor(Theme.getColor("voipgroup_nameText"));
            this.leftTab.setTextSize(13);
            this.leftTab.setLeftDrawable(NUM);
            this.leftTab.setText(LocaleController.getString("VoipGroupInviteCanSpeak", NUM));
            this.leftTab.setGravity(17);
            addView(this.leftTab, LayoutHelper.createFrame(-1, -1.0f, 51, 14.0f, 0.0f, 0.0f, 0.0f));
            this.leftTab.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ShareAlert.SwitchView.this.lambda$new$0$ShareAlert$SwitchView(view);
                }
            });
            SimpleTextView simpleTextView2 = new SimpleTextView(context);
            this.rightTab = simpleTextView2;
            simpleTextView2.setTextColor(Theme.getColor("voipgroup_nameText"));
            this.rightTab.setTextSize(13);
            this.rightTab.setLeftDrawable(NUM);
            this.rightTab.setText(LocaleController.getString("VoipGroupInviteListenOnly", NUM));
            this.rightTab.setGravity(17);
            addView(this.rightTab, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 14.0f, 0.0f));
            this.rightTab.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ShareAlert.SwitchView.this.lambda$new$1$ShareAlert$SwitchView(view);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ShareAlert$SwitchView(View view) {
            switchToTab(0);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$1 */
        public /* synthetic */ void lambda$new$1$ShareAlert$SwitchView(View view) {
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
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor(ShareAlert.this.darkTheme ? "voipgroup_searchBackground" : "dialogSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(NUM);
            String str = "dialogSearchIcon";
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(ShareAlert.this.darkTheme ? "voipgroup_mutedIcon" : str), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView3.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            String str2 = "voipgroup_searchPlaceholder";
            this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(ShareAlert.this.darkTheme ? str2 : str), PorterDuff.Mode.MULTIPLY));
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ShareAlert.SearchField.this.lambda$new$0$ShareAlert$SearchField(view);
                }
            });
            AnonymousClass1 r0 = new EditTextBoldCursor(context, ShareAlert.this) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.setLocation(obtain.getRawX(), obtain.getRawY() - ShareAlert.this.containerView.getTranslationY());
                    if (obtain.getAction() == 1) {
                        obtain.setAction(3);
                    }
                    ShareAlert.this.gridView.dispatchTouchEvent(obtain);
                    obtain.recycle();
                    return super.dispatchTouchEvent(motionEvent);
                }
            };
            this.searchEditText = r0;
            r0.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor(!ShareAlert.this.darkTheme ? "dialogSearchHint" : str2));
            String str3 = "voipgroup_searchText";
            this.searchEditText.setTextColor(Theme.getColor(ShareAlert.this.darkTheme ? str3 : "dialogSearchText"));
            this.searchEditText.setBackgroundDrawable((Drawable) null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(NUM);
            this.searchEditText.setHint(LocaleController.getString("ShareSendTo", NUM));
            this.searchEditText.setCursorColor(Theme.getColor(!ShareAlert.this.darkTheme ? "featuredStickers_addedIcon" : str3));
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
                    String obj = SearchField.this.searchEditText.getText().toString();
                    if (obj.length() != 0) {
                        if (ShareAlert.this.searchEmptyView != null) {
                            ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", NUM));
                        }
                    } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                        int access$1200 = ShareAlert.this.getCurrentTop();
                        ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", NUM));
                        ShareAlert.this.searchEmptyView.showTextView();
                        ShareAlert.this.gridView.setAdapter(ShareAlert.this.listAdapter);
                        ShareAlert.this.listAdapter.notifyDataSetChanged();
                        if (access$1200 > 0) {
                            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -access$1200);
                        }
                    }
                    if (ShareAlert.this.searchAdapter != null) {
                        ShareAlert.this.searchAdapter.searchDialogs(obj);
                    }
                }
            });
            this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return ShareAlert.SearchField.this.lambda$new$1$ShareAlert$SearchField(textView, i, keyEvent);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ShareAlert$SearchField(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$1 */
        public /* synthetic */ boolean lambda$new$1$ShareAlert$SearchField(TextView textView, int i, KeyEvent keyEvent) {
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
        this(context, (ChatActivity) null, arrayList, str, (String) null, z, str2, (String) null, z2, false);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ShareAlert(android.content.Context r25, org.telegram.ui.ChatActivity r26, java.util.ArrayList<org.telegram.messenger.MessageObject> r27, java.lang.String r28, java.lang.String r29, boolean r30, java.lang.String r31, java.lang.String r32, boolean r33, boolean r34) {
        /*
            r24 = this;
            r0 = r24
            r1 = r25
            r2 = r27
            r3 = r30
            r4 = r34
            r5 = 1
            r0.<init>(r1, r5)
            r6 = 2
            java.lang.String[] r7 = new java.lang.String[r6]
            r0.sendingText = r7
            android.view.View[] r7 = new android.view.View[r6]
            r0.shadow = r7
            android.animation.AnimatorSet[] r7 = new android.animation.AnimatorSet[r6]
            r0.shadowAnimation = r7
            android.util.LongSparseArray r7 = new android.util.LongSparseArray
            r7.<init>()
            r0.selectedDialogs = r7
            android.graphics.RectF r7 = new android.graphics.RectF
            r7.<init>()
            r0.rect = r7
            android.graphics.Paint r7 = new android.graphics.Paint
            r7.<init>(r5)
            r0.paint = r7
            android.text.TextPaint r7 = new android.text.TextPaint
            r7.<init>(r5)
            r0.textPaint = r7
            java.lang.String[] r7 = new java.lang.String[r6]
            r0.linkToCopy = r7
            boolean r7 = r1 instanceof android.app.Activity
            if (r7 == 0) goto L_0x0044
            r7 = r1
            android.app.Activity r7 = (android.app.Activity) r7
            r0.parentActivity = r7
        L_0x0044:
            r0.darkTheme = r4
            r7 = r26
            r0.parentFragment = r7
            android.content.res.Resources r7 = r25.getResources()
            r8 = 2131166024(0x7var_, float:1.7946282E38)
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r8)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r0.shadowDrawable = r7
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            boolean r9 = r0.darkTheme
            java.lang.String r10 = "dialogBackground"
            java.lang.String r11 = "voipgroup_inviteMembersBackground"
            if (r9 == 0) goto L_0x0067
            r9 = r11
            goto L_0x0068
        L_0x0067:
            r9 = r10
        L_0x0068:
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r9, r12)
            r7.setColorFilter(r8)
            r7 = r33
            r0.isFullscreen = r7
            java.lang.String[] r7 = r0.linkToCopy
            r8 = 0
            r7[r8] = r31
            r7[r5] = r32
            r0.sendingMessageObjects = r2
            org.telegram.ui.Components.ShareAlert$ShareSearchAdapter r7 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter
            r7.<init>(r1)
            r0.searchAdapter = r7
            r0.isChannel = r3
            java.lang.String[] r7 = r0.sendingText
            r7[r8] = r28
            r7[r5] = r29
            r0.useSmoothKeyboard = r5
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r0.sendingMessageObjects
            if (r7 == 0) goto L_0x00bc
            int r7 = r7.size()
            r9 = 0
        L_0x009b:
            if (r9 >= r7) goto L_0x00bc
            java.util.ArrayList<org.telegram.messenger.MessageObject> r12 = r0.sendingMessageObjects
            java.lang.Object r12 = r12.get(r9)
            org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
            boolean r13 = r12.isPoll()
            if (r13 == 0) goto L_0x00b9
            boolean r12 = r12.isPublicPoll()
            if (r12 == 0) goto L_0x00b3
            r12 = 2
            goto L_0x00b4
        L_0x00b3:
            r12 = 1
        L_0x00b4:
            r0.hasPoll = r12
            if (r12 != r6) goto L_0x00b9
            goto L_0x00bc
        L_0x00b9:
            int r9 = r9 + 1
            goto L_0x009b
        L_0x00bc:
            if (r3 == 0) goto L_0x00f7
            r0.loadingLink = r5
            org.telegram.tgnet.TLRPC$TL_channels_exportMessageLink r3 = new org.telegram.tgnet.TLRPC$TL_channels_exportMessageLink
            r3.<init>()
            java.lang.Object r7 = r2.get(r8)
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            int r7 = r7.getId()
            r3.id = r7
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            java.lang.Object r2 = r2.get(r8)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer_id
            int r2 = r2.channel_id
            org.telegram.tgnet.TLRPC$InputChannel r2 = r7.getInputChannel((int) r2)
            r3.channel = r2
            int r2 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            org.telegram.ui.Components.-$$Lambda$ShareAlert$FwxuZo6CliUuC7uPACVzrxBEdnU r7 = new org.telegram.ui.Components.-$$Lambda$ShareAlert$FwxuZo6CliUuC7uPACVzrxBEdnU
            r7.<init>(r1)
            r2.sendRequest(r3, r7)
        L_0x00f7:
            org.telegram.ui.Components.ShareAlert$1 r2 = new org.telegram.ui.Components.ShareAlert$1
            r2.<init>(r1)
            r0.containerView = r2
            r2.setWillNotDraw(r8)
            android.view.ViewGroup r3 = r0.containerView
            r3.setClipChildren(r8)
            android.view.ViewGroup r3 = r0.containerView
            int r7 = r0.backgroundPaddingLeft
            r3.setPadding(r7, r8, r7, r8)
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r1)
            r0.frameLayout = r3
            boolean r7 = r0.darkTheme
            if (r7 == 0) goto L_0x011a
            r7 = r11
            goto L_0x011b
        L_0x011a:
            r7 = r10
        L_0x011b:
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r3.setBackgroundColor(r7)
            boolean r3 = r0.darkTheme
            if (r3 == 0) goto L_0x0156
            java.lang.String[] r3 = r0.linkToCopy
            r3 = r3[r5]
            if (r3 == 0) goto L_0x0156
            org.telegram.ui.Components.ShareAlert$2 r3 = new org.telegram.ui.Components.ShareAlert$2
            r3.<init>(r1)
            r0.switchView = r3
            android.widget.FrameLayout r7 = r0.frameLayout
            r9 = -1
            r12 = 1108344832(0x42100000, float:36.0)
            r13 = 51
            r14 = 0
            r15 = 1093664768(0x41300000, float:11.0)
            r16 = 0
            r17 = 0
            r26 = r9
            r27 = r12
            r28 = r13
            r29 = r14
            r30 = r15
            r31 = r16
            r32 = r17
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r7.addView(r3, r9)
        L_0x0156:
            org.telegram.ui.Components.ShareAlert$SearchField r3 = new org.telegram.ui.Components.ShareAlert$SearchField
            r3.<init>(r1)
            android.widget.FrameLayout r7 = r0.frameLayout
            r9 = -1
            r12 = 58
            r13 = 83
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r12, r13)
            r7.addView(r3, r14)
            org.telegram.ui.Components.ShareAlert$3 r7 = new org.telegram.ui.Components.ShareAlert$3
            r7.<init>(r1)
            r0.gridView = r7
            r14 = 13
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r7.setTag(r14)
            org.telegram.ui.Components.RecyclerListView r7 = r0.gridView
            r14 = 1111490560(0x42400000, float:48.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r7.setPadding(r8, r8, r8, r15)
            org.telegram.ui.Components.RecyclerListView r7 = r0.gridView
            r7.setClipToPadding(r8)
            org.telegram.ui.Components.RecyclerListView r7 = r0.gridView
            androidx.recyclerview.widget.GridLayoutManager r15 = new androidx.recyclerview.widget.GridLayoutManager
            android.content.Context r12 = r24.getContext()
            r6 = 4
            r15.<init>(r12, r6)
            r0.layoutManager = r15
            r7.setLayoutManager(r15)
            androidx.recyclerview.widget.GridLayoutManager r7 = r0.layoutManager
            org.telegram.ui.Components.ShareAlert$4 r12 = new org.telegram.ui.Components.ShareAlert$4
            r12.<init>()
            r7.setSpanSizeLookup(r12)
            org.telegram.ui.Components.RecyclerListView r7 = r0.gridView
            r7.setHorizontalScrollBarEnabled(r8)
            org.telegram.ui.Components.RecyclerListView r7 = r0.gridView
            r7.setVerticalScrollBarEnabled(r8)
            org.telegram.ui.Components.RecyclerListView r7 = r0.gridView
            org.telegram.ui.Components.ShareAlert$5 r12 = new org.telegram.ui.Components.ShareAlert$5
            r12.<init>()
            r7.addItemDecoration(r12)
            android.view.ViewGroup r7 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r12 = r0.gridView
            r15 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r27 = r15
            r28 = r17
            r29 = r18
            r30 = r19
            r31 = r20
            r32 = r21
            r33 = r22
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r7.addView(r12, r15)
            org.telegram.ui.Components.RecyclerListView r7 = r0.gridView
            org.telegram.ui.Components.ShareAlert$ShareDialogsAdapter r12 = new org.telegram.ui.Components.ShareAlert$ShareDialogsAdapter
            r12.<init>(r1)
            r0.listAdapter = r12
            r7.setAdapter(r12)
            org.telegram.ui.Components.RecyclerListView r7 = r0.gridView
            boolean r12 = r0.darkTheme
            if (r12 == 0) goto L_0x01f2
            r12 = r11
            goto L_0x01f4
        L_0x01f2:
            java.lang.String r12 = "dialogScrollGlow"
        L_0x01f4:
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r7.setGlowColor(r12)
            org.telegram.ui.Components.RecyclerListView r7 = r0.gridView
            org.telegram.ui.Components.-$$Lambda$ShareAlert$tsqYP84rsmwyZrf_ARrZIDFFQec r12 = new org.telegram.ui.Components.-$$Lambda$ShareAlert$tsqYP84rsmwyZrf_ARrZIDFFQec
            r12.<init>(r3)
            r7.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r12)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            org.telegram.ui.Components.ShareAlert$6 r7 = new org.telegram.ui.Components.ShareAlert$6
            r7.<init>()
            r3.setOnScrollListener(r7)
            org.telegram.ui.Components.EmptyTextProgressView r3 = new org.telegram.ui.Components.EmptyTextProgressView
            r3.<init>(r1)
            r0.searchEmptyView = r3
            r3.setShowAtCenter(r5)
            org.telegram.ui.Components.EmptyTextProgressView r3 = r0.searchEmptyView
            r3.showTextView()
            org.telegram.ui.Components.EmptyTextProgressView r3 = r0.searchEmptyView
            r7 = 2131626304(0x7f0e0940, float:1.887984E38)
            java.lang.String r12 = "NoChats"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r12, r7)
            r3.setText(r7)
            org.telegram.ui.Components.RecyclerListView r3 = r0.gridView
            org.telegram.ui.Components.EmptyTextProgressView r7 = r0.searchEmptyView
            r3.setEmptyView(r7)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.EmptyTextProgressView r7 = r0.searchEmptyView
            r12 = -1
            r15 = -1082130432(0xffffffffbvar_, float:-1.0)
            r17 = 51
            r18 = 0
            r19 = 1112539136(0x42500000, float:52.0)
            r20 = 0
            r21 = 0
            r27 = r12
            r28 = r15
            r29 = r17
            r30 = r18
            r31 = r19
            r32 = r20
            r33 = r21
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r3.addView(r7, r12)
            android.widget.FrameLayout$LayoutParams r3 = new android.widget.FrameLayout$LayoutParams
            int r7 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r12 = 51
            r3.<init>(r9, r7, r12)
            boolean r7 = r0.darkTheme
            if (r7 == 0) goto L_0x0271
            java.lang.String[] r7 = r0.linkToCopy
            r7 = r7[r5]
            if (r7 == 0) goto L_0x0271
            r7 = 1121845248(0x42de0000, float:111.0)
            goto L_0x0273
        L_0x0271:
            r7 = 1114112000(0x42680000, float:58.0)
        L_0x0273:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r3.topMargin = r7
            android.view.View[] r7 = r0.shadow
            android.view.View r15 = new android.view.View
            r15.<init>(r1)
            r7[r8] = r15
            android.view.View[] r7 = r0.shadow
            r7 = r7[r8]
            java.lang.String r15 = "dialogShadowLine"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r7.setBackgroundColor(r6)
            android.view.View[] r6 = r0.shadow
            r6 = r6[r8]
            r7 = 0
            r6.setAlpha(r7)
            android.view.View[] r6 = r0.shadow
            r6 = r6[r8]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r5)
            r6.setTag(r7)
            android.view.ViewGroup r6 = r0.containerView
            android.view.View[] r7 = r0.shadow
            r7 = r7[r8]
            r6.addView(r7, r3)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.FrameLayout r6 = r0.frameLayout
            boolean r7 = r0.darkTheme
            if (r7 == 0) goto L_0x02bc
            java.lang.String[] r7 = r0.linkToCopy
            r7 = r7[r5]
            if (r7 == 0) goto L_0x02bc
            r7 = 111(0x6f, float:1.56E-43)
            goto L_0x02be
        L_0x02bc:
            r7 = 58
        L_0x02be:
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r7, r12)
            r3.addView(r6, r7)
            android.widget.FrameLayout$LayoutParams r3 = new android.widget.FrameLayout$LayoutParams
            int r6 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r3.<init>(r9, r6, r13)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r3.bottomMargin = r6
            android.view.View[] r6 = r0.shadow
            android.view.View r7 = new android.view.View
            r7.<init>(r1)
            r6[r5] = r7
            android.view.View[] r6 = r0.shadow
            r6 = r6[r5]
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r6.setBackgroundColor(r7)
            android.view.ViewGroup r6 = r0.containerView
            android.view.View[] r7 = r0.shadow
            r7 = r7[r5]
            r6.addView(r7, r3)
            boolean r3 = r0.isChannel
            java.lang.String r6 = "fonts/rmedium.ttf"
            if (r3 != 0) goto L_0x0308
            java.lang.String[] r3 = r0.linkToCopy
            r3 = r3[r8]
            if (r3 == 0) goto L_0x02fe
            goto L_0x0308
        L_0x02fe:
            android.view.View[] r3 = r0.shadow
            r3 = r3[r5]
            r7 = 0
            r3.setAlpha(r7)
            goto L_0x04b0
        L_0x0308:
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r1)
            r0.pickerBottomLayout = r3
            boolean r7 = r0.darkTheme
            if (r7 == 0) goto L_0x0314
            r10 = r11
        L_0x0314:
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            boolean r10 = r0.darkTheme
            java.lang.String r11 = "voipgroup_listSelector"
            java.lang.String r12 = "listSelectorSDK21"
            if (r10 == 0) goto L_0x0322
            r10 = r11
            goto L_0x0323
        L_0x0322:
            r10 = r12
        L_0x0323:
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createSelectorWithBackgroundDrawable(r7, r10)
            r3.setBackgroundDrawable(r7)
            android.widget.TextView r3 = r0.pickerBottomLayout
            boolean r7 = r0.darkTheme
            java.lang.String r10 = "voipgroup_listeningText"
            java.lang.String r14 = "dialogTextBlue2"
            if (r7 == 0) goto L_0x033a
            r7 = r10
            goto L_0x033b
        L_0x033a:
            r7 = r14
        L_0x033b:
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r3.setTextColor(r7)
            android.widget.TextView r3 = r0.pickerBottomLayout
            r7 = 1096810496(0x41600000, float:14.0)
            r3.setTextSize(r5, r7)
            android.widget.TextView r3 = r0.pickerBottomLayout
            r7 = 1099956224(0x41900000, float:18.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r15 = 1099956224(0x41900000, float:18.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r3.setPadding(r7, r8, r15, r8)
            android.widget.TextView r3 = r0.pickerBottomLayout
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r3.setTypeface(r7)
            android.widget.TextView r3 = r0.pickerBottomLayout
            r7 = 17
            r3.setGravity(r7)
            boolean r3 = r0.darkTheme
            if (r3 == 0) goto L_0x0387
            java.lang.String[] r3 = r0.linkToCopy
            r3 = r3[r5]
            if (r3 == 0) goto L_0x0387
            android.widget.TextView r3 = r0.pickerBottomLayout
            r7 = 2131628113(0x7f0e1051, float:1.888351E38)
            java.lang.String r15 = "VoipGroupCopySpeakerLink"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r15, r7)
            java.lang.String r7 = r7.toUpperCase()
            r3.setText(r7)
            goto L_0x0399
        L_0x0387:
            android.widget.TextView r3 = r0.pickerBottomLayout
            r7 = 2131625014(0x7f0e0436, float:1.8877224E38)
            java.lang.String r15 = "CopyLink"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r15, r7)
            java.lang.String r7 = r7.toUpperCase()
            r3.setText(r7)
        L_0x0399:
            android.widget.TextView r3 = r0.pickerBottomLayout
            org.telegram.ui.Components.-$$Lambda$ShareAlert$Jk_mrfDjAsna0QY0gGjdECS7var_ r7 = new org.telegram.ui.Components.-$$Lambda$ShareAlert$Jk_mrfDjAsna0QY0gGjdECS7var_
            r7.<init>()
            r3.setOnClickListener(r7)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.TextView r7 = r0.pickerBottomLayout
            r15 = 48
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r15, r13)
            r3.addView(r7, r15)
            org.telegram.ui.ChatActivity r3 = r0.parentFragment
            if (r3 == 0) goto L_0x04b0
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getCurrentChat()
            boolean r3 = org.telegram.messenger.ChatObject.hasAdminRights(r3)
            if (r3 == 0) goto L_0x04b0
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.sendingMessageObjects
            int r3 = r3.size()
            if (r3 <= 0) goto L_0x04b0
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.sendingMessageObjects
            java.lang.Object r3 = r3.get(r8)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            int r3 = r3.forwards
            if (r3 <= 0) goto L_0x04b0
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.sendingMessageObjects
            java.lang.Object r3 = r3.get(r8)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            boolean r7 = r3.isForwarded()
            if (r7 != 0) goto L_0x04b0
            android.widget.LinearLayout r7 = new android.widget.LinearLayout
            r7.<init>(r1)
            r0.sharesCountLayout = r7
            r7.setOrientation(r8)
            android.widget.LinearLayout r7 = r0.sharesCountLayout
            r15 = 16
            r7.setGravity(r15)
            android.widget.LinearLayout r7 = r0.sharesCountLayout
            boolean r15 = r0.darkTheme
            if (r15 == 0) goto L_0x03fa
            goto L_0x03fb
        L_0x03fa:
            r11 = r12
        L_0x03fb:
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r12 = 2
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r11, r12)
            r7.setBackgroundDrawable(r11)
            android.view.ViewGroup r7 = r0.containerView
            android.widget.LinearLayout r11 = r0.sharesCountLayout
            r17 = -2
            r18 = 1111490560(0x42400000, float:48.0)
            r19 = 85
            r20 = 1086324736(0x40CLASSNAME, float:6.0)
            r21 = 0
            r22 = -1061158912(0xffffffffc0CLASSNAME, float:-6.0)
            r23 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r7.addView(r11, r12)
            android.widget.LinearLayout r7 = r0.sharesCountLayout
            org.telegram.ui.Components.-$$Lambda$ShareAlert$U4F_1ljMicnLTo1_tjEwp5yQbGc r11 = new org.telegram.ui.Components.-$$Lambda$ShareAlert$U4F_1ljMicnLTo1_tjEwp5yQbGc
            r11.<init>(r3)
            r7.setOnClickListener(r11)
            android.widget.ImageView r7 = new android.widget.ImageView
            r7.<init>(r1)
            r11 = 2131166022(0x7var_, float:1.7946278E38)
            r7.setImageResource(r11)
            android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
            boolean r12 = r0.darkTheme
            if (r12 == 0) goto L_0x043d
            r12 = r10
            goto L_0x043e
        L_0x043d:
            r12 = r14
        L_0x043e:
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.MULTIPLY
            r11.<init>(r12, r15)
            r7.setColorFilter(r11)
            android.widget.LinearLayout r11 = r0.sharesCountLayout
            r17 = -2
            r18 = -1
            r19 = 16
            r20 = 20
            r21 = 0
            r22 = 0
            r23 = 0
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
            r11.addView(r7, r12)
            android.widget.TextView r7 = new android.widget.TextView
            r7.<init>(r1)
            java.lang.Object[] r11 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            int r3 = r3.forwards
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r11[r8] = r3
            java.lang.String r3 = "%d"
            java.lang.String r3 = java.lang.String.format(r3, r11)
            r7.setText(r3)
            r3 = 1096810496(0x41600000, float:14.0)
            r7.setTextSize(r5, r3)
            boolean r3 = r0.darkTheme
            if (r3 == 0) goto L_0x0485
            goto L_0x0486
        L_0x0485:
            r10 = r14
        L_0x0486:
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r7.setTextColor(r3)
            r3 = 16
            r7.setGravity(r3)
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r7.setTypeface(r3)
            android.widget.LinearLayout r3 = r0.sharesCountLayout
            r17 = -2
            r18 = -1
            r19 = 16
            r20 = 8
            r21 = 0
            r22 = 20
            r23 = 0
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
            r3.addView(r7, r10)
        L_0x04b0:
            org.telegram.ui.Components.ShareAlert$7 r3 = new org.telegram.ui.Components.ShareAlert$7
            r3.<init>(r1)
            r0.frameLayout2 = r3
            r3.setWillNotDraw(r8)
            android.widget.FrameLayout r3 = r0.frameLayout2
            r7 = 0
            r3.setAlpha(r7)
            android.widget.FrameLayout r3 = r0.frameLayout2
            r7 = 4
            r3.setVisibility(r7)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.FrameLayout r7 = r0.frameLayout2
            r10 = -2
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r13)
            r3.addView(r7, r9)
            android.widget.FrameLayout r3 = r0.frameLayout2
            org.telegram.ui.Components.-$$Lambda$ShareAlert$T1ALJm93IMMnbzuP0RS6nBlxlrM r7 = org.telegram.ui.Components.$$Lambda$ShareAlert$T1ALJm93IMMnbzuP0RS6nBlxlrM.INSTANCE
            r3.setOnTouchListener(r7)
            org.telegram.ui.Components.ShareAlert$8 r3 = new org.telegram.ui.Components.ShareAlert$8
            r7 = 0
            r9 = 1
            r26 = r3
            r27 = r24
            r28 = r25
            r29 = r2
            r30 = r7
            r31 = r9
            r26.<init>(r28, r29, r30, r31)
            r0.commentTextView = r3
            if (r4 == 0) goto L_0x04fd
            org.telegram.ui.Components.EditTextCaption r2 = r3.getEditText()
            java.lang.String r3 = "voipgroup_nameText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
        L_0x04fd:
            org.telegram.ui.Components.EditTextEmoji r2 = r0.commentTextView
            r3 = 2131627473(0x7f0e0dd1, float:1.8882211E38)
            java.lang.String r4 = "ShareComment"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setHint(r3)
            org.telegram.ui.Components.EditTextEmoji r2 = r0.commentTextView
            r2.onResume()
            android.widget.FrameLayout r2 = r0.frameLayout2
            org.telegram.ui.Components.EditTextEmoji r3 = r0.commentTextView
            r9 = -1
            r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r11 = 51
            r12 = 0
            r13 = 0
            r14 = 1118306304(0x42a80000, float:84.0)
            r15 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r2.addView(r3, r4)
            android.widget.FrameLayout r2 = r0.frameLayout2
            r2.setClipChildren(r8)
            org.telegram.ui.Components.EditTextEmoji r2 = r0.commentTextView
            r2.setClipChildren(r8)
            org.telegram.ui.Components.ShareAlert$9 r2 = new org.telegram.ui.Components.ShareAlert$9
            r2.<init>(r1)
            r0.writeButtonContainer = r2
            r2.setFocusable(r5)
            android.widget.FrameLayout r2 = r0.writeButtonContainer
            r2.setFocusableInTouchMode(r5)
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
            r7 = 1114636288(0x42700000, float:60.0)
            r9 = 85
            r10 = 0
            r11 = 0
            r12 = 1086324736(0x40CLASSNAME, float:6.0)
            r13 = 1092616192(0x41200000, float:10.0)
            r26 = r5
            r27 = r7
            r28 = r9
            r29 = r10
            r30 = r11
            r31 = r12
            r32 = r13
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r2.addView(r4, r5)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r4 = 1113587712(0x42600000, float:56.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            java.lang.String r7 = "dialogFloatingButton"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            int r10 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r10 < r11) goto L_0x0595
            java.lang.String r7 = "dialogFloatingButtonPressed"
        L_0x0595:
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r5, r9, r7)
            if (r10 >= r11) goto L_0x05cb
            android.content.res.Resources r7 = r25.getResources()
            r9 = 2131165412(0x7var_e4, float:1.794504E38)
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r9)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            r12 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r12, r13)
            r7.setColorFilter(r9)
            org.telegram.ui.Components.CombinedDrawable r9 = new org.telegram.ui.Components.CombinedDrawable
            r9.<init>(r7, r5, r8, r8)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r9.setIconSize(r5, r7)
            r5 = r9
        L_0x05cb:
            r2.setBackgroundDrawable(r5)
            r5 = 2131165269(0x7var_, float:1.794475E38)
            r2.setImageResource(r5)
            r5 = 2
            r2.setImportantForAccessibility(r5)
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            java.lang.String r7 = "dialogFloatingIcon"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r7, r9)
            r2.setColorFilter(r5)
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r5)
            if (r10 < r11) goto L_0x05f7
            org.telegram.ui.Components.ShareAlert$10 r5 = new org.telegram.ui.Components.ShareAlert$10
            r5.<init>()
            r2.setOutlineProvider(r5)
        L_0x05f7:
            android.widget.FrameLayout r5 = r0.writeButtonContainer
            if (r10 < r11) goto L_0x05fe
            r7 = 56
            goto L_0x0600
        L_0x05fe:
            r7 = 60
        L_0x0600:
            if (r10 < r11) goto L_0x0603
            goto L_0x0605
        L_0x0603:
            r4 = 1114636288(0x42700000, float:60.0)
        L_0x0605:
            r9 = 51
            if (r10 < r11) goto L_0x060c
            r10 = 1073741824(0x40000000, float:2.0)
            goto L_0x060d
        L_0x060c:
            r10 = 0
        L_0x060d:
            r11 = 0
            r12 = 0
            r13 = 0
            r26 = r7
            r27 = r4
            r28 = r9
            r29 = r10
            r30 = r11
            r31 = r12
            r32 = r13
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r5.addView(r2, r4)
            org.telegram.ui.Components.-$$Lambda$ShareAlert$Uko9yg1NoarQ9lJ2Fv56y__kpL8 r4 = new org.telegram.ui.Components.-$$Lambda$ShareAlert$Uko9yg1NoarQ9lJ2Fv56y__kpL8
            r4.<init>()
            r2.setOnClickListener(r4)
            android.text.TextPaint r2 = r0.textPaint
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.setTextSize(r4)
            android.text.TextPaint r2 = r0.textPaint
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r2.setTypeface(r4)
            org.telegram.ui.Components.ShareAlert$11 r2 = new org.telegram.ui.Components.ShareAlert$11
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
            r9 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r10 = 1091567616(0x41100000, float:9.0)
            r25 = r3
            r26 = r4
            r27 = r5
            r28 = r6
            r29 = r7
            r30 = r9
            r31 = r10
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r1.addView(r2, r3)
            r0.updateSelectedCount(r8)
            int r1 = r0.currentAccount
            org.telegram.messenger.AccountInstance r1 = org.telegram.messenger.AccountInstance.getInstance(r1)
            org.telegram.ui.DialogsActivity.loadDialogs(r1)
            org.telegram.ui.Components.ShareAlert$ShareDialogsAdapter r1 = r0.listAdapter
            java.util.ArrayList r1 = r1.dialogs
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x069f
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            r1.addObserver(r0, r2)
        L_0x069f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.<init>(android.content.Context, org.telegram.ui.ChatActivity, java.util.ArrayList, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$ShareAlert(Context context, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, context) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ Context f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ShareAlert.this.lambda$new$0$ShareAlert(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ShareAlert(TLObject tLObject, Context context) {
        if (tLObject != null) {
            this.exportedMessageLink = (TLRPC$TL_exportedMessageLink) tLObject;
            if (this.copyLinkOnEnd) {
                copyLink(context);
            }
        }
        this.loadingLink = false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$ShareAlert(SearchField searchField, View view, int i) {
        TLRPC$Dialog tLRPC$Dialog;
        int i2;
        if (i >= 0) {
            RecyclerView.Adapter adapter = this.gridView.getAdapter();
            ShareDialogsAdapter shareDialogsAdapter = this.listAdapter;
            if (adapter == shareDialogsAdapter) {
                tLRPC$Dialog = shareDialogsAdapter.getItem(i);
            } else {
                tLRPC$Dialog = this.searchAdapter.getItem(i);
            }
            if (tLRPC$Dialog != null) {
                if (this.hasPoll != 0 && (i2 = (int) tLRPC$Dialog.id) < 0) {
                    TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i2));
                    boolean z = ChatObject.isChannel(chat) && this.hasPoll == 2 && !chat.megagroup;
                    if (z || !ChatObject.canSendPolls(chat)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                ShareDialogCell shareDialogCell = (ShareDialogCell) view;
                if (this.selectedDialogs.indexOfKey(tLRPC$Dialog.id) >= 0) {
                    this.selectedDialogs.remove(tLRPC$Dialog.id);
                    shareDialogCell.setChecked(false, true);
                    updateSelectedCount(1);
                    return;
                }
                this.selectedDialogs.put(tLRPC$Dialog.id, tLRPC$Dialog);
                shareDialogCell.setChecked(true, true);
                updateSelectedCount(2);
                int i3 = UserConfig.getInstance(this.currentAccount).clientUserId;
                if (this.gridView.getAdapter() == this.searchAdapter) {
                    TLRPC$Dialog tLRPC$Dialog2 = (TLRPC$Dialog) this.listAdapter.dialogsMap.get(tLRPC$Dialog.id);
                    if (tLRPC$Dialog2 == null) {
                        this.listAdapter.dialogsMap.put(tLRPC$Dialog.id, tLRPC$Dialog);
                        this.listAdapter.dialogs.add(this.listAdapter.dialogs.isEmpty() ^ true ? 1 : 0, tLRPC$Dialog);
                    } else if (tLRPC$Dialog2.id != ((long) i3)) {
                        this.listAdapter.dialogs.remove(tLRPC$Dialog2);
                        this.listAdapter.dialogs.add(this.listAdapter.dialogs.isEmpty() ^ true ? 1 : 0, tLRPC$Dialog2);
                    }
                    searchField.searchEditText.setText("");
                    this.gridView.setAdapter(this.listAdapter);
                    searchField.hideKeyboard();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$ShareAlert(View view) {
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
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$ShareAlert(MessageObject messageObject, View view) {
        this.parentFragment.presentFragment(new MessageStatisticActivity(messageObject));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$6 */
    public /* synthetic */ void lambda$new$6$ShareAlert(View view) {
        int i = 0;
        int i2 = 0;
        while (true) {
            boolean z = true;
            if (i2 < this.selectedDialogs.size()) {
                long keyAt = this.selectedDialogs.keyAt(i2);
                Context context = getContext();
                int i3 = this.currentAccount;
                if (this.frameLayout2.getTag() == null || this.commentTextView.length() <= 0) {
                    z = false;
                }
                if (!AlertsCreator.checkSlowMode(context, i3, keyAt, z)) {
                    i2++;
                } else {
                    return;
                }
            } else {
                if (this.sendingMessageObjects != null) {
                    while (i < this.selectedDialogs.size()) {
                        long keyAt2 = this.selectedDialogs.keyAt(i);
                        if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), keyAt2, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                        }
                        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingMessageObjects, keyAt2, true, 0);
                        i++;
                    }
                    onSend(this.selectedDialogs, this.sendingMessageObjects.size());
                } else {
                    SwitchView switchView2 = this.switchView;
                    int access$7500 = switchView2 != null ? switchView2.currentTab : 0;
                    if (this.sendingText[access$7500] != null) {
                        while (i < this.selectedDialogs.size()) {
                            long keyAt3 = this.selectedDialogs.keyAt(i);
                            if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), keyAt3, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                            }
                            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingText[access$7500], keyAt3, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
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
        if (holder.getAdapterPosition() == 0 && childAt.getTop() >= 0) {
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
        if (this.gridView.getChildCount() > 0 && !this.panTranslationMoveLayout) {
            View childAt = this.gridView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(childAt);
            int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
            int i = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
            if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
                runShadowAnimation(0, true);
                top = i;
            } else {
                runShadowAnimation(0, false);
            }
            int i2 = this.scrollOffsetY;
            if (i2 != top) {
                this.previousScrollOffsetY = i2;
                RecyclerListView recyclerListView = this.gridView;
                int i3 = (int) (((float) top) + this.currentPanTranslationY);
                this.scrollOffsetY = i3;
                recyclerListView.setTopGlowOffset(i3);
                this.frameLayout.setTranslationY(((float) this.scrollOffsetY) + this.currentPanTranslationY);
                this.searchEmptyView.setTranslationY(((float) this.scrollOffsetY) + this.currentPanTranslationY);
                this.containerView.invalidate();
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
                    ((LaunchActivity) this.parentActivity).showBulletin(new Function(z) {
                        public final /* synthetic */ boolean f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final Object apply(Object obj) {
                            return ((BulletinFactory) obj).createCopyLinkBulletin(this.f$0);
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
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

        /* JADX WARNING: Code restructure failed: missing block: B:8:0x006c, code lost:
            r5 = r4.id;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void fetchDialogs() {
            /*
                r9 = this;
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r9.dialogs
                r0.clear()
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r9.dialogsMap
                r0.clear()
                org.telegram.ui.Components.ShareAlert r0 = org.telegram.ui.Components.ShareAlert.this
                int r0 = r0.currentAccount
                org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
                int r0 = r0.clientUserId
                org.telegram.ui.Components.ShareAlert r1 = org.telegram.ui.Components.ShareAlert.this
                int r1 = r1.currentAccount
                org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r1 = r1.dialogsForward
                boolean r1 = r1.isEmpty()
                r2 = 0
                if (r1 != 0) goto L_0x0047
                org.telegram.ui.Components.ShareAlert r1 = org.telegram.ui.Components.ShareAlert.this
                int r1 = r1.currentAccount
                org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r1 = r1.dialogsForward
                java.lang.Object r1 = r1.get(r2)
                org.telegram.tgnet.TLRPC$Dialog r1 = (org.telegram.tgnet.TLRPC$Dialog) r1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r3 = r9.dialogs
                r3.add(r1)
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r3 = r9.dialogsMap
                long r4 = r1.id
                r3.put(r4, r1)
            L_0x0047:
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                org.telegram.ui.Components.ShareAlert r3 = org.telegram.ui.Components.ShareAlert.this
                int r3 = r3.currentAccount
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
                java.util.ArrayList r3 = r3.getAllDialogs()
            L_0x005a:
                int r4 = r3.size()
                if (r2 >= r4) goto L_0x00e6
                java.lang.Object r4 = r3.get(r2)
                org.telegram.tgnet.TLRPC$Dialog r4 = (org.telegram.tgnet.TLRPC$Dialog) r4
                boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_dialog
                if (r5 != 0) goto L_0x006c
                goto L_0x00e2
            L_0x006c:
                long r5 = r4.id
                int r7 = (int) r5
                if (r7 != r0) goto L_0x0073
                goto L_0x00e2
            L_0x0073:
                r8 = 32
                long r5 = r5 >> r8
                int r6 = (int) r5
                if (r7 == 0) goto L_0x00e2
                r5 = 1
                if (r6 == r5) goto L_0x00e2
                if (r7 <= 0) goto L_0x0093
                int r6 = r4.folder_id
                if (r6 != r5) goto L_0x0086
                r1.add(r4)
                goto L_0x008b
            L_0x0086:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r5 = r9.dialogs
                r5.add(r4)
            L_0x008b:
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r5 = r9.dialogsMap
                long r6 = r4.id
                r5.put(r6, r4)
                goto L_0x00e2
            L_0x0093:
                org.telegram.ui.Components.ShareAlert r6 = org.telegram.ui.Components.ShareAlert.this
                int r6 = r6.currentAccount
                org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
                int r7 = -r7
                java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
                org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
                if (r6 == 0) goto L_0x00e2
                boolean r7 = org.telegram.messenger.ChatObject.isNotInChat(r6)
                if (r7 != 0) goto L_0x00e2
                boolean r7 = r6.gigagroup
                if (r7 == 0) goto L_0x00b8
                boolean r7 = org.telegram.messenger.ChatObject.hasAdminRights(r6)
                if (r7 == 0) goto L_0x00e2
            L_0x00b8:
                boolean r7 = org.telegram.messenger.ChatObject.isChannel(r6)
                if (r7 == 0) goto L_0x00ce
                boolean r7 = r6.creator
                if (r7 != 0) goto L_0x00ce
                org.telegram.tgnet.TLRPC$TL_chatAdminRights r7 = r6.admin_rights
                if (r7 == 0) goto L_0x00ca
                boolean r7 = r7.post_messages
                if (r7 != 0) goto L_0x00ce
            L_0x00ca:
                boolean r6 = r6.megagroup
                if (r6 == 0) goto L_0x00e2
            L_0x00ce:
                int r6 = r4.folder_id
                if (r6 != r5) goto L_0x00d6
                r1.add(r4)
                goto L_0x00db
            L_0x00d6:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r5 = r9.dialogs
                r5.add(r4)
            L_0x00db:
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r5 = r9.dialogsMap
                long r6 = r4.id
                r5.put(r6, r4)
            L_0x00e2:
                int r2 = r2 + 1
                goto L_0x005a
            L_0x00e6:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r9.dialogs
                r0.addAll(r1)
                r9.notifyDataSetChanged()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareDialogsAdapter.fetchDialogs():void");
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
                view = new ShareDialogCell(this.context, ShareAlert.this.darkTheme ? 1 : 0);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ShareDialogCell shareDialogCell = (ShareDialogCell) viewHolder.itemView;
                TLRPC$Dialog item = getItem(i);
                shareDialogCell.setDialog((int) item.id, ShareAlert.this.selectedDialogs.indexOfKey(item.id) >= 0, (CharSequence) null);
            }
        }
    }

    public class ShareSearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        /* access modifiers changed from: private */
        public int lastGlobalSearchId;
        /* access modifiers changed from: private */
        public int lastLocalSearchId;
        /* access modifiers changed from: private */
        public int lastSearchId;
        private String lastSearchText;
        private SearchAdapterHelper searchAdapterHelper;
        /* access modifiers changed from: private */
        public ArrayList<Object> searchResult = new ArrayList<>();
        private Runnable searchRunnable;
        private Runnable searchRunnable2;
        private int waitingResponseCount;

        public int getItemViewType(int i) {
            return i == 0 ? 1 : 0;
        }

        static /* synthetic */ int access$8210(ShareSearchAdapter shareSearchAdapter) {
            int i = shareSearchAdapter.waitingResponseCount;
            shareSearchAdapter.waitingResponseCount = i - 1;
            return i;
        }

        public ShareSearchAdapter(Context context2) {
            this.context = context2;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate(ShareAlert.this) {
                public /* synthetic */ SparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                public /* synthetic */ SparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }

                public void onDataSetChanged(int i) {
                    ShareSearchAdapter.access$8210(ShareSearchAdapter.this);
                    int unused = ShareSearchAdapter.this.lastGlobalSearchId = i;
                    if (ShareSearchAdapter.this.lastLocalSearchId != i) {
                        ShareSearchAdapter.this.searchResult.clear();
                    }
                    ShareSearchAdapter.this.notifyDataSetChanged();
                }

                public boolean canApplySearchResults(int i) {
                    return i == ShareSearchAdapter.this.lastSearchId;
                }
            });
        }

        private void searchDialogsInternal(String str, int i) {
            MessagesStorage.getInstance(ShareAlert.this.currentAccount).getStorageQueue().postRunnable(new Runnable(str, i) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ShareAlert.ShareSearchAdapter.this.lambda$searchDialogsInternal$1$ShareAlert$ShareSearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* JADX WARNING: type inference failed for: r11v24 */
        /* JADX WARNING: type inference failed for: r11v26 */
        /* JADX WARNING: type inference failed for: r11v30 */
        /* JADX WARNING: type inference failed for: r11v32 */
        /* access modifiers changed from: private */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:173:0x03fd A[Catch:{ Exception -> 0x0419 }, LOOP:7: B:145:0x034a->B:173:0x03fd, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:191:0x0163 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:212:0x0394 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:75:0x01ce A[Catch:{ Exception -> 0x0419 }, LOOP:2: B:46:0x0110->B:75:0x01ce, LOOP_END] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* renamed from: lambda$searchDialogsInternal$1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$searchDialogsInternal$1$ShareAlert$ShareSearchAdapter(java.lang.String r21, int r22) {
            /*
                r20 = this;
                r1 = r20
                java.lang.String r0 = r21.trim()     // Catch:{ Exception -> 0x0419 }
                java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0419 }
                int r2 = r0.length()     // Catch:{ Exception -> 0x0419 }
                r3 = -1
                if (r2 != 0) goto L_0x001e
                r1.lastSearchId = r3     // Catch:{ Exception -> 0x0419 }
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0419 }
                r0.<init>()     // Catch:{ Exception -> 0x0419 }
                int r2 = r1.lastSearchId     // Catch:{ Exception -> 0x0419 }
                r1.updateSearchResults(r0, r2)     // Catch:{ Exception -> 0x0419 }
                return
            L_0x001e:
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0419 }
                java.lang.String r2 = r2.getTranslitString(r0)     // Catch:{ Exception -> 0x0419 }
                boolean r4 = r0.equals(r2)     // Catch:{ Exception -> 0x0419 }
                if (r4 != 0) goto L_0x0032
                int r4 = r2.length()     // Catch:{ Exception -> 0x0419 }
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
                java.lang.String[] r8 = new java.lang.String[r7]     // Catch:{ Exception -> 0x0419 }
                r8[r6] = r0     // Catch:{ Exception -> 0x0419 }
                if (r2 == 0) goto L_0x0043
                r8[r4] = r2     // Catch:{ Exception -> 0x0419 }
            L_0x0043:
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0419 }
                r0.<init>()     // Catch:{ Exception -> 0x0419 }
                java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x0419 }
                r2.<init>()     // Catch:{ Exception -> 0x0419 }
                android.util.LongSparseArray r9 = new android.util.LongSparseArray     // Catch:{ Exception -> 0x0419 }
                r9.<init>()     // Catch:{ Exception -> 0x0419 }
                org.telegram.ui.Components.ShareAlert r10 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x0419 }
                int r10 = r10.currentAccount     // Catch:{ Exception -> 0x0419 }
                org.telegram.messenger.MessagesStorage r10 = org.telegram.messenger.MessagesStorage.getInstance(r10)     // Catch:{ Exception -> 0x0419 }
                org.telegram.SQLite.SQLiteDatabase r10 = r10.getDatabase()     // Catch:{ Exception -> 0x0419 }
                java.lang.String r11 = "SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400"
                java.lang.Object[] r12 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0419 }
                org.telegram.SQLite.SQLiteCursor r10 = r10.queryFinalized(r11, r12)     // Catch:{ Exception -> 0x0419 }
            L_0x0068:
                boolean r11 = r10.next()     // Catch:{ Exception -> 0x0419 }
                if (r11 == 0) goto L_0x00b0
                long r11 = r10.longValue(r6)     // Catch:{ Exception -> 0x0419 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r13 = new org.telegram.ui.Components.ShareAlert$DialogSearchResult     // Catch:{ Exception -> 0x0419 }
                r13.<init>()     // Catch:{ Exception -> 0x0419 }
                int r14 = r10.intValue(r4)     // Catch:{ Exception -> 0x0419 }
                r13.date = r14     // Catch:{ Exception -> 0x0419 }
                r9.put(r11, r13)     // Catch:{ Exception -> 0x0419 }
                int r13 = (int) r11     // Catch:{ Exception -> 0x0419 }
                r14 = 32
                long r11 = r11 >> r14
                int r12 = (int) r11     // Catch:{ Exception -> 0x0419 }
                if (r13 == 0) goto L_0x0068
                if (r12 == r4) goto L_0x0068
                if (r13 <= 0) goto L_0x009d
                java.lang.Integer r11 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x0419 }
                boolean r11 = r0.contains(r11)     // Catch:{ Exception -> 0x0419 }
                if (r11 != 0) goto L_0x0068
                java.lang.Integer r11 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x0419 }
                r0.add(r11)     // Catch:{ Exception -> 0x0419 }
                goto L_0x0068
            L_0x009d:
                int r11 = -r13
                java.lang.Integer r12 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x0419 }
                boolean r12 = r2.contains(r12)     // Catch:{ Exception -> 0x0419 }
                if (r12 != 0) goto L_0x0068
                java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x0419 }
                r2.add(r11)     // Catch:{ Exception -> 0x0419 }
                goto L_0x0068
            L_0x00b0:
                r10.dispose()     // Catch:{ Exception -> 0x0419 }
                boolean r10 = r0.isEmpty()     // Catch:{ Exception -> 0x0419 }
                java.lang.String r11 = ";;;"
                java.lang.String r12 = ","
                java.lang.String r13 = "@"
                java.lang.String r15 = " "
                if (r10 != 0) goto L_0x01ed
                org.telegram.ui.Components.ShareAlert r10 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x0419 }
                int r10 = r10.currentAccount     // Catch:{ Exception -> 0x0419 }
                org.telegram.messenger.MessagesStorage r10 = org.telegram.messenger.MessagesStorage.getInstance(r10)     // Catch:{ Exception -> 0x0419 }
                org.telegram.SQLite.SQLiteDatabase r10 = r10.getDatabase()     // Catch:{ Exception -> 0x0419 }
                java.util.Locale r5 = java.util.Locale.US     // Catch:{ Exception -> 0x0419 }
                java.lang.String r3 = "SELECT data, status, name FROM users WHERE uid IN(%s)"
                java.lang.Object[] r14 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x0419 }
                java.lang.String r0 = android.text.TextUtils.join(r12, r0)     // Catch:{ Exception -> 0x0419 }
                r14[r6] = r0     // Catch:{ Exception -> 0x0419 }
                java.lang.String r0 = java.lang.String.format(r5, r3, r14)     // Catch:{ Exception -> 0x0419 }
                java.lang.Object[] r3 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0419 }
                org.telegram.SQLite.SQLiteCursor r0 = r10.queryFinalized(r0, r3)     // Catch:{ Exception -> 0x0419 }
                r3 = 0
            L_0x00e6:
                boolean r5 = r0.next()     // Catch:{ Exception -> 0x0419 }
                if (r5 == 0) goto L_0x01e5
                r5 = 2
                java.lang.String r10 = r0.stringValue(r5)     // Catch:{ Exception -> 0x0419 }
                org.telegram.messenger.LocaleController r5 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0419 }
                java.lang.String r5 = r5.getTranslitString(r10)     // Catch:{ Exception -> 0x0419 }
                boolean r14 = r10.equals(r5)     // Catch:{ Exception -> 0x0419 }
                if (r14 == 0) goto L_0x0100
                r5 = 0
            L_0x0100:
                int r14 = r10.lastIndexOf(r11)     // Catch:{ Exception -> 0x0419 }
                r4 = -1
                if (r14 == r4) goto L_0x010e
                int r14 = r14 + 3
                java.lang.String r4 = r10.substring(r14)     // Catch:{ Exception -> 0x0419 }
                goto L_0x010f
            L_0x010e:
                r4 = 0
            L_0x010f:
                r14 = 0
            L_0x0110:
                if (r6 >= r7) goto L_0x01d9
                r17 = r14
                r14 = r8[r6]     // Catch:{ Exception -> 0x0419 }
                boolean r18 = r10.startsWith(r14)     // Catch:{ Exception -> 0x0419 }
                if (r18 != 0) goto L_0x015e
                r18 = r11
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0419 }
                r11.<init>()     // Catch:{ Exception -> 0x0419 }
                r11.append(r15)     // Catch:{ Exception -> 0x0419 }
                r11.append(r14)     // Catch:{ Exception -> 0x0419 }
                java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0419 }
                boolean r11 = r10.contains(r11)     // Catch:{ Exception -> 0x0419 }
                if (r11 != 0) goto L_0x0160
                if (r5 == 0) goto L_0x0151
                boolean r11 = r5.startsWith(r14)     // Catch:{ Exception -> 0x0419 }
                if (r11 != 0) goto L_0x0160
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0419 }
                r11.<init>()     // Catch:{ Exception -> 0x0419 }
                r11.append(r15)     // Catch:{ Exception -> 0x0419 }
                r11.append(r14)     // Catch:{ Exception -> 0x0419 }
                java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0419 }
                boolean r11 = r5.contains(r11)     // Catch:{ Exception -> 0x0419 }
                if (r11 == 0) goto L_0x0151
                goto L_0x0160
            L_0x0151:
                if (r4 == 0) goto L_0x015b
                boolean r11 = r4.startsWith(r14)     // Catch:{ Exception -> 0x0419 }
                if (r11 == 0) goto L_0x015b
                r11 = 2
                goto L_0x0161
            L_0x015b:
                r11 = r17
                goto L_0x0161
            L_0x015e:
                r18 = r11
            L_0x0160:
                r11 = 1
            L_0x0161:
                if (r11 == 0) goto L_0x01ce
                r4 = 0
                org.telegram.tgnet.NativeByteBuffer r5 = r0.byteBufferValue(r4)     // Catch:{ Exception -> 0x0419 }
                if (r5 == 0) goto L_0x01cb
                int r6 = r5.readInt32(r4)     // Catch:{ Exception -> 0x0419 }
                org.telegram.tgnet.TLRPC$User r6 = org.telegram.tgnet.TLRPC$User.TLdeserialize(r5, r6, r4)     // Catch:{ Exception -> 0x0419 }
                r5.reuse()     // Catch:{ Exception -> 0x0419 }
                int r4 = r6.id     // Catch:{ Exception -> 0x0419 }
                long r4 = (long) r4     // Catch:{ Exception -> 0x0419 }
                java.lang.Object r4 = r9.get(r4)     // Catch:{ Exception -> 0x0419 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r4 = (org.telegram.ui.Components.ShareAlert.DialogSearchResult) r4     // Catch:{ Exception -> 0x0419 }
                org.telegram.tgnet.TLRPC$UserStatus r5 = r6.status     // Catch:{ Exception -> 0x0419 }
                r19 = r9
                r10 = 1
                if (r5 == 0) goto L_0x018b
                int r9 = r0.intValue(r10)     // Catch:{ Exception -> 0x0419 }
                r5.expires = r9     // Catch:{ Exception -> 0x0419 }
            L_0x018b:
                if (r11 != r10) goto L_0x0198
                java.lang.String r5 = r6.first_name     // Catch:{ Exception -> 0x0419 }
                java.lang.String r9 = r6.last_name     // Catch:{ Exception -> 0x0419 }
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r9, r14)     // Catch:{ Exception -> 0x0419 }
                r4.name = r5     // Catch:{ Exception -> 0x0419 }
                goto L_0x01bf
            L_0x0198:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0419 }
                r5.<init>()     // Catch:{ Exception -> 0x0419 }
                r5.append(r13)     // Catch:{ Exception -> 0x0419 }
                java.lang.String r9 = r6.username     // Catch:{ Exception -> 0x0419 }
                r5.append(r9)     // Catch:{ Exception -> 0x0419 }
                java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0419 }
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0419 }
                r9.<init>()     // Catch:{ Exception -> 0x0419 }
                r9.append(r13)     // Catch:{ Exception -> 0x0419 }
                r9.append(r14)     // Catch:{ Exception -> 0x0419 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0419 }
                r10 = 0
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r10, r9)     // Catch:{ Exception -> 0x0419 }
                r4.name = r5     // Catch:{ Exception -> 0x0419 }
            L_0x01bf:
                r4.object = r6     // Catch:{ Exception -> 0x0419 }
                org.telegram.tgnet.TLRPC$Dialog r4 = r4.dialog     // Catch:{ Exception -> 0x0419 }
                int r5 = r6.id     // Catch:{ Exception -> 0x0419 }
                long r5 = (long) r5     // Catch:{ Exception -> 0x0419 }
                r4.id = r5     // Catch:{ Exception -> 0x0419 }
                int r3 = r3 + 1
                goto L_0x01dd
            L_0x01cb:
                r19 = r9
                goto L_0x01dd
            L_0x01ce:
                r17 = r4
                r19 = r9
                int r6 = r6 + 1
                r14 = r11
                r11 = r18
                goto L_0x0110
            L_0x01d9:
                r19 = r9
                r18 = r11
            L_0x01dd:
                r11 = r18
                r9 = r19
                r4 = 1
                r6 = 0
                goto L_0x00e6
            L_0x01e5:
                r19 = r9
                r18 = r11
                r0.dispose()     // Catch:{ Exception -> 0x0419 }
                goto L_0x01f2
            L_0x01ed:
                r19 = r9
                r18 = r11
                r3 = 0
            L_0x01f2:
                boolean r0 = r2.isEmpty()     // Catch:{ Exception -> 0x0419 }
                if (r0 != 0) goto L_0x02d7
                org.telegram.ui.Components.ShareAlert r0 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x0419 }
                int r0 = r0.currentAccount     // Catch:{ Exception -> 0x0419 }
                org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x0419 }
                org.telegram.SQLite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x0419 }
                java.util.Locale r4 = java.util.Locale.US     // Catch:{ Exception -> 0x0419 }
                java.lang.String r5 = "SELECT data, name FROM chats WHERE uid IN(%s)"
                r6 = 1
                java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0419 }
                java.lang.String r2 = android.text.TextUtils.join(r12, r2)     // Catch:{ Exception -> 0x0419 }
                r6 = 0
                r9[r6] = r2     // Catch:{ Exception -> 0x0419 }
                java.lang.String r2 = java.lang.String.format(r4, r5, r9)     // Catch:{ Exception -> 0x0419 }
                java.lang.Object[] r4 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0419 }
                org.telegram.SQLite.SQLiteCursor r0 = r0.queryFinalized(r2, r4)     // Catch:{ Exception -> 0x0419 }
            L_0x021e:
                boolean r2 = r0.next()     // Catch:{ Exception -> 0x0419 }
                if (r2 == 0) goto L_0x02d1
                r2 = 1
                java.lang.String r4 = r0.stringValue(r2)     // Catch:{ Exception -> 0x0419 }
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0419 }
                java.lang.String r2 = r2.getTranslitString(r4)     // Catch:{ Exception -> 0x0419 }
                boolean r5 = r4.equals(r2)     // Catch:{ Exception -> 0x0419 }
                if (r5 == 0) goto L_0x0238
                r2 = 0
            L_0x0238:
                r5 = 0
            L_0x0239:
                if (r5 >= r7) goto L_0x02cb
                r6 = r8[r5]     // Catch:{ Exception -> 0x0419 }
                boolean r9 = r4.startsWith(r6)     // Catch:{ Exception -> 0x0419 }
                if (r9 != 0) goto L_0x0279
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0419 }
                r9.<init>()     // Catch:{ Exception -> 0x0419 }
                r9.append(r15)     // Catch:{ Exception -> 0x0419 }
                r9.append(r6)     // Catch:{ Exception -> 0x0419 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0419 }
                boolean r9 = r4.contains(r9)     // Catch:{ Exception -> 0x0419 }
                if (r9 != 0) goto L_0x0279
                if (r2 == 0) goto L_0x0276
                boolean r9 = r2.startsWith(r6)     // Catch:{ Exception -> 0x0419 }
                if (r9 != 0) goto L_0x0279
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0419 }
                r9.<init>()     // Catch:{ Exception -> 0x0419 }
                r9.append(r15)     // Catch:{ Exception -> 0x0419 }
                r9.append(r6)     // Catch:{ Exception -> 0x0419 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0419 }
                boolean r9 = r2.contains(r9)     // Catch:{ Exception -> 0x0419 }
                if (r9 == 0) goto L_0x0276
                goto L_0x0279
            L_0x0276:
                int r5 = r5 + 1
                goto L_0x0239
            L_0x0279:
                r2 = 0
                org.telegram.tgnet.NativeByteBuffer r4 = r0.byteBufferValue(r2)     // Catch:{ Exception -> 0x0419 }
                if (r4 == 0) goto L_0x02cb
                int r5 = r4.readInt32(r2)     // Catch:{ Exception -> 0x0419 }
                org.telegram.tgnet.TLRPC$Chat r5 = org.telegram.tgnet.TLRPC$Chat.TLdeserialize(r4, r5, r2)     // Catch:{ Exception -> 0x0419 }
                r4.reuse()     // Catch:{ Exception -> 0x0419 }
                if (r5 == 0) goto L_0x02cb
                boolean r2 = org.telegram.messenger.ChatObject.isNotInChat(r5)     // Catch:{ Exception -> 0x0419 }
                if (r2 != 0) goto L_0x02cb
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r5)     // Catch:{ Exception -> 0x0419 }
                if (r2 == 0) goto L_0x02a9
                boolean r2 = r5.creator     // Catch:{ Exception -> 0x0419 }
                if (r2 != 0) goto L_0x02a9
                org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r5.admin_rights     // Catch:{ Exception -> 0x0419 }
                if (r2 == 0) goto L_0x02a5
                boolean r2 = r2.post_messages     // Catch:{ Exception -> 0x0419 }
                if (r2 != 0) goto L_0x02a9
            L_0x02a5:
                boolean r2 = r5.megagroup     // Catch:{ Exception -> 0x0419 }
                if (r2 == 0) goto L_0x02cb
            L_0x02a9:
                int r2 = r5.id     // Catch:{ Exception -> 0x0419 }
                long r9 = (long) r2     // Catch:{ Exception -> 0x0419 }
                long r9 = -r9
                r2 = r19
                java.lang.Object r4 = r2.get(r9)     // Catch:{ Exception -> 0x0419 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r4 = (org.telegram.ui.Components.ShareAlert.DialogSearchResult) r4     // Catch:{ Exception -> 0x0419 }
                java.lang.String r9 = r5.title     // Catch:{ Exception -> 0x0419 }
                r10 = 0
                java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.generateSearchName(r9, r10, r6)     // Catch:{ Exception -> 0x0419 }
                r4.name = r6     // Catch:{ Exception -> 0x0419 }
                r4.object = r5     // Catch:{ Exception -> 0x0419 }
                org.telegram.tgnet.TLRPC$Dialog r4 = r4.dialog     // Catch:{ Exception -> 0x0419 }
                int r5 = r5.id     // Catch:{ Exception -> 0x0419 }
                int r5 = -r5
                long r5 = (long) r5     // Catch:{ Exception -> 0x0419 }
                r4.id = r5     // Catch:{ Exception -> 0x0419 }
                int r3 = r3 + 1
                goto L_0x02cd
            L_0x02cb:
                r2 = r19
            L_0x02cd:
                r19 = r2
                goto L_0x021e
            L_0x02d1:
                r2 = r19
                r0.dispose()     // Catch:{ Exception -> 0x0419 }
                goto L_0x02d9
            L_0x02d7:
                r2 = r19
            L_0x02d9:
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0419 }
                r0.<init>(r3)     // Catch:{ Exception -> 0x0419 }
                r3 = 0
            L_0x02df:
                int r4 = r2.size()     // Catch:{ Exception -> 0x0419 }
                if (r3 >= r4) goto L_0x02f9
                java.lang.Object r4 = r2.valueAt(r3)     // Catch:{ Exception -> 0x0419 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r4 = (org.telegram.ui.Components.ShareAlert.DialogSearchResult) r4     // Catch:{ Exception -> 0x0419 }
                org.telegram.tgnet.TLObject r5 = r4.object     // Catch:{ Exception -> 0x0419 }
                if (r5 == 0) goto L_0x02f6
                java.lang.CharSequence r5 = r4.name     // Catch:{ Exception -> 0x0419 }
                if (r5 == 0) goto L_0x02f6
                r0.add(r4)     // Catch:{ Exception -> 0x0419 }
            L_0x02f6:
                int r3 = r3 + 1
                goto L_0x02df
            L_0x02f9:
                org.telegram.ui.Components.ShareAlert r3 = org.telegram.ui.Components.ShareAlert.this     // Catch:{ Exception -> 0x0419 }
                int r3 = r3.currentAccount     // Catch:{ Exception -> 0x0419 }
                org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r3)     // Catch:{ Exception -> 0x0419 }
                org.telegram.SQLite.SQLiteDatabase r3 = r3.getDatabase()     // Catch:{ Exception -> 0x0419 }
                java.lang.String r4 = "SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid"
                r5 = 0
                java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0419 }
                org.telegram.SQLite.SQLiteCursor r3 = r3.queryFinalized(r4, r6)     // Catch:{ Exception -> 0x0419 }
            L_0x0310:
                boolean r4 = r3.next()     // Catch:{ Exception -> 0x0419 }
                if (r4 == 0) goto L_0x040b
                r4 = 3
                int r4 = r3.intValue(r4)     // Catch:{ Exception -> 0x0419 }
                long r4 = (long) r4     // Catch:{ Exception -> 0x0419 }
                int r4 = r2.indexOfKey(r4)     // Catch:{ Exception -> 0x0419 }
                if (r4 < 0) goto L_0x0323
                goto L_0x0310
            L_0x0323:
                r5 = 2
                java.lang.String r4 = r3.stringValue(r5)     // Catch:{ Exception -> 0x0419 }
                org.telegram.messenger.LocaleController r6 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0419 }
                java.lang.String r10 = r6.getTranslitString(r4)     // Catch:{ Exception -> 0x0419 }
                boolean r6 = r4.equals(r10)     // Catch:{ Exception -> 0x0419 }
                if (r6 == 0) goto L_0x0337
                r10 = 0
            L_0x0337:
                r6 = r18
                int r9 = r4.lastIndexOf(r6)     // Catch:{ Exception -> 0x0419 }
                r11 = -1
                if (r9 == r11) goto L_0x0347
                int r9 = r9 + 3
                java.lang.String r9 = r4.substring(r9)     // Catch:{ Exception -> 0x0419 }
                goto L_0x0348
            L_0x0347:
                r9 = 0
            L_0x0348:
                r12 = 0
                r14 = 0
            L_0x034a:
                if (r12 >= r7) goto L_0x0405
                r5 = r8[r12]     // Catch:{ Exception -> 0x0419 }
                boolean r16 = r4.startsWith(r5)     // Catch:{ Exception -> 0x0419 }
                if (r16 != 0) goto L_0x0391
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0419 }
                r11.<init>()     // Catch:{ Exception -> 0x0419 }
                r11.append(r15)     // Catch:{ Exception -> 0x0419 }
                r11.append(r5)     // Catch:{ Exception -> 0x0419 }
                java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0419 }
                boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x0419 }
                if (r11 != 0) goto L_0x0391
                if (r10 == 0) goto L_0x0387
                boolean r11 = r10.startsWith(r5)     // Catch:{ Exception -> 0x0419 }
                if (r11 != 0) goto L_0x0391
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0419 }
                r11.<init>()     // Catch:{ Exception -> 0x0419 }
                r11.append(r15)     // Catch:{ Exception -> 0x0419 }
                r11.append(r5)     // Catch:{ Exception -> 0x0419 }
                java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0419 }
                boolean r11 = r10.contains(r11)     // Catch:{ Exception -> 0x0419 }
                if (r11 == 0) goto L_0x0387
                goto L_0x0391
            L_0x0387:
                if (r9 == 0) goto L_0x0392
                boolean r11 = r9.startsWith(r5)     // Catch:{ Exception -> 0x0419 }
                if (r11 == 0) goto L_0x0392
                r14 = 2
                goto L_0x0392
            L_0x0391:
                r14 = 1
            L_0x0392:
                if (r14 == 0) goto L_0x03fd
                r11 = 0
                org.telegram.tgnet.NativeByteBuffer r4 = r3.byteBufferValue(r11)     // Catch:{ Exception -> 0x0419 }
                if (r4 == 0) goto L_0x03fa
                int r9 = r4.readInt32(r11)     // Catch:{ Exception -> 0x0419 }
                org.telegram.tgnet.TLRPC$User r9 = org.telegram.tgnet.TLRPC$User.TLdeserialize(r4, r9, r11)     // Catch:{ Exception -> 0x0419 }
                r4.reuse()     // Catch:{ Exception -> 0x0419 }
                org.telegram.ui.Components.ShareAlert$DialogSearchResult r4 = new org.telegram.ui.Components.ShareAlert$DialogSearchResult     // Catch:{ Exception -> 0x0419 }
                r4.<init>()     // Catch:{ Exception -> 0x0419 }
                org.telegram.tgnet.TLRPC$UserStatus r10 = r9.status     // Catch:{ Exception -> 0x0419 }
                if (r10 == 0) goto L_0x03b6
                r12 = 1
                int r11 = r3.intValue(r12)     // Catch:{ Exception -> 0x0419 }
                r10.expires = r11     // Catch:{ Exception -> 0x0419 }
            L_0x03b6:
                org.telegram.tgnet.TLRPC$Dialog r10 = r4.dialog     // Catch:{ Exception -> 0x0419 }
                int r11 = r9.id     // Catch:{ Exception -> 0x0419 }
                long r11 = (long) r11     // Catch:{ Exception -> 0x0419 }
                r10.id = r11     // Catch:{ Exception -> 0x0419 }
                r4.object = r9     // Catch:{ Exception -> 0x0419 }
                r11 = 1
                if (r14 != r11) goto L_0x03ce
                java.lang.String r10 = r9.first_name     // Catch:{ Exception -> 0x0419 }
                java.lang.String r9 = r9.last_name     // Catch:{ Exception -> 0x0419 }
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r10, r9, r5)     // Catch:{ Exception -> 0x0419 }
                r4.name = r5     // Catch:{ Exception -> 0x0419 }
                r10 = 0
                goto L_0x03f5
            L_0x03ce:
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0419 }
                r10.<init>()     // Catch:{ Exception -> 0x0419 }
                r10.append(r13)     // Catch:{ Exception -> 0x0419 }
                java.lang.String r9 = r9.username     // Catch:{ Exception -> 0x0419 }
                r10.append(r9)     // Catch:{ Exception -> 0x0419 }
                java.lang.String r9 = r10.toString()     // Catch:{ Exception -> 0x0419 }
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0419 }
                r10.<init>()     // Catch:{ Exception -> 0x0419 }
                r10.append(r13)     // Catch:{ Exception -> 0x0419 }
                r10.append(r5)     // Catch:{ Exception -> 0x0419 }
                java.lang.String r5 = r10.toString()     // Catch:{ Exception -> 0x0419 }
                r10 = 0
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r9, r10, r5)     // Catch:{ Exception -> 0x0419 }
                r4.name = r5     // Catch:{ Exception -> 0x0419 }
            L_0x03f5:
                r0.add(r4)     // Catch:{ Exception -> 0x0419 }
                r5 = r10
                goto L_0x0407
            L_0x03fa:
                r11 = 1
                r5 = 0
                goto L_0x0407
            L_0x03fd:
                r5 = 0
                r11 = 1
                int r12 = r12 + 1
                r5 = 2
                r11 = -1
                goto L_0x034a
            L_0x0405:
                r5 = 0
                r11 = 1
            L_0x0407:
                r18 = r6
                goto L_0x0310
            L_0x040b:
                r3.dispose()     // Catch:{ Exception -> 0x0419 }
                org.telegram.ui.Components.-$$Lambda$ShareAlert$ShareSearchAdapter$Js7rkkKd8pGQQsV2FkNY8qyayIk r2 = org.telegram.ui.Components.$$Lambda$ShareAlert$ShareSearchAdapter$Js7rkkKd8pGQQsV2FkNY8qyayIk.INSTANCE     // Catch:{ Exception -> 0x0419 }
                java.util.Collections.sort(r0, r2)     // Catch:{ Exception -> 0x0419 }
                r2 = r22
                r1.updateSearchResults(r0, r2)     // Catch:{ Exception -> 0x0419 }
                goto L_0x041d
            L_0x0419:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x041d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.lambda$searchDialogsInternal$1$ShareAlert$ShareSearchAdapter(java.lang.String, int):void");
        }

        static /* synthetic */ int lambda$searchDialogsInternal$0(Object obj, Object obj2) {
            int i = ((DialogSearchResult) obj).date;
            int i2 = ((DialogSearchResult) obj2).date;
            if (i < i2) {
                return 1;
            }
            return i > i2 ? -1 : 0;
        }

        private void updateSearchResults(ArrayList<Object> arrayList, int i) {
            AndroidUtilities.runOnUIThread(new Runnable(i, arrayList) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ShareAlert.ShareSearchAdapter.this.lambda$updateSearchResults$2$ShareAlert$ShareSearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$updateSearchResults$2 */
        public /* synthetic */ void lambda$updateSearchResults$2$ShareAlert$ShareSearchAdapter(int i, ArrayList arrayList) {
            boolean z;
            if (i == this.lastSearchId) {
                this.lastLocalSearchId = i;
                if (this.lastGlobalSearchId != i) {
                    this.searchAdapterHelper.clear();
                }
                if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
                    ShareAlert shareAlert = ShareAlert.this;
                    int unused = shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    ShareAlert.this.gridView.setAdapter(ShareAlert.this.searchAdapter);
                    ShareAlert.this.searchAdapter.notifyDataSetChanged();
                }
                int i2 = 0;
                while (true) {
                    z = true;
                    if (i2 >= arrayList.size()) {
                        break;
                    }
                    TLObject tLObject = ((DialogSearchResult) arrayList.get(i2)).object;
                    if (tLObject instanceof TLRPC$User) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putUser((TLRPC$User) tLObject, true);
                    } else if (tLObject instanceof TLRPC$Chat) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putChat((TLRPC$Chat) tLObject, true);
                    }
                    i2++;
                }
                boolean z2 = !this.searchResult.isEmpty() && arrayList.isEmpty();
                if (!this.searchResult.isEmpty() || !arrayList.isEmpty()) {
                    z = false;
                }
                if (z2) {
                    ShareAlert shareAlert2 = ShareAlert.this;
                    int unused2 = shareAlert2.topBeforeSwitch = shareAlert2.getCurrentTop();
                }
                this.searchResult = arrayList;
                this.searchAdapterHelper.mergeResults(arrayList);
                notifyDataSetChanged();
                if (!z && !z2 && ShareAlert.this.topBeforeSwitch > 0) {
                    ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
                    int unused3 = ShareAlert.this.topBeforeSwitch = -1000;
                }
                ShareAlert.this.searchEmptyView.showTextView();
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
                if (TextUtils.isEmpty(str)) {
                    this.searchResult.clear();
                    this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
                    this.searchAdapterHelper.queryServerSearch((String) null, true, true, true, true, false, 0, false, 0, 0);
                    ShareAlert shareAlert = ShareAlert.this;
                    int unused = shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    this.lastSearchId = -1;
                    notifyDataSetChanged();
                    return;
                }
                int i = this.lastSearchId + 1;
                this.lastSearchId = i;
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                $$Lambda$ShareAlert$ShareSearchAdapter$xWORWmbZu4PZ9TNXoxvlvdahI r2 = new Runnable(str, i) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        ShareAlert.ShareSearchAdapter.this.lambda$searchDialogs$4$ShareAlert$ShareSearchAdapter(this.f$1, this.f$2);
                    }
                };
                this.searchRunnable = r2;
                dispatchQueue.postRunnable(r2, 300);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$searchDialogs$4 */
        public /* synthetic */ void lambda$searchDialogs$4$ShareAlert$ShareSearchAdapter(String str, int i) {
            this.searchRunnable = null;
            searchDialogsInternal(str, i);
            $$Lambda$ShareAlert$ShareSearchAdapter$0sSDq1XOPQynxBeTtPg7n7ovAYk r0 = new Runnable(i, str) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ShareAlert.ShareSearchAdapter.this.lambda$searchDialogs$3$ShareAlert$ShareSearchAdapter(this.f$1, this.f$2);
                }
            };
            this.searchRunnable2 = r0;
            AndroidUtilities.runOnUIThread(r0);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$searchDialogs$3 */
        public /* synthetic */ void lambda$searchDialogs$3$ShareAlert$ShareSearchAdapter(int i, String str) {
            this.searchRunnable2 = null;
            if (i == this.lastSearchId) {
                this.searchAdapterHelper.queryServerSearch(str, true, true, true, true, false, 0, false, 0, i);
            }
        }

        public int getItemCount() {
            int size = this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size();
            return size != 0 ? size + 1 : size;
        }

        public TLRPC$Dialog getItem(int i) {
            int i2 = i - 1;
            TLRPC$TL_dialog tLRPC$TL_dialog = null;
            if (i2 < 0) {
                return null;
            }
            if (i2 < this.searchResult.size()) {
                return ((DialogSearchResult) this.searchResult.get(i2)).dialog;
            }
            int size = i2 - this.searchResult.size();
            ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
            if (size < localServerSearch.size()) {
                TLObject tLObject = localServerSearch.get(size);
                tLRPC$TL_dialog = new TLRPC$TL_dialog();
                if (tLObject instanceof TLRPC$User) {
                    tLRPC$TL_dialog.id = (long) ((TLRPC$User) tLObject).id;
                } else {
                    tLRPC$TL_dialog.id = (long) (-((TLRPC$Chat) tLObject).id);
                }
            }
            return tLRPC$TL_dialog;
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
                view = new ShareDialogCell(this.context, ShareAlert.this.darkTheme ? 1 : 0);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            CharSequence charSequence;
            long j;
            long j2;
            int indexOfIgnoreCase;
            if (viewHolder.getItemViewType() == 0) {
                int i2 = i - 1;
                ShareDialogCell shareDialogCell = (ShareDialogCell) viewHolder.itemView;
                if (i2 < this.searchResult.size()) {
                    DialogSearchResult dialogSearchResult = (DialogSearchResult) this.searchResult.get(i2);
                    j = dialogSearchResult.dialog.id;
                    charSequence = dialogSearchResult.name;
                } else {
                    TLObject tLObject = this.searchAdapterHelper.getLocalServerSearch().get(i2 - this.searchResult.size());
                    if (tLObject instanceof TLRPC$User) {
                        TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                        j2 = (long) tLRPC$User.id;
                        charSequence = ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name);
                    } else {
                        TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject;
                        j2 = (long) (-tLRPC$Chat.id);
                        charSequence = tLRPC$Chat.title;
                    }
                    String lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                    if (!(TextUtils.isEmpty(lastFoundUsername) || charSequence == null || (indexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(charSequence.toString(), lastFoundUsername)) == -1)) {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
                        spannableStringBuilder.setSpan(new ForegroundColorSpanThemable("windowBackgroundWhiteBlueText4"), indexOfIgnoreCase, lastFoundUsername.length() + indexOfIgnoreCase, 33);
                        charSequence = spannableStringBuilder;
                    }
                    j = j2;
                }
                shareDialogCell.setDialog((int) j, ShareAlert.this.selectedDialogs.indexOfKey(j) >= 0, charSequence);
            }
        }
    }
}

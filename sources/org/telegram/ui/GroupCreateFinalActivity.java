package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;

public class GroupCreateFinalActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate {
    private static final int done_button = 1;
    private GroupCreateAdapter adapter;
    private TLRPC.FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    /* access modifiers changed from: private */
    public RLottieImageView avatarEditor;
    /* access modifiers changed from: private */
    public BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public View avatarOverlay;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    private RLottieDrawable cameraDrawable;
    private int chatType;
    private boolean createAfterUpload;
    /* access modifiers changed from: private */
    public String currentGroupCreateAddress;
    private Location currentGroupCreateLocation;
    private GroupCreateFinalActivityDelegate delegate;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    private boolean donePressed;
    /* access modifiers changed from: private */
    public EditTextEmoji editText;
    /* access modifiers changed from: private */
    public FrameLayout editTextContainer;
    private FrameLayout floatingButtonContainer;
    /* access modifiers changed from: private */
    public ImageView floatingButtonIcon;
    private boolean forImport;
    private ImageUpdater imageUpdater;
    private TLRPC.InputFile inputPhoto;
    private TLRPC.InputFile inputVideo;
    private String inputVideoPath;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private String nameToSet;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    private int reqId;
    /* access modifiers changed from: private */
    public ArrayList<Long> selectedContacts;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private double videoTimestamp;

    public interface GroupCreateFinalActivityDelegate {
        void didFailChatCreation();

        void didFinishChatCreation(GroupCreateFinalActivity groupCreateFinalActivity, long j);

        void didStartChatCreation();
    }

    public GroupCreateFinalActivity(Bundle args) {
        super(args);
        this.chatType = args.getInt("chatType", 0);
        this.currentGroupCreateAddress = args.getString("address");
        this.currentGroupCreateLocation = (Location) args.getParcelable("location");
        this.forImport = args.getBoolean("forImport", false);
        this.nameToSet = args.getString("title", (String) null);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidFailCreate);
        ImageUpdater imageUpdater2 = new ImageUpdater(true);
        this.imageUpdater = imageUpdater2;
        imageUpdater2.parentFragment = this;
        this.imageUpdater.setDelegate(this);
        long[] contacts = getArguments().getLongArray("result");
        if (contacts != null) {
            this.selectedContacts = new ArrayList<>(contacts.length);
            for (long valueOf : contacts) {
                this.selectedContacts.add(Long.valueOf(valueOf));
            }
        }
        ArrayList<Long> usersToLoad = new ArrayList<>();
        for (int a = 0; a < this.selectedContacts.size(); a++) {
            Long uid = this.selectedContacts.get(a);
            if (getMessagesController().getUser(uid) == null) {
                usersToLoad.add(uid);
            }
        }
        if (usersToLoad.isEmpty() == 0) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            ArrayList<TLRPC.User> users = new ArrayList<>();
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new GroupCreateFinalActivity$$ExternalSyntheticLambda5(this, users, usersToLoad, countDownLatch));
            try {
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (usersToLoad.size() != users.size() || users.isEmpty()) {
                return false;
            }
            Iterator<TLRPC.User> it = users.iterator();
            while (it.hasNext()) {
                getMessagesController().putUser(it.next(), true);
            }
        }
        return super.onFragmentCreate();
    }

    /* renamed from: lambda$onFragmentCreate$0$org-telegram-ui-GroupCreateFinalActivity  reason: not valid java name */
    public /* synthetic */ void m3032xd3eevar_(ArrayList users, ArrayList usersToLoad, CountDownLatch countDownLatch) {
        users.addAll(MessagesStorage.getInstance(this.currentAccount).getUsers(usersToLoad));
        countDownLatch.countDown();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidFailCreate);
        this.imageUpdater.clear();
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
        }
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        GroupCreateAdapter groupCreateAdapter = this.adapter;
        if (groupCreateAdapter != null) {
            groupCreateAdapter.notifyDataSetChanged();
        }
        this.imageUpdater.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
        this.imageUpdater.onPause();
    }

    public void dismissCurrentDialog() {
        if (!this.imageUpdater.dismissCurrentDialog(this.visibleDialog)) {
            super.dismissCurrentDialog();
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return this.imageUpdater.dismissDialogOnPause(dialog) && super.dismissDialogOnPause(dialog);
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        this.imageUpdater.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return true;
        }
        this.editText.hidePopup(true);
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean hideKeyboardOnShow() {
        return false;
    }

    public View createView(Context context) {
        String str;
        int i;
        Context context2 = context;
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NewGroup", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    GroupCreateFinalActivity.this.finishFragment();
                }
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                int heightSize2 = heightSize - getPaddingTop();
                measureChildWithMargins(GroupCreateFinalActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                if (measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    GroupCreateFinalActivity.this.editText.hideEmojiView();
                    this.ignoreLayout = false;
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == GroupCreateFinalActivity.this.actionBar)) {
                        if (GroupCreateFinalActivity.this.editText == null || !GroupCreateFinalActivity.this.editText.isPopupView(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec((heightSize2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                int count = getChildCount();
                int keyboardSize = measureKeyboardHeight();
                int paddingBottom = (keyboardSize > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : GroupCreateFinalActivity.this.editText.getEmojiPadding();
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
                                childLeft = (r - width) - lp.rightMargin;
                                break;
                            default:
                                childLeft = lp.leftMargin;
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
                        if (GroupCreateFinalActivity.this.editText != null && GroupCreateFinalActivity.this.editText.isPopupView(child)) {
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
        this.fragmentView = sizeNotifierFrameLayout;
        this.fragmentView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.fragmentView.setOnTouchListener(GroupCreateFinalActivity$$ExternalSyntheticLambda3.INSTANCE);
        this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        LinearLayout linearLayout = new LinearLayout(context2) {
            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == GroupCreateFinalActivity.this.listView && GroupCreateFinalActivity.this.shadowDrawable != null) {
                    int y = GroupCreateFinalActivity.this.editTextContainer.getMeasuredHeight();
                    GroupCreateFinalActivity.this.shadowDrawable.setBounds(0, y, getMeasuredWidth(), GroupCreateFinalActivity.this.shadowDrawable.getIntrinsicHeight() + y);
                    GroupCreateFinalActivity.this.shadowDrawable.draw(canvas);
                }
                return result;
            }
        };
        linearLayout.setOrientation(1);
        sizeNotifierFrameLayout.addView(linearLayout, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.editTextContainer = frameLayout;
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        AnonymousClass4 r7 = new BackupImageView(context2) {
            public void invalidate() {
                if (GroupCreateFinalActivity.this.avatarOverlay != null) {
                    GroupCreateFinalActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate();
            }

            public void invalidate(int l, int t, int r, int b) {
                if (GroupCreateFinalActivity.this.avatarOverlay != null) {
                    GroupCreateFinalActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate(l, t, r, b);
            }
        };
        this.avatarImage = r7;
        r7.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable.setInfo(5, (String) null, (String) null);
        this.avatarImage.setImageDrawable(this.avatarDrawable);
        this.avatarImage.setContentDescription(LocaleController.getString("ChoosePhoto", NUM));
        int i2 = 3;
        this.editTextContainer.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        final Paint paint = new Paint(1);
        paint.setColor(NUM);
        AnonymousClass5 r8 = new View(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (GroupCreateFinalActivity.this.avatarImage != null && GroupCreateFinalActivity.this.avatarProgressView.getVisibility() == 0) {
                    paint.setAlpha((int) (GroupCreateFinalActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f * GroupCreateFinalActivity.this.avatarProgressView.getAlpha()));
                    canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, ((float) getMeasuredWidth()) / 2.0f, paint);
                }
            }
        };
        this.avatarOverlay = r8;
        this.editTextContainer.addView(r8, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        this.avatarOverlay.setOnClickListener(new GroupCreateFinalActivity$$ExternalSyntheticLambda1(this));
        this.cameraDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(60.0f), AndroidUtilities.dp(60.0f), false, (int[]) null);
        AnonymousClass6 r82 = new RLottieImageView(context2) {
            public void invalidate(int l, int t, int r, int b) {
                super.invalidate(l, t, r, b);
                GroupCreateFinalActivity.this.avatarOverlay.invalidate();
            }

            public void invalidate() {
                super.invalidate();
                GroupCreateFinalActivity.this.avatarOverlay.invalidate();
            }
        };
        this.avatarEditor = r82;
        r82.setScaleType(ImageView.ScaleType.CENTER);
        this.avatarEditor.setAnimation(this.cameraDrawable);
        this.avatarEditor.setEnabled(false);
        this.avatarEditor.setClickable(false);
        this.avatarEditor.setPadding(AndroidUtilities.dp(2.0f), 0, 0, AndroidUtilities.dp(1.0f));
        this.editTextContainer.addView(this.avatarEditor, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        AnonymousClass7 r83 = new RadialProgressView(context2) {
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                GroupCreateFinalActivity.this.avatarOverlay.invalidate();
            }
        };
        this.avatarProgressView = r83;
        r83.setSize(AndroidUtilities.dp(30.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.avatarProgressView.setNoProgress(false);
        this.editTextContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        showAvatarProgress(false, false);
        EditTextEmoji editTextEmoji2 = new EditTextEmoji(context2, sizeNotifierFrameLayout, this, 0);
        this.editText = editTextEmoji2;
        int i3 = this.chatType;
        if (i3 == 0 || i3 == 4) {
            i = NUM;
            str = "EnterGroupNamePlaceholder";
        } else {
            i = NUM;
            str = "EnterListName";
        }
        editTextEmoji2.setHint(LocaleController.getString(str, i));
        String str2 = this.nameToSet;
        if (str2 != null) {
            this.editText.setText(str2);
            this.nameToSet = null;
        }
        this.editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        this.editTextContainer.addView(this.editText, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        GroupCreateAdapter groupCreateAdapter = new GroupCreateAdapter(context2);
        this.adapter = groupCreateAdapter;
        recyclerListView.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        GroupCreateDividerItemDecoration decoration = new GroupCreateDividerItemDecoration();
        decoration.setSkipRows(this.currentGroupCreateAddress != null ? 5 : 2);
        this.listView.addItemDecoration(decoration);
        linearLayout.addView(this.listView, LayoutHelper.createLinear(-1, -1));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(GroupCreateFinalActivity.this.editText);
                }
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new GroupCreateFinalActivity$$ExternalSyntheticLambda8(this));
        this.floatingButtonContainer = new FrameLayout(context2);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable2 = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable2, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButtonContainer.setBackgroundDrawable(drawable);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            Paint paint2 = paint;
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButtonContainer.setStateListAnimator(animator);
            this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        VerticalPositionAutoAnimator.attach(this.floatingButtonContainer);
        FrameLayout frameLayout2 = this.floatingButtonContainer;
        int i4 = 60;
        int i5 = Build.VERSION.SDK_INT >= 21 ? 56 : 60;
        float f = Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f;
        if (!LocaleController.isRTL) {
            i2 = 5;
        }
        sizeNotifierFrameLayout.addView(frameLayout2, LayoutHelper.createFrame(i5, f, i2 | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        this.floatingButtonContainer.setOnClickListener(new GroupCreateFinalActivity$$ExternalSyntheticLambda2(this));
        ImageView imageView = new ImageView(context2);
        this.floatingButtonIcon = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.floatingButtonIcon.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.floatingButtonIcon.setImageResource(NUM);
        this.floatingButtonIcon.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        this.floatingButtonContainer.setContentDescription(LocaleController.getString("Done", NUM));
        FrameLayout frameLayout3 = this.floatingButtonContainer;
        ImageView imageView2 = this.floatingButtonIcon;
        if (Build.VERSION.SDK_INT >= 21) {
            i4 = 56;
        }
        frameLayout3.addView(imageView2, LayoutHelper.createFrame(i4, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f));
        ContextProgressView contextProgressView = new ContextProgressView(context2, 1);
        this.progressView = contextProgressView;
        contextProgressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.floatingButtonContainer.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$1(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-GroupCreateFinalActivity  reason: not valid java name */
    public /* synthetic */ void m3026lambda$createView$4$orgtelegramuiGroupCreateFinalActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new GroupCreateFinalActivity$$ExternalSyntheticLambda4(this), new GroupCreateFinalActivity$$ExternalSyntheticLambda0(this));
        this.cameraDrawable.setCurrentFrame(0);
        this.cameraDrawable.setCustomEndFrame(43);
        this.avatarEditor.playAnimation();
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-GroupCreateFinalActivity  reason: not valid java name */
    public /* synthetic */ void m3024lambda$createView$2$orgtelegramuiGroupCreateFinalActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.inputPhoto = null;
        this.inputVideo = null;
        this.inputVideoPath = null;
        this.videoTimestamp = 0.0d;
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) null);
        this.avatarEditor.setAnimation(this.cameraDrawable);
        this.cameraDrawable.setCurrentFrame(0);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-GroupCreateFinalActivity  reason: not valid java name */
    public /* synthetic */ void m3025lambda$createView$3$orgtelegramuiGroupCreateFinalActivity(DialogInterface dialog) {
        if (!this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCustomEndFrame(86);
            this.avatarEditor.playAnimation();
            return;
        }
        this.cameraDrawable.setCurrentFrame(0, false);
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-GroupCreateFinalActivity  reason: not valid java name */
    public /* synthetic */ void m3028lambda$createView$6$orgtelegramuiGroupCreateFinalActivity(View view, int position) {
        if ((view instanceof TextSettingsCell) && AndroidUtilities.isGoogleMapsInstalled(this)) {
            LocationActivity fragment = new LocationActivity(4);
            fragment.setDialogId(0);
            fragment.setDelegate(new GroupCreateFinalActivity$$ExternalSyntheticLambda9(this));
            presentFragment(fragment);
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-GroupCreateFinalActivity  reason: not valid java name */
    public /* synthetic */ void m3027lambda$createView$5$orgtelegramuiGroupCreateFinalActivity(TLRPC.MessageMedia location, int live, boolean notify, int scheduleDate) {
        this.currentGroupCreateLocation.setLatitude(location.geo.lat);
        this.currentGroupCreateLocation.setLongitude(location.geo._long);
        this.currentGroupCreateAddress = location.address;
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-GroupCreateFinalActivity  reason: not valid java name */
    public /* synthetic */ void m3029lambda$createView$7$orgtelegramuiGroupCreateFinalActivity(View view) {
        if (!this.donePressed) {
            if (this.editText.length() == 0) {
                Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                AndroidUtilities.shakeView(this.editText, 2.0f, 0);
                return;
            }
            this.donePressed = true;
            AndroidUtilities.hideKeyboard(this.editText);
            this.editText.setEnabled(false);
            if (this.imageUpdater.isUploadingImage()) {
                this.createAfterUpload = true;
                return;
            }
            showEditDoneProgress(true);
            this.reqId = getMessagesController().createChat(this.editText.getText().toString(), this.selectedContacts, (String) null, this.chatType, this.forImport, this.currentGroupCreateLocation, this.currentGroupCreateAddress, this);
        }
    }

    public void onUploadProgressChanged(float progress) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(progress);
        }
    }

    public void didStartUpload(boolean isVideo) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(0.0f);
        }
    }

    public void didUploadPhoto(TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, String videoPath, TLRPC.PhotoSize bigSize, TLRPC.PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new GroupCreateFinalActivity$$ExternalSyntheticLambda6(this, photo, video, videoPath, videoStartTimestamp, smallSize, bigSize));
    }

    /* renamed from: lambda$didUploadPhoto$8$org-telegram-ui-GroupCreateFinalActivity  reason: not valid java name */
    public /* synthetic */ void m3030lambda$didUploadPhoto$8$orgtelegramuiGroupCreateFinalActivity(TLRPC.InputFile photo, TLRPC.InputFile video, String videoPath, double videoStartTimestamp, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
        TLRPC.InputFile inputFile = photo;
        TLRPC.InputFile inputFile2 = video;
        if (inputFile != null) {
            TLRPC.PhotoSize photoSize = smallSize;
            TLRPC.PhotoSize photoSize2 = bigSize;
        } else if (inputFile2 != null) {
            TLRPC.PhotoSize photoSize3 = smallSize;
            TLRPC.PhotoSize photoSize4 = bigSize;
        } else {
            this.avatar = smallSize.location;
            this.avatarBig = bigSize.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", (Drawable) this.avatarDrawable, (Object) null);
            showAvatarProgress(true, false);
            return;
        }
        this.inputPhoto = inputFile;
        this.inputVideo = inputFile2;
        this.inputVideoPath = videoPath;
        this.videoTimestamp = videoStartTimestamp;
        if (this.createAfterUpload) {
            GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate = this.delegate;
            if (groupCreateFinalActivityDelegate != null) {
                groupCreateFinalActivityDelegate.didStartChatCreation();
            }
            getMessagesController().createChat(this.editText.getText().toString(), this.selectedContacts, (String) null, this.chatType, this.forImport, this.currentGroupCreateLocation, this.currentGroupCreateAddress, this);
        }
        showAvatarProgress(false, true);
        this.avatarEditor.setImageDrawable((Drawable) null);
    }

    public String getInitialSearchString() {
        return this.editText.getText().toString();
    }

    public void setDelegate(GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate) {
        this.delegate = groupCreateFinalActivityDelegate;
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarEditor != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                this.avatarAnimation = new AnimatorSet();
                if (show) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f})});
                } else {
                    this.avatarEditor.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f})});
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (GroupCreateFinalActivity.this.avatarAnimation != null && GroupCreateFinalActivity.this.avatarEditor != null) {
                            if (show) {
                                GroupCreateFinalActivity.this.avatarEditor.setVisibility(4);
                            } else {
                                GroupCreateFinalActivity.this.avatarProgressView.setVisibility(4);
                            }
                            AnimatorSet unused = GroupCreateFinalActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        AnimatorSet unused = GroupCreateFinalActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (show) {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(4);
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
            } else {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(0);
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
            }
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.imageUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (!(imageUpdater2 == null || imageUpdater2.currentPicturePath == null)) {
            args.putString("path", this.imageUpdater.currentPicturePath);
        }
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            String text = editTextEmoji.getText().toString();
            if (text.length() != 0) {
                args.putString("nameTextView", text);
            }
        }
    }

    public void restoreSelfArgs(Bundle args) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.currentPicturePath = args.getString("path");
        }
        String text = args.getString("nameTextView");
        if (text != null) {
            EditTextEmoji editTextEmoji = this.editText;
            if (editTextEmoji != null) {
                editTextEmoji.setText(text);
            } else {
                this.nameToSet = text;
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.editText.openKeyboard();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int i = id;
        if (i == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                int mask = args[0].intValue();
                if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 || (MessagesController.UPDATE_MASK_NAME & mask) != 0 || (MessagesController.UPDATE_MASK_STATUS & mask) != 0) {
                    int count = this.listView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) child).update(mask);
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.chatDidFailCreate) {
            this.reqId = 0;
            this.donePressed = false;
            showEditDoneProgress(false);
            EditTextEmoji editTextEmoji = this.editText;
            if (editTextEmoji != null) {
                editTextEmoji.setEnabled(true);
            }
            GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate = this.delegate;
            if (groupCreateFinalActivityDelegate != null) {
                groupCreateFinalActivityDelegate.didFailChatCreation();
            }
        } else if (i == NotificationCenter.chatDidCreated) {
            this.reqId = 0;
            long chatId = args[0].longValue();
            GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate2 = this.delegate;
            if (groupCreateFinalActivityDelegate2 != null) {
                groupCreateFinalActivityDelegate2.didFinishChatCreation(this, chatId);
            } else {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                Bundle args2 = new Bundle();
                args2.putLong("chat_id", chatId);
                presentFragment(new ChatActivity(args2), true);
            }
            if (this.inputPhoto != null || this.inputVideo != null) {
                long j = chatId;
                getMessagesController().changeChatAvatar(chatId, (TLRPC.TL_inputChatPhoto) null, this.inputPhoto, this.inputVideo, this.videoTimestamp, this.inputVideoPath, this.avatar, this.avatarBig, (Runnable) null);
            }
        }
    }

    private void showEditDoneProgress(boolean show) {
        final boolean z = show;
        if (this.floatingButtonIcon != null) {
            AnimatorSet animatorSet = this.doneItemAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.doneItemAnimation = new AnimatorSet();
            if (z) {
                this.progressView.setVisibility(0);
                this.floatingButtonContainer.setEnabled(false);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButtonIcon, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f})});
            } else {
                this.floatingButtonIcon.setVisibility(0);
                this.floatingButtonContainer.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "alpha", new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (GroupCreateFinalActivity.this.doneItemAnimation != null && GroupCreateFinalActivity.this.doneItemAnimation.equals(animation)) {
                        if (!z) {
                            GroupCreateFinalActivity.this.progressView.setVisibility(4);
                        } else {
                            GroupCreateFinalActivity.this.floatingButtonIcon.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (GroupCreateFinalActivity.this.doneItemAnimation != null && GroupCreateFinalActivity.this.doneItemAnimation.equals(animation)) {
                        AnimatorSet unused = GroupCreateFinalActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public class GroupCreateAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int usersStartRow;

        public GroupCreateAdapter(Context ctx) {
            this.context = ctx;
        }

        public int getItemCount() {
            int count = GroupCreateFinalActivity.this.selectedContacts.size() + 2;
            if (GroupCreateFinalActivity.this.currentGroupCreateAddress != null) {
                return count + 3;
            }
            return count;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 3;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.ui.Cells.GroupCreateUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r6, int r7) {
            /*
                r5 = this;
                switch(r7) {
                    case 0: goto L_0x0023;
                    case 1: goto L_0x0015;
                    case 2: goto L_0x000b;
                    default: goto L_0x0003;
                }
            L_0x0003:
                org.telegram.ui.Cells.TextSettingsCell r0 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r1 = r5.context
                r0.<init>(r1)
                goto L_0x004d
            L_0x000b:
                org.telegram.ui.Cells.GroupCreateUserCell r0 = new org.telegram.ui.Cells.GroupCreateUserCell
                android.content.Context r1 = r5.context
                r2 = 3
                r3 = 0
                r0.<init>(r1, r3, r2, r3)
                goto L_0x004d
            L_0x0015:
                org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r1 = r5.context
                r0.<init>(r1)
                r1 = 46
                r0.setHeight(r1)
                r1 = r0
                goto L_0x004d
            L_0x0023:
                org.telegram.ui.Cells.ShadowSectionCell r0 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r1 = r5.context
                r0.<init>(r1)
                android.content.Context r1 = r5.context
                r2 = 2131165467(0x7var_b, float:1.7945152E38)
                java.lang.String r3 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r2, (java.lang.String) r3)
                org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r3 = new android.graphics.drawable.ColorDrawable
                java.lang.String r4 = "windowBackgroundGray"
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r3.<init>(r4)
                r2.<init>(r3, r1)
                r3 = 1
                r2.setFullsize(r3)
                r0.setBackgroundDrawable(r2)
            L_0x004d:
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateFinalActivity.GroupCreateAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1:
                    HeaderCell cell = (HeaderCell) holder.itemView;
                    if (GroupCreateFinalActivity.this.currentGroupCreateAddress == null || position != 1) {
                        cell.setText(LocaleController.formatPluralString("Members", GroupCreateFinalActivity.this.selectedContacts.size()));
                        return;
                    } else {
                        cell.setText(LocaleController.getString("AttachLocation", NUM));
                        return;
                    }
                case 2:
                    ((GroupCreateUserCell) holder.itemView).setObject(GroupCreateFinalActivity.this.getMessagesController().getUser((Long) GroupCreateFinalActivity.this.selectedContacts.get(position - this.usersStartRow)), (CharSequence) null, (CharSequence) null);
                    return;
                case 3:
                    ((TextSettingsCell) holder.itemView).setText(GroupCreateFinalActivity.this.currentGroupCreateAddress, false);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (GroupCreateFinalActivity.this.currentGroupCreateAddress == null) {
                this.usersStartRow = 2;
            } else if (position == 0) {
                return 0;
            } else {
                if (position == 1) {
                    return 1;
                }
                if (position == 2) {
                    return 3;
                }
                position -= 3;
                this.usersStartRow = 5;
            }
            switch (position) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                default:
                    return 2;
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 2) {
                ((GroupCreateUserCell) holder.itemView).recycle();
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new GroupCreateFinalActivity$$ExternalSyntheticLambda7(this);
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollActive"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollInactive"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_hintText"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_cursor"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, (Paint) null, Theme.avatarDrawables, cellDelegate, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressInner2"));
        themeDescriptions.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressOuter2"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$9$org-telegram-ui-GroupCreateFinalActivity  reason: not valid java name */
    public /* synthetic */ void m3031x91f4CLASSNAME() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) child).update(0);
                }
            }
        }
    }
}

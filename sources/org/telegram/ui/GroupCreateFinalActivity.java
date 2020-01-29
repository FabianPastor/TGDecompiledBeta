package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
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
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.LocationActivity;

public class GroupCreateFinalActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate {
    private static final int done_button = 1;
    private GroupCreateAdapter adapter;
    private TLRPC.FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    /* access modifiers changed from: private */
    public ImageView avatarEditor;
    /* access modifiers changed from: private */
    public BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public View avatarOverlay;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
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
    private ImageUpdater imageUpdater;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private String nameToSet;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    private int reqId;
    /* access modifiers changed from: private */
    public ArrayList<Integer> selectedContacts;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private TLRPC.InputFile uploadedAvatar;

    public interface GroupCreateFinalActivityDelegate {
        void didFailChatCreation();

        void didFinishChatCreation(GroupCreateFinalActivity groupCreateFinalActivity, int i);

        void didStartChatCreation();
    }

    static /* synthetic */ boolean lambda$createView$1(View view, MotionEvent motionEvent) {
        return true;
    }

    public GroupCreateFinalActivity(Bundle bundle) {
        super(bundle);
        this.chatType = bundle.getInt("chatType", 0);
        this.currentGroupCreateAddress = bundle.getString("address");
        this.currentGroupCreateLocation = (Location) bundle.getParcelable("location");
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidFailCreate);
        this.imageUpdater = new ImageUpdater();
        ImageUpdater imageUpdater2 = this.imageUpdater;
        imageUpdater2.parentFragment = this;
        imageUpdater2.delegate = this;
        this.selectedContacts = getArguments().getIntegerArrayList("result");
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.selectedContacts.size(); i++) {
            Integer num = this.selectedContacts.get(i);
            if (MessagesController.getInstance(this.currentAccount).getUser(num) == null) {
                arrayList.add(num);
            }
        }
        if (!arrayList.isEmpty()) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            ArrayList arrayList2 = new ArrayList();
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(arrayList2, arrayList, countDownLatch) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ CountDownLatch f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    GroupCreateFinalActivity.this.lambda$onFragmentCreate$0$GroupCreateFinalActivity(this.f$1, this.f$2, this.f$3);
                }
            });
            try {
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (arrayList.size() != arrayList2.size() || arrayList2.isEmpty()) {
                return false;
            }
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                MessagesController.getInstance(this.currentAccount).putUser((TLRPC.User) it.next(), true);
            }
        }
        return super.onFragmentCreate();
    }

    public /* synthetic */ void lambda$onFragmentCreate$0$GroupCreateFinalActivity(ArrayList arrayList, ArrayList arrayList2, CountDownLatch countDownLatch) {
        arrayList.addAll(MessagesStorage.getInstance(this.currentAccount).getUsers(arrayList2));
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
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return true;
        }
        this.editText.hidePopup(true);
        return false;
    }

    public View createView(Context context) {
        String str;
        int i;
        CombinedDrawable combinedDrawable;
        Context context2 = context;
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NewGroup", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    GroupCreateFinalActivity.this.finishFragment();
                }
            }
        });
        AnonymousClass2 r2 = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                int paddingTop = size2 - getPaddingTop();
                measureChildWithMargins(GroupCreateFinalActivity.this.actionBar, i, 0, i2, 0);
                if (getKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    GroupCreateFinalActivity.this.editText.hideEmojiView();
                    this.ignoreLayout = false;
                }
                int childCount = getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == GroupCreateFinalActivity.this.actionBar)) {
                        if (GroupCreateFinalActivity.this.editText == null || !GroupCreateFinalActivity.this.editText.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (paddingTop - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((paddingTop - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:25:0x0072  */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x008c  */
            /* JADX WARNING: Removed duplicated region for block: B:40:0x00b3  */
            /* JADX WARNING: Removed duplicated region for block: B:41:0x00bc  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                    r9 = this;
                    int r10 = r9.getChildCount()
                    int r0 = r9.getKeyboardHeight()
                    r1 = 1101004800(0x41a00000, float:20.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    r2 = 0
                    if (r0 > r1) goto L_0x0026
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r0 != 0) goto L_0x0026
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r0 != 0) goto L_0x0026
                    org.telegram.ui.GroupCreateFinalActivity r0 = org.telegram.ui.GroupCreateFinalActivity.this
                    org.telegram.ui.Components.EditTextEmoji r0 = r0.editText
                    int r0 = r0.getEmojiPadding()
                    goto L_0x0027
                L_0x0026:
                    r0 = 0
                L_0x0027:
                    r9.setBottomClip(r0)
                L_0x002a:
                    if (r2 >= r10) goto L_0x00d3
                    android.view.View r1 = r9.getChildAt(r2)
                    int r3 = r1.getVisibility()
                    r4 = 8
                    if (r3 != r4) goto L_0x003a
                    goto L_0x00cf
                L_0x003a:
                    android.view.ViewGroup$LayoutParams r3 = r1.getLayoutParams()
                    android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
                    int r4 = r1.getMeasuredWidth()
                    int r5 = r1.getMeasuredHeight()
                    int r6 = r3.gravity
                    r7 = -1
                    if (r6 != r7) goto L_0x004f
                    r6 = 51
                L_0x004f:
                    r7 = r6 & 7
                    r6 = r6 & 112(0x70, float:1.57E-43)
                    r7 = r7 & 7
                    r8 = 1
                    if (r7 == r8) goto L_0x0063
                    r8 = 5
                    if (r7 == r8) goto L_0x005e
                    int r7 = r3.leftMargin
                    goto L_0x006e
                L_0x005e:
                    int r7 = r13 - r4
                    int r8 = r3.rightMargin
                    goto L_0x006d
                L_0x0063:
                    int r7 = r13 - r11
                    int r7 = r7 - r4
                    int r7 = r7 / 2
                    int r8 = r3.leftMargin
                    int r7 = r7 + r8
                    int r8 = r3.rightMargin
                L_0x006d:
                    int r7 = r7 - r8
                L_0x006e:
                    r8 = 16
                    if (r6 == r8) goto L_0x008c
                    r8 = 48
                    if (r6 == r8) goto L_0x0084
                    r8 = 80
                    if (r6 == r8) goto L_0x007d
                    int r3 = r3.topMargin
                    goto L_0x0099
                L_0x007d:
                    int r6 = r14 - r0
                    int r6 = r6 - r12
                    int r6 = r6 - r5
                    int r3 = r3.bottomMargin
                    goto L_0x0097
                L_0x0084:
                    int r3 = r3.topMargin
                    int r6 = r9.getPaddingTop()
                    int r3 = r3 + r6
                    goto L_0x0099
                L_0x008c:
                    int r6 = r14 - r0
                    int r6 = r6 - r12
                    int r6 = r6 - r5
                    int r6 = r6 / 2
                    int r8 = r3.topMargin
                    int r6 = r6 + r8
                    int r3 = r3.bottomMargin
                L_0x0097:
                    int r3 = r6 - r3
                L_0x0099:
                    org.telegram.ui.GroupCreateFinalActivity r6 = org.telegram.ui.GroupCreateFinalActivity.this
                    org.telegram.ui.Components.EditTextEmoji r6 = r6.editText
                    if (r6 == 0) goto L_0x00ca
                    org.telegram.ui.GroupCreateFinalActivity r6 = org.telegram.ui.GroupCreateFinalActivity.this
                    org.telegram.ui.Components.EditTextEmoji r6 = r6.editText
                    boolean r6 = r6.isPopupView(r1)
                    if (r6 == 0) goto L_0x00ca
                    boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r3 == 0) goto L_0x00bc
                    int r3 = r9.getMeasuredHeight()
                    int r6 = r1.getMeasuredHeight()
                    goto L_0x00c9
                L_0x00bc:
                    int r3 = r9.getMeasuredHeight()
                    int r6 = r9.getKeyboardHeight()
                    int r3 = r3 + r6
                    int r6 = r1.getMeasuredHeight()
                L_0x00c9:
                    int r3 = r3 - r6
                L_0x00ca:
                    int r4 = r4 + r7
                    int r5 = r5 + r3
                    r1.layout(r7, r3, r4, r5)
                L_0x00cf:
                    int r2 = r2 + 1
                    goto L_0x002a
                L_0x00d3:
                    r9.notifyHeightChanged()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateFinalActivity.AnonymousClass2.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.fragmentView = r2;
        this.fragmentView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.fragmentView.setOnTouchListener($$Lambda$GroupCreateFinalActivity$zr63HmKznAwKqWBGncMkUB2788.INSTANCE);
        this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        AnonymousClass3 r4 = new LinearLayout(context2) {
            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == GroupCreateFinalActivity.this.listView && GroupCreateFinalActivity.this.shadowDrawable != null) {
                    int measuredHeight = GroupCreateFinalActivity.this.editTextContainer.getMeasuredHeight();
                    GroupCreateFinalActivity.this.shadowDrawable.setBounds(0, measuredHeight, getMeasuredWidth(), GroupCreateFinalActivity.this.shadowDrawable.getIntrinsicHeight() + measuredHeight);
                    GroupCreateFinalActivity.this.shadowDrawable.draw(canvas);
                }
                return drawChild;
            }
        };
        r4.setOrientation(1);
        r2.addView(r4, LayoutHelper.createFrame(-1, -1.0f));
        this.editTextContainer = new FrameLayout(context2);
        r4.addView(this.editTextContainer, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context2) {
            public void invalidate() {
                if (GroupCreateFinalActivity.this.avatarOverlay != null) {
                    GroupCreateFinalActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate();
            }

            public void invalidate(int i, int i2, int i3, int i4) {
                if (GroupCreateFinalActivity.this.avatarOverlay != null) {
                    GroupCreateFinalActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate(i, i2, i3, i4);
            }
        };
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable.setInfo(5, (String) null, (String) null);
        this.avatarImage.setImageDrawable(this.avatarDrawable);
        this.avatarImage.setContentDescription(LocaleController.getString("ChoosePhoto", NUM));
        int i2 = 3;
        this.editTextContainer.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        final Paint paint = new Paint(1);
        paint.setColor(NUM);
        this.avatarOverlay = new View(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (GroupCreateFinalActivity.this.avatarImage != null && GroupCreateFinalActivity.this.avatarProgressView.getVisibility() == 0) {
                    paint.setAlpha((int) (GroupCreateFinalActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f * GroupCreateFinalActivity.this.avatarProgressView.getAlpha()));
                    canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(32.0f), paint);
                }
            }
        };
        this.editTextContainer.addView(this.avatarOverlay, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        this.avatarOverlay.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCreateFinalActivity.this.lambda$createView$3$GroupCreateFinalActivity(view);
            }
        });
        this.avatarEditor = new ImageView(context2) {
            public void invalidate(int i, int i2, int i3, int i4) {
                super.invalidate(i, i2, i3, i4);
                GroupCreateFinalActivity.this.avatarOverlay.invalidate();
            }

            public void invalidate() {
                super.invalidate();
                GroupCreateFinalActivity.this.avatarOverlay.invalidate();
            }
        };
        this.avatarEditor.setScaleType(ImageView.ScaleType.CENTER);
        this.avatarEditor.setImageResource(NUM);
        this.avatarEditor.setEnabled(false);
        this.avatarEditor.setClickable(false);
        this.avatarEditor.setPadding(AndroidUtilities.dp(2.0f), 0, 0, 0);
        this.editTextContainer.addView(this.avatarEditor, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        this.avatarProgressView = new RadialProgressView(context2) {
            public void setAlpha(float f) {
                super.setAlpha(f);
                GroupCreateFinalActivity.this.avatarOverlay.invalidate();
            }
        };
        this.avatarProgressView.setSize(AndroidUtilities.dp(30.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.editTextContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        showAvatarProgress(false, false);
        this.editText = new EditTextEmoji(context2, r2, this, 0);
        EditTextEmoji editTextEmoji2 = this.editText;
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
        this.listView = new RecyclerListView(context2);
        RecyclerListView recyclerListView = this.listView;
        GroupCreateAdapter groupCreateAdapter = new GroupCreateAdapter(context2);
        this.adapter = groupCreateAdapter;
        recyclerListView.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        GroupCreateDividerItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        groupCreateDividerItemDecoration.setSkipRows(this.currentGroupCreateAddress != null ? 5 : 2);
        this.listView.addItemDecoration(groupCreateDividerItemDecoration);
        r4.addView(this.listView, LayoutHelper.createLinear(-1, -1));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(GroupCreateFinalActivity.this.editText);
                }
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                GroupCreateFinalActivity.this.lambda$createView$5$GroupCreateFinalActivity(view, i);
            }
        });
        this.floatingButtonContainer = new FrameLayout(context2);
        float f = 56.0f;
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable2.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            combinedDrawable = combinedDrawable2;
        } else {
            combinedDrawable = createSimpleSelectorCircleDrawable;
        }
        this.floatingButtonContainer.setBackgroundDrawable(combinedDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButtonContainer.setStateListAnimator(stateListAnimator);
            this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        FrameLayout frameLayout = this.floatingButtonContainer;
        int i4 = Build.VERSION.SDK_INT >= 21 ? 56 : 60;
        float f2 = Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f;
        if (!LocaleController.isRTL) {
            i2 = 5;
        }
        r2.addView(frameLayout, LayoutHelper.createFrame(i4, f2, i2 | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        this.floatingButtonContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCreateFinalActivity.this.lambda$createView$6$GroupCreateFinalActivity(view);
            }
        });
        this.floatingButtonIcon = new ImageView(context2);
        this.floatingButtonIcon.setScaleType(ImageView.ScaleType.CENTER);
        this.floatingButtonIcon.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.floatingButtonIcon.setImageResource(NUM);
        this.floatingButtonIcon.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        this.floatingButtonContainer.setContentDescription(LocaleController.getString("Done", NUM));
        FrameLayout frameLayout2 = this.floatingButtonContainer;
        ImageView imageView = this.floatingButtonIcon;
        int i5 = Build.VERSION.SDK_INT >= 21 ? 56 : 60;
        if (Build.VERSION.SDK_INT < 21) {
            f = 60.0f;
        }
        frameLayout2.addView(imageView, LayoutHelper.createFrame(i5, f));
        this.progressView = new ContextProgressView(context2, 1);
        this.progressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.floatingButtonContainer.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$3$GroupCreateFinalActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() {
            public final void run() {
                GroupCreateFinalActivity.this.lambda$null$2$GroupCreateFinalActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$2$GroupCreateFinalActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.uploadedAvatar = null;
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) null);
        this.avatarEditor.setImageResource(NUM);
    }

    public /* synthetic */ void lambda$createView$5$GroupCreateFinalActivity(View view, int i) {
        if ((view instanceof TextSettingsCell) && AndroidUtilities.isGoogleMapsInstalled(this)) {
            LocationActivity locationActivity = new LocationActivity(4);
            locationActivity.setDialogId(0);
            locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate() {
                public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                    GroupCreateFinalActivity.this.lambda$null$4$GroupCreateFinalActivity(messageMedia, i, z, i2);
                }
            });
            presentFragment(locationActivity);
        }
    }

    public /* synthetic */ void lambda$null$4$GroupCreateFinalActivity(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
        this.currentGroupCreateLocation.setLatitude(messageMedia.geo.lat);
        this.currentGroupCreateLocation.setLongitude(messageMedia.geo._long);
        this.currentGroupCreateAddress = messageMedia.address;
    }

    public /* synthetic */ void lambda$createView$6$GroupCreateFinalActivity(View view) {
        if (!this.donePressed) {
            if (this.editText.length() == 0) {
                Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                AndroidUtilities.shakeView(this.editText, 2.0f, 0);
                return;
            }
            this.donePressed = true;
            AndroidUtilities.hideKeyboard(this.editText);
            this.editText.setEnabled(false);
            if (this.imageUpdater.uploadingImage != null) {
                this.createAfterUpload = true;
                return;
            }
            showEditDoneProgress(true);
            this.reqId = MessagesController.getInstance(this.currentAccount).createChat(this.editText.getText().toString(), this.selectedContacts, (String) null, this.chatType, this.currentGroupCreateLocation, this.currentGroupCreateAddress, this);
        }
    }

    public void didUploadPhoto(TLRPC.InputFile inputFile, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new Runnable(inputFile, photoSize2, photoSize) {
            private final /* synthetic */ TLRPC.InputFile f$1;
            private final /* synthetic */ TLRPC.PhotoSize f$2;
            private final /* synthetic */ TLRPC.PhotoSize f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                GroupCreateFinalActivity.this.lambda$didUploadPhoto$7$GroupCreateFinalActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$didUploadPhoto$7$GroupCreateFinalActivity(TLRPC.InputFile inputFile, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2) {
        if (inputFile != null) {
            this.uploadedAvatar = inputFile;
            if (this.createAfterUpload) {
                GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate = this.delegate;
                if (groupCreateFinalActivityDelegate != null) {
                    groupCreateFinalActivityDelegate.didStartChatCreation();
                }
                MessagesController.getInstance(this.currentAccount).createChat(this.editText.getText().toString(), this.selectedContacts, (String) null, this.chatType, this.currentGroupCreateLocation, this.currentGroupCreateAddress, this);
            }
            showAvatarProgress(false, true);
            this.avatarEditor.setImageDrawable((Drawable) null);
            return;
        }
        this.avatar = photoSize.location;
        this.avatarBig = photoSize2.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", (Drawable) this.avatarDrawable, (Object) null);
        showAvatarProgress(true, false);
    }

    public String getInitialSearchString() {
        return this.editText.getText().toString();
    }

    public void setDelegate(GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate) {
        this.delegate = groupCreateFinalActivityDelegate;
    }

    private void showAvatarProgress(final boolean z, boolean z2) {
        if (this.avatarEditor != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (z2) {
                this.avatarAnimation = new AnimatorSet();
                if (z) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f})});
                } else {
                    this.avatarEditor.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f})});
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (GroupCreateFinalActivity.this.avatarAnimation != null && GroupCreateFinalActivity.this.avatarEditor != null) {
                            if (z) {
                                GroupCreateFinalActivity.this.avatarEditor.setVisibility(4);
                            } else {
                                GroupCreateFinalActivity.this.avatarProgressView.setVisibility(4);
                            }
                            AnimatorSet unused = GroupCreateFinalActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        AnimatorSet unused = GroupCreateFinalActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (z) {
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

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.imageUpdater.onActivityResult(i, i2, intent);
    }

    public void saveSelfArgs(Bundle bundle) {
        String obj;
        String str;
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (!(imageUpdater2 == null || (str = imageUpdater2.currentPicturePath) == null)) {
            bundle.putString("path", str);
        }
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null && (obj = editTextEmoji.getText().toString()) != null && obj.length() != 0) {
            bundle.putString("nameTextView", obj);
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.currentPicturePath = bundle.getString("path");
        }
        String string = bundle.getString("nameTextView");
        if (string != null) {
            EditTextEmoji editTextEmoji = this.editText;
            if (editTextEmoji != null) {
                editTextEmoji.setText(string);
            } else {
                this.nameToSet = string;
            }
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.editText.openKeyboard();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                int intValue = objArr[0].intValue();
                if ((intValue & 2) != 0 || (intValue & 1) != 0 || (intValue & 4) != 0) {
                    int childCount = this.listView.getChildCount();
                    for (int i3 = 0; i3 < childCount; i3++) {
                        View childAt = this.listView.getChildAt(i3);
                        if (childAt instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) childAt).update(intValue);
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
            int intValue2 = objArr[0].intValue();
            GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate2 = this.delegate;
            if (groupCreateFinalActivityDelegate2 != null) {
                groupCreateFinalActivityDelegate2.didFinishChatCreation(this, intValue2);
            } else {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                Bundle bundle = new Bundle();
                bundle.putInt("chat_id", intValue2);
                presentFragment(new ChatActivity(bundle), true);
            }
            if (this.uploadedAvatar != null) {
                MessagesController.getInstance(this.currentAccount).changeChatAvatar(intValue2, this.uploadedAvatar, this.avatar, this.avatarBig);
            }
        }
    }

    private void showEditDoneProgress(boolean z) {
        final boolean z2 = z;
        if (this.floatingButtonIcon != null) {
            AnimatorSet animatorSet = this.doneItemAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.doneItemAnimation = new AnimatorSet();
            if (z2) {
                this.progressView.setVisibility(0);
                this.floatingButtonContainer.setEnabled(false);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButtonIcon, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f})});
            } else {
                this.floatingButtonIcon.setVisibility(0);
                this.floatingButtonContainer.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "alpha", new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (GroupCreateFinalActivity.this.doneItemAnimation != null && GroupCreateFinalActivity.this.doneItemAnimation.equals(animator)) {
                        if (!z2) {
                            GroupCreateFinalActivity.this.progressView.setVisibility(4);
                        } else {
                            GroupCreateFinalActivity.this.floatingButtonIcon.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (GroupCreateFinalActivity.this.doneItemAnimation != null && GroupCreateFinalActivity.this.doneItemAnimation.equals(animator)) {
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

        public GroupCreateAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            int size = GroupCreateFinalActivity.this.selectedContacts.size() + 2;
            return GroupCreateFinalActivity.this.currentGroupCreateAddress != null ? size + 3 : size;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 3;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: org.telegram.ui.Cells.GroupCreateUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
            /*
                r4 = this;
                r5 = 1
                if (r6 == 0) goto L_0x0027
                if (r6 == r5) goto L_0x001a
                r5 = 2
                if (r6 == r5) goto L_0x0010
                org.telegram.ui.Cells.TextSettingsCell r5 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r6 = r4.context
                r5.<init>(r6)
                goto L_0x0052
            L_0x0010:
                org.telegram.ui.Cells.GroupCreateUserCell r5 = new org.telegram.ui.Cells.GroupCreateUserCell
                android.content.Context r6 = r4.context
                r0 = 0
                r1 = 3
                r5.<init>(r6, r0, r1)
                goto L_0x0052
            L_0x001a:
                org.telegram.ui.Cells.HeaderCell r5 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r6 = r4.context
                r5.<init>(r6)
                r6 = 46
                r5.setHeight(r6)
                goto L_0x0052
            L_0x0027:
                org.telegram.ui.Cells.ShadowSectionCell r6 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r0 = r4.context
                r6.<init>(r0)
                android.content.Context r0 = r4.context
                r1 = 2131165409(0x7var_e1, float:1.7945034E38)
                java.lang.String r2 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r0, (int) r1, (java.lang.String) r2)
                org.telegram.ui.Components.CombinedDrawable r1 = new org.telegram.ui.Components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r2 = new android.graphics.drawable.ColorDrawable
                java.lang.String r3 = "windowBackgroundGray"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r2.<init>(r3)
                r1.<init>(r2, r0)
                r1.setFullsize(r5)
                r6.setBackgroundDrawable(r1)
                r5 = r6
            L_0x0052:
                org.telegram.ui.Components.RecyclerListView$Holder r6 = new org.telegram.ui.Components.RecyclerListView$Holder
                r6.<init>(r5)
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateFinalActivity.GroupCreateAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 1) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (GroupCreateFinalActivity.this.currentGroupCreateAddress == null || i != 1) {
                    headerCell.setText(LocaleController.formatPluralString("Members", GroupCreateFinalActivity.this.selectedContacts.size()));
                } else {
                    headerCell.setText(LocaleController.getString("AttachLocation", NUM));
                }
            } else if (itemViewType == 2) {
                ((GroupCreateUserCell) viewHolder.itemView).setObject(MessagesController.getInstance(GroupCreateFinalActivity.this.currentAccount).getUser((Integer) GroupCreateFinalActivity.this.selectedContacts.get(i - this.usersStartRow)), (CharSequence) null, (CharSequence) null);
            } else if (itemViewType == 3) {
                ((TextSettingsCell) viewHolder.itemView).setText(GroupCreateFinalActivity.this.currentGroupCreateAddress, false);
            }
        }

        public int getItemViewType(int i) {
            if (GroupCreateFinalActivity.this.currentGroupCreateAddress == null) {
                this.usersStartRow = 2;
            } else if (i == 0) {
                return 0;
            } else {
                if (i == 1) {
                    return 1;
                }
                if (i == 2) {
                    return 3;
                }
                i -= 3;
                this.usersStartRow = 5;
            }
            if (i != 0) {
                return i != 1 ? 2 : 1;
            }
            return 0;
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 2) {
                ((GroupCreateUserCell) viewHolder.itemView).recycle();
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$GroupCreateFinalActivity$VPEySrwsnDav95hCHqJfgjLqoE r9 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                GroupCreateFinalActivity.this.lambda$getThemeDescriptions$8$GroupCreateFinalActivity();
            }
        };
        $$Lambda$GroupCreateFinalActivity$VPEySrwsnDav95hCHqJfgjLqoE r7 = r9;
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollActive"), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollInactive"), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollText"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_hintText"), new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_cursor"), new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"), new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, r7, "avatar_text"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundRed"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundOrange"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundViolet"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundGreen"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundCyan"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundBlue"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundPink"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressInner2"), new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressOuter2"), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText")};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$8$GroupCreateFinalActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) childAt).update(0);
                }
            }
        }
    }
}

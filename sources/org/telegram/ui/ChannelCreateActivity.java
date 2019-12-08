package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC.TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_chats;
import org.telegram.tgnet.TLRPC.TL_messages_exportChatInvite;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AdminedChannelCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class ChannelCreateActivity extends BaseFragment implements NotificationCenterDelegate, ImageUpdaterDelegate {
    private static final int done_button = 1;
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList();
    private TextInfoPrivacyCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private FileLocation avatarBig;
    private AvatarDrawable avatarDrawable;
    private ImageView avatarEditor;
    private BackupImageView avatarImage;
    private View avatarOverlay;
    private RadialProgressView avatarProgressView;
    private boolean canCreatePublic = true;
    private int chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private boolean createAfterUpload;
    private int currentStep;
    private EditTextBoldCursor descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private EditText editText;
    private HeaderCell headerCell;
    private TextView helpTextView;
    private ImageUpdater imageUpdater;
    private ExportedChatInvite invite;
    private boolean isPrivate;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout2;
    private LinearLayout linkContainer;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    private EditTextEmoji nameTextView;
    private String nameToSet;
    private TextBlockCell privateContainer;
    private AlertDialog progressDialog;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private ShadowSectionCell sectionCell;
    private TextInfoPrivacyCell typeInfoCell;
    private InputFile uploadedAvatar;

    public ChannelCreateActivity(Bundle bundle) {
        super(bundle);
        this.currentStep = bundle.getInt("step", 0);
        int i = this.currentStep;
        if (i == 0) {
            this.avatarDrawable = new AvatarDrawable();
            this.imageUpdater = new ImageUpdater();
            TL_channels_checkUsername tL_channels_checkUsername = new TL_channels_checkUsername();
            tL_channels_checkUsername.username = "1";
            tL_channels_checkUsername.channel = new TL_inputChannelEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_checkUsername, new -$$Lambda$ChannelCreateActivity$Dgmh8FVFPsTpRB5V7O64UrcVHdE(this));
            return;
        }
        if (i == 1) {
            this.canCreatePublic = bundle.getBoolean("canCreatePublic", true);
            boolean z = this.canCreatePublic;
            this.isPrivate = z ^ 1;
            if (!z) {
                loadAdminedChannels();
            }
        }
        this.chatId = bundle.getInt("chat_id", 0);
    }

    public /* synthetic */ void lambda$new$1$ChannelCreateActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChannelCreateActivity$3ctYbjqxEmY29fuS63ZiDPUydpA(this, tL_error));
    }

    public /* synthetic */ void lambda$null$0$ChannelCreateActivity(TL_error tL_error) {
        boolean z = tL_error == null || !tL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
        this.canCreatePublic = z;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidFailCreate);
        if (this.currentStep == 1) {
            generateLink();
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.parentFragment = this;
            imageUpdater.delegate = this;
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidFailCreate);
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.clear();
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    public void onResume() {
        super.onResume();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return true;
        }
        this.nameTextView.hidePopup(true);
        return false;
    }

    public View createView(Context context) {
        Context context2 = context;
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ChannelCreateActivity.this.finishFragment();
                } else if (i == 1) {
                    String str = "vibrator";
                    Vibrator vibrator;
                    if (ChannelCreateActivity.this.currentStep == 0) {
                        if (!ChannelCreateActivity.this.donePressed && ChannelCreateActivity.this.getParentActivity() != null) {
                            if (ChannelCreateActivity.this.nameTextView.length() == 0) {
                                vibrator = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService(str);
                                if (vibrator != null) {
                                    vibrator.vibrate(200);
                                }
                                AndroidUtilities.shakeView(ChannelCreateActivity.this.nameTextView, 2.0f, 0);
                                return;
                            }
                            ChannelCreateActivity.this.donePressed = true;
                            if (ChannelCreateActivity.this.imageUpdater.uploadingImage != null) {
                                ChannelCreateActivity.this.createAfterUpload = true;
                                ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                                channelCreateActivity.progressDialog = new AlertDialog(channelCreateActivity.getParentActivity(), 3);
                                ChannelCreateActivity.this.progressDialog.setOnCancelListener(new -$$Lambda$ChannelCreateActivity$1$b7xFRmQZDJGprfBGkWoRomCKvDQ(this));
                                ChannelCreateActivity.this.progressDialog.show();
                                return;
                            }
                            i = MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).createChat(ChannelCreateActivity.this.nameTextView.getText().toString(), new ArrayList(), ChannelCreateActivity.this.descriptionTextView.getText().toString(), 2, null, null, ChannelCreateActivity.this);
                            ChannelCreateActivity channelCreateActivity2 = ChannelCreateActivity.this;
                            channelCreateActivity2.progressDialog = new AlertDialog(channelCreateActivity2.getParentActivity(), 3);
                            ChannelCreateActivity.this.progressDialog.setOnCancelListener(new -$$Lambda$ChannelCreateActivity$1$q1TLoAihH5rBozhaNhmW5QGac-s(this, i));
                            ChannelCreateActivity.this.progressDialog.show();
                        }
                    } else if (ChannelCreateActivity.this.currentStep == 1) {
                        if (!ChannelCreateActivity.this.isPrivate) {
                            if (ChannelCreateActivity.this.descriptionTextView.length() == 0) {
                                Builder builder = new Builder(ChannelCreateActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", NUM));
                                builder.setMessage(LocaleController.getString("ChannelPublicEmptyUsername", NUM));
                                builder.setPositiveButton(LocaleController.getString("Close", NUM), null);
                                ChannelCreateActivity.this.showDialog(builder.create());
                                return;
                            } else if (ChannelCreateActivity.this.lastNameAvailable) {
                                MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).updateChannelUserName(ChannelCreateActivity.this.chatId, ChannelCreateActivity.this.lastCheckName);
                            } else {
                                vibrator = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService(str);
                                if (vibrator != null) {
                                    vibrator.vibrate(200);
                                }
                                AndroidUtilities.shakeView(ChannelCreateActivity.this.checkTextView, 2.0f, 0);
                                return;
                            }
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("step", 2);
                        bundle.putInt("chatId", ChannelCreateActivity.this.chatId);
                        bundle.putInt("chatType", 2);
                        ChannelCreateActivity.this.presentFragment(new GroupCreateActivity(bundle), true);
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ChannelCreateActivity$1(DialogInterface dialogInterface) {
                ChannelCreateActivity.this.createAfterUpload = false;
                ChannelCreateActivity.this.progressDialog = null;
                ChannelCreateActivity.this.donePressed = false;
            }

            public /* synthetic */ void lambda$onItemClick$1$ChannelCreateActivity$1(int i, DialogInterface dialogInterface) {
                ConnectionsManager.getInstance(ChannelCreateActivity.this.currentAccount).cancelRequest(i, true);
                ChannelCreateActivity.this.donePressed = false;
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        int i = this.currentStep;
        String str = "windowBackgroundWhiteHintText";
        String str2 = "windowBackgroundWhiteBlackText";
        String str3 = "windowBackgroundWhite";
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("NewChannel", NUM));
            AnonymousClass2 anonymousClass2 = new SizeNotifierFrameLayout(context2) {
                private boolean ignoreLayout;

                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    int size = MeasureSpec.getSize(i);
                    int size2 = MeasureSpec.getSize(i2);
                    setMeasuredDimension(size, size2);
                    size2 -= getPaddingTop();
                    measureChildWithMargins(ChannelCreateActivity.this.actionBar, i, 0, i2, 0);
                    int i3 = 0;
                    if (getKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                        this.ignoreLayout = true;
                        ChannelCreateActivity.this.nameTextView.hideEmojiView();
                        this.ignoreLayout = false;
                    }
                    int childCount = getChildCount();
                    while (i3 < childCount) {
                        View childAt = getChildAt(i3);
                        if (!(childAt == null || childAt.getVisibility() == 8 || childAt == ChannelCreateActivity.this.actionBar)) {
                            if (ChannelCreateActivity.this.nameTextView == null || !ChannelCreateActivity.this.nameTextView.isPopupView(childAt)) {
                                measureChildWithMargins(childAt, i, 0, i2, 0);
                            } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                                childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                            } else if (AndroidUtilities.isTablet()) {
                                childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                            } else {
                                childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec((size2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                            }
                        }
                        i3++;
                    }
                }

                /* Access modifiers changed, original: protected */
                /* JADX WARNING: Removed duplicated region for block: B:41:0x00bc  */
                /* JADX WARNING: Removed duplicated region for block: B:40:0x00b3  */
                /* JADX WARNING: Removed duplicated region for block: B:32:0x008c  */
                /* JADX WARNING: Removed duplicated region for block: B:25:0x0072  */
                /* JADX WARNING: Removed duplicated region for block: B:40:0x00b3  */
                /* JADX WARNING: Removed duplicated region for block: B:41:0x00bc  */
                public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                    /*
                    r9 = this;
                    r10 = r9.getChildCount();
                    r0 = r9.getKeyboardHeight();
                    r1 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                    r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                    r2 = 0;
                    if (r0 > r1) goto L_0x0026;
                L_0x0011:
                    r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow;
                    if (r0 != 0) goto L_0x0026;
                L_0x0015:
                    r0 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r0 != 0) goto L_0x0026;
                L_0x001b:
                    r0 = org.telegram.ui.ChannelCreateActivity.this;
                    r0 = r0.nameTextView;
                    r0 = r0.getEmojiPadding();
                    goto L_0x0027;
                L_0x0026:
                    r0 = 0;
                L_0x0027:
                    r9.setBottomClip(r0);
                L_0x002a:
                    if (r2 >= r10) goto L_0x00d3;
                L_0x002c:
                    r1 = r9.getChildAt(r2);
                    r3 = r1.getVisibility();
                    r4 = 8;
                    if (r3 != r4) goto L_0x003a;
                L_0x0038:
                    goto L_0x00cf;
                L_0x003a:
                    r3 = r1.getLayoutParams();
                    r3 = (android.widget.FrameLayout.LayoutParams) r3;
                    r4 = r1.getMeasuredWidth();
                    r5 = r1.getMeasuredHeight();
                    r6 = r3.gravity;
                    r7 = -1;
                    if (r6 != r7) goto L_0x004f;
                L_0x004d:
                    r6 = 51;
                L_0x004f:
                    r7 = r6 & 7;
                    r6 = r6 & 112;
                    r7 = r7 & 7;
                    r8 = 1;
                    if (r7 == r8) goto L_0x0063;
                L_0x0058:
                    r8 = 5;
                    if (r7 == r8) goto L_0x005e;
                L_0x005b:
                    r7 = r3.leftMargin;
                    goto L_0x006e;
                L_0x005e:
                    r7 = r13 - r4;
                    r8 = r3.rightMargin;
                    goto L_0x006d;
                L_0x0063:
                    r7 = r13 - r11;
                    r7 = r7 - r4;
                    r7 = r7 / 2;
                    r8 = r3.leftMargin;
                    r7 = r7 + r8;
                    r8 = r3.rightMargin;
                L_0x006d:
                    r7 = r7 - r8;
                L_0x006e:
                    r8 = 16;
                    if (r6 == r8) goto L_0x008c;
                L_0x0072:
                    r8 = 48;
                    if (r6 == r8) goto L_0x0084;
                L_0x0076:
                    r8 = 80;
                    if (r6 == r8) goto L_0x007d;
                L_0x007a:
                    r3 = r3.topMargin;
                    goto L_0x0099;
                L_0x007d:
                    r6 = r14 - r0;
                    r6 = r6 - r12;
                    r6 = r6 - r5;
                    r3 = r3.bottomMargin;
                    goto L_0x0097;
                L_0x0084:
                    r3 = r3.topMargin;
                    r6 = r9.getPaddingTop();
                    r3 = r3 + r6;
                    goto L_0x0099;
                L_0x008c:
                    r6 = r14 - r0;
                    r6 = r6 - r12;
                    r6 = r6 - r5;
                    r6 = r6 / 2;
                    r8 = r3.topMargin;
                    r6 = r6 + r8;
                    r3 = r3.bottomMargin;
                L_0x0097:
                    r3 = r6 - r3;
                L_0x0099:
                    r6 = org.telegram.ui.ChannelCreateActivity.this;
                    r6 = r6.nameTextView;
                    if (r6 == 0) goto L_0x00ca;
                L_0x00a1:
                    r6 = org.telegram.ui.ChannelCreateActivity.this;
                    r6 = r6.nameTextView;
                    r6 = r6.isPopupView(r1);
                    if (r6 == 0) goto L_0x00ca;
                L_0x00ad:
                    r3 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r3 == 0) goto L_0x00bc;
                L_0x00b3:
                    r3 = r9.getMeasuredHeight();
                    r6 = r1.getMeasuredHeight();
                    goto L_0x00c9;
                L_0x00bc:
                    r3 = r9.getMeasuredHeight();
                    r6 = r9.getKeyboardHeight();
                    r3 = r3 + r6;
                    r6 = r1.getMeasuredHeight();
                L_0x00c9:
                    r3 = r3 - r6;
                L_0x00ca:
                    r4 = r4 + r7;
                    r5 = r5 + r3;
                    r1.layout(r7, r3, r4, r5);
                L_0x00cf:
                    r2 = r2 + 1;
                    goto L_0x002a;
                L_0x00d3:
                    r9.notifyHeightChanged();
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelCreateActivity$AnonymousClass2.onLayout(boolean, int, int, int, int):void");
                }

                public void requestLayout() {
                    if (!this.ignoreLayout) {
                        super.requestLayout();
                    }
                }
            };
            anonymousClass2.setOnTouchListener(-$$Lambda$ChannelCreateActivity$lIiXta2UxN2m1Vi-R2Lzg4Rdjg4.INSTANCE);
            this.fragmentView = anonymousClass2;
            this.fragmentView.setTag(str3);
            this.fragmentView.setBackgroundColor(Theme.getColor(str3));
            this.linearLayout = new LinearLayout(context2);
            this.linearLayout.setOrientation(1);
            anonymousClass2.addView(this.linearLayout, new LayoutParams(-1, -2));
            FrameLayout frameLayout = new FrameLayout(context2);
            this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            this.avatarImage = new BackupImageView(context2) {
                public void invalidate() {
                    if (ChannelCreateActivity.this.avatarOverlay != null) {
                        ChannelCreateActivity.this.avatarOverlay.invalidate();
                    }
                    super.invalidate();
                }

                public void invalidate(int i, int i2, int i3, int i4) {
                    if (ChannelCreateActivity.this.avatarOverlay != null) {
                        ChannelCreateActivity.this.avatarOverlay.invalidate();
                    }
                    super.invalidate(i, i2, i3, i4);
                }
            };
            this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
            this.avatarDrawable.setInfo(5, null, null);
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            final Paint paint = new Paint(1);
            paint.setColor(NUM);
            this.avatarOverlay = new View(context2) {
                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    if (ChannelCreateActivity.this.avatarImage != null && ChannelCreateActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                        paint.setAlpha((int) (ChannelCreateActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                        canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(32.0f), paint);
                    }
                }
            };
            frameLayout.addView(this.avatarOverlay, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            this.avatarOverlay.setOnClickListener(new -$$Lambda$ChannelCreateActivity$s4ZwNFyULCtdH_y7TaKdhDy0a3c(this));
            this.avatarEditor = new ImageView(context2) {
                public void invalidate(int i, int i2, int i3, int i4) {
                    super.invalidate(i, i2, i3, i4);
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }

                public void invalidate() {
                    super.invalidate();
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }
            };
            this.avatarEditor.setScaleType(ScaleType.CENTER);
            this.avatarEditor.setImageResource(NUM);
            this.avatarEditor.setEnabled(false);
            this.avatarEditor.setClickable(false);
            frameLayout.addView(this.avatarEditor, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            this.avatarProgressView = new RadialProgressView(context2);
            this.avatarProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            showAvatarProgress(false, false);
            this.nameTextView = new EditTextEmoji(context2, anonymousClass2, this, 0);
            this.nameTextView.setHint(LocaleController.getString("EnterChannelName", NUM));
            String str4 = this.nameToSet;
            if (str4 != null) {
                this.nameTextView.setText(str4);
                this.nameToSet = null;
            }
            this.nameTextView.setFilters(new InputFilter[]{new LengthFilter(100)});
            frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
            this.descriptionTextView = new EditTextBoldCursor(context2);
            this.descriptionTextView.setTextSize(1, 18.0f);
            this.descriptionTextView.setHintTextColor(Theme.getColor(str));
            this.descriptionTextView.setTextColor(Theme.getColor(str2));
            this.descriptionTextView.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.descriptionTextView.setInputType(180225);
            this.descriptionTextView.setImeOptions(6);
            this.descriptionTextView.setFilters(new InputFilter[]{new LengthFilter(120)});
            this.descriptionTextView.setHint(LocaleController.getString("DescriptionPlaceholder", NUM));
            this.descriptionTextView.setCursorColor(Theme.getColor(str2));
            this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            this.descriptionTextView.setCursorWidth(1.5f);
            this.linearLayout.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 18.0f, 24.0f, 0.0f));
            this.descriptionTextView.setOnEditorActionListener(new -$$Lambda$ChannelCreateActivity$MiQ4URkqTYXvUua86XByJz6HPDg(this));
            this.descriptionTextView.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }
            });
            this.helpTextView = new TextView(context2);
            this.helpTextView.setTextSize(1, 15.0f);
            this.helpTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
            this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.helpTextView.setText(LocaleController.getString("DescriptionInfo", NUM));
            this.linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 20));
        } else if (i == 1) {
            this.fragmentView = new ScrollView(context2);
            ScrollView scrollView = (ScrollView) this.fragmentView;
            scrollView.setFillViewport(true);
            this.linearLayout = new LinearLayout(context2);
            this.linearLayout.setOrientation(1);
            scrollView.addView(this.linearLayout, new LayoutParams(-1, -2));
            this.actionBar.setTitle(LocaleController.getString("ChannelSettings", NUM));
            this.fragmentView.setTag("windowBackgroundGray");
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            this.linearLayout2 = new LinearLayout(context2);
            this.linearLayout2.setOrientation(1);
            this.linearLayout2.setBackgroundColor(Theme.getColor(str3));
            this.linearLayout.addView(this.linearLayout2, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell1 = new RadioButtonCell(context2);
            this.radioButtonCell1.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", NUM), LocaleController.getString("ChannelPublicInfo", NUM), false, this.isPrivate ^ 1);
            this.linearLayout2.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell1.setOnClickListener(new -$$Lambda$ChannelCreateActivity$ERDUGoRSlmYfCpWGH5A0thCtafY(this));
            this.radioButtonCell2 = new RadioButtonCell(context2);
            this.radioButtonCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", NUM), LocaleController.getString("ChannelPrivateInfo", NUM), false, this.isPrivate);
            this.linearLayout2.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell2.setOnClickListener(new -$$Lambda$ChannelCreateActivity$4I7Y-V5uH9YemI181a2NrRWSAsc(this));
            this.sectionCell = new ShadowSectionCell(context2);
            this.linearLayout.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
            this.linkContainer = new LinearLayout(context2);
            this.linkContainer.setOrientation(1);
            this.linkContainer.setBackgroundColor(Theme.getColor(str3));
            this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
            this.headerCell = new HeaderCell(context2);
            this.linkContainer.addView(this.headerCell);
            this.publicContainer = new LinearLayout(context2);
            this.publicContainer.setOrientation(0);
            this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0f, 7.0f, 17.0f, 0.0f));
            this.editText = new EditText(context2);
            EditText editText = this.editText;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
            stringBuilder.append("/");
            editText.setText(stringBuilder.toString());
            this.editText.setTextSize(1, 18.0f);
            this.editText.setHintTextColor(Theme.getColor(str));
            this.editText.setTextColor(Theme.getColor(str2));
            this.editText.setMaxLines(1);
            this.editText.setLines(1);
            this.editText.setEnabled(false);
            this.editText.setBackgroundDrawable(null);
            this.editText.setPadding(0, 0, 0, 0);
            this.editText.setSingleLine(true);
            this.editText.setInputType(163840);
            this.editText.setImeOptions(6);
            this.publicContainer.addView(this.editText, LayoutHelper.createLinear(-2, 36));
            this.descriptionTextView = new EditTextBoldCursor(context2);
            this.descriptionTextView.setTextSize(1, 18.0f);
            this.descriptionTextView.setHintTextColor(Theme.getColor(str));
            this.descriptionTextView.setTextColor(Theme.getColor(str2));
            this.descriptionTextView.setMaxLines(1);
            this.descriptionTextView.setLines(1);
            this.descriptionTextView.setBackgroundDrawable(null);
            this.descriptionTextView.setPadding(0, 0, 0, 0);
            this.descriptionTextView.setSingleLine(true);
            this.descriptionTextView.setInputType(163872);
            this.descriptionTextView.setImeOptions(6);
            this.descriptionTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", NUM));
            this.descriptionTextView.setCursorColor(Theme.getColor(str2));
            this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            this.descriptionTextView.setCursorWidth(1.5f);
            this.publicContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, 36));
            this.descriptionTextView.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                    channelCreateActivity.checkUserName(channelCreateActivity.descriptionTextView.getText().toString());
                }
            });
            this.privateContainer = new TextBlockCell(context2);
            this.privateContainer.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.linkContainer.addView(this.privateContainer);
            this.privateContainer.setOnClickListener(new -$$Lambda$ChannelCreateActivity$9kLD_6LVZi1jry2jmUaAGbUgTCw(this));
            this.checkTextView = new TextView(context2);
            this.checkTextView.setTextSize(1, 15.0f);
            this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.checkTextView.setVisibility(8);
            this.linkContainer.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 17, 3, 17, 7));
            this.typeInfoCell = new TextInfoPrivacyCell(context2);
            this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
            this.loadingAdminedCell = new LoadingCell(context2);
            this.linearLayout.addView(this.loadingAdminedCell, LayoutHelper.createLinear(-1, -2));
            this.adminnedChannelsLayout = new LinearLayout(context2);
            this.adminnedChannelsLayout.setBackgroundColor(Theme.getColor(str3));
            this.adminnedChannelsLayout.setOrientation(1);
            this.linearLayout.addView(this.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
            this.adminedInfoCell = new TextInfoPrivacyCell(context2);
            this.adminedInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
            updatePrivatePublic();
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$4$ChannelCreateActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new -$$Lambda$ChannelCreateActivity$B1GdgGV1tK78Cjzb4rWLcAqpV0M(this));
    }

    public /* synthetic */ void lambda$null$3$ChannelCreateActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.uploadedAvatar = null;
        showAvatarProgress(false, true);
        this.avatarImage.setImage(null, null, this.avatarDrawable, null);
    }

    public /* synthetic */ boolean lambda$createView$5$ChannelCreateActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6) {
            View view = this.doneButton;
            if (view != null) {
                view.performClick();
                return true;
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$createView$6$ChannelCreateActivity(View view) {
        if (this.isPrivate) {
            this.isPrivate = false;
            updatePrivatePublic();
        }
    }

    public /* synthetic */ void lambda$createView$7$ChannelCreateActivity(View view) {
        if (!this.isPrivate) {
            this.isPrivate = true;
            updatePrivatePublic();
        }
    }

    public /* synthetic */ void lambda$createView$8$ChannelCreateActivity(View view) {
        if (this.invite != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                Toast.makeText(getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private void generateLink() {
        if (!this.loadingInvite && this.invite == null) {
            this.loadingInvite = true;
            TL_messages_exportChatInvite tL_messages_exportChatInvite = new TL_messages_exportChatInvite();
            tL_messages_exportChatInvite.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_exportChatInvite, new -$$Lambda$ChannelCreateActivity$B1f5C5wPqlSBUACduCE1syb6pss(this));
        }
    }

    public /* synthetic */ void lambda$generateLink$10$ChannelCreateActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChannelCreateActivity$02OCxzLG_caeGKZbAxcXnBPRtU4(this, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$9$ChannelCreateActivity(TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            this.invite = (ExportedChatInvite) tLObject;
        }
        this.loadingInvite = false;
        TextBlockCell textBlockCell = this.privateContainer;
        ExportedChatInvite exportedChatInvite = this.invite;
        textBlockCell.setText(exportedChatInvite != null ? exportedChatInvite.link : LocaleController.getString("Loading", NUM), false);
    }

    private void updatePrivatePublic() {
        if (this.sectionCell != null) {
            String str = "windowBackgroundGrayShadow";
            int i = 8;
            String str2;
            TextInfoPrivacyCell textInfoPrivacyCell;
            if (this.isPrivate || this.canCreatePublic) {
                int i2;
                str2 = "windowBackgroundWhiteGrayText4";
                this.typeInfoCell.setTag(str2);
                this.typeInfoCell.setTextColor(Theme.getColor(str2));
                this.sectionCell.setVisibility(0);
                this.adminedInfoCell.setVisibility(8);
                this.adminnedChannelsLayout.setVisibility(8);
                textInfoPrivacyCell = this.typeInfoCell;
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell.getContext(), NUM, str));
                this.linkContainer.setVisibility(0);
                this.loadingAdminedCell.setVisibility(8);
                textInfoPrivacyCell = this.typeInfoCell;
                if (this.isPrivate) {
                    i2 = NUM;
                    str = "ChannelPrivateLinkHelp";
                } else {
                    i2 = NUM;
                    str = "ChannelUsernameHelp";
                }
                textInfoPrivacyCell.setText(LocaleController.getString(str, i2));
                HeaderCell headerCell = this.headerCell;
                if (this.isPrivate) {
                    i2 = NUM;
                    str = "ChannelInviteLinkTitle";
                } else {
                    i2 = NUM;
                    str = "ChannelLinkTitle";
                }
                headerCell.setText(LocaleController.getString(str, i2));
                this.publicContainer.setVisibility(this.isPrivate ? 8 : 0);
                this.privateContainer.setVisibility(this.isPrivate ? 0 : 8);
                this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.dp(7.0f));
                TextBlockCell textBlockCell = this.privateContainer;
                ExportedChatInvite exportedChatInvite = this.invite;
                textBlockCell.setText(exportedChatInvite != null ? exportedChatInvite.link : LocaleController.getString("Loading", NUM), false);
                TextView textView = this.checkTextView;
                if (!(this.isPrivate || textView.length() == 0)) {
                    i = 0;
                }
                textView.setVisibility(i);
            } else {
                this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", NUM));
                str2 = "windowBackgroundWhiteRedText4";
                this.typeInfoCell.setTag(str2);
                this.typeInfoCell.setTextColor(Theme.getColor(str2));
                this.linkContainer.setVisibility(8);
                this.sectionCell.setVisibility(8);
                if (this.loadingAdminedChannels) {
                    this.loadingAdminedCell.setVisibility(0);
                    this.adminnedChannelsLayout.setVisibility(8);
                    textInfoPrivacyCell = this.typeInfoCell;
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell.getContext(), NUM, str));
                    this.adminedInfoCell.setVisibility(8);
                } else {
                    textInfoPrivacyCell = this.typeInfoCell;
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell.getContext(), NUM, str));
                    this.loadingAdminedCell.setVisibility(8);
                    this.adminnedChannelsLayout.setVisibility(0);
                    this.adminedInfoCell.setVisibility(0);
                }
            }
            this.radioButtonCell1.setChecked(this.isPrivate ^ 1, true);
            this.radioButtonCell2.setChecked(this.isPrivate, true);
            this.descriptionTextView.clearFocus();
            AndroidUtilities.hideKeyboard(this.descriptionTextView);
        }
    }

    public void didUploadPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChannelCreateActivity$m0E-xD_NIIwbm9llzALGLpdnpvI(this, inputFile, photoSize2, photoSize));
    }

    public /* synthetic */ void lambda$didUploadPhoto$11$ChannelCreateActivity(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        if (inputFile != null) {
            this.uploadedAvatar = inputFile;
            if (this.createAfterUpload) {
                try {
                    if (this.progressDialog != null && this.progressDialog.isShowing()) {
                        this.progressDialog.dismiss();
                        this.progressDialog = null;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.donePressed = false;
                this.doneButton.performClick();
            }
            showAvatarProgress(false, true);
            return;
        }
        this.avatar = photoSize.location;
        this.avatarBig = photoSize2.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", this.avatarDrawable, null);
        showAvatarProgress(true, false);
    }

    public String getInitialSearchString() {
        return this.nameTextView.getText().toString();
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
                AnimatorSet animatorSet2;
                Animator[] animatorArr;
                if (z) {
                    this.avatarProgressView.setVisibility(0);
                    animatorSet2 = this.avatarAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f});
                    animatorSet2.playTogether(animatorArr);
                } else {
                    this.avatarEditor.setVisibility(0);
                    animatorSet2 = this.avatarAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f});
                    animatorSet2.playTogether(animatorArr);
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ChannelCreateActivity.this.avatarAnimation != null && ChannelCreateActivity.this.avatarEditor != null) {
                            if (z) {
                                ChannelCreateActivity.this.avatarEditor.setVisibility(4);
                            } else {
                                ChannelCreateActivity.this.avatarProgressView.setVisibility(4);
                            }
                            ChannelCreateActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        ChannelCreateActivity.this.avatarAnimation = null;
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
        if (this.currentStep == 0) {
            String str;
            ImageUpdater imageUpdater = this.imageUpdater;
            if (imageUpdater != null) {
                str = imageUpdater.currentPicturePath;
                if (str != null) {
                    bundle.putString("path", str);
                }
            }
            EditTextEmoji editTextEmoji = this.nameTextView;
            if (editTextEmoji != null) {
                str = editTextEmoji.getText().toString();
                if (str != null && str.length() != 0) {
                    bundle.putString("nameTextView", str);
                }
            }
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        if (this.currentStep == 0) {
            ImageUpdater imageUpdater = this.imageUpdater;
            if (imageUpdater != null) {
                imageUpdater.currentPicturePath = bundle.getString("path");
            }
            String string = bundle.getString("nameTextView");
            if (string != null) {
                EditTextEmoji editTextEmoji = this.nameTextView;
                if (editTextEmoji != null) {
                    editTextEmoji.setText(string);
                } else {
                    this.nameToSet = string;
                }
            }
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && this.currentStep != 1) {
            this.nameTextView.openKeyboard();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        AlertDialog alertDialog;
        if (i == NotificationCenter.chatDidFailCreate) {
            alertDialog = this.progressDialog;
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            this.donePressed = false;
        } else if (i == NotificationCenter.chatDidCreated) {
            alertDialog = this.progressDialog;
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            i = ((Integer) objArr[0]).intValue();
            Bundle bundle = new Bundle();
            bundle.putInt("step", 1);
            bundle.putInt("chat_id", i);
            bundle.putBoolean("canCreatePublic", this.canCreatePublic);
            if (this.uploadedAvatar != null) {
                MessagesController.getInstance(this.currentAccount).changeChatAvatar(i, this.uploadedAvatar, this.avatar, this.avatarBig);
            }
            presentFragment(new ChannelCreateActivity(bundle), true);
        }
    }

    private void loadAdminedChannels() {
        if (!this.loadingAdminedChannels) {
            this.loadingAdminedChannels = true;
            updatePrivatePublic();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_channels_getAdminedPublicChannels(), new -$$Lambda$ChannelCreateActivity$sX4Ap2SwpMlriw_rHlFC0Efiffk(this));
        }
    }

    public /* synthetic */ void lambda$loadAdminedChannels$17$ChannelCreateActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChannelCreateActivity$HGsozgXd5QuUD9PHWrJWhWbHHQs(this, tLObject));
    }

    public /* synthetic */ void lambda$null$16$ChannelCreateActivity(TLObject tLObject) {
        this.loadingAdminedChannels = false;
        if (tLObject != null && getParentActivity() != null) {
            int i;
            for (i = 0; i < this.adminedChannelCells.size(); i++) {
                this.linearLayout.removeView((View) this.adminedChannelCells.get(i));
            }
            this.adminedChannelCells.clear();
            TL_messages_chats tL_messages_chats = (TL_messages_chats) tLObject;
            for (i = 0; i < tL_messages_chats.chats.size(); i++) {
                AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), new -$$Lambda$ChannelCreateActivity$v16kHitJ-P9-5lDgeOrLxNNrfqc(this));
                Chat chat = (Chat) tL_messages_chats.chats.get(i);
                boolean z = true;
                if (i != tL_messages_chats.chats.size() - 1) {
                    z = false;
                }
                adminedChannelCell.setChannel(chat, z);
                this.adminedChannelCells.add(adminedChannelCell);
                this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
            }
            updatePrivatePublic();
        }
    }

    public /* synthetic */ void lambda$null$15$ChannelCreateActivity(View view) {
        Chat currentChannel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        String str = "/";
        Object[] objArr;
        StringBuilder stringBuilder;
        if (currentChannel.megagroup) {
            objArr = new Object[2];
            stringBuilder = new StringBuilder();
            stringBuilder.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
            stringBuilder.append(str);
            stringBuilder.append(currentChannel.username);
            objArr[0] = stringBuilder.toString();
            objArr[1] = currentChannel.title;
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", NUM, objArr)));
        } else {
            objArr = new Object[2];
            stringBuilder = new StringBuilder();
            stringBuilder.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
            stringBuilder.append(str);
            stringBuilder.append(currentChannel.username);
            objArr[0] = stringBuilder.toString();
            objArr[1] = currentChannel.title;
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", NUM, objArr)));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new -$$Lambda$ChannelCreateActivity$XKw5YNSkvJyf-2lbcB8fcZcxM-I(this, currentChannel));
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$14$ChannelCreateActivity(Chat chat, DialogInterface dialogInterface, int i) {
        TL_channels_updateUsername tL_channels_updateUsername = new TL_channels_updateUsername();
        tL_channels_updateUsername.channel = MessagesController.getInputChannel(chat);
        tL_channels_updateUsername.username = "";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_updateUsername, new -$$Lambda$ChannelCreateActivity$itNfNKVGl3ReRwzmbqq8toziSoA(this), 64);
    }

    public /* synthetic */ void lambda$null$13$ChannelCreateActivity(TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ChannelCreateActivity$L_G3GfO7KOkNy_wD7C5sV0zstvg(this));
        }
    }

    public /* synthetic */ void lambda$null$12$ChannelCreateActivity() {
        this.canCreatePublic = true;
        if (this.descriptionTextView.length() > 0) {
            checkUserName(this.descriptionTextView.getText().toString());
        }
        updatePrivatePublic();
    }

    private boolean checkUserName(String str) {
        if (str == null || str.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        Runnable runnable = this.checkRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        String str2 = "windowBackgroundWhiteRedText4";
        if (str != null) {
            String str3 = "_";
            String str4 = "LinkInvalid";
            if (str.startsWith(str3) || str.endsWith(str3)) {
                this.checkTextView.setText(LocaleController.getString(str4, NUM));
                this.checkTextView.setTag(str2);
                this.checkTextView.setTextColor(Theme.getColor(str2));
                return false;
            }
            int i = 0;
            while (i < str.length()) {
                char charAt = str.charAt(i);
                if (i == 0 && charAt >= '0' && charAt <= '9') {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", NUM));
                    this.checkTextView.setTag(str2);
                    this.checkTextView.setTextColor(Theme.getColor(str2));
                    return false;
                } else if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                    this.checkTextView.setText(LocaleController.getString(str4, NUM));
                    this.checkTextView.setTag(str2);
                    this.checkTextView.setTextColor(Theme.getColor(str2));
                    return false;
                } else {
                    i++;
                }
            }
        }
        if (str == null || str.length() < 5) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", NUM));
            this.checkTextView.setTag(str2);
            this.checkTextView.setTextColor(Theme.getColor(str2));
            return false;
        } else if (str.length() > 32) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", NUM));
            this.checkTextView.setTag(str2);
            this.checkTextView.setTextColor(Theme.getColor(str2));
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", NUM));
            str2 = "windowBackgroundWhiteGrayText8";
            this.checkTextView.setTag(str2);
            this.checkTextView.setTextColor(Theme.getColor(str2));
            this.lastCheckName = str;
            this.checkRunnable = new -$$Lambda$ChannelCreateActivity$mDP7DqCeyyygG_tNsvar_xfQvY(this, str);
            AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
            return true;
        }
    }

    public /* synthetic */ void lambda$checkUserName$20$ChannelCreateActivity(String str) {
        TL_channels_checkUsername tL_channels_checkUsername = new TL_channels_checkUsername();
        tL_channels_checkUsername.username = str;
        tL_channels_checkUsername.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_checkUsername, new -$$Lambda$ChannelCreateActivity$btlKiyTzqeUa7MaXnitfG9ByjcI(this, str), 2);
    }

    public /* synthetic */ void lambda$null$19$ChannelCreateActivity(String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChannelCreateActivity$ONcbh_gtesiOrY2PbEXECoYX8sk(this, str, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$18$ChannelCreateActivity(String str, TL_error tL_error, TLObject tLObject) {
        this.checkReqId = 0;
        String str2 = this.lastCheckName;
        if (str2 != null && str2.equals(str)) {
            String str3;
            if (tL_error == null && (tLObject instanceof TL_boolTrue)) {
                this.checkTextView.setText(LocaleController.formatString("LinkAvailable", NUM, str));
                str3 = "windowBackgroundWhiteGreenText";
                this.checkTextView.setTag(str3);
                this.checkTextView.setTextColor(Theme.getColor(str3));
                this.lastNameAvailable = true;
                return;
            }
            if (tL_error == null || !tL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                this.checkTextView.setText(LocaleController.getString("LinkInUse", NUM));
            } else {
                this.canCreatePublic = false;
                loadAdminedChannels();
            }
            str3 = "windowBackgroundWhiteRedText4";
            this.checkTextView.setTag(str3);
            this.checkTextView.setTextColor(Theme.getColor(str3));
            this.lastNameAvailable = false;
        }
    }

    private void showErrorAlert(String str) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            int i = -1;
            int hashCode = str.hashCode();
            if (hashCode != NUM) {
                if (hashCode == NUM && str.equals("USERNAME_OCCUPIED")) {
                    i = 1;
                }
            } else if (str.equals("USERNAME_INVALID")) {
                i = 0;
            }
            if (i == 0) {
                builder.setMessage(LocaleController.getString("LinkInvalid", NUM));
            } else if (i != 1) {
                builder.setMessage(LocaleController.getString("ErrorOccurred", NUM));
            } else {
                builder.setMessage(LocaleController.getString("LinkInUse", NUM));
            }
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$ChannelCreateActivity$ieYbFS24yeOuvItbU135YWldd4c -__lambda_channelcreateactivity_ieybfs24yeouvitbu135ywldd4c = new -$$Lambda$ChannelCreateActivity$ieYbFS24yeOuvItbU135YWldd4c(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[54];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[7] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[8] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField");
        themeDescriptionArr[9] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated");
        themeDescriptionArr[10] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[11] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[12] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField");
        themeDescriptionArr[13] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated");
        themeDescriptionArr[14] = new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText8");
        themeDescriptionArr[15] = new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[16] = new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[17] = new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundGrayShadow");
        View view = this.headerCell;
        Class[] clsArr = new Class[]{HeaderCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[18] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[19] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[20] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[21] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteRedText4");
        themeDescriptionArr[22] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGrayText8");
        themeDescriptionArr[23] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGreenText");
        themeDescriptionArr[24] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[25] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[26] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText4");
        themeDescriptionArr[27] = new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[28] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[29] = new ThemeDescription(this.privateContainer, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[30] = new ThemeDescription(this.privateContainer, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[31] = new ThemeDescription(this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, "progressCircle");
        themeDescriptionArr[32] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        view = this.radioButtonCell1;
        int i = ThemeDescription.FLAG_CHECKBOX;
        clsArr = new Class[]{RadioButtonCell.class};
        strArr = new String[1];
        strArr[0] = "radioButton";
        themeDescriptionArr[33] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "radioBackground");
        themeDescriptionArr[34] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackgroundChecked");
        themeDescriptionArr[35] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.radioButtonCell1;
        i = ThemeDescription.FLAG_TEXTCOLOR;
        clsArr = new Class[]{RadioButtonCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        themeDescriptionArr[36] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[37] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[38] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackground");
        themeDescriptionArr[39] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackgroundChecked");
        themeDescriptionArr[40] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[41] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[42] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.adminnedChannelsLayout;
        i = ThemeDescription.FLAG_TEXTCOLOR;
        clsArr = new Class[]{AdminedChannelCell.class};
        strArr = new String[1];
        strArr[0] = "statusTextView";
        themeDescriptionArr[43] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[44] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, null, null, null, "windowBackgroundWhiteLinkText");
        themeDescriptionArr[45] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, null, null, null, "windowBackgroundWhiteGrayText");
        -$$Lambda$ChannelCreateActivity$ieYbFS24yeOuvItbU135YWldd4c -__lambda_channelcreateactivity_ieybfs24yeouvitbu135ywldd4c2 = -__lambda_channelcreateactivity_ieybfs24yeouvitbu135ywldd4c;
        themeDescriptionArr[46] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_savedDrawable}, -__lambda_channelcreateactivity_ieybfs24yeouvitbu135ywldd4c2, "avatar_text");
        themeDescriptionArr[47] = new ThemeDescription(null, 0, null, null, null, -__lambda_channelcreateactivity_ieybfs24yeouvitbu135ywldd4c2, "avatar_backgroundRed");
        themeDescriptionArr[48] = new ThemeDescription(null, 0, null, null, null, -__lambda_channelcreateactivity_ieybfs24yeouvitbu135ywldd4c2, "avatar_backgroundOrange");
        themeDescriptionArr[49] = new ThemeDescription(null, 0, null, null, null, -__lambda_channelcreateactivity_ieybfs24yeouvitbu135ywldd4c2, "avatar_backgroundViolet");
        themeDescriptionArr[50] = new ThemeDescription(null, 0, null, null, null, -__lambda_channelcreateactivity_ieybfs24yeouvitbu135ywldd4c2, "avatar_backgroundGreen");
        themeDescriptionArr[51] = new ThemeDescription(null, 0, null, null, null, -__lambda_channelcreateactivity_ieybfs24yeouvitbu135ywldd4c2, "avatar_backgroundCyan");
        themeDescriptionArr[52] = new ThemeDescription(null, 0, null, null, null, -__lambda_channelcreateactivity_ieybfs24yeouvitbu135ywldd4c2, "avatar_backgroundBlue");
        themeDescriptionArr[53] = new ThemeDescription(null, 0, null, null, null, -__lambda_channelcreateactivity_ieybfs24yeouvitbu135ywldd4c2, "avatar_backgroundPink");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$21$ChannelCreateActivity() {
        LinearLayout linearLayout = this.adminnedChannelsLayout;
        if (linearLayout != null) {
            int childCount = linearLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.adminnedChannelsLayout.getChildAt(i);
                if (childAt instanceof AdminedChannelCell) {
                    ((AdminedChannelCell) childAt).update();
                }
            }
        }
    }
}

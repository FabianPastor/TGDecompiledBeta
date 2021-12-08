package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class GroupStickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private long chatId;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    /* access modifiers changed from: private */
    public boolean donePressed;
    private EditTextBoldCursor editText;
    /* access modifiers changed from: private */
    public ImageView eraseImageView;
    /* access modifiers changed from: private */
    public int headerRow;
    /* access modifiers changed from: private */
    public boolean ignoreTextChanges;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public int infoRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public LinearLayout nameContainer;
    /* access modifiers changed from: private */
    public int nameRow;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    private Runnable queryRunnable;
    private int reqId;
    /* access modifiers changed from: private */
    public int rowCount;
    private boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public int selectedStickerRow;
    /* access modifiers changed from: private */
    public TLRPC.TL_messages_stickerSet selectedStickerSet;
    /* access modifiers changed from: private */
    public int stickersEndRow;
    /* access modifiers changed from: private */
    public int stickersShadowRow;
    /* access modifiers changed from: private */
    public int stickersStartRow;
    /* access modifiers changed from: private */
    public EditTextBoldCursor usernameTextView;

    public GroupStickersActivity(long id) {
        this.chatId = id;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupStickers", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    GroupStickersActivity.this.finishFragment();
                } else if (id == 1 && !GroupStickersActivity.this.donePressed) {
                    boolean unused = GroupStickersActivity.this.donePressed = true;
                    if (GroupStickersActivity.this.searching) {
                        GroupStickersActivity.this.showEditDoneProgress(true);
                    } else {
                        GroupStickersActivity.this.saveStickerSet();
                    }
                }
            }
        });
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        ContextProgressView contextProgressView = new ContextProgressView(context2, 1);
        this.progressView = contextProgressView;
        contextProgressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        AnonymousClass2 r4 = new LinearLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (GroupStickersActivity.this.selectedStickerSet != null) {
                    canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
                }
            }
        };
        this.nameContainer = r4;
        r4.setWeightSum(1.0f);
        this.nameContainer.setWillNotDraw(false);
        this.nameContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.nameContainer.setOrientation(0);
        this.nameContainer.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(14.0f), 0);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.editText = editTextBoldCursor;
        editTextBoldCursor.setText(MessagesController.getInstance(this.currentAccount).linkPrefix + "/addstickers/");
        this.editText.setTextSize(1, 17.0f);
        this.editText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setFocusable(false);
        this.editText.setBackgroundDrawable((Drawable) null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setGravity(16);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        this.nameContainer.addView(this.editText, LayoutHelper.createLinear(-2, 42));
        EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context2);
        this.usernameTextView = editTextBoldCursor2;
        editTextBoldCursor2.setTextSize(1, 17.0f);
        this.usernameTextView.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.usernameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.usernameTextView.setCursorWidth(1.5f);
        this.usernameTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.usernameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.usernameTextView.setMaxLines(1);
        this.usernameTextView.setLines(1);
        this.usernameTextView.setBackgroundDrawable((Drawable) null);
        this.usernameTextView.setPadding(0, 0, 0, 0);
        this.usernameTextView.setSingleLine(true);
        this.usernameTextView.setGravity(16);
        this.usernameTextView.setInputType(163872);
        this.usernameTextView.setImeOptions(6);
        this.usernameTextView.setHint(LocaleController.getString("ChooseStickerSetPlaceholder", NUM));
        this.usernameTextView.addTextChangedListener(new TextWatcher() {
            boolean ignoreTextChange;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (GroupStickersActivity.this.eraseImageView != null) {
                    GroupStickersActivity.this.eraseImageView.setVisibility(s.length() > 0 ? 0 : 4);
                }
                if (!this.ignoreTextChange && !GroupStickersActivity.this.ignoreTextChanges) {
                    if (s.length() > 5) {
                        this.ignoreTextChange = true;
                        try {
                            Uri uri = Uri.parse(s.toString());
                            if (uri != null) {
                                List<String> segments = uri.getPathSegments();
                                if (segments.size() == 2 && segments.get(0).toLowerCase().equals("addstickers")) {
                                    GroupStickersActivity.this.usernameTextView.setText(segments.get(1));
                                    GroupStickersActivity.this.usernameTextView.setSelection(GroupStickersActivity.this.usernameTextView.length());
                                }
                            }
                        } catch (Exception e) {
                        }
                        this.ignoreTextChange = false;
                    }
                    GroupStickersActivity.this.resolveStickerSet();
                }
            }
        });
        this.nameContainer.addView(this.usernameTextView, LayoutHelper.createLinear(0, 42, 1.0f));
        ImageView imageView = new ImageView(context2);
        this.eraseImageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.eraseImageView.setImageResource(NUM);
        this.eraseImageView.setPadding(AndroidUtilities.dp(16.0f), 0, 0, 0);
        this.eraseImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText3"), PorterDuff.Mode.MULTIPLY));
        this.eraseImageView.setVisibility(4);
        this.eraseImageView.setOnClickListener(new GroupStickersActivity$$ExternalSyntheticLambda0(this));
        this.nameContainer.addView(this.eraseImageView, LayoutHelper.createLinear(42, 42, 0.0f));
        TLRPC.ChatFull chatFull = this.info;
        if (!(chatFull == null || chatFull.stickerset == null)) {
            this.ignoreTextChanges = true;
            this.usernameTextView.setText(this.info.stickerset.short_name);
            EditTextBoldCursor editTextBoldCursor3 = this.usernameTextView;
            editTextBoldCursor3.setSelection(editTextBoldCursor3.length());
            this.ignoreTextChanges = false;
        }
        this.listAdapter = new ListAdapter(context2);
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setFocusable(true);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        AnonymousClass4 r5 = new LinearLayoutManager(context2) {
            public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
                return false;
            }

            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r5;
        r5.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new GroupStickersActivity$$ExternalSyntheticLambda7(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(GroupStickersActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-GroupStickersActivity  reason: not valid java name */
    public /* synthetic */ void m3037lambda$createView$0$orgtelegramuiGroupStickersActivity(View v) {
        this.searchWas = false;
        this.selectedStickerSet = null;
        this.usernameTextView.setText("");
        updateRows();
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-GroupStickersActivity  reason: not valid java name */
    public /* synthetic */ void m3038lambda$createView$1$orgtelegramuiGroupStickersActivity(View view, int position) {
        if (getParentActivity() != null) {
            int i = this.selectedStickerRow;
            if (position == i) {
                if (this.selectedStickerSet != null) {
                    showDialog(new StickersAlert((Context) getParentActivity(), (BaseFragment) this, (TLRPC.InputStickerSet) null, this.selectedStickerSet, (StickersAlert.StickersAlertDelegate) null));
                }
            } else if (position >= this.stickersStartRow && position < this.stickersEndRow) {
                boolean needScroll = i == -1;
                int row = this.layoutManager.findFirstVisibleItemPosition();
                int top = Integer.MAX_VALUE;
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(row);
                if (holder != null) {
                    top = holder.itemView.getTop();
                }
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSets(0).get(position - this.stickersStartRow);
                this.selectedStickerSet = tL_messages_stickerSet;
                this.ignoreTextChanges = true;
                this.usernameTextView.setText(tL_messages_stickerSet.set.short_name);
                EditTextBoldCursor editTextBoldCursor = this.usernameTextView;
                editTextBoldCursor.setSelection(editTextBoldCursor.length());
                this.ignoreTextChanges = false;
                AndroidUtilities.hideKeyboard(this.usernameTextView);
                updateRows();
                if (needScroll && top != Integer.MAX_VALUE) {
                    this.layoutManager.scrollToPositionWithOffset(row + 1, top);
                }
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoad) {
            if (args[0].intValue() == 0) {
                updateRows();
            }
        } else if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null && chatFull.stickerset != null) {
                    this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(chatFull.stickerset);
                }
                this.info = chatFull;
                updateRows();
            }
        } else if (id == NotificationCenter.groupStickersDidLoad) {
            long longValue = args[0].longValue();
            TLRPC.ChatFull chatFull2 = this.info;
            if (chatFull2 != null && chatFull2.stickerset != null && this.info.stickerset.id == ((long) id)) {
                updateRows();
            }
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null && chatFull.stickerset != null) {
            this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
        }
    }

    /* access modifiers changed from: private */
    public void resolveStickerSet() {
        if (this.listAdapter != null) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            Runnable runnable = this.queryRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.queryRunnable = null;
            }
            this.selectedStickerSet = null;
            if (this.usernameTextView.length() <= 0) {
                this.searching = false;
                this.searchWas = false;
                if (this.selectedStickerRow != -1) {
                    updateRows();
                    return;
                }
                return;
            }
            this.searching = true;
            this.searchWas = true;
            String query = this.usernameTextView.getText().toString();
            TLRPC.TL_messages_stickerSet existingSet = MediaDataController.getInstance(this.currentAccount).getStickerSetByName(query);
            if (existingSet != null) {
                this.selectedStickerSet = existingSet;
            }
            int i = this.selectedStickerRow;
            if (i == -1) {
                updateRows();
            } else {
                this.listAdapter.notifyItemChanged(i);
            }
            if (existingSet != null) {
                this.searching = false;
                return;
            }
            GroupStickersActivity$$ExternalSyntheticLambda2 groupStickersActivity$$ExternalSyntheticLambda2 = new GroupStickersActivity$$ExternalSyntheticLambda2(this, query);
            this.queryRunnable = groupStickersActivity$$ExternalSyntheticLambda2;
            AndroidUtilities.runOnUIThread(groupStickersActivity$$ExternalSyntheticLambda2, 500);
        }
    }

    /* renamed from: lambda$resolveStickerSet$4$org-telegram-ui-GroupStickersActivity  reason: not valid java name */
    public /* synthetic */ void m3042lambda$resolveStickerSet$4$orgtelegramuiGroupStickersActivity(String query) {
        if (this.queryRunnable != null) {
            TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
            req.stickerset = new TLRPC.TL_inputStickerSetShortName();
            req.stickerset.short_name = query;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new GroupStickersActivity$$ExternalSyntheticLambda5(this));
        }
    }

    /* renamed from: lambda$resolveStickerSet$3$org-telegram-ui-GroupStickersActivity  reason: not valid java name */
    public /* synthetic */ void m3041lambda$resolveStickerSet$3$orgtelegramuiGroupStickersActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new GroupStickersActivity$$ExternalSyntheticLambda3(this, response));
    }

    /* renamed from: lambda$resolveStickerSet$2$org-telegram-ui-GroupStickersActivity  reason: not valid java name */
    public /* synthetic */ void m3040lambda$resolveStickerSet$2$orgtelegramuiGroupStickersActivity(TLObject response) {
        this.searching = false;
        if (response instanceof TLRPC.TL_messages_stickerSet) {
            this.selectedStickerSet = (TLRPC.TL_messages_stickerSet) response;
            if (this.donePressed) {
                saveStickerSet();
            } else {
                int i = this.selectedStickerRow;
                if (i != -1) {
                    this.listAdapter.notifyItemChanged(i);
                } else {
                    updateRows();
                }
            }
        } else {
            int i2 = this.selectedStickerRow;
            if (i2 != -1) {
                this.listAdapter.notifyItemChanged(i2);
            }
            if (this.donePressed) {
                this.donePressed = false;
                showEditDoneProgress(false);
                if (getParentActivity() != null) {
                    Toast.makeText(getParentActivity(), LocaleController.getString("AddStickersNotFound", NUM), 0).show();
                }
            }
        }
        this.reqId = 0;
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new GroupStickersActivity$$ExternalSyntheticLambda1(this), 100);
        }
    }

    /* renamed from: lambda$onTransitionAnimationEnd$5$org-telegram-ui-GroupStickersActivity  reason: not valid java name */
    public /* synthetic */ void m3039xa3CLASSNAMEa83() {
        EditTextBoldCursor editTextBoldCursor = this.usernameTextView;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.usernameTextView);
        }
    }

    /* access modifiers changed from: private */
    public void saveStickerSet() {
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null || (!(chatFull.stickerset == null || (tL_messages_stickerSet = this.selectedStickerSet) == null || tL_messages_stickerSet.set.id != this.info.stickerset.id) || (this.info.stickerset == null && this.selectedStickerSet == null))) {
            finishFragment();
            return;
        }
        showEditDoneProgress(true);
        TLRPC.TL_channels_setStickers req = new TLRPC.TL_channels_setStickers();
        req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
        if (this.selectedStickerSet == null) {
            req.stickerset = new TLRPC.TL_inputStickerSetEmpty();
        } else {
            SharedPreferences.Editor edit = MessagesController.getEmojiSettings(this.currentAccount).edit();
            edit.remove("group_hide_stickers_" + this.info.id).commit();
            req.stickerset = new TLRPC.TL_inputStickerSetID();
            req.stickerset.id = this.selectedStickerSet.set.id;
            req.stickerset.access_hash = this.selectedStickerSet.set.access_hash;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new GroupStickersActivity$$ExternalSyntheticLambda6(this));
    }

    /* renamed from: lambda$saveStickerSet$7$org-telegram-ui-GroupStickersActivity  reason: not valid java name */
    public /* synthetic */ void m3044lambda$saveStickerSet$7$orgtelegramuiGroupStickersActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new GroupStickersActivity$$ExternalSyntheticLambda4(this, error));
    }

    /* renamed from: lambda$saveStickerSet$6$org-telegram-ui-GroupStickersActivity  reason: not valid java name */
    public /* synthetic */ void m3043lambda$saveStickerSet$6$orgtelegramuiGroupStickersActivity(TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.selectedStickerSet;
            if (tL_messages_stickerSet == null) {
                this.info.stickerset = null;
            } else {
                this.info.stickerset = tL_messages_stickerSet.set;
                MediaDataController.getInstance(this.currentAccount).putGroupStickerSet(this.selectedStickerSet);
            }
            if (this.info.stickerset == null) {
                this.info.flags |= 256;
            } else {
                this.info.flags &= -257;
            }
            MessagesStorage.getInstance(this.currentAccount).updateChatInfo(this.info, false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, true, false);
            finishFragment();
            return;
        }
        Toast.makeText(getParentActivity(), LocaleController.getString("ErrorOccurred", NUM) + "\n" + error.text, 0).show();
        this.donePressed = false;
        showEditDoneProgress(false);
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.nameRow = 0;
        if (this.selectedStickerSet != null || this.searchWas) {
            this.rowCount = i + 1;
            this.selectedStickerRow = i;
        } else {
            this.selectedStickerRow = -1;
        }
        int i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.infoRow = i2;
        ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(0);
        if (!stickerSets.isEmpty()) {
            int i3 = this.rowCount;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.headerRow = i3;
            this.stickersStartRow = i4;
            this.stickersEndRow = i4 + stickerSets.size();
            int size = this.rowCount + stickerSets.size();
            this.rowCount = size;
            this.rowCount = size + 1;
            this.stickersShadowRow = size;
        } else {
            this.headerRow = -1;
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
        }
        LinearLayout linearLayout = this.nameContainer;
        if (linearLayout != null) {
            linearLayout.invalidate();
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.usernameTextView.requestFocus();
            AndroidUtilities.showKeyboard(this.usernameTextView);
        }
    }

    /* access modifiers changed from: private */
    public void showEditDoneProgress(boolean show) {
        final boolean z = show;
        if (this.doneItem != null) {
            AnimatorSet animatorSet = this.doneItemAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.doneItemAnimation = new AnimatorSet();
            if (z) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f})});
            } else {
                this.doneItem.getContentView().setVisibility(0);
                this.doneItem.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "alpha", new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (GroupStickersActivity.this.doneItemAnimation != null && GroupStickersActivity.this.doneItemAnimation.equals(animation)) {
                        if (!z) {
                            GroupStickersActivity.this.progressView.setVisibility(4);
                        } else {
                            GroupStickersActivity.this.doneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (GroupStickersActivity.this.doneItemAnimation != null && GroupStickersActivity.this.doneItemAnimation.equals(animation)) {
                        AnimatorSet unused = GroupStickersActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return GroupStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long id;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    ArrayList<TLRPC.TL_messages_stickerSet> arrayList = MediaDataController.getInstance(GroupStickersActivity.this.currentAccount).getStickerSets(0);
                    int row = position - GroupStickersActivity.this.stickersStartRow;
                    StickerSetCell cell = (StickerSetCell) holder.itemView;
                    TLRPC.TL_messages_stickerSet set = arrayList.get(row);
                    cell.setStickersSet(arrayList.get(row), row != arrayList.size() - 1);
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        id = GroupStickersActivity.this.selectedStickerSet.set.id;
                    } else if (GroupStickersActivity.this.info == null || GroupStickersActivity.this.info.stickerset == null) {
                        id = 0;
                    } else {
                        id = GroupStickersActivity.this.info.stickerset.id;
                    }
                    if (set.set.id == id) {
                        z = true;
                    }
                    cell.setChecked(z);
                    return;
                case 1:
                    if (position == GroupStickersActivity.this.infoRow) {
                        String text = LocaleController.getString("ChooseStickerSetMy", NUM);
                        int index = text.indexOf("@stickers");
                        if (index != -1) {
                            try {
                                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
                                stringBuilder.setSpan(new URLSpanNoUnderline("@stickers") {
                                    public void onClick(View widget) {
                                        MessagesController.getInstance(GroupStickersActivity.this.currentAccount).openByUserName("stickers", GroupStickersActivity.this, 1);
                                    }
                                }, index, "@stickers".length() + index, 18);
                                ((TextInfoPrivacyCell) holder.itemView).setText(stringBuilder);
                                return;
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                                ((TextInfoPrivacyCell) holder.itemView).setText(text);
                                return;
                            }
                        } else {
                            ((TextInfoPrivacyCell) holder.itemView).setText(text);
                            return;
                        }
                    } else {
                        return;
                    }
                case 4:
                    ((HeaderCell) holder.itemView).setText(LocaleController.getString("ChooseFromYourStickers", NUM));
                    return;
                case 5:
                    StickerSetCell cell2 = (StickerSetCell) holder.itemView;
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        cell2.setStickersSet(GroupStickersActivity.this.selectedStickerSet, false);
                        return;
                    } else if (GroupStickersActivity.this.searching) {
                        cell2.setText(LocaleController.getString("Loading", NUM), (String) null, 0, false);
                        return;
                    } else {
                        cell2.setText(LocaleController.getString("ChooseStickerSetNotFound", NUM), LocaleController.getString("ChooseStickerSetNotFoundInfo", NUM), NUM, false);
                        return;
                    }
                default:
                    return;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 2 || type == 5;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                case 5:
                    View view2 = new StickerSetCell(this.mContext, viewType == 0 ? 3 : 2);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view2;
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
                case 2:
                    view = GroupStickersActivity.this.nameContainer;
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
                default:
                    View view3 = new HeaderCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view3;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int i) {
            if (i >= GroupStickersActivity.this.stickersStartRow && i < GroupStickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (i == GroupStickersActivity.this.infoRow) {
                return 1;
            }
            if (i == GroupStickersActivity.this.nameRow) {
                return 2;
            }
            if (i == GroupStickersActivity.this.stickersShadowRow) {
                return 3;
            }
            if (i == GroupStickersActivity.this.headerRow) {
                return 4;
            }
            if (i == GroupStickersActivity.this.selectedStickerRow) {
                return 5;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.nameContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menuSelector"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"));
        return themeDescriptions;
    }
}

package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.InviteTextCell;
import org.telegram.ui.Cells.InviteUserCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class InviteContactsActivity extends BaseFragment implements OnClickListener, NotificationCenterDelegate {
    private InviteAdapter adapter;
    private ArrayList<GroupCreateSpan> allSpans = new ArrayList();
    private int containerHeight;
    private TextView counterTextView;
    private FrameLayout counterView;
    private GroupCreateSpan currentDeletingSpan;
    private GroupCreateDividerItemDecoration decoration;
    private EditTextBoldCursor editText;
    private EmptyTextProgressView emptyView;
    private int fieldY;
    private boolean ignoreScrollEvent;
    private TextView infoTextView;
    private RecyclerListView listView;
    private ArrayList<Contact> phoneBookContacts;
    private ScrollView scrollView;
    private boolean searchWas;
    private boolean searching;
    private HashMap<String, GroupCreateSpan> selectedContacts = new HashMap();
    private SpansContainer spansContainer;
    private TextView textView;

    /* renamed from: org.telegram.ui.InviteContactsActivity$5 */
    class C14365 implements Callback {
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        C14365() {
        }
    }

    /* renamed from: org.telegram.ui.InviteContactsActivity$6 */
    class C14376 implements OnKeyListener {
        private boolean wasEmpty;

        C14376() {
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            boolean z = true;
            if (keyEvent.getAction() == null) {
                if (InviteContactsActivity.this.editText.length() != null) {
                    z = false;
                }
                this.wasEmpty = z;
            } else if (keyEvent.getAction() == 1 && this.wasEmpty != null && InviteContactsActivity.this.allSpans.isEmpty() == null) {
                InviteContactsActivity.this.spansContainer.removeSpan((GroupCreateSpan) InviteContactsActivity.this.allSpans.get(InviteContactsActivity.this.allSpans.size() - 1));
                InviteContactsActivity.this.updateHint();
                InviteContactsActivity.this.checkVisibleRows();
                return true;
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.InviteContactsActivity$7 */
    class C14387 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C14387() {
        }

        public void afterTextChanged(Editable editable) {
            if (InviteContactsActivity.this.editText.length() != null) {
                InviteContactsActivity.this.searching = true;
                InviteContactsActivity.this.searchWas = true;
                InviteContactsActivity.this.adapter.setSearching(true);
                InviteContactsActivity.this.adapter.searchDialogs(InviteContactsActivity.this.editText.getText().toString());
                InviteContactsActivity.this.listView.setFastScrollVisible(false);
                InviteContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                InviteContactsActivity.this.emptyView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
                return;
            }
            InviteContactsActivity.this.closeSearch();
        }
    }

    private class SpansContainer extends ViewGroup {
        private View addingSpan;
        private boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList();
        private AnimatorSet currentAnimation;
        private View removingSpan;

        /* renamed from: org.telegram.ui.InviteContactsActivity$SpansContainer$1 */
        class C14431 extends AnimatorListenerAdapter {
            C14431() {
            }

            public void onAnimationEnd(Animator animator) {
                SpansContainer.this.addingSpan = null;
                SpansContainer.this.currentAnimation = null;
                SpansContainer.this.animationStarted = false;
                InviteContactsActivity.this.editText.setAllowDrawCursor(true);
            }
        }

        public SpansContainer(Context context) {
            super(context);
        }

        protected void onMeasure(int i, int i2) {
            SpansContainer spansContainer = this;
            int childCount = getChildCount();
            int size = MeasureSpec.getSize(i);
            int dp = size - AndroidUtilities.dp(32.0f);
            float f = 12.0f;
            int dp2 = AndroidUtilities.dp(12.0f);
            int dp3 = AndroidUtilities.dp(12.0f);
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            while (i3 < childCount) {
                View childAt = getChildAt(i3);
                if (childAt instanceof GroupCreateSpan) {
                    childAt.measure(MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                    if (childAt != spansContainer.removingSpan && childAt.getMeasuredWidth() + i4 > dp) {
                        dp2 += childAt.getMeasuredHeight() + AndroidUtilities.dp(f);
                        i4 = 0;
                    }
                    if (childAt.getMeasuredWidth() + i5 > dp) {
                        dp3 += childAt.getMeasuredHeight() + AndroidUtilities.dp(f);
                        i5 = 0;
                    }
                    int dp4 = AndroidUtilities.dp(16.0f) + i4;
                    if (!spansContainer.animationStarted) {
                        if (childAt == spansContainer.removingSpan) {
                            childAt.setTranslationX((float) (AndroidUtilities.dp(16.0f) + i5));
                            childAt.setTranslationY((float) dp3);
                        } else if (spansContainer.removingSpan != null) {
                            if (childAt.getTranslationX() != ((float) dp4)) {
                                spansContainer.animators.add(ObjectAnimator.ofFloat(childAt, "translationX", new float[]{r8}));
                            }
                            if (childAt.getTranslationY() != ((float) dp2)) {
                                spansContainer.animators.add(ObjectAnimator.ofFloat(childAt, "translationY", new float[]{r8}));
                            }
                        } else {
                            childAt.setTranslationX((float) dp4);
                            childAt.setTranslationY((float) dp2);
                        }
                    }
                    if (childAt != spansContainer.removingSpan) {
                        i4 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    i5 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
                i3++;
                f = 12.0f;
            }
            if (AndroidUtilities.isTablet()) {
                childCount = AndroidUtilities.dp(366.0f) / 3;
            } else {
                childCount = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(164.0f)) / 3;
            }
            if (dp - i4 < childCount) {
                dp2 += AndroidUtilities.dp(44.0f);
                i4 = 0;
            }
            if (dp - i5 < childCount) {
                dp3 += AndroidUtilities.dp(44.0f);
            }
            InviteContactsActivity.this.editText.measure(MeasureSpec.makeMeasureSpec(dp - i4, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
            if (!spansContainer.animationStarted) {
                dp3 += AndroidUtilities.dp(44.0f);
                i4 += AndroidUtilities.dp(16.0f);
                InviteContactsActivity.this.fieldY = dp2;
                if (spansContainer.currentAnimation != null) {
                    boolean z;
                    if (InviteContactsActivity.this.containerHeight != dp2 + AndroidUtilities.dp(44.0f)) {
                        spansContainer.animators.add(ObjectAnimator.ofInt(InviteContactsActivity.this, "containerHeight", new int[]{dp2}));
                    }
                    if (InviteContactsActivity.this.editText.getTranslationX() != ((float) i4)) {
                        spansContainer.animators.add(ObjectAnimator.ofFloat(InviteContactsActivity.this.editText, "translationX", new float[]{r3}));
                    }
                    if (InviteContactsActivity.this.editText.getTranslationY() != ((float) InviteContactsActivity.this.fieldY)) {
                        ArrayList arrayList = spansContainer.animators;
                        float[] fArr = new float[1];
                        z = false;
                        fArr[0] = (float) InviteContactsActivity.this.fieldY;
                        arrayList.add(ObjectAnimator.ofFloat(InviteContactsActivity.this.editText, "translationY", fArr));
                    } else {
                        z = false;
                    }
                    InviteContactsActivity.this.editText.setAllowDrawCursor(z);
                    spansContainer.currentAnimation.playTogether(spansContainer.animators);
                    spansContainer.currentAnimation.start();
                    spansContainer.animationStarted = true;
                } else {
                    InviteContactsActivity.this.containerHeight = dp3;
                    InviteContactsActivity.this.editText.setTranslationX((float) i4);
                    InviteContactsActivity.this.editText.setTranslationY((float) InviteContactsActivity.this.fieldY);
                }
            } else if (!(spansContainer.currentAnimation == null || InviteContactsActivity.this.ignoreScrollEvent || spansContainer.removingSpan != null)) {
                InviteContactsActivity.this.editText.bringPointIntoView(InviteContactsActivity.this.editText.getSelectionStart());
            }
            setMeasuredDimension(size, InviteContactsActivity.this.containerHeight);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            z = getChildCount();
            for (boolean z2 = false; z2 < z; z2++) {
                i3 = getChildAt(z2);
                i3.layout(0, 0, i3.getMeasuredWidth(), i3.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan groupCreateSpan) {
            InviteContactsActivity.this.allSpans.add(groupCreateSpan);
            InviteContactsActivity.this.selectedContacts.put(groupCreateSpan.getKey(), groupCreateSpan);
            InviteContactsActivity.this.editText.setHintVisible(false);
            if (this.currentAnimation != null) {
                this.currentAnimation.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new C14431());
            this.currentAnimation.setDuration(150);
            this.addingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", new float[]{0.0f, 1.0f}));
            addView(groupCreateSpan);
        }

        public void removeSpan(final GroupCreateSpan groupCreateSpan) {
            InviteContactsActivity.this.ignoreScrollEvent = true;
            InviteContactsActivity.this.selectedContacts.remove(groupCreateSpan.getKey());
            InviteContactsActivity.this.allSpans.remove(groupCreateSpan);
            groupCreateSpan.setOnClickListener(null);
            if (this.currentAnimation != null) {
                this.currentAnimation.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(groupCreateSpan);
                    SpansContainer.this.removingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    InviteContactsActivity.this.editText.setAllowDrawCursor(true);
                    if (InviteContactsActivity.this.allSpans.isEmpty() != null) {
                        InviteContactsActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            this.currentAnimation.setDuration(150);
            this.removingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    /* renamed from: org.telegram.ui.InviteContactsActivity$1 */
    class C21571 extends ActionBarMenuOnItemClick {
        C21571() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                InviteContactsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.InviteContactsActivity$8 */
    class C21598 implements OnItemClickListener {
        C21598() {
        }

        public void onItemClick(View view, int i) {
            if (i == 0 && InviteContactsActivity.this.searching == 0) {
                try {
                    view = new Intent("android.intent.action.SEND");
                    view.setType("text/plain");
                    i = ContactsController.getInstance(InviteContactsActivity.this.currentAccount).getInviteText(0);
                    view.putExtra("android.intent.extra.TEXT", i);
                    InviteContactsActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(view, i), 500);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            } else if ((view instanceof InviteUserCell) != 0) {
                InviteUserCell inviteUserCell = (InviteUserCell) view;
                Contact contact = inviteUserCell.getContact();
                if (contact != 0) {
                    boolean containsKey = InviteContactsActivity.this.selectedContacts.containsKey(contact.key);
                    if (containsKey) {
                        InviteContactsActivity.this.spansContainer.removeSpan((GroupCreateSpan) InviteContactsActivity.this.selectedContacts.get(contact.key));
                    } else {
                        GroupCreateSpan groupCreateSpan = new GroupCreateSpan(InviteContactsActivity.this.editText.getContext(), contact);
                        InviteContactsActivity.this.spansContainer.addSpan(groupCreateSpan);
                        groupCreateSpan.setOnClickListener(InviteContactsActivity.this);
                    }
                    InviteContactsActivity.this.updateHint();
                    if (InviteContactsActivity.this.searching == 0) {
                        if (InviteContactsActivity.this.searchWas == 0) {
                            inviteUserCell.setChecked(containsKey ^ true, true);
                            if (InviteContactsActivity.this.editText.length() > null) {
                                InviteContactsActivity.this.editText.setText(0);
                            }
                        }
                    }
                    AndroidUtilities.showKeyboard(InviteContactsActivity.this.editText);
                    if (InviteContactsActivity.this.editText.length() > null) {
                        InviteContactsActivity.this.editText.setText(0);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.InviteContactsActivity$9 */
    class C21609 extends OnScrollListener {
        C21609() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1) {
                AndroidUtilities.hideKeyboard(InviteContactsActivity.this.editText);
            }
        }
    }

    public class InviteAdapter extends SelectionAdapter {
        private Context context;
        private ArrayList<Contact> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;
        private boolean searching;

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public InviteAdapter(Context context) {
            this.context = context;
        }

        public void setSearching(boolean z) {
            if (this.searching != z) {
                this.searching = z;
                notifyDataSetChanged();
            }
        }

        public int getItemCount() {
            if (this.searching) {
                return this.searchResult.size();
            }
            return InviteContactsActivity.this.phoneBookContacts.size() + 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 1) {
                i = new InviteUserCell(this.context, true);
            } else {
                i = new InviteTextCell(this.context);
                ((InviteTextCell) i).setTextAndIcon(LocaleController.getString("ShareTelegram", C0446R.string.ShareTelegram), C0446R.drawable.share);
            }
            return new Holder(i);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                Contact contact;
                InviteUserCell inviteUserCell = (InviteUserCell) viewHolder.itemView;
                if (this.searching) {
                    contact = (Contact) this.searchResult.get(i);
                    i = (CharSequence) this.searchResultNames.get(i);
                } else {
                    contact = (Contact) InviteContactsActivity.this.phoneBookContacts.get(i - 1);
                    i = 0;
                }
                inviteUserCell.setUser(contact, i);
                inviteUserCell.setChecked(InviteContactsActivity.this.selectedContacts.containsKey(contact.key), false);
            }
        }

        public int getItemViewType(int i) {
            return (this.searching || i != 0) ? 0 : 1;
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof InviteUserCell) {
                ((InviteUserCell) viewHolder.itemView).recycle();
            }
        }

        public void searchDialogs(final String str) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {

                /* renamed from: org.telegram.ui.InviteContactsActivity$InviteAdapter$1$1 */
                class C14401 implements Runnable {

                    /* renamed from: org.telegram.ui.InviteContactsActivity$InviteAdapter$1$1$1 */
                    class C14391 implements Runnable {
                        C14391() {
                        }

                        /* JADX WARNING: inconsistent code. */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public void run() {
                            String toLowerCase = str.trim().toLowerCase();
                            if (toLowerCase.length() == 0) {
                                InviteAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                                return;
                            }
                            String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
                            if (toLowerCase.equals(translitString) || translitString.length() == 0) {
                                translitString = null;
                            }
                            int i = 0;
                            String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
                            strArr[0] = toLowerCase;
                            if (translitString != null) {
                                strArr[1] = translitString;
                            }
                            ArrayList arrayList = new ArrayList();
                            ArrayList arrayList2 = new ArrayList();
                            int i2 = 0;
                            while (i2 < InviteContactsActivity.this.phoneBookContacts.size()) {
                                Contact contact = (Contact) InviteContactsActivity.this.phoneBookContacts.get(i2);
                                String toLowerCase2 = ContactsController.formatName(contact.first_name, contact.last_name).toLowerCase();
                                String translitString2 = LocaleController.getInstance().getTranslitString(toLowerCase2);
                                if (toLowerCase2.equals(translitString2)) {
                                    translitString2 = null;
                                }
                                int length = strArr.length;
                                int i3 = i;
                                int i4 = i3;
                                while (i3 < length) {
                                    String str = strArr[i3];
                                    if (!toLowerCase2.startsWith(str)) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append(" ");
                                        stringBuilder.append(str);
                                        if (!toLowerCase2.contains(stringBuilder.toString())) {
                                            if (translitString2 != null) {
                                                if (!translitString2.startsWith(str)) {
                                                    StringBuilder stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append(" ");
                                                    stringBuilder2.append(str);
                                                }
                                            }
                                            if (i4 != 0) {
                                                arrayList2.add(AndroidUtilities.generateSearchName(contact.first_name, contact.last_name, str));
                                                arrayList.add(contact);
                                                break;
                                            }
                                            i3++;
                                        }
                                    }
                                    i4 = 1;
                                    if (i4 != 0) {
                                        arrayList2.add(AndroidUtilities.generateSearchName(contact.first_name, contact.last_name, str));
                                        arrayList.add(contact);
                                        break;
                                    }
                                    i3++;
                                }
                                i2++;
                                i = 0;
                            }
                            InviteAdapter.this.updateSearchResults(arrayList, arrayList2);
                        }
                    }

                    C14401() {
                    }

                    public void run() {
                        Utilities.searchQueue.postRunnable(new C14391());
                    }
                }

                public void run() {
                    try {
                        InviteAdapter.this.searchTimer.cancel();
                        InviteAdapter.this.searchTimer = null;
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    AndroidUtilities.runOnUIThread(new C14401());
                }
            }, 200, 300);
        }

        private void updateSearchResults(final ArrayList<Contact> arrayList, final ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    InviteAdapter.this.searchResult = arrayList;
                    InviteAdapter.this.searchResultNames = arrayList2;
                    InviteAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            int itemCount = getItemCount();
            boolean z = false;
            InviteContactsActivity.this.emptyView.setVisibility(itemCount == 1 ? 0 : 4);
            GroupCreateDividerItemDecoration access$3100 = InviteContactsActivity.this.decoration;
            if (itemCount == 1) {
                z = true;
            }
            access$3100.setSingle(z);
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsImported);
        fetchContacts();
        if (!UserConfig.getInstance(this.currentAccount).contactsReimported) {
            ContactsController.getInstance(this.currentAccount).forceImportContacts();
            UserConfig.getInstance(this.currentAccount).contactsReimported = true;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsImported);
    }

    public void onClick(View view) {
        GroupCreateSpan groupCreateSpan = (GroupCreateSpan) view;
        if (groupCreateSpan.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(groupCreateSpan);
            updateHint();
            checkVisibleRows();
            return;
        }
        if (this.currentDeletingSpan != null) {
            this.currentDeletingSpan.cancelDeleteAnimation();
        }
        this.currentDeletingSpan = groupCreateSpan;
        groupCreateSpan.startDeleteAnimation();
    }

    public View createView(Context context) {
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteFriends", C0446R.string.InviteFriends));
        this.actionBar.setActionBarMenuOnItemClick(new C21571());
        this.fragmentView = new ViewGroup(context2) {
            protected void onMeasure(int i, int i2) {
                int dp;
                int measuredHeight;
                i = MeasureSpec.getSize(i);
                i2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(i, i2);
                if (!AndroidUtilities.isTablet()) {
                    if (i2 <= i) {
                        dp = AndroidUtilities.dp(56.0f);
                        InviteContactsActivity.this.infoTextView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                        InviteContactsActivity.this.counterView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
                        if (InviteContactsActivity.this.infoTextView.getVisibility() != 0) {
                            measuredHeight = InviteContactsActivity.this.infoTextView.getMeasuredHeight();
                        } else {
                            measuredHeight = InviteContactsActivity.this.counterView.getMeasuredHeight();
                        }
                        InviteContactsActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                        InviteContactsActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec((i2 - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - measuredHeight, NUM));
                        InviteContactsActivity.this.emptyView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec((i2 - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - AndroidUtilities.dp(72.0f), NUM));
                    }
                }
                dp = AndroidUtilities.dp(144.0f);
                InviteContactsActivity.this.infoTextView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                InviteContactsActivity.this.counterView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
                if (InviteContactsActivity.this.infoTextView.getVisibility() != 0) {
                    measuredHeight = InviteContactsActivity.this.counterView.getMeasuredHeight();
                } else {
                    measuredHeight = InviteContactsActivity.this.infoTextView.getMeasuredHeight();
                }
                InviteContactsActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                InviteContactsActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec((i2 - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - measuredHeight, NUM));
                InviteContactsActivity.this.emptyView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec((i2 - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - AndroidUtilities.dp(72.0f), NUM));
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                InviteContactsActivity.this.scrollView.layout(0, 0, InviteContactsActivity.this.scrollView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight());
                InviteContactsActivity.this.listView.layout(0, InviteContactsActivity.this.scrollView.getMeasuredHeight(), InviteContactsActivity.this.listView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight() + InviteContactsActivity.this.listView.getMeasuredHeight());
                InviteContactsActivity.this.emptyView.layout(0, InviteContactsActivity.this.scrollView.getMeasuredHeight() + AndroidUtilities.dp(NUM), InviteContactsActivity.this.emptyView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight() + InviteContactsActivity.this.emptyView.getMeasuredHeight());
                i4 -= i2;
                z = i4 - InviteContactsActivity.this.infoTextView.getMeasuredHeight();
                InviteContactsActivity.this.infoTextView.layout(0, z, InviteContactsActivity.this.infoTextView.getMeasuredWidth(), InviteContactsActivity.this.infoTextView.getMeasuredHeight() + z);
                i4 -= InviteContactsActivity.this.counterView.getMeasuredHeight();
                InviteContactsActivity.this.counterView.layout(0, i4, InviteContactsActivity.this.counterView.getMeasuredWidth(), InviteContactsActivity.this.counterView.getMeasuredHeight() + i4);
            }

            protected boolean drawChild(Canvas canvas, View view, long j) {
                j = super.drawChild(canvas, view, j);
                if (view == InviteContactsActivity.this.listView || view == InviteContactsActivity.this.emptyView) {
                    InviteContactsActivity.this.parentLayout.drawHeaderShadow(canvas, InviteContactsActivity.this.scrollView.getMeasuredHeight());
                }
                return j;
            }
        };
        ViewGroup viewGroup = (ViewGroup) this.fragmentView;
        this.scrollView = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                if (InviteContactsActivity.this.ignoreScrollEvent) {
                    InviteContactsActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
                rect.top += InviteContactsActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rect.bottom += InviteContactsActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(view, rect, z);
            }
        };
        this.scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_windowBackgroundWhite));
        viewGroup.addView(this.scrollView);
        this.spansContainer = new SpansContainer(context2);
        this.scrollView.addView(this.spansContainer, LayoutHelper.createFrame(-1, -2.0f));
        this.editText = new EditTextBoldCursor(context2) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (InviteContactsActivity.this.currentDeletingSpan != null) {
                    InviteContactsActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    InviteContactsActivity.this.currentDeletingSpan = null;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setCursorColor(Theme.getColor(Theme.key_groupcreate_cursor));
        this.editText.setCursorWidth(1.5f);
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setBackgroundDrawable(null);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setImeOptions(268435462);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        r0.spansContainer.addView(r0.editText);
        r0.editText.setHintText(LocaleController.getString("SearchFriends", C0446R.string.SearchFriends));
        r0.editText.setCustomSelectionActionModeCallback(new C14365());
        r0.editText.setOnKeyListener(new C14376());
        r0.editText.addTextChangedListener(new C14387());
        r0.emptyView = new EmptyTextProgressView(context2);
        if (ContactsController.getInstance(r0.currentAccount).isLoadingContacts()) {
            r0.emptyView.showProgress();
        } else {
            r0.emptyView.showTextView();
        }
        r0.emptyView.setText(LocaleController.getString("NoContacts", C0446R.string.NoContacts));
        viewGroup.addView(r0.emptyView);
        LayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        r0.listView = new RecyclerListView(context2);
        r0.listView.setEmptyView(r0.emptyView);
        RecyclerListView recyclerListView = r0.listView;
        Adapter inviteAdapter = new InviteAdapter(context2);
        r0.adapter = inviteAdapter;
        recyclerListView.setAdapter(inviteAdapter);
        r0.listView.setLayoutManager(linearLayoutManager);
        r0.listView.setVerticalScrollBarEnabled(true);
        r0.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView2 = r0.listView;
        ItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        r0.decoration = groupCreateDividerItemDecoration;
        recyclerListView2.addItemDecoration(groupCreateDividerItemDecoration);
        viewGroup.addView(r0.listView);
        r0.listView.setOnItemClickListener(new C21598());
        r0.listView.setOnScrollListener(new C21609());
        r0.infoTextView = new TextView(context2);
        r0.infoTextView.setBackgroundColor(Theme.getColor(Theme.key_contacts_inviteBackground));
        r0.infoTextView.setTextColor(Theme.getColor(Theme.key_contacts_inviteText));
        r0.infoTextView.setGravity(17);
        r0.infoTextView.setText(LocaleController.getString("InviteFriendsHelp", C0446R.string.InviteFriendsHelp));
        r0.infoTextView.setTextSize(1, 13.0f);
        r0.infoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.infoTextView.setPadding(AndroidUtilities.dp(17.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(17.0f), AndroidUtilities.dp(9.0f));
        viewGroup.addView(r0.infoTextView, LayoutHelper.createFrame(-1, -2, 83));
        r0.counterView = new FrameLayout(context2);
        r0.counterView.setBackgroundColor(Theme.getColor(Theme.key_contacts_inviteBackground));
        r0.counterView.setVisibility(4);
        viewGroup.addView(r0.counterView, LayoutHelper.createFrame(-1, 48, 83));
        r0.counterView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    view = new StringBuilder();
                    int i = 0;
                    int i2 = i;
                    while (i < InviteContactsActivity.this.allSpans.size()) {
                        Contact contact = ((GroupCreateSpan) InviteContactsActivity.this.allSpans.get(i)).getContact();
                        if (view.length() != 0) {
                            view.append(';');
                        }
                        view.append((String) contact.phones.get(0));
                        if (i == 0 && InviteContactsActivity.this.allSpans.size() == 1) {
                            i2 = contact.imported;
                        }
                        i++;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("smsto:");
                    stringBuilder.append(view.toString());
                    Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse(stringBuilder.toString()));
                    intent.putExtra("sms_body", ContactsController.getInstance(InviteContactsActivity.this.currentAccount).getInviteText(i2));
                    InviteContactsActivity.this.getParentActivity().startActivityForResult(intent, 500);
                    MediaController.getInstance().startSmsObserver();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                InviteContactsActivity.this.finishFragment();
            }
        });
        View linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        r0.counterView.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 17));
        r0.counterTextView = new TextView(context2);
        r0.counterTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.counterTextView.setTextSize(1, 14.0f);
        r0.counterTextView.setTextColor(Theme.getColor(Theme.key_contacts_inviteBackground));
        r0.counterTextView.setGravity(17);
        r0.counterTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(10.0f), -1));
        r0.counterTextView.setMinWidth(AndroidUtilities.dp(20.0f));
        r0.counterTextView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(1.0f));
        linearLayout.addView(r0.counterTextView, LayoutHelper.createLinear(-2, 20, 16, 0, 0, 10, 0));
        r0.textView = new TextView(context2);
        r0.textView.setTextSize(1, 14.0f);
        r0.textView.setTextColor(Theme.getColor(Theme.key_contacts_inviteText));
        r0.textView.setGravity(17);
        r0.textView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        r0.textView.setText(LocaleController.getString("InviteToTelegram", C0446R.string.InviteToTelegram).toUpperCase());
        r0.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(r0.textView, LayoutHelper.createLinear(-2, -2, 16));
        updateHint();
        r0.adapter.notifyDataSetChanged();
        return r0.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.editText != null) {
            this.editText.requestFocus();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.contactsImported) {
            fetchContacts();
        }
    }

    public void setContainerHeight(int i) {
        this.containerHeight = i;
        if (this.spansContainer != 0) {
            this.spansContainer.requestLayout();
        }
    }

    public int getContainerHeight() {
        return this.containerHeight;
    }

    private void checkVisibleRows() {
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof InviteUserCell) {
                InviteUserCell inviteUserCell = (InviteUserCell) childAt;
                Contact contact = inviteUserCell.getContact();
                if (contact != null) {
                    inviteUserCell.setChecked(this.selectedContacts.containsKey(contact.key), true);
                }
            }
        }
    }

    private void updateHint() {
        if (this.selectedContacts.isEmpty()) {
            this.infoTextView.setVisibility(0);
            this.counterView.setVisibility(4);
            return;
        }
        this.infoTextView.setVisibility(4);
        this.counterView.setVisibility(0);
        this.counterTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.selectedContacts.size())}));
    }

    private void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.adapter.setSearching(false);
        this.adapter.searchDialogs(null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.setText(LocaleController.getString("NoContacts", C0446R.string.NoContacts));
    }

    private void fetchContacts() {
        this.phoneBookContacts = new ArrayList(ContactsController.getInstance(this.currentAccount).phoneBookContacts);
        Collections.sort(this.phoneBookContacts, new Comparator<Contact>() {
            public int compare(Contact contact, Contact contact2) {
                if (contact.imported > contact2.imported) {
                    return -1;
                }
                return contact.imported < contact2.imported ? 1 : null;
            }
        });
        if (this.emptyView != null) {
            this.emptyView.showTextView();
        }
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        AnonymousClass12 anonymousClass12 = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (InviteContactsActivity.this.listView != null) {
                    int childCount = InviteContactsActivity.this.listView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = InviteContactsActivity.this.listView.getChildAt(i);
                        if (childAt instanceof InviteUserCell) {
                            ((InviteUserCell) childAt).update(0);
                        }
                    }
                }
            }
        };
        r10 = new ThemeDescription[44];
        r10[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r10[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r10[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r10[14] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[15] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_groupcreate_hintText);
        r10[16] = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_groupcreate_cursor);
        r10[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, null, null, null, Theme.key_graySection);
        r10[18] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, null, null, null, Theme.key_groupcreate_sectionShadow);
        r10[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        r10[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        r10[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_groupcreate_checkbox);
        r10[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_groupcreate_checkboxCheck);
        r10[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{InviteUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_onlineText);
        r10[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{InviteUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_offlineText);
        r10[25] = new ThemeDescription(this.listView, 0, new Class[]{InviteUserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        AnonymousClass12 anonymousClass122 = anonymousClass12;
        r10[26] = new ThemeDescription(null, 0, null, null, null, anonymousClass122, Theme.key_avatar_backgroundRed);
        r10[27] = new ThemeDescription(null, 0, null, null, null, anonymousClass122, Theme.key_avatar_backgroundOrange);
        r10[28] = new ThemeDescription(null, 0, null, null, null, anonymousClass122, Theme.key_avatar_backgroundViolet);
        r10[29] = new ThemeDescription(null, 0, null, null, null, anonymousClass122, Theme.key_avatar_backgroundGreen);
        r10[30] = new ThemeDescription(null, 0, null, null, null, anonymousClass122, Theme.key_avatar_backgroundCyan);
        r10[31] = new ThemeDescription(null, 0, null, null, null, anonymousClass122, Theme.key_avatar_backgroundBlue);
        r10[32] = new ThemeDescription(null, 0, null, null, null, anonymousClass122, Theme.key_avatar_backgroundPink);
        r10[33] = new ThemeDescription(this.listView, 0, new Class[]{InviteTextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[34] = new ThemeDescription(this.listView, 0, new Class[]{InviteTextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r10[35] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_avatar_backgroundGroupCreateSpanBlue);
        r10[36] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanBackground);
        r10[37] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanText);
        r10[38] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_avatar_backgroundBlue);
        r10[39] = new ThemeDescription(this.infoTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_contacts_inviteText);
        r10[40] = new ThemeDescription(this.infoTextView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_contacts_inviteBackground);
        r10[41] = new ThemeDescription(this.counterView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_contacts_inviteBackground);
        r10[42] = new ThemeDescription(this.counterTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_contacts_inviteBackground);
        r10[43] = new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_contacts_inviteText);
        return r10;
    }
}

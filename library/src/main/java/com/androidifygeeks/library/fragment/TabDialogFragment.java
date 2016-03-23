package com.androidifygeeks.library.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.SpannedString;
import android.text.TextUtils;
import android.view.View;

import com.androidifygeeks.library.core.BaseDialogBuilder;
import com.androidifygeeks.library.core.BaseDialogFragment;
import com.androidifygeeks.library.iface.INegativeButtonDialogListener;
import com.androidifygeeks.library.iface.INeutralButtonDialogListener;
import com.androidifygeeks.library.iface.IPositiveButtonDialogListener;

import java.util.List;

/**
 * Created by b_ashish on 21-Mar-16.
 */
public class TabDialogFragment extends BaseDialogFragment {

    protected final static String ARG_MESSAGE = "message";
    protected final static String ARG_TITLE = "title";
    protected final static String ARG_SUB_TITLE = "sub_title";
    protected final static String ARG_POSITIVE_BUTTON = "positive_button";
    protected final static String ARG_NEGATIVE_BUTTON = "negative_button";
    protected final static String ARG_NEUTRAL_BUTTON = "neutral_button";

    protected final static String ARG_TAB_BUTTON = "tab_button";


    public static TabDialogBuilder createBuilder(Context context, FragmentManager fragmentManager) {
        return new TabDialogBuilder(context, fragmentManager, TabDialogFragment.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Key method for extending {com.avast.android.dialogs.fragment.TabDialogFragment}.
     * Children can extend this to add more things to base builder.
     */
    @Override
    protected BaseDialogFragment.Builder build(BaseDialogFragment.Builder builder) {
        final CharSequence title = getTitle();
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        final CharSequence subTitle = getSubTitle();
        if (!TextUtils.isEmpty(subTitle)) {
            builder.setSubTitle(subTitle);
        }

        final CharSequence message = getMessage();
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }

        final CharSequence positiveButtonText = getPositiveButtonText();
        if (!TextUtils.isEmpty(positiveButtonText)) {
            builder.setPositiveButton(positiveButtonText, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (IPositiveButtonDialogListener listener : getPositiveButtonDialogListeners()) {
                        listener.onPositiveButtonClicked(mRequestCode);
                    }
                    dismiss();
                }
            });
        }

        final CharSequence negativeButtonText = getNegativeButtonText();
        if (!TextUtils.isEmpty(negativeButtonText)) {
            builder.setNegativeButton(negativeButtonText, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (INegativeButtonDialogListener listener : getNegativeButtonDialogListeners()) {
                        listener.onNegativeButtonClicked(mRequestCode);
                    }
                    dismiss();
                }
            });
        }

        final CharSequence neutralButtonText = getNeutralButtonText();
        if (!TextUtils.isEmpty(neutralButtonText)) {
            builder.setNeutralButton(neutralButtonText, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (INeutralButtonDialogListener listener : getNeutralButtonDialogListeners()) {
                        listener.onNeutralButtonClicked(mRequestCode);
                    }
                    dismiss();
                }
            });
        }

        final CharSequence[] tabButtonText = getTabButtonText();
        if (tabButtonText != null && tabButtonText.length > 0) {
            buildTab(builder);
        }

        return builder;
    }

    private void buildTab(final Builder builder) {

        builder.setTabItems(
                prepareAdapter(), getTabButtonText());
    }


    private TabViewPagerAdapter prepareAdapter() {

        TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getChildFragmentManager(), getTabButtonText());
        return tabViewPagerAdapter;

    }

    protected CharSequence getMessage() {
        return getArguments().getCharSequence(ARG_MESSAGE);
    }

    protected CharSequence getTitle() {
        return getArguments().getCharSequence(ARG_TITLE);
    }

    protected CharSequence getSubTitle() {
        return getArguments().getCharSequence(ARG_SUB_TITLE);
    }

    protected CharSequence getPositiveButtonText() {
        return getArguments().getCharSequence(ARG_POSITIVE_BUTTON);
    }

    protected CharSequence getNegativeButtonText() {
        return getArguments().getCharSequence(ARG_NEGATIVE_BUTTON);
    }

    protected CharSequence getNeutralButtonText() {
        return getArguments().getCharSequence(ARG_NEUTRAL_BUTTON);
    }

    protected CharSequence[] getTabButtonText() {
        return getArguments().getCharSequenceArray(ARG_TAB_BUTTON);
    }


    /**
     * Get positive button dialog listeners.
     * There might be more than one listener.
     *
     * @return Dialog listeners
     * @since 2.1.0
     */
    protected List<IPositiveButtonDialogListener> getPositiveButtonDialogListeners() {
        return getDialogListeners(IPositiveButtonDialogListener.class);
    }

    /**
     * Get negative button dialog listeners.
     * There might be more than one listener.
     *
     * @return Dialog listeners
     * @since 2.1.0
     */
    protected List<INegativeButtonDialogListener> getNegativeButtonDialogListeners() {
        return getDialogListeners(INegativeButtonDialogListener.class);
    }

    /**
     * Get neutral button dialog listeners.
     * There might be more than one listener.
     *
     * @return Dialog listeners
     * @since 2.1.0
     */
    protected List<INeutralButtonDialogListener> getNeutralButtonDialogListeners() {
        return getDialogListeners(INeutralButtonDialogListener.class);
    }


    public static class TabDialogBuilder extends BaseDialogBuilder {

        private CharSequence mTitle;
        private CharSequence mSubTitle;
        private CharSequence mMessage;
        private CharSequence mPositiveButtonText;
        private CharSequence mNegativeButtonText;
        private CharSequence mNeutralButtonText;

        private CharSequence[] mTabButtonText;


        public TabDialogBuilder(Context context, FragmentManager fragmentManager, Class<? extends BaseDialogFragment> clazz) {
            super(context, fragmentManager, clazz);
        }

        @Override
        protected TabDialogBuilder self() {
            return this;
        }

        public TabDialogBuilder setTitle(int titleResourceId) {
            mTitle = mContext.getString(titleResourceId);
            return this;
        }


        public TabDialogBuilder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public TabDialogBuilder setSubTitle(int subTitleResourceId) {
            mSubTitle = mContext.getString(subTitleResourceId);
            return this;
        }

        public TabDialogBuilder setSubTitle(CharSequence subTitle) {
            mSubTitle = subTitle;
            return this;
        }

        public TabDialogBuilder setMessage(int messageResourceId) {
            mMessage = mContext.getText(messageResourceId);
            return this;
        }

        /**
         * Allow to set resource string with HTML formatting and bind %s,%i.
         * This is workaround for https://code.google.com/p/android/issues/detail?id=2923
         */
        public TabDialogBuilder setMessage(int resourceId, Object... formatArgs) {
            mMessage = Html.fromHtml(String.format(Html.toHtml(new SpannedString(mContext.getText(resourceId))), formatArgs));
            return this;
        }

        public TabDialogBuilder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public TabDialogBuilder setPositiveButtonText(int textResourceId) {
            mPositiveButtonText = mContext.getString(textResourceId);
            return this;
        }

        public TabDialogBuilder setPositiveButtonText(CharSequence text) {
            mPositiveButtonText = text;
            return this;
        }

        public TabDialogBuilder setNegativeButtonText(int textResourceId) {
            mNegativeButtonText = mContext.getString(textResourceId);
            return this;
        }

        public TabDialogBuilder setNegativeButtonText(CharSequence text) {
            mNegativeButtonText = text;
            return this;
        }

        public TabDialogBuilder setNeutralButtonText(int textResourceId) {
            mNeutralButtonText = mContext.getString(textResourceId);
            return this;
        }

        public TabDialogBuilder setNeutralButtonText(CharSequence text) {
            mNeutralButtonText = text;
            return this;
        }

        public TabDialogBuilder setTabButtonText(CharSequence[] value) {
            mTabButtonText = value;
            return this;
        }

        @Override
        protected Bundle prepareArguments() {
            Bundle args = new Bundle();
            args.putCharSequence(TabDialogFragment.ARG_MESSAGE, mMessage);
            args.putCharSequence(TabDialogFragment.ARG_TITLE, mTitle);
            args.putCharSequence(TabDialogFragment.ARG_SUB_TITLE, mSubTitle);
            args.putCharSequence(TabDialogFragment.ARG_POSITIVE_BUTTON, mPositiveButtonText);
            args.putCharSequence(TabDialogFragment.ARG_NEGATIVE_BUTTON, mNegativeButtonText);
            args.putCharSequence(TabDialogFragment.ARG_NEUTRAL_BUTTON, mNeutralButtonText);

            args.putCharSequenceArray(TabDialogFragment.ARG_TAB_BUTTON, mTabButtonText);

            return args;
        }
    }
}

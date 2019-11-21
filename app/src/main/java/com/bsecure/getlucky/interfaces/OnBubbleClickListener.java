package com.bsecure.getlucky.interfaces;

import android.view.View;

import com.bsecure.getlucky.bubble.Hyperlink;

public interface OnBubbleClickListener {
    /**
     *
     * @param view
     *            - BubbleEditText
     * @param linkSpec
     *            - object containing text that was clicked, start and stop
     *            position
     */
    public void onBubbleClick(View view, Hyperlink linkSpec);
}
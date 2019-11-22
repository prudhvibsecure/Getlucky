package com.bsecure.getlucky.bubble;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bsecure.getlucky.R;


/**
 * Created by Prudhvi Kumar on 9/10/17.
 *
 * Simple custom view example to show how to get selected events from the token
 * view. See ContactsCompletionView and contact_token.xml for usage
 */
public class TokenTextView extends TextView {

    public TokenTextView(Context context) {
        super(context);
    }

    public TokenTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setCompoundDrawablesWithIntrinsicBounds(0, 0, selected ? R.mipmap.close_x : 0, 0);
    }
}

package com.bsecure.getlucky.bubble;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.models.ChipsItem;


/**
 * Sample token completion view for basic contact info
 * <p>
 * Created on 9/12/13.
 *
 * @author prudhvi
 */
public class ContactsCompletionView extends TokenCompleteTextView<ChipsItem> {

    public ContactsCompletionView(Context context) {
        super(context);
    }

    public ContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactsCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(ChipsItem person) {
        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TokenTextView token = (TokenTextView) l.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);
        String emailAddress = person.getEmailAddress();
        token.setText(emailAddress);
        return token;
    }

    @Override
    public ChipsItem defaultObject(String completionText) {


        int index = completionText.indexOf("@");
        if (index == -1) {
            return new ChipsItem(completionText);
        }
        return new ChipsItem(completionText);
    }

}

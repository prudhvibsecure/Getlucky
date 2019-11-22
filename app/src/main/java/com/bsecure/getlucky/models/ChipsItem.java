package com.bsecure.getlucky.models;

import java.io.Serializable;

/**
 *
 * Created by Prudhvi Kumar on 9/10/17.
 *
 */

public class ChipsItem implements Serializable {

    String Name, EmailAddress;
    int Image;

    public ChipsItem(String completionText) {
        EmailAddress = completionText;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    @Override
    public String toString() { return EmailAddress; }
}

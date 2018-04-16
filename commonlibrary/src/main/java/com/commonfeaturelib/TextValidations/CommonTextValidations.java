package com.commonfeaturelib.TextValidations;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Parcel;
import android.provider.ContactsContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED;

/**
 * Created by janarthananr on 21/3/18.
 */

public class CommonTextValidations {
    //TODO Check Email Validation
    public String CheckEmailvalidation(String useremail) {
        String msg = "";
        if (useremail.trim().equals("")) {
            msg = "enter email";
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(useremail).matches()) {
            msg = "not a vaild mail";
        } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(useremail).matches()) {
            msg = "vaild mail";
        }

        return msg;
    }

    //TODO Check Password Validation(Send the Parameters as true depends on your requirement)
    //TODO Check 1st prams is your text,2nd is minimum length, 3rd is Special character validation,
    //TODO Check 4th is number validation, 5th is small alphabets validation,
    //TODO Check 6th is Caps alphabet validations ,7th is Alphabets validations
    public String CheckPasswordvalidation(String password, int minimumlength, boolean specialchars, boolean numbers, boolean smallleters, boolean capsletters, boolean aplhapets) {
        String msg = "";
        Pattern spl_regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");
        Pattern digits_regex = Pattern.compile("\\d+");
        Pattern digits_aplhapets = Pattern.compile("[a-zA-Z]");
        Pattern digits_aplhapetssmall = Pattern.compile("[a-z]");
        Pattern digits_aplhapetscaps = Pattern.compile("[A-Z]");
        if (password.trim().equals("")) {
            msg = "enter password";
        } else if (password.length() < minimumlength) {
            msg = "minimum error";
        } else if (specialchars && !spl_regex.matcher(password).find()) {
            msg = "special char issue";
        } else if (numbers && !digits_regex.matcher(password).find()) {
            msg = "number issue";
        } else if (aplhapets && !digits_aplhapets.matcher(password).find()) {
            msg = "aplhapets issue";
        } else if (smallleters && !digits_aplhapetssmall.matcher(password).find()) {
            msg = "small letter issue";
        } else if (capsletters && !digits_aplhapetscaps.matcher(password).find()) {
            msg = "caps letter issue";
        }

        return msg;
    }

    //TODO Check Common Validation (send the flag true if you need to check the Minimum and maximum Validation)
    public String CheckCommonvalidation(String yourtext, int minlength, int maxlength, boolean flagminimum, boolean flagmaximum) {
        String message = "";
        if (yourtext.trim().equals("")) {
            message = "empty filed";
        } else if (flagminimum && yourtext.length() < minlength) {
            message = "mimimum error";
        } else if (flagmaximum && yourtext.length() > maxlength) {
            message = "maximum error";
        }
        return message;
    }

    //TODO Check Language Validation 1st Params is Your String , 2nd Params is your Language Code
    public boolean CheckLanguageValidation(String name, String language) {
        boolean tagnotpresent = false;
        try {
            if (name.length() > 0) {
                for (int i = 0; i < name.length(); i++) {
                    int c = name.codePointAt(i);
                    if (language.equalsIgnoreCase("ar")) {
                        if ((c >= 0x0600 && c <= 0x06FF) || (c >= 0x0000 && c <= 0x0040) || (c >= 0x005B && c <= 0x0060)
                                || (c >= 0x007B && c <= 0x007F) || (c >= 0x0080 && c <= 0x00FF)
                                || (c >= 0x20A0 && c <= 0x20BE) || (c >= 0x00A2 && c <= 0x00A5)
                                || (c >= 0x0100 && c <= 0x017F) || (c >= 0x0180 && c <= 0x0233)) {

                        } else {
                            tagnotpresent = true;
                        }
                    } else if (language.equalsIgnoreCase("en")) {
                        if ((c >= 0x0000 && c <= 0x007F) || (c >= 0x0000 && c <= 0x0040) || (c >= 0x005B && c <= 0x0060)
                                || (c >= 0x007B && c <= 0x007F) || (c >= 0x0080 && c <= 0x00FF)
                                || (c >= 0x20A0 && c <= 0x20BE) || (c >= 0x00A2 && c <= 0x00A5)
                                || (c >= 0x0100 && c <= 0x017F) || (c >= 0x0180 && c <= 0x0233)) {

                        } else {
                            tagnotpresent = true;

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tagnotpresent;
    }

    //    TODO Load Image in Picasso
    public void LoadPicassoImages(String imageurl, ImageView imageview, int errorimage) {
        try {
            if (imageurl != null && !imageurl.equals("")) {
                if (imageurl.contains(" "))
                    imageurl = imageurl.replaceAll(" ", "%20");
                Picasso.get()
                        .load(imageurl)
                        .error(errorimage)
                        .fit()
                        .centerCrop()
                        .into(imageview);
            } else {
                Picasso.get()
                        .load(errorimage)
                        .error(errorimage)
                        .fit()
                        .centerCrop()
                        .into(imageview);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    TODO Spanable Strings 1st params your textview , 2nd params your spannable lists its include( start and end counts index, textcolor, your clicable text and underline or not flag) 3rd your clickable will handle in interface
    public void CheckSpanableString(TextView yourtextView, ArrayList<SplanableModelPojo> splanableModelPojo, final CommonStringInterface commonStringInterface) {
        final SpannableString help_subscription_span = new SpannableString(yourtextView.getText().toString());
        for (final SplanableModelPojo splanableModelPojo1 : splanableModelPojo) {
            if (splanableModelPojo1.YourTag != null && !splanableModelPojo1.YourTag.equals("")) {
                help_subscription_span.setSpan(new MyClickableSpan(splanableModelPojo1.YourTag, commonStringInterface), splanableModelPojo1.YourStartCount, splanableModelPojo1.YourEndCount, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (splanableModelPojo1.flagunderline) {
                UnderlineSpan noUnderline3 = new UnderlineSpan();
                help_subscription_span.setSpan(noUnderline3, splanableModelPojo1.YourStartCount, splanableModelPojo1.YourEndCount, 0);
            } else {
                NoUnderlineSpan noUnderline3 = new NoUnderlineSpan();
                help_subscription_span.setSpan(noUnderline3, splanableModelPojo1.YourStartCount, splanableModelPojo1.YourEndCount, 0);
            }
            help_subscription_span.setSpan(new ForegroundColorSpan(splanableModelPojo1.YourForegroundColor), splanableModelPojo1.YourStartCount, splanableModelPojo1.YourEndCount, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            yourtextView.setText(help_subscription_span);
            yourtextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    //TODO Replace Special Characters
    public String ReplaceSpecialCharacters(String input) {
        String output = input;

        if (output.contains("&quot;")) output = output.replaceAll("&quot;", "\"");
        if (output.contains("&amp;quot;")) output = output.replaceAll("&amp;quot;", "\"");
        if (output.contains("&amp;amp;")) output = output.replaceAll("&amp;amp;", "&");
        if (output.contains("&amp;bull;")) output = output.replaceAll("&amp;bull;", "•");
        if (output.contains("&amp;radic;")) output = output.replaceAll("&amp;radic;", "√");
        if (output.contains("&amp;pi;")) output = output.replaceAll("&amp;pi;", "π");
        if (output.contains("&amp;divide;")) output = output.replaceAll("&amp;divide;", "÷");
        if (output.contains("&amp;times;")) output = output.replaceAll("&amp;times;", "×");
        if (output.contains("&amp;para;")) output = output.replaceAll("&amp;para;", "¶");
        if (output.contains("&amp;deg;")) output = output.replaceAll("&amp;deg;", "°");
        if (output.contains("&amp;cent;")) output = output.replaceAll("&amp;cent;", "¢");
        if (output.contains("&amp;euro;")) output = output.replaceAll("&amp;euro;", "€");
        if (output.contains("&amp;pound;")) output = output.replaceAll("&amp;pound;", "£");
        if (output.contains("&amp;copy;")) output = output.replaceAll("&amp;copy;", "©");
        if (output.contains("&amp;reg;")) output = output.replaceAll("&amp;reg;", "®");
        if (output.contains("&amp;trade;")) output = output.replaceAll("&amp;trade;", "™");
        if (output.contains("&amp;gt;")) output = output.replaceAll("&amp;gt;", ">");
        if (output.contains("&amp;lt;")) output = output.replaceAll("&amp;lt;", "<");
        if (output.contains("&#039;")) output = output.replaceAll("&#039;", "'");
        if (output.contains("&rsquo;")) output = output.replaceAll("&rsquo;", "’");
        if (output.contains("&ecirc;")) output = output.replaceAll("&ecirc;", "ê");
        if (output.contains("&eacute;")) output = output.replaceAll("&eacute;", "é");
        if (output.contains("&egrave;")) output = output.replaceAll("&egrave;", "è");
        if (output.contains("&euml;")) output = output.replaceAll("&euml;", "ë");
        if (output.contains("&aelig;")) output = output.replaceAll("&aelig;", "æ");
        if (output.contains("&agrave;")) output = output.replaceAll("&agrave;", "à");
        if (output.contains("&atilde;")) output = output.replaceAll("&atilde;", "ã");
        if (output.contains("&aring;")) output = output.replaceAll("&aring;", "å");
        if (output.contains("&aacute;")) output = output.replaceAll("&aacute;", "á");
        if (output.contains("&times;")) output = output.replaceAll("&times;", "×");
        if (output.contains("&amp;")) output = output.replaceAll("&amp;", "&");

        return output;
    }

    //TODO Check Data Saver is on or Off
    public boolean DataSaverOnorOff(Context context) {
        boolean flagdatasaver = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // Checks if the device is on a metered network
            if (connMgr.isActiveNetworkMetered()) {
                // Checks user’s Data Saver settings.
                switch (connMgr.getRestrictBackgroundStatus()) {
                    case RESTRICT_BACKGROUND_STATUS_ENABLED:
                        // Background data usage is blocked for this app. Wherever possible,
                        // the app should also use less data in the foreground.
                        flagdatasaver = true;
                        break;
                    case RESTRICT_BACKGROUND_STATUS_WHITELISTED:
                        // The app is whitelisted. Wherever possible,
                        // the app should use less data in the foreground and background.
                        flagdatasaver = false;
                        break;
                    case RESTRICT_BACKGROUND_STATUS_DISABLED:
                        flagdatasaver = false;
                        break;
                    // Data Saver is disabled. Since the device is connected to a
                    // metered network, the app should use less data wherever possible.
                }
            }
        } else {
            flagdatasaver = false;
        }

        return flagdatasaver;
    }

    public ArrayList<Contact_Model> readContacts(Context context) {
        ArrayList<Contact_Model> contactList = new ArrayList<Contact_Model>();
        byte[] photoByte = null;
        Cursor contactsCursor = context.getContentResolver().
                query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");
        while (contactsCursor != null && contactsCursor.moveToNext()) {
            Contact_Model contact_model = new Contact_Model();
            contact_model.contactId = contactsCursor.getString(contactsCursor.getColumnIndex("_ID"));
            contact_model.contactName = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            contact_model.contactNumber = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (contactsCursor.getString(contactsCursor.getColumnIndex("mimetype"))
                    .equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                switch (contactsCursor.getInt(contactsCursor
                        .getColumnIndex("data2"))) {
                    case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                        contact_model.contactEmail = contactsCursor.getString(contactsCursor
                                .getColumnIndex("data1"));
                        break;
                    case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                        contact_model.contactEmail = contactsCursor.getString(contactsCursor
                                .getColumnIndex("data1"));
                        break;

                }
            }
            if (contactsCursor
                    .getString(
                            contactsCursor.getColumnIndex("mimetype"))
                    .equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
                photoByte = contactsCursor.getBlob(contactsCursor
                        .getColumnIndex("data15")); // get photo in
                if (photoByte != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(
                            photoByte, 0, photoByte.length);
                    File cacheDirectory = context.getCacheDir();
                    File tmp = new File(cacheDirectory.getPath()
                            + "/_androhub" + contact_model.contactId + ".png");
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(
                                tmp);
                        bitmap.compress(Bitmap.CompressFormat.PNG,
                                100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    contact_model.contactPhoto = tmp.getPath();
                }

            }
            contactList.add(contact_model);

        }
        return contactList;
    }

    //    TODO Clickable Span
    class MyClickableSpan extends ClickableSpan {
        String mText = "";
        CommonStringInterface commonStringInterface;

        private MyClickableSpan(String text, CommonStringInterface commonStringInterface) {
            this.mText = text;
            this.commonStringInterface = commonStringInterface;
        }

        @Override
        public void onClick(final View widget) {
            commonStringInterface.valuesfrominterface(mText);
        }

    }

    class NoUnderlineSpan extends UnderlineSpan {
        public NoUnderlineSpan() {
        }

        public NoUnderlineSpan(Parcel src) {
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }
    }
}

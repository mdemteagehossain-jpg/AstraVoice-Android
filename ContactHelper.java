package com.astra.voiceassistant;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactHelper {

    private final Context context;

    public ContactHelper(Context context) {
        this.context = context;
    }

    public String getPhoneNumberByName(String name) {
        if (name == null || name.trim().isEmpty()) return null;

        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(name.trim()));
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };

        try (Cursor cursor = resolver.query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                if (numberIndex != -1) {
                    return cursor.getString(numberIndex);
                }
            }
        } catch (SecurityException e) {
            return null;
        }
        return null;
    }
}
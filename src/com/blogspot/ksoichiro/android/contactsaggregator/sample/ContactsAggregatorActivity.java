/*
 * Copyright (c) 2011 Soichiro Kashima
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.blogspot.ksoichiro.android.contactsaggregator.sample;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Gets some contact information from your phone.<br />
 * This application shows the information only on the LogCat.
 * 
 * @author Soichiro Kashima
 * @since 2011/08/19
 */
public final class ContactsAggregatorActivity extends Activity {

    /** Upper limit of contacts to get. */
    private static final int MAX_CONTACTS = 10;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Gets cursor to aggregate contacts
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
                );
        try {

            // Aggregates MAX_CONTACTS contacts from the cursor
            int i = 0;
            while (i < MAX_CONTACTS && cursor.moveToNext()) {
                // ID
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                // Display name
                String displayName = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                // Emails: some people have 2 or more emails
                final String[] selectionArgs = {
                        id,
                };
                Cursor cursorEmail = resolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        selectionArgs, null);

                List<String> emails = new ArrayList<String>();
                try {
                    while (cursorEmail.moveToNext()) {
                        emails.add(cursorEmail.getString(cursorEmail
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
                    }
                } finally {
                    cursorEmail.close();
                }

                // Logs the information
                Log.i("Contacts", "Display name: " + displayName);
                for (String email : emails) {
                    Log.i("Contacts", "Email: " + email);
                }

                i++;
            }
        } finally {
            cursor.close();
        }
    }
}

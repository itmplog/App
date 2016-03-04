package itmp.top.app.contentprovider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import itmp.top.app.R;

public class ContactProviderTest extends AppCompatActivity {

    private Button query;
    private Button add;
    private final int REQUIREPERMISSION_RTN = 0x11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_provider_test);

        query = (Button) findViewById(R.id.query);
        add = (Button) findViewById(R.id.add);

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(ContactProviderTest.this,
                            Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(ContactProviderTest.this,
                                new String[]{ Manifest.permission.READ_CONTACTS },
                                REQUIREPERMISSION_RTN);
                    } else {
                        readContacts();
                    }
            }
        });
    }

    public void readContacts() {
        final ArrayList<String> names = new ArrayList<String>();
        final ArrayList<ArrayList<String>> details = new ArrayList<ArrayList<String>>();

        Cursor cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            names.add(name);

            Cursor phones = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            ArrayList<String> detail = new ArrayList<String>();
            while (phones.moveToNext()) {
                String phoneNum = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                detail.add("电话号码： " + phoneNum);
            }
            phones.close();
            Cursor emails = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,
                    null, null
            );
            while (emails.moveToNext()) {
                String emailAddr = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                detail.add("邮件地址: " + emailAddr);
            }
            emails.close();
            details.add(detail);
        }
        cursor.close();

        View resultDialog = getLayoutInflater().inflate(
                R.layout.contact_provider_query_result, null
        );

        ExpandableListView expandableListView = (ExpandableListView) resultDialog.findViewById(R.id.resultDialog);

        ExpandableListAdapter expandableListAdapter = new BaseExpandableListAdapter() {
            @Override
            public int getChildrenCount(int i) {
                return details.get(i).size();
            }

            @Override
            public Object getChild(int i, int i1) {
                return details.get(i).get(i1);
            }

            @Override
            public long getChildId(int i, int i1) {
                return i1;
            }

            public TextView getTextView() {
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                );
                TextView textView = new TextView(ContactProviderTest.this);
                textView.setLayoutParams(lp);
                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                textView.setPadding(60, 0, 0, 0);
                textView.setTextSize(20);
                return textView;
            }

            @Override
            public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
                TextView textView = getTextView();
                textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                textView.setText(getChild(i, i1).toString());
                return textView;
            }

            @Override
            public Object getGroup(int i) {
                return names.get(i);
            }

            @Override
            public int getGroupCount() {
                return names.size();
            }

            @Override
            public long getGroupId(int i) {
                return i;
            }

            @Override
            public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
                TextView textView = getTextView();
                textView.setText(getGroup(i).toString());
                return textView;
            }

            @Override
            public boolean isChildSelectable(int i, int i1) {
                return true;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };

        expandableListView.setAdapter(expandableListAdapter);

        new AlertDialog.Builder(ContactProviderTest.this)
                .setView(resultDialog).setPositiveButton("确定", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUIREPERMISSION_RTN:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    readContacts();
                }else{
                    Snackbar.make(query, "Need Permission.", Snackbar.LENGTH_LONG).setAction("Request Again", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(ContactProviderTest.this,
                                    new String[]{ Manifest.permission.READ_CONTACTS },
                                    REQUIREPERMISSION_RTN);
                        }
                    }).show();
                }
                break;
            default:
                    break;
        }
    }
}

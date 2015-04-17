package com.scheduler.aw.scheduler;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.RadioGroup;

public class MainActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "com.scheduler.aw.scheduler.MESSAGE";
    public final static String EXTRA_MESSAGE_2 = "com.scheduler.aw.scheduler.MESSAGE_2";
    public final static String EXTRA_INT = "com.scheduler.aw.scheduler.INT";
    //public static

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_search){
            openSearch();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openSearch(){


    }

    public void enterMessage(View v){
        Intent intent = new Intent(this, ScheduleActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);            //retrieves message one
        editText = (EditText) findViewById(R.id.editText2);
        message = editText.getText().toString();
        if(message.equals("")){
            message = " ";
        }
        intent.putExtra(EXTRA_MESSAGE_2, message);          //retrieves message two
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        intent.putExtra(EXTRA_INT,radioGroup.getCheckedRadioButtonId());
        startActivity(intent);
    }
}

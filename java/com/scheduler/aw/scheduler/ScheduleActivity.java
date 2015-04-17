package com.scheduler.aw.scheduler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class ScheduleActivity extends ActionBarActivity{

    final String fileSeparator = "@@@";
    final String emptyValue = "###";
    ArrayAdapter<String> adapter;
    ListView myListView;
    //String[] myList;
    ArrayList<String> myTasks;   //TODO refactor this information into a class
    ArrayList<String> myDetails;
    ArrayList<Integer> myPriority;
    Thread thread;
    ColorUpdater runnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main_activity2);
		Intent intent = getIntent();
		String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        int priority = intent.getIntExtra(MainActivity.EXTRA_INT, 1);

        myTasks = new ArrayList<>();  //TODO move this to its own method
        myDetails = new ArrayList<>();
        myPriority = new ArrayList<>();
        String[] tasks = loadData(getString(R.string.local_file_location_tasks));
        String[] details = loadData(getString(R.string.local_file_location_details));
        myPriority = loadDataInt(getString(R.string.local_file_location_priority));
        if(tasks != null){
            myTasks.addAll(Arrays.asList(tasks));
        }
        if(details != null){
            myDetails.addAll(Arrays.asList(details));
        }
        if(myPriority == null){
            myPriority = new ArrayList<Integer>();
        }
        //myTasks.addAll()


        if(message != null && ! message.equals("")) {
            myTasks.add(message);
            myDetails.add(intent.getStringExtra(MainActivity.EXTRA_MESSAGE_2));
            Log.i("TestV1", "at the zero place:" + myDetails.get(0));
            myPriority.add(priority);
            intent.putExtra(MainActivity.EXTRA_MESSAGE, message); //TODO
        }

		 adapter = new ArrayAdapter<String>(this,
				R.layout.simple_task, myTasks);

		// Set the text view as the activity layout
		setContentView(R.layout.activity_main_activity2);
        myListView = (ListView) findViewById(R.id.listView);
        final ListView listView = myListView; //TODO better way to do this?
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("V1test", "on Item clicked called");
                showMyDialog((int) id);
            }
        });

        //runnable = new ColorUpdater(this);
        //thread  = new Thread(runnable);
        //thread.start();

        Log.i("V1test","ended instantiation");
	}

    @Override
    public void onResume(){
        super.onResume();

        runnable = new ColorUpdater(this);
        thread  = new Thread(runnable);
        //thread.start();           //TODO This approach is all sorts of problematic
    }

    public void yieldThread(){
        try {
            thread.sleep(100,0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        try {
            runnable.stop();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void setPriorityColors(){
        for(int x = 0; x < myPriority.size(); x++){
            View v = myListView.getChildAt(x);
            if(v != null && v.isShown()) {
                switch (myPriority.get(x)) {
                    case R.id.radioButtonLowPriority:
                        v.setBackgroundColor(Color.GREEN);
                        break;
                    case R.id.radioButtonMediumPriority:
                        v.setBackgroundColor(Color.YELLOW);
                        break;
                    case R.id.radioButtonRadioHighPrioirty:
                    default:
                        v.setBackgroundColor(Color.RED);
                        break;
                }
            }else{
                //Log.i("V1test",x + " View is null");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        return true;
    }

    @Override
    protected void onStop(){
        super.onStop();  // Always call the superclass method first

        //Save all the data
        saveData(myTasks, myDetails);
        saveData(myPriority, getString(R.string.local_file_location_priority));


    }

    public void addTask(String s){
        adapter.add(s);  //TODO add new features as they come up
    }

    public  void removeTask(int pos){
        //myListView.removeViewAt(pos);
        myTasks.remove(pos);
        myDetails.remove(pos);
        myPriority.remove(pos);
        adapter.notifyDataSetChanged();
    }

    public void showMyDialog(int id){

        final int temp = id;  //TODO is this the best way to handle this?

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(myDetails.get(id))
                .setTitle(myTasks.get(id));
        builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();                    //Ends the dialog.
            }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeTask(temp);
                dialog.dismiss();
            }
        });

// 3. Get the AlertDialog from create()
        dialog = builder.create();

        dialog.show();

    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_create_task) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

    /**
     * Reads a properly formatted outside file for a prioirty list.
     * Proper format is:
     * 1 header integer specifying Y number of integers
     * Y following integers
     * @param savedFiledLocation The location of the file to load as a String
     * @return an ArrayList containing integers specifying priority
     */
    public ArrayList<Integer> loadDataInt(String savedFiledLocation){
        ArrayList<Integer> myInts = new ArrayList<Integer>();
        FileInputStream fileInputStream;
        try{
            fileInputStream = openFileInput(savedFiledLocation);
            if(fileInputStream != null){  //Not sure if this can be null  //TODO REMOVE
                DataInputStream dis = new DataInputStream(fileInputStream);
                int valuesToRead = dis.readInt();
                for(int x=0; x<valuesToRead; x++){
                    myInts.add(dis.readInt());
                }
                fileInputStream.close();
                return myInts;
            }
        } catch (Exception e) {
            Log.i("Error"," " + e.getMessage()); //TODO possibly handle the error in a better way
        }
        return null;


    }

    public void saveData(ArrayList<Integer> integers, String fileLocation){
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(fileLocation, this.MODE_PRIVATE);
            DataOutputStream dos = new DataOutputStream(outputStream);
            dos.writeInt(integers.size());
            for(int x = 0; x < integers.size(); x++) {
                dos.writeInt(integers.get(x));
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String[] loadData(String savedFiledLocation){
        FileInputStream fileInputStream;
        try{
            fileInputStream = openFileInput(savedFiledLocation);
            if(fileInputStream != null){  //Not sure if this can be null  //TODO REMOVE
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                if(sb.toString().equals("")){
                    return null;   //TODO see if this solves the empty problem
                }
                String[] schedules =  sb.toString().split(fileSeparator);  //TODO unpackage the files in a more fitting way?
                deleteFile(getString(R.string.local_file_location_tasks)); //deletes old file
                fileInputStream.close();
                return schedules;
            }
        } catch (Exception e) {
            Log.i("Error",e.getMessage()); //TODO possibly handle the error in a better way
        }
        return null;
    }

    public void saveData(ArrayList<String> tasks, ArrayList<String> details){
        String filename = getString(R.string.local_file_location_tasks);  //TODO Consider Refactoring
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, this.MODE_PRIVATE);
            for(int x = 0; x < tasks.size(); x++) {
                outputStream.write((tasks.get(x) + fileSeparator).getBytes());  //saves the schedules seperated by fileSeperator
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        filename = getString(R.string.local_file_location_details);
        try {
            outputStream = openFileOutput(filename, this.MODE_PRIVATE);
            for(int x = 0; x < details.size(); x++) {
                outputStream.write((details.get(x) + fileSeparator).getBytes());  //saves the schedules seperated by fileSeperator
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //public void saveDataInt
}


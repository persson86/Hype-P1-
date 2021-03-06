package br.com.schneider.persson.p1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class MyReservations extends Activity {

    private Firebase firebaseRef, bookingRef, idRef;
    ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> listDateHour = new ArrayList<String>();

    String dateLine, hourLine, userId, node;
    String url_indexId;
    Integer count = 1;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        setConfigScreen();
        setConfigFirebase();
        setProgress();
    }

    public void setConfigScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void setConfigFirebase() {
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
    }

    public void setProgress() {

        Log.i("LF", "pre progress");
        progressBar.setVisibility(View.VISIBLE);
        count = 1;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        new MyTask().execute(10);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                String userID = firebaseRef.getAuth().getUid();
                Log.i("LF", userID);

                final Firebase indexIdRef;
                String url_indexId = "https://perssomobappfirebase.firebaseio.com/indexId/";
                url_indexId = url_indexId.concat(userID);
                indexIdRef = new Firebase(url_indexId);

                indexIdRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            return;
                        }
                        for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                            IndexId indexId = idSnapshot.getValue(IndexId.class);

                            String eventHour = indexId.getHour();
                            String eventDate = indexId.getDate();

                            Log.d("LF", eventHour);

                            list.add(eventDate + " at " + eventHour);
                            listDateHour.add(eventDate + ":" + eventHour);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //instantiate custom adapter
                                    MyCustomAdapterReservations adapter = new MyCustomAdapterReservations(list, getApplicationContext());

                                    //handle listview and assign adapter
                                    ListView lView = (ListView) findViewById(R.id.listViewReservations);
                                    lView.setAdapter(adapter);
                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.i("LF", firebaseError.getMessage());
                    }
                });

            }
        }).start();
*/
    }

    public void deleteHourBD(final String eventLine) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                //logica
                String[] parts = eventLine.split(" at ");
                dateLine = parts[0];
                hourLine = parts[1];
                userId = parts[2];

                //select base indexId
                final Firebase indexIdRef;
                url_indexId = "https://perssomobappfirebase.firebaseio.com/indexId/";
                url_indexId = url_indexId.concat(userId);
                indexIdRef = new Firebase(url_indexId);

                indexIdRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (final DataSnapshot eventSnapshot : snapshot.getChildren()) {
                            final IndexId indexId = eventSnapshot.getValue(IndexId.class);

                            final String eventHour = indexId.getHour();
                            String eventDate = indexId.getDate();

                            if (dateLine.equals(eventDate) && hourLine.equals(eventHour)) {
                                node = indexId.getNode();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        deleteBDBooking(node);
                                        final Firebase indexDeleteRef;
                                        String indexDelete = url_indexId;
                                        indexDelete = indexDelete.concat("/" + eventSnapshot.getKey().toString());
                                        indexDeleteRef = new Firebase(indexDelete);
                                        indexDeleteRef.removeValue();

                                    }
                                });
                            }

                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.i("LF", firebaseError.getMessage());
                    }
                });

            }
        }).start();

    }

    public void deleteBDBooking(String node) {
        String formatedDate = dateLine.replaceAll("/", "");

        final Firebase bookingRef;
        String url_booking = "https://perssomobappfirebase.firebaseio.com/booking/";
        url_booking = url_booking.concat(formatedDate + "/" + node);
        bookingRef = new Firebase(url_booking);
        bookingRef.removeValue();

    }

    class MyTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {

            String userID = firebaseRef.getAuth().getUid();

            final Firebase indexIdRef;
            String url_indexId = "https://perssomobappfirebase.firebaseio.com/indexId/";
            url_indexId = url_indexId.concat(userID);
            indexIdRef = new Firebase(url_indexId);

            indexIdRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) {
                        return;
                    }
                    for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                        IndexId indexId = idSnapshot.getValue(IndexId.class);

                        String eventHour = indexId.getHour();
                        String eventDate = indexId.getDate();

                        list.add(eventDate + " at " + eventHour);
                        listDateHour.add(eventDate + ":" + eventHour);

                        //instantiate custom adapter
                        MyCustomAdapterReservations adapter = new MyCustomAdapterReservations(list, getApplicationContext());

                        //handle listview and assign adapter
                        ListView lView = (ListView) findViewById(R.id.listViewReservations);
                        lView.setAdapter(adapter);

                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.i("LF", firebaseError.getMessage());
                }
            });

/*
            for (; count <= params[0]; count++) {
                try {
                    Thread.sleep(500);
                    publishProgress(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
            return "Task Completed.";

        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            //txt.setText("Task Starting...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //txt.setText("Running..."+ values[0]);
            progressBar.setProgress(values[0]);
        }
    }

}

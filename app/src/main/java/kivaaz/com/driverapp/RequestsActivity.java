package kivaaz.com.driverapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {

    private static final String TAG = ".RequestsActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database;
    DatabaseReference myRef;
    List<RequestList> reqList;
    List<RequestList> acc_reqList;
    RequestAdapter adapter;

    RecyclerView req_recycle;
    RecyclerView acc_req_recycle;

    TextView availableTV, acceptedTV;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Requests");
        AuthChecker();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    req_recycle = (RecyclerView) findViewById(R.id.request_recycle);
        acc_req_recycle = (RecyclerView) findViewById(R.id.acc_request_recycle);

        availableTV = (TextView) findViewById(R.id.availableTV);
        acceptedTV = (TextView) findViewById(R.id.acceptedTV);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                reqList = new ArrayList<>();
                acc_reqList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    RequestList req = snapshot.getValue(RequestList.class);
                    if(!req.getReqAccepted()){
                        reqList.add(req);
                    }else{
                        if(req.getAcceptedBy().equals(mAuth.getCurrentUser().getEmail())){
                            acc_reqList.add(req);
                        }
                    }
                }
                if(!reqList.isEmpty()){
                    adapter = new RequestAdapter(reqList, getBaseContext(), new RequestAdapter.OnItemClick() {
                        @Override
                        public void OnClick(final String reqname, final String reqemail) {
                            String uploadID = reqname.replace(" ","")+ "_" + reqemail.replace(".","");
                            myRef.child(uploadID).child("reqAccepted").setValue(true);
                            myRef.child(uploadID).child("acceptedBy").setValue(mAuth.getCurrentUser().getEmail());
                        }
                    });
                    req_recycle.setAdapter(adapter);
                    req_recycle.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL,false));
                }else {
                    availableTV.setText("No Requests Available");
                    req_recycle.setVisibility(View.GONE);
                }

                if(!acc_reqList.isEmpty()){
                    adapter = new RequestAdapter(acc_reqList, getBaseContext(), new RequestAdapter.OnItemClick() {
                        @Override
                        public void OnClick(final String reqname, final String reqemail) {
                            String uploadID = reqname.replace(" ","")+ "_" + reqemail.replace(".","");
                            myRef.child(uploadID).child("reqAccepted").setValue(false);
                            myRef.child(uploadID).child("acceptedBy").setValue("None");
                        }
                    });
                    acc_req_recycle.setAdapter(adapter);
                    acc_req_recycle.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL,false));
                }else {
                    acceptedTV.setText("You Haven't accepted any Requests");
                    acc_req_recycle.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void AuthChecker() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(getBaseContext(),MainActivity.class));
                }
            }
        };
    }

}

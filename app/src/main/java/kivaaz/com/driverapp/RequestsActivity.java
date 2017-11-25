package kivaaz.com.driverapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
    RequestAdapter adapter;

    RecyclerView req_recycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Requests");
        AuthChecker();

        req_recycle = (RecyclerView) findViewById(R.id.request_recycle);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                reqList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    RequestList req = snapshot.getValue(RequestList.class);
                    if(!req.getReqAccepted()){
                        reqList.add(req);
                    }
                }
                if(!reqList.isEmpty()){
                    adapter = new RequestAdapter(reqList, getBaseContext(), new RequestAdapter.OnItemClick() {
                        @Override
                        public void OnClick(final String reqname, final String reqemail) {

                            myRef.child(dataSnapshot.getKey()).child("reqAccepted").setValue(true);
                            myRef.child(dataSnapshot.getKey()).child("acceptedBy").setValue(mAuth.getCurrentUser().getEmail());
                        }
                    });
                    req_recycle.setAdapter(adapter);
                    req_recycle.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL,false));
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

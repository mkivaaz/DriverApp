package kivaaz.com.driverapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = ".MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database;
    DatabaseReference myRef;
    EditText emailET;
    EditText passwordET;

    private final String USER_REFERNCE = "Users";

    Button signupBtn, loginBtn, logoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USER_REFERNCE);
        AuthChecker();

        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);

        signupBtn = (Button) findViewById(R.id.btn_SignUp);
        loginBtn  = (Button) findViewById(R.id.btn_Login);
        logoutBtn  = (Button) findViewById(R.id.btn_LogOut);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = emailET.getText().toString().trim();
                String Password = passwordET.getText().toString().trim();
                mAuth.createUserWithEmailAndPassword(Email,Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    Exception e = task.getException();
                                    Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                }else {
                                    String uid = task.getResult().getUser().getUid();
                                    String curr_email = task.getResult().getUser().getEmail();
                                    UserType userType = new UserType(curr_email,"Driver");
                                    myRef.child(uid).setValue(userType).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(MainActivity.this,"User Registereed",Toast.LENGTH_SHORT).show();
                                            logoutBtn.setVisibility(View.VISIBLE);
                                            loginBtn.setVisibility(View.GONE);
                                            signupBtn.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }
                        });
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = emailET.getText().toString().trim();
                String Password = passwordET.getText().toString().trim();
                mAuth.signInWithEmailAndPassword(Email,Password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    Exception e = task.getException();
                                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }else {
                                    String uid = task.getResult().getUser().getUid();
                                    myRef.child(uid).child("role").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.getValue().equals("Driver")){
                                                Toast.makeText(MainActivity.this,"User Logged in", Toast.LENGTH_SHORT).show();
                                                logoutBtn.setVisibility(View.VISIBLE);
                                                loginBtn.setVisibility(View.GONE);
                                                signupBtn.setVisibility(View.GONE);
//                                                startActivity(new Intent(getBaseContext(),RequestsActivity.class));
                                            }else {
                                                Toast.makeText(MainActivity.this,"You're in the wrong app", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

//
                                }
                            }
                        });
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                logoutBtn.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);
                signupBtn.setVisibility(View.VISIBLE);
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
                }
            }
        };
    }
}

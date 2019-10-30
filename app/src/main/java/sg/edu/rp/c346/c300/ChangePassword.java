package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity {

    private Button xChangeBtn;
    private EditText xNew, xOld, xCfm;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        xChangeBtn = (Button) findViewById(R.id.pChangeBtn);
        xNew = (EditText) findViewById(R.id.pNewPw);
        xOld = (EditText) findViewById(R.id.pOldPw);
        xCfm = (EditText) findViewById(R.id.pCfmPw);

        reference = FirebaseDatabase.getInstance().getReference().child("Customer").child(uid).child("Parent");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String oldPw = dataSnapshot.child("password").getValue().toString();
                xChangeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String newPw = xNew.getText().toString();
                        final String cfmPw = xCfm.getText().toString();



                            if(!oldPw.equals(newPw) && newPw.equals(cfmPw) && (newPw.matches(".*[0-9]{1,}.*") && newPw.matches(".*[A-Z]{1,}.*") && newPw.matches(".*[@#$]{1,}.*") && newPw.length() >= 6 && newPw.length() <= 40)){
                            reference.child("password").setValue(newPw);
                            Toast.makeText(ChangePassword.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ChangePassword.this,TestingParentMain.class));
                            finish();
                        }else if(oldPw.equals(newPw)){
                            Toast.makeText(ChangePassword.this, "Please enter a new password", Toast.LENGTH_SHORT).show();
                        }else if(newPw.isEmpty() && cfmPw.isEmpty()){
                            Toast.makeText(ChangePassword.this,"Please input new password", Toast.LENGTH_SHORT).show();
                        }else if (cfmPw.isEmpty()){
                            Toast.makeText(ChangePassword.this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
                        }else if (!cfmPw.equals(newPw)){
                            Toast.makeText(ChangePassword.this, "Please make sure that your new password is the same", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ChangePassword.this,"Length is between 7 and 40. Must contain at least one digit, one upper and lower case, one special characters(@, #, $, !, %, ^, *)", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}

package elfak.mosis.mosis_17928

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class Register : AppCompatActivity() {


    private lateinit var editMail: EditText
    private lateinit var editUserName: EditText
    private lateinit var editFirstName: EditText
    private lateinit var editLastName: EditText
    private lateinit var editPhone: EditText
    private lateinit var editPass: EditText
    private lateinit var regButton: Button
    private lateinit var pBar: ProgressBar
    private lateinit var textView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseDatabase
    private lateinit var dbRef:DatabaseReference


    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editMail=findViewById(R.id.email2)
        editUserName=findViewById(R.id.userName)
        editFirstName=findViewById(R.id.firstName)
        editLastName=findViewById(R.id.lastName)
        editPhone=findViewById(R.id.phone)
        editPass= findViewById(R.id.password2)
        regButton= findViewById(R.id.btn_register)
        pBar= findViewById(R.id.progressBar2)
        textView= findViewById(R.id.loginNow)
        auth=FirebaseAuth.getInstance()

        db= FirebaseDatabase.getInstance("https://mosis17928-default-rtdb.europe-west1.firebasedatabase.app/")
        dbRef= db.reference.child("User")

        regButton.setOnClickListener {

            val email: String= editMail.text.toString()
            val password: String= editPass.text.toString()
            val username: String= editUserName.text.toString()
            val firstname: String= editFirstName.text.toString()
            val lastname: String= editLastName.text.toString()
            val phone: String= editPhone.text.toString()

            pBar.setVisibility(View.VISIBLE)

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            if (username.isEmpty()) {
                Toast.makeText(this, "Enter username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            if (firstname.isEmpty()) {
                Toast.makeText(this, "Enter first name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            if (lastname.isEmpty()) {
                Toast.makeText(this, "Enter last name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            if (phone.isEmpty()) {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account created.", Toast.LENGTH_SHORT).show()




                        val uid= auth.currentUser!!.uid

                        val newUser = User(uid,username,0,password,firstname,lastname,phone,email)
                        val newChildRef = dbRef.push()

                        newChildRef.setValue(newUser).addOnSuccessListener {
                            editMail.text.clear()
                            editPass.text.clear()
                            editFirstName.text.clear()
                            editLastName.text.clear()
                            editPhone.text.clear()
                            editUserName.text.clear()


                            val intent = Intent(applicationContext, Login::class.java)
                            startActivity(intent)
                            finish()


                        }.addOnFailureListener(){
                            Toast.makeText(this, "Failed.", Toast.LENGTH_SHORT).show()
                        }


                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()

                    }
                }





            pBar.setVisibility(View.GONE)
        }

        textView.setOnClickListener{
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}
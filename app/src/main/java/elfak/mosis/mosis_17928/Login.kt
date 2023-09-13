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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var editMail: EditText
    private lateinit var editPass: EditText
    private lateinit var logButton: Button
    private lateinit var pBar: ProgressBar
    private lateinit var textView: TextView
    private lateinit var auth: FirebaseAuth


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
        setContentView(R.layout.activity_login)

        editMail=findViewById(R.id.email1)
        editPass= findViewById(R.id.password1)
        logButton= findViewById(R.id.btn_login)
        pBar= findViewById(R.id.progressBar1)
        textView= findViewById(R.id.registerNow)
        auth=FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this)


        logButton.setOnClickListener {
            val email: String= editMail.text.toString()
            val password: String= editPass.text.toString()


            if (email=="") {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password=="") {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            pBar.setVisibility(View.VISIBLE)


            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, " Login successful.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            pBar.setVisibility(View.GONE)
        }

        textView.setOnClickListener{
            val intent = Intent(applicationContext, Register::class.java)
            startActivity(intent)
            finish()
        }
    }




}
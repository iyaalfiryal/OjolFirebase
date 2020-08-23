package com.idniyal.ojolfirebase.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.idniyal.ojolfirebase.MainActivity
import com.idniyal.ojolfirebase.R
import com.idniyal.ojolfirebase.model.Users
import com.idniyal.ojolfirebase.utils.Constan
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    var googleSignInClient: GoogleSignInClient? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //todo 11
        signUpButtonGmail.onClick {
            signIn()
        }

        signUpLink.onClick {
            startActivity<SignUpActivity>()
        }

        loginButton.onClick {
            if (loginEmail.text.isNotEmpty() &&
                loginPassword.text.isNotEmpty()
            ) {
                authUserSignIn(
                    loginEmail.text.toString(),
                    loginPassword.text.toString()
                )
            }
        }
    }

    //todo 10
    //authentikasi sign in email pw
    private fun authUserSignIn(email: String, pass: String){
        var status: Boolean? = null

        auth?.signInWithEmailAndPassword(email, pass)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    startActivity<MainActivity>()
                    finish()
                }else{
                    toast("login failed")
                    Log.e("error", "message")
                }
            }
    }


    //todo 5
    //request sign in google/gmail
    private fun signIn(){
        val gson = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gson)

        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, 4)
    }

    //todo 6
    //hasil request sign google
    /* setelah user memilih account yang sudah ter sign in akan
    mengambil informasi dari user
    yang sign in google menggunakan onactivity result*/
    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode,
            resultCode, data)

        if (requestCode == 4){
            val task = GoogleSignIn
                .getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class
                    .java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException){

            }
        }
    }

    //todo 7
    //authentication firebase sign in
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {

        var uid = String()
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->

                if (task.isSuccessful){
                    val user = auth?.currentUser
                    checkDatabase(task.result?.user?.uid, account)
                    uid = user?.uid.toString()

                } else{

                }
            }
    }

    //todo 8
    //check database
    private fun checkDatabase(uid: String?, account: GoogleSignInAccount?) {

        val database = FirebaseDatabase.getInstance()
        val myref = database.getReference(Constan.tb_uaser)
        val query = myref.orderByChild("uid").equalTo(auth?.uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    startActivity<MainActivity>()
                } else{
                    account?.displayName?.let {
                        account.email?.let { it1 ->
                            insertUser(it, it1,"", uid )
                        }
                    }
                }
            }
        })
    }

    //todo 9
    //menambahkan data user ke realtime database
    private fun insertUser(name: String, email: String,
                           hp: String, idUser: String?): Boolean {
        val user = Users()
        user.email = email
        user.name = name
        user.hp = hp
        user.uid = auth?.uid

        val database = FirebaseDatabase.getInstance()
        val key = database.reference.push().key
        val myref = database.getReference(Constan.tb_uaser)

        myref.child(key?: "")
            .setValue(user)

        startActivity<AuthentikasiHpActivity>(Constan.key to key)

        return true

    }

}

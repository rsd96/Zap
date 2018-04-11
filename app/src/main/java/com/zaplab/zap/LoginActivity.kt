package com.zaplab.zap

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*



/**
 * Created by Ramshad on 11/9/17.
 */
class LoginActivity: AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var callbackManager: CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setContentView(R.layout.activity_login)

        layoutLoginOptions.visibility = View.VISIBLE
        layoutEmailLogin.visibility = View.GONE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            (findViewById<ViewGroup>(R.id.cvLogin)).layoutTransition
                    .enableTransitionType(LayoutTransition.CHANGING);
        }

        btnLoginEmail.setOnClickListener({
            layoutLoginOptions.visibility = View.GONE
            layoutEmailLogin.visibility = View.VISIBLE
        })

        btn_login_mail_back.setOnClickListener({
            layoutLoginOptions.visibility = View.VISIBLE
            layoutEmailLogin.visibility = View.GONE
        })




        // Facebook login
        callbackManager = CallbackManager.Factory.create()
        btnLoginFB.setReadPermissions("email", "public_profile")
        btnLoginFB.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                result?.getAccessToken()?.let { handleFacebookAccessToken(it) }
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
            }

        })


        /// Email login
        btn_login_signup.setOnClickListener({view ->
            startActivity(Intent(this, SignUpActivity::class.java))
        })

        btn_login_reset_password.setOnClickListener({view ->
            startActivity(Intent(this, ResetPassActivity::class.java))
        })

        btn_login.setOnClickListener({view ->
            val email = et_email.getText().toString().trim()
            val password = et_password.getText().toString().trim()

            if (TextUtils.isEmpty(email))
                Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT).show()
            else if (TextUtils.isEmpty(password))
                Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT).show()
            else {
                pb_login.visibility = View.VISIBLE

                if ( auth != null) {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                pb_login.visibility = View.GONE
                                if (!task.isSuccessful) {
                                    // error
                                    if (password.length <= 6) {
                                        et_password.setError(getString(R.string.minimum_password))
                                    } else {
                                        Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                }
                            }
                }
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun handleFacebookAccessToken(token: AccessToken) {

        var credential = FacebookAuthProvider.getCredential(token.getToken())
        auth.signInWithCredential(credential).addOnCompleteListener(this, {
            if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = auth.getCurrentUser()
                if(user!= null)
                    startActivity(Intent(applicationContext, MainActivity::class.java))
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(applicationContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
            }
        })
    }
}
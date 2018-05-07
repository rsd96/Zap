package com.zaplab.zap

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.IOException

/**
 * Created by Ramshad on 4/8/18.
 */
class ProfileFragment: Fragment() {

    var user = FirebaseAuth.getInstance().currentUser


    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loadProfile()
        tvProfileMail.text = (activity?.application as Global).currentUser.email
        btnLogout.setOnClickListener({
            signout()
        })

        ivProfileImage.setOnClickListener {
            var builder = AlertDialog.Builder(activity)
            builder.setMessage("Would you like to change your profile picture ?")
            builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                chooseImage()
            })

            builder.setNegativeButton("No", null)
            builder.create().show()
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.data != null) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)
                ivProfileImage.setImageBitmap(bitmap)
                val uploadTask = FirebaseStorage.getInstance().reference.child("ProfilePics/${(activity?.application as Global).currentUser.uid}").putFile(filePath!!)
                uploadTask?.addOnFailureListener({
                    Toast.makeText(activity, "Failed to upload image!", Toast.LENGTH_SHORT).show()
                })?.addOnSuccessListener({ taskSnapshot ->
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    val downloadUrl = taskSnapshot.downloadUrl.toString()
                    Toast.makeText(activity, "Image uploaded!", Toast.LENGTH_SHORT).show()
                    var map = hashMapOf<String, Any>()
                    map.put("photoUrl", downloadUrl)
                    FirebaseDatabase.getInstance().reference.child("Users").child((activity?.application as Global).currentUser.uid).
                            updateChildren(map)
                    (activity?.application as Global).currentUser.photoUrl = downloadUrl
                })
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
    /**
     * Load and populate user profile info.
     */
    private fun loadProfile() {
        var name = (activity?.application as Global).currentUser.userName
        var imageUrl = (activity?.application as Global).currentUser.photoUrl
        Log.d("Profile Fragment", name)


        if (imageUrl != "null") {
            // Load user profile image
            Picasso.with(activity)
                    .load(imageUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(ivProfileImage, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError() {
                            // Try again online if cache failed
                            Picasso.with(activity)
                                    .load(imageUrl)
                                    .into(ivProfileImage, object : Callback {
                                        override fun onSuccess() {

                                        }

                                        override fun onError() {

                                        }
                                    })
                        }
                    })
        }

        tvProfileName.text = name
    }


    /**
     * Signout user
     */
    private fun signout() {
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        val googleSignInClient = GoogleSignIn.getClient(activity?.applicationContext!!, gso)
        googleSignInClient.signOut()
        startActivity(Intent(activity, LoginActivity::class.java))
    }
}
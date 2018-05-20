package com.zaplab.zap

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.*
import com.google.firebase.database.*
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.zaplab.zap.RecyclerItemClickListener.OnItemClickListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_history_list.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ramshad on 5/3/18.
 *
 * Page to show all the activities of the user
 */
class HistoryActivity: AppCompatActivity() {


    var transactionList = mutableListOf<Transaction>()
    var vehicleList = hashMapOf<String, String>()
    var currentUser = ""
    val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
    var rater = ""
    var ratedUser = ""
    var dbRef = FirebaseDatabase.getInstance().reference
    lateinit var adapter: HistoryListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_list)

        currentUser = (application as Global).currentUser.uid

        adapter = HistoryListRecyclerAdapter(this, transactionList, vehicleList, currentUser)
        rvHistoryList.layoutManager = LinearLayoutManager(this)


        getTransactions()

        rvHistoryList.addOnItemTouchListener(object : RecyclerItemClickListener(this,
                OnItemClickListener { view, position ->

                    var fromDate = dateTimeFormat.parse(transactionList[position].fromDate)
                    var toDate= dateTimeFormat.parse(transactionList[position].toDate)
                    var currentDate = dateTimeFormat.parse(dateTimeFormat.format(Date()))

                    var dialog = Dialog(this)
                    dialog.setContentView(R.layout.dialog_rate_report)
                    var btnRate = dialog.findViewById<TextView>(R.id.tvHistoryDialogRate)
                    var btnReport = dialog.findViewById<TextView>(R.id.tvHistoryDialogReport)

                    btnReport.setOnClickListener {
                        reportUser()
                    }

                    btnRate.setOnClickListener {
                        dialog.dismiss()
                        showRatingDialog(position, ratedUser, rater)
                    }

                    if (transactionList[position].owner == currentUser && toDate.before(currentDate)) {
                        rater = transactionList[position].owner
                        ratedUser = transactionList[position].renter
                        dialog.show()
                    } else if (transactionList[position].renter == currentUser && toDate.before(currentDate)) {
                        rater = transactionList[position].renter
                        ratedUser = transactionList[position].owner
                        dialog.show()
                    }

                })
        {})
    }

    /**
     * start report user activity
     */
    private fun reportUser() {
        startActivity(Intent(this, ReportActivity::class.java))
    }

    /**
     * Create and show rating dialog that asks user to rate and review on the user they dealt with
     */
    private fun showRatingDialog(position: Int, user: String, rater: String) {
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_rate_review)
        var profilePic = dialog.findViewById<CircleImageView>(R.id.ivRateDialogProfile)
        var userName = dialog.findViewById<TextView>(R.id.tvRateDialogName)
        var star = dialog.findViewById<RatingBar>(R.id.starRateDialog)
        var tvReview = dialog.findViewById<EditText>(R.id.etRateDialogReview)
        var btnDone = dialog.findViewById<Button>(R.id.btnRateDialogDone)
        FirebaseDatabase.getInstance().reference.child("Users").child(user).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}

            var imageUrl = ""
            override fun onDataChange(snap: DataSnapshot?) {
                if ( snap != null) {
                    imageUrl = snap.child("photoUrl").value.toString()
                    userName.text = snap.child("userName").value.toString()

                }

                Picasso.with(dialog.context)
                        .load(imageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(profilePic, object : Callback {
                            override fun onSuccess() {

                            }

                            override fun onError() {
                                // Try again online if cache failed
                                Picasso.with(dialog.context)
                                        .load(imageUrl)
                                        .into(profilePic, object : Callback {
                                            override fun onSuccess() {

                                            }

                                            override fun onError() {

                                            }
                                        })
                            }
                        })
            }

        })

        btnDone.setOnClickListener {
            var rating = star.rating
            var review = tvReview.text.trim().toString()
            var rateReview = RateReview()
            rateReview.user = user
            rateReview.rater = rater
            rateReview.rating = rating.toDouble()
            rateReview.review = review
            FirebaseDatabase.getInstance().reference.child("Ratings").push().setValue(rateReview).addOnCompleteListener {
                Toast.makeText(this, "Rating successfully added!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            // add user rating to database for easier access

            FirebaseDatabase.getInstance().reference.child("Users").child(user).runTransaction(object : com.google.firebase.database.Transaction.Handler {
                override fun onComplete(error: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                }

                override fun doTransaction(data: MutableData?): com.google.firebase.database.Transaction.Result {
                    if (data?.hasChildren()!!) {
                        var user = data.getValue(User::class.java)
                        if (user != null) {
                            user.rating += rating
                            user.rateSum++
                            data.child("rating").value  = user.rating
                            data.child("rateSum").value = user.rateSum
                        }
                   }
                    return com.google.firebase.database.Transaction.success(data)
                }
            })

        }

        dialog.show()
    }

    /**
     * Get all transactions that involved current user
     */
    private fun getTransactions() {
        FirebaseDatabase.getInstance().reference.child("Transactions").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(snap: DataSnapshot?) {
                transactionList.clear()
                if ( snap != null) {
                    for ( x in snap.children) {
                        if ( x.child("owner").value == currentUser || x.child("renter").value == currentUser) {
                            transactionList.add(x.getValue(Transaction::class.java)!!)
                        }
                    }

                        getVehicleList()
                }
            }

        })
    }


    /**
     * Get vehicles from database that were part of transactions
     */
    private fun getVehicleList() {

        FirebaseDatabase.getInstance().reference.child("Vehicles").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(snap: DataSnapshot?) {
                if ( snap != null) {
                    for ( x in snap.children) {
                        transactionList.find { it.vehicleId == x.key }?.vehicleId?.let {
                            vehicleList.put(it, "${x.child("make").value}-${x.child("model").value}")
                        }
                    }
                    rvHistoryList.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

        })
    }


}
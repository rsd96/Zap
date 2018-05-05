package com.zaplab.zap

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

/**
 * Created by Ramshad on 5/5/18.
 */
class ReviewsRecyclerAdapter (_context: Context, _list: MutableList<RateReview>) :
        RecyclerView.Adapter<ReviewsRecyclerAdapter.MyViewHolder>() {

    val myContext = _context
    var list = _list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsRecyclerAdapter.MyViewHolder {
        var v = LayoutInflater.from(myContext).inflate(R.layout.review_list_content, parent, false)
        var myViewHolder = MyViewHolder(v)
        return myViewHolder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ReviewsRecyclerAdapter.MyViewHolder, position: Int) {


        var imageUrl = ""
        FirebaseDatabase.getInstance().reference.child("Users").child(list[position].rater).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(snap: DataSnapshot?) {
                if ( snap != null) {
                    imageUrl = snap.child("photoUrl").value.toString()

                }

                Picasso.with(myContext)
                        .load(imageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.image, object : Callback {
                            override fun onSuccess() {

                            }

                            override fun onError() {
                                // Try again online if cache failed
                                Picasso.with(myContext)
                                        .load(imageUrl)
                                        .into(holder.image, object : Callback {
                                            override fun onSuccess() {

                                            }

                                            override fun onError() {

                                            }
                                        })
                            }
                        })
            }
        })

        holder?.message.text = list[position].review
        holder.rating.text = list[position].rating.toString()
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivReviewProfile)
        val message: TextView = view.findViewById(R.id.tvReviewMessage)
        val rating: TextView = view.findViewById(R.id.tvReviewRating)
    }
}
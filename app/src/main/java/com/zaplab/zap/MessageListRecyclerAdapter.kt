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
 * Created by Ramshad on 4/24/18.
 */
class MessageListRecyclerAdapter(_context: Context, _list: MutableList<Channel>, _currentUser: String) :
        RecyclerView.Adapter<MessageListRecyclerAdapter.MyViewHolder>() {

    val myContext = _context
    var list = _list
    var currentUser = _currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListRecyclerAdapter.MyViewHolder {
        var v = LayoutInflater.from(myContext).inflate(R.layout.message_list_content, parent, false)
        var myViewHolder = MyViewHolder(v)
        return myViewHolder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MessageListRecyclerAdapter.MyViewHolder, position: Int) {


        var toUser = if (list[position].user1 == currentUser) list[position].user2 else list[position].user1
        var imageUrl = ""
        var name = ""
        FirebaseDatabase.getInstance().reference.child("Users").child(toUser).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(snap: DataSnapshot?) {
                if ( snap != null) {
                    imageUrl = snap.child("photoUrl").value.toString()
                    name = snap.child("userName").value.toString()

                }

                Picasso.with(myContext)
                        .load(imageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.profileImage, object : Callback {
                            override fun onSuccess() {

                            }

                            override fun onError() {
                                // Try again online if cache failed
                                Picasso.with(myContext)
                                        .load(imageUrl)
                                        .into(holder.profileImage, object : Callback {
                                            override fun onSuccess() {

                                            }

                                            override fun onError() {

                                            }
                                        })
                            }
                        })


                holder?.userName.text = name

            }

        })
        holder.lastMessage.text= list[position].lastMessage
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.ivMessageListPic)
        val userName: TextView = view.findViewById(R.id.tvMessageListName)
        val lastMessage: TextView = view.findViewById(R.id.tvMessageListMessage)
    }
}
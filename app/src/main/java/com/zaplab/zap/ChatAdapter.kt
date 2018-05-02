package com.zaplab.zap

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by Ramshad on 4/23/18.
 */
class ChatAdapter(private var context: Context, private var chatList: MutableList<Message>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val CHAT_END = 1
    private val CHAT_START = 2
    var TAG = "CarsRecycleAdapter"

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTextView.text = chatList[position].message
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val v: View

        if (viewType === CHAT_END) {
            v = LayoutInflater.from(parent?.context).inflate(R.layout.chat_item_receive, parent, false)
        } else {
            Log.d(TAG, "message sent ...")
            v = LayoutInflater.from(parent?.context).inflate(R.layout.chat_item_send, parent, false)
        }

        return ViewHolder(v)
    }

    override fun getItemViewType(position: Int): Int {
        if (chatList[position].from == FirebaseAuth.getInstance().currentUser?.uid) {
            Log.d(TAG, "returning ${CHAT_START}")
            return CHAT_START
        }

        Log.d(TAG, "returning ${CHAT_END}")
        return CHAT_END
    }

    override fun getItemCount(): Int = chatList.size

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var mTextView: TextView

        init {
            mTextView = itemView.findViewById<View>(R.id.tvMessage) as TextView
        }
    }

    fun refreshData(membership_list: MutableList<Message>) {
        this.chatList = membership_list
        notifyDataSetChanged()
    }
}
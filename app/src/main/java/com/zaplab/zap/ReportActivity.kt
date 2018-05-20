package com.zaplab.zap

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_report.*

/**
 * Created by Ramshad on 5/7/18.
 *
 * Page to let user report another user
 */
class ReportActivity: AppCompatActivity(),  View.OnClickListener{

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        btnReportCancel.setOnClickListener(this)
        btnReportSend.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnReportSend -> sendEmail()

            R.id.btnReportCancel -> finish()
        }
    }

    /**
     * Get subect and message from user and launch default email app to send a mail to admin
     */
    private fun sendEmail() {
        if (etEmailSubject!!.text.toString().trim().equals("", ignoreCase = true))
            etEmailSubject.error = "Enter a subject !"
        else if (etEmailBody!!.text.toString().trim().equals("", ignoreCase = true))
            etEmailBody.error = "Enter a message !"
        else {
            val subject = etEmailSubject.text.toString()
            val body = etEmailBody.text.toString()
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:")
            // admin mail, change later
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("rmshdbasheer@gmail.com"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            emailIntent.putExtra(Intent.EXTRA_TEXT, body)
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent)
            }
        }
    }
}
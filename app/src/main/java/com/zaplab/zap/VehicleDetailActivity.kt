package com.zaplab.zap

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_vehicle_detail.*
import org.joda.time.DateTime
import org.joda.time.Hours
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Ramshad on 4/21/18.
 */
class VehicleDetailActivity: AppCompatActivity() {

    private val TAG = "VehicleDetailAcitvity"
    var dbRef = FirebaseDatabase.getInstance().reference
    lateinit var userName: String
    var vehicle = Vehicle()
    lateinit var fromDate: Date
    lateinit var toDate: Date
    var isInsurancePayDamage = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)


        vehicle = intent.getSerializableExtra("vehicle") as Vehicle

        var imagePagerAdapter = VehicleDetailPagerAdapter(this, vehicle.imageList)
        viewPagerVehicleDetail.adapter = imagePagerAdapter
        indicatorVehicleDetail.setViewPager(viewPagerVehicleDetail)
        imagePagerAdapter.registerDataSetObserver(indicatorVehicleDetail.dataSetObserver)

        setSupportActionBar(toolbarDetailActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        collapsingToolbarVehicleDetail.title = "${vehicle.make} - ${vehicle.model}"
        collapsingToolbarVehicleDetail.setCollapsedTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        collapsingToolbarVehicleDetail.setExpandedTitleTextColor(ColorStateList.valueOf(0xffffff))

        // Start message activity
        fabVehicleDetailMessage.setOnClickListener({
            var intent = Intent(this, ChatActivity::class.java)
            // Pass id of vehicle owner
            intent.putExtra("TO_USER", vehicle.ownerId)
            startActivity(intent)
        })


        loadOwnerProfile()

        btnVehicleDetailAvail.setOnClickListener({
            showVehicleAvailability()
        })

        btnVehicleDetailLocation.setOnClickListener({
            showVehicleLocation()
        })

        btnVehicleDetailRent.setOnClickListener({
            startBooking()
        })

        // Populate vehicle details
        tvVehicleDetailDesc.text = "\"${vehicle.desc} \""
        tvVehicleDetailTrans.text = vehicle.transmission.toString()
        tvVehicleDetailMileage.text = vehicle.odometer.toString()
        tvVehicleDetailBond.text = "$${vehicle.bond}"
        tvVehicleDetailRent.text = "$${vehicle.rent}"



    }

    val RQC_PICK_DATE_TIME_RANGE = 111
    /**
     * Start booking process to rent a car
     */
    private fun startBooking() {
        showFromDatePicker()
    }

    private fun showFromDatePicker() {
        // Initialize
        val dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                "From date",
                "OK",
                "Cancel"
        )

// Assign values
        dateTimeFragment.startAtCalendarView()
        dateTimeFragment.set24HoursMode(true)
        dateTimeFragment.setMinimumDateTime(GregorianCalendar(2018, Calendar.JANUARY, 1).getTime())
        dateTimeFragment.setMaximumDateTime(GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime())
        dateTimeFragment.setDefaultDateTime(GregorianCalendar(2018, Calendar.MAY, 4, 15, 20).getTime())
// Or assign each element, default element is the current moment
// dateTimeFragment.setDefaultHourOfDay(15);
// dateTimeFragment.setDefaultMinute(20);
// dateTimeFragment.setDefaultDay(4);
// dateTimeFragment.setDefaultMonth(Calendar.MARCH);
        dateTimeFragment.setDefaultYear(2018)

// Define new day and month format
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(SimpleDateFormat("dd MMMM", Locale.getDefault()))
        } catch (e: SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException) {

        }


// Set listener
        dateTimeFragment.setOnButtonClickListener(object : SwitchDateTimeDialogFragment.OnButtonClickListener {
            override fun onPositiveButtonClick(date: Date) {
                fromDate = date
                showToDatePicker()

            }

            override fun onNegativeButtonClick(date: Date) {
                // Date is get on negative button click
            }
        })

// Show
        dateTimeFragment.show(supportFragmentManager, "dialog_time")
    }

    private fun showToDatePicker() {
        // Initialize
        val dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                "To date",
                "OK",
                "Cancel"
        )

// Assign values
        dateTimeFragment.startAtCalendarView()
        dateTimeFragment.set24HoursMode(true)
        dateTimeFragment.setMinimumDateTime(GregorianCalendar(2018, Calendar.JANUARY, 1).getTime())
        dateTimeFragment.setMaximumDateTime(GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime())
        dateTimeFragment.setDefaultDateTime(GregorianCalendar(2018, Calendar.MAY, 4, 15, 20).getTime())
// Or assign each element, default element is the current moment
// dateTimeFragment.setDefaultHourOfDay(15);
// dateTimeFragment.setDefaultMinute(20);
// dateTimeFragment.setDefaultDay(4);
// dateTimeFragment.setDefaultMonth(Calendar.MARCH);
        dateTimeFragment.setDefaultYear(2018)

// Define new day and month format
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(SimpleDateFormat("dd MMMM", Locale.getDefault()))
        } catch (e: SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException) {

        }


// Set listener
        dateTimeFragment.setOnButtonClickListener(object : SwitchDateTimeDialogFragment.OnButtonClickListener {
            override fun onPositiveButtonClick(date: Date) {
                toDate = date
                showDamageDialog()
            }

            override fun onNegativeButtonClick(date: Date) {
                // Date is get on negative button click
            }
        })

// Show
        dateTimeFragment.show(supportFragmentManager, "dialog_time")
    }

    /**
     * Show user options to pay for damage claims
     */
    private fun showDamageDialog() {
        var dialog = Dialog(this@VehicleDetailActivity)
        val view = layoutInflater?.inflate(R.layout.dialog_damage_insurance, null)
        dialog.setContentView(view)
        var viewPayMyself = dialog.findViewById<View>(R.id.viewDamagePayMyself)
        var viewPayInsurance = dialog.findViewById<View>(R.id.viewDamageInsurance)
        var btnConfirm = dialog.findViewById<Button>(R.id.btnDamageConfirm)
        dialog.window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        viewPayMyself.setOnClickListener({
            viewPayMyself.setBackgroundColor(ContextCompat.getColor(this@VehicleDetailActivity, R.color.colorPrimaryAlpha))
            viewPayInsurance.setBackgroundColor(ContextCompat.getColor(this@VehicleDetailActivity, android.R.color.transparent))
            isInsurancePayDamage = false
        })

        viewPayInsurance.setOnClickListener({
            viewPayInsurance.setBackgroundColor(ContextCompat.getColor(this@VehicleDetailActivity, R.color.colorPrimaryAlpha))
            viewPayMyself.setBackgroundColor(ContextCompat.getColor(this@VehicleDetailActivity, android.R.color.transparent))
            isInsurancePayDamage = true
        })
        btnConfirm.setOnClickListener({
            // show money breakdown
            dialog.dismiss()
            showBreakdownDialog()
        })
        dialog.show()
    }


    /**
     * Show dialog that shows user the amount they will have to pay
     */
    private fun showBreakdownDialog() {
        var dialog = Dialog(this@VehicleDetailActivity)
        val view = layoutInflater?.inflate(R.layout.dialog_damage_breakdown, null)
        dialog.window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        dialog.setContentView(view)
        var carName = dialog.findViewById<TextView>(R.id.tvCheckoutName)
        var from = dialog.findViewById<TextView>(R.id.tvCheckoutFrom)
        var to = dialog.findViewById<TextView>(R.id.tvCheckoutTo)
        var rent = dialog.findViewById<TextView>(R.id.tvCheckoutRent)
        var insurance = dialog.findViewById<TextView>(R.id.tvCheckoutInsurance)
        var total = dialog.findViewById<TextView>(R.id.tvCheckoutTotal)
        var insuranceAmount = dialog.findViewById<TextView>(R.id.tvCheckoutInsuranceAmount)
        var btnPay = dialog.findViewById<Button>(R.id.btnCheckoutPay)

        carName.text = "${vehicle.make}-${vehicle.model}"
        from.text = SimpleDateFormat("dd:MM:yy - HH:mm:ss").format(fromDate)
        to.text = SimpleDateFormat("dd:MM:yy - HH:mm:ss").format(toDate)
        //var difference = Interval(DateTime(fromDate), DateTime(toDate))
        var hObj = Hours.hoursBetween(DateTime(fromDate).withTimeAtStartOfDay(), DateTime(toDate).withTimeAtStartOfDay())
        var hours = hObj.hours
        var totalAmount = 0.00
        Log.d(TAG, "hours between = $hours")
        var calcedRent = hours*vehicle.rent
        totalAmount += calcedRent
        rent.text = "$hours x ${vehicle.rent} = $calcedRent"
        if (!isInsurancePayDamage) {
            insurance.visibility = View.GONE
            insuranceAmount.visibility = View.GONE
        } else {

            totalAmount += 35
        }

        total.text = "$totalAmount"

        btnPay.setOnClickListener({
            // start credit card dialog
            showPaymentDialog()
        })
        dialog.show()
    }

    private fun showPaymentDialog() {

    }

    /**
     * Get info of owner from database and populate the views
     * */
    private fun loadOwnerProfile() {
        // Set owner's user name and profile picture
        dbRef.child("Users").child(vehicle.ownerId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(snap: DataSnapshot?) {
                userName = snap?.child("userName")?.value.toString()
                var imageUri = snap?.child("photoUrl")?.value.toString()
                tvVehicleDetailUserName.text = userName

                Picasso.with(applicationContext)
                        .load(imageUri)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(ivVehicleDetailProfile, object : Callback {
                            override fun onSuccess() {}

                            override fun onError() {
                                // Try again online if cache failed
                                Picasso.with(applicationContext)
                                        .load(imageUri)
                                        .into(ivVehicleDetailProfile, object : Callback {
                                            override fun onSuccess() {}

                                            override fun onError() {}
                                        })
                            }
                        })
            }
        })
    }

    /**
     * Show dialog that displays the location of selected vehicle
     * */
    private fun showVehicleLocation() {

            var dialog = Dialog(this)
            val view = this.layoutInflater?.inflate(R.layout.dialog_car_detail_location, null)
            dialog.setContentView(view)
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            OnMapAndViewReadyListener(mapFragment, object : OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener{
                override fun onMapReady(googleMap: GoogleMap?) {
                    if (googleMap != null) {

                        var mMap: GoogleMap = googleMap
                        val vehicleLocation = LatLng(vehicle.lat, vehicle.long)
                        mMap.addMarker(MarkerOptions()
                                .position(vehicleLocation)
                                .title(vehicle.city))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vehicleLocation, 10f))
                    }
                }
            })
            dialog.show()

    }

    /**
     * Show dialog that displays the available timing of selected vehicle when user clicks the button
     * */
    private fun showVehicleAvailability() {

            var dialog = Dialog(this)
            val view = this.layoutInflater?.inflate(R.layout.dialog_car_detail_availability, null)
            dialog.setContentView(view)
            var mondayText = dialog.findViewById<TextView>(R.id.mondayText)
            var tuesText = dialog.findViewById<TextView>(R.id.tuesdayText)
            var wedText = dialog.findViewById<TextView>(R.id.wednesdayText)
            var thursText = dialog.findViewById<TextView>(R.id.thursdayText)
            var friText = dialog.findViewById<TextView>(R.id.fridayText)
            var satText = dialog.findViewById<TextView>(R.id.saturdayText)
            var sunText = dialog.findViewById<TextView>(R.id.sundayText)
            mondayText.text = vehicle.availability.monday
            tuesText.text = vehicle.availability.tuesday
            wedText.text = vehicle.availability.wednesday
            thursText.text = vehicle.availability.thursday
            friText.text = vehicle.availability.friday
            satText.text = vehicle.availability.saturday
            sunText.text = vehicle.availability.sunday
            dialog.show()
    }
}
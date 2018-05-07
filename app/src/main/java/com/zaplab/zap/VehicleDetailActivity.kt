package com.zaplab.zap

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.InflateException
import android.view.View
import android.widget.*
import com.cooltechworks.creditcarddesign.CreditCardView
import com.dx.dxloadingbutton.lib.LoadingButton
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
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
    var vehicleId = ""
    lateinit var fromDate: DateTime
    lateinit var toDate: DateTime
    var isInsurancePayDamage = false
    var listOfCards = mutableListOf<CreditCard>()
    var transaction = Transaction()
    var reviewList = mutableListOf<RateReview>()

    val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)


        vehicle = intent.getSerializableExtra("vehicle") as Vehicle
        vehicleId = intent.getStringExtra("vehicleId")

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

        loadReviews()

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
        tvVehicleDetailRent.text = "$${vehicle.rent}"
    }

    private fun loadReviews() {
        var adapter = ReviewsRecyclerAdapter(applicationContext, reviewList)
        rvVehicleDetailReviews.layoutManager = LinearLayoutManager(this)
        dbRef.child("Ratings").addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(snap: DataSnapshot?) {
                        if ( snap != null) {
                            for ( x in snap.children) {
                                var r = x.getValue(RateReview::class.java)
                                Log.d(TAG, r.toString())
                                if (r?.user == vehicle.ownerId)
                                    reviewList.add(r)
                            }
                            if (reviewList.isEmpty()) {
                                tvVehicleDetailReviews.visibility = View.GONE
                            } else {
                                tvVehicleDetailReviews.visibility = View.VISIBLE
                            }
                            rvVehicleDetailReviews.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }

                    }

                })
    }

    /**
     * Start booking process to rent a car
     */
    private fun startBooking() {
        transaction.transactionDate = dateTimeFormat.format(Date())
        transaction.owner = vehicle.ownerId
        transaction.renter = (application as Global).currentUser.uid
        transaction.vehicleId = vehicleId
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
                fromDate = DateTime(date)
                transaction.fromDate = dateTimeFormat.format(date)
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
                toDate = DateTime(date)
                transaction.toDate = dateTimeFormat.format(date)
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
            transaction.isDamagePaidByInsurance = false
        })

        viewPayInsurance.setOnClickListener({
            viewPayInsurance.setBackgroundColor(ContextCompat.getColor(this@VehicleDetailActivity, R.color.colorPrimaryAlpha))
            viewPayMyself.setBackgroundColor(ContextCompat.getColor(this@VehicleDetailActivity, android.R.color.transparent))
            isInsurancePayDamage = true
            transaction.isDamagePaidByInsurance = true
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
        dialog.setContentView(view)
        dialog.window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        var carName = dialog.findViewById<TextView>(R.id.tvCheckoutName)
        var from = dialog.findViewById<TextView>(R.id.tvCheckoutFrom)
        var to = dialog.findViewById<TextView>(R.id.tvCheckoutTo)
        var rent = dialog.findViewById<TextView>(R.id.tvCheckoutRent)
        var insurance = dialog.findViewById<TextView>(R.id.tvCheckoutInsurance)
        var total = dialog.findViewById<TextView>(R.id.tvCheckoutTotal)
        var insuranceAmount = dialog.findViewById<TextView>(R.id.tvCheckoutInsuranceAmount)
        var btnPay = dialog.findViewById<Button>(R.id.btnCheckoutPay)

        carName.text = "${vehicle.make}-${vehicle.model}"
        from.text = SimpleDateFormat("dd/MM/yy - HH:mm").format(fromDate)
        to.text = SimpleDateFormat("dd/MM/yy - HH:mm").format(toDate)
        //var difference = Interval(DateTime(fromDate), DateTime(toDate))
        var hObj = Hours.hoursBetween(DateTime(fromDate).withTimeAtStartOfDay(), DateTime(toDate).withTimeAtStartOfDay())
        var hours = hObj.hours
        var totalAmount = 0.00
        Log.d(TAG, "hours between = $hours")
        var calcedRent = hours*vehicle.rent
        totalAmount += calcedRent
        rent.text = "$hours x ${vehicle.rent} = $$calcedRent"
        if (!isInsurancePayDamage) {
            insurance.visibility = View.GONE
            insuranceAmount.visibility = View.GONE
        } else {

            totalAmount += 35
        }

        total.text = "$totalAmount"

        transaction.amount = totalAmount

        btnPay.setOnClickListener({
            // start credit card dialog
            dialog.dismiss()
            showPaymentDialog()
        })
        dialog.show()
    }

    private fun showPaymentDialog() {
        var dialog = Dialog(this@VehicleDetailActivity)
        val view = layoutInflater?.inflate(R.layout.dialog_transaction_pay, null)
        dialog.setContentView(view)
        dialog.window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        var viewPager = dialog.findViewById<ViewPager>(R.id.viewPagerTransactionPay)
        loadCreditCards(viewPager)
        var selectCard = dialog.findViewById<LoadingButton>(R.id.btnTransactionPaySelect)
        var addCard = dialog.findViewById<FloatingActionButton>(R.id.fabTransactionPay)
        var selectedCard = dialog.findViewById<CreditCardView>(R.id.selectedCardTransactionPay)
        var btnFinish = dialog.findViewById<LoadingButton>(R.id.btnTransactionPayFinish)

        addCard.setOnClickListener({
            AddCreditCardDialog.showDialog(this@VehicleDetailActivity)
        })

        selectCard.setOnClickListener({
            var card = listOfCards[viewPager.currentItem]
            selectedCard.visibility = View.VISIBLE
            selectedCard.cardNumber = card.number
            selectedCard.cardHolderName = card.name
            selectedCard.setCardExpiry(card.exp)
            selectedCard.setCVV(card.cvv)
            viewPager.visibility = View.GONE
            selectCard.visibility = View.GONE
            addCard.visibility = View.GONE
            btnFinish.visibility = View.VISIBLE
            transaction.renterCard = card.number
        })

        btnFinish.setOnClickListener({
            btnFinish.startLoading()
            var map = hashMapOf<String, String>()
            map.put("from", dateTimeFormat.format(fromDate))
            map.put("to", dateTimeFormat.format(toDate))
            FirebaseDatabase.getInstance().reference.child("Bookings").child(vehicleId).push().setValue(map)
            FirebaseDatabase.getInstance().reference.child("Transactions").push().setValue(transaction).addOnCompleteListener {
                btnFinish.loadingSuccessful()
                dialog.dismiss()
            }
        })
        dialog.show()
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

        lateinit var view: View
        var dialog = Dialog(this)
        try {
            view = this.layoutInflater?.inflate(R.layout.dialog_car_detail_location, null)!!
        } catch (e: InflateException) {
            /* map is already there, just return view as it is */
        }

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
        var bookingList = dialog.findViewById<ListView>(R.id.lvCurrentBookings)
        var currentBookingList = mutableListOf<String>()
        var transactionList = mutableListOf<Transaction>()
        if (vehicle.availability.monday == "Not Available")
            mondayText.background.mutate().setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN)
        if (vehicle.availability.tuesday == "Not Available")
            tuesText.background.mutate().setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN)
        if (vehicle.availability.wednesday == "Not Available")
            wedText.background.mutate().setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN)
        if (vehicle.availability.thursday == "Not Available")
            thursText.background.mutate().setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN)
        if (vehicle.availability.friday == "Not Available")
            friText.background.mutate().setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN)
        if (vehicle.availability.saturday == "Not Available")
            satText.background.mutate().setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN)
        if (vehicle.availability.sunday == "Not Available")
            sunText.background.mutate().setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN)

        dbRef.child("Transactions").addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(snap: DataSnapshot?) {
                        if ( snap != null) {
                            for ( x in snap.children) {
                                if(x.child("vehicleId").value.toString() == vehicleId) {
                                    transactionList.add(x.getValue(Transaction::class.java)!!)
                                }
                            }

                            var currentDate = DateTime()
                            var toDate = DateTime()
                            var it = transactionList.iterator()
                            while (it.hasNext()) {
                                var t = it.next()
                                toDate = DateTime(dateTimeFormat.parse(t.toDate))
                                if (toDate.isBeforeNow) {
                                    it.remove()
                                } else {
                                    currentBookingList.add("${t.fromDate} -> ${t.toDate}")
                                }
                            }

                            bookingList.adapter = ArrayAdapter<String>(this@VehicleDetailActivity, android.R.layout.simple_list_item_1, currentBookingList)

                        }

                    }

                })
        dialog.show()
    }


    /**
     * Load all credit cards belonging to this user
     */
    private fun loadCreditCards(viewPager: ViewPager) {
        dbRef.child("Cards").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(snap: DataSnapshot?) {
                if (snap != null) {
                    listOfCards.clear()
                    for (x in snap.children) {
                        var creditCard = x.getValue(CreditCard::class.java)
                        if (creditCard?.cardHolder?.equals(FirebaseAuth.getInstance().currentUser?.uid)!!)
                            creditCard.let { listOfCards.add(it) }
                        var adapter = CreditCardListAdapter(this@VehicleDetailActivity, listOfCards)
                        viewPager.adapter = adapter
                        viewPager.clipToPadding = false
                        viewPager.setPadding(100, 0, 100, 0)
                    }
                }
            }

        })
    }
}
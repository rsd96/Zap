package com.zaplab.zap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dummy.*
import java.util.*

/**
 * Created by Ramshad on 5/14/18.
 */
class DummyData : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dummy)


        var profiles = mutableListOf("https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/ProfilePics%2Fdownload.jpeg?alt=media&token=e992f91c-d8e5-4eee-aefb-d29ee30375f7",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/ProfilePics%2Fpexels-photo-733872.jpeg?alt=media&token=62c575fa-1676-4dcc-ad4b-11dc1cf66bbd",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/ProfilePics%2Fpp.jpeg?alt=media&token=1ca5f958-3107-4e89-93a4-dce0951b4f2a",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/ProfilePics%2Fprofile-pictures-50422.jpg?alt=media&token=9f7faa56-27be-4039-8406-6ce652faeaa0",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/ProfilePics%2Fwerwer.jpeg?alt=media&token=37c0f0ab-34db-4fe9-ae6f-d8b9601336f8")

        var vehiclePics = arrayListOf("https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2F2Q%3D%3D.jpg?alt=media&token=b372167b-34f9-4260-950a-f2bdcaaf9aa2",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2F9k%3D%20(2).jpg?alt=media&token=8d23af51-fe6a-4b2d-a439-e45604110dc7",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2F9k%3D%20(3).jpg?alt=media&token=5fddef63-6b57-41ff-af07-c6dd5fc25c9c",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2F9k%3D.jpg?alt=media&token=3211a578-d093-4e30-ac78-fae1e7bbba12",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2Fimages%20(1).jpg?alt=media&token=68d58cf6-eeab-49b4-aabc-decd4aa96be0",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2Fimages%20(2).jpg?alt=media&token=ff69bcbe-cd94-4a09-ba8e-8686f3123ea6",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2Fimages%20(3).jpg?alt=media&token=ebcd7885-ccc5-4bad-b778-94f111caf90d",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2Fimages%20(4).jpg?alt=media&token=67ea22ad-4b3d-432a-ac74-78cd333e374e",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2Fimages%20(5).jpg?alt=media&token=3a1f5fc3-7eca-4cd5-bae3-5e824d8df413",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2Fimages%20(6).jpg?alt=media&token=16db6133-a3e7-4fa2-8af6-98709d04288a",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2Fimages%20(7).jpg?alt=media&token=108fed53-caa0-4537-be50-4316219dcc11",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2Fac1.jpg?alt=media&token=592390d1-9059-4a0f-b776-62eb01b16ad1",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2Fac2.jpg?alt=media&token=241f5c90-de7a-45d5-a636-a070955b7aa4",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2Fac3.jpg?alt=media&token=deeacf51-afa9-44fc-9e5d-b197859c74cd",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2Fac4.jpg?alt=media&token=37343fdb-7b7a-484d-9f7e-966e5a3de9a8",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2Fac5.jpg?alt=media&token=04cddfbd-e681-4a45-a237-24a2f635af35",
                "https://firebasestorage.googleapis.com/v0/b/zaptheapp-4a3f7.appspot.com/o/Vehicles%2Fac6.jpg?alt=media&token=b611e6ba-93f7-4f81-977f-762b9e551e64")


        var avail = AvailableDates()
        avail.sunday = "All Day"
        avail.monday = "All Day"
        avail.tuesday = "All Day"
        avail.wednesday = "All Day"
        avail.thursday = "All Day"
        avail.friday = "All Day"
        avail.saturday = "All Day"

        var creditNumber = "1762839462074978"
        var creditExp = "0921"
        var creditCVV = "398"

        var lat = -34.4278121
        var long = 150.8930607
        var odometer = 20000

        var userList = mutableListOf<User>()
        var vehicleIdList = mutableListOf<Vehicle>()

        var amenities = listOf<String>("AUX", "Air Condition", "Charging port")

        var countryList = hashMapOf<String, MutableList<String>>()
        countryList.put("Australia", mutableListOf("Sydney", "Wollongong", "Darwin", "Melbourne", "Canberra"))
        countryList.put("UAE", mutableListOf("Dubai", "Abu Dhabi"))
        countryList.put("New Zealand", mutableListOf("Auckland", "Hamilton"))


        var makeModel = hashMapOf<String, MutableList<String>>()
        makeModel.put("Honda", mutableListOf("Accord", "Civic", "Veloster", "CR-V"))
        makeModel.put("Toyota", mutableListOf("Corolla", "Land Cruiser", "Yaris"))
        makeModel.put("Nissan", mutableListOf("Micra", "GTR"))
        makeModel.put("Bugatti", mutableListOf("Veyron"))
        makeModel.put("Jeep", mutableListOf("Renegade"))
        makeModel.put("Hyundai", mutableListOf("SantaFe"))

        var descList = mutableListOf("Guaranteed performance",
                "Best car in the world",
                "This car never disappoints",
                "Wakanda forever!")


        var ownerVehicles = hashMapOf<String, MutableList<String>>()

        FirebaseDatabase.getInstance().reference.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(snap: DataSnapshot?) {
                if (snap != null) {
                    for (x in snap.children) {
                        var user = x.getValue(User::class.java)
                        user?.uid = x.key
                        userList.add(user!!)
                        ownerVehicles.put(user.uid, mutableListOf())
                    }
                }
                Toast.makeText(this@DummyData, "Got them motherfucking users!!", Toast.LENGTH_SHORT).show()

//                FirebaseDatabase.getInstance().reference.child("Vehicles").addValueEventListener(object : ValueEventListener{
//                    override fun onCancelled(p0: DatabaseError?) {}
//
//                    override fun onDataChange(snap: DataSnapshot?) {
//                        if ( snap != null) {
//                            for ( x in snap.children) {
//                                var vehicle = x.getValue(Vehicle::class.java)!!
//                                var array = ownerVehicles[vehicle.ownerId]
//                                array?.add(x.key)
//                                ownerVehicles[vehicle.ownerId]?.addAll(array!!)
//                            }
//                        }
//                        Toast.makeText(this@DummyData, "Got them bitchass rides!!", Toast.LENGTH_SHORT).show()
//                    }
//                })
            }
        })


        var fromTo = hashMapOf<String, String>()
        fromTo.put("20/05/2018 15:00", "20/05/2018 22:00")
        fromTo.put("20/05/2018 17:00", "20/05/2018 20:00")
        fromTo.put("20/05/2018 13:00", "21/05/2018 05:00")
        fromTo.put("23/05/2018 13:00", "25/05/2018 22:00")
        fromTo.put("24/05/2018 15:00", "24/05/2018 22:00")
        fromTo.put("24/05/2018 15:00", "25/05/2018 18:00")
        fromTo.put("25/05/2018 15:00", "26/05/2018 22:00")

        var transList = mutableListOf("15/05/2018", "16/05/2018", "17/05/2018", "18/05/2018", "19/05/2018")

        var reviewRating = hashMapOf<Double, String>()
        reviewRating.put(1.0, "Horrible experience ! ")
        reviewRating.put(2.0, "Very rude..")
        reviewRating.put(2.5, "Vehicle did not match exactly with the description")
        reviewRating.put(3.0, "Okay experience")
        reviewRating.put(4.0, "Friendly, but takes a while to reply")
        reviewRating.put(4.5, "No complaints")
        reviewRating.put(5.0, "Great experience, very friendly")


        var rand = Random()
        rand.setSeed(1234567)
        magicButton.setOnClickListener({
            var done = false


            for (i in 0..1000) {

                var rateReview = RateReview()
                var rateId = rand.nextInt(reviewRating.keys.size)
                rateReview.rating = reviewRating.keys.elementAt(rateId)
                rateReview.review = reviewRating[rateReview.rating].toString()
                var raterId = rand.nextInt(userList.size)
                var rater = userList[raterId]
                rateReview.rater = rater.uid
                var userId = rand.nextInt(userList.size)
                var user = userList[userId]
                while (user == rater) {
                    userId = rand.nextInt(userList.size)
                    user = userList[userId]
                }
                rateReview.user = user.uid


                Log.d("Dummy", user.uid)

                FirebaseDatabase.getInstance().reference.child("Users").child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(x: DataSnapshot?) {

                        if ( x != null) {
                            var rating: Double = x.child("rating").value?.toString()?.toDouble()!!
                            var rateSum: Int = x.child("rateSum").value?.toString()?.toInt()!!
                            var map = hashMapOf<String, Any>()
                            map.put("rating", (rating + rateReview.rating))
                            map.put("rateSum", ++rateSum)
                            FirebaseDatabase.getInstance().reference.child("Users").child(x.key).updateChildren(map).addOnCompleteListener({
                                FirebaseDatabase.getInstance().reference.child("Ratings").push().setValue(rateReview)
                                done = true
                            })
                        }

                    }


                })


            }


        })
    }
}





/*
    fun generateVehicles() {
        var vehicle = Vehicle()
        vehicle.amenities = amenities
        vehicle.availability = avail
        var rent = rand.nextInt(35) + 20
        vehicle.rent = rent.toDouble()
        var makeId = rand.nextInt(makeModel.size)
        vehicle.make = makeModel.keys.elementAt(makeId)
        var modelId = makeModel[makeModel.keys.elementAt(makeId)]?.size?.let { it1 -> rand.nextInt(it1) }
        vehicle.model = makeModel[makeModel.keys.elementAt(makeId)]?.get(modelId!!)!!
        var countryId = rand.nextInt(countryList.size)
        vehicle.country = countryList.keys.elementAt(countryId)
        var cityId = rand.nextInt(countryList[countryList.keys.elementAt(countryId)]?.size!!)
        vehicle.city = countryList[countryList.keys.elementAt(countryId)]?.get(cityId)!!
        var ownerId = rand.nextInt(userList.size)
        var user = userList[ownerId]
        vehicle.ownerId = user.uid
        var card = CreditCard()
        card.cardHolder = user.uid
        card.cvv = creditCVV
        card.exp = creditExp
        card.number = creditNumber
        card.name = user.userName
        vehicle.card = card
        var descId = rand.nextInt(descList.size)
        vehicle.desc = descList[descId]
        vehicle.plate = "G123"
        vehicle.lat = lat
        vehicle.long = long
        var transId = rand.nextInt(2)
        if (transId == 0 )
            vehicle.transmission = com.zaplab.zap.Transmission.Manual
        else if (transId == 1)
            vehicle.transmission = com.zaplab.zap.Transmission.Auto
        vehicle.odometer = odometer
        vehicle.color = -65536
        vehiclePics.shuffle()
        vehicle.imageList = ArrayList(vehiclePics.subList(0, 4))

        FirebaseDatabase.getInstance().reference.child("Vehicles").push().setValue(vehicle)
    }
    */





// REset ratereview
//FirebaseDatabase.getInstance().reference.child("Users").addValueEventListener(object : ValueEventListener{
//    override fun onCancelled(p0: DatabaseError?) {
//    }
//
//    override fun onDataChange(snap: DataSnapshot?) {
//        if ( snap != null) {
//            for ( x in snap.children) {
//                var map = hashMapOf<String, Any>()
//                map.put("rating", 0)
//                map.put("rateSum", 0)
//                FirebaseDatabase.getInstance().reference.child("Users").child(x.key).updateChildren(map)
//            }
//        }
//
//    }
//
//})




/*
 var names = mutableListOf<String>( "Avril", "Avrit", "Ayn", "Bab", "Babara", "Babb", "Babbette", "Babbie", "Babette", "Babita", "Babs", "Bambi", "Catherine"
            ,"Cathi", "Cathie", "Cathleen", "Cathlene", "Cathrin",
            "Cathrine"
            ,
            "Cathryn"
            ,
            "Cathy"
            ,
            "Cathyleen"
            ,
            "Cati"
            ,
            "Catie"
            ,
            "Catina"
            ,
            "Catlaina"
            ,
            "Catlee"
            ,
            "Catlin"
            ,
            "Catrina"
            ,
            "Catriona"
            ,
            "Caty"
            ,
            "Caye"
            ,
            "Cayla",
            "Dusty"
            ,
            "Dyan"
            ,
            "Dyana"
            ,
            "Dyane"
            ,
            "Dyann"
            ,
            "Dyanna"
            ,
            "Dyanne"
            ,
            "Dyna"
            ,
            "Dynah"
            ,
            "Eachelle"
            ,
            "Eada"
            ,
            "Eadie"
            ,
            "Eadith"
            ,
            "Ealasaid"
            ,
            "Eartha"
            ,
            "Easter"
            ,
            "Eba"
            ,
            "Ebba"
            ,
            "Ebonee"
            ,
            "Ebony"
            ,
            "Eda"
            ,
            "Eddi"
            ,
            "Eddie"
            ,
            "Eddy"
            ,
            "Ede"
            ,
            "Edee"
            ,
            "Edeline"
            ,
            "Eden"
            ,
            "Edi"
            ,
            "Edie"
            ,
            "Edin"
            ,
            "Edita"
            ,
            "Edith"
            ,
            "Editha"
            ,
            "Edithe"
            ,
            "Ediva",
            "Edna",
            "Edwina",
            "Edy",
            "Edyth",
            "Edythe",
            "Effie", "Eileen", "Eilis"
        )


 */



/** GENERATE TRANSACTIONS */

//            for (i in 0..500) {
//                var transaction = Transaction()
//                var ownerId = rand.nextInt(ownerVehicles.size)
//                var owner = ownerVehicles.keys.elementAt(ownerId)
//                transaction.owner = owner
//                Log.d("Dummy", "Bound ::: ${ownerVehicles[owner]?.size}")
//                if (ownerVehicles[owner]?.size!! > 0) {
//                    var vehicleId = rand.nextInt(ownerVehicles[owner]?.size!!)
//                    var vehicle = ownerVehicles[owner]?.get(vehicleId)
//                    transaction.vehicleId = vehicle.toString()
//                    var damageId = rand.nextInt(2)
//                    if (damageId == 0)
//                        transaction.isDamagePaidByInsurance = true
//                    else if (damageId == 1)
//                        transaction.isDamagePaidByInsurance = false
//                    var amount = rand.nextInt(150) + 50
//                    transaction.amount = amount.toDouble()
//                    var renterId = rand.nextInt(userList.size)
//                    while (renterId == ownerId)
//                        renterId = rand.nextInt(userList.size)
//                    transaction.renter = userList[renterId].uid
//                    transaction.transactionDate = transList[rand.nextInt(transList.size)]
//                    var fromToId = rand.nextInt(fromTo.size)
//                    transaction.fromDate = fromTo.keys.elementAt(fromToId)
//                    transaction.toDate = fromTo[fromTo.keys.elementAt(fromToId)].toString()
//
//                    var creditCard = CreditCard()
//                    creditCard.name = userList.find { it.uid == owner }?.userName.toString()
//                    creditCard.number = "9823645619872346"
//                    creditCard.cvv = "455"
//                    creditCard.exp = "0522"
//                    creditCard.cardHolder = owner
//
//                    transaction.renterCard = creditCard.number
//
//                    FirebaseDatabase.getInstance().reference.child("Cards").push().setValue(creditCard).addOnCompleteListener {
//
//                        FirebaseDatabase.getInstance().reference.child("Transactions").push().setValue(transaction).addOnCompleteListener {
//                            var fromToMap = hashMapOf<String, String>()
//                            fromToMap.put("from", transaction.fromDate)
//                            fromToMap.put("to", transaction.toDate)
//                            FirebaseDatabase.getInstance().reference.child("Bookings").child(vehicle).push().setValue(fromToMap)
//                        }
//                    }
//
//
//                } else {
//                    Toast.makeText(this@DummyData, "This broke ass motherfucker got no ride", Toast.LENGTH_SHORT).show()
//                }
//
//            }
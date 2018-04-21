package com.zaplab.zap

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.nguyenhoanglam.imagepicker.model.Image
import kotlinx.android.synthetic.main.activity_add_car.*
import kotlinx.android.synthetic.main.fragment_add_car_pay.*
import java.io.ByteArrayOutputStream
import java.io.File


/**
 * Created by Ramshad on 4/6/18.
 */
class AddCarActivity: AppCompatActivity() {



    var infoCheck = false
    var images: ArrayList<Image> = ArrayList()

    var fragmentAdapter = FragmentAdapter(supportFragmentManager)
    var vehicle: Vehicle = Vehicle()
    var titleList = arrayListOf("INFO", "PICTURES", "AVAILABILITY", "LOCATION", "PAYMENT")

    var dbRef = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_car)


        fragmentAdapter.addFragment(AddCarInfoFragment())
        fragmentAdapter.addFragment(AddCarPicsFragment())
        fragmentAdapter.addFragment(AddCarAvailFragment())
        fragmentAdapter.addFragment(AddCarLocationFragment())
        fragmentAdapter.addFragment(AddCarPayFragment())

        pagerAddCar.adapter = fragmentAdapter
        indicatorAddCar.setViewPager(pagerAddCar)

        pagerAddCar.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                tvAddCarTitle.text = titleList[position]
            }

        })
    }

    fun nextPager(pos: Int) {
        pagerAddCar.currentItem = pos
    }

    fun finishAdding() {

        // Push vehicle to DB, get Unique id, create folder in storage and upload pics of car to it
        // update vehicle image url list

        // Add city and country to DB for filtering
        //TODO fix new city updating old one
        dbRef.child("Locations").addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(snap: DataSnapshot?) {
                        if ( snap != null) {
                            var countryFound = false
                            for ( x in snap.children) {
                                if ( x.key == vehicle.country) {
                                    countryFound = true
                                    for (y in x.children) {
                                        if (y.value == vehicle.city) {
                                            break
                                        }
                                        var key = dbRef.child("Locations").child(vehicle.country).push().key
                                        var newCity = hashMapOf<String, String>()
                                        newCity.put(key, vehicle.city)
                                        dbRef.child("Locations").child(vehicle.country).setValue(newCity)
                                    }
                                }
                            }
                            if (!countryFound) {
                                var key = dbRef.child("Locations").child(vehicle.country).push().key
                                var newCity = hashMapOf<String, String>()
                                newCity.put(key, vehicle.city)
                                dbRef.child("Locations").child(vehicle.country).setValue(newCity)
                            }
                        }
                    }

                })

        var key = dbRef.child("Vehicles").push().key



        // Upload images into folder created using unique id and get url list
        var imageUrlList = arrayListOf<String>() // list to store urls of added images
        for ( x in images ) {
            val imgFile = File(x.path)

            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                val baos = ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()


                var storage = FirebaseStorage.getInstance().reference.child("Vehicles/$key/${imgFile.name}")
                val uploadTask = storage.putBytes(data)
                uploadTask.addOnFailureListener(OnFailureListener {
                    // Handle unsuccessful uploads
                    btnAddCarPayFinish.loadingFailed()
                }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    val downloadUrl = taskSnapshot.downloadUrl
                    imageUrlList.add(downloadUrl.toString())
                    vehicle.imageList.clear()
                    vehicle.imageList.addAll(imageUrlList)
                    dbRef.child("Vehicles").child(key).setValue(vehicle).addOnSuccessListener {
                        btnAddCarPayFinish.loadingSuccessful()
                        Toast.makeText(this, "Vehicle successfully added !", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                })
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        //fragmentAdapter.getItem(1).onActivityResult(requestCode, resultCode, data)
    }


}









    /*




        barAvailSun.minimumProgress = 0
        barAvailSun.maximumProgress = 24



        loadingBtnAddCar.setOnClickListener {
            loadingBtnAddCar.startLoading()
            var hasAllData = true
            for (x in tvList) {
                if (x.text.isEmpty()){
                    x.error = "Field cannot be left blank !"
                    loadingBtnAddCar.loadingFailed()
                    hasAllData = false
                    break
                }
            }

            // check if all fields are not empty
            if (hasAllData) {
                vehicle.make = etAddCarMake.text.toString()
                vehicle.model = etAddCarModel.text.toString()
                vehicle.desc = etAddCarDesc.text.toString()
                vehicle.odometer = Integer.parseInt(etAddMileage.text.toString())
                vehicle.bond = Integer.parseInt(etAddBond.text.toString())
                vehicle.rent = Integer.parseInt(etAddRent.text.toString())
                vehicle.color = selectedColor
                dbRef.child("Vehicles").push().setValue(vehicle).addOnCompleteListener {
                    loadingBtnAddCar.loadingSuccessful()
                    finish()
                }
            }
        }

        viewAddColor.setOnClickListener {

            val mColors = resources.getIntArray(R.array.rainbow)

            val dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                    mColors,
                    selectedColor,
                    5, // Number of columns
                    ColorPickerDialog.SIZE_SMALL,
                    true, // True or False to enable or disable the serpentine effect
                    1, // stroke width
                    Color.BLACK // stroke color
            )

            dialog.setOnColorSelectedListener { color ->
                selectedColor = color

                val bgShape = viewAddColor.background as GradientDrawable
                bgShape.setColor(selectedColor)
            }

            dialog.show(fragmentManager, "color_dialog_test");
        }

    }

    fun onRadioClick(view: View?) {
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked

        // Check which radio button was clicked
        when (view.getId()) {
            R.id.rbAuto -> {
                if (checked) {
                    vehicle.transmission = Transmission.Auto
                }
            }
            R.id.rbManual -> {
                if (checked) {
                    vehicle.transmission = Transmission.Manual
                }
            }
        }
    }
     */
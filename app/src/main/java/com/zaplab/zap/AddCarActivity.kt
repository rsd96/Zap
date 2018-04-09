package com.zaplab.zap

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_car.*
import org.xdty.preference.colorpicker.ColorPickerDialog






/**
 * Created by Ramshad on 4/6/18.
 */
class AddCarActivity: AppCompatActivity() {

    private var selectedColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_car)

        loadingBtnAddCar.setOnClickListener {
            loadingBtnAddCar.startLoading()
            if (etAddCarDesc.text.isEmpty()) {
                loadingBtnAddCar.loadingFailed()
                etAddCarDesc.error = "Field cannot be empty!"
            } else
                loadingBtnAddCar.loadingSuccessful()
        }

        selectedColor = ContextCompat.getColor(this, R.color.red)

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
}
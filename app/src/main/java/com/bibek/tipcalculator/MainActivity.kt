package com.bibek.tipcalculator

import android.animation.ArgbEvaluator
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt


private const val TAG = "MainActivity"
private const val INITIAL_VALUE = 15

class MainActivity : AppCompatActivity() {

    private lateinit var seekBarTip : SeekBar
    private lateinit var tvTipPercent : TextView
    private lateinit var etBaseAmount : EditText
    private lateinit var tvTipAmount : TextView
    private lateinit var tvTotalAmount : TextView
    private lateinit var tvTipDescription : TextView
    private lateinit var etNumberOfPeople : EditText
    private lateinit var switchRoundUp : Switch
    private lateinit var spinnerCountry: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initializing the vars

        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercent = findViewById(R.id.tvTipPercent)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        etNumberOfPeople = findViewById(R.id.etNumberOfPeople)
        switchRoundUp = findViewById(R.id.switchRound)
        spinnerCountry = findViewById(R.id.spinnerCountry)

        // handling the the initial value for the seekBar and tip and also tip description
        seekBarTip.progress = INITIAL_VALUE
        tvTipPercent.text = "$INITIAL_VALUE$"
        tvTipDescription.text = "Good"
        tvTipDescription.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorForGood))


        // handling the changes in the seekbar
        seekBarTip.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvTipPercent.text = "$progress%"
                updateTipDescription(progress)
//                updateTipDescriptionColor(progress)
                computeTipAndTotal()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        // handling base amount
        etBaseAmount.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                computeTipAndTotal()
            }

        })

        // handle the number of people for dividing the total amount
        etNumberOfPeople.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                computeTipAndTotal()
            }

        })


        // round up the total value
       switchRoundUp.setOnCheckedChangeListener { _, isChecked ->
           if (isChecked) roundUp() else computeTipAndTotal()
       }




    }

    // update the currency according to the country name
    private fun updateCurrency(currency: String , tipAmount : Double, totalAmount : Double) {
        tvTipAmount.text = String.format("$currency %.2f", tipAmount)
        tvTotalAmount.text =  String.format("$currency %.2f", totalAmount)

    }

    // round up the total amount
    private fun roundUp(){

        // only take the value after the space
        val totalAmountStr = tvTotalAmount.text.toString()
         var  totalAmount : Double  = 0.0
        for(i in totalAmountStr.indices){

            if(totalAmountStr[i] == ' ') {
                 totalAmount = totalAmountStr.substring(i + 1, totalAmountStr.length).toDouble()
                val roundedValue = totalAmount.roundToInt()
                tvTotalAmount.text = totalAmountStr.replace(totalAmount.toString(), roundedValue.toString(), true)
                break
            }

        }
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription :String = when(tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 ->  "Good"
            in 20..24 ->  "Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription
        val tipDescriptionColor = ArgbEvaluator().evaluate(tipPercent.toFloat() / seekBarTip.max, ContextCompat.getColor(this,R.color.worstColor),
        ContextCompat.getColor(this, R.color.bestColor)) as Int
        tvTipDescription.setTextColor(tipDescriptionColor)

    }
//    private fun updateTipDescriptionColor(tipPercent: Int){
//        val tipDescriptionColor : Int = when(tipPercent) {
//            in 0.. 9 -> R.color.colorForPoor
//            in 10.. 14 -> R.color.colorForAcceptable
//            in 15..19 ->  R.color.colorForGood
//            in 20..24 ->  R.color.colorForGreat
//            else -> R.color.colorForAmazing
//        }
//        tvTipDescription.setTextColor(ContextCompat.getColor(this@MainActivity, tipDescriptionColor))
//
//    }

   private fun computeTipAndTotal() {
        // get the value of the base and the tip percent

        try {
            val baseAmount = etBaseAmount.text.toString().toDouble()
            val tipPercent = seekBarTip.progress
            val numberOfPeople = etNumberOfPeople.text.toString().toInt()
            Log.i(TAG, tipPercent.toString())
            val tipAmount = (baseAmount * tipPercent) / 100
            val totalAmount = (baseAmount + tipAmount) / numberOfPeople
            updateCurrency("NPR", tipAmount, totalAmount)



            // handle the item selection on the spinner
            spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when(adapterView?.getItemAtPosition(position).toString()){

                        "India" -> updateCurrency("INR", tipAmount, totalAmount)

                        "USA" -> updateCurrency("$", tipAmount, totalAmount)

                        else -> updateCurrency("NPR", tipAmount, totalAmount)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

        }
        catch(e : NumberFormatException){
            e.printStackTrace()
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
        }

    }

}
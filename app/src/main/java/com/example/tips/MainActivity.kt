/* Extensions
1.  split the bill by n people
2. Round up the final amount
3. Tip Per Person
4. color or ui updates
*/

package com.example.tips

import android.animation.ArgbEvaluator
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 0

class MainActivity : AppCompatActivity() {
    private lateinit var  etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private  lateinit var tvTipPercent: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var tvTotalRoundedUp: TextView
    private lateinit var tvBillAmountRounded: TextView
    private lateinit var tvSplit: TextView
    private lateinit var etSplit: EditText
    private lateinit var tvTotalSplit: TextView
    private  lateinit var tvSplitBillAmount: TextView
    private lateinit var tvSplitRoundedUp: TextView
    private lateinit var SplitBillRoundedAmount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Lock on portrait mode
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)

        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercent = findViewById(R.id.tvTipPercent)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        //new features
        tvTotalRoundedUp = findViewById(R.id.tvTotalRoundedUp)
        tvBillAmountRounded = findViewById(R.id.tvBillAmountRounded)
        tvSplit = findViewById(R.id.tvSplit)
        etSplit = findViewById(R.id.etSplit)
        tvTotalSplit = findViewById(R.id.tvTotalSplit)
        tvSplitBillAmount = findViewById(R.id.tvSplitBillAmount)
        tvSplitRoundedUp = findViewById(R.id.tvSplitRoundedUp)
        SplitBillRoundedAmount = findViewById(R.id.SplitBillRoundedAmount)

        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)


        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG,"onProgressChanged $progress")
                tvTipPercent.text = "$progress%"
                computeTipAndTotal()
                splittingBills()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
        etBaseAmount.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG,"afterTextChanged $s")
                computeTipAndTotal()
            }

        })

        etSplit.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG,"afterTextChanges $s")
                splittingBills()
            }

        })
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription
        // Update the color based on tipPercent
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat()/seekBarTip.max,
            ContextCompat.getColor(this, R.color.red),
            ContextCompat.getColor(this, R.color.green)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal(){
        if(etBaseAmount.text.isEmpty()){
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            tvBillAmountRounded.text=""
            return
        }
        // 1. Gets the value of base and tip percent
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        // 2. Computing the tip and total
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount =  baseAmount + tipAmount
        var total = String.format("%.02f ₹(INR)", totalAmount)
        var tip = String.format("%.02f ₹(INR)", tipAmount)
        val totalRounded = totalAmount.roundToInt()
        val totalRoundedFormat = String.format("%d ₹(INR)",totalRounded)
        // 3. Updating the UI
        tvTipAmount.text = tip
        tvTotalAmount.text = total
        tvBillAmountRounded.text = totalRoundedFormat
    }

    private fun splittingBills(){
        if(etBaseAmount.text.isEmpty() ){
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            tvBillAmountRounded.text=""
            tvSplitBillAmount.text = ""
            SplitBillRoundedAmount.text = ""
            tvSplitBillAmount.text = ""
            return
        }

       if(etSplit.text.isEmpty() xor  etBaseAmount.text.isEmpty()){
            SplitBillRoundedAmount.text = ""
            tvSplitBillAmount.text = ""
            return
        }
        // 1. Gets the value of base and tip percent and no of persons
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        val persons = etSplit.text.toString().toDouble()
        // 2. Computing the tip and total
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount =  baseAmount + tipAmount
        val splitBill = totalAmount / persons
        val splitBillFormat = String.format("%.02f ₹(INR)", splitBill)
        // spliting logic
        val splitBillRounded = splitBill.roundToInt()
        val splitBillRoundedFormat = String.format("%d ₹(INR)",splitBillRounded)
        // 3.Update the UI
        tvSplitBillAmount.text = splitBillFormat
        SplitBillRoundedAmount.text = splitBillRoundedFormat
    }


}
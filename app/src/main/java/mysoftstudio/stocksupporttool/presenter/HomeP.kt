package mysoftstudio.stocksupporttool.presenter

import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import mysoftstudio.stocksupporttool.MyApplication
import mysoftstudio.stocksupporttool.Preferences
import mysoftstudio.stocksupporttool.R
import mysoftstudio.stocksupporttool.data.ResultData
import mysoftstudio.stocksupporttool.view.vi.HomeVI
import java.math.BigDecimal

class HomeP(private val vi: HomeVI) {
    private var checked by Preferences("isChecked", false)
    private var cost = 0.0
    private var isCheck = false
    private var fee = 0.00145
    private var target = 0.0
    private var stock = 0
    private var rate = 0.0
    private var rateChecked = false
    private var targetChecked = true

    fun handleCost(txt: String) {
        cost = if (txt.isNotEmpty()) if (txt != ".") txt.toDouble() else "0$txt".toDouble() else 0.0
    }

    fun handleCheck(checked: Boolean) { isCheck = checked }

    fun handleFee(txt: String) {
        fee = if (txt.isNotEmpty()) if (txt != ".") txt.toDouble() else "0$txt".toDouble() else 0.00145
    }

    fun handleTarget(txt: String) {
        target = if (txt.isNotEmpty()) if (txt != ".") txt.toDouble() else "0$txt".toDouble() else 0.0
    }

    fun handleStock(txt: String) {
        stock = if (txt.isNotEmpty()) txt.toDouble().toInt() else 0
    }

    fun handleRate(txt: String) {
        rate = if (txt.isNotEmpty()) if (txt != ".") txt.toDouble() else "0$txt".toDouble() else 0.0
    }

    fun handleRadioCheckedChanging(id: Int) {
        if (id == R.id.rbTarget) {
            rateChecked = false
            targetChecked = true
        } else {
            rateChecked = true
            targetChecked = false
        }
    }

    fun handleResetData() {
        cost = 0.0
        isCheck = false
        fee = 0.00145
        target = 0.0
        stock = 0
        rate = 0.0
        rateChecked = false
        targetChecked = true
        vi.showResult(ResultData())
        vi.changeState(false)
    }

    fun handleCalculate() {
        val resultData = ResultData()
        var balance = 0.0
        var income = 0
        var result = 0.0
        vi.showResult(resultData)
        when {
            cost == 0.0 -> vi.showErrorMessage(R.string.error_message_cost)
            isCheck && fee == 0.0 -> vi.showErrorMessage(R.string.error_message_fee)
            targetChecked && target == 0.0 && stock > 0 -> vi.showErrorMessage(R.string.error_message_target)
            stock == 0 && target > 0.0 -> vi.showErrorMessage(R.string.error_message_stock)
            else -> {
                balance = if (isCheck) cost * (1.003 + fee / 1000.0) else cost * 1.00445
                resultData.balance = BigDecimal(balance).setScale(2, BigDecimal.ROUND_UP).toDouble()
                if (targetChecked) {
                    val taxAndFee = if (isCheck) target * (0.003 + fee / 1000.0) else target * 0.00445
                    val difference = target - taxAndFee - cost
                    result = difference / (cost + taxAndFee) * 100.0
                    income = (difference * stock).toInt()
                    resultData.income = BigDecimal(income).setScale(0, BigDecimal.ROUND_HALF_UP).toInt()
                    resultData.result = BigDecimal(result).setScale(2, BigDecimal.ROUND_HALF_UP).toString()
                    resultData.resultUnit = MyApplication.instance.getString(R.string.unit_percent)
                    resultData.resultTitle = MyApplication.instance.getString(R.string.view_b_title_result_a)
                }
                if (rateChecked) {
                    val targetRate = (rate + 100.0) / 100.0
                    val targetMoney = cost * targetRate
                    val difference = targetMoney - cost
                    result = if (isCheck) targetMoney * (1.003 + fee / 1000.0) else targetMoney * 1.00445
                    income = (difference * stock).toInt()
                    resultData.income = BigDecimal(income).setScale(0, BigDecimal.ROUND_HALF_UP).toInt()
                    resultData.result = BigDecimal(result).setScale(2, BigDecimal.ROUND_UP).toString()
                    resultData.resultUnit = MyApplication.instance.getString(R.string.unit_money)
                    resultData.resultTitle = MyApplication.instance.getString(R.string.view_b_title_result_b)
                }
                vi.showResult(resultData)
                vi.changeState(true)
            }
        }
    }

    fun handleMenuClick(it: MenuItem): Boolean {
        return when (it.itemId) {
            R.id.menu_about -> {
                vi.showAbout()
                true
            }
            R.id.menu_switch -> {
                switchDayNightMode()
                true
            }
            else -> false
        }
    }

    private fun switchDayNightMode() {
        if (checked) {
            checked = false
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            checked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}
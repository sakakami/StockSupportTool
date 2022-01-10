package mysoftstudio.stocksupporttool.presenter

import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
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

    fun handleCost(txt: String) {
        if (txt.isNotEmpty()) cost = if (txt != ".") txt.toDouble() else "0$txt".toDouble()
    }

    fun handleCheck(checked: Boolean) { isCheck = checked }

    fun handleFee(txt: String) {
        if (txt.isNotEmpty()) fee = if (txt != ".") txt.toDouble() else "0$txt".toDouble()
    }

    fun handleTarget(txt: String) {
        if (txt.isNotEmpty()) target = if (txt != ".") txt.toDouble() else "0$txt".toDouble()
    }

    fun handleStock(txt: String) { if (txt.isNotEmpty()) stock = txt.toDouble().toInt() }

    fun handleCalculate() {
        val resultData = ResultData()
        var buy = 0.0
        var balance = 0.0
        var income = 0
        var rate = 0.0
        when {
            cost == 0.0 -> vi.showErrorMessage(R.string.error_message_cost)
            isCheck && fee == 0.0 -> vi.showErrorMessage(R.string.error_message_fee)
            target == 0.0 && stock > 0 -> vi.showErrorMessage(R.string.error_message_target)
            stock == 0 && target > 0.0 -> vi.showErrorMessage(R.string.error_message_stock)
            else -> {
                buy = if (isCheck) cost / (1.0 + fee / 1000.0) else cost / 1.00145
                balance = if (isCheck) cost * (1.003 + fee / 1000.0) else cost * 1.00445
                if (target > 0.0) {
                    val difference = target - balance
                    rate = difference / balance * 100.0
                    income = (difference * stock).toInt()
                }
                //透過BigDecimal將結果四捨五入至小數點第二位
                resultData.buy = BigDecimal(buy).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                resultData.balance = BigDecimal(balance).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                resultData.income = BigDecimal(income).setScale(0, BigDecimal.ROUND_HALF_UP).toInt()
                resultData.rate = BigDecimal(rate).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " %"
                vi.showResult(resultData)
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
package mysoftstudio.stocksupporttool.presenter

import androidx.appcompat.app.AppCompatDelegate
import mysoftstudio.stocksupporttool.Preferences
import mysoftstudio.stocksupporttool.R
import mysoftstudio.stocksupporttool.data.ResultData
import mysoftstudio.stocksupporttool.data.StockData
import mysoftstudio.stocksupporttool.view.vi.HomeVI
import java.math.BigDecimal

class HomeP(private val vi: HomeVI) {
    private var checked by Preferences("isChecked", false)

    fun handleCalculate(data: StockData) {
        val resultData = ResultData()
        var buy = 0.0
        var balance = 0.0
        var income = 0
        var rate = 0.0
        when {
            data.cost == 0.0 -> vi.showErrorMessage(R.string.error_message_cost)
            data.isCheck && data.fee == 0.0 -> vi.showErrorMessage(R.string.error_message_fee)
            data.target == 0.0 && data.stock > 0 -> vi.showErrorMessage(R.string.error_message_target)
            data.stock == 0 && data.target > 0.0 -> vi.showErrorMessage(R.string.error_message_stock)
            else -> {
                buy = if (data.isCheck) data.cost / (1.0 + data.fee / 1000.0) else data.cost / 1.00145
                balance = if (data.isCheck) data.cost * (1.003 + data.fee / 1000.0) else data.cost * 1.00445
                if (data.target > 0.0) {
                    val difference = data.target - balance
                    rate = difference / balance * 100.0
                    income = (difference * data.stock).toInt()
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

    fun switchDayNightMode() {
        if (checked) {
            checked = false
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            checked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}
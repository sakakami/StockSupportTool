package mysoftstudio.stocksupporttool.view.vi

import mysoftstudio.stocksupporttool.data.ResultData

interface HomeVI {
    fun showErrorMessage(resource: Int)
    fun showResult(resultData: ResultData)
    fun showAbout()
    fun changeState(state: Boolean)
}
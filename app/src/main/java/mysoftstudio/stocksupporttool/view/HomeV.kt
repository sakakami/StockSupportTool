package mysoftstudio.stocksupporttool.view

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import mysoftstudio.stocksupporttool.R
import mysoftstudio.stocksupporttool.data.ResultData
import mysoftstudio.stocksupporttool.data.StockData
import mysoftstudio.stocksupporttool.databinding.DialogMenuAboutBinding
import mysoftstudio.stocksupporttool.databinding.FragmentHomeVBinding
import mysoftstudio.stocksupporttool.presenter.HomeP
import mysoftstudio.stocksupporttool.view.vi.HomeVI

/**
 * create on 2021.12.22
 */
class HomeV : Fragment(), HomeVI {
    private var _binding: FragmentHomeVBinding? = null
    private val binding get() = _binding!!
    private val p by lazy { HomeP(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeVBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun showErrorMessage(resource: Int) {
        AlertDialog.Builder(requireContext())
            .setMessage(resource)
            .setPositiveButton(R.string.confirm) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    override fun showResult(resultData: ResultData) {
        binding.txtBuyResult.text = resultData.buy.toString()
        binding.txtBalanceResult.text = resultData.balance.toString()
        binding.txtIncomeResult.text = resultData.income.toString()
        binding.txtRateResult.text = resultData.rate.toString()
    }

    private fun init() {
        var cost = 0.0
        var isCheck = false
        var fee = 0.00145
        var target = 0.0
        var stock = 0
        binding.editCost.addTextChangedListener { cost = if (it.isNullOrEmpty()) 0.0 else it.toString().toDouble() }
        binding.checkbox.setOnCheckedChangeListener { _, isChecked -> isCheck = isChecked }
        binding.editFee.addTextChangedListener { fee = if (it.isNullOrEmpty()) 0.00145 else it.toString().toDouble() }
        binding.editTarget.addTextChangedListener { target = if (it.isNullOrEmpty()) 0.0 else it.toString().toDouble() }
        binding.editStock.addTextChangedListener { stock = if (it.isNullOrEmpty()) 0 else it.toString().toInt() }
        binding.txtCalculate.setOnClickListener {
            val data = StockData()
            with(data) {
                this.cost = cost
                this.isCheck = isCheck
                this.fee = fee
                this.target = target
                this.stock = stock
            }
            p.handleCalculate(data)
        }
        val toolbar = Toolbar(requireContext())
        toolbar.inflateMenu(R.menu.menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_about -> {
                    showAbout()
                    true
                }
                else -> false
            }
        }
    }

    private fun showAbout() {
        val view = DialogMenuAboutBinding.inflate(LayoutInflater.from(requireContext()))
        val version = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName
        view.txtResultVersion.text = version
        AlertDialog.Builder(requireContext())
            .setView(view.root)
            .setPositiveButton(R.string.confirm) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
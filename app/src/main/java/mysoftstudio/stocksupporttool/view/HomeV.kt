package mysoftstudio.stocksupporttool.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import mysoftstudio.stocksupporttool.Preferences
import mysoftstudio.stocksupporttool.R
import mysoftstudio.stocksupporttool.data.ResultData
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
    private var checked by Preferences("isChecked", false)
    private var isCalculated = false

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
        binding.txtBalanceResult.text = resultData.balance.toString()
        binding.txtIncomeResult.text = resultData.income.toString()
        binding.txtSelectUnit.text = resultData.resultUnit
        binding.txtSelect.text = resultData.resultTitle
        binding.txtSelectResult.text = resultData.result
    }

    override fun changeState(state: Boolean) {
        isCalculated = state
        if (state) {
            binding.txtCalculate.text = getString(R.string.view_d_title_clear)
        } else {
            binding.editStock.setText("")
            binding.editCost.setText("")
            binding.editFee.setText("")
            binding.editTarget.setText("")
            binding.editRate.setText("")
            binding.txtCalculate.text = getString(R.string.view_d_title_calculate)
            binding.radioGroup.check(R.id.rbTarget)
            binding.checkbox.isChecked = false
        }
    }

    override fun showAbout() {
        val view = DialogMenuAboutBinding.inflate(LayoutInflater.from(requireContext()))
        val version = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName
        val project = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse("https://github.com/sakakami/StockSupportTool") }
        val rate = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse("https://play.google.com/store/apps/details?id=mysoftstudio.stocksupporttool") }
        view.txtResultVersion.text = version
        view.txtResultProject.setOnClickListener { startActivity(project) }
        view.txtResultRate.setOnClickListener { startActivity(rate) }
        AlertDialog.Builder(requireContext())
            .setView(view.root)
            .setPositiveButton(R.string.confirm) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun init() {
        //設定成本均價edit只能輸入浮點數
        binding.editCost.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.editCost.addTextChangedListener { p.handleCost(it.toString()) }
        binding.checkbox.setOnCheckedChangeListener { _, isChecked -> p.handleCheck(isChecked) }
        //設定手續費edit只能輸入浮點數
        binding.editFee.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.editFee.addTextChangedListener { p.handleFee(it.toString()) }
        //設定目標價格edit只能輸入浮點數
        binding.editTarget.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.editTarget.addTextChangedListener { p.handleTarget(it.toString()) }
        //設定持有股數edit只能輸入正整數
        binding.editStock.inputType = InputType.TYPE_CLASS_NUMBER
        binding.editStock.addTextChangedListener { p.handleStock(it.toString()) }

        binding.txtCalculate.setOnClickListener { if (isCalculated) p.handleResetData() else p.handleCalculate() }
        //設定目標成數edit只能輸入浮點數
        binding.editRate.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.editRate.addTextChangedListener { p.handleRate(it.toString()) }
        //設定tool bar的menu清單內容
        binding.toolbar.inflateMenu(R.menu.menu)
        //設定menu內夜間模式是否啟用
        binding.toolbar.menu.findItem(R.id.menu_switch).isChecked = checked
        binding.toolbar.setOnMenuItemClickListener { p.handleMenuClick(it) }
        //設定預設選項
        binding.radioGroup.check(R.id.rbTarget)
        //監聽選項更變
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId -> p.handleRadioCheckedChanging(checkedId) }
        showResult(ResultData())
    }
}
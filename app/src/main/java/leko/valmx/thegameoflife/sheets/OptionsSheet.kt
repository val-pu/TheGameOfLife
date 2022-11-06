package leko.valmx.thegameoflife.sheets

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.core.Sheet
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputCheckBox
import com.maxkeppeler.sheets.input.type.InputRadioButtons
import com.maxkeppeler.sheets.input.type.spinner.InputSpinner
import com.maxkeppeler.sheets.option.Option
import com.maxkeppeler.sheets.option.OptionSheet
import kotlinx.android.synthetic.main.sheet_rules.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.utils.GameRuleSet
import leko.valmx.thegameoflife.recyclers.RuleSheetAdapter

class OptionsSheet(context: Context, val gameView: GameView) : Sheet() {
    fun show(ctx: Context) {
        this.windowContext = ctx
        this.width = null

        positiveText = "Apply"
        positiveListener = {
            GameRuleSet(ctx).saveRules((rulesRecycler.adapter as RuleSheetAdapter).ruleSet)
            gameView.actorManager.applyRuleSet()
        }

        this.show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rulesRecycler.layoutManager = LinearLayoutManager(requireContext())
        rulesRecycler.adapter = RuleSheetAdapter(requireContext())


    }

    override fun onCreateLayoutView(): View {
        return View.inflate(context, R.layout.sheet_rules, null)
    }
}
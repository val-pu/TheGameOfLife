package leko.valmx.thegameoflife.sheets

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.core.Sheet
import kotlinx.android.synthetic.main.sheet_blueptint_more_info.*
import kotlinx.android.synthetic.main.sheet_rules.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.utils.GameRuleHelper
import leko.valmx.thegameoflife.recyclers.BlueprintInfoRecycler
import leko.valmx.thegameoflife.recyclers.RulePresetPickerAdapter
import leko.valmx.thegameoflife.recyclers.RuleSheetAdapter
import leko.valmx.thegameoflife.utils.blueprints.Blueprint


class BlueprintInfoSheet(val blueprint: Blueprint) : Sheet() {
    fun show(ctx: Context) {
        this.windowContext = ctx
        positiveText = "Apply"
        show()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*preview.init {
            preview.previewManager.init(blueprint, true)
        }*/
        info_recycler.layoutManager = LinearLayoutManager(context)
        info_recycler.adapter = BlueprintInfoRecycler(blueprint)
    }
    override fun onCreateLayoutView(): View {
        return View.inflate(context, R.layout.sheet_blueptint_more_info, null)
    }

}
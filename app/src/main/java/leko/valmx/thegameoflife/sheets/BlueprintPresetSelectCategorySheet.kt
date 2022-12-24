package leko.valmx.thegameoflife.sheets

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.core.Sheet
import com.maxkeppeler.sheets.core.SheetStyle
import kotlinx.android.synthetic.main.sheet_blueprint_library_category.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.recyclers.BlueprintCategoryAdapter
import leko.valmx.thegameoflife.utils.AssetUtils
import leko.valmx.thegameoflife.utils.PresetCategory

class BlueprintPresetSelectCategorySheet(context: Context, val gameView: GameView) : Sheet() {
    init {
        show(context)
    }

    private fun show(ctx: Context) {
        this.windowContext = ctx
        this.width = null
        displayPositiveButton(false)
        style(SheetStyle.DIALOG)
        title("Select a category")
        
        this.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presets_category_recycler.layoutManager = LinearLayoutManager(requireContext())
        presets_category_recycler.adapter = BlueprintCategoryAdapter { category: PresetCategory ->
            BlueprintPresetSelectionSheet(
                gameView,
                category
            )
            dismiss()
        }
    }

    override fun onCreateLayoutView(): View {
        return View.inflate(context, R.layout.sheet_blueprint_library_category, null)
    }

}
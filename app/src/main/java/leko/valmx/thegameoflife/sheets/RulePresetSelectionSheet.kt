package leko.valmx.thegameoflife.sheets

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.core.IconButton
import com.maxkeppeler.sheets.core.Sheet
import com.maxkeppeler.sheets.core.SheetStyle
import kotlinx.android.synthetic.main.sheet_predefined_selector.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.recyclers.RulePresetPickerAdapter

class RulePresetSelectionSheet(context: Context, val gameView: GameView, val ruleSelectedListener: RulePresetPickerAdapter.RuleSelectedListener) : Sheet(){
    fun show(ctx: Context) {
        this.windowContext = ctx
        this.width = null


        this.show()
        title("Select a rule")
        style(SheetStyle.DIALOG)

        withIconButton(IconButton(R.drawable.external_link)) {
            SourceSheet(
                requireContext(),
                "https://conwaylife.com/wiki/List_of_Life-like_cellular_automata",
                "16.12.2022",
                "GNU Free Documentation License 1.2",
                "https://www.gnu.org/licenses/fdl-1.3.html"
            )
        }


        displayPositiveButton(false)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presets_recycler.layoutManager = GridLayoutManager(context,2)
        presets_recycler.adapter = RulePresetPickerAdapter(requireContext(), ruleSelectedListener) {
            dismiss()
        }
    }

    override fun onCreateLayoutView(): View {
        return View.inflate(context, R.layout.sheet_predefined_selector, null)
    }


}
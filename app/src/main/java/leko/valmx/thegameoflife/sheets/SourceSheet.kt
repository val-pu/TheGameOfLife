package leko.valmx.thegameoflife.sheets

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.core.Sheet
import kotlinx.android.synthetic.main.sheet_rules.*
import kotlinx.android.synthetic.main.sheet_source.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.utils.GameRuleHelper
import leko.valmx.thegameoflife.recyclers.RulePresetPickerAdapter
import leko.valmx.thegameoflife.recyclers.RuleSheetAdapter

class SourceSheet(
    context: Context,
    private val urlString: String,
    private val time: String,
    private val licenceName: String,
    private val licenceUrl: String
) : Sheet() {

    init {
        show(context)
    }

    private fun show(ctx: Context) {
        this.windowContext = ctx
        this.width = null

        displayNegativeButton(false)

        showsDialog = true

        this.show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        url.text = urlString
        licence.text = licenceName

        url_btn.setOnClickListener {
            val urlIntent = Intent(Intent.ACTION_VIEW)
            urlIntent.data = Uri.parse(urlString)
            startActivity(urlIntent)
        }

        licence_btn.setOnClickListener {
            val urlIntent = Intent(Intent.ACTION_VIEW)
            urlIntent.data = Uri.parse(licenceUrl)
            startActivity(urlIntent)
        }

        timestamp.text = "Retrieved on: $time"
    }


    override fun onCreateLayoutView(): View {
        return View.inflate(context, R.layout.sheet_source, null)
    }

}
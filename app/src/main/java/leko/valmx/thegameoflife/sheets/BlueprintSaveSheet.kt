package leko.valmx.thegameoflife.sheets

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.maxkeppeler.sheets.core.Sheet
import kotlinx.android.synthetic.main.sheet_save_blueprint.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.tools.copypasta.Sketch
import leko.valmx.thegameoflife.game.tools.copypasta.SketchLoadSaver

class BlueprintSaveSheet(context: Context, val sketch: Sketch) : Sheet() {

    private var sketchName = ""
    private var sketchLoadSaver = SketchLoadSaver(context)

    fun show(ctx: Context) {
        this.windowContext = ctx
        this.width = null

        positiveText = "Save"
        positiveListener = {

            SketchLoadSaver(ctx).saveSketch(sketchName, sketch)

        }
        showsDialog = true

        this.show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preview_blueprint.post {
            preview_blueprint.previewManager.init(sketch)
        }
        displayButtonPositive(false)

        sheet_save_name_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s == null) return

                sketchName = s.toString().trim()

                displayButtonPositive(sketchName != "" && sketchLoadSaver.isValidName(sketchName))

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    override fun onCreateLayoutView(): View {
        return View.inflate(context, R.layout.sheet_save_blueprint, null)
    }

}
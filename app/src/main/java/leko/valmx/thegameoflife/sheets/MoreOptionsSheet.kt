package leko.valmx.thegameoflife.sheets

import android.content.Context
import android.content.Intent
import com.maxkeppeler.sheets.option.Info
import com.maxkeppeler.sheets.option.Option
import com.maxkeppeler.sheets.option.OptionSheet
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.SettingActivity
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.tools.SelectionTool

class MoreOptionsSheet(context: Context, gameView: GameView) {
    init {
        OptionSheet().show(context) {

            title("Select an Option")
            columns(3)
//            withInfo(Info("S"))
            with(
                Option(R.drawable.ic_round_ballot_24, "Change rules"),
                Option(R.drawable.upload, "Place Blueprint"),
                Option(R.drawable.settings, "App Settings"),
                Option(R.drawable.ic_round_photo_size_select_small_24, "Start Selection")
            )
            onPositive { index: Int, option: Option ->

                if (index == 0) RulesSheet(context, gameView).show(context)
                if (index == 1) BlueprintSheet(context, gameView).show(context)
                if (index == 2) startActivity(Intent(context, SettingActivity::class.java))
                if (index == 3) {
                    gameView.interactionManager.registeredInteraction = SelectionTool(gameView)
                }
            }

        }

    }
}
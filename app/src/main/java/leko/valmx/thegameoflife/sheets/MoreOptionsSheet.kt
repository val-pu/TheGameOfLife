package leko.valmx.thegameoflife.sheets

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.maxkeppeler.sheets.core.SheetStyle
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
            style(SheetStyle.DIALOG)
            with(
                Option(R.drawable.ic_round_ballot_24, "Change rules"),
                Option(R.drawable.upload, "Place Blueprint"),
                Option(R.drawable.settings, "App Settings"),
                Option(R.drawable.ic_round_photo_size_select_small_24, "Start Selection"),
                Option(R.drawable.trash_2, "Clear everything"),
                Option(R.drawable.star, "Rate")
            )
            onPositive { index: Int, option: Option ->

                if (index == 0) RulesSheet(gameView).show(context)
                if (index == 1) BlueprintPresetSelectCategorySheet(context, gameView)
                if (index == 2) startActivity(Intent(context, SettingActivity::class.java))
                if (index == 3) gameView.interactionManager.registeredInteraction =
                    SelectionTool(gameView)
                if (index == 4) gameView.javaActorManager.clearCells()
                if (index == 5) {

                    val appPackageName: String =
                        "leko.valmx.thegameoflife" // package name of the app

                    try {
                        ContextCompat.startActivity(
                            context,
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$appPackageName")
                            ), null
                        )
                    } catch (anfe: ActivityNotFoundException) {
                        ContextCompat.startActivity(
                            context,
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                            ), null)

                    }
                }
            }

        }

    }
}
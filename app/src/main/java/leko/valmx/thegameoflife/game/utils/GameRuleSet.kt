package leko.valmx.thegameoflife.game.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import leko.valmx.thegameoflife.recyclers.RuleSheetAdapter

class GameRuleSet(val ctx: Context) {
    val PREF_ID = "CWGOL_VAL_RULES"
    val SAVE_ID = "RULE_"

    init {
//        loadRules()
    }

    var ruleSet =
        arrayOf(
            Rule.DIE,
            Rule.SURVIVE,
            Rule.LIVE_SURVIVE,
            Rule.DIE,
            Rule.DIE,
            Rule.DIE,
            Rule.DIE,
            Rule.DIE
        )

    val prefs = ctx.getSharedPreferences(PREF_ID, MODE_PRIVATE)

    fun loadRules() {

        ruleSet[0] = Rule.valueOf(prefs.getInt(SAVE_ID + 0, 0))
        ruleSet[1] = Rule.valueOf(prefs.getInt(SAVE_ID + 1, 2))
        ruleSet[2] = Rule.valueOf(prefs.getInt(SAVE_ID + 2, 3))
        ruleSet[3] = Rule.valueOf(prefs.getInt(SAVE_ID + 3, 0))
        ruleSet[4] = Rule.valueOf(prefs.getInt(SAVE_ID + 4, 0))
        ruleSet[5] = Rule.valueOf(prefs.getInt(SAVE_ID + 5, 0))
        ruleSet[6] = Rule.valueOf(prefs.getInt(SAVE_ID + 6, 0))
        ruleSet[7] = Rule.valueOf(prefs.getInt(SAVE_ID + 7, 0))
    }

    public enum class Rule(val id: Int) {
        LIVE(1), DIE(0), SURVIVE(2), LIVE_SURVIVE(3);


        companion object {
            fun valueOf(i: Int): Rule {
                return when (i) {
                    0 -> DIE
                    1 -> LIVE
                    2 -> SURVIVE
                    3 -> LIVE_SURVIVE
                    else -> SURVIVE
                }
            }
        }
    }

    fun saveRules(arr: Array<RuleSheetAdapter.RuleBundle>) {
        val edit = prefs.edit()

        repeat(8) { i ->
            edit.putInt(SAVE_ID + i, arr[i].rule.id)
        }
        edit.apply()
    }

}
package leko.valmx.thegameoflife.game.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import leko.valmx.thegameoflife.utils.AssetUtils
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.pow

class GameRuleHelper(val ctx: Context) {
    val PREF_ID = "CWGOL_VAL_RULES"
    val SAVE_ID = "RULE"

    init {
//        loadRules()
    }

    lateinit var ruleSet: RuleSet

    private val defaultRuleInt = 0b000011000000100

    val defaultRuleSet =
        RuleSet(defaultRuleInt)

    fun resetRules() {
        saveRule(defaultRuleSet)
    }

    val prefs = ctx.getSharedPreferences(PREF_ID, MODE_PRIVATE)

    fun loadRules() {

        ruleSet = RuleSet(prefs.getInt(SAVE_ID, defaultRuleInt))
    }

    fun loadPresetRules(): HashMap<String, String> {
        val rules =
            AssetUtils.loadAssetString(ctx, "rules/rules.txt")!!.replace("\n", "").split("//")

        val result = HashMap<String, String>()

        rules.forEach { rule ->

            val name = rule.substringAfter("$")
            val ruleString = rule.substringBefore("$")

            result[name] = ruleString
        }

        return result

    }

    fun saveRule(rule: RuleSet) {
        val edit = prefs.edit()

        edit.putInt(SAVE_ID, rule.getRuleInt())
        edit.apply()
    }

    class RuleSet {
        private val bornValues: Array<Boolean> = Array<Boolean>(8) { false }
        private val surviveValues: Array<Boolean> = Array<Boolean>(8) { false }
        private val defaultRuleInt = 0b000011000000100


        constructor(ruleInt: Int) {
            initWithRuleInt(ruleInt)
        }

        constructor(input: String) {
            try {
                val ruleString = input.replace("rule=", "")
                var result = 0

                val rules = ruleString.split("/")
                val bornRules = rules[0].toLowerCase(Locale.ROOT).strip().substringAfter("b")
                val surivalRules = rules[1].toLowerCase(Locale.ROOT).strip().substringAfter("s")


                bornRules.toCharArray().forEach {
                    result += (2.0.pow(it.digitToInt().toDouble() - 1)).toInt()
                }
                surivalRules.toCharArray().forEach {
                    result += (2.0.pow(8 + it.digitToInt().toDouble() - 1)).toInt()

                }
                initWithRuleInt(result)
            } catch (e: Exception) {
                initWithRuleInt(defaultRuleInt)
            }
        }

        private fun initWithRuleInt(ruleInt: Int) {

            var lastInt = ruleInt
            var i = 0
            while (lastInt != 0 && i != 16) {

                if (lastInt % 2 == 1) {
                    if (i >= 8) {
                        surviveValues[i - 8] = true

                    } else {
                        bornValues[i] = true
                    }
                }
                lastInt /= 2

                i++;
            }


        }

        fun willBeBorn(neighbours: Int): Boolean {
            if (neighbours == 0) return false


            return bornValues[neighbours - 1]
        }

        fun willSurvive(neighbours: Int): Boolean {
            if (neighbours == 0) return false
            return surviveValues[neighbours - 1]
        }

        fun setBorn(neighbours: Int, bool: Boolean) {
            bornValues[neighbours] = bool
        }

        fun setSurvive(neighbours: Int, bool: Boolean) {
            surviveValues[neighbours] = bool
        }

        fun getRuleInt(): Int {
            var res = 0;
            bornValues.forEachIndexed { index, b ->
                if (b) res += 2.0.pow(index).toInt();
            }

            surviveValues.forEachIndexed { index, b ->
                if (b) res += 2.0.pow(index + 8).toInt();
            }
            return res

        }

    }


}
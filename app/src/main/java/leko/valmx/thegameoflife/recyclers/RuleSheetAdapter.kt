package leko.valmx.thegameoflife.recyclers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.item_rule_tweak.view.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.utils.GameRuleSet

class RuleSheetAdapter(ctx: Context) : Adapter<RuleSheetAdapter.VH>() {

    lateinit var ruleSet: Array<RuleBundle>

    init {
        val r = GameRuleSet(ctx).apply { loadRules() }.ruleSet
        val rules = Array<RuleBundle>(8) {
            RuleBundle(r[it], it + 1)
        }

        ruleSet = rules


    }

    class VH(item: View) : ViewHolder(item) {

        fun bind(bundle: RuleBundle) {
            itemView.tweak_title.text = "${bundle.neighbours} Neighbours"
            itemView.check_born.isChecked = false
            itemView.check_survive.isChecked = false
            when (bundle.rule) {
                GameRuleSet.Rule.SURVIVE -> {
                    itemView.check_survive.isChecked = true
                }
                GameRuleSet.Rule.LIVE -> {
                    itemView.check_born.isChecked = true
                }
                GameRuleSet.Rule.LIVE_SURVIVE -> {
                    itemView.check_born.isChecked = true
                    itemView.check_survive.isChecked = true
                }
                else -> {}
            }

            itemView.check_born.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (bundle.rule == GameRuleSet.Rule.SURVIVE) bundle.rule =
                        GameRuleSet.Rule.LIVE_SURVIVE
                    else bundle.rule = GameRuleSet.Rule.LIVE
                } else {
                    if (bundle.rule == GameRuleSet.Rule.LIVE_SURVIVE) bundle.rule =
                        GameRuleSet.Rule.SURVIVE
                    else bundle.rule = GameRuleSet.Rule.DIE
                }
            }
            itemView.check_survive.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (bundle.rule == GameRuleSet.Rule.LIVE) bundle.rule =
                        GameRuleSet.Rule.LIVE_SURVIVE
                    else bundle.rule = GameRuleSet.Rule.SURVIVE
                } else {
                    if (bundle.rule == GameRuleSet.Rule.LIVE_SURVIVE) bundle.rule =
                        GameRuleSet.Rule.LIVE
                    else bundle.rule = GameRuleSet.Rule.DIE
                }
            }
        }

    }

    class RuleBundle(var rule: GameRuleSet.Rule, val neighbours: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rule_tweak, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(ruleSet[position])
    }

    override fun getItemCount(): Int {
        return ruleSet.size
    }
}
package leko.valmx.thegameoflife.recyclers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.item_rule_selection.view.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.utils.GameRuleHelper
import java.util.LinkedList

class RulePresetPickerAdapter(
    ctx: Context,
    val listener: RuleSelectedListener,
    val onSelection: () -> Unit
) : Adapter<RulePresetPickerAdapter.VH>() {

    val rules = GameRuleHelper(ctx).loadPresetRules()
    val ruleNames = LinkedList(rules.keys)


    class VH(itemView: View) : ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rule_selection, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val name = ruleNames[position]

        holder.itemView.setOnClickListener {
            listener.onRuleSelected(GameRuleHelper.RuleSet(rules[name]!!))
            onSelection()
        }

        holder.itemView.ruleName.text = name
        holder.itemView.rule_str.text = rules[name]

    }

    override fun getItemCount(): Int {
        return ruleNames.size
    }

    interface RuleSelectedListener {
        fun onRuleSelected(rule: GameRuleHelper.RuleSet)
    }

}
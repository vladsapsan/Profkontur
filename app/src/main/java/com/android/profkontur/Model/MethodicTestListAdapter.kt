package com.android.profkontur.Model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.profkontur.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView


class MethodicTestListAdapter(private val testList: List<MethodicItemModel>, private val onItemClick: (MethodicItemModel) -> Unit) :
    RecyclerView.Adapter<MethodicTestListAdapter.TestViewHolder>() {

    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.card_view)
        val testNameTextView: MaterialTextView = itemView.findViewById(R.id.test_name)
        val IsDoneLayout: LinearLayout = itemView.findViewById(R.id.IsDoneLayout)
        val arrow:ImageView  = itemView.findViewById(R.id.arrow)
        val testDescriptionTextView: MaterialTextView = itemView.findViewById(R.id.test_author) // Добавляем TextView для описания
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.test_list_item, parent, false) // Создаем экземпляр ViewHolder
        return TestViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val testItem = testList[position]
        holder.testNameTextView.text = testItem.name
        if(testItem.authors.length>0){
            holder.testDescriptionTextView.visibility = View.VISIBLE
            holder.testDescriptionTextView.text = testItem.authors // Устанавливаем machine_name в качестве описания
        }
        if(testItem.isDone==true){
            holder.IsDoneLayout.visibility = View.VISIBLE
            holder.arrow.visibility = View.GONE
        }
        holder.cardView.setOnClickListener {
            onItemClick(testItem)
        }
    }

    override fun getItemCount(): Int {
        return testList.size
    }
}
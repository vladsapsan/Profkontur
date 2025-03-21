package com.android.profkontur.Model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.profkontur.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView


class MethodicTestListAdapter(private val testList: List<MethodicItemModel>, private val onItemClick: (MethodicItemModel) -> Unit) :
    RecyclerView.Adapter<MethodicTestListAdapter.TestViewHolder>() {

    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.card_view)
        val testNameTextView: MaterialTextView = itemView.findViewById(R.id.test_name)
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
        holder.testDescriptionTextView.text = testItem.machine_name // Устанавливаем machine_name в качестве описания

        holder.cardView.setOnClickListener {
            onItemClick(testItem)
        }
    }

    override fun getItemCount(): Int {
        return testList.size
    }
}
package com.android.profkontur

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ChekingTestFragment : Fragment() {


    private lateinit var database: DatabaseReference
    private  lateinit var AboutTestText:TextView
    private lateinit var answersRadioGroup:RadioGroup
    private  lateinit var questionTextView:TextView
    private  lateinit var CurrentQuestionNumber:TextView
    private  lateinit var AllQuestionNUmber:TextView
    private  lateinit var TestData:QuestionsData
    private  lateinit var nextQuestionButton:Button
    private  var CurrentQuestion:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cheking_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AboutTestText = view.findViewById(R.id.AboutTestText);
        questionTextView = view.findViewById(R.id.questionTextView);
        nextQuestionButton= view.findViewById(R.id.nextQuestionButton);

        AllQuestionNUmber = view.findViewById(R.id.AllQuestionNUmber);
        CurrentQuestionNumber = view.findViewById(R.id.CurrentQuestionNumber);

        answersRadioGroup= view.findViewById(R.id.answersRadioGroup);

        database = Firebase.database.reference
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val questionsResponse = dataSnapshot.getValue(QuestionsData::class.java)
                if (questionsResponse != null) {
                    TestData = questionsResponse
                    AllQuestionNUmber.text = TestData.questions?.size.toString()
                    LoadNextQuestion()
                };
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок
            }
        })

        //Переход в следующий вопрос
        nextQuestionButton.setOnClickListener {
            LoadNextQuestion()
        }

    }

    fun LoadNextQuestion(){
        if(TestData!=null){
        CurrentQuestion++
        CurrentQuestionNumber.text = CurrentQuestion.toString()
        questionTextView.text = TestData.questions?.get(CurrentQuestion)?.question
        AboutTestText.text = TestData.questions?.get(CurrentQuestion)?.dsc
            val radioButton = RadioButton(context)
            radioButton.text = TestData.questions?.get(CurrentQuestion)?.answers?.get(1)?.answer
            answersRadioGroup.addView(radioButton)
           // TestData.questions?.get(CurrentQuestion)?.answers?.let { addRadioButtons(answersRadioGroup, it) }
           // addRadioButtons(answersRadioGroup, TestData.questions?.get(CurrentQuestion)?.answers!!)
        }
    }



    private fun addRadioButtons(radioGroup: RadioGroup, answers: ArrayList<Answer>) {
        radioGroup.removeAllViews() // Очищаем RadioGroup перед добавлением новых RadioButtons
        var i = 0
        while (i<answers.size){
            val radioButton = RadioButton(context)
            radioButton.text = answers.get(i).answer.toString()
            radioGroup.addView(radioButton)
            i++
        }

    }
}
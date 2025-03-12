package com.android.profkontur.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.android.profkontur.Model.LoadingState
import com.android.profkontur.Model.MetaData
import com.android.profkontur.Model.Question
import com.android.profkontur.R
import com.android.profkontur.ViewModel.TestsVIewModel
import com.android.profkontur.timer.TestTimer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AboutTestFragment : Fragment() {


    private  lateinit var DscTestTextView: TextView
    private lateinit var ProgressBar:ProgressBar
    private lateinit var AboutTestLayout:LinearLayout
    private lateinit var TestLayuot:LinearLayout
    private  lateinit var TestNameTextView: TextView
    private  lateinit var TimeTestTextView: TextView
    private  lateinit var StartTestButton: Button

    private  lateinit var questionTextView: TextView
    private  lateinit var AboutQuestionText: TextView
    private  lateinit var TimerText: TextView
    private  lateinit var CurrentQuestionNumber: TextView
    private  lateinit var AllQuestionNUmber: TextView
    private lateinit var answersRadioGroup:RadioGroup
    private lateinit var nextQuestionButton:Button

    private  lateinit var MetaData: MetaData

    private lateinit var testTimer: TestTimer

    private lateinit var viewModel: TestsVIewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  binding = FragmentAboutTestBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_about_test, container, false)
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector", "DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TestsVIewModel::class.java)


        initAboutTestViews(view)
        initTestViews(view)

        StartTestButton = view.findViewById(R.id.StartTestButton);
        StartTestButton.setOnClickListener {
            StartTest()
        }

        nextQuestionButton.setOnClickListener {
            if(answersRadioGroup.checkedRadioButtonId==-1){
                Toast.makeText(context,"Выберите вариант ответа", Toast.LENGTH_SHORT).show()
            }else {
                    viewModel.selectAnswer(answersRadioGroup.indexOfChild(view.findViewById(answersRadioGroup.checkedRadioButtonId))+1) //Отправляем выбранный ответ
                    viewModel.nextQuestion()
                    DisplayCurrentQuestion()
            }
        }

        viewModel.TestIsDone.observe(viewLifecycleOwner, Observer { isDone ->
            if (isDone) {
                TestDone()
            } else {

            }
        })

        // Наблюдение за состоянием загрузки
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadingstate.collectLatest { loadingState ->
                    when (loadingState) {
                        is LoadingState.Loading -> {
                            ProgressBar.visibility = View.VISIBLE
                            AboutTestLayout.visibility = View.GONE
                        }
                        is LoadingState.Ready -> {
                            DisplayMeta()
                            InitTimer()
                            AllQuestionNUmber.text = (viewModel.AllTestData.value?.questions?.size).toString()
                            ProgressBar.visibility = View.GONE
                            AboutTestLayout.visibility = View.VISIBLE
                        }
                        is LoadingState.Error -> {
                            ProgressBar.visibility = View.GONE
                            AboutTestLayout.visibility = View.GONE
                            Toast.makeText(requireContext(), loadingState.messege, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }



    fun InitTimer(){
        testTimer = TestTimer(
            timerDurationSeconds = (viewModel.AllTestData.value?.meta?.time?.toLong()?.times((60))!!),
            coroutineScope = lifecycleScope,
            OnTimeEndAction = {}
        )
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                testTimer.remainingTime.collectLatest { remainingSeconds ->
                    val minutes = remainingSeconds / 60
                    val seconds = remainingSeconds % 60
                    val timeString = String.format("%02d:%02d", minutes, seconds)
                    TimerText.text = timeString
                }
            }
        }
    }
    fun initAboutTestViews(view: View){
        DscTestTextView = view.findViewById(R.id.DscTestTextView);
        TestNameTextView = view.findViewById(R.id.TestNameTextView);
        TimeTestTextView = view.findViewById(R.id.TimeTestTextView);
        AboutTestLayout= view.findViewById(R.id.AboutTestLayout);
        TestLayuot= view.findViewById(R.id.TestLayuot);
        ProgressBar= view.findViewById(R.id.ProgressBar);
    }
    fun initTestViews(view: View){
        questionTextView= view.findViewById(R.id.questionTextView);
        AboutQuestionText = view.findViewById(R.id.AboutQuestionText);
        TimerText = view.findViewById(R.id.TimerText);
        CurrentQuestionNumber = view.findViewById(R.id.CurrentQuestionNumber);
        AllQuestionNUmber = view.findViewById(R.id.AllQuestionNUmber);
        answersRadioGroup = view.findViewById(R.id.answersRadioGroup);
        nextQuestionButton = view.findViewById(R.id.nextQuestionButton);
    }
    fun DisplayMeta(){
        MetaData = viewModel.getCurrentMeta()!!
        if(MetaData!=null){
            DscTestTextView.text = MetaData.short_dsc
            TestNameTextView.text = MetaData.name
            TimeTestTextView.text = MetaData.time
        }else{

        }
    }

    fun DisplayCurrentQuestion(){
        val question = viewModel.getCurrentQuestion()
        if (question != null) {
            CurrentQuestionNumber.text = viewModel.QurrentQuestionIndex.value.toString()
            questionTextView.text = question.question
            AboutQuestionText.text = question.dsc
            LoadRadioButtonAnxwer(question)
        } else {
            questionTextView.text = "Loading..."
        }
    }

    private fun StartTest(){
        AboutTestLayout.visibility = View.GONE
        TestLayuot.visibility = View.VISIBLE
        testTimer.StartTimer()
        DisplayCurrentQuestion()
    }

    private fun TestDone(){
        testTimer.StopTimer()
        TestLayuot.visibility = View.GONE
        NavigateToReportAboutTest()
    }
    private fun NavigateToReportAboutTest(){
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.aboutTestFragment, true) // true - удалить
            .build()
        findNavController().navigate(R.id.action_aboutTestFragment_to_testReportFragment,null,navOptions)
    }
    private fun LoadRadioButtonAnxwer(question: Question) {
        answersRadioGroup.removeAllViews() // Очищаем RadioGroup перед добавлением новых RadioButtons
        answersRadioGroup.clearCheck()
        when (question.answers?.size) {
            2 -> {
                val radioButton1 = RadioButton(context)
                radioButton1.text =
                    question.answers.get("1")?.answer ?: ""
                answersRadioGroup.addView(radioButton1)
                val radioButton2 = RadioButton(context)
                radioButton2.text =
                    question.answers.get("2")?.answer ?: ""
                answersRadioGroup.addView(radioButton2)
            }
            3 -> {
                val radioButton1 = RadioButton(context)
                radioButton1.text =
                    question.answers.get("1")?.answer ?: ""
                answersRadioGroup.addView(radioButton1)
                val radioButton2 = RadioButton(context)
                radioButton2.text =
                    question.answers.get("2")?.answer ?: ""
                answersRadioGroup.addView(radioButton2)
                val radioButton3 = RadioButton(context)
                radioButton3.text =
                    question.answers.get("3")?.answer ?: ""
                answersRadioGroup.addView(radioButton3)

            }
        }
    }

        override fun onDestroyView() {
        super.onDestroyView()
        testTimer.StopTimer()
    }

}
package com.android.profkontur.View.Test

import android.R.attr.animation
import android.R.attr.backgroundTint
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.android.profkontur.Model.LoadingState
import com.android.profkontur.Model.MetaData
import com.android.profkontur.Model.QuestViewModelFactory
import com.android.profkontur.Model.Question
import com.android.profkontur.Model.answers
import com.android.profkontur.R
import com.android.profkontur.ViewModel.Methodic.MethodicViewModel
import com.android.profkontur.ViewModel.Test.TestsVIewModel
import com.android.profkontur.timer.TestTimer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AboutTestFragment : Fragment() {


    private  lateinit var questionTextView: TextView
    private  lateinit var AboutQuestionText: TextView
    private  lateinit var TimerText1: TextView
    private  lateinit var TimerText2: TextView
    private  lateinit var TimerText3: TextView
    private  lateinit var TimerText4: TextView
    private  lateinit var NameTestTextView: TextView
    private  lateinit var CurrentQuestionNumber: TextView
    private  lateinit var AllQuestionNUmber: TextView
    private lateinit var answersRadioGroup:RadioGroup
    private lateinit var nextQuestionButton:Button
    private lateinit var ExitButton:FloatingActionButton


    private lateinit var ProgressBar: ProgressBar
    private lateinit var progresstestbar: ProgressBar

    private lateinit var questionCardView: CardView
    private lateinit var AnswerCardView: CardView

    private  lateinit var MetaData: MetaData

    private lateinit var testTimer: TestTimer

    private lateinit var viewModel: TestsVIewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  binding = FragmentAboutTestBinding.inflate(inflater, container, false)
        SetTransitAnimation()
        return inflater.inflate(R.layout.fragment_about_test, container, false)
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector", "DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireParentFragment(), QuestViewModelFactory(requireActivity()))[TestsVIewModel::class.java]

        initTestViews(view)
        CheckbackPresstest()

        nextQuestionButton.setOnClickListener {
            if(answersRadioGroup.checkedRadioButtonId==-1){
                Toast.makeText(context,"Выберите вариант ответа", Toast.LENGTH_SHORT).show()
            }else {
                    SendAnswer(answersRadioGroup.indexOfChild(view.findViewById(answersRadioGroup.checkedRadioButtonId))+1)
            }
        }
        ExitButton.setOnClickListener { ShowDialogAboutFinishedtest() }

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
                        }
                        is LoadingState.Ready -> {
                            //Запуск теста
                            StartTest()
                            AllQuestionNUmber.text = (viewModel.AllTestData.value?.questions?.size).toString()
                            ProgressBar.visibility = View.GONE
                        }
                        is LoadingState.Error -> {
                            ProgressBar.visibility = View.GONE
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
                    val timeString = String.format("%02d%02d", minutes, seconds)
                    val digit1 = timeString[0].toString()
                    val digit2 = timeString[1].toString()
                    val digit3 = timeString[2].toString()
                    val digit4 = timeString[3].toString()
                    if(digit1!=TimerText1.text){
                        animateTimerChange(TimerText1, digit1)
                    }
                    if(digit2!=TimerText2.text){
                        animateTimerChange(TimerText2, digit2)
                    }
                    if(digit3!=TimerText3.text){
                        animateTimerChange(TimerText3, digit3)
                    }
                    if(digit4!=TimerText4.text){
                        animateTimerChange(TimerText4, digit4)
                    }

                }
            }
        }
    }

    fun initTestViews(view: View){
        NameTestTextView= view.findViewById(R.id.NameTestTextView);
        ExitButton= view.findViewById(R.id.ExitButton);
        AnswerCardView= view.findViewById(R.id.AnswerCardView);
        questionCardView= view.findViewById(R.id.questionCardView);
        questionTextView= view.findViewById(R.id.questionTextView);
        AboutQuestionText = view.findViewById(R.id.AboutQuestionText);
        TimerText1 = view.findViewById(R.id.TimerText1);
        TimerText2 = view.findViewById(R.id.TimerText2);
        TimerText3 = view.findViewById(R.id.TimerText3);
        TimerText4 = view.findViewById(R.id.TimerText4);
        ProgressBar = view.findViewById(R.id.ProgressBar);
        progresstestbar= view.findViewById(R.id.progresstestbar);
        CurrentQuestionNumber = view.findViewById(R.id.CurrentQuestionNumber);
        AllQuestionNUmber = view.findViewById(R.id.AllQuestionNUmber);
        answersRadioGroup = view.findViewById(R.id.answersRadioGroup);
        nextQuestionButton = view.findViewById(R.id.nextQuestionButton);
    }

    fun SendAnswer(int: Int){
        viewModel.selectAnswer(int) //Отправляем выбранный ответ
        viewModel.nextQuestion()
        animateCurrentQuestionDisplay()
        updateProgressBar()
    }

    fun DisplayCurrentQuestion(){
        val question = viewModel.getCurrentQuestion()
        if (question != null) {
            CurrentQuestionNumber.text = viewModel.QurrentQuestionIndex.value.toString()
            questionTextView.text = question.question
            AboutQuestionText.text = question.dsc
            LoadRadioButtonAnxwer(question)
            if(CurrentQuestionNumber.text.toString() == AllQuestionNUmber.text.toString()){
                nextQuestionButton.text="Завершить"
            }
        } else {
            questionTextView.text = "Loading..."
        }
    }


    private fun animateCurrentQuestionDisplay() {
        // Создаем анимацию для CardView с вопросом
        val fadeOutQuestion = ObjectAnimator.ofFloat(questionCardView, View.ALPHA, 1f, 0f)
        fadeOutQuestion.duration = 300

        // Создаем анимацию для CardView с ответами
        val fadeOutAnswers = ObjectAnimator.ofFloat(AnswerCardView, View.ALPHA, 1f, 0f)
        fadeOutAnswers.duration = 300

        // Создаем слушатель для выполнения операций после fadeOut анимаций
        val animatorListener = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                questionCardView.isVisible = false
                AnswerCardView.isVisible = false // Добавляем скрытие answersCardView

                // Обновляем текст вопроса и ответов
                DisplayCurrentQuestion()

                // Подготавливаем анимацию появления CardView с вопросом
                questionCardView.alpha = 0f
                questionCardView.isVisible = true

                val fadeInQuestion = ObjectAnimator.ofFloat(questionCardView, View.ALPHA, 0f, 1f)
                fadeInQuestion.duration = 300

                // Подготавливаем анимацию появления CardView с ответами
                AnswerCardView.alpha = 0f
                AnswerCardView.isVisible = true // Делаем answersCardView видимой

                val fadeInAnswers = ObjectAnimator.ofFloat(AnswerCardView, View.ALPHA, 0f, 1f)
                fadeInAnswers.duration = 300

                // Запускаем анимации появления
                fadeInQuestion.start()
                fadeInAnswers.start()
            }
        }

        // Добавляем слушатель к анимации исчезновения вопроса
        fadeOutQuestion.addListener(animatorListener)

        // Запускаем анимации исчезновения
        fadeOutQuestion.start()
        fadeOutAnswers.start()  // Запускаем анимацию исчезновения ответов
    }

    private fun PlusProgressBar() {
        progresstestbar.progress++
    }

    private fun updateProgressBar() {
        val animator = ObjectAnimator.ofInt(progresstestbar, "progress", progresstestbar.progress, viewModel.QurrentQuestionIndex.value* 100)
        animator.setDuration(500)
        animator.setAutoCancel(true)
        animator.setInterpolator(DecelerateInterpolator())
        animator.start()
    }
    private fun SetProgressBar(){
        progresstestbar.max= (viewModel.AllTestData.value?.questions?.size)!!* 100
        progresstestbar.progress=1* 100
    }
    private fun StartTest(){
        SetProgressBar()
        InitTimer()
        testTimer.StartTimer()
        animateCurrentQuestionDisplay()
        NameTestTextView.text = viewModel.AllTestData.value?.meta?.name
    }

    private fun TestDone(){
        testTimer.StopTimer()
        SendReportToMethodicView()
        NavigateToMethodic()
    }
    private fun NavigateToReportAboutTest(){
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.aboutTestFragment, true) // true - удалить
            .build()
    //    findNavController().navigate(R.id.action_aboutTestFragment_to_testReportFragment,null,navOptions)
    }
    private fun NavigateToMethodic(){
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.aboutTestFragment, true) // true - удалить
            .setPopUpTo(R.id.metaTestFragment, true) // true - удалить
            .build()
        findNavController().navigate(R.id.action_aboutTestFragment_to_methodicFragment,null,navOptions)
    }

    private fun SendReportToMethodicView(){
        val MethodicViewModel = ViewModelProvider(requireParentFragment()).get(MethodicViewModel::class.java)
        val TestName = viewModel.AllTestData.value?.meta?.machine_name
        for (test in MethodicViewModel.testList.value!!){
            if(test.machine_name==TestName){
                test.isDone=true
                break
            }
        }
        if (TestName != null) {
            MethodicViewModel.addTest(TestName,convertAnswers(viewModel.selectedAnswers.value))
        }
        MethodicViewModel.TestDOneCountReport()
    }
    fun convertAnswers(selectedAnswers: List<Int?>): answers {
        val result = answers(mutableMapOf())
        selectedAnswers.forEachIndexed { index, answerId ->
            if (answerId != null) {
                val stringList = listOf(answerId.toString())
                result.answer?.put(index+1, stringList)
            }
        }
        if (result.answer?.isEmpty() == true){
            result.answer = null
        }
        return result
    }

    private fun SetTransitAnimation(){
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
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
            4 -> {
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
                val radioButton4 = RadioButton(context)
                radioButton4.text =
                    question.answers.get("4")?.answer ?: ""
                answersRadioGroup.addView(radioButton4)

            }
        }
    }

    fun ShowDialogAboutFinishedtest(){
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Преждевремменное завершение теста")
                .setMessage("Отменить прохождения теста и выйти, результат прохождения не будет сохранен")
                .setNeutralButton("Остаться") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Выйти") { dialog, which ->
                    findNavController().popBackStack()
                }
                .show()
        }
    }
    fun CheckbackPresstest(){
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ShowDialogAboutFinishedtest()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }


    fun animateTimerChange(textView: TextView, newValue: String) {
        val translateYUp = ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, 0f, -textView.height.toFloat())
        val fadeOut = ObjectAnimator.ofFloat(textView, View.ALPHA, 1f, 0f)
        val fadeIn = ObjectAnimator.ofFloat(textView, View.ALPHA, 0f, 1f)
        val translateYDown = ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, textView.height.toFloat(), 0f)

        translateYUp.interpolator = DecelerateInterpolator()
        fadeOut.interpolator = DecelerateInterpolator()
        fadeIn.interpolator = DecelerateInterpolator()
        translateYDown.interpolator = DecelerateInterpolator()

        translateYUp.duration = 250
        fadeOut.duration = 250
        fadeIn.duration = 250
        translateYDown.duration = 250

        translateYUp.doOnEnd {
            textView.text = newValue
            translateYDown.start()
            fadeIn.start()
        }

        fadeOut.start()
        translateYUp.start()
    }



        override fun onDestroyView() {
        super.onDestroyView()
        testTimer.StopTimer()
    }

}
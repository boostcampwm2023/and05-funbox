package com.example.funbox.presentation.game.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.funbox.R
import com.example.funbox.databinding.FragmentAnswerCheckBinding
import com.example.funbox.presentation.BaseDialogFragment

class AnswerCheckFragment : BaseDialogFragment<FragmentAnswerCheckBinding>(R.layout.fragment_answer_check) {

    private val viewModel: QuizViewModel by activityViewModels()


}
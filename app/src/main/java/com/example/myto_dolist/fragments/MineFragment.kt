// MineFragment.kt
package com.example.myto_dolist.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.myto_dolist.ItemAdapter
import com.example.myto_dolist.R
import com.example.myto_dolist.TaskDao
import com.example.myto_dolist.TodoApp
import com.example.myto_dolist.databinding.FragmentMineBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class MineFragment : Fragment() {

    private lateinit var binding: FragmentMineBinding
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var taskDao: TaskDao
    private var countAll: Int = 0
    private var countWork: Int = 0
    private var countPersonal: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskDao = (requireActivity().application as TodoApp).db.taskDao()

        lifecycleScope.launch {
            taskDao.getCountOfCompletedTasks().collect {
                binding.numberOfCompletedTasks.text = it.toString()
            }
        }

        lifecycleScope.launch {
            taskDao.getCountOfIncompleteTasks().collect {
                binding.numberOfPendingTasks.text = it.toString()
            }
        }

        lifecycleScope.launch {
            // Collect data for PieChart
            countAll = taskDao.getCountOfIncompleteAllTasks().first()
            countWork = taskDao.getCountOfIncompleteWorkTasks().first()
            countPersonal = taskDao.getCountOfIncompletePersonalTasks().first()

            // Get the PieChart from the layout
            val pieChart = binding.Piechart
            // Create entries for the PieChart
            val entries = mutableListOf<PieEntry>()
            val colors = mutableListOf<Int>(ContextCompat.getColor(requireContext(), R.color.color1),
                ContextCompat.getColor(requireContext(), R.color.color2),
                ContextCompat.getColor(requireContext(), R.color.color3))

            entries.add(PieEntry(countAll.toFloat(), "All"))
            entries.add(PieEntry(countWork.toFloat(), "Work"))
            entries.add(PieEntry(countPersonal.toFloat(), "Personal"))

            // Create a dataset and give it a type
            val dataSet = PieDataSet(entries, "")
            dataSet.colors = colors

            // Create data object with the dataset
            val data = PieData(dataSet)
            pieChart.data = data

            // Customize the chart
            pieChart.description.isEnabled = false
            pieChart.isDrawHoleEnabled = true
            pieChart.setHoleColor(android.R.color.transparent)
            pieChart.setEntryLabelColor(android.R.color.black)
            pieChart.setEntryLabelTextSize(12f)

            // Refresh the chart
            pieChart.invalidate()
        }
    }
}

package com.example.myto_dolist.fragments

import android.app.Dialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myto_dolist.DateUtils
import com.example.myto_dolist.ItemAdapter
import com.example.myto_dolist.R
import com.example.myto_dolist.TaskDao
import com.example.myto_dolist.TaskEntity
import com.example.myto_dolist.TodoApp
import com.example.myto_dolist.databinding.AddTaskBinding
import com.example.myto_dolist.databinding.FragmentCalendarBinding
import com.example.myto_dolist.databinding.FragmentPersonalBinding
import kotlinx.coroutines.launch
import java.util.Locale


class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var taskDao: TaskDao
    private var handlerAnimation = Handler()

    private lateinit var selectedDateString: String
    private lateinit var currentDateString: String

    private val runnable = object : Runnable {
        override fun run() {
            binding.IvAnimation.animate().scaleX(2f).scaleY(2f).alpha(0f).setDuration(1000)
                .withEndAction {
                    binding.IvAnimation.scaleX = 1f
                    binding.IvAnimation.scaleY = 1f
                    binding.IvAnimation.alpha = 1f
                    handlerAnimation.postDelayed(this, 1000)
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // This allows fragment to handle menu events

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val calendarView = binding.calendarView

        val calendar = Calendar.getInstance()
        val currentDate = calendar.time

        // Format the current date to a readable string format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        currentDateString = dateFormat.format(currentDate)

        // Set the CalendarView to today's date
        calendarView.date = currentDate.time

        // Display the current date string in the TextView
        binding.dateSelected.text = currentDateString

        // Set an OnDateChangeListener to update the TextView when a date is selected
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val date = calendar.time
            selectedDateString = dateFormat.format(date)
            binding.dateSelected.text = selectedDateString

            lifecycleScope.launch {
                taskDao.fetchAllTasksByDate(selectedDateString).collect {
                    val taskList = ArrayList(it)
                    setupListofDataIntoRecyclerView(taskList, taskDao)
                }
            }
        }

        binding.fb.setOnClickListener {
            addTaskDialog()
            Toast.makeText(activity, "HEhe", Toast.LENGTH_LONG).show()
        }

        taskDao = (requireActivity().application as TodoApp).db.taskDao()

//        lifecycleScope.launch {
//            taskDao.fetchAllTasksByDate(currentDateString).collect {
//                val taskList = ArrayList(it)
//                setupListofDataIntoRecyclerView(taskList, taskDao)
//            }
//        }

//        lifecycleScope.launch {
//            taskDao.getCountOfCompletedTasks().collect { count ->
//                binding.tvcompletedtasks.visibility = if (count == 0) View.GONE else View.VISIBLE
//            }
//        }

//        lifecycleScope.launch {
//            taskDao.fetchAllCompleteTasks().collect {
//                val taskList = ArrayList(it)
//                setupListofDataIntoCompletedItemsRecyclerView(taskList, taskDao)
//            }
//        }

//        setupSwipeToDelete(binding.rvItemsList, taskDao)
//        setupSwipeToDelete(binding.rvCompletedItemslist, taskDao)
//        setupDragAndDrop(binding.rvItemsList)
//        setupDragAndDrop(binding.rvCompletedItemslist)
//        runnable.run()

        // Initialize adapters with empty lists to avoid uninitialized exceptions
        itemAdapter = ItemAdapter(arrayListOf()) { item ->
            lifecycleScope.launch {
                taskDao.update(item)
                refreshData()
            }
        }

        binding.rvItemsList.layoutManager = LinearLayoutManager(activity)
        binding.rvItemsList.adapter = itemAdapter


        setupSwipeToDelete(binding.rvItemsList, taskDao, itemAdapter)

        refreshData()

        runnable.run()
    }

    private fun addTaskDialog() {
        val addTaskDialog = Dialog(requireContext(), R.style.Theme_Dialog)
        addTaskDialog.setCancelable(false)
        val binding = AddTaskBinding.inflate(layoutInflater)
        addTaskDialog.setContentView(binding.root)

        val spinnerId = binding.spinner
        val categories = arrayOf("All", "Work", "Personal")
        var categorySelected: String = "All"

        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerId.adapter = arrayAdapter

        spinnerId.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Toast.makeText(activity, "${categories[position]}", Toast.LENGTH_LONG).show()
                categorySelected = categories[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                categorySelected = "All"
            }
        }

        binding.btnaddtask.setOnClickListener {
            val task = binding.ettask.text.toString()

            if (task.isNotEmpty()) {
                val newTask = TaskEntity(task = task, category = categorySelected, date = selectedDateString, isCompleted = false)
                lifecycleScope.launch {
                    try {
                        taskDao.insert(newTask)
                        Toast.makeText(activity, "Task Added", Toast.LENGTH_LONG).show()
                        Log.d("TaskFragment", "Task added successfully")
                    } catch (e: Exception) {
                        Log.e("TaskFragment", "Error adding task", e)
                    }
                }
            } else {
                Toast.makeText(activity, "Task cannot be blank", Toast.LENGTH_LONG).show()
            }
            addTaskDialog.dismiss()
        }
        addTaskDialog.show()
    }


    private fun updatetaskCompletion(id: Int, task: String, category: String,date: String, isChecked: Boolean, taskDao: TaskDao) {
        lifecycleScope.launch {
            taskDao.update(TaskEntity(id, task, category,date, isChecked))
        }
    }

    private fun setupListofDataIntoRecyclerView(
        taskList: ArrayList<TaskEntity>,
        taskDao: TaskDao
    ) {
        itemAdapter.updateData(taskList)
    }

    private fun setupListofDataIntoCompletedItemsRecyclerView(
        completedTaskList: ArrayList<TaskEntity>,
        taskDao: TaskDao
    ) {
        itemAdapter.updateData(completedTaskList)
    }

    private fun setupSwipeToDelete(recyclerView: RecyclerView?, taskDao: TaskDao, itemAdapter: ItemAdapter) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item: TaskEntity = itemAdapter.getItemAtPosition(position)
                Log.d("TaskFragment", "Swiped item: ${item.task}")

                lifecycleScope.launch {
                    taskDao.delete(item)
                    Toast.makeText(requireContext(), "Task deleted successfully", Toast.LENGTH_SHORT).show()
                    refreshData()
                }
            }
        }
        val touchHelper = ItemTouchHelper(itemTouchHelperCallback)
        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun refreshData() {
        val currentDate = DateUtils.getCurrentDateString()
        val date = if (::selectedDateString.isInitialized) selectedDateString else currentDate

        lifecycleScope.launch {
            taskDao.fetchAllTasksByDate(date).collect { incompleteTaskList ->
                itemAdapter.updateData(ArrayList(incompleteTaskList))
            }
        }

//        lifecycleScope.launch {
//            taskDao.fetchAllCompleteTasks(currentDate).collect { completedTaskList ->
//                itemAdapter.updateData(ArrayList(completedTaskList))
//            }
//        }
    }

//    private suspend fun refreshData(taskDao: TaskDao, lifecycleScope: LifecycleCoroutineScope) {
//        val currentDate = DateUtils.getCurrentDateString()
//
//        lifecycleScope.launch {
//            taskDao.fetchAllIncompleteTasks(currentDate).collect { incompleteTaskList ->
//                itemAdapter.updateData(ArrayList(incompleteTaskList))
//            }
//        }
//
//        lifecycleScope.launch {
//            taskDao.fetchAllCompleteTasks(currentDate).collect { completedTaskList ->
//                itemAdapter.updateData(ArrayList(completedTaskList))
//            }
//        }
//    }


}
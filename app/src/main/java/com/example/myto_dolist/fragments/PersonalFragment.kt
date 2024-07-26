package com.example.myto_dolist.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myto_dolist.*
import com.example.myto_dolist.databinding.AddTaskBinding
import com.example.myto_dolist.databinding.FragmentPersonalBinding
import com.example.myto_dolist.databinding.FragmentTaskBinding
import com.example.myto_dolist.databinding.ToobarBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PersonalFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentPersonalBinding
    private lateinit var toolbarBinding: ToobarBinding
    private lateinit var itemAdapter1: ItemAdapter
    private lateinit var itemAdapter2: ItemAdapter
    private lateinit var taskDao: TaskDao
    private var handlerAnimation = Handler()

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
    ): View {
        binding = FragmentPersonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate: String = DateUtils.getCurrentDateString()


        // Set up the toolbar
        toolbarBinding = ToobarBinding.bind(binding.root.findViewById(R.id.toolbar))
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarBinding.root.findViewById(R.id.toolbar))

        taskDao = (requireActivity().application as TodoApp).db.taskDao()

        binding.toolbar.btnall.setOnClickListener {
            Toast.makeText(activity, "Main Page", Toast.LENGTH_LONG).show()
            (requireActivity() as MainActivity).openTaskFragment()
        }

        binding.toolbar.btnwork.setOnClickListener {
            Toast.makeText(activity, "Work Page", Toast.LENGTH_LONG).show()
//            val intent = Intent(activity, WorkActivity::class.java)
//            startActivity(intent)
            (requireActivity() as MainActivity).openWorkFragment()
        }

        binding.toolbar.btnpersonal.setOnClickListener {

        }

        binding.fb.setOnClickListener {
            addTaskDialog()
            Toast.makeText(activity, "HEhe", Toast.LENGTH_LONG).show()
        }

        // Initialize adapters with empty lists to avoid uninitialized exceptions
        itemAdapter1 = ItemAdapter(arrayListOf()) { item ->
            lifecycleScope.launch {
                taskDao.update(item)
                refreshData()
            }
        }
        itemAdapter2 = ItemAdapter(arrayListOf()) { item ->
            lifecycleScope.launch {
                taskDao.update(item)
                refreshData()
            }
        }

        binding.rvItemsList.layoutManager = LinearLayoutManager(activity)
        binding.rvItemsList.adapter = itemAdapter1

        binding.rvCompletedItemslist.layoutManager = LinearLayoutManager(activity)
        binding.rvCompletedItemslist.adapter = itemAdapter2

        setupSwipeToDelete(binding.rvItemsList, taskDao, itemAdapter1)
        setupSwipeToDelete(binding.rvCompletedItemslist, taskDao, itemAdapter2)

        refreshData()

        lifecycleScope.launch {
            taskDao.getCountOfCompletedTasks().collect { count ->
                binding.tvcompletedtasks.visibility = if (count == 0) View.GONE else View.VISIBLE
            }
        }

        runnable.run()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.custom_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.clearFocus()
        searchView?.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val searchQuery = "%$newText%"
        val currentDate = DateUtils.getCurrentDateString()
        lifecycleScope.launch {
            taskDao.searchDatabaseForPersonalIncomplete(searchQuery, currentDate).collect {
                val taskList = ArrayList(it)
                setupListofDataIntoRecyclerView(taskList, taskDao)
            }
        }
        lifecycleScope.launch {
            taskDao.searchDatabaseForPersonalComplete(searchQuery, currentDate).collect {
                val taskList = ArrayList(it)
                setupListofDataIntoCompletedItemsRecyclerView(taskList, taskDao)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentDate = DateUtils.getCurrentDateString()
        return when (item.itemId) {
            R.id.sort -> {
                lifecycleScope.launch {
                    taskDao.fetchPersonalSortedTasks(currentDate).collect {
                        val taskList = ArrayList(it)
                        setupListofDataIntoRecyclerView(taskList, taskDao)
                    }
                }
                lifecycleScope.launch {
                    taskDao.fetchPersonalSortedCompletedTasks(currentDate).collect {
                        val taskList = ArrayList(it)
                        setupListofDataIntoCompletedItemsRecyclerView(taskList, taskDao)
                    }
                }
                true
            }
            R.id.sortbycreattime -> {
                lifecycleScope.launch {
                    taskDao.fetchPersonalSortedTasksByTime(currentDate).collect {
                        val taskList = ArrayList(it)
                        setupListofDataIntoRecyclerView(taskList, taskDao)
                    }
                }
                lifecycleScope.launch {
                    taskDao.fetchPersonalSortedCompletedTasksByTime(currentDate).collect {
                        val taskList = ArrayList(it)
                        setupListofDataIntoCompletedItemsRecyclerView(taskList, taskDao)
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
                val newTask = TaskEntity(task = task, category = categorySelected, isCompleted = false)
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

    private fun setupListofDataIntoRecyclerView(
        taskList: ArrayList<TaskEntity>,
        taskDao: TaskDao
    ) {
        itemAdapter1.updateData(taskList)
    }

    private fun setupListofDataIntoCompletedItemsRecyclerView(
        completedTaskList: ArrayList<TaskEntity>,
        taskDao: TaskDao
    ) {
        itemAdapter2.updateData(completedTaskList)
    }

    private fun updatetaskCompletion(id: Int, task: String, category: String,date: String, isChecked: Boolean, taskDao: TaskDao) {
        lifecycleScope.launch {
            taskDao.update(TaskEntity(id, task, category,date, isChecked))
        }
    }

    private fun setupSwipeToDelete(recyclerView: RecyclerView?, taskDao: TaskDao, itemAdapter: ItemAdapter) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item: TaskEntity = itemAdapter.getItemAtPosition(position)
                Log.d("Position is", "$position")
                Log.d("Item is ", "$item")

                lifecycleScope.launch {
                    taskDao.delete(item)
                    Toast.makeText(requireContext(), "Task deleted successfully", Toast.LENGTH_SHORT).show()
                    refreshData()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

//    private fun setupDragAndDrop(recyclerView: RecyclerView?) {
//        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
//            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
//        ) {
//            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//                val fromPosition = viewHolder.adapterPosition
//                val toPosition = target.adapterPosition
//                itemAdapter.moveItem(fromPosition, toPosition)
//                return true
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//            }
//        }
//        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
//        itemTouchHelper.attachToRecyclerView(recyclerView)
//    }

    private fun refreshData() {
        val currentDate = DateUtils.getCurrentDateString()

        lifecycleScope.launch {
            taskDao.fetchPersonaIncompleteTasks(currentDate).collect { incompleteTaskList ->
                itemAdapter1.updateData(ArrayList(incompleteTaskList))
            }
        }

        lifecycleScope.launch {
            taskDao.fetchPersonalCompleteTasks(currentDate).collect { completedTaskList ->
                itemAdapter2.updateData(ArrayList(completedTaskList))
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
    }
}

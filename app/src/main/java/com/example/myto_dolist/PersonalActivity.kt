//package com.example.myto_dolist
//
//import android.app.Dialog
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.os.Handler
//import android.util.Log
//import android.view.Menu
//import android.view.MenuItem
//import android.view.View
//import android.widget.AdapterView
//import android.widget.ArrayAdapter
//import android.widget.Toast
//import androidx.appcompat.widget.SearchView
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.ItemTouchHelper
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.myto_dolist.databinding.ActivityMainBinding
//import com.example.myto_dolist.databinding.ActivityPersonalBinding
//import com.example.myto_dolist.databinding.AddTaskBinding
//import com.example.myto_dolist.databinding.ToobarBinding
//import kotlinx.coroutines.launch
//
//class PersonalActivity : AppCompatActivity()
//    , SearchView.OnQueryTextListener {
//    private var binding: ActivityPersonalBinding? = null
//    private lateinit var toolbarbinding: ToobarBinding
//    private lateinit var taskDao: TaskDao
//    private lateinit var itemAdapter: ItemAdapter
//
//    private var handlerAnimation = Handler()
//
//    private var runnable = object : Runnable {
//        override fun run() {
//            binding?.IvAnimation?.animate()?.scaleX(2f)?.scaleY(2f)?.alpha(0f)?.setDuration(1000)
//                ?.withEndAction{
//                    binding?.IvAnimation?.scaleX = 1f
//                    binding?.IvAnimation?.scaleY = 1f
//                    binding?.IvAnimation?.alpha = 1f
//                    handlerAnimation.postDelayed(this,1000)
//                }
//
//
//        }
//
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        taskDao = (application as TodoApp).db.taskDao()
//        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_personal)
//
//        binding = ActivityPersonalBinding.inflate(layoutInflater)
//        setContentView(binding!!.root)
//
////        toolbarbinding = ToobarBinding.bind(binding!!.root)
//        toolbarbinding = ToobarBinding.bind(binding!!.root.findViewById(R.id.toolbar))
//        setSupportActionBar(toolbarbinding.root.findViewById(R.id.toolbar))
//        supportActionBar?.title = ""
//
//        toolbarbinding.btnall.setOnClickListener {
//            Toast.makeText(this, "Main Page", Toast.LENGTH_LONG).show()
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//
//        toolbarbinding.btnwork.setOnClickListener {
//            Toast.makeText(this, "Work Page", Toast.LENGTH_LONG).show()
//            val intent = Intent(this, WorkActivity::class.java)
//            startActivity(intent)
//        }
//
//        toolbarbinding.btnpersonal.setOnClickListener {
//            Toast.makeText(this, "Personal Page", Toast.LENGTH_LONG).show()
//        }
//
//        binding?.fb?.setOnClickListener{
//            addTaskDialog()
//            Toast.makeText(this, "HEhe", Toast.LENGTH_LONG).show()
//
//        }
//
//        lifecycleScope.launch {
//            taskDao.fetchPersonaIncompleteTasks().collect{
//                val tasklist = ArrayList(it)
//                setupListofDataIntoRecyclerView(tasklist, taskDao)
//
//            }
//        }
//
//        lifecycleScope.launch {
//            taskDao.getCountOfCompletedTasks().collect{count->
//                if(count == 0)
//                {
//                    binding!!.tvcompletedtasks.visibility = View.GONE
//                }
//                else
//                {
//                    binding!!.tvcompletedtasks.visibility = View.VISIBLE
//                }
//
//            }
//
//        }
//
//        lifecycleScope.launch {
//            taskDao.fetchPersonalCompleteTasks().collect{
//                val tasklist = ArrayList(it)
//                setupListofDataIntoCompletedItemsRecyclerView(tasklist, taskDao)
//
//            }
//
//        }
//
//        setupSwipeToDelete(binding?.rvItemsList, taskDao)
//        setupSwipeToDelete(binding?.rvCompletedItemslist, taskDao)
//        setupDragAndDrop(binding?.rvItemsList)
//        setupDragAndDrop(binding?.rvCompletedItemslist)
////        handlerAnimation.post(runnable)
//        runnable.run()
//
//
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//
//        menuInflater.inflate(R.menu.custom_menu, menu)
//        val searchItem = menu?.findItem(R.id.search)
//
//        if (searchItem == null) {
//            Log.e("MainActivity", "Search menu item is null")
//            return true
//        }
////        Log.d("MainActivity", "searchItem title is: ${searchItem.title}")
////        Log.d("MainActivity", "searchItem actionView is: ${searchItem.actionView}")
//        val searchView = searchItem?.actionView as? SearchView
//        searchView?.clearFocus()
//
//        // Configure the search info and add any event listeners
//        searchView?.setOnQueryTextListener(this)
//        return true
//    }
//
//    override fun onQueryTextSubmit(query: String?): Boolean {
//        return false
//    }
//
//    override fun onQueryTextChange(newText: String?): Boolean {
//        // Handle the search query text change
//        val searchQuery = "%$newText%"
//
//        lifecycleScope.launch {
//            taskDao.searchDatabaseForPersonalIncomplete(searchQuery).collect{
//                val tasklist = ArrayList(it)
//                setupListofDataIntoRecyclerView(tasklist, taskDao)
//
//            }
//        }
//        lifecycleScope.launch {
//            taskDao.searchDatabaseForPersonalComplete(searchQuery).collect{
//                val tasklist = ArrayList(it)
//                setupListofDataIntoCompletedItemsRecyclerView(tasklist, taskDao)
//
//            }
//        }
//
//        return true
//    }
//
//
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        return when(item.itemId)
//        {
////            R.id.search -> {
////                Toast.makeText(this, "Search clicked", Toast.LENGTH_LONG).show()
//////                onQueryTextChange()
////                return true
////            }
//            R.id.sort -> {
//                //write code here to sort items in recyclerview
//                lifecycleScope.launch {
//                    taskDao.fetchPersonalSortedTasks().collect{
//                        val tasklist = ArrayList(it)
//                        setupListofDataIntoRecyclerView(tasklist, taskDao)
//
//                    }
//                }
//                lifecycleScope.launch {
//                    taskDao.fetchPersonalSortedCompletedTasks().collect{
//                        val tasklist = ArrayList(it)
//                        setupListofDataIntoCompletedItemsRecyclerView(tasklist, taskDao)
//
//                    }
//                }
//                return true
//            }
//            R.id.sortbycreattime -> {
//                //write code here to sort items in recyclerview
//                lifecycleScope.launch {
//                    taskDao.fetchPersonalSortedTasksByTime().collect{
//                        val tasklist = ArrayList(it)
//                        setupListofDataIntoRecyclerView(tasklist, taskDao)
//
//                    }
//                }
//                lifecycleScope.launch {
//                    taskDao.fetchPersonalSortedCompletedTasksByTime().collect{
//                        val tasklist = ArrayList(it)
//                        setupListofDataIntoCompletedItemsRecyclerView(tasklist, taskDao)
//
//                    }
//                }
//                return true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//
//    }
//
//    fun addTaskDialog()  {
//        val addTaskDialog = Dialog(this, R.style.Theme_Dialog)
//        addTaskDialog.setCancelable(false)
//        /*Set the screen content from a layout resource.
//         The resource will be inflated, adding all top-level views to the screen.*/
//        val binding = AddTaskBinding.inflate(layoutInflater)
//        addTaskDialog.setContentView(binding.root)
//
//        val spinnerId = binding.spinner
//        val categories = arrayOf("All", "Work", "Personal")
//        var categorySelected : String = "All"
//
//        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
//        spinnerId.adapter = arrayAdapter
//
//        spinnerId.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                Toast.makeText(this@PersonalActivity, "${categories[position]}", Toast.LENGTH_LONG).show()
//                categorySelected = categories[position]
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("Not yet implemented")
//                categorySelected = "All"
//            }
//
//        }
//
//        binding.btnaddtask.setOnClickListener {
//
////            Toast.makeText(this, "Add button is working", Toast.LENGTH_LONG).show()
//
//            val task = binding?.ettask?.text.toString()
//
//            if (task.isNotEmpty()) {
//                val newTask = TaskEntity(task = task, isCompleted = false)
//                lifecycleScope.launch {
//                    try {
//                        taskDao.insert(newTask)
//                        Toast.makeText(applicationContext, "Task Added", Toast.LENGTH_LONG).show()
//                        Log.d("MainActivity", "Task added successfully")
//                    } catch (e: Exception) {
//                        Log.e("MainActivity", "Error adding task", e)
//                    }
//                }
//            } else {
//                Toast.makeText(
//                    applicationContext,
//                    "Task cannot be blank",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//            addTaskDialog.dismiss()
//        }
//
//        //Start the dialog and display it on screen.
//        addTaskDialog.show()
//    }
//
//    private fun setupListofDataIntoRecyclerView(
//        taskList: ArrayList<TaskEntity>,
//        taskDao: TaskDao
//    ){
//        itemAdapter = ItemAdapter(
//            taskList,{
//                    id, task,category, ischecked ->
//                updatetaskCompletion(id, task,category, ischecked, taskDao)
//            }
//        )
//        binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)
//        binding?.rvItemsList?.adapter = itemAdapter
//        binding?.rvItemsList?.visibility = View.VISIBLE
//
//    }
//
//    private fun setupListofDataIntoCompletedItemsRecyclerView(
//        completedtaskList: ArrayList<TaskEntity>,
//        taskDao: TaskDao
//    ){
//        itemAdapter = ItemAdapter(
//            completedtaskList,{
//                    id, task,category, ischecked ->
//                updatetaskCompletion(id, task,category, ischecked, taskDao)
//            }
//        )
//        binding?.rvCompletedItemslist?.layoutManager = LinearLayoutManager(this)
//        binding?.rvCompletedItemslist?.adapter = itemAdapter
//        binding?.rvCompletedItemslist?.visibility = View.VISIBLE
//
//    }
//
//    private fun updatetaskCompletion(id: Int,task: String,category: String, ischecked:Boolean, taskDao: TaskDao)
//    {
//        lifecycleScope.launch {
//            taskDao.update(TaskEntity(id,task, category,ischecked))
//        }
//    }
//
//
//    private fun setupSwipeToDelete(recyclerView: RecyclerView?, taskDao: TaskDao) {
//        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
//            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//                return false // We don't want to support move in this example
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                //Delete Item
//                val position = viewHolder.adapterPosition
//                val item: TaskEntity = itemAdapter.getItemAtPosition(position)
//                Log.d("Position is", "$position")
//                Log.d("Item is ", "$item")
//
//                lifecycleScope.launch {
//                    taskDao.delete(item)
//                }
//            }
//        }
//        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
//        itemTouchHelper.attachToRecyclerView(recyclerView)
//    }
//
//    private fun setupDragAndDrop(recyclerView: RecyclerView?) {
//        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
//            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
//            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//                val fromPosition = viewHolder.adapterPosition
//                val toPosition = target.adapterPosition
//                itemAdapter.moveItem(fromPosition, toPosition)
//                return true
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                // Do nothing
//            }
//        }
//        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
//        itemTouchHelper.attachToRecyclerView(recyclerView)
//    }
//
//}
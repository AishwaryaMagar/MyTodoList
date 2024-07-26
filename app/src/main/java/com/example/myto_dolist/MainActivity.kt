package com.example.myto_dolist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myto_dolist.databinding.ActivityMainBinding
import com.example.myto_dolist.fragments.CalendarFragment
import com.example.myto_dolist.fragments.MineFragment
import com.example.myto_dolist.fragments.PersonalFragment
import com.example.myto_dolist.fragments.TaskFragment
import com.example.myto_dolist.fragments.WorkFragment

class MainActivity : AppCompatActivity()
{

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val taskFragment = TaskFragment()
        val calendarFragment = CalendarFragment()
        val mineFragment = MineFragment()

        makeCurrentFragment(taskFragment)

        binding!!.bottomnav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_tasks -> makeCurrentFragment(taskFragment)
                R.id.ic_calendar -> makeCurrentFragment(calendarFragment)
                R.id.ic_mine -> makeCurrentFragment(mineFragment)
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }

    fun openTaskFragment() {
        val taskFragment = TaskFragment()
        makeCurrentFragment(taskFragment)
    }

    fun openWorkFragment() {
        val workFragment = WorkFragment()
        makeCurrentFragment(workFragment)
    }

    fun openPersonalFragment() {
        val personalFragment = PersonalFragment()
        makeCurrentFragment(personalFragment)
    }


}
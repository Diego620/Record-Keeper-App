package com.diegocupido.recordkeeper

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.commit
import com.diegocupido.recordkeeper.cycling.CyclingFragment
import com.diegocupido.recordkeeper.databinding.ActivityMainBinding
import com.diegocupido.recordkeeper.running.RunningFragment
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setOnItemSelectedListener(this)

        supportFragmentManager.commit {
            replace(R.id.frame_content, RunningFragment())
        }
    }

    private fun onRunningClicked(): Boolean {
        supportFragmentManager.commit {
            replace(R.id.frame_content, RunningFragment())
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuClickHandled =  when (item.itemId) {
            R.id.reset_running->{
                showConfirmationDialog(RUNNING_DISPLAY_VALUE)
                true
            }
            R.id.reset_cycling->{
                showConfirmationDialog(CYCLING_DISPLAY_VALUE)
                true
            }
            R.id.reset_all->{
                showConfirmationDialog(ALL_DISPLAY_VALUE)
                true
            }
            else->{
                super.onOptionsItemSelected(item)
            }
        }

        return menuClickHandled
    }

    private fun showConfirmationDialog(selection: String) {
        AlertDialog.Builder(this)
            .setTitle("Reset $selection records")
            .setMessage("Are you sure you want to clear the records")
            .setPositiveButton("Yes") { _, _ ->
                when(selection){
                    ALL_DISPLAY_VALUE ->{
                        getSharedPreferences(RunningFragment.FILENAME, MODE_PRIVATE).edit { clear() }
                        getSharedPreferences(CyclingFragment.FILENAME, MODE_PRIVATE).edit { clear() }
                    }
                    RUNNING_DISPLAY_VALUE ->{
                        getSharedPreferences(RunningFragment.FILENAME, MODE_PRIVATE).edit { clear() }
                    }
                    CYCLING_DISPLAY_VALUE ->{
                        getSharedPreferences(CyclingFragment.FILENAME, MODE_PRIVATE).edit { clear() }
                    }
                }
                refreshCurrentFragment()
                showConfirmation()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showConfirmation() {
        val snackbar =
            Snackbar.make(binding.root, "Records cleared successfully!", Snackbar.LENGTH_LONG)
        snackbar.anchorView = binding.bottomNav
        snackbar.show()
    }

    private fun refreshCurrentFragment() {
        when (binding.bottomNav.selectedItemId) {
            R.id.nav_running -> {
                onRunningClicked()
            }

            R.id.nav_cycling -> {
                onCyclingClicked()
            }

            else -> {}
        }
    }

    private fun onCyclingClicked(): Boolean {
        supportFragmentManager.commit {
            replace(R.id.frame_content, CyclingFragment())
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.nav_cycling -> onCyclingClicked()
        R.id.nav_running -> onRunningClicked()
        else -> false
    }
    companion object{
        const val RUNNING_DISPLAY_VALUE = "running"
        const val CYCLING_DISPLAY_VALUE = "cycling"
        const val ALL_DISPLAY_VALUE = "all"
    }
}

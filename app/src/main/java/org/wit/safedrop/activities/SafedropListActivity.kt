package org.wit.safedrop.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.safedrop.R
import org.wit.safedrop.adapters.SafedropAdapter
import org.wit.safedrop.adapters.SafedropListener
import org.wit.safedrop.databinding.ActivitySafedropListBinding
import org.wit.safedrop.main.MainApp
import org.wit.safedrop.models.SafedropModel

class SafedropListActivity : AppCompatActivity(), SafedropListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivitySafedropListBinding
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySafedropListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = SafedropAdapter(app.safedrops.findAll(),this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, SafedropActivity::class.java)
                getResult.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

     private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.
                notifyItemRangeChanged(0,app.safedrops.findAll().size)
            }
        }

    override fun onSafedropClick(safedrop: SafedropModel, pos : Int) {
        val launcherIntent = Intent(this, SafedropActivity::class.java)
        launcherIntent.putExtra("safedrop_edit", safedrop)
        position = pos
        getClickResult.launch(launcherIntent)
    }

    private val getClickResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.
                notifyItemRangeChanged(0,app.safedrops.findAll().size)
            }
            else // Deleting
            if (it.resultCode == 99)
                (binding.recyclerView.adapter)?.notifyItemRemoved(position)
        }
}

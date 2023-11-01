package org.wit.safedrop.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.safedrop.R
import org.wit.safedrop.databinding.ActivitySafedropBinding
import org.wit.safedrop.main.MainApp
import org.wit.safedrop.models.Location
import org.wit.safedrop.models.SafedropModel
import org.wit.safedrop.showImagePicker
import timber.log.Timber.Forest.i


class SafedropActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySafedropBinding
    var safedrop = SafedropModel()
    lateinit var app: MainApp
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        edit = false

        binding = ActivitySafedropBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        val spinnerSeverity = findViewById<Spinner>(R.id.severityLevel)
        val severityArr = resources.getStringArray(R.array.severity_level).toList()


        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.severity_level,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinnerSeverity.setAdapter(adapter);

        app = application as MainApp

        i("Safedrop Activity started...")



        if (intent.hasExtra("safedrop_edit")) {
            edit = true
            safedrop = intent.extras?.getParcelable("safedrop_edit")!!
            binding.safedropTitle.setText(safedrop.title)
            binding.description.setText(safedrop.description)
            binding.severityLevel.setSelection(severityArr.indexOf(safedrop.severityLevel))
            binding.btnAdd.setText(R.string.save_safedrop)
            Picasso.get()
                .load(safedrop.image)
                .into(binding.safedropImage)
            if (safedrop.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_safedrop_image)
            }

        }

        binding.btnAdd.setOnClickListener() {
            safedrop.title = binding.safedropTitle.text.toString()
            safedrop.description = binding.description.text.toString()
            safedrop.severityLevel = binding.severityLevel.selectedItem.toString()
            if (safedrop.title.isEmpty()) {
                Snackbar.make(it,R.string.enter_safedrop_title, Snackbar.LENGTH_LONG)
                        .show()
            } else {
                if (edit) {
                    app.safedrops.update(safedrop.copy())
                } else {
                    app.safedrops.create(safedrop.copy())
                }
            }
            i("add Button Pressed: $safedrop")

            setResult(RESULT_OK)
            finish()
        }

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher,this)
        }

        binding.safedropLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            if (safedrop.zoom != 0f) {
                location.lat =  safedrop.lat
                location.lng = safedrop.lng
                location.zoom = safedrop.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }

        registerImagePickerCallback()
        registerMapCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_safedrop, menu)
        if (edit) menu.getItem(0).isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_delete -> {
                setResult(99)
                app.safedrops.delete(safedrop)
                finish()
            }
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")

                            val image = result.data!!.data!!
                            contentResolver.takePersistableUriPermission(image,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            safedrop.image = image

                            Picasso.get()
                                .load(safedrop.image)
                                .into(binding.safedropImage)
                            binding.chooseImage.setText(R.string.change_safedrop_image)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Location ${result.data.toString()}")
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            i("Location == $location")
                            safedrop.lat = location.lat
                            safedrop.lng = location.lng
                            safedrop.zoom = location.zoom
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}

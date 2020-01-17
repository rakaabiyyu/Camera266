package com.rakaabiyyu.cameramaster

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val permission :Array<String> = arrayOf<String>(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private var imageUri: Uri? = null
    private val permissionCode = 12
    private val cameraCode = 123
    private val permissionChoose = 13
    private val chooseCode = 1234
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        takePhoto.setOnClickListener(this)
        choosePhoto.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.takePhoto -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(
                            this.applicationContext,
                            permission[0]
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            this.applicationContext,
                            permission[1]
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        startCamera()
                    } else {
                        ActivityCompat.requestPermissions(this, permission,
                            permissionCode)
                    }
                } else {
                    startCamera()
                }
            }
            R.id.choosePhoto -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(
                            this.applicationContext,
                            permission[2]
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        chooseImage()
                    } else {
                        ActivityCompat.requestPermissions(this, permission,
                            permissionCode)
                    }
                } else {
                    chooseImage()
                }
            }
        }
    }
    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, chooseCode)
    }
    private fun startCamera() {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //untuk mengambil images dr camera
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "IMG" + timeStamp +
                ".jpg")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Take from the camera")
        imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri) //picture di save di imageUri
        startActivityForResult(cameraIntent, cameraCode)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions:
    Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions,
            grantResults)
        when(requestCode){
            permissionCode -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    startCamera()
                }else{
                    Toast.makeText(this, "Permission denied!",
                        Toast.LENGTH_SHORT).show()
                }
            }
            permissionChoose -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    Toast.makeText(
                        this, "Permission denied!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data:
    Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (resultCode == cameraCode){
                photoDisplay.setImageURI(imageUri)
                /*val path = getPathFromUri(imageUri!!)
                Toast.makeText(this, path, Toast.LENGTH_SHORT).show()*/
            }else{
                imageUri = data?.getData() as Uri
                photoDisplay.setImageURI(imageUri)
                /*val path = getPathFromUri(imageUri!!)
                Toast.makeText(this, path, Toast.LENGTH_SHORT).show()*/

            }
        }
    }
}
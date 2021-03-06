/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taxi.android.city.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.taxi.android.city.DataKeeper
import com.taxi.android.city.MyFirebaseMessagingService
import com.taxi.android.city.R
import com.taxi.android.city.databinding.FragmentEggTimerBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.pawegio.kandroid.visible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_egg_timer.*
import java.io.File

class EggTimerFragment : Fragment() {
    private val supportChatManager: MyFirebaseMessagingService? = null
    internal var Database_Path = "All_Image_Uploads_Database"
    var mStorageRef: StorageReference? = null
    val REQUEST_RUNTIME_PERMISSION = 1
    var checkCound = 0


    val REQUEST_IMAGE_CAPTURE = 1
    var storageReference: StorageReference? = null
    var databaseReference: DatabaseReference? = null

    private val PICK_IMAGE_CODE = 2
    private var Test = 0
    private var imageUri: Uri? = null
    private val TOPIC = "breakfast"
    var imageFile: File?            = null

    private lateinit var binding: FragmentEggTimerBinding
    val permission = Manifest.permission.CAMERA

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path)
        storageReference = FirebaseStorage.getInstance().reference
        mStorageRef = FirebaseStorage.getInstance().getReference()

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_egg_timer, container, false
        )
        val viewModel = ViewModelProviders.of(this).get(EggTimerViewModel::class.java)

        binding.eggTimerViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())


        createChannel(
            getString(R.string.egg_notification_channel_id),
            getString(R.string.egg_notification_channel_name)
        )
        createChannel(
            getString(R.string.breakfast_notification_channel_id),
            getString(R.string.breakfast_notification_channel_name)
        )
        subscribeTopic()

        binding.passwordSelect.setOnClickListener {
            Test = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPremission()//    checkPermissions()
            } else {
                checkPremission()//   pickFromGallery()
            }
        }

        binding.adressCelect.setOnClickListener {
            Test = 1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPremission()//    checkPermissions()
            } else {
                checkPremission()//   pickFromGallery()
            }
        }
        binding.VUFaceSelect.setOnClickListener {
            Test = 2
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPremission()//    checkPermissions()
            } else {
                checkPremission()//   pickFromGallery()
            }
        }

        binding.VUBackSelect.setOnClickListener {
            Test = 3
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPremission()//    checkPermissions()
            } else {
                checkPremission()//   pickFromGallery()
            }
        }





        binding.button2.isEnabled = false
        binding.button2.isClickable = false
        binding.button2.setOnClickListener {
            activity?.finish()
            Toast.makeText(context,"Ваши данные успешно отправлены на проверку с вами свяжуться",Toast.LENGTH_LONG).show()
        }


        binding.passwordDownload.setOnClickListener {
            Test = 0
            upload()

        }
        binding.adressDownload.setOnClickListener {
            Test = 1
            upload()
        }


        binding.VUFaseDownload.setOnClickListener {
            Test = 2
            upload()

        }
        binding.VUBackDownload.setOnClickListener {
            Test = 3
            upload()
        }
        return binding.root
    }

    private fun saveFullImage() {
        val intent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file =  File(
            Environment.getExternalStorageDirectory(),
                "test.jpg")
        imageUri = Uri.fromFile(file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, REQUEST_RUNTIME_PERMISSION)
    }

    fun upload() {
       // val file = Uri.fromFile(File("path/to/images/profile.jpg"))
        if (imageUri != null) {
            //todo profile - вместо этого ключ для нотификации и ФИО
        var string =""
            when (Test){
                0->{
                    string = "___Фото паспорта"

                } 1->{
                string = "___Фото прописки"

            } 2->{
                string = "___Фото ВУ(лицевая сторона)"

            } 3->{
                string = "___Фото ВУ(обратная сторона)"

            }
            }

            val tokken = DataKeeper.getClientName(context!!)
            val riversRef = mStorageRef?.child(
                "drivers/____" + tokken +string
            )
            binding.progressBar.visible = true
            riversRef?.putFile(imageUri!!)
                ?.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    // Get a URL to the uploaded content
                    // val downloadUrl = taskSnapshot.getDownloadUrl()
                    taskSnapshot

                    Toast.makeText(context, "Фото успешно загружено!", Toast.LENGTH_SHORT).show()
                    val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                    when (Test) {
                        0 -> {
                            binding.passwordDownload.setImageResource(R.drawable.ic_verified)
                            checkCound = 1

                        }
                        1 -> {
                            binding.adressDownload.setImageResource(R.drawable.ic_verified)
                            checkCound = 2

                        }
                        2 -> {
                            binding.VUFaseDownload.setImageResource(R.drawable.ic_verified)
                            checkCound = 3
                        }
                        3 -> {
                            binding.VUBackDownload.setImageResource(R.drawable.ic_verified)
                            checkCound = 4
                        }
                    }
                    if (checkCound == 4){
                        binding.button2.isEnabled = true
                        binding.button2.isClickable = true
                    }
                    binding.progressBar.visible = false

                })
                ?.addOnFailureListener(OnFailureListener {
                    // Handle unsuccessful uploads
                    Toast.makeText(
                        context,
                        "Ошибка загрузки попроуйте позже или нет интернета!",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.visible = false
                })
                ?.addOnProgressListener {taskSnapshot->
                    val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                }
        }
        else {
            Toast.makeText(context, "Сделайте снимок!", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkPremission() {
      //  saveFullImage()

          //select whi

          val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
          if (ContextCompat.checkSelfPermission(
                  context!!,
                  permission
              ) != PackageManager.PERMISSION_GRANTED
          ) {
              if (ActivityCompat.shouldShowRequestPermissionRationale(
                      activity!!,
                      permission
                  )

              ) {

              } else {
                  ActivityCompat.requestPermissions(
                      activity!!,
                      arrayOf(permission),
                      REQUEST_RUNTIME_PERMISSION
                  )
              }
              checkPremission()
          } else {
              // you have permission go ahead launch service
             // SomeTask()
            //  UploadImageFileToFirebaseStorage()
            //  neW()
              //upload()
              saveFullImage()
          }
    }

    override fun onRequestPermissionsResult(requestCode:Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_RUNTIME_PERMISSION -> {
                val numOfRequest = grantResults.size
                val isGranted =
                    numOfRequest == 1 && PackageManager.PERMISSION_GRANTED == grantResults[numOfRequest - 1]
                if (isGranted) {
                    // you have permission go ahead
                    SomeTask()
                } else {
                    // you dont have permission show toast
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun SomeTask() {
        val firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.getReferenceFromUrl("gs://project-1913949624994167419.appspot.com")
        val storageReference2nd = storageReference!!.child("All_image_uploads/"/*Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(imageUri!!)*/)

        storageReference2nd?.putFile(imageUri!!)
            ?.addOnSuccessListener { taskSnapshot ->
                // Getting image name from EditText and store into string variable.
                val TempImageName = "TEST"
                val imageUploadInfo =
                    ImageUploadInfo(TempImageName, taskSnapshot.uploadSessionUri.toString())
                // Getting image upload ID.
                val ImageUploadId = databaseReference?.push()?.key

                // Adding image upload id s child element into databaseReference.
                databaseReference?.child(ImageUploadId!!)?.setValue(imageUploadInfo)
            }
            // If something goes wrong .
            ?.addOnFailureListener { exception ->
                exception
            }
        storageReference!!.getBytes(java.lang.Long.MAX_VALUE).addOnSuccessListener { bytes ->
            var bytes = bytes
        }.addOnFailureListener { exception ->
            // Handle any errors
            exception
        }
    }

    private fun pickFromGallery() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        val chooserIntent = Intent.createChooser(getIntent, getString(R.string.gcm_defaultSenderId))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        startActivityForResult(chooserIntent, PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)  {

        if (requestCode == REQUEST_RUNTIME_PERMISSION && resultCode == RESULT_OK) {
            // Проверяем, содержит ли результат маленькую картинку

              //  binding.ciAvatar.setImageURI(imageUri)
            imageUri
            if (imageUri != null) {
               // imageUri = data.data
                when (Test){
                    0-> {
                        passwordAv.setImageURI(imageUri)
                        Picasso.get().load(imageUri).placeholder(R.drawable.placeholder)
                            .into(binding.passwordAv)
                    }
                    1->{
                        Picasso.get().load(imageUri).placeholder(R.drawable.placeholder)
                            .into(binding.adressAv)
                    }
                    2->{
                        Picasso.get().load(imageUri).placeholder(R.drawable.placeholder)
                            .into(binding.VUFaseAV)
                    }
                    3->{
                        Picasso.get().load(imageUri).placeholder(R.drawable.placeholder)
                            .into(binding.VUBackAv)
                    }
                }
            }
            else {
                Toast.makeText(context, "Ошибка загрузки!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.breakfast_notification_channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    private fun subscribeTopic() {
        // [START subscribe_topic]
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            .addOnCompleteListener { task ->
                var message = getString(R.string.message_subscribed)
                if (!task.isSuccessful) {
                    message = getString(R.string.message_subscribe_failed)
                }
             //   Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        // [END subscribe_topics]
    }

    companion object {
        fun newInstance() = EggTimerFragment()
    }

}


package com.android.enoc.enoclinksampleapp.ui.main.ui.userprofile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.android.enoc.enoclinksampleapp.BR
import com.android.enoc.enoclinksampleapp.BuildConfig
import com.android.enoc.enoclinksampleapp.MainActivity
import com.android.enoc.enoclinksampleapp.R
import com.android.enoc.enoclinksampleapp.camera.component.Gravatar
import com.android.enoc.enoclinksampleapp.databinding.UserDetailsFragmentBinding
import com.android.enoc.enoclinksampleapp.utils.Util
import com.android.enoc.enoclinksampleapp.utils.Utils
import com.android.enoc.enoclinksampleapp.viewModel.UserDetailsViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.user_details_fragment.*
import kotlinx.android.synthetic.main.user_details_fragment.view.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


private const val REQ_CAPTURE = 100
const val ONE_MEGA_BYTE = 1000000

@AndroidEntryPoint
class UserDetailsFragment : Fragment() {

    private var queryImageUrl: String = ""
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var imgPath: String = ""
    private var imageUri: Uri? = null
    private val args: UserDetailsFragmentArgs by navArgs()
    private val permissions = arrayOf(Manifest.permission.CAMERA)
    lateinit var  mContext:Context
    lateinit var dataBinding:UserDetailsFragmentBinding


    @Inject
    lateinit var userDetailsModel: UserDetailsViewModel
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
        chooseImage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event

            }
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = UserDetailsFragmentBinding.inflate(inflater, container, false)
        dataBinding.setLifecycleOwner(this);
        dataBinding.setVariable(BR.viewModel, userDetailsModel);
        dataBinding.executePendingBindings();

        return dataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        dataBinding.loading.visibility= View.VISIBLE
        dataBinding.imageView.setOnClickListener {
            if (isPermissionsAllowed(permissions, true, REQ_CAPTURE)) {
                resultLauncher.launch(getPickImageIntent())
            }

        }
        userDetailsModel.init()
        userDetailsModel.getUserInfo(args)
        userDetailsModel.emailId.observe(viewLifecycleOwner, Observer { emailId ->
            emailId ?: return@Observer
            loading.visibility= View.GONE
            initiateImageVerification()
        })


    }

    private fun initiateImageVerification() {

         val imgHash = Utils.convertEmailToHash(userDetailsModel.emailId.value.toString())
        val bitmap = Util.loadBitmap(mContext, imgHash + ".png")
        if (bitmap == null) {
            loadImageFromUrl()
        } else {
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun loadImageFromUrl() {
        var mAvatarImageViewPixelSize =
            getResources().getDimensionPixelSize(R.dimen.avatar_image_view_size);
        var gravatarUrl =
            Gravatar.init()?.with(userDetailsModel.emailId.value)?.forceDefault()?.defaultImage(
                Gravatar.DefaultImage.RETRO
            )?.size(mAvatarImageViewPixelSize)
                ?.build()!!
        if (!userDetailsModel.imageUrl.value.isNullOrBlank())
            gravatarUrl = userDetailsModel.imageUrl.value.toString()
        gravatarUrl?.let { imageLoad(it, false) }
    }

    fun isPermissionsAllowed(
        permissions: Array<String>,
        shouldRequestIfNotAllowed: Boolean = false,
        requestCode: Int = -1
    ): Boolean {
        var isGranted = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                isGranted = ContextCompat.checkSelfPermission(
                    activity as MainActivity,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
                if (!isGranted)
                    break
            }
        }
        if (!isGranted && shouldRequestIfNotAllowed) {
            if (requestCode.equals(-1))
                throw RuntimeException("Send request code in third parameter")
            requestRequiredPermissions(permissions, requestCode)
        }

        return isGranted
    }

    fun requestRequiredPermissions(permissions: Array<String>, requestCode: Int) {
        val pendingPermissions: ArrayList<String> = ArrayList()
        permissions.forEachIndexed { index, permission ->
            if (ContextCompat.checkSelfPermission(
                    activity as MainActivity,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            )
                pendingPermissions.add(permission)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val array = arrayOfNulls<String>(pendingPermissions.size)
            pendingPermissions.toArray(array)
            requestPermissions(array, requestCode)
        }
    }

    fun isAllPermissionsGranted(grantResults: IntArray): Boolean {
        var isGranted = true
        for (grantResult in grantResults) {
            isGranted = grantResult.equals(PackageManager.PERMISSION_GRANTED)
            if (!isGranted)
                break
        }
        return isGranted
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_CAPTURE -> {
                if (isAllPermissionsGranted(grantResults)) {
                    resultLauncher.launch(getPickImageIntent())
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.permission_not_granted),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun chooseImage() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    handleImageRequest(data)
                }
            }
    }

    private fun getPickImageIntent(): Intent? {
        var chooserIntent: Intent? = null

        var intentList: MutableList<Intent> = java.util.ArrayList()

        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> {
                takePhotoIntent.putExtra(
                    "android.intent.extras.CAMERA_FACING",
                    CameraCharacteristics.LENS_FACING_FRONT
                )  // Tested on API 24 Android version 7.0(Samsung S6)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                takePhotoIntent.putExtra(
                    "android.intent.extras.CAMERA_FACING",
                    CameraCharacteristics.LENS_FACING_FRONT
                ) // Tested on API 27 Android version 8.0(Nexus 6P)
                takePhotoIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                takePhotoIntent.putExtra("camerafacing", "front")
                takePhotoIntent.putExtra("previous_mode", "front")
            }
            else -> takePhotoIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        }
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri())

        intentList = addIntentsToList((activity as MainActivity), intentList, pickIntent)
        intentList = addIntentsToList((activity as MainActivity), intentList, takePhotoIntent)

        if (intentList.size > 0) {
            chooserIntent = Intent.createChooser(
                intentList.removeAt(intentList.size - 1),
                getString(R.string.select_capture_image)
            )
            chooserIntent!!.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                intentList.toTypedArray<Parcelable>()
            )
        }

        return chooserIntent
    }

    private fun setImageUri(): Uri {
        val folder =
            File("${(activity as MainActivity).getExternalFilesDir(Environment.DIRECTORY_DCIM)}")
        folder.mkdirs()

        val file = File(folder, ".jpg")
        if (file.exists())
            file.delete()
        file.createNewFile()
        imageUri = FileProvider.getUriForFile(
            (activity as MainActivity),
            BuildConfig.APPLICATION_ID + getString(R.string.file_provider_name),
            file
        )
        imgPath = file.absolutePath
        return imageUri!!
    }


    private fun addIntentsToList(
        context: Context,
        list: MutableList<Intent>,
        intent: Intent
    ): MutableList<Intent> {
        val resInfo = context.packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resInfo) {
            val packageName = resolveInfo.activityInfo.packageName
            val targetedIntent = Intent(intent)
            targetedIntent.setPackage(packageName)
            list.add(targetedIntent)
        }
        return list
    }

    private fun handleImageRequest(data: Intent?) {
        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
            Toast.makeText(
                context,
                t.localizedMessage ?: getString(R.string.some_err),
                Toast.LENGTH_SHORT
            ).show()
        }

        GlobalScope.launch(Dispatchers.Main + exceptionHandler) {


            if (data?.data != null) {     //Photo from gallery
                imageUri = data.data
                queryImageUrl = imageUri?.toString()!!
            } else {
                queryImageUrl = imgPath ?: ""
            }

            if (queryImageUrl.isNotEmpty()) {
                imageLoad(queryImageUrl, true)

            }

        }

    }

    private fun imageLoad(url: String, imageUpload: Boolean) {
       val imgHash= userDetailsModel.emailId.value?.let { Utils.convertEmailToHash(it) }
        Glide.with(this@UserDetailsFragment)
            .asBitmap()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    imageView.setImageBitmap(resource)
                    Util.saveBitmap(mContext, resource,
                        imgHash+ ".png" )
                    UploadImage(imageUpload,resource,imgHash)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    private fun UploadImage(uploadImage: Boolean, resource: Bitmap,imgHash:String?) {
        if(uploadImage) {
            loading.visibility= View.VISIBLE
            setImageUploadObserver()
            val byteArray = Util.convertBitmapToByteArray(resource, ONE_MEGA_BYTE)
            byteArray?.let { userDetailsModel.uploadImage(it,imgHash+ ".png",args) }
        }
    }

    private fun setImageUploadObserver() {
        userDetailsModel.imageUpLoaded.observe(viewLifecycleOwner, Observer { imageUpLoaded ->
            imageUpLoaded ?: return@Observer
            loading.visibility= View.GONE
           updateUiOnImageUpload(userDetailsModel.imageUpLoaded.value)
        })
    }

    private fun updateUiOnImageUpload(imageUploaded: Boolean?) {
        if (imageUploaded == true) {
            Toast.makeText(
                    context,
                    getString(R.string.successful_imageload),
                    Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                    context,
                    getString(R.string.failed_imageload),
                    Toast.LENGTH_SHORT
            ).show()
        }
    }


}

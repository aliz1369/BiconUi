package com.tivasoft.biconui.ui.common

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tivasoft.biconui.data.source.local.ChatMessagesDao
import com.tivasoft.biconui.ui.doctor.tab_doctor_profile.expanded.ExpandedScheduleFragmentDirections
import com.tivasoft.biconui.ui.patient.tab_userprofile.UserProfileExpandedFragmentDirections
import com.tivasoft.biconui.utils.InputStreamRequestBody
import com.dropbox.core.v2.teamlog.EventType.logout
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.tivasoft.biconui.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.pushy.sdk.Pushy
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import java.io.File
import java.io.FileWriter
import java.io.IOException
import kotlin.jvm.Throws
import javax.inject.Inject


/**
 * BaseFragment Class configure some settings here
 */

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    var tempCameraUri: Uri? = null
    var tempVideoCameraUri: Uri? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var chatMessagesDao: ChatMessagesDao


    protected fun showLongToast(message: String?) {
        if (message != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    protected fun showShortToast(message: String?) {
        if (message != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }


    protected fun getColorRes(colorRes: Int): Int {
        return if (activity != null) ContextCompat.getColor(requireActivity(), colorRes) else 0
    }

    protected fun getDrawableRes(drawableRes: Int): Drawable? {
        return ContextCompat.getDrawable(requireActivity(), drawableRes)
    }

    protected fun showViewWithAnimation(view: View) {
        view.visibility = View.VISIBLE
        view.alpha = 0.0f
        // Start the animation
        view.animate()
            .translationY(0f)
            .alpha(1.0f)
            .duration = 300
    }

    protected fun animated(animation: String): Animation? {
        val result = when (animation) {
            "fadein" -> AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            "fadeout" -> AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
            "slidein" -> AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in)
            "slideinleft" -> AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_left)
            "slideout" -> AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out)
            "slideoutright" -> AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.slide_out_right
            )
            "slideup" -> AnimationUtils.loadAnimation(requireContext(),R.anim.slide_up)
            "slideupout" -> AnimationUtils.loadAnimation(requireContext(),R.anim.slide_up_out)
            "scale" -> AnimationUtils.loadAnimation(requireContext(), R.anim.scale)
            "scalein" -> AnimationUtils.loadAnimation(requireContext(), R.anim.scalein)
            else -> {}
        }
        return result as Animation?
    }

    protected fun initLoading(loading: View?, show: Boolean) {
        loading!!.visibility = if (show) View.VISIBLE else View.GONE
    }


    protected fun showSnackBar(layout: View, msg: String, onclick: (() -> Unit)?) {
        val mSnackBar = Snackbar.make(layout, msg, Snackbar.LENGTH_LONG)
            .setAction("بستن") {
                onclick.let { click ->
                    click?.invoke()
                }
            }
            .setActionTextColor(getColorRes(R.color.biconRed))
            .show()
    }

    /**
     * Open Camera via Intent and GetResult from it in onActivityResult.
     * in android 10+ use tempCameraUri in onActivityResult instead of the provided Intent.
     */
    fun openCamera() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT >= 29) {
            tempCameraUri = createImageFile()
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempCameraUri)
        }
        startActivityForResult(takePhotoIntent, TAKE_PHOTO)
    }

    /**
     * Open File Picker via Intent and GetResult from it in onActivityResult.
     */
    fun openCamera(isSelectorEnabled: Boolean) {
        if (!isSelectorEnabled) {
            openCamera()
            return
        }
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        tempCameraUri = createImageFile()
        tempVideoCameraUri = createVideoFile()
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempCameraUri)
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempVideoCameraUri)
        val chooserIntent =
            Intent.createChooser(takePictureIntent, getString(R.string.camera_chooser))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takeVideoIntent))
        startActivityForResult(chooserIntent, CAPTURE_MEDIA)
    }

    /**
     * Open File Picker via Intent and GetResult from it in onActivityResult.
     */
    fun openGallery(type: String = "image") {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "$type/*"
        startActivityForResult(intent, if (type == "image") IMAGE_CHOOSE else VIDEO_CHOOSE)
    }

    /**
     * Creates a Image File via ScopedAccess for Camera in android 10+.
     *
     * @return Uri of the Created Image File.
     */
    private fun createImageFile(): Uri? {
        return if (Build.VERSION.SDK_INT >= 29) {
            deleteFile("${Environment.getExternalStorageDirectory().path}/Pictures/Bicon/temp_img.jpg")
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Bicon")
            contentValues.put(MediaStore.Images.Media.TITLE, "temp_img")
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "temp_img.jpg")
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            contentValues.put(
                MediaStore.Images.Media.DATE_ADDED,
                System.currentTimeMillis() / 1000
            )
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        } else {
            deleteFile(
                requireContext().externalMediaDirs.first().absolutePath +
                        "/${Environment.DIRECTORY_PICTURES}/temp_img.jpg"
            )
            val file = createMediaFile(
                fileName = "temp_img.jpg",
                directory = Environment.DIRECTORY_PICTURES,
            )
            FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider",
                file
            )
        }
    }

    /**
     * Show Setting Dialog.
     *
     *
     */
    fun showSettingsDialog() {
        val builder =
            AlertDialog.Builder(context)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton(
            "GOTO SETTINGS"
        ) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    /**
     * Open Setting.
     *
     *
     */
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context?.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    /**
     * Show Image.
     * @param uri string of file path
     *
     *
     */
    /* fun imagePopUp(uri: String) {
         val builder: AlertDialog.Builder = AlertDialog.Builder(context)
         val layoutInflater = layoutInflater

         val customView: View = layoutInflater.inflate(R.layout.image_viewr, null)
         val imageView = customView.findViewById<ImageView>(R.id.show_image)
         Picasso.get().load(Uri.parse(uri)).into(imageView)
         builder.setView(customView)
         builder.create()
         builder.show()
     }*/

    /**
     * Request Camera Permission.
     */
    fun requestCameraPermission(isSelectorEnabled: Boolean = false) {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    openCamera(isSelectorEnabled)
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    response?.let {
                        if (response.isPermanentlyDenied) {
                            showSettingsDialog()
                        }
                    }
                }
            }).check()
    }

    /**
     * Get File  form Assets.
     * @param context application context
     * @param fileName name of file
     */
    @Throws(IOException::class)
    fun getFileFromAssets(context: Context, fileName: String): File =
        File(context.cacheDir, fileName)
            .also {
                if (!it.exists()) {
                    it.outputStream().use { cache ->
                        context.assets.open(fileName).use { inputStream ->
                            inputStream.copyTo(cache)
                        }
                    }
                }
            }

    /**
     * Get MultiPart.
     * @param Uri uri of file
     * @param type type of file
     * @return Multipart
     */
    fun getMultiPartFile(uri: Uri, type: String): MultipartBody.Part {
        val contentResolver = requireContext().contentResolver
        val mediaType = type.toMediaType()
        val contentType = InputStreamRequestBody(mediaType, contentResolver, uri)
        val name = getFileName(uri)
        return MultipartBody.Part.createFormData("file", name, contentType)
    }

    /**
     * Get File Name form Assets.
     * @param Uri uri of file
     */
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context?.contentResolver?.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }

    protected fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.exit)
            .setMessage(R.string.exit_message)
            .setPositiveButton(R.string.logout) { dialog, _ ->
                dialog.dismiss()
                logout()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        sharedPreferences.edit()
            .clear()
            .apply()
        CoroutineScope(Dispatchers.IO).launch {
            chatMessagesDao.deleteAll()
            Pushy.unregister(requireContext())
        }
        val action = if (sharedPreferences.getInt("accountType", 1) == 1)
            UserProfileExpandedFragmentDirections.actionSignOut()
        else
            ExpandedScheduleFragmentDirections.actionSignOut()
        findNavController().navigate(action)
    }

    protected fun writeIntoTextFile(
        gameId: String,
        doctorId: String,
        patientId: String
    ) {
        val file = createTextFile()
        if (file.exists())
            file.delete()
        file.createNewFile()
        val writer = FileWriter(file)
        writer.append(gameId)
        writer.append("\r\n")
        writer.append(doctorId)
        writer.append("\r\n")
        writer.append(patientId)
        writer.flush()
        writer.close()
    }

    private fun createTextFile(): File {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            File(requireContext().getExternalFilesDir(null), "file.txt")
        } else {
            File(requireContext().filesDir, "file.txt")
        }
    }

    protected fun openFileManagerForDocuments() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        val mimeTypes = arrayOf(
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain",
            "application/pdf",
            "application/zip"
        )
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, FILES)
    }

    protected fun createVoiceFile(): File {
        val name = "${System.currentTimeMillis()}.aac"
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            File(requireContext().getExternalFilesDir("voices"), name)
        } else {
            File(requireContext().filesDir, name)
        }
    }

    private fun createVideoFile(): Uri? {
        return if (Build.VERSION.SDK_INT >= 29) {
            deleteFile("${Environment.getExternalStorageDirectory().path}/Movies/Bicon/temp_video.mp4")
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Bicon")
            contentValues.put(MediaStore.Video.Media.TITLE, "temp_video")
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, "temp_video.mp4")
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            contentValues.put(
                MediaStore.Video.Media.DATE_ADDED,
                System.currentTimeMillis() / 1000
            )
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
            requireContext().contentResolver.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        } else {
            deleteFile(
                requireContext().externalMediaDirs.first().absolutePath +
                        "/${Environment.DIRECTORY_MOVIES}/temp_video.mp4"
            )
            val file = createMediaFile(
                fileName = "temp_video.mp4",
                directory = Environment.DIRECTORY_MOVIES,
            )
            FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider",
                file
            )
        }
    }

    private fun createMediaFile(
        fileName: String,
        directory: String
    ): File = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        val parent =
            File("${requireContext().externalMediaDirs.first().absolutePath}/$directory")
        if (!parent.exists())
            parent.mkdirs()
        File(parent, fileName)
    } else {
        File(requireContext().externalMediaDirs.first(), fileName)
    }

    private fun deleteFile(path: String) {
        val file =
            File(path)
        if (file.exists())
            file.delete()
    }

    companion object {
        val IMAGE_CHOOSE = 1000
        val TAKE_PHOTO = 1001
        val PERMISSION_CODE = 1003
        val FILES = 1004
        val VIDEO_CHOOSE = 1005
        const val CAPTURE_MEDIA = 1006
    }

}
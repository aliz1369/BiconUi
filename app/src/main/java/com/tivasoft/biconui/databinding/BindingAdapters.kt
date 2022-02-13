package com.tivasoft.biconui.databinding

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import br.com.simplepass.loadingbutton.customViews.CircularProgressImageButton
import com.tivasoft.biconui.data.model.local.ui.LookForXStatus
import com.tivasoft.biconui.data.model.local.ui.Message
import com.tivasoft.biconui.data.model.local.ui.MessageType
import com.tivasoft.biconui.data.model.local.ui.ScheduleItem
import com.tivasoft.biconui.data.model.local.ui.chat.SocketMessageType
import com.tivasoft.biconui.data.model.network.responses.common.ConversationData
import com.tivasoft.biconui.data.model.network.responses.common.PackageItems
import com.tivasoft.biconui.data.source.local.PrescriptionsDao
import com.tivasoft.biconui.utils.VoiceUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stfalcon.imageviewer.StfalconImageViewer
import com.tivasoft.biconui.R
import com.unity3d.player.UnityPlayerActivity
import kotlinx.coroutines.*
import java.io.File
import java.io.FileWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BindingAdapters @Inject constructor(
    private val prescriptionsDao: PrescriptionsDao,
    private val sharedPreference: SharedPreferences
) {
    @BindingAdapter("backgroundRes")
    fun setBackground(view: View, type: Int) {
        when (type) {
            1 -> view.setBackgroundResource(R.drawable.rounded_gradient_yellow)
            2 -> view.setBackgroundResource(R.drawable.rounded_gradient_blue)
            3 -> view.setBackgroundResource(R.drawable.rounded_gradient_green)
            4 -> view.setBackgroundResource(R.drawable.rounded_gradient_blue)
            else -> view.setBackgroundResource(R.drawable.rounded_gradient_yellow)
        }
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("setScheduleType")
    fun setScheduleType(view: TextView, type: Int) {
        when (type) {
            1 -> view.text = "Video Call"
            2 -> view.text = "Voice Call"
            3 -> view.text = "Chat"
        }
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("setPackagePrice")
    fun setPackagePrice(view: TextView, price: Double) {
        view.text = price.toInt().toString() + " " + "monthly"
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("setPackagePeriod")
    fun setPackagePeriod(view: TextView, period: Int) {
        view.text = "$period month"
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("addItem")
    fun addItem(view: TextView, items: PackageItems) {
        view.text = "\\u25CF ${items.name} ${items.unit} ${items.value}"
    }

    @BindingAdapter("setScheduleTypeIcon")
    fun setScheduleTypeIcon(view: ImageView, type: Int) {
        view.setImageResource(type)
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("start", "end")
    fun getScheduleTime(view: TextView, start: Long, end: Long) {
        val formatter: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start: String = formatter.format(Date(start))
        val end: String = formatter.format(Date(end))
        view.text = "$start - $end"
    }

    @BindingAdapter("date")
    fun getScheduleDate(view: TextView, date: Long) {
        val formatter: DateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val date: String = formatter.format(Date(date))
        view.text = date
    }

    @BindingAdapter("doctorConversationItemClicked")
    fun setOnItemClickListener(view: View, id: Int) {
//        view.setOnClickListener {
//            (it.context as FragmentActivity).supportFragmentManager
//                .commit {
//                    setReorderingAllowed(true)
//                    val fragment = ConversationFragment()
//                    val bundle = Bundle()
//                    bundle.putBoolean("isPatient", false)
//                    bundle.putInt("conversationId", id)
//                    fragment.arguments = bundle
//                    add(R.id.fragment_bottom_sheet, fragment)
//                }
//        }
    }

    @BindingAdapter("setIndicator")
    fun setIndicator(view: View, messageType: Int) {
        val resourceId = when (messageType) {
            1 -> R.color.profile_background
            2 -> R.drawable.indicator_new_request
            3 -> R.drawable.indicator_test_result
            4 -> R.drawable.indicator_new_message
            else -> R.drawable.indicator_new_request
        }
        view.setBackgroundResource(resourceId)
    }

    @BindingAdapter("setRequestVisibility")
    fun setVisibility(view: View, messageType: Int) {
        view.visibility = when (messageType) {
            2 -> View.VISIBLE
            else -> View.GONE
        }
    }

    @BindingAdapter("setChartVisibility")
    fun setChartVisibility(view: View, conversation: ConversationData) {
        view.visibility = when {
            conversation.patientStatus == 4 -> View.GONE
            conversation.newMessageType == 2 -> View.GONE
            else -> View.VISIBLE
        }
    }

    @BindingAdapter("itemAlpha")
    fun setAlpha(view: View, patientStatus: Int) {
        view.alpha = when (patientStatus) {
            3 -> 0.45F
            else -> 1F
        }
    }

    @BindingAdapter("bookVisibility")
    fun setVisibility(view: View, isCategory: Boolean) {
        view.visibility = if (isCategory) View.VISIBLE else View.GONE
    }

    @BindingAdapter("getTextFromStatus")
    fun setPatientStatus(view: TextView, status: Int) {
        val text = when (status) {
            1 -> "New Patient"
            2 -> "Current Patient"
            3 -> "Previous Patient"
            else -> "Current Doctors"
        }
        view.text = text
    }

    @BindingAdapter("setChatImage")
    fun setChatImage(view: ImageView, url: String) {
        Glide.with(view.context).load(url).into(view)
        view.setOnClickListener {
            StfalconImageViewer.Builder(view.context, arrayOf(url)) { imageView, image ->
                Glide.with(view.context).load(image).into(imageView)
            }.show()
        }
    }

    @BindingAdapter("fileName")
    fun setFileName(view: TextView, url: String) {
        val fileName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."))
        view.text = fileName
    }

    @BindingAdapter("fileFormat")
    fun setFileFormat(view: TextView, url: String) {
        val fileName = url.substring(url.lastIndexOf(".") + 1)
        view.text = fileName.toUpperCase(Locale.ENGLISH)
    }

    @BindingAdapter("fileClickListener")
    fun setOnFileClickListener(view: CircularProgressImageButton, url: String) {
        val file = createMediaFile(
            fileName = url.substring(url.lastIndexOf("/") + 1),
            directory = Environment.DIRECTORY_DOCUMENTS,
            context = view.context
        )
        if (!file.exists()) {
            view.setImageResource(R.drawable.ic_download)
        } else {
            view.setImageResource(R.drawable.ic_play)
        }
        view.setOnClickListener {
            if (!file.exists()) {
                downloadFile(file, url, view)
            } else {
                val uri = FileProvider.getUriForFile(
                    view.context,
                    view.context.applicationContext.packageName + ".provider",
                    file
                )
                viewFile(uri, file.extension, view.context)
            }
        }
    }

    @BindingAdapter("playVoice")
    fun playVoice(view: CircularProgressImageButton, url: String) {
        val file = createMediaFile(
            fileName = url.substring(url.lastIndexOf("/") + 1),
            directory = Environment.DIRECTORY_MUSIC,
            context = view.context
        )
        if (!file.exists()) {
            view.setImageResource(R.drawable.ic_download)
        } else {
            view.setImageResource(R.drawable.ic_play)
        }
        view.setOnClickListener {
            if (!file.exists()) {
                downloadFile(file, url, view)
            } else {
                val pauseRes = ResourcesCompat.getDrawable(
                    view.resources,
                    R.drawable.ic_pause,
                    view.context.theme
                )?.constantState
                if (view.background.constantState == pauseRes) {
                    view.setImageResource(R.drawable.ic_play)
                    VoiceUtils.pauseAudio()
                } else {
                    if (file.extension.equals("caf", true)) {
                        Toast.makeText(
                            view.context,
                            R.string.media_not_supported,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val uri = Uri.fromFile(file)
                        view.setImageResource(R.drawable.ic_pause)
                        VoiceUtils.playAudio(view.context, uri)
                    }
                }
                VoiceUtils.player.apply {
                    setOnCompletionListener {
                        view.setImageResource(R.drawable.ic_play)
                        reset()
                    }
                }
            }
        }
    }

    @BindingAdapter("setVideoImage")
    fun setVideoImage(view: ImageView, url: String) {
        val requestOption = RequestOptions.frameOf(1000)
        Glide.with(view.context).load(url).apply(requestOption).into(view)
    }

    @BindingAdapter("playVideo")
    fun playVideo(view: CircularProgressImageButton, url: String) {
        val file = createMediaFile(
            fileName = url.substring(url.lastIndexOf("/") + 1),
            directory = Environment.DIRECTORY_MOVIES,
            context = view.context
        )
        if (!file.exists()) {
            view.setImageResource(R.drawable.ic_download)
        } else {
            view.setImageResource(R.drawable.ic_play)
        }
        view.setOnClickListener {
            if (!file.exists()) {
                downloadFile(file, url, view)
            } else {
                view.setImageResource(R.drawable.ic_play)
                val uri = FileProvider.getUriForFile(
                    view.context,
                    view.context.applicationContext.packageName + ".provider",
                    file
                )
                viewFile(uri, file.extension, view.context)
            }
        }
    }

    private fun downloadFile(file: File, url: String, view: CircularProgressImageButton) {
        /*  view.startAnimation()
          val uri = FileProvider.getUriForFile(
              view.context,
              view.context.applicationContext.packageName + ".provider",
              file
          )
          val ktor = HttpClient(Android)
          view.context.contentResolver.openOutputStream(uri)?.let { outputStream ->
              CoroutineScope(Dispatchers.IO).launch {
                  ktor.downloadFile(DownloadEntity(outputStream, url)).collect {
                      withContext(Dispatchers.Main) {
                          when (it) {
                              is DownloadResult.Idle -> Unit
                              is DownloadResult.Success -> {
                                  val type = view.context.contentResolver.getType(uri)
                                  if (type != null) {
                                      if (!type.contains("audio", true))
                                          viewFile(uri, file.extension, view.context)
                                      else
                                          VoiceUtils.playAudio(view.context, uri)
                                  }
                                  view.revertAnimation()
                                  view.setImageResource(R.drawable.ic_play)
                              }
                              is DownloadResult.Error -> {
                                  view.revertAnimation()
                                  Log.e("TAG_DL", it.message)
                                  file.delete()
                                  Toast.makeText(
                                      view.context,
                                      "an error occurred while downloading the file...",
                                      Toast.LENGTH_LONG
                                  ).show()
                              }
                              is DownloadResult.Progress -> {
                              }
                          }
                      }
                  }
              }
          }*/
    }

    private fun viewFile(uri: Uri, fileExt: String, context: Context) {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.setDataAndType(uri, mimeType)
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, R.string.no_suitable_app_found, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun createMediaFile(
        fileName: String,
        directory: String,
        context: Context
    ): File =
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val parent = File("${context.externalMediaDirs.first().absolutePath}/$directory")
            if (!parent.exists())
                parent.mkdirs()
            File(parent, fileName)
        } else {
            File(context.externalMediaDirs.first(), fileName)
        }

    @BindingAdapter("setChatTime")
    fun setChatTime(view: TextView, date: String) {
        val time = date.substring(date.indexOf("T") + 1, date.lastIndexOf(":"))
        view.text = time
    }

    @BindingAdapter("prescriptionClickListener")
    fun setPrescriptionClickListener(view: View, message: Message) {
        view.setOnClickListener {
            if (message.contentType == SocketMessageType.GAME) {
                writeIntoTextFile(
                    gameId = message.content,
                    doctorId = if (message.messageType == MessageType.INCOMING) message.sender else "",
                    patientId = if (message.messageType == MessageType.INCOMING) message.receiver else "",
                    view.context
                )
                val intent = Intent(view.context, UnityPlayerActivity::class.java)
                view.context.startActivity(intent)
            }
        }
    }

    private fun writeIntoTextFile(
        gameId: String,
        doctorId: String,
        patientId: String,
        context: Context
    ) {
        val file = createTextFile(context)
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

    private fun createTextFile(context: Context): File {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            File(context.getExternalFilesDir(null), "file.txt")
        } else {
            File(context.filesDir, "file.txt")
        }
    }

    @BindingAdapter("setPrescriptionName")
    fun setPrescriptionName(view: TextView, message: Message) {
        CoroutineScope(Dispatchers.IO).launch {
            view.text = prescriptionsDao.get(message.content).title
        }
    }

    @BindingAdapter("setRoundedImageFromUrl")
    fun setRoundedImageFromUrl(view: ImageView, url: String?) {
        Glide.with(view.context).load(url)
            .placeholder(R.drawable.rounded_edit_text)
            .error(R.drawable.rounded_edit_text)
            .into(view)
    }

    @BindingAdapter("setScheduleName")
    fun setScheduleName(view: TextView, scheduleItem: ScheduleItem) {
        when (sharedPreference.getInt("accountType", 1) == 1) {
            true -> view.text = scheduleItem.doctorName
            false -> view.text = scheduleItem.patientName
        }
    }

    @BindingAdapter("setScheduleIcon")
    fun setScheduleIcon(view: ImageView, scheduleItem: ScheduleItem) {
        val url = when (sharedPreference.getInt("accountType", 1) == 1) {
            true -> scheduleItem.doctorIcon
            false -> scheduleItem.patientIcon
        }
        Glide.with(view.context).load(url)
            .placeholder(R.drawable.rounded_edit_text)
            .error(R.drawable.rounded_edit_text)
            .into(view)
    }

    @BindingAdapter("setDate")
    fun setDate(view: TextView, dateString: String) {
        try {
            val now = Calendar.getInstance()
            val calendar = Calendar.getInstance()

            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            calendar.time = format.parse(dateString)!!

            val result: String = when {
                now.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) &&
                        now.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                        now.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) -> {
                    view.context.getString(R.string.today)
                }
                else -> {
                    StringBuilder().append(
                        calendar.getDisplayName(
                            Calendar.MONTH, Calendar.LONG, Locale.getDefault()
                        )
                    )
                        .append(" ${calendar.get(Calendar.DAY_OF_MONTH)}, ")
                        .append(calendar.get(Calendar.YEAR))
                        .toString()
                }
            }
            view.text = result
        } catch (_: Exception) {
        }
    }

    @BindingAdapter("handleAcceptVisibility")
    fun handleAcceptVisibility(view: TextView, status: Int) {
        view.visibility = if (sharedPreference.getBoolean("isAssistant", false)) {
            when (status) {
                LookForXStatus.NEW_REQUEST.ordinal -> View.GONE
                LookForXStatus.ACCEPTED_FROM_DOCTOR.ordinal -> View.VISIBLE
                LookForXStatus.REJECTED_FROM_DOCTOR.ordinal -> View.GONE
                LookForXStatus.ACCEPTED_FROM_ASSISTANT.ordinal -> View.GONE
                LookForXStatus.REJECTED_FROM_ASSISTANT.ordinal -> View.GONE
                LookForXStatus.DISCONNECTED.ordinal -> View.GONE
                LookForXStatus.ALREADY_CONNECTED.ordinal -> View.GONE
                else -> View.GONE
            }
        } else {
            when (status) {
                LookForXStatus.NEW_REQUEST.ordinal -> View.VISIBLE
                LookForXStatus.ACCEPTED_FROM_DOCTOR.ordinal -> View.GONE
                LookForXStatus.REJECTED_FROM_DOCTOR.ordinal -> View.GONE
                LookForXStatus.ACCEPTED_FROM_ASSISTANT.ordinal -> View.GONE
                LookForXStatus.REJECTED_FROM_ASSISTANT.ordinal -> View.GONE
                LookForXStatus.DISCONNECTED.ordinal -> View.GONE
                LookForXStatus.ALREADY_CONNECTED.ordinal -> View.GONE
                else -> View.GONE
            }
        }
    }

    @BindingAdapter("handleRejectVisibility")
    fun handleRejectVisibility(view: TextView, status: Int) {
        view.visibility = if (sharedPreference.getBoolean("isAssistant", false)) {
            when (status) {
                LookForXStatus.NEW_REQUEST.ordinal -> View.VISIBLE
                LookForXStatus.ACCEPTED_FROM_DOCTOR.ordinal -> View.VISIBLE
                LookForXStatus.REJECTED_FROM_DOCTOR.ordinal -> View.GONE
                LookForXStatus.ACCEPTED_FROM_ASSISTANT.ordinal -> View.VISIBLE
                LookForXStatus.REJECTED_FROM_ASSISTANT.ordinal -> View.GONE
                LookForXStatus.DISCONNECTED.ordinal -> View.GONE
                LookForXStatus.ALREADY_CONNECTED.ordinal -> View.GONE
                else -> View.GONE
            }
        } else {
            when (status) {
                LookForXStatus.NEW_REQUEST.ordinal -> View.VISIBLE
                LookForXStatus.ACCEPTED_FROM_DOCTOR.ordinal -> View.VISIBLE
                LookForXStatus.REJECTED_FROM_DOCTOR.ordinal -> View.GONE
                LookForXStatus.ACCEPTED_FROM_ASSISTANT.ordinal -> View.VISIBLE
                LookForXStatus.REJECTED_FROM_ASSISTANT.ordinal -> View.GONE
                LookForXStatus.DISCONNECTED.ordinal -> View.GONE
                LookForXStatus.ALREADY_CONNECTED.ordinal -> View.GONE
                else -> View.GONE
            }
        }
    }

    @BindingAdapter("setStatusText")
    fun setStatusText(view: TextView, status: Int) {
        view.text = when (status) {
            LookForXStatus.NEW_REQUEST.ordinal -> {
                if (sharedPreference.getBoolean("isAssistant", false)) {
                    "Pending"
                } else {
                    "New Request"
                }
            }
            else -> LookForXStatus.values()[status].status
        }
    }
}
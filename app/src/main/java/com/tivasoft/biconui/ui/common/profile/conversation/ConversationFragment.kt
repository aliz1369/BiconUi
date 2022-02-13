package com.tivasoft.biconui.ui.common.profile.conversation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tivasoft.biconui.data.model.local.ui.chat.*
import com.tivasoft.biconui.data.model.network.requests.MeetRequest
import com.tivasoft.biconui.data.model.network.responses.common.ChatMessage
import com.tivasoft.biconui.ui.common.profile.backpack.BackpackTabAdapter
import com.tivasoft.biconui.ui.common.profile.adapter.ConversationAdapter
import com.tivasoft.biconui.ui.common.tests.TestViewModel
import com.tivasoft.biconui.utils.*
import com.tivasoft.biconui.utils.channel_intents.MeetIntent
import com.tivasoft.biconui.utils.channel_intents.SocketIntent
import com.tivasoft.biconui.utils.channel_intents.TestIntent
import com.google.android.material.tabs.TabLayoutMediator
import com.tivasoft.biconui.R
import com.tivasoft.biconui.data.model.local.ui.Message
import com.tivasoft.biconui.data.model.local.ui.MessageType
import com.tivasoft.biconui.databinding.BottomSheetChatBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.utils.ChatItemClickListener
import com.unity3d.player.UnityPlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import java.io.File
import java.io.IOException
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ConversationFragment : BaseFragment(), ChatItemClickListener {

    private var _binding: BottomSheetChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var backpackTabAdapter: BackpackTabAdapter

    private val viewModel: ConversationViewModel by viewModels()
    private val testViewModel: TestViewModel by viewModels()
    private val args: ConversationFragmentArgs by navArgs()

    private var isPatient = true
    private lateinit var userId: String
    private lateinit var conversationId: String
    private var meetType = "voice"

    private var recorder: MediaRecorder? = null
    private var recorderFile: File? = null

    private var startTime = 0L
    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private var meetJob: Job? = null
    private var backpackJob: Job? = null
    private var fileJob: Job? = null
    private var conversationJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isPatient = sharedPreferences.getInt("accountType", 1) == 1
        conversationId = args.conversationId
        userId = sharedPreferences.getString("userId", "") ?: ""
        sharedPreferences.edit().putString("conversationId", conversationId).apply()

        setupAdapters()
        setupPagers()
        setupConversationList()
        subscribeObservers()
        subscribeFileObserver()
        lifecycleScope.launch {
            val patientId = if (!isPatient) conversationId else null
            testViewModel.channel.send(TestIntent.GetBackPack(patientId))
        }

        binding.apply {
            name.text = args.name
            chatEdittext.imeOptions = EditorInfo.IME_ACTION_SEND
            chatEdittext.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    val message = SocketMessage(
                        sender = userId,
                        receiver = conversationId,
                        content = chatEdittext.text.toString()
                    )
                    chatEdittext.setText("")
                    sendIntentToSocketChannel(SocketIntent.SendMessage(message))
                    return@setOnEditorActionListener true
                }
                false
            }
            chatEdittext.addTextChangedListener(chatTextWatcher)
            chatActionCall.setOnClickListener {
                setBackgroundTint(
                    extraLayout = extraActionsLayout,
                    actionButton = it,
                    otherLayout = backpackLayout,
                    otherActionButton = backpack
                )
            }
            backpack.setOnClickListener {
                setBackgroundTint(
                    extraLayout = backpackLayout,
                    actionButton = it,
                    otherLayout = extraActionsLayout,
                    otherActionButton = chatActionCall
                )
            }
            chatBack.setOnClickListener {
                findNavController().popBackStack()
            }
            chatEdittext.setOnClickListener {
                hideExtraLayouts()
                lifecycleScope.launch {
                    val currentChatId = sharedPreferences.getString(
                        "currentChatId", ""
                    ) ?: ""
                    val seenInfo = SeenInfo(userId, currentChatId)
                    viewModel.channel.send(SocketIntent.SeenMessages(seenInfo))
                }
            }
            actionExtraVideoCall.setOnClickListener {
                meetType = "video"
                sendMeetIntent()
            }
            actionExtraCall.setOnClickListener {
                meetType = "voice"
                sendMeetIntent()
            }
            actionExtraPhoto.setOnClickListener {
                openGallery()
            }
            actionExtraFiles.setOnClickListener {
                openFileManagerForDocuments()
            }
            actionExtraVideo.setOnClickListener {
                openGallery("video")
            }
            actionExtraCamera.setOnClickListener {
                requestCameraPermission(true)
            }
            actionExtraVoice.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        view.performClick()
                        if (requireActivity().checkSelfPermission(
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            actionExtraVoice.backgroundTintList = getTintList(true)!!
                            startRecording()
                        } else
                            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 3000)
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_BUTTON_RELEASE,
                    MotionEvent.ACTION_OUTSIDE,
                    MotionEvent.ACTION_CANCEL -> {
                        stopRecording()
                        actionExtraVoice.backgroundTintList = getTintList(false)!!
                    }
                }
                false
            }
            if (!isPatient) {
                actionExtraPrescription.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        //     (requireActivity() as MainActivity).setPrescriptionBottomSheetState(true)
                    }
                }
            }
        }

        sendIntentToSocketChannel(SocketIntent.ConnectToSocket)
        val roomInfo = SocketRoomInfo(
            thisUserId = userId,
            thisUserName = "",
            otherUserId = conversationId,
            otherUserName = ""
        )
        sendIntentToSocketChannel(SocketIntent.JoinToRoom(roomInfo))
    }

    private fun setupAdapters() {
        backpackTabAdapter = BackpackTabAdapter(this, arrayListOf())
        conversationAdapter = ConversationAdapter(isPatient, userId)
        conversationAdapter.setOnItemClickListener(this)
    }

    private fun setupPagers() {
        binding.apply {
            backpackPager.adapter = backpackTabAdapter
            TabLayoutMediator(backpackTabLayout, backpackPager) { _, _ ->
            }.attach()
        }
    }

    private fun setupConversationList() {
        binding.conversationList.adapter = conversationAdapter
        binding.conversationList.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            v.onTouchEvent(event)
            true
        }
        conversationJob = lifecycleScope.launch {
            viewModel.getConversations(userId, conversationId).collect {
                conversationAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }

    /**
     * Set BackgroundTint and visibility of Call and Backpack Buttons and their
     * respective layouts.
     *
     * @param extraLayout is the layout that should be showed.
     * @param actionButton is the Selected Button that needs to have a BackgroundTint.
     * @param otherLayout is the layout that should be hidden.
     * @param otherActionButton is the deselected Button that should be reset without BackgroundTint.
     */
    private fun setBackgroundTint(
        extraLayout: View,
        actionButton: View,
        otherLayout: View,
        otherActionButton: View
    ) {
        closeKeyboard()
        otherLayout.visibility = View.GONE
        otherActionButton.backgroundTintList = getTintList(false)!!
        when (extraLayout.visibility) {
            View.GONE -> {
                extraLayout.visibility = View.VISIBLE
                actionButton.backgroundTintList = getTintList(true)!!
            }
            else -> {
                extraLayout.visibility = View.GONE
                actionButton.backgroundTintList = getTintList(false)!!
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(runnable)
        meetJob?.cancel()
        backpackJob?.cancel()
        fileJob?.cancel()
        conversationJob?.cancel()
    }

    /**
     * Get ColorStateList for BackgroundTint.
     *
     * @param isActive Indicates whether or not the button is selected or deselected.
     *
     * @return ColorStateList based on isActive value
     */
    private fun getTintList(isActive: Boolean): ColorStateList? {
        return if (isActive)
            ContextCompat.getColorStateList(requireContext(), R.color.black_translucent)
        else ContextCompat.getColorStateList(requireContext(), R.color.background)
    }

    /**
     * Hide soft keyboard.
     */
    private fun closeKeyboard() {
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            binding.chatEdittext.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    /**
     * hide any extra layout ant reset backgroundTint of their associated button.
     */
    private fun hideExtraLayouts() {
        binding.apply {
            chatActionCall.backgroundTintList = getTintList(false)!!
            backpack.backgroundTintList = getTintList(false)!!
            extraActionsLayout.visibility = View.GONE
            backpackLayout.visibility = View.GONE
        }
    }

    override fun onItemClickListener(message: Message) {
        when (message.contentType) {
            SocketMessageType.GAME -> {
                writeIntoTextFile(
                    gameId = message.content,
                    doctorId = if (message.messageType == MessageType.INCOMING) message.sender else "",
                    patientId = if (message.messageType == MessageType.INCOMING) message.receiver else ""
                )
                val intent = Intent(requireContext(), UnityPlayerActivity::class.java)
                startActivity(intent)
            }
            /* SocketMessageType.BREATH ->
                 (requireActivity() as MainActivity).setTestsNavGraph(null)*/
            else -> Unit
        }
    }

    private fun sendIntentToSocketChannel(intent: SocketIntent) {
        lifecycleScope.launch {
            viewModel.channel.send(intent)
        }
    }

    private val chatTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val isTyping = IsTyping(name = "test")
            sendIntentToSocketChannel(SocketIntent.UserIsTyping(isTyping))
        }

        override fun afterTextChanged(s: Editable?) {
        }

    }

    private fun subscribeObservers() {
        subscribeObserverForMeet()
        subscribeObserverForBackPack()
    }

    private fun subscribeObserverForMeet() {
        meetJob = lifecycleScope.launch {
            viewModel.meetItem.collect { meetDataState ->
                when (meetDataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        /*    (requireActivity() as MainActivity).setupMeet(
                                meetDataState.data.item.roomName,
                                meetType
                            )*/
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            meetDataState.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", meetDataState.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    private fun subscribeObserverForBackPack() {
        backpackJob = lifecycleScope.launch {
            testViewModel.backpack.collect { backPackDataState ->
                when (backPackDataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        Log.e("TAG", "size: ${backPackDataState.data.size}")
                        backpackTabAdapter =
                            BackpackTabAdapter(
                                this@ConversationFragment,
                                ArrayList(backPackDataState.data)
                            )
                        setupPagers()
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            backPackDataState.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", backPackDataState.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    private fun sendMeetIntent() {
        sharedPreferences.edit()
            .putString("conversationId", conversationId)
            .putString("meetType", meetType)
            .apply()
        lifecycleScope.launch {
            val entity = MeetRequest(
                thisUserId = userId,
                otherUserId = conversationId,
                type = meetType
            )
            viewModel.meetChannel.send(MeetIntent.Call(entity))
        }
    }

    private fun subscribeFileObserver() {
        fileJob = lifecycleScope.launch {
            viewModel.file.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> {
                        val format =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                        val message = ChatMessage(
                            _id = System.currentTimeMillis().toString(),
                            seen = true,
                            time = format.format(Calendar.getInstance().time),
                            content = "",
                            sender = userId,
                            receiver = conversationId,
                            type = SocketMessageType.UPLOADING.type
                        )
                        sendIntentToSocketChannel(SocketIntent.InsertPlaceholder(message))
                    }
                    is DataState.Success -> {
                        val item = dataState.data.item
                        val message = SocketMessage(
                            sender = item.sender,
                            receiver = item.receiver,
                            content = item.url,
                            type = item.type
                        )
                        sendIntentToSocketChannel(SocketIntent.SendMessage(message))
                        sendIntentToSocketChannel(
                            SocketIntent.RemovePlaceholder(
                                item.sender,
                                item.receiver
                            )
                        )
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            dataState.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", dataState.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_CHOOSE -> data?.data?.let {
                    sendUploadIntent(it, SocketMessageType.IMAGE.type)
                }
                FILES -> data?.data?.let {
                    sendUploadIntent(it, SocketMessageType.FILE.type)
                }
                VIDEO_CHOOSE -> data?.data?.let {
                    sendUploadIntent(it, SocketMessageType.VIDEO.type)
                }
                CAPTURE_MEDIA -> {
                    if (data?.data != null) {
                        val uri = data.data!!
                        val resolver = requireContext().contentResolver
                        val type = resolver.getType(uri) ?: ""
                        when {
                            type.contains("image", true) ->
                                sendUploadIntent(uri, SocketMessageType.IMAGE.type)
                            type.contains("video", true) ->
                                sendUploadIntent(uri, SocketMessageType.VIDEO.type)
                        }
                    } else {
                        val videoPath = if (Build.VERSION.SDK_INT >= 29)
                            Environment.getExternalStorageDirectory().path +
                                    "/Movies/Bicon/temp_video.mp4"
                        else requireContext().externalMediaDirs.first().absolutePath +
                                "/${Environment.DIRECTORY_MOVIES}" +
                                "/temp_video.mp4"

                        val imagePath = if (Build.VERSION.SDK_INT >= 29)
                            Environment.getExternalStorageDirectory().path +
                                    "/Pictures/Bicon/temp_img.jpg"
                        else requireContext().externalMediaDirs.first().absolutePath +
                                "/${Environment.DIRECTORY_PICTURES}/temp_img.jpg"

                        val video = File(videoPath)
                        val image = File(imagePath)

                        when {
                            video.length() != 0L -> {
                                val uri = Uri.fromFile(video)
                                sendUploadIntent(uri, SocketMessageType.VIDEO.type)
                            }
                            image.length() != 0L -> {
                                val uri = Uri.fromFile(image)
                                sendUploadIntent(uri, SocketMessageType.IMAGE.type)
                            }
                            else -> {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.unexpected_error,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sendUploadIntent(uri: Uri, type: String) {
        val contentResolver = requireContext().contentResolver
        val mime = contentResolver.getType(uri) ?: "*/*"
        val mediaType = mime.toMediaType()
        val contentPart = InputStreamRequestBody(mediaType, contentResolver, uri)
        val name = getFileName(uri)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("files", name, contentPart)
            .addFormDataPart("sender", userId)
            .addFormDataPart("receiver", conversationId)
            .addFormDataPart("type", type)
            .build()
        lifecycleScope.launch {
            viewModel.channel.send(SocketIntent.UploadFile(requestBody))
        }
    }

    private fun startRecording() {
        startRecordTimer()
        recorderFile = createVoiceFile()
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            if (Build.VERSION.SDK_INT >= 26) {
                setOutputFile(recorderFile)
            } else {
                setOutputFile(recorderFile?.absolutePath)
            }
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            try {
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("LOG_VOICE", "prepare() failed")
                stopRecordTimer()
            }
        }
    }

    private fun stopRecording() {
        stopRecordTimer()
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        sendUploadIntent(Uri.fromFile(recorderFile), SocketMessageType.AUDIO.type)
    }

    override fun onDestroy() {
        super.onDestroy()
        recorder?.release()
        recorder = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 3000) {
            if (grantResults.first() != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    requireContext(),
                    "PERMISSION ERROR",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun startRecordTimer() {
        binding.chatEdittext.setText(
            requireContext().getString(R.string.recording_timer, "00:00")
        )
        startTime = SystemClock.uptimeMillis()
        handler.postDelayed(runnable, 0)
    }

    private fun stopRecordTimer() {
        startTime = 0L
        binding.chatEdittext.setText("")
        handler.removeCallbacks(runnable)
    }

    private val runnable = object : Runnable {
        override fun run() {
            val updateTime = SystemClock.uptimeMillis() - startTime
            val timeInSec = (updateTime / 1000).toInt()
            val hour = timeInSec / 3600
            val minutes = (timeInSec - (hour * 3600)) / 60
            val seconds = (timeInSec - (hour * 3600) - (minutes * 60))

            val time = StringBuilder()
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds))
                .toString()
            binding.chatEdittext.setText(
                requireContext().getString(R.string.recording_timer, time)
            )
            handler.postDelayed(this, 0)
        }
    }
}
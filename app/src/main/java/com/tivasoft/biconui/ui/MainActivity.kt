package com.tivasoft.biconui.ui

import android.app.Activity
import android.content.*
import android.content.BroadcastReceiver
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tivasoft.biconui.R
import com.tivasoft.biconui.data.model.network.requests.MeetRequest
import com.tivasoft.biconui.data.model.network.responses.tests.TestData
import com.tivasoft.biconui.databinding.ActivityMainBinding
import com.tivasoft.biconui.ui.common.profile.conversation.ConversationViewModel
import com.tivasoft.biconui.utils.Constants
import com.tivasoft.biconui.utils.channel_intents.MeetIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.pushy.sdk.Pushy
import org.jitsi.meet.sdk.*
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ConversationViewModel by viewModels()
    private lateinit var behavior: BottomSheetBehavior<FrameLayout>
    private lateinit var behaviorTest: BottomSheetBehavior<FrameLayout>
    private lateinit var prescriptions: BottomSheetBehavior<FrameLayout>
    private lateinit var bookAppointment: BottomSheetBehavior<FrameLayout>
    private lateinit var register: BottomSheetBehavior<FrameLayout>

    @Inject
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomSheets()
        setupPushy()
        if (intent.hasExtra("roomName") && intent.hasExtra("type"))
            setupMeet(
                intent.getStringExtra("roomName")!!,
                intent.getStringExtra("type")!!
            )
    }

    private fun setupBottomSheets() {
        behaviorTest = BottomSheetBehavior.from(binding.fragmentBottomSheetTest)
        behaviorTest.peekHeight = 0

        prescriptions = BottomSheetBehavior.from(binding.fragmentBottomSheetPrescriptions)
        prescriptions.peekHeight = 0

        behavior = BottomSheetBehavior.from(binding.fragmentBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bookAppointment = BottomSheetBehavior.from(binding.fragmentBottomSheetAppointment)
        bookAppointment.state = BottomSheetBehavior.STATE_COLLAPSED

        register = BottomSheetBehavior.from(binding.fragmentBottomSheetRegister)
        register.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun setBottomSheetState(shouldOpen: Boolean) {
        behaviorTest.peekHeight = if (shouldOpen) 100 else 0
        behaviorTest.state = if (shouldOpen) BottomSheetBehavior.STATE_EXPANDED
        else BottomSheetBehavior.STATE_COLLAPSED
    }

    fun setPrescriptionBottomSheetState(shouldOpen: Boolean) {
        prescriptions.state = if (shouldOpen) BottomSheetBehavior.STATE_EXPANDED
        else BottomSheetBehavior.STATE_COLLAPSED
    }

    fun setAppointmentBottomSheetState(shouldOpen: Boolean) {
        bookAppointment.state = if (shouldOpen) BottomSheetBehavior.STATE_EXPANDED
        else BottomSheetBehavior.STATE_COLLAPSED
    }

    fun setRegisterBottomSheetState(shouldOpen: Boolean) {
        register.state = if (shouldOpen) BottomSheetBehavior.STATE_EXPANDED
        else BottomSheetBehavior.STATE_COLLAPSED
    }

    fun setChatBottomSheetVisibility(shouldBeVisible: Boolean) {
        binding.apply {
            val params = fragmentHolderLayout.layoutParams as ViewGroup.MarginLayoutParams
            if (shouldBeVisible) {
                fragmentBottomSheet.visibility = View.VISIBLE
                //   params.bottomMargin = resources.getDimensionPixelSize(R.dimen._86sdp)
            } else {
                fragmentBottomSheet.visibility = View.GONE
                params.bottomMargin = 0
            }
        }
    }

    fun setChatBottomSheetDrag(shouldBeVisible: Boolean) {
        binding.apply {
            if (shouldBeVisible) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
//                fragmentBottomSheet.visibility = View.VISIBLE
                //   params.bottomMargin = resources.getDimensionPixelSize(R.dimen._86sdp)
            } else {
//                fragmentBottomSheet.visibility = View.GONE
//                params.bottomMargin = 0
            }
        }
    }

    fun setConversationNavGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_bottom_sheet) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.conversation_nav_graph)
    }

    fun setPrescriptionNavGraph() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_bottom_sheet_prescriptions) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.prescription_nav_graph)
    }

    fun setAppointmentNavGraph() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_bottom_sheet_appointment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.book_appointment_nav_graph)
    }

    fun setRegisterNavGraph() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_bottom_sheet_register) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.register_nav_graph)
    }

    fun setTestsNavGraph(data: TestData?) {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_bottom_sheet_test) as NavHostFragment
        val navController = navHostFragment.navController
        val bundle = Bundle()
        bundle.putParcelable("testData", data)
        navController.setGraph(R.navigation.tests_nav_graph, bundle)
    }

    fun setFragmentHolderBackground(@ColorRes colorId: Int) {
        val color = ContextCompat.getColor(this, colorId)
        binding.root.setBackgroundColor(color)
    }

    private fun setupPushy() {
        if (!Pushy.isRegistered(this)) {
            RegisterForPushNotificationsAsync(this).execute()
        }
        Pushy.listen(this)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onBroadcastReceived(intent)
        }
    }

    fun setupMeet(roomName: String, type: String) {
        val isTypeVoice = type.equals("voice", true)
        val serverURL: URL = try {
            URL(Constants.MEET_URL)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw RuntimeException("Invalid server URL!")
        }
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setWelcomePageEnabled(false)
            .setAudioOnly(isTypeVoice)
            .setVideoMuted(isTypeVoice)
            .setWelcomePageEnabled(false)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        registerForBroadcastMessages()

        val options = JitsiMeetConferenceOptions.Builder()
            .setRoom(roomName)
            .setFeatureFlag("pip.enabled", false)
            .setFeatureFlag("chat.enabled", false)
            .setFeatureFlag("invite.enabled", false)
            .setFeatureFlag("kick-out.enabled", false)
            .setFeatureFlag("tile-view.enabled", !isTypeVoice)
            .setFeatureFlag("add-people.enabled", false)
            .setFeatureFlag("video-mute.enabled", !isTypeVoice)
            .setFeatureFlag("meeting-name.enabled", false)
            .setFeatureFlag("overflow-menu.enabled", false)
            .build()
        JitsiMeetActivity.launch(this, options)
    }

    private fun registerForBroadcastMessages() {
        val intentFilter = IntentFilter()

        for (type in BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.action)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun onBroadcastReceived(intent: Intent?) {
        if (intent != null) {
            val event = BroadcastEvent(intent)
            when (event.type) {
                BroadcastEvent.Type.CONFERENCE_TERMINATED,
                BroadcastEvent.Type.PARTICIPANT_LEFT -> {
                    val hangupBroadcastIntent: Intent =
                        BroadcastIntentHelper.buildHangUpIntent()
                    LocalBroadcastManager.getInstance(org.webrtc.ContextUtils.getApplicationContext())
                        .sendBroadcast(hangupBroadcastIntent)
                    if (pref.contains("userId") &&
                        pref.contains("conversationId") &&
                        pref.contains("meetType")
                    ) {
                        lifecycleScope.launch {
                            val entity = MeetRequest(
                                thisUserId = pref.getString("userId", "")!!,
                                otherUserId = pref.getString("conversationId", "")!!,
                                type = pref.getString("meetType", "")!!,
                            )
                            viewModel.meetChannel.send(MeetIntent.EndCall(entity))
                        }
                    }
                }
                BroadcastEvent.Type.PARTICIPANT_JOINED -> {
                    Log.e("TAG_MEET", "PARTICIPANT_JOINED")
                    Log.e("TAG_MEET", "isLocal:${event.data["isLocal"]}")
                    Log.e("TAG_MEET", "participantId:${event.data["participantId"]}")
                    Log.e("TAG_MEET", "role:${event.data["role"]}")
                    // doctor side
                    // if isLocal null OR if participantId not null
                    // start timer api
                    // flag for repeated event

                    //#2 method if role = moderator OR participant
                    // start timer api
                    // flag for repeated event
                }
                else -> Unit
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
}

class RegisterForPushNotificationsAsync(
    private val activity: Activity
) :
    AsyncTask<Void, Void, Any>() {

    override fun doInBackground(vararg params: Void): Any {
        return try {
            val deviceToken = Pushy.register(activity)

            PreferenceManager.getDefaultSharedPreferences(activity)
                .edit()
                .putString("pushyToken", deviceToken)
                .apply()
            deviceToken
        } catch (exc: Exception) {
            exc
        }
    }

    override fun onPostExecute(result: Any) {

        // Registration failed?
//        var message: String = if (result is Exception) {
//            // Log to console
//            Log.e("Pushy", "Pushy:${result.message}")
//
//            // Display error in alert
//            result.message.toString()
//        } else {
//            // Registration success, result is device token
//            "Pushy device token: $result\n\n(copy from logcat)"
//        }
//
//        // Display dialog
//        android.app.AlertDialog.Builder(activity)
//            .setTitle("Pushy")
//            .setMessage(message)
//            .setPositiveButton(android.R.string.ok, null)
//            .show()
    }


    /**
     * reject:  userB rejects > userB send notif of reject > userA receive notif
     * userA send broadcast:
     *
     * method 1:
     * val intent = BroadcastIntentHelper.buildHangUpIntent()
     *
     * method 2:
     * val intent = Intent("org.jitsi.meet.HANG_UP")
     *
     * method 3:
     * val intent = Intent(BroadcastEvent.Type.CONFERENCE_TERMINATED.action)
     *
     *
     * boardcast via
     * LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
     */
}
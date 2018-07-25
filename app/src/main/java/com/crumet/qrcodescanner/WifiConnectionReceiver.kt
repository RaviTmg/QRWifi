package com.crumet.qrcodescanner

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log


class WifiConnectionReceiver : BroadcastReceiver() {
    /**
     * Notifies the receiver to turn wifi on
     */
    private val ACTION_WIFI_ON = "android.intent.action.WIFI_ON"

    /**
     * Notifies the receiver to turn wifi off
     */
    private val ACTION_WIFI_OFF = "android.intent.action.WIFI_OFF"

    /**
     * Notifies the receiver to connect to a specified wifi
     */
    private val ACTION_CONNECT_TO_WIFI = "android.intent.action.CONNECT_TO_WIFI"

    private var wifiManager: WifiManager? = null


    override fun onReceive(c: Context, intent: Intent) {
        Log.d(TAG, "onReceive() called with: intent = [$intent]")

        wifiManager = c.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val action = intent.action

        if (!isTextNullOrEmpty(action)) {
            when (action) {
                ACTION_WIFI_ON ->
                    // Turns wifi on
                    wifiManager!!.isWifiEnabled = true
                ACTION_WIFI_OFF ->
                    // Turns wifi off
                    wifiManager!!.isWifiEnabled = false
                ACTION_CONNECT_TO_WIFI -> {
                    // Connects to a specific wifi network
                    val networkSSID = intent.getStringExtra("ssid")
                    val networkPassword = intent.getStringExtra("password")

                    if (!isTextNullOrEmpty(networkSSID) && !isTextNullOrEmpty(networkPassword)) {
                        connectToWifi(networkSSID, networkPassword)
                    } else {
                        Log.e(TAG, "onReceive: cannot use " + ACTION_CONNECT_TO_WIFI +
                                "without passing in a proper wifi SSID and password.")
                    }
                }
            }
        }
    }

    private fun isTextNullOrEmpty(text: String?): Boolean {
        return text != null && !text.isEmpty()
    }

    /**
     * Connect to the specified wifi network.
     *
     * @param networkSSID     - The wifi network SSID
     * @param networkPassword - the wifi password
     */
    private fun connectToWifi(networkSSID: String, networkPassword: String) {
        if (!wifiManager!!.isWifiEnabled) {
            wifiManager!!.isWifiEnabled = true
        }

        val conf = WifiConfiguration()
        conf.SSID = String.format("\"%s\"", networkSSID)
        conf.preSharedKey = String.format("\"%s\"", networkPassword)

        val netId = wifiManager!!.addNetwork(conf)
        wifiManager!!.disconnect()
        wifiManager!!.enableNetwork(netId, true)
        wifiManager!!.reconnect()
    }

    fun getIntentFilterForWifiConnectionReceiver(): IntentFilter {
        val randomIntentFilter = IntentFilter(ACTION_WIFI_ON)
        randomIntentFilter.addAction(ACTION_WIFI_OFF)
        randomIntentFilter.addAction(ACTION_CONNECT_TO_WIFI)
        return randomIntentFilter
    }


}
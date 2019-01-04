package io.socialmarket.mobile

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.os.StrictMode
import android.widget.ProgressBar
import android.view.View
import android.widget.TextView
import okhttp3.*
import org.json.JSONObject
import android.content.Intent


class MainActivity : AppCompatActivity() {

    private var AUTHENTICATION_SUCCESS = "AUTHENTICATION_SUCCESS"
    private var BAD_CREDENTIALS = "BAD_CREDENTIALS"
    private var loginURL = "http://192.168.0.4:8082/api/auth/login"
    private var jsonType = MediaType.parse("application/json; charset=utf-8")
    private var client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get reference to button
        var btn_login = findViewById<Button>(R.id.buttonIngresar)
        var progress_bar = findViewById<ProgressBar>(R.id.loginProgressBar)
        var messageText = findViewById<TextView>(R.id.message)

        messageText.visibility = View.INVISIBLE
        progress_bar.visibility = View.INVISIBLE

        // set on-click listener
        btn_login.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            messageText.visibility = View.INVISIBLE

            var usuarioText = findViewById<EditText>(R.id.usuario)
            var passwordText = findViewById<EditText>(R.id.password)

            if (android.os.Build.VERSION.SDK_INT > 9) {
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
            }

            var response = login(usuarioText.text.toString(), passwordText.text.toString())

            //println("response2:$response")

            var json = JSONObject(response)
            var status = json.getString("status")
            if (status == AUTHENTICATION_SUCCESS){
               var token = json.getString("token")
               progress_bar.visibility = View.INVISIBLE

               val intent = Intent(this, NavigationActivity::class.java)
               intent.putExtra("token", token)
               startActivity(intent)
            } else if (status == BAD_CREDENTIALS){
               progress_bar.visibility = View.INVISIBLE
               messageText.visibility = View.VISIBLE
               messageText.text = "Credenciales incorrectas."
            }

        }
    }

    private fun login(usuarioVal: String, password: String) : String? {
        var json = createLoginJson(usuarioVal, password)
        var body = RequestBody.create(jsonType, json)
        var request = Request.Builder()
            .addHeader("content-type", "application/json")
            .addHeader("cache-control", "no-cache")
            .url(loginURL)
            .post(body)
            .build()
        client.newCall(request).execute().use {
             response -> return response.body()?.string()
        }
    }

    private fun createLoginJson(usuarioVal: String, password: String): String?  {
        return "{\"username\":\"$usuarioVal\",\"password\":\"$password\"}"
    }

}

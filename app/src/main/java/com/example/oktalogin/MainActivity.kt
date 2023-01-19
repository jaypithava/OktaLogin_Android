package com.example.oktalogin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.okta.oidc.*
import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.storage.SharedPreferenceStorage
import com.okta.oidc.util.AuthorizationException
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var webAuth: WebAuthClient


    /**
     * The authorized client to interact with Okta's endpoints.
     */
    private lateinit var sessionClient: SessionClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupWebAuth()

        setupWebAuthCallback(webAuth)

        signIn.setOnClickListener {
            val payload = AuthenticationPayload.Builder()
                .build()
            webAuth.signIn(this, payload)
        }
        signOut.setOnClickListener {
            webAuth.signOutOfOkta(this@MainActivity)
        }
    }




    private fun setupWebAuth() {
        val oidcConfig = OIDCConfig.Builder()
            .clientId("0oa4vbykzhyxACr9m417")
            .redirectUri("com.okta.supercell:/callback")
            .endSessionRedirectUri("com.okta.supercell:/logout")
            .scopes("openid", "profile", "offline_access")
            .discoveryUri("https://sso.supercell.com")
            .create()



        webAuth = Okta.WebAuthBuilder()
            .withConfig(oidcConfig)
            .withContext(applicationContext)
            .withStorage(SharedPreferenceStorage(this))
            .setRequireHardwareBackedKeyStore(true)
            .create()
        sessionClient = webAuth.sessionClient
    }

    private fun setupWebAuthCallback(webAuth: WebAuthClient) {
        webAuth.registerCallback(object :
            ResultCallback<AuthorizationStatus?, AuthorizationException?> {
            override fun onSuccess(status: AuthorizationStatus) {
                if (status == AuthorizationStatus.AUTHORIZED) {
                    Toast.makeText(this@MainActivity, "Login Success", Toast.LENGTH_SHORT).show()
                    //client is authorized.
                    Log.d("jay", "name: "+status.name)
                    Log.d("jay", "ordinal: "+status.ordinal)
                    val tokens: Tokens = sessionClient.tokens
                    Log.d("jay", "token: "+tokens.idToken)


                } else if (status == AuthorizationStatus.SIGNED_OUT) {

                    Toast.makeText(this@MainActivity, "SIGNED_OUT", Toast.LENGTH_SHORT).show()
                    //this only clears the browser session.

                }
            }

            override fun onCancel() {
                Toast.makeText(this@MainActivity, "cancel", Toast.LENGTH_SHORT).show()
                //authorization canceled
            }


            override fun onError(msg: String?, exception: AuthorizationException?) {
                Toast.makeText(this@MainActivity, "Error $exception", Toast.LENGTH_SHORT).show()
                Log.d("jay", "onError: $msg $exception")
            }


        }, this)
    }
}
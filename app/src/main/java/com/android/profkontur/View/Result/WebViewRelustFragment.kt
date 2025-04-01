package com.android.profkontur.View.Result

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.registerReceiver
import androidx.navigation.fragment.findNavController
import com.android.profkontur.R
import com.android.profkontur.RestApi.ApiService
import com.android.profkontur.ViewModel.Result.WebViewRelustViewModel
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream

class WebViewRelustFragment : Fragment() {

    lateinit var ExitButton:FloatingActionButton
    lateinit var WebView:WebView
    private lateinit var pdfView: PDFView
    private var downloadId: Long = -1

    private val viewModel: WebViewRelustViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    init {

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_web_view_relust, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewInit(view)


    }

    fun WebViewInit(){
        WebView.settings.javaScriptEnabled = true
        WebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
              //  view?.evaluateJavascript("document.getElementById('view_report').click();", null)
            }
        }
        WebView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            downloadPdf(url)
        }
        arguments?.let {
            WebView.loadUrl(it.getString("WebUri").toString())
        }
    }

    private fun downloadPdf(url: String) {
        val retrofit = Retrofit.Builder().baseUrl("https://regions.profkontur.com/").build()
        val apiService = retrofit.create(ApiService::class.java)
        Log.i("PDF",apiService.downloadPdf(url).toString())
        apiService.downloadPdf(url).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("PDF",response.toString())
                response.body()?.let { body ->
                    val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "downloaded.pdf")
                    body.byteStream().use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    pdfView.fromFile(file).load()
                    pdfView.visibility = View.VISIBLE
                    WebView.visibility = View.GONE
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle error
            }
        })
    }




    fun ViewInit(view: View){
        WebView = view.findViewById(R.id.WebView)
        pdfView = view.findViewById(R.id.pdfView)
        ExitButton = view.findViewById(R.id.ExitButton)
        ExitButton.setOnClickListener {
            findNavController().popBackStack()
        }
        WebViewInit()
    }
}
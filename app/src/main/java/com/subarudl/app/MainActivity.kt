package com.subarudl.app

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.ScrollView
import android.widget.TextView
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import java.io.File
import kotlin.concurrent.thread

/**
 * Fase 1 (viabilidade): baixa um vídeo do YouTube no Android com yt-dlp + QuickJS.
 * Tudo o que acontece vai para a tela e para o logcat (tag SUBARU).
 */
class MainActivity : Activity() {
    private lateinit var tela: TextView

    private fun log(msg: String) {
        Log.i("SUBARU", msg)
        runOnUiThread { tela.append(msg + "\n") }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tela = TextView(this).apply { textSize = 12f; setPadding(24, 48, 24, 24); setTextIsSelectable(true) }
        setContentView(ScrollView(this).apply { addView(tela) })

        val url = intent.getStringExtra("url") ?: "https://www.youtube.com/watch?v=jNQXAC9IVRw"
        val atualizar = intent.getBooleanExtra("atualizar", true)
        thread { teste(url, atualizar) }
    }

    private fun teste(url: String, atualizar: Boolean) {
        try {
            val ydl = YoutubeDL.getInstance()
            ydl.init(applicationContext)
            FFmpeg.getInstance().init(applicationContext)
            log("ABI: ${android.os.Build.SUPPORTED_ABIS.joinToString()}  Android ${android.os.Build.VERSION.RELEASE}")
            log("yt-dlp embutido: ${YoutubeDL.version(applicationContext)}")

            if (atualizar) {
                log("Atualizando o yt-dlp…")
                val status = ydl.updateYoutubeDL(applicationContext, YoutubeDL.UpdateChannel.STABLE)
                log("Atualização: $status → ${YoutubeDL.version(applicationContext)}")
            }

            val qjs = File(applicationInfo.nativeLibraryDir, "libqjs.so")
            log("QuickJS: ${qjs.absolutePath} existe=${qjs.exists()} executável=${qjs.canExecute()} (${qjs.length()} bytes)")

            val saida = File(getExternalFilesDir(null), "teste").apply { mkdirs() }
            val req = YoutubeDLRequest(url).apply {
                addOption("--no-playlist")
                addOption("--js-runtimes", "quickjs:${qjs.absolutePath}")
                addOption("-v")
                intent.getStringExtra("client")?.let { addOption("--extractor-args", "youtube:player_client=$it") }
                addOption("-f", intent.getStringExtra("fmt") ?: "bv*[height<=360]+ba/b[height<=360]/b")
                addOption("-o", "${saida.absolutePath}/%(title)s.%(ext)s")
            }
            log("Baixando $url …")
            val r = ydl.execute(req, "teste") { progresso, _, linha -> Log.i("SUBARU", "[${progresso.toInt()}%] $linha") }
            log("Código de saída: ${r.exitCode}")
            r.out.lines().takeLast(8).forEach { log("  $it") }
            r.err.lines().forEach { Log.i("SUBARU", "ERR " + it.take(200)) }
            saida.listFiles()?.forEach { log("ARQUIVO: ${it.name} (${it.length()} bytes)") }
            log("FIM")
        } catch (e: Throwable) {
            log("FALHOU: ${e::class.java.simpleName}: ${e.message}")
            Log.e("SUBARU", "erro", e)
        }
    }
}

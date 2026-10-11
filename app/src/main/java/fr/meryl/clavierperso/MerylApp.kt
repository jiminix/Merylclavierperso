package fr.meryl.clavierperso

import android.app.Application
import android.content.Context
import android.os.Process
import java.io.PrintWriter
import java.io.StringWriter

class MerylApp : Application() {
 override fun onCreate() {
  super.onCreate()
  val previous = Thread.getDefaultUncaughtExceptionHandler()
  Thread.setDefaultUncaughtExceptionHandler { thread, error ->
   try {
    val writer=StringWriter()
    error.printStackTrace(PrintWriter(writer))
    getSharedPreferences("diagnostics", Context.MODE_PRIVATE).edit()
     .putString("last_crash",writer.toString().take(14000))
     .putLong("last_crash_time",System.currentTimeMillis()).commit()
   } catch (_:Throwable) { }
   if(previous != null) previous.uncaughtException(thread,error)
   else { Process.killProcess(Process.myPid()); kotlin.system.exitProcess(10) }
  }
 }
}

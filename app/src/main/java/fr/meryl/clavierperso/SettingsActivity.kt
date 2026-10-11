package fr.meryl.clavierperso

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.graphics.Color
import android.widget.*
import android.view.inputmethod.InputMethodManager

class SettingsActivity : Activity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val layout=LinearLayout(this).apply {
   orientation=LinearLayout.VERTICAL
   setPadding(24,32,24,24)
   setBackgroundColor(Color.WHITE)
  }
  fun addButton(label:String, action:()->Unit) {
   layout.addView(Button(this).apply { text=label; setOnClickListener { action() } })
  }
  layout.addView(TextView(this).apply {
   text="Meryl Clavier Perso - diagnostic"
   textSize=22f
   setTextColor(Color.BLACK)
  })
  addButton("Afficher le dernier plantage") {
   val crash=getSharedPreferences("diagnostics",MODE_PRIVATE).getString("last_crash",null)
    ?: "Aucun rapport enregistré"
   android.app.AlertDialog.Builder(this).setTitle("Diagnostic")
    .setMessage(crash.take(12000))
    .setPositiveButton("Fermer",null)
    .setNeutralButton("Copier") { _,_->
     val clipboard=getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
     clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Crash Meryl",crash))
    }.show()
  }
  addButton("Activer le clavier") { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
  addButton("Choisir le clavier") {
   (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
  }
  setContentView(layout)
 }
}

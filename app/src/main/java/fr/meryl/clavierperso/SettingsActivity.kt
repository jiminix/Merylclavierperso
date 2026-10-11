package fr.meryl.clavierperso

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.view.View
import android.widget.*
import android.graphics.Color

class SettingsActivity : Activity() {
 private val prefs by lazy { getSharedPreferences("keyboard", MODE_PRIVATE) }
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); render() }
 private fun render() {
  val root=LinearLayout(this).apply { orientation=1; setPadding(24,24,24,24); setBackgroundColor(Color.WHITE) }
  val scroll=ScrollView(this); scroll.addView(root); setContentView(scroll)
  fun title(s:String) { root.addView(TextView(this).apply { text=s; textSize=19f; setTextColor(Color.BLACK); setPadding(0,15,0,8) }) }
  fun button(s:String, f:()->Unit) { root.addView(Button(this).apply { text=s; setOnClickListener { f() } }) }
  title("Meryl Clavier Perso")
  button("Afficher le dernier plantage") {
   val crash=getSharedPreferences("diagnostics",MODE_PRIVATE).getString("last_crash",null)
   val message=crash ?: "Aucun plantage enregistré. Active le clavier, reproduis le problème, puis rouvre cette application."
   android.app.AlertDialog.Builder(this).setTitle("Diagnostic du clavier")
    .setMessage(message.take(12000))
    .setPositiveButton("OK",null)
    .setNeutralButton("Copier") { _,_->
     (getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager)
      .setPrimaryClip(android.content.ClipData.newPlainText("Meryl crash",message))
     Toast.makeText(this,"Diagnostic copié",Toast.LENGTH_SHORT).show()
    }.show()
  }
  button("1. Activer le clavier") { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
  button("2. Choisir le clavier") { (getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager).showInputMethodPicker() }
  title("Thème")
  button(if(prefs.getBoolean("dark",true)) "Blanc sur noir" else "Noir sur blanc") { prefs.edit().putBoolean("dark",!prefs.getBoolean("dark",true)).apply(); render() }
  title("Couleurs personnalisées (hexadécimal : #RRGGBB)")
  for ((key,label,default) in listOf(Triple("background","Fond clavier","#202124"),Triple("keycolor","Fond touches","#42454A"),Triple("lettercolor","Lettres","#FFFFFF"))) {
   val field=EditText(this).apply { setSingleLine(true); hint=label; setText(prefs.getString(key,default)) }
   root.addView(field)
   button("Enregistrer : $label") { try { Color.parseColor(field.text.toString()); prefs.edit().putString(key,field.text.toString()).apply(); Toast.makeText(this,"Couleur enregistrée",Toast.LENGTH_SHORT).show() } catch(e:Exception) { Toast.makeText(this,"Format attendu : #RRGGBB",Toast.LENGTH_SHORT).show() } }
  }
  title("Touches personnalisables (hors lettres)")
  val ids=listOf("accent","comma","period","question","symbols","emoji","enter","shift","delete","clipboard","downloads")
  for (id in ids) button("Modifier : $id") { editKey(id) }
 }
 private fun editKey(id:String) {
  val types=arrayOf("Défaut","Vide","Texte / e-mail / URL","Presse-papiers","Téléchargements")
  val layout=LinearLayout(this).apply { orientation=1; setPadding(30,12,30,0) }
  val spinner=Spinner(this).apply { adapter=ArrayAdapter(this@SettingsActivity,android.R.layout.simple_spinner_dropdown_item,types) }
  val field=EditText(this).apply { hint="Texte à insérer"; setText(prefs.getString("text_$id","")) }
  layout.addView(spinner); layout.addView(field)
  spinner.setSelection(prefs.getInt("type_$id",0))
  android.app.AlertDialog.Builder(this).setTitle("Touche : $id").setView(layout).setPositiveButton("Enregistrer") { _,_->prefs.edit().putInt("type_$id",spinner.selectedItemPosition).putString("text_$id",field.text.toString()).apply() }.setNegativeButton("Annuler",null).show()
 }
}

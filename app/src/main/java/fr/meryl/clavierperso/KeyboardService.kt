package fr.meryl.clavierperso

import android.inputmethodservice.InputMethodService
import android.content.*
import android.graphics.Color
import android.view.*
import android.view.inputmethod.InputConnection
import android.widget.*

class KeyboardService : InputMethodService() {
 private val prefs by lazy { getSharedPreferences("keyboard",MODE_PRIVATE) }
 private var shifted=false
 private var symbols=false
 override fun onCreateInputView(): View = buildKeyboard()
 private fun color(key:String, fallback:String):Int = try { Color.parseColor(prefs.getString(key,fallback)) } catch(e:Exception) { Color.parseColor(fallback) }
 private fun buildKeyboard():View {
  val dark=prefs.getBoolean("dark",true)
  val bg=color("background",if(dark)"#202124" else "#ECEFF1")
  val keyBg=color("keycolor",if(dark)"#42454A" else "#FFFFFF")
  val fg=color("lettercolor",if(dark)"#FFFFFF" else "#151515")
  val root=LinearLayout(this).apply { orientation=1; setBackgroundColor(bg); setPadding(3,7,3,7) }
  val rows=if(symbols) listOf(listOf("1","2","3","4","5","6","7","8","9","0"),listOf("@","#","€","%","&","-","+","(",")","/"),listOf("*","\"","'",":",";","!","?","accent"),listOf("symbols","comma","space","period","delete","enter"))
    else listOf(listOf("a","z","e","r","t","y","u","i","o","p"),listOf("q","s","d","f","g","h","j","k","l","m"),listOf("shift","w","x","c","v","b","n","accent","delete"),listOf("symbols","comma","space","period","clipboard","downloads","enter"))
  for(row in rows) {
   val line=LinearLayout(this).apply { orientation=0 }
   for(id in row) {
    val label=when(id) {"accent"->"´";"shift"->"⇧";"delete"->"⌫";"space"->" "; "symbols"->if(symbols)"ABC" else "?123";"comma"->",";"period"->".";"clipboard"->"▤";"downloads"->"↓";"enter"->"↵";else->if(shifted)id.uppercase() else id}
    val b=TextView(this).apply {
     text=label; gravity=Gravity.CENTER; textSize=if(id=="space")20f else 21f; setTextColor(fg); setBackgroundColor(keyBg)
     setOnClickListener { press(id) }
     if(id !in listOf("space") && (id.length>1 || id in listOf(",","."))) setOnLongClickListener { showEdit(id);true }
    }
    val wrap=LinearLayout(this).apply { setPadding(2,3,2,3); addView(b,LinearLayout.LayoutParams(-1,-1)) }
    val weight=if(id=="space")4.2f else if(id=="enter")1.3f else 1f
    line.addView(wrap,LinearLayout.LayoutParams(0,if(id=="space")67 else if(row.contains("space"))67 else 51,weight))
   }
   root.addView(line)
  }
  return root
 }
 private fun press(id:String) {
  val ic=currentInputConnection ?: return
  val custom=id in listOf("accent","comma","period","symbols","shift","delete","enter","clipboard","downloads","emoji","question")
  if(custom) {
   when(prefs.getInt("type_$id",0)) {
    1->return
    2->{ ic.commitText(prefs.getString("text_$id","")?:"",1);return }
    3->{ paste(ic);return }
    4->{ downloads();return }
   }
  }
  when(id) {
   "space"->ic.commitText(" ",1)
   "delete"->ic.deleteSurroundingText(1,0)
   "enter"->ic.commitText("\n",1)
   "shift"->{shifted=!shifted;setInputView(buildKeyboard())}
   "symbols"->{symbols=!symbols;setInputView(buildKeyboard())}
   "clipboard"->paste(ic)
   "downloads"->downloads()
   "accent"->ic.commitText("´",1)
   "comma"->ic.commitText(",",1)
   "period"->ic.commitText(".",1)
   else->ic.commitText(if(shifted)id.uppercase() else id,1)
  }
 }
 private fun paste(ic:InputConnection) {
  val clip=getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
  val item=clip.primaryClip?.takeIf { it.itemCount>0 }?.getItemAt(0)
  item?.coerceToText(this)?.let { ic.commitText(it,1) }
 }
 private fun downloads() {
  try { val intent=Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type="*/*"; addCategory(Intent.CATEGORY_OPENABLE); addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) };startActivity(intent) }
  catch(e:Exception) { Toast.makeText(this,"Impossible d'ouvrir les fichiers",Toast.LENGTH_SHORT).show() }
 }
 private fun showEdit(id:String) {
  val types=arrayOf("Défaut","Vide","Texte / e-mail / URL","Presse-papiers","Téléchargements")
  val layout=LinearLayout(this).apply { orientation=1; setPadding(20,10,20,0) }
  val spinner=Spinner(this).apply { adapter=ArrayAdapter(this@KeyboardService,android.R.layout.simple_spinner_dropdown_item,types);setSelection(prefs.getInt("type_$id",0)) }
  val field=EditText(this).apply { hint="Texte à insérer";setText(prefs.getString("text_$id","")) }
  layout.addView(spinner);layout.addView(field)
  val dialog=android.app.AlertDialog.Builder(this).setTitle("Modifier : $id").setView(layout).setPositiveButton("Enregistrer") { _,_->prefs.edit().putInt("type_$id",spinner.selectedItemPosition).putString("text_$id",field.text.toString()).apply();setInputView(buildKeyboard()) }.setNegativeButton("Annuler",null).create()
  dialog.window?.attributes?.token=window.window?.attributes?.token
  dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG)
  try { dialog.show() } catch(e:Exception) { Toast.makeText(this,"Utilise les réglages de l'application",Toast.LENGTH_SHORT).show() }
 }
}

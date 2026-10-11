package fr.meryl.clavierperso

import android.inputmethodservice.InputMethodService
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView

class KeyboardService : InputMethodService() {
 override fun onEvaluateInputViewShown(): Boolean = true
 override fun onCreateInputView(): View {
  val root = LinearLayout(this).apply {
   orientation = LinearLayout.VERTICAL
   setBackgroundColor(Color.rgb(28,29,32))
   setPadding(4,8,4,8)
  }
  val rows = listOf("azertyuiop","qsdfghjklm","wxcvbn", " ")
  for (row in rows) {
   val line = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
   for (character in row) {
    val key = TextView(this).apply {
     text = if (character == ' ') "ESPACE" else character.toString()
     textSize = 20f
     gravity = Gravity.CENTER
     setTextColor(Color.WHITE)
     setBackgroundColor(Color.rgb(68,70,76))
     setOnClickListener { currentInputConnection?.commitText(character.toString(),1) }
    }
    val holder = LinearLayout(this).apply {
     setPadding(2,3,2,3)
     addView(key, LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
    }
    line.addView(holder, LinearLayout.LayoutParams(0, dp(if(character==' ') 67 else 51),1f))
   }
   root.addView(line, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
    dp(if(row==" ") 67 else 51)))
  }
  return root
 }
 private fun dp(value:Int) = (value * resources.displayMetrics.density + 0.5f).toInt()
}

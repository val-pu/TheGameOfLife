package leko.valmx.thegameoflife

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    val gitHubUrl = "https://github.com/val-mx/TheGameOfLife"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        btn_back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        btn_git.setOnClickListener {
            val urlIntent = Intent(Intent.ACTION_VIEW)
            urlIntent.data = Uri.parse(gitHubUrl)
            startActivity(urlIntent)
        }
        btn_licenses.setOnClickListener {
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
        }
    }
}
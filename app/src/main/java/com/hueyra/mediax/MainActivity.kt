package com.hueyra.mediax

import android.Manifest
import android.annotation.SuppressLint
import android.widget.TextView
import android.os.Bundle
import com.permissionx.guolindev.PermissionX
import android.widget.Toast
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.hueyra.mediax.MediaX
import com.github.hueyra.mediax.entity.LocalMedia

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mResult: TextView
    private lateinit var mImage: ImageView
    private val mSb = StringBuffer()

    @SuppressLint("SetTextI18n")
    private val mLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val list: List<LocalMedia>? =
                MediaX.obtainResult(activityResult.resultCode, activityResult.data)
            mSb.setLength(0)
            if (list != null) {
                mSb.append("result不为空").append("\n")

                mImage.visibility = View.VISIBLE
                if (!list[0].cutPath.isNullOrEmpty()) {
                    Glide.with(this).load(list[0].cutPath).into(mImage)
                } else {
                    Glide.with(this).load(list[0].realPath).into(mImage)
                }

                for (i in list.indices) {
                    mSb.append("------list of $i-----").append("\n")
                    mSb.append("realPath -> ${list[i].realPath}").append("\n")
                    mSb.append("cutPath -> ${list[i].cutPath}").append("\n")
                }
            } else {
                mSb.append("result为空")
                mImage.visibility = View.GONE
            }
            mResult.text = mSb.toString()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.camera_btn_both).setOnClickListener(this)
        findViewById<View>(R.id.camera_btn_img).setOnClickListener(this)
        findViewById<View>(R.id.camera_btn_img_crop).setOnClickListener(this)
        findViewById<View>(R.id.camera_btn_vdo).setOnClickListener(this)
        findViewById<View>(R.id.album_btn_both).setOnClickListener { openAlbumBoth() }
        findViewById<View>(R.id.album_btn_img).setOnClickListener { openAlbumOnlyImage() }
        mResult = findViewById(R.id.result)
        mImage = findViewById(R.id.image)
    }

    override fun onClick(v: View) {
        val mediaXBuilder = MediaX.Builder()
        when (v.id) {
            R.id.camera_btn_both -> mediaXBuilder.both()
            R.id.camera_btn_img -> mediaXBuilder.onlyImage()
            R.id.camera_btn_img_crop -> mediaXBuilder.onlyImage().cropImage()
            R.id.camera_btn_vdo -> mediaXBuilder.onlyVideo()
        }

        val mediaX = mediaXBuilder.build()

        PermissionX.init(this).permissions(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
            .request { allGranted: Boolean, _: List<String?>?, _: List<String?>? ->
                if (allGranted) {
                    mLauncher.launch(mediaX.newIntent4Camera(this))
                    mediaX.openWithDefaultAnim(this)
                } else {
                    Toast.makeText(this, "请授权", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun openAlbumBoth() {
        PermissionX.init(this).permissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .request { allGranted: Boolean, _: List<String?>?, _: List<String?>? ->
                if (allGranted) {
                    val mediaX = MediaX.Builder()
                        .both()
                        .maxSelect(4)
                        .build()
                    mLauncher.launch(mediaX.newIntent4Album(this))
                    mediaX.openWithDefaultAnim(this)

                } else {
                    Toast.makeText(this, "请授权", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun openAlbumOnlyImage() {
        PermissionX.init(this).permissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .request { allGranted: Boolean, _: List<String?>?, _: List<String?>? ->
                if (allGranted) {
                    val mediaX = MediaX.Builder().singleCropIMG().build()
                    mLauncher.launch(mediaX.newIntent4Album(this))
                    mediaX.openWithDefaultAnim(this)
                } else {
                    Toast.makeText(this, "请授权", Toast.LENGTH_SHORT).show()
                }
            }


    }
}
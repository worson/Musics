package app.sen.musics.ui.settings

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import app.sen.musics.R
import app.sen.musics.module.log.L
import app.sen.musics.ui.share.SongsViewModel
import app.sen.musics.utils.T
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.File

class SettingsFragment : Fragment {
    val  TAG = "SettingsFragment"

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var songViewModel: SongsViewModel

    constructor()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        songViewModel =
                ViewModelProviders.of(this).get(SongsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        settingsViewModel.text.observe(viewLifecycleOwner, Observer {

        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        L.d(TAG, { "onActivityCreated: " })
        initView()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initView() {

        btnAdd.setOnClickListener {
            PermissionX.init(activity)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        val editText= EditText(requireContext())
                        editText.hint="输入需要添加的文件或文件夹位置"
//                        editText.setText("/storage/emulated/0/12530/download")
                        editText.setText("/sdcard/aaaa")
                        AlertDialog.Builder(requireContext())
                            .setTitle("添加文件目录")
                            .setView(editText)
                            .setPositiveButton("确定"){
                                    a,b ->
                                if (editText.text.toString().isNullOrBlank()){
                                    T.normal("请输入文件夹目录").show()
                                    return@setPositiveButton
                                }
                                val path=File(editText.text.toString())
                                if (!path.exists()){
                                    T.normal("路径不存在").show()
                                    return@setPositiveButton
                                }
                                songViewModel.importFilePath(path)

                            }.show()
                    } else {
                        T.normal("请授予相关权限").show()
                    }
                }


        }
    }
}
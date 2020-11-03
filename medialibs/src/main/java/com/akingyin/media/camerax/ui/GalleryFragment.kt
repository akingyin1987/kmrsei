/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camerax.ui

import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.akingyin.base.SimpleFragment
import com.akingyin.base.config.BaseConfig
import com.akingyin.base.ext.click
import com.akingyin.media.R
import com.akingyin.media.camerax.CameraxManager
import com.akingyin.media.databinding.FragmentGalleryBinding
import com.akingyin.util.padWithDisplayCutout
import com.akingyin.util.showImmersive
import java.io.File

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/17 10:55
 * @version V1.0
 */
class GalleryFragment internal constructor() : SimpleFragment() {
    private lateinit var mediaList: MutableList<File>
    lateinit var bindView: FragmentGalleryBinding
    private val args: GalleryFragmentArgs by navArgs()
    override fun injection() {

    }

    override fun useViewBind() = true

    override fun getLayoutId() = R.layout.fragment_gallery

    override fun initEventAndData() {
        args.photoPaths.data?.let {
            mediaList = it.map { path ->
                File(path)
            }.toMutableList()
        } ?: mutableListOf<File>()
    }

    override fun initView() {
        if (mediaList.isEmpty()) {
            bindView.deleteButton.isEnabled = false
        }
        bindView.photoViewPager.offscreenPageLimit = 2
        bindView.photoViewPager.adapter = MediaPagerAdapter(childFragmentManager)
        // Make sure that the cutout "safe area" avoids the screen notch if any
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Use extension method to pad "inside" view containing UI using display cutout's bounds
            bindView.cutoutSafeArea.padWithDisplayCutout()
        }
        bindView.backButton.click {
            findNavController().navigateUp()
        }
        bindView.shareButton.click {
            mediaList.getOrNull(bindView.photoViewPager.currentItem)?.let {mediaFile->
                try {
                    val intent = Intent().apply {
                        // Infer media type from file extension
                        val mediaType = MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(mediaFile.extension)
                        // Get URI from our FileProvider implementation
                        val uri = FileProvider.getUriForFile(
                                requireContext(), BaseConfig.getAuthority() + ".provider", mediaFile)
                        // Set the appropriate intent extra, type, action and flags
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = mediaType
                        action = Intent.ACTION_SEND
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    startActivity(Intent.createChooser(intent, getString(R.string.share_hint)))
                }catch (e: Exception){
                    e.printStackTrace()
                    showError("分享出错了,${e.message}")
                }

            }
        }

        bindView.deleteButton.click {
            mediaList.getOrNull(bindView.photoViewPager.currentItem)?.let {mediaFile->
                try {

                    AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Dialog)
                            .setTitle(getString(R.string.delete_title))
                            .setMessage(getString(R.string.delete_dialog))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("确定") { _, _ ->
                                CameraxManager.sendDelectTakePhoto(mediaFile.absolutePath,requireContext())
                                // Delete current photo
                                mediaFile.delete()

                                // Send relevant broadcast to notify other apps of deletion
                                MediaScannerConnection.scanFile(
                                        requireContext(), arrayOf(mediaFile.absolutePath), null, null)

                                // Notify our view pager
                                mediaList.removeAt(bindView.photoViewPager.currentItem)

                                bindView.photoViewPager.adapter?.notifyDataSetChanged()

                                // If all photos have been deleted, return to camera
                                if (mediaList.isEmpty()) {
                                    findNavController().navigateUp()
                                }

                            }

                            .setNegativeButton("取消", null)
                            .create().showImmersive()
                }catch (e : Exception){
                    e.printStackTrace()
                    showError("删除出错了,${e.message}")
                }
            }
        }
    }

    override fun lazyLoad() {

    }

    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View? {
        bindView = FragmentGalleryBinding.inflate(inflater, container, false)
        return bindView.root
    }

    /** Adapter class used to present a fragment containing one photo or video as a page */
    inner class MediaPagerAdapter(fm: FragmentManager) : FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount() = mediaList.size

        override fun createFragment(position: Int): Fragment {
            return PhotoFragment.create(mediaList[position])
        }
    }
}
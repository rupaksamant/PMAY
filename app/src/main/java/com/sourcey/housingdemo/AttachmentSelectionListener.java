package com.sourcey.housingdemo;

/**
 * Created by Biswajit on 02-03-2018.
 */

public interface AttachmentSelectionListener {

    public void onCameraItemClick(int result, String attachmentName);
    public void onGalleryItemClick(int result, String attachmentName);
}

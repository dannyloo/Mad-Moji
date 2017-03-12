package com.qeue.ahhh_round.activities.helpers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.qeue.ahhh_round.events.ShowShareDialogEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;

/**
 * This class handles sharing an image and text via a share intent.
 */
public class ShareHelper {
    private Activity activity;
    private Bus bus;
    private boolean currentlySharing;

    public ShareHelper(Activity activity, Bus bus) {
        this.activity = activity;
        this.bus = bus;
        bus.register(this);
    }

    public void onResume() {
        if (currentlySharing) {
            currentlySharing = false;
        }
    }

    @Subscribe
    public void showShareDialog(final ShowShareDialogEvent event) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!currentlySharing) {
                    currentlySharing = true;

                    Uri uri = putScreenshotInFilesystem(event.screenshot);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/png");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "I just hopped over " + event.score + (event.score == 1 ? " enemy" : " enemies") + " in Ahhh-round. Bet you can’t beat me! FIXME__SHARE_URL");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activity.startActivity(shareIntent);
                }
            }
        });
    }

    private Uri putScreenshotInFilesystem(Pixmap screenshot) {
        File imagePath = new File(activity.getFilesDir(), "screenshots");
        File newFile = new File(imagePath, "screenshot.png");
        Uri uri = FileProvider.getUriForFile(activity, "com.qeue.ahhh_round.fileprovider", newFile);
        FileHandle fileHandle = Gdx.files.getFileHandle(newFile.getAbsolutePath(), Files.FileType.Absolute);
        PixmapIO.writePNG(fileHandle, screenshot);
        screenshot.dispose();
        return uri;
    }
}

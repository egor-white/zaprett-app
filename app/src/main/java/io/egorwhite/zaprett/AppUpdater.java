package io.egorwhite.zaprett;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppUpdater {

    private static final String TAG = "AppUpdater";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/egor-white/zaprett-app/releases/latest";
    private static final String FILE_PROVIDER_AUTHORITY = "io.egorwhite.zaprett.fileprovider";

    private final Context context;
    private final UpdateCheckCallback callback;
    private AlertDialog downloadDialog;
    private LinearProgressIndicator progressBar;
    private long downloadId;
    private DownloadManager downloadManager;
    private boolean isDownloading = false;
    private String fileName;
    private Handler progressHandler;
    private boolean isMonitoringProgress = false;

    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long receivedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId == receivedId) {
                handleDownloadComplete();
            }
        }
    };

    public AppUpdater(Context context, UpdateCheckCallback callback) {
        this.context = context;
        this.callback = callback;
        this.progressHandler = new Handler(Looper.getMainLooper());
    }

    public void checkForUpdates() {
        new FetchReleaseTask().execute();
    }

    private class FetchReleaseTask extends AsyncTask<Void, Void, ReleaseInfo> {
        @Override
        protected ReleaseInfo doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(GITHUB_API_URL)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.e(TAG, "HTTP error: " + response.code());
                    return null;
                }

                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                String version = jsonObject.getString("tag_name");
                String changelog = jsonObject.getString("body");
                String downloadUrl = jsonObject.getJSONArray("assets")
                        .getJSONObject(0)
                        .getString("browser_download_url");

                return new ReleaseInfo(version, changelog, downloadUrl);

            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error checking for updates", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReleaseInfo releaseInfo) {
            if (releaseInfo != null && callback != null) {
                String currentVersion = getCurrentAppVersion();
                if (isNewVersionAvailable(currentVersion, releaseInfo.getVersion())) {
                    callback.onUpdateAvailable(releaseInfo);
                } else {
                    callback.onNoUpdateAvailable();
                }
            } else if (callback != null) {
                callback.onError("Failed to check for updates");
            }
        }
    }

    public static boolean isNewVersionAvailable(String currentVersion, String latestVersion) {
        if (currentVersion.equals(latestVersion)) return false;

        String[] currentParts = currentVersion.split("\\.");
        String[] latestParts = latestVersion.split("\\.");

        for (int i = 0; i < Math.min(currentParts.length, latestParts.length); i++) {
            int current = Integer.parseInt(currentParts[i]);
            int latest = Integer.parseInt(latestParts[i]);

            if (latest > current) return true;
            if (latest < current) return false;
        }

        return latestParts.length > currentParts.length;
    }

    private String getCurrentAppVersion() {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0.0";
        }
    }

    public void downloadUpdate(ReleaseInfo releaseInfo) {
    if (isDownloading) {
        Toast.makeText(context, R.string.already_downloading, Toast.LENGTH_SHORT).show();
        return;
    }

    fileName = "zaprett-" + releaseInfo.getVersion() + ".apk";

    View customDownloadView = LayoutInflater.from(context).inflate(R.layout.download_dialog, null);
    TextView downloadText = customDownloadView.findViewById(R.id.downloadText);
    progressBar = customDownloadView.findViewById(R.id.downloadBar);

    downloadText.setText(String.format(context.getString(R.string.downloading_text), getCurrentAppVersion(), releaseInfo.getVersion(), releaseInfo.getChangelog()));

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.downloading_title))
            .setView(customDownloadView)
            .setCancelable(false)
            .setNegativeButton(R.string.btn_cancel, (dialog, which) -> cancelDownload());

    downloadDialog = builder.create();
    downloadDialog.show();

    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(releaseInfo.getDownloadUrl()));
    request.setTitle(context.getString(R.string.updating_msg));
    //request.setDescription("Downloading version " + releaseInfo.getVersion());
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

    downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    downloadId = downloadManager.enqueue(request);
    isDownloading = true;

    registerDownloadReceiver();
    startProgressMonitoring();
}
    private void startProgressMonitoring() {
        if (isMonitoringProgress) return;

        isMonitoringProgress = true;
        progressHandler.post(new ProgressUpdater());
    }

    private class ProgressUpdater implements Runnable {
        @Override
        public void run() {
            if (!isDownloading || !isMonitoringProgress) return;

            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(downloadId);

            try (Cursor cursor = downloadManager.query(q)) {
                if (cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                    int bytesDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(
                        DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytesTotal = cursor.getInt(cursor.getColumnIndexOrThrow(
                        DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        progressBar.setProgressCompat(100, true);
                        handleDownloadComplete();
                        return;
                    }

                    if (bytesTotal > 0) {
                        int progress = (int) ((bytesDownloaded * 100L) / bytesTotal);
                        progressBar.setProgressCompat(progress, true);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Progress monitoring error", e);
                stopProgressMonitoring();
                return;
            }

            if (isMonitoringProgress) {
                progressHandler.postDelayed(this, 500);
            }
        }
    }

    private void handleDownloadComplete() {
        stopProgressMonitoring();
        isDownloading = false;
        unregisterDownloadReceiver();

        if (downloadDialog != null && downloadDialog.isShowing()) {
            downloadDialog.dismiss();
        }

        installUpdate();
    }

    private void stopProgressMonitoring() {
        isMonitoringProgress = false;
        progressHandler.removeCallbacksAndMessages(null);
    }

    private void registerDownloadReceiver() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(downloadReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(downloadReceiver, filter);
        }
    }

    private void unregisterDownloadReceiver() {
        try {
            context.unregisterReceiver(downloadReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Receiver not registered", e);
        }
    }

    private void cancelDownload() {
        if (isDownloading && downloadManager != null) {
            downloadManager.remove(downloadId);
            isDownloading = false;
            stopProgressMonitoring();
            unregisterDownloadReceiver();

            if (downloadDialog != null && downloadDialog.isShowing()) {
                downloadDialog.dismiss();
            }

            Toast.makeText(context, R.string.download_canceled, Toast.LENGTH_SHORT).show();
        }
    }

    private void installUpdate() {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);

        if (file.exists()) {
            Uri apkUri = FileProvider.getUriForFile(context,
                FILE_PROVIDER_AUTHORITY, file);

            Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            installIntent.setData(apkUri);
            installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);

            context.startActivity(installIntent);
        } else {
            Toast.makeText(context,
                "Error: update file not found",
                Toast.LENGTH_LONG).show();
            Log.e(TAG, "File not found: " + file.getAbsolutePath());
        }
    }

    public static class ReleaseInfo {
        private final String version;
        private final String changelog;
        private final String downloadUrl;

        public ReleaseInfo(String version, String changelog, String downloadUrl) {
            this.version = version;
            this.changelog = changelog;
            this.downloadUrl = downloadUrl;
        }

        public String getVersion() {
            return version;
        }

        public String getChangelog() {
            return changelog;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }
    }

    public interface UpdateCheckCallback {
        void onUpdateAvailable(ReleaseInfo releaseInfo);
        void onNoUpdateAvailable();
        void onError(String errorMessage);
    }
}

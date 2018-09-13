package com.sourcey.housingdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

public class PMAYLogger {

	private static PMAYLogger sInstance;

	private final static String FILE_NAME = "PMAYLogs";
	private final static String FILE_EXT = ".txt";
	private final static String OLD_FILE = "old";
	//greater than 5, so that we rename all files to old
	private int mFileCounter =6;

	FileOutputStream mFileOutputStream;
	PrintWriter mPrintWriter;
	File mLogFile;
	Context mctx;
	SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS",
			Locale.US);

	private PMAYLogger(Context ctx) {
		this.mctx = ctx;

	}

	public static PMAYLogger getWriteLogInstance(Context ctx) {
		if (sInstance == null) {
			sInstance = new PMAYLogger(ctx);
		}
		return sInstance;
	}

	public static PMAYLogger getWriteLogInstance() {
		/*if (sInstance == null) {
			sInstance = new PMAYLogger(mctx);
		}*/
		return sInstance;
	}

	public void createLog() {
		String FOLDER_PATH = Environment.getExternalStorageDirectory()
				+ "/PMAYLogs";
		createFolderIfNotExists(FOLDER_PATH);

		// create a new file
		if (mFileCounter > 5) {
			// start from beginning
			checkIfFileExistToRename(FOLDER_PATH);
		}
			
		mLogFile = new File(FOLDER_PATH + "/" + FILE_NAME + "_"
				+ mFileCounter + FILE_EXT);
		mFileCounter++;
		
		try {
			mFileOutputStream = new FileOutputStream(mLogFile, true);
			mPrintWriter = new PrintWriter(mFileOutputStream);
			refreshLogFile(mLogFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void checkIfFileExistToRename(String path) {

		for (int fileCounter = 1; fileCounter <= 5; fileCounter++) {

			File file = new File(path + "/" + FILE_NAME + "_" + fileCounter
					+ FILE_EXT);
			if (file != null && file.exists() && file.isFile()) {
				// rename all existing files to old file
				File oldFile = new File(path + "/" + FILE_NAME + "_" + OLD_FILE + "_" + fileCounter
						+ FILE_EXT);
				boolean isSuccess = file.renameTo(oldFile);	
				Log.v("PTTLOG",
						" PMAYLogger :: renamed file :  "
								+ file.getName() +", isSuccess: "+isSuccess);
				if(isSuccess) {
					refreshLogFile(oldFile);
				}
				//rename existing file and start new file with same counter.
				//mFileCounter = fileCounter;				
			} 
		}
		//start new file from beginning
		mFileCounter = 1;
	}
	
	private void createFolderIfNotExists(String FOLDER_PATH) {
		File folder = new File(FOLDER_PATH);

		if (!folder.exists() && !folder.isDirectory()) {
			folder.mkdir();
		}
	}

	public void writeLog(String tag, String log) {
		if(TextUtils.isEmpty(tag) || TextUtils.isEmpty(log)) {
			return;
		}
		Log.d(tag, log);
		if (mPrintWriter == null) {
			createLog();
		}
		//Fix for the crash - 60701 XP5-ATT
		//some times its observed that mPrintWriter is null
		if (mPrintWriter != null) {
			Date date = new Date(System.currentTimeMillis());
			String logTime = mDateFormat.format(date);

			mPrintWriter.print(logTime + "\t" + tag + "\t" + log + "\r\n");
			mPrintWriter.flush();
		}
		
		if (mLogFile != null && mLogFile.length() > 5242880) {
			closeLog();
			createLog();
		}
	}
	
	/**
	 * This method is used to refresh the current log file so that
	 * log file can be viewed in MTP without rebooting the device. 
	 * pass null to refresh the current file.
	 */
	public void refreshLogFile(File file) {
		try {
			if (file == null) {
				file = mLogFile;
				refreshCdeLogs();
			}
			if (file != null) {
				deleteMediaScanEntry(file);

				MediaScannerConnection
						.scanFile(mctx,
								new String[] { file.getAbsolutePath() }, null,
								null);
			}
		} catch (Exception e) {
			Log.e("PTTLOG", "Exception occurred while refreshing log file ..");
			e.printStackTrace();
		}
	}
	
	private void refreshCdeLogs() {
		String logPath = Environment.getExternalStorageDirectory()
				+ "/PMAYLogs/";
		File file;
		for (int fileCounter = 0; fileCounter <= 9; fileCounter++) {
/*			if (fileCounter == 0) {
				file = new File(logPath + "sonimCdeLogs" + FILE_EXT);
			} else {
*/				file = new File(logPath + "sonimCdeLog" + fileCounter
						+ FILE_EXT);
//			}
			if (file != null && file.exists() && file.isFile()) {				
				try {
					deleteMediaScanEntry(file);
					MediaScannerConnection.scanFile(mctx,
							new String[] { file.getAbsolutePath() }, null, null);
				} catch (Exception e) {
					Log.e("PTTLOG", "Exception occurred while refreshing cde logs");
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * This method deletes an existing file from MTP
	 * @param file to delete
	 */
	private void deleteMediaScanEntry(File file) {
		Cursor cursor = null;
		try {
			// file storage uri
			Uri fileUri = MediaStore.Files.getContentUri("external");
			// query for the file to be deleted and get the row id
			cursor = mctx.getContentResolver().query(fileUri,
					new String[] { MediaStore.Files.FileColumns._ID, },
					MediaStore.Files.FileColumns.DISPLAY_NAME + "=?",
					new String[] { file.getName() }, null);

			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				String id = cursor
						.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));

				// delete this file
				mctx.getContentResolver().delete(
						MediaStore.Files.getContentUri("external"),
						MediaStore.Files.FileColumns._ID + "=?",
						new String[] { id });
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

		}

	}

	public void closeLog() {
		if (mPrintWriter != null && mFileOutputStream != null) {
			mPrintWriter.close();
			refreshLogFile(mLogFile);
			try {
				mFileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

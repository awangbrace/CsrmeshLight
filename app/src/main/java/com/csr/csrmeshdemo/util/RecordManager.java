package com.csr.csrmeshdemo.util;

import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.csr.csrmeshdemo.R;

import java.io.File;
import java.io.IOException;

/**
 * amr音频处理
 *
 * @author hongfa.yy
 * @version 创建时间2012-11-21 下午4:33:28
 */
public class RecordManager {
	private final String TAG = "RecordManager";
	private MediaRecorder mMediaRecorder;
	public static final int MAX_LENGTH = 1000 * 60 * 10;// 最大录音时长1000*60*10;
	private File file;
	private DeviceController mdeviceController;

	public RecordManager(DeviceController deviceController,File file,ImageView view) {
		this.file = file;
		this.view=view;
		this.mdeviceController = deviceController;
	}
	public RecordManager(File file) {
		this.file = file;
	}

	private long startTime;
	private long endTime;

	/**
	 * 开始录音 使用amr格式
	 * @param
	 * @return
	 */
	public void startRecord() {
		// 开始录音
		/* ①Initial：实例化MediaRecorder对象 */
		if (mMediaRecorder == null)
			mMediaRecorder = new MediaRecorder();
		try {
			/* ②setAudioSource/setVedioSource */
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
			/*
			 * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
			 * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
			 */
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			/* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			/* ③准备 */
			mMediaRecorder.setOutputFile(file.getAbsolutePath());
			mMediaRecorder.setMaxDuration(MAX_LENGTH);
			mMediaRecorder.prepare();
			/* ④开始 */
			mMediaRecorder.start();
			// AudioRecord audioRecord.
			/* 获取开始时间* */
			startTime = System.currentTimeMillis();
			// pre=mMediaRecorder.getMaxAmplitude();
			updateMicStatus();
			Log.i("ACTION_START", "startTime" + startTime);
		} catch (IllegalStateException e) {
			Log.i(TAG,
					"call startAmr(File mRecAudioFile) failed!"
							+ e.getMessage());
		} catch (IOException e) {
			Log.i(TAG,
					"call startAmr(File mRecAudioFile) failed!"
							+ e.getMessage());
		}

	}

	/**
	 * 停止录音
	 *
	 * @param
	 */
	public long stopRecord() {
		if (mMediaRecorder == null)
			return 0L;
		endTime = System.currentTimeMillis();
		Log.i("ACTION_END", "endTime" + endTime);
		mMediaRecorder.stop();
		mMediaRecorder.reset();
		mMediaRecorder.release();
		mMediaRecorder = null;
		Log.i("ACTION_LENGTH", "Time" + (endTime - startTime));
		return endTime - startTime;
	}

	private final Handler mHandler = new Handler();
	private Runnable mUpdateMicStatusTimer = new Runnable() {
		public void run() {
			updateMicStatus();
		}
	};

	/**
	 * 更新话筒状态 分贝是也就是相对响度 分贝的计算公式K=20lg(Vo/Vi) Vo当前振幅值 Vi基准值为600：我是怎么制定基准值的呢？ 当20
	 * * Math.log10(mMediaRecorder.getMaxAmplitude() / Vi)==0的时候vi就是我所需要的基准值
	 * 当我不对着麦克风说任何话的时候，测试获得的mMediaRecorder.getMaxAmplitude()值即为基准值。
	 * Log.i("mic_", "麦克风的基准值：" + mMediaRecorder.getMaxAmplitude());前提时不对麦克风说任何话
	 */
	private int BASE = 600;
	private int SPACE = 200;// 间隔取样时间
	private ImageView view;

	private void updateMicStatus() {
		if (mMediaRecorder != null && view != null) {
			// int vuSize = 10 * mMediaRecorder.getMaxAmplitude() / 32768;
			int ratio = mMediaRecorder.getMaxAmplitude() / BASE;
			int db = 0;// 分贝
			if (ratio > 1)
				db = (int) (20 * Math.log10(ratio));
//			System.out.println("分贝值："+db+"     "+Math.log10(ratio));
			Log.e("mic_", "麦克风的音量的大小：" + db);
			switch (db / 4) {
				case 0:
					view.setImageResource(R.drawable.circle_01);
					mdeviceController.onMicrophoneValueChane(0);
					break;
				case 1:
					view.setImageResource(R.drawable.circle_01);
					mdeviceController.onMicrophoneValueChane(1);
					break;
				case 2:
					view.setImageResource(R.drawable.circle_02);
					mdeviceController.onMicrophoneValueChane(2);
					break;
				case 3:
					view.setImageResource(R.drawable.circle_03);
					mdeviceController.onMicrophoneValueChane(3);
					break;
				case 4:
					view.setImageResource(R.drawable.circle_04);
					mdeviceController.onMicrophoneValueChane(4);
					break;
				case 5:
					view.setImageResource(R.drawable.circle_05);
					mdeviceController.onMicrophoneValueChane(5);
					break;
				default:
					view.setImageResource(R.drawable.circle_01);
					break;
			}
			mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);

//			 if (db > 1) {
//				 Log.i("mic_", "麦克风的音量的大小：" + db);
//			 } else
//				 Log.i("mic_", "麦克风的音量的大小：" + 0);

		}
	}

}

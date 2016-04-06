/**
 * 注意：此类名以及此包名不能修改
 */
package com.pd.hid;

import android.os.Message;

import com.taichangkeji.tckj.activity.MeasureAty;
import com.taichangkeji.tckj.utils.LogUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * hid设备数据传输
 * @author fenghui
 *
 */
public class HidMsg implements Runnable {

	private final static String TAG = "HidMsg";

	private IHidMsgCallback msgCallback;

	public HidMsg(IHidMsgCallback callBack) {
		this.msgCallback = callBack;
	}

	// 握手命令
	String[] HandShakeSignal = { "5a", "a5", "5a", "a5", "1" };
	String[] HandShakeFeedbackSig = { "a5", "5a", "a5", "5a", "1" };

	// 拔出命令
	String[] PullOutSignal = { "fc", "00", "00", "00", "00", "00", "00", "00" };

	// 设置日期命令
	static byte[] SetDateFeedbackSig = { 0x00, (byte) 0x00, 0x00, 0x00, 0x10 };

	// 设置时间命令
	static byte[] SetTimeFeedbackSig = { 0x00, 0x00, 0x00, 0x00, 0x11 };

	// 血压返回成功信息
	static byte[] BpFeedbackSig = { 0x01, 0x00, 0x00, 0x00, 0x03 };

	// 确认重量信息
	static byte[] CheckWeightSignal = { 0x00, (byte) 0xCE, 0x10, 0x0A, 0x1C };

	static byte[] checkZfySignal = { 0x00, (byte) 0xEE, 0x00, 0x00, 0x1C };

	// 确认用户信息
	static byte[] CheckUserSignal = { 0x1a, (byte) 0xaf, (byte) 0x82, 0x00,
			0x13 };

	// 脂肪仪返回成功信息
	static byte[] FatFeedbackSig = { 0x01, 0x00, 0x00, 0x00, 0x03 };

	// 耳温枪返回成功信息
	static byte[] TemperatureFeedbackSig = { 0x01, 0x00, 0x00, 0x00, 0x03 };

	// 血氧仪返回成功信息
	static byte[] XueyFeedbackSignal = { (byte) 0x00, (byte) 0x00, 0x43, 0x01,
			0x00, 0x00, 0x00, 0x03 };

	// 血糖接收成功后返回给设备信息
	static byte[] XueTFeedbackSignal = { 0x00, 0x00, 0x45, 0x01, 0x00, 0x00,
			0x00, 0x03 };
	public static String zfl; // 脂肪率

	public static String zfdj; // 脂肪等级

	public static String jrl; // 肌肉量

	public static String sf; // 水分

	public static String weight; // 体重

	public static String highBp; // 高血压

	public static String lowBp; // 低血压值

	public static String pulseNum;// 脉搏

	public static String xueYang; // 血氧值

	public static String xueTang; // 血糖值

	public static String maiB; // 脉搏值

	int lx = 0;

	public static boolean isHidUsed = false; // 是否有Hid设备正在测试

	public static String jiBu; // 记步数据 [[],[]]

	public StringBuffer jibuBuffer = new StringBuffer(""); // 记步数据字符串

	public static String wendu;

	boolean weight_init_flag = false;

	String last_weight_val = "0.0";

	public volatile boolean exit = true;

	private static byte[] data = new byte[8];

	private static boolean isWrite = false;

	private static boolean isRead = false;

	public static boolean isExit = false;

	private static long curTime;

	private static long between;

	/**
	 * 返回脂肪接收信息到手持终端
	 *
	 * @param recvMsg
	 */
	private static void judgeFatFeedback(byte[] recvMsg) {

		data[0] = (byte) 0x00;
		data[1] = (byte) 0x00;
		data[2] = recvMsg[2];
		for (int i = 0; i < 5; i++) {
			data[i + 3] = FatFeedbackSig[i];
		}
		data[2] = (byte) set_bit(data[2], 6);
		if (!pprocessData(data)) {
			data[2] = (byte) reverse_bit(data[2], 7);
		}

		isWrite = true;
	}

	/**
	 * 返回体温接收信息到手持终端
	 *
	 * @param recvMsg
	 */
	private static void judgeTemperatureFeedback(byte[] recvMsg) {

		data[0] = (byte) 0x00;
		data[1] = (byte) 0x00;
		data[2] = recvMsg[2];
		for (int i = 0; i < 5; i++) {
			data[i + 3] = TemperatureFeedbackSig[i];
		}
		data[2] = (byte) set_bit(data[2], 6);
		if (!pprocessData(data)) {
			data[2] = (byte) reverse_bit(data[2], 7);
		}

		isWrite = true;
	}

	// 设置某位为1
	public static int set_bit(int value, int index) {
		return value | (1 << index);
	}

	// 取反某一位
	public static int reverse_bit(int value, int index) {
		assert (index >= 0 && index < 32);
		return value ^ (1 << index);
	}

	public static int countStr(String str1, String str2) {
		int counter = 0;
		if (str1.indexOf(str2) == -1) {
			return 0;
		}
		while (str1.indexOf(str2) != -1) {
			counter++;
			str1 = str1.substring(str1.indexOf(str2) + str2.length());
		}
		return counter;
	}

	/*
	 * 进行奇偶校验的处理过程
	 *
	 * @return 校验成功与否
	 */
	public static boolean pprocessData(byte[] byteArr) {
		int totalNum = 0;
		for (int i = 0; i < byteArr.length; i++) {
			totalNum += countStr(Integer.toBinaryString(byteArr[i] & 0xff), "1");
		}
		if (totalNum % 2 == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 返回用户信息到手持终端
	 *
	 * @param recvMsg
	 */
	private static void judgeSetUserInfoAndFeedback(byte[] recvMsg) {

		data[0] = (byte) 0x00;
		data[1] = (byte) 0x00;
		data[2] = 0x42;

		for (int i = 0; i < 5; i++) {
			data[i + 3] = CheckUserSignal[i];
		}

		data[3] = MeasureAty.userInfo.getAge();
		data[4] = MeasureAty.userInfo.getHeight();
		data[5] = MeasureAty.userInfo.getSex();
		data[6] = MeasureAty.userInfo.getStep();

		data[2] = (byte) set_bit(data[2], 6);
		if (!pprocessData(data)) {
			data[2] = (byte) reverse_bit(data[2], 7);
		}

		isWrite = true;
	}

	/**
	 * 返回体重计信息到手持终端
	 *
	 * @param recvMsg
	 */
	private static void judgeSetWeightAndFeedback(byte[] recvMsg) {
		int user_weight = MeasureAty.userInfo.getWeight();
		byte weight_high = (byte) ((user_weight & 0xff00) >> 8);
		byte weight_low = (byte) (user_weight & 0x00ff);
		CheckWeightSignal[0] = weight_high;
		CheckWeightSignal[1] = weight_low;

		data[0] = (byte) 0x00;
		data[1] = (byte) 0x00;
		data[2] = recvMsg[2];
		for (int i = 0; i < 5; i++) {
			data[i + 3] = CheckWeightSignal[i];
		}
		data[2] = (byte) set_bit(data[2], 6);
		if (!pprocessData(data)) {
			data[2] = (byte) reverse_bit(data[2], 7);
		}

		isWrite = true;
	}

	/**
	 * 返回日期信息到手持终端
	 *
	 * @param recvMsg
	 */
	private static void judgeSetDateAndFeedback(byte[] recvMsg) {

		data[0] = (byte) 0x00;
		data[1] = (byte) 0x00;
		data[2] = recvMsg[2];
		for (int i = 0; i < 5; i++) {
			data[i + 3] = SetDateFeedbackSig[i];
		}
		data[2] = (byte) set_bit(data[2], 6);
		if (!pprocessData(data)) {
			data[2] = (byte) reverse_bit(data[2], 7);
		}
		isWrite = true;
	}

	/**
	 * 返回时间信息到手持终端
	 *
	 * @param recvMsg
	 */
	private static void judgeSetTimeAndFeedback(byte[] recvMsg) {

		data[0] = (byte) 0x00;
		data[1] = (byte) 0x00;
		data[2] = recvMsg[2];
		for (int i = 0; i < 5; i++) {
			data[i + 3] = SetTimeFeedbackSig[i];
		}
		data[2] = (byte) set_bit(data[2], 6);
		if (!pprocessData(data)) {
			data[2] = (byte) reverse_bit(data[2], 7);
		}
		isWrite = true;
	}

	/**
	 * 返回脂肪仪信号信息到手持终端
	 *
	 * @param recvMsg
	 */
	private static void JudgeZfySignalFeedback(byte[] recvMsg) {
		int user_weight = MeasureAty.userInfo.getWeight();
		byte weight_high = (byte) ((user_weight & 0xff00) >> 8);
		byte weight_low = (byte) (user_weight & 0x00ff);
		checkZfySignal[0] = weight_high;
		checkZfySignal[1] = weight_low;

		data[0] = (byte) 0x00;
		data[1] = (byte) 0x00;
		data[2] = recvMsg[2];
		for (int i = 0; i < 5; i++) {
			data[i + 3] = checkZfySignal[i];
		}
		data[2] = (byte) set_bit(data[2], 6);
		if (!pprocessData(data)) {
			data[2] = (byte) reverse_bit(data[2], 7);
		}
		isWrite = true;
	}

	/**
	 * 返回血压信息到手持终端
	 *
	 * @param recvMsg
	 */
	private static void JudgeBpFeedback(byte[] recvMsg) {

		data[0] = (byte) 0x00;
		data[1] = (byte) 0x00;
		data[2] = recvMsg[2];
		for (int i = 0; i < 5; i++) {
			data[i + 3] = BpFeedbackSig[i];
		}
		data[2] = (byte) set_bit(data[2], 6);
		if (!pprocessData(data)) {
			data[2] = (byte) reverse_bit(data[2], 7);
		}
		isWrite = true;
	}

	/**
	 * 返回血糖消息接收成功消息
	 *
	 * @param 
	 */
	private static void JudgeXueTFeedback() {
		writeMsg(XueTFeedbackSignal);
	}

	/**
	 * 返回血氧仪消息接收成功消息
	 *
	 * @param recvMsg
	 */
	private static void JudgeXueyFeedback(byte[] recvMsg) {

		for (int i = 0; i < 8; i++) {
			data[i] = XueyFeedbackSignal[i];
		}

		isWrite = true;
	}

	/**
	 * 发送usb序列号获取指令
	 *
	 */
	private static void sendSerialNumber() {

		data = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xcc };

		isWrite = true;
	}

	/*
	 * A native method that is implemented by the 'hello-jni' native library,
	 * which is packaged with this application.
	 */

	/**
	 * 初始化Hid设备
	 *
	 * @return
	 */
	public native static int initHid();

	/**
	 * 读取hid设备信息
	 *
	 * @return
	 */
	public native static byte[] readMsg();

	/**
	 * 关闭hid设备
	 *
	 * @return
	 */
	public native static int closeHid();

	/**
	 * 重启hid设备
	 *
	 * @return
	 */
	public native static int resetHid();

	/**
	 * 向Hid设备写信息
	 *
	 * @param writeArr
	 * @return
	 */
	public native static int writeMsg(byte[] writeArr);

	/*
	 * This is another native method declaration that is *not* implemented by
	 * 'hello-jni'. This is simply to show that you can declare as many native
	 * methods in your Java code as you want, their implementation is searched
	 * in the currently loaded native libraries only the first time you call
	 * them.
	 *
	 * Trying to call this function will result in a
	 * java.lang.UnsatisfiedLinkError exception !
	 */
	public native String unimplementedInitHid();

	public static int bytesToInt(byte startByte, byte endByte) {
		return Integer.parseInt(Integer.toString(startByte & 0xff)) * 256
				+ Integer.parseInt(Integer.toString(endByte & 0xff));
	}

	public static int byteArrtoInt(byte[] bRefArr) {
		int iOutcome = 0;
		byte bLoop;

		for (int i = 0; i < 4; i++) {
			bLoop = bRefArr[i];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}

	public static byte[] int2ByteArr(int value) {
		byte high = (byte) ((value & 0xff00) >> 8);
		byte low = (byte) (value & 0x00ff);
		return new byte[] { high, low };
	}

	/*
	 * this is used to load the 'hello-jni' library on application startup. The
	 * library has already been unpacked into
	 * /data/data/com.example.hellojni/lib/libhello-jni.so at installation time
	 * by the package manager.
	 */
	static {
		System.loadLibrary("usbpd"); // 加载libusb.so文件
	}

	// @Override
	public void run() {

		new Thread(new HidRead()).start();

		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (isWrite) {

				if (!isRead) {
					writeMsg(data);
					LogUtils.d("current thread is : " + Thread.currentThread().getId());
					LogUtils.d("isWrite :  " + isWrite + " isRead: " + isRead);
				}

				isWrite = false;
			}

			if (isExit) {
				break;
			}

		}

	}

	class HidRead implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			// android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
			// //标准较重要显示优先级，对于输入事件同样适用。
			// android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_FOREGROUND);
			boolean isHandShake;
			int openValue = -1; // 判断hid设备是否正在使用
			Calendar c = Calendar.getInstance();
			byte[] yearByteArr = int2ByteArr(c.get(Calendar.YEAR));
			SetDateFeedbackSig[0] = yearByteArr[0];
			SetDateFeedbackSig[1] = yearByteArr[1];
			// 月份
			SetDateFeedbackSig[2] = (byte) (c.get(Calendar.MONTH) + 1);
			// 日期
			SetDateFeedbackSig[3] = (byte) (c.get(Calendar.DATE));
			SetTimeFeedbackSig[0] = (byte) (c.get(Calendar.HOUR_OF_DAY));
			SetTimeFeedbackSig[1] = (byte) (c.get(Calendar.MINUTE));
			Message msg = new Message();
			while (exit) {
				if (isExit) {
					break;
				}

				if (openValue < 0) {
					openValue = initHid();
					LogUtils.d("openValue is: " + openValue);

					if (openValue >= 0) {
						String callBackMsg = "扫描到usb设备接入";
						msgCallback.onReceiveMessage(callBackMsg);

						sendSerialNumber();
					} else {
						String callBackMsg = "未发现usb设备";
						msgCallback.onReceiveMessage(callBackMsg);
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					try {

						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// if(isWrite)
						// {
						// continue;
						// }
						// else
						// {
						isRead = true;

						byte[] recvMsg = readMsg();
						LogUtils.d("" + recvMsg);
						isRead = false;
						if (null == recvMsg) {
							closeHid();

							openValue = -1;

							continue;
						}
						isHandShake = true;
						if (recvMsg != null
								&& !Integer.toHexString(recvMsg[0] & 0xff)
								.equalsIgnoreCase("9d")) {
							for (int i = 0; i < 8; i++) {
								LogUtils.d("receiveMsg," + i + ":" + Integer.toHexString(recvMsg[i] & 0xff));
							}
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("10")) {
								judgeSetDateAndFeedback(recvMsg);
								continue;
							}
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("11")) {
								judgeSetTimeAndFeedback(recvMsg);
								continue;
							}
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("13")) {
								judgeSetUserInfoAndFeedback(recvMsg);
								continue;
							}
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("14")) {
								int data = bytesToInt(recvMsg[3], recvMsg[4]);
								zfl = String.valueOf(data / 10) + "."
										+ String.valueOf(data % 10);
								judgeFatFeedback(recvMsg);
								continue;
							}
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("15")) {
								int data = bytesToInt(recvMsg[3], recvMsg[4]);
								sf = String.valueOf(data / 10) + "."
										+ String.valueOf(data % 10);
								judgeFatFeedback(recvMsg);
								continue;
							}
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("16")) {
								int data = bytesToInt(recvMsg[3], recvMsg[4]);
								jrl = String.valueOf(data / 10) + "."
										+ String.valueOf(data % 10);
								judgeFatFeedback(recvMsg);
								continue;
							}
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("1b")) {
								int data = bytesToInt(recvMsg[3], recvMsg[4]);
								zfdj = String.valueOf(data);
								StringBuffer repBuffer = new StringBuffer();
								repBuffer.append("['0',");
								repBuffer.append(zfdj);
								repBuffer.append(",'");
								repBuffer.append(zfl);
								repBuffer.append("','");
								repBuffer.append(jrl);
								repBuffer.append("','");
								repBuffer.append(sf);
								repBuffer.append("']");

								String callBackMsg = new StringBuffer("接收到脂肪仪测量数据。")
										.append("脂肪等级：").append(zfdj)
										.append(",脂肪率:").append(zfl)
										.append(",肌肉量:").append(jrl)
										.append(",水分:").append(sf)
										.toString();

								msgCallback.onReceiveMessage(callBackMsg);

								judgeFatFeedback(recvMsg);
								continue;
							}

							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("12")) {
								weight_init_flag = true;
								int data = bytesToInt(recvMsg[3], recvMsg[4]);
								weight = String.valueOf(data / 10) + "."
										+ String.valueOf(data % 10);

								if (weight.equals(last_weight_val)) {
									continue;
								}
								last_weight_val = weight;
								StringBuffer repBuffer = new StringBuffer();
								repBuffer.append("['1','");
								repBuffer.append(weight);
								repBuffer.append("', '0']");

								String callBackMsg = new StringBuffer("接收到体重测量数据。")
										.append("体重值：").append(weight)
										.toString();
								msgCallback.onReceiveMessage(callBackMsg);

								continue;
							}

							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("1c")) {
								if (Integer.toHexString(recvMsg[2] & 0xff)
										.equalsIgnoreCase("1")
										|| Integer.toHexString(
										recvMsg[2] & 0xff)
										.equalsIgnoreCase("81")) {
									if (!weight_init_flag) {
										continue;
									}
									int data = bytesToInt(recvMsg[3],
											recvMsg[4]);
									weight = String.valueOf(data / 10) + "."
											+ String.valueOf(data % 10);
									StringBuffer repBuffer = new StringBuffer();
									repBuffer.append("['1','");
									repBuffer.append(weight);
									repBuffer.append("','1']");
									weight_init_flag = false;
									last_weight_val = "0.0";

									String callBackMsg = new StringBuffer("接收到体重测量数据。")
											.append("体重:").append(weight)
											.toString();

									msgCallback.onReceiveMessage(callBackMsg);

									continue;
								}

								else if (Integer.toHexString(recvMsg[2] & 0xff)
										.equalsIgnoreCase("2")
										|| Integer.toHexString(
										recvMsg[2] & 0xff)
										.equalsIgnoreCase("c2")) {
									judgeSetWeightAndFeedback(recvMsg);
								}
								continue;
							}

							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("1d")) {
								StringBuffer repBuffer = new StringBuffer();
								repBuffer.append("[-1,'");
								repBuffer.append(lx);
								repBuffer.append("']");

								String callBackMsg = "javascript:usbReceiveData("
										+ repBuffer.toString() + ")";
								msgCallback.onReceiveMessage(callBackMsg);

								int data = bytesToInt(recvMsg[3], recvMsg[4]);
								highBp = String.valueOf(data);
								JudgeBpFeedback(recvMsg);
								continue;
							}
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("1e")) {
								int data = bytesToInt(recvMsg[3], recvMsg[4]);
								lowBp = String.valueOf(data);
								JudgeBpFeedback(recvMsg);
								continue;
							}
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("1f")) {
								pulseNum = String.valueOf(Integer
										.parseInt(Integer
												.toString(recvMsg[3] & 0xff)));
								StringBuffer repBuffer = new StringBuffer();
								repBuffer.append("['2',");
								repBuffer.append(highBp);
								repBuffer.append(",");
								repBuffer.append(lowBp);
								repBuffer.append(",");
								repBuffer.append(pulseNum);
								repBuffer.append("]");

								String callBackMsg = new StringBuffer("接收到血压计测量数据。")
										.append("收缩压:").append(highBp)
										.append(",舒张压:").append(lowBp)
										.append(",脉率:").append(pulseNum)
										.toString();
								msgCallback.onReceiveMessage(callBackMsg);

								JudgeBpFeedback(recvMsg);
								continue;
							}

							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("23")) // 发送血糖信息到浏览器中，单位摩尔
							{
								int xueTangVal = Integer.parseInt(Integer
										.toString(recvMsg[4] & 0xff));
								xueTang = String.valueOf(xueTangVal / 10) + "."
										+ String.valueOf(xueTangVal % 10);
								StringBuffer repBuffer = new StringBuffer();
								repBuffer.append("['3','");
								repBuffer.append(xueTang);
								repBuffer.append("']");

								String callBackMsg = new StringBuffer("接收到血糖仪测量数据。")
										.append("血糖结果：").append(xueTang)
										.toString();
								msgCallback.onReceiveMessage(callBackMsg);

								JudgeXueTFeedback();
								continue;
							}

							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("24")) // 发送血氧含量到浏览器中
							{
								xueYang = String.valueOf(Integer
										.parseInt(Integer
												.toString(recvMsg[3] & 0xff)));
								maiB = String.valueOf(Integer.parseInt(Integer
										.toString(recvMsg[4] & 0xff)));

								StringBuffer repBuffer = new StringBuffer();
								repBuffer.append("['6',");
								repBuffer.append(xueYang); // 血氧值
								repBuffer.append(",");
								repBuffer.append(maiB); // 脉搏值，为0时，浏览器可弃置该值，不改变页面中该值的内容
								repBuffer.append("]");

								String callBackMsg = new StringBuffer("接收到血氧仪测量数据。")
										.append("血氧：").append(xueYang)
										.toString();
								msgCallback.onReceiveMessage(callBackMsg);

								JudgeXueyFeedback(recvMsg);
								continue;
							}

							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("25")) {
								// maiB =
								// String.valueOf(Integer.parseInt(Integer
								// .toString(recvMsg[3] & 0xff)));
								// StringBuffer repBuffer = new StringBuffer();
								// repBuffer.append("['6',");
								// repBuffer.append(0); // 血氧值，为0时，浏览器弃置该值
								// repBuffer.append(",");
								// repBuffer.append(maiB); // 脉搏值
								// repBuffer.append("]");
								// msg = MainActivity.handler.obtainMessage();
								// msg.what = 0;
								// msg.obj = repBuffer.toString();
								// MainActivity.handler.sendMessage(msg);
								// Log.d("caolm:JudgeXueyFeedback",
								// "----------------");
								// JudgeXueyFeedback(recvMsg);
								continue;
							}

							// 体重、步距
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("20")) {
								data = new byte[] { (byte) 0x00, (byte) 0x00,
										(byte) 0xc4, 0x00, 0x00, 0x00, 0x00,
										0x20 };
								byte[] byteArrTmp = int2ByteArr(MeasureAty.userInfo
										.getWeight());
								data[3] = byteArrTmp[0];
								data[4] = byteArrTmp[1];
								data[5] = (byte) (MeasureAty.userInfo
										.getStep());
								data[2] = (byte) set_bit(data[2], 6);
								if (!pprocessData(data)) {
									data[2] = (byte) reverse_bit(data[2], 7);
								}
								isWrite = true;
								continue;
							}

							// 计步器清零数据
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("21")) {
								// 计步器不清零响应消息
								data = new byte[] { (byte) 0x00, (byte) 0x00,
										(byte) 0x00, 0x03, 0x00, 0x00, 0x00,
										0x21 };
								data[2] = recvMsg[2];
								data[2] = (byte) set_bit(data[2], 6);
								if (!pprocessData(data)) {
									data[2] = (byte) reverse_bit(data[2], 7);
								}
								isWrite = true;
								continue;
							}

							// 记步数据(30天数据)
							if (recvMsg[7] >= 38 && recvMsg[7] <= 67) {
								// 记步数据
								String stepType = Integer
										.toHexString(recvMsg[6] & 0xff);
								if (stepType.equalsIgnoreCase("1")) { // 总步数
									jibuBuffer = new StringBuffer("['5',");
									byte[] btmp = new byte[4];
									btmp[0] = recvMsg[5];
									btmp[1] = recvMsg[4];
									btmp[2] = recvMsg[3];
									btmp[3] = 0x00;
									int step_total = byteArrtoInt(btmp);
									jibuBuffer.append(step_total);
									jibuBuffer.append(",'");
								} else if (stepType.equalsIgnoreCase("2")) { // 卡路里
									byte[] btmp = new byte[4];
									btmp[0] = recvMsg[5];
									btmp[1] = recvMsg[4];
									btmp[2] = 0x00;
									btmp[3] = 0x00;
									int kcal_total = byteArrtoInt(btmp);
									jibuBuffer.append(String
											.valueOf(kcal_total / 100)
											+ "."
											+ String.valueOf(kcal_total % 100));
									jibuBuffer.append("',");
								} else if (stepType.equalsIgnoreCase("3")) { // 有氧步数
									byte[] btmp = new byte[4];
									btmp[0] = recvMsg[5];
									btmp[1] = recvMsg[4];
									btmp[2] = recvMsg[3];
									btmp[3] = 0x00;
									int o2step_total = byteArrtoInt(btmp);
									jibuBuffer.append(o2step_total);
									jibuBuffer.append(",'");
								} else if (stepType.equalsIgnoreCase("4")) { // 有氧卡路里
									byte[] btmp = new byte[4];
									btmp[0] = recvMsg[5];
									btmp[1] = recvMsg[4];
									btmp[2] = 0x00;
									btmp[3] = 0x00;
									int o2kcal_total = byteArrtoInt(btmp);
									jibuBuffer.append(o2kcal_total);
									jibuBuffer.append("']");

									String callBackMsg = new StringBuffer("接收到计步器测量数据。")
											.toString();
									msgCallback.onReceiveMessage(callBackMsg);
								}
								continue;
							}

							// 耳温枪
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("44")) {
								int data = bytesToInt(recvMsg[3], recvMsg[4]);
								wendu = String.valueOf(data / 10) + "."
										+ String.valueOf(data % 10);
								StringBuffer repBuffer = new StringBuffer();
								repBuffer.append("['4','");
								repBuffer.append(wendu);
								repBuffer.append("']");

								String callBackMsg = new StringBuffer("接收到耳温枪测量数据。")
										.append("温度：").append(wendu)
										.toString();
								msgCallback.onReceiveMessage(callBackMsg);

								judgeTemperatureFeedback(recvMsg);
								continue;
							}

							// 设备异常
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("2")) {
								String parm1;
								String parm2;
								msg.what = 0;
								parm1 = Integer.toHexString(recvMsg[2] & 0xff);
								if (parm1.equalsIgnoreCase("87")) {
									parm2 = Integer
											.toHexString(recvMsg[6] & 0xff);
								} else {
									parm2 = Integer
											.toHexString(recvMsg[3] & 0xff);
								}
								if (parm1.length() == 1) {
									parm1 = "0" + parm1;
								}
								if (parm2.length() == 1) {
									parm2 = "0" + parm2;
								}

								String callBackMsg = new StringBuffer("设备异常").toString();
								msgCallback.onReceiveMessage(callBackMsg);

								continue;
							}

							// 设备低压
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("8")) {

								String callBackMsg = new StringBuffer("设备低压").toString();
								msgCallback.onReceiveMessage(callBackMsg);

								continue;
							}

							// 设备关机
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("6")) {

								String callBackMsg = "javascript:usbReceiveData("
										+ "[-3, " + lx + "]" + ")";
								msgCallback.onReceiveMessage(callBackMsg);

								continue;
							}

							// 拔出设备(
							if (Integer.toHexString(recvMsg[0] & 0xff)
									.equalsIgnoreCase("9d")
									|| Integer.toHexString(recvMsg[0] & 0xff)
									.equalsIgnoreCase("fc")) {
								resetHid();
								// closeHid();
								openValue = -1;
								continue;
							}

							// usb序列号
							if (Integer.toHexString(recvMsg[7] & 0xff)
									.equalsIgnoreCase("cc")) {
								String serialNumber = "";
								for (int i = 0; i < 7; i++) {
									serialNumber += Integer
											.toHexString(recvMsg[i] & 0xff);
								}
								LogUtils.d("serialNumber is :" + serialNumber);
								StringBuffer repBuffer = new StringBuffer();
								repBuffer.append("['-6','");
								repBuffer.append(serialNumber);
								repBuffer.append("']");

								String callBackMsg = "javascript:usbReceiveData("
										+ repBuffer.toString() + ")";
								msgCallback.onReceiveMessage(callBackMsg);

								continue;
							}

							for (int j = 0; j < 5; j++) {
								if (!HandShakeSignal[j]
										.equalsIgnoreCase(Integer
												.toHexString(recvMsg[j + 3] & 0xff))) {
									isHandShake = false;
								}
							}

							if (isHandShake) {

								between = new Date().getTime() - curTime;

								if (between < 200) {
									continue;
								}

								String[] deviceNames = {"脂肪仪", "体重计", "血压计", "血糖仪", "耳温枪", "计步器", "血氧仪"};

								if (Integer.toHexString(recvMsg[2] & 0xff)
										.equalsIgnoreCase("2")) // 设备为脂肪仪
								{
									lx = 0;
								}
								if (Integer.toHexString(recvMsg[2] & 0xff)
										.equalsIgnoreCase("1")) // 设备为体重计
								{
									lx = 1;
								}
								if (Integer.toHexString(recvMsg[2] & 0xff)
										.equalsIgnoreCase("83")) // 设备为血压计
								{
									lx = 2;
								}
								if (Integer.toHexString(recvMsg[2] & 0xff)
										.equalsIgnoreCase("85")) // 设备为血糖仪
								{
									lx = 3;
								}
								if (Integer.toHexString(recvMsg[2] & 0xff)
										.equalsIgnoreCase("6")) // 设备为血氧仪
								{
									lx = 6;
								}
								if (Integer.toHexString(recvMsg[2] & 0xff)
										.equalsIgnoreCase("4")) // 设备为计步器
								{
									lx = 5;
								}
								if (Integer.toHexString(recvMsg[2] & 0xff)
										.equalsIgnoreCase("7")) // 设备为耳温枪
								{
									lx = 4;
								}
								StringBuffer repBuffer = new StringBuffer();
								repBuffer.append("[-1,'");
								repBuffer.append(lx);
								repBuffer.append("']");

								if(lx < deviceNames.length) {
									String callBackMsg = "检测到设备连接，设备类型：" + deviceNames[lx];
									msgCallback.onReceiveMessage(callBackMsg);
								}

								data[0] = (byte) 0x00;
								data[1] = (byte) 0x00;
								data[2] = recvMsg[2];
								data[3] = (byte) 0xa5;
								data[4] = 0x5a;
								data[5] = (byte) 0xa5;
								data[6] = 0x5a;
								data[7] = 0x1;
								if (lx == 3) {
									data[2] = 0x45;
								} else {
									data[2] = (byte) (data[2] | 64);
									if (!pprocessData(data)) {
										data[2] = (byte) (data[2] | 128);
									}
								}
								isWrite = true;

								// }

								curTime = new Date().getTime();

								continue;
							}
						}

						// }

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

}

package com.pd.hid;

/**
 * 定义回调接口，hid模块接收到数据后通知activity
 * @author fenghui
 *
 */
public interface IHidMsgCallback {

	/**
	 * TODO 这里作为demo返回String结果，具体开发中根据实际场景具体处理
	 * @param content
	 */
	void onReceiveMessage(String content);
}

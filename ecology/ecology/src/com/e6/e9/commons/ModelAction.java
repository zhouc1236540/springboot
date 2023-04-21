/**
* @Title: ModelAction
* @Description: 
* @author: HM
* @date 2022年5月4日 下午7:21:23
*/
package com.e6.e9.commons;

import java.util.Map;

import org.apache.log4j.Logger;

import com.e6.common.utils.CustomUtil;
import com.weaver.general.Util;

import weaver.formmode.customjavacode.AbstractModeExpandJavaCode;
import weaver.soa.workflow.request.RequestInfo;

/**
 * 描述:台账接口
 * 
 * @author HM
 * @date 2022年5月4日
 *
 */
public abstract class ModelAction extends AbstractModeExpandJavaCode {
	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * <p>
	 * 
	 * @Title:doModeExpand
	 *                     </p>
	 *                     <p>
	 * @Description:
	 *               </p>
	 * 
	 * @param arg0
	 * @throws Exception
	 * @see weaver.formmode.customjavacode.AbstractModeExpandJavaCode#doModeExpand(java.util.Map)
	 */
	@Override
	public void doModeExpand(Map<String, Object> param) throws Exception {
		RequestInfo requestInfo = (RequestInfo) param.get("RequestInfo");
		String requestId = requestInfo.getRequestid();
		log.error(this.getClass().getName() + " Start, requestId = " + requestId);
		int billid = -1;// 数据id
		int modeid = -1;// 模块id
		try {

			if (requestInfo != null) {
				billid = Util.getIntValue(requestInfo.getRequestid());
				modeid = Util.getIntValue(requestInfo.getWorkflowid());
				if (billid > 0 && modeid > 0) {
					process(requestInfo);
				}
			}
		} catch (Exception e) {
			String msg = "<div style=\"padding-bottom: 10px;\">报错时间：" + CustomUtil.getStringDate("yyyy-MM-dd HH:mm:ss")
					+ "， 请求ID：" + requestId + "，文件：" + getClass().getName() + "</div>" + "<span>提示消息：" + e.getMessage()
					+ "</span>" + "<span>数据id：" + billid + "模块id:" + modeid + "</span>";
			log.error(msg, e);
			requestInfo.getRequestManager().setMessageid("20088");
			requestInfo.getRequestManager().setMessagecontent(msg);
		} finally {
			log.error(this.getClass().getName() + " End, requestId = " + requestId);
		}
	}

	public abstract void process(RequestInfo requestInfo) throws Exception;

}

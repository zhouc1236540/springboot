/**
* @Title: RequestInfoService
* @Description: 
* @author: HM
* @date 2022年4月21日 下午4:36:09
*/
package com.e6.e9.service;

import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.Property;

/**
 * 描述:流程创建封装
 * 
 * @author HM
 * @date 2022年4月21日
 *
 */
public class RequestInfoService {

	public static Property generateProperty(String name, Object value, String type) {
		Property field = new Property();
		field.setName(name);
		field.setValue(value + "");
		field.setType(type);
		return field;
	}

	public static Cell generateCell(String name, Object value, String type) {
		Cell field = new Cell();
		field.setName(name);
		field.setValue(value + "");
		field.setType(type);
		return field;
	}

}

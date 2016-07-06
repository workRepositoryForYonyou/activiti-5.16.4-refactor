/**
 * 
 */
package com.yonyou.bpm.engine.query.item;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;

import com.yonyou.bpm.engine.query.data.ObjectProperty;

/**
 * @author chouhl
 *
 */
public abstract class ListItem extends PropertysetItem implements Comparable<ListItem>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3553842650969566488L;
	
	@Override
	public int compareTo(ListItem other) {
		String id = (String)this.getItemProperty("pk").getValue();
		String otherId = (String)other.getItemProperty("pk").getValue();
		
		return id.compareTo(otherId);
	}
	
	protected <T extends Object>void changeToItem(T t){
		Field[] fs = t.getClass().getDeclaredFields();
		int len = fs != null ? fs.length : 0;
		for(int i = 0; i < len; i++){
			if(fs[i] == null){
				continue;
			}
			
			if(!fs[i].getType().isAssignableFrom(String.class)){
				continue;
			}
			
			if(fs[i].getModifiers() != Modifier.PRIVATE){
				continue;
			}
			
			try {
				Method get = t.getClass().getDeclaredMethod("get" + fs[i].getName().substring(0,1).toUpperCase() + fs[i].getName().substring(1), new Class[0]);
				addItemProperty(fs[i].getName(), new ObjectProperty<String>(get.invoke(t, new Object[0]), String.class));
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		
		this.addDefaultItems(t);
	}
	
	/**
	 * 增加参照默认Item属性
	 * pk、code、name、pk_extends（pk扩展信息,多值以","分隔）
	 * @param t
	 */
	protected abstract <T extends Object>void addDefaultItems(T t);
	
}
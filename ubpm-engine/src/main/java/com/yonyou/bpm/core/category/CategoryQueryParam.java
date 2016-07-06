package com.yonyou.bpm.core.category;

import java.util.Set;
/**
 * 根据目录过滤
 * @author zhaohb
 *
 * @param <T>
 */
public interface CategoryQueryParam<T> {
	
	T categoryIds(Set<String> categoryIds);

}

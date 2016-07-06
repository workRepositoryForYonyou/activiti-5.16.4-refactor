package com.yonyou.bpm.core.category;

import java.util.List;

import com.yonyou.bpm.core.impl.CategoryLinkQueryParam;
import com.yonyou.bpm.core.impl.CategoryQueryParam;


/**
 * 目录管理
 * @author zhaohb
 *
 */
public interface CategoryService {
	/**
	 * 根据目录ID获取目录
	 * @param id
	 * @return
	 */
    Category getCategoryById(String id);
    
    
    /**
     * 查询所有目录分类
     * @return
     */
    List<? extends Category> findAllCategory();
    /**
     * 查询带有应用的目录
     * @return
     */
    List<? extends Category> findAllCategoryWithApplication();
    
    /**
     * 根据ids获取目录
     * @param ids
     * @return
     */
    List<? extends Category> findCategoriesByIds(String[] ids);
    /**
     * 获取名称或者编码匹配的目录
     * @param keyWord
     * @return
     */
    List<? extends Category> findCategoriesByKeyWord(String keyWord);
    /**
     * 获取名称或者编码匹配的目录
     * @param code
     * @param name
     * @return
     */
    List<? extends Category> findCategoriesByCodeOrName(String code,String name);
    /**
     * 获取用户可以管理的目录
     * @param userId
     * @return
     */
    List<? extends Category> findAuthorizedCategoriesByUserId(CategoryLinkQueryParam categoryLinkQueryParam);
    
    List<? extends Category> getList(CategoryQueryParam categoryQueryParam);
    
    List<? extends CategoryLink> getList(CategoryLinkQueryParam categoryLinkQueryParam);
    /**
     * 用户是否可以管理此目录
     * @param userId
     * @param categoryId
     * @return
     */
    boolean isAuthorized(CategoryLinkQueryParam categoryLinkQueryParam);
    /**
     * 删除目录
     * @param id
     */
    int delete(Category category)throws Exception;
    /**
     * 删除目录
     * @param Categories
     */
    int delete(Category[] Categories)throws Exception;
    
    /**
     * 保存目录
     * @param category
     * @return
     * @throws Exception
     */
    int save(Category category) throws Exception ;
    /**
     * 批量保存
     * @param categoryLinks
     * @throws Exception
     */
    public int save(List<CategoryLink> categoryLinks)throws Exception;
    /**
     * 批量删除
     * @param categoryLinks
     * @return
     * @throws Exception
     */
    public int delete(List<CategoryLink> categoryLinks) throws Exception ;
}

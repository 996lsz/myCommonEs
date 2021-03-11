package com.core.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.core.constant.EsBaseAnnotationConstant;
import lombok.Data;

import java.util.List;

/**
 * description
 * 
 * @author LSZ 2020/07/23 14:14
 * @contact 648748030@qq.com
 */
public class EsPageHelper {

	private static ThreadLocal<EsPageInfo> threadLocal = new ThreadLocal();

	public static EsPageInfo getPageInfo(){
		return threadLocal.get();
	}

	public static void remove(){
		threadLocal.remove();
	}

	public static void setTrackTotalHits(Boolean trackTotalHits){
		EsPageInfo esPage = threadLocal.get();
		esPage = esPage == null ? new EsPageInfo() : esPage;
		esPage.setTrackTotalHits(trackTotalHits);
		threadLocal.set(esPage);
	}

	public static void startPage(int page, int size){
		EsPageInfo esPage = threadLocal.get();
		esPage = esPage == null ? new EsPageInfo() : esPage;
		esPage.setSize(size);
		if(page > 1){
			esPage.setFrom((page-1) * size + 1);
		}
		threadLocal.set(esPage);
	}

	public static void startPage(int page, int size, Boolean trackTotalHits){
		startPage(page, size);
		setTrackTotalHits(trackTotalHits);
	}

	public static void startPage(int page, int size, String field, EsBaseAnnotationConstant.Sort sort){
		startPage(page, size);
		orderBy(field, sort);
	}

	public static void startPage(int page, int size, String field, EsBaseAnnotationConstant.Sort sort, Boolean trackTotalHits){
		startPage(page, size);
		orderBy(field, sort);
		setTrackTotalHits(trackTotalHits);
	}

	public static void orderBy(String field, EsBaseAnnotationConstant.Sort sort) {
		EsPageInfo esPage = threadLocal.get();
		esPage = esPage == null ? new EsPageInfo() : esPage;
		JSONArray sortObject = esPage.getSort() == null ? new JSONArray() : esPage.getSort();
		if(sort == EsBaseAnnotationConstant.Sort.DESC){
			sortObject.add(JSONObject.parseObject(String.format("{\"%s\":{\"order\":\"desc\"}}", field)));
		}else{
			sortObject.add(JSONObject.parseObject(String.format("{\"%s\":{\"order\":\"asc\"}}", field)));
		}
		esPage.setSort(sortObject);
		threadLocal.set(esPage);
	}

	public static void setSource(String... source){
		EsPageInfo esPage = threadLocal.get();
		esPage = esPage == null ? new EsPageInfo() : esPage;
		esPage.setSource(source);
		threadLocal.set(esPage);
	}

	@Data
	public static class EsPageInfo{

		private Integer from;

		private Integer size;

		private JSONArray sort;

		private Boolean trackTotalHits;

		private String[] source;
	}

}

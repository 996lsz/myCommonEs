package com.core.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.core.annotation.Id;
import com.core.constant.EsBaseAnnotationConstant;
import com.core.entity.common.*;
import com.core.filter.QueryFilter;
import com.core.utils.EsConfigHelper;
import com.core.utils.EsPageHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * description
 * 
 * @author LSZ 2020/07/22 11:25
 * @contact 648748030@qq.com
 */
@Service
public abstract class EsService<T> {

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RestClient restClient;

	/**
	 * 根据条件查询
	 * @param o
	 * @return
	 */
	public SearchResult<T> search(Object o){
		return search(o,null);
	}

	/**
	 * 根据条件查询
	 * @param esQuery
	 * @return
	 */
	public SearchResult<T> search(EsQuery esQuery){
		return search(null, esQuery);
	}

	/**
	 * 根据条件查询
	 * @param esQuery
	 * @return
	 */
	public SearchResult<T> search(Object o, EsQuery esQuery){
		JSONObject query;
		try {
			query = prepareSearch(o, esQuery);

			HttpEntity entity = new NStringEntity(query.toJSONString(), ContentType.APPLICATION_JSON);
			Request request = new Request("GET", EsConfigHelper.getServiceClassIndexName(this.getClass()) + "/_search");
			request.setEntity(entity);
			Response response = restClient.performRequest(request);
			return JSONObject.parseObject(EntityUtils.toString(response.getEntity()), new TypeReference<SearchResult<T>>(EsConfigHelper.getserviceGenericityClass(this.getClass())){});

		}catch (Exception e){
			throw new RuntimeException(e);
		}finally {
			EsPageHelper.remove();
		}
	}


	/**
	 * 根据条件查询符合的一条记录
	 * @param o
	 * @return
	 */
	public InnerHits<T> searchOne(Object o){
		try {
			SearchResult<T> result = search(o);
			List<InnerHits<T>> hits = result.getHits().getHits();
			if(hits.size() > 1){
				throw new RuntimeException("searchOne but found "+ hits.size());
			}
			if(hits.size() == 0){
				return null;
			}
			return hits.get(0);

		}catch (Exception e){
			throw new RuntimeException(e);
		}finally {
			EsPageHelper.remove();
		}
	}

	/**
	 * 根据主键查询
	 * @param o 可以传实体类或者id
	 * @return
	 */
	public InnerHits<T> searchById(Object o){
		String indexName;
		String id = null;
		List<IndexProperty.Property> indexProperty = EsConfigHelper.getIndexProperty(o.getClass(), Id.class);
		//通过实体类传参
		if(indexProperty != null){
			if(indexProperty.size() > 1){
				throw new RuntimeException("index more than one id annotation");
			}
			indexName = EsConfigHelper.getIndexName(o.getClass());
			try {
				id = indexProperty.get(0).getGetMethod().invoke(o).toString();
			} catch (Exception e) {
				LOGGER.error("selectById error", e);
			}
		}else{
			indexName = EsConfigHelper.getServiceClassIndexName(this.getClass());
			id = o.toString();
		}
		if(indexName == null){
			throw new RuntimeException("selectById cannot found index name");
		}
		try {
			Request request = new Request("GET", indexName + "/_doc/" + id);
			Response response = restClient.performRequest(request);
			return JSONObject.parseObject(EntityUtils.toString(response.getEntity()), new TypeReference<InnerHits<T>>(EsConfigHelper.getserviceGenericityClass(this.getClass())){});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	public JSONObject prepareSearch(Object o, EsQuery esQuery){
		JSONObject query;
		JSONObject query1 = null;
		JSONObject query2 = null;
		try {
			if(o != null) {
				query1 = JSONObject.parseObject("{\"query\":{\"bool\":{\"must\":[],\"must_not\":[],\"should\":[]}}}");
				for (QueryFilter filter : EsConfigHelper.getQueryFilter()) {
					try {
						filter.execute(query1, o);
					} catch (Exception e) {
						LOGGER.error("es query error", e);
					}
				}
			}
			if(esQuery != null){
				query2 = esQuery.getQueryObject();
			}
			query = merge(query1, query2);
			//查询sql优化
			optimizeQuery(query);
			return query;
		}finally {
			EsPageHelper.remove();
		}
	}

	public JSONObject prepareSearch(Object o){
		return prepareSearch(o, null);
	}

	/**
	 * 查询sql优化
	 * @param query
	 */
	public void optimizeQuery(JSONObject query){
		JSONObject temp = query.getJSONObject("query").getJSONObject("bool");
		if(temp.getJSONArray("must").size() == 0){
			temp.remove("must");
		}
		if(temp.getJSONArray("must_not").size() == 0){
			temp.remove("must_not");
		}
		if(temp.getJSONArray("should").size() == 0){
			temp.remove("should");
		}
	}

	/**
	 * 统计总数
	 * @param o
	 * @return
	 */
	public Long count(Object o){
		try {
			Request request = new Request("GET", EsConfigHelper.getIndexName(o.getClass()) + "/_count");
			Response response = restClient.performRequest(request);
			JSONObject responseResult = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
			return responseResult.getLong("count");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * 统计最大值
	 * @param colunm 字段
	 * @return
	 */
	public Object max(Object o, String colunm) throws IOException {
		return aggs(o, EsBaseAnnotationConstant.EsAggsTypeEnum.MAX, colunm);
	}

	/**
	 * 统计最小值
	 * @param colunm 字段
	 * @return
	 */
	public Object min(Object o, String colunm) throws IOException {
		return aggs(o, EsBaseAnnotationConstant.EsAggsTypeEnum.MIN, colunm);
	}

	/**
	 * 统计汇总值
	 * @param colunm 字段
	 * @return
	 */
	public Object sum(Object o, String colunm) throws IOException {
		return aggs(o, EsBaseAnnotationConstant.EsAggsTypeEnum.SUM, colunm);
	}

	/**
	 * 统计平均值
	 * @param colunm 字段
	 * @return
	 */
	public Object avg(Object o, String colunm) throws IOException {
		return aggs(o, EsBaseAnnotationConstant.EsAggsTypeEnum.AVG, colunm);
	}

	/**
	 *
	 * @param types max/min/avg/sum
	 * @param colunms 需要统计的字段
	 * @return
	 */
	public Object[] multiAggs(Object o, EsBaseAnnotationConstant.EsAggsTypeEnum[] types, String[] colunms) throws IOException {
		return aggs(o, types, colunms);
	}

	private Object aggs(Object o, EsBaseAnnotationConstant.EsAggsTypeEnum type, String colunm) throws IOException {
		EsBaseAnnotationConstant.EsAggsTypeEnum[] types = {type};
		String[] colunms = {colunm};
		return aggs(o, types, colunms)[0];
	}

	private Object[] aggs(Object o, EsBaseAnnotationConstant.EsAggsTypeEnum[] types, String[] colunms) throws IOException {
		if(ArrayUtils.isEmpty(types) || ArrayUtils.isEmpty(colunms) || types.length != colunms.length){
			throw new RuntimeException("types length no equal colunms length");
		}
		String indexName = EsConfigHelper.getIndexName(o.getClass());
		Object[] result = new Object[types.length];
		//拼接aggs查询语句
		JSONObject query = prepareSearch(o);;
		query.put("size", 0);
		JSONObject aggs = new JSONObject();
		for(int i = 0; i < colunms.length; i++){
			String aggStr = String.format("{\"%s\":{\"field\": \"%s\"}}", types[i].getType(), colunms[i]);
			aggs.put("aggs" + i, JSONObject.parseObject(aggStr));
		}
		query.put("aggs", aggs);
		HttpEntity entity = new NStringEntity(query.toJSONString(), ContentType.APPLICATION_JSON);
		Request request = new Request("GET", indexName + "/_search");
		request.setEntity(entity);
		Response response = restClient.performRequest(request);
		JSONObject responseObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
		responseObject = responseObject.getJSONObject("aggregations");
		for(int i = 0; i < colunms.length; i++){
			result[i] = responseObject.getJSONObject("aggs" + i).get("value");
		}
		return result;
	}


	/**
	 * @description:
	 * @param pageSize 每次取数大小，默认为size的大小
	 * @param processFunction 对数据的处理过程
	 * @return: void
	 * @author: panzhihui
	 * @time: 2020/5/28 18:00
	 */
	public void searchScroll(Object o, int pageSize, int timeOut, Consumer<SearchResult<T>> processFunction) {
		try {
			SearchResult[] result = new SearchResult[2];
			String indexName = EsConfigHelper.getIndexName(o.getClass());
			String url = indexName + "/_search?scroll=" + timeOut + "m";
			JSONObject query = prepareSearch(o);
			query.put("size", pageSize);
			System.out.println(query.toJSONString());
			//首轮查询
			HttpEntity entity = new NStringEntity(JSONObject.toJSONString(query), ContentType.APPLICATION_JSON);
			Request request = new Request("GET", url);
			request.setEntity(entity);
			Response response = restClient.performRequest(request);
			SearchResult<T> firstResult = JSONObject.parseObject(EntityUtils.toString(response.getEntity()), new TypeReference<SearchResult<T>>(EsConfigHelper.getserviceGenericityClass(this.getClass())){});
			result[0] = firstResult;
			String scrollId = firstResult.getScrollId();
			System.out.println("首查询");
			int i = 0;
			while (true) {
				SearchResult consumeData = result[i % 2];
				i++;
				if (consumeData.getHits().getHits().size() == 0) {
					break;
				}
				CompletableFuture<SearchResult> nextResult = CompletableFuture.supplyAsync(() -> searchScroll(timeOut, scrollId));
				processFunction.accept(consumeData);
				result[i % 2] = nextResult.get();
			}
			deleteScrollId(scrollId);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}


	/**
	 * @description:根据scrollId取下一页数据
	 * @param timeOut 游标有效时间，分钟
	 * @param scrollId 游标ID
	 * @return: com.alibaba.fastjson.JSONObject
	 * @author: panzhihui
	 * @time: 2020/5/28 15:51
	 */
	private SearchResult searchScroll(int timeOut, String scrollId) {
		try {
			String qsl = String.format("{\"scroll\":\"%sm\",\"scroll_id\":\"%s\"}", timeOut, scrollId);
			String url = "/_search/scroll";
			HttpEntity entity = new NStringEntity(qsl, ContentType.APPLICATION_JSON);
			Request request = new Request("GET", url);
			request.setEntity(entity);
			Response response = restClient.performRequest(request);
			return JSONObject.parseObject(EntityUtils.toString(response.getEntity()), new TypeReference<SearchResult<T>>(EsConfigHelper.getserviceGenericityClass(this.getClass())) {
			});
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * @description: 删除scroll
	 * @param scrollId
	 * @author: panzhihui  2020/6/19 15:08
	 */
	private void deleteScrollId(String scrollId) {
		String url = "/_search/scroll/";
		HttpEntity entity = new NStringEntity("{\"scroll_id\":\""+scrollId+"\"}", ContentType.APPLICATION_JSON);
		try {
			Request request = new Request("DELETE", url);
			request.setEntity(entity);
			Response response = restClient.performRequest(request);
		} catch (IOException e) {
			LOGGER.info("delete scroll id fail",e);
		}
	}

	/**
	 * 创建
	 * @param t
	 */
	public CUDResult create(T t){
		try {
			String indexName = EsConfigHelper.getIndexName(t.getClass());
			Object id = EsConfigHelper.getIndexProperty(t.getClass(), Id.class).get(0).getGetMethod().invoke(t);
			HttpEntity entity = new NStringEntity(JSONObject.toJSONString(t), ContentType.APPLICATION_JSON);
			Request request = new Request("PUT", indexName + "/_create/" + id);
			request.setEntity(entity);
			Response response = restClient.performRequest(request);
			return JSONObject.parseObject(EntityUtils.toString(response.getEntity()), CUDResult.class);

		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * 更新（覆盖）
	 * @param t
	 */
	public CUDResult updateById(T t){
		try {
			String indexName = EsConfigHelper.getIndexName(t.getClass());
			Object id = EsConfigHelper.getIndexProperty(t.getClass(), Id.class).get(0).getGetMethod().invoke(t);
			HttpEntity entity = new NStringEntity(JSONObject.toJSONString(t), ContentType.APPLICATION_JSON);
			Request request = new Request("PUT", indexName + "/_doc/" + id);
			request.setEntity(entity);
			Response response = restClient.performRequest(request);
			return JSONObject.parseObject(EntityUtils.toString(response.getEntity()), CUDResult.class);

		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * 更新非空字段
	 * @param t
	 */
	public CUDResult updateByIdSelective(T t){
		try {
			String indexName = EsConfigHelper.getIndexName(t.getClass());
			Object id = EsConfigHelper.getIndexProperty(t.getClass(), Id.class).get(0).getGetMethod().invoke(t);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("doc", t);
			HttpEntity entity = new NStringEntity(jsonObject.toJSONString(), ContentType.APPLICATION_JSON);
			Request request = new Request("POST", indexName + "/_update/" + id);
			request.setEntity(entity);
			Response response = restClient.performRequest(request);
			return JSONObject.parseObject(EntityUtils.toString(response.getEntity()), CUDResult.class);

		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据条件更新指定字段（需要用再开发）
	 * @param query
	 * @param t
	 */
	public CUDResult updateSelective(Object query, T t){
		return null;
	}


	/**
	 * 根据Id删除（软删除更新is_deleted = 1）
	 * @param o 可以传实体类或者id
	 */
	public CUDResult deleteByIdSoft(Object o){
		try {
			String indexName;
			String id = null;
			List<IndexProperty.Property> indexProperty = EsConfigHelper.getIndexProperty(o.getClass(), Id.class);
			//通过实体类传参
			if (indexProperty != null) {
				if (indexProperty.size() > 1) {
					throw new RuntimeException("index more than one id annotation");
				}
				indexName = EsConfigHelper.getIndexName(o.getClass());
				try {
					id = indexProperty.get(0).getGetMethod().invoke(o).toString();
				} catch (Exception e) {
					LOGGER.error("selectById error", e);
				}
			} else {
				indexName = EsConfigHelper.getServiceClassIndexName(this.getClass());
				id = o.toString();
			}
			if (indexName == null) {
				throw new RuntimeException("selectById cannot found index name");
			}
			HttpEntity entity = new NStringEntity("{\"doc\":{\"is_deleted\":\"1\"}}", ContentType.APPLICATION_JSON);
			Request request = new Request("POST", indexName + "/_update/" + id);
			request.setEntity(entity);
			Response response = restClient.performRequest(request);
			return JSONObject.parseObject(EntityUtils.toString(response.getEntity()), CUDResult.class);

		}catch (Exception e){
			throw new RuntimeException(e);
		}

	}

	/**
	 * 根据Id删除
	 * @param o 可以传实体类或者id
	 */
	public CUDResult deleteById(Object o){
		try {
			String indexName;
			String id = null;
			List<IndexProperty.Property> indexProperty = EsConfigHelper.getIndexProperty(o.getClass(), Id.class);
			//通过实体类传参
			if (indexProperty != null) {
				if (indexProperty.size() > 1) {
					throw new RuntimeException("index more than one id annotation");
				}
				indexName = EsConfigHelper.getIndexName(o.getClass());
				try {
					id = indexProperty.get(0).getGetMethod().invoke(o).toString();
				} catch (Exception e) {
					LOGGER.error("selectById error", e);
				}
			} else {
				indexName = EsConfigHelper.getServiceClassIndexName(this.getClass());
				id = o.toString();
			}
			if (indexName == null) {
				throw new RuntimeException("deleteById cannot found index name");
			}
			Request request = new Request("DELETE", indexName + "/_doc/" + id);
			Response response = restClient.performRequest(request);
			return JSONObject.parseObject(EntityUtils.toString(response.getEntity()), CUDResult.class);

		}catch (Exception e){
			throw new RuntimeException(e);
		}

	}

	/**
	 * 删除符合条件的记录（需要用再开发）
	 * @param t
	 */
	public CUDResult delete(T t){
		return null;
	}

	private JSONObject merge(JSONObject j1, JSONObject j2){
		if(j1 == null || j1.isEmpty()){
			return j2;
		}
		if(j2 == null || j2.isEmpty()){
			return j1;
		}

		JSONObject result = new JSONObject();
		Set<String> keySet = new HashSet<>();
		keySet.addAll(j1.keySet());
		keySet.addAll(j2.keySet());

		for (String s : keySet) {
			Object value;
			Object value1 = j1.get(s);
			Object value2 = j2.get(s);
			if(j1.containsKey(s) && j2.containsKey(s)){
				if(value1 instanceof List){
					List list = new ArrayList((List) value1);
					list.addAll(new ArrayList((List) value2));
					value = list;
				}else{
					value = merge((JSONObject)value1, (JSONObject)value2);
				}
			}else{
				value = value1 == null ? value2 : value1;
			}
			result.put(s, value);

		}
		return result;
	}
}

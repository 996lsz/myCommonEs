import com.core.constant.EsBaseAnnotationConstant;
import com.core.entity.common.Bucket;
import com.core.entity.common.EsQuery;
import com.core.entity.common.InnerHits;
import com.core.entity.common.SearchResult;
import com.Application;
import com.core.utils.EsPageHelper;
import com.test.entity.po.BooksPO;
import com.test.entity.qto.BooksQTO;
import com.test.service.BookService;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * description
 * 
 * @author LSZ 2020/07/25 15:15
 * @contact 648748030@qq.com
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class EsTest {

	@Autowired
	private BookService bookService;

	@Test
	public void curd() throws ParseException {
		//增
		BooksPO booksPO = new BooksPO();
		booksPO.setBookId("10001");
		booksPO.setBookName("米奇不妙屋");
		booksPO.setAuthorName("污妖王");
		booksPO.setCover("xxx.jpg");
		booksPO.setCode("HOP-01");
		booksPO.setType("VAN");
		booksPO.setPublishTime(DateUtils.addMonths(new Date(),-1));
		booksPO.setCtime(new Date());
		booksPO.setMtime(new Date());
		try {
			bookService.create(booksPO);
		}catch (Exception e){}
		//查 - 根据ID
		InnerHits<BooksPO> searchById1 = bookService.searchById("10001");
		System.out.println("searchById1:" + searchById1.getSource().getBookName());
		InnerHits<BooksPO> searchById2 = bookService.searchById(booksPO);
		System.out.println("searchById2:" + searchById2.getSource().getBookName());
		//查 - 根据条件
		BooksQTO qto = new BooksQTO();
		qto.setAuthorName("污妖王");
		qto.setPublishTimeGte(DateUtils.parseDate("2020-06-25 11:11:11", "yyyy-MM-dd HH:mm:ss"));
		qto.setPublishTimeLte(DateUtils.parseDate("2020-08-26 11:11:11", "yyyy-MM-dd HH:mm:ss"));
		SearchResult<BooksPO> search = bookService.search(qto);
		System.out.println("search sql:" + bookService.prepareSearch(qto));
		System.out.println("search result:" + search.getHits().getHits());
		//改
		BooksPO update= new BooksPO();
		update.setBookId("10001");
		update.setCover("辣妹儿.jpg");
		//选择更新
		bookService.updateByIdSelective(update);
		//覆盖更新
		bookService.updateById(update);
		//删
		bookService.deleteByIdSoft(update);
		System.out.println(bookService.deleteByIdSoft("10001"));
		bookService.deleteById(update);
		//bookService.deleteById("10001");

	}


	@Test
	public void query() throws ParseException {
		BooksQTO qto = new BooksQTO();
		EsQuery esQuery = new EsQuery();
		qto.setBookIds(Arrays.asList("10001","10002"));
		qto.setBookName("书本");
		//qto.setAuthorName("某人");
		qto.setPublishTimeGte(DateUtils.parseDate("2020-06-25 11:11:11", "yyyy-MM-dd HH:mm:ss"));
		qto.setPublishTimeLte(DateUtils.parseDate("2020-08-26 11:11:11", "yyyy-MM-dd HH:mm:ss"));
		//两种效果一样， must_not type = VAN
		//qto.setTypeMustNot("VAN");
		esQuery.term("type", "VAN", EsBaseAnnotationConstant.BoolTypeEnum.MUST_NOT);

		qto.setSource(Arrays.asList("book_id","book_name"));
		EsPageHelper.startPage(1,100);
		EsPageHelper.orderBy("publish_time", EsBaseAnnotationConstant.Sort.DESC);
		System.out.println(bookService.prepareSearch(qto));

	}


	@Test
	public void testScorllo(){
		List<Object> list = new ArrayList<>();

		bookService.searchScroll(new BooksQTO(), 100,5, result ->{
			list.addAll(result.getHits().getHits());
		});

		System.out.println(list.size());
	}


	@Test
	public void testAggsQuery(){
		BooksQTO qto = new BooksQTO();
		qto.setPublishTimeGte(DateUtils.addDays(new Date(), -60));
		qto.setPublishTimeLte(DateUtils.addDays(new Date(), -1));
		EsQuery esQuery = new EsQuery().aggsTerms("type",100)
				.aggsTopHits(1,"publish_time", EsBaseAnnotationConstant.Sort.DESC);
		System.out.println(bookService.prepareSearch(qto, esQuery));
		SearchResult<BooksPO> search = bookService.search(qto, esQuery);
		List<Bucket<BooksPO>> buckets = search.getAggregations().getAggregationsData().getBuckets();
		System.out.println(buckets.get(0).getKey());
		System.out.println(buckets.get(0).getAggregationsData().getHits().getHits().get(0).getSource().getBookId());
	}

}

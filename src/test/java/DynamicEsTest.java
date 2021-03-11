import com.Application;
import com.core.constant.EsBaseAnnotationConstant;
import com.core.entity.common.*;
import com.core.exception.VersionConflictException;
import com.core.utils.EsPageHelper;
import com.test.entity.po.BooksPO;
import com.test.entity.qto.BooksQTO;
import com.test.service.BookService;
import com.test.service.MasterBookService;
import com.test.service.NodeBookService;
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
public class DynamicEsTest {

	@Autowired
	private MasterBookService masterBookService;

	@Autowired
	private NodeBookService nodeBookService;

	@Test
	public void testMaster() throws ParseException {
		//增
		BooksPO booksPO = new BooksPO();
		booksPO.setBookId("10010");
		booksPO.setBookName("测试多数据源");
		booksPO.setAuthorName("某作者");
		booksPO.setCover("xxx.jpg");
		booksPO.setCode("test010");
		booksPO.setType("test");
		booksPO.setPublishTime(DateUtils.addMonths(new Date(), -1));
		booksPO.setCtime(new Date());
		booksPO.setMtime(new Date());

		try {
			CUDResult cudResult = masterBookService.create(booksPO);
		}catch (VersionConflictException e){

		}
		System.out.println(masterBookService.searchById("10010"));

	}

	@Test
	public void testNode() throws ParseException {
		//增
		BooksPO booksPO = new BooksPO();
		booksPO.setBookId("10010");
		booksPO.setBookName("测试多数据源");
		booksPO.setAuthorName("某作者");
		booksPO.setCover("xxx.jpg");
		booksPO.setCode("test010");
		booksPO.setType("test");
		booksPO.setPublishTime(DateUtils.addMonths(new Date(), -1));
		booksPO.setCtime(new Date());
		booksPO.setMtime(new Date());

		try {
			CUDResult cudResult = nodeBookService.create(booksPO);
		}catch (VersionConflictException e){

		}
		System.out.println(nodeBookService.searchById("10010"));

	}

	/**
	 * 嵌套测试
	 */
	@Test
	public void test(){
		BooksPO booksPO = new BooksPO();
		booksPO.setBookId("10030");
		booksPO.setBookName("master");
		masterBookService.create(booksPO);
		booksPO.setBookName("node");
		nodeBookService.create(booksPO);
	}

	/**
	 * 嵌套测试
	 */
	@Test
	public void test2(){
		BooksPO booksPO = new BooksPO();
		booksPO.setBookId("10040");
		booksPO.setBookName("master");
		masterBookService.create(booksPO);
		booksPO.setBookName("node");
		masterBookService.createNodeBook(booksPO);
		booksPO.setBookId("10050");
		masterBookService.createNodeBookByAnnotation(booksPO);
	}

	/**
	 * 嵌套测试
	 */
	@Test
	public void test3(){
		BooksPO booksPO = new BooksPO();
		booksPO.setBookId("10060");
		booksPO.setBookName("嵌套测试");
		masterBookService.createMasterAndNodeBook(booksPO);
	}

}

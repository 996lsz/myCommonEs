package com.test.service;

import com.core.annotation.DS;
import com.core.service.EsService;
import com.test.entity.po.BooksPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * description
 * 
 * @author LSZ 2020/08/15 16:34
 * @contact 648748030@qq.com
 */
@Service
@DS("master")
public class MasterBookService extends EsService<BooksPO> {

	@Autowired
	private NodeBookService nodeBookService;

	public void createNodeBook(BooksPO booksPO){
		nodeBookService.create(booksPO);
	}

	@DS("node")
	public void createNodeBookByAnnotation(BooksPO booksPO){
		create(booksPO);
	}

	public void createMasterAndNodeBook(BooksPO booksPO){
		create(booksPO);
		nodeBookService.create(booksPO);
	}
}

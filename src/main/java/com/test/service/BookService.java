package com.test.service;

import com.core.annotation.DS;
import com.core.service.EsService;
import com.test.entity.po.BooksPO;
import org.springframework.stereotype.Service;

/**
 * description
 * 
 * @author LSZ 2020/08/15 16:34
 * @contact 648748030@qq.com
 */
@Service
@DS("master")
public class BookService extends EsService<BooksPO> {
    
}

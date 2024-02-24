# myCommonEs
由于某些历史问题，早期项目需要通过写json去操作es，做了一套框架去简化es的增删查改操作

支持多数据源

使用demo
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
			CUDResult cudResult = bookService.create(booksPO);
		}catch (Exception e){
		}
		//查 - 根据ID
		InnerHits<BooksPO> searchById1 = bookService.searchById("10001");
		System.out.println("searchById1:" + searchById1.getSource().getBookName());
		InnerHits<BooksPO> searchById2 = bookService.searchById(booksPO);
		System.out.println("searchById2:" + searchById2.getSource().getBookName());
		//查 - 根据条件
		BooksQTO qto = new BooksQTO();
		//EsPageHelper工具类可以完成分页，排序，查询指定字段
		EsPageHelper.setSource("book_name","code");
		qto.setAuthorName("污妖王");
		qto.setPublishTimeGte(DateUtils.parseDate("2021-01-01 11:11:11", "yyyy-MM-dd HH:mm:ss"));
		qto.setPublishTimeLte(DateUtils.parseDate("2021-02-26 11:11:11", "yyyy-MM-dd HH:mm:ss"));
		SearchResult<BooksPO> search = bookService.search(qto);
		System.out.println("search sql:" + bookService.prepareSearch(qto));
		System.out.println("search result:" + search.getHits().getHits());
		//改
		/*BooksPO update= new BooksPO();
		update.setBookId("10001");
		update.setCover("辣妹儿.jpg");
		//选择更新
		bookService.updateByIdSelective(update);
		//覆盖更新
		bookService.updateById(update);
		//删
		bookService.deleteByIdSoft(update);
		System.out.println(bookService.deleteByIdSoft("10001"));
		bookService.deleteById(update);*/
		//bookService.deleteById("10001");

	}

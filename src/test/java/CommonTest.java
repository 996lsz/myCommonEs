import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * description
 * 
 * @author LSZ 2020/07/15 17:59
 * @contact 648748030@qq.com
 */

public class CommonTest {

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());


	@Test
	public void test() {
		String[] test1 = {"1a","2","3"};
		List<String> test2 = new ArrayList<>();test2.add("1");test2.add("2");test2.add("3");

		System.out.println(test1.getClass());
		System.out.println(Arrays.asList(test1).toString());
		System.out.println(test2.toString());

		System.out.println(test1 instanceof Object[]);

		List<Date> dates = new ArrayList<>();
		dates.add(new Date());
		dates.add(new Date());

		System.out.println(test1.getClass().isArray());
	}

	@Test
	public void testBarrier(){
		CyclicBarrier barrier = new CyclicBarrier(5, () -> System.out.println("发令枪响了，跑！"));
		for (int i = 0; i < 15; i++) {
			new MyThread(barrier, "运动员" + i + "号").start();

		}
	}


	public class MyThread extends Thread {
		private CyclicBarrier cyclicBarrier;
		private String name;

		public MyThread(CyclicBarrier cyclicBarrier, String name) {
			super();
			this.cyclicBarrier = cyclicBarrier;
			this.name = name;
		}

		@Override
		public void run() {
			System.out.println(name + "开始准备");
			try {
				Thread.currentThread().sleep(5000);
				System.out.println(name + "准备完毕！等待发令枪");
				try {
					cyclicBarrier.await();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}



	@Test
	public void testPullALL(){
		JSONObject j1 = new JSONObject();
		JSONObject j1List = new JSONObject();
		JSONObject j2 = new JSONObject();
		JSONObject j2List = new JSONObject();

		j1List.put("list",Arrays.asList("1","2"));
		j1.put("test",j1List);
		j2List.put("list",Arrays.asList("3","4"));
		j2.put("test",j2List);
		j2.put("123",123);

		System.out.println(merge(j1,j2));


	}

	private JSONObject merge(JSONObject j1, JSONObject j2){
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

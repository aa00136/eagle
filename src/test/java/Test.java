import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huisa.common.database.BaseDao;
import com.huisa.common.exception.ServiceException;

public class Test {
	private static Logger logger = LoggerFactory.getLogger(Test.class);

	public static void main(String[] args) throws Exception {
		// 生成javabean
		try {
			new BaseDao()
					.generateJavaBean(
							"publish_subcribe_relation",
							"E:\\学习资料\\大学\\课程资料\\毕业设计\\eagle\\src\\test\\java",
							"com.cscw");
		} catch (ServiceException e) {
		}
	}
}

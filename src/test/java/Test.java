import com.huisa.common.database.BaseDao;
import com.huisa.common.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
	private static Logger logger = LoggerFactory.getLogger(Test.class);

	public static void main(String[] args) throws Exception {
		// 生成javabean
		try {
			new BaseDao()
					.generateJavaBean(
							"consume_info",
							"D:\\git_repository_self\\eagle\\src\\test\\java",
							"com.lgh");
		} catch (ServiceException e) {
		}
	}
}

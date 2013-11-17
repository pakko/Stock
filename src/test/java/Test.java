import hirondelle.date4j.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.ml.db.MongoDB;
import com.ml.model.StockCode;
import com.ml.task.InitiateDatasets;
import com.ml.util.Constants;
import com.ml.util.DateUtil;


public class Test {

	public static void main(String[] args) throws IOException {
		String confFile = Constants.DefaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		
		// stock codes
		//List<String> stockCodes = FileUtils.readLines(new File(args[1]));
		List<String> stockCodes = FileUtils.readLines(new File(Constants.CorpCodesFile));
		System.out.println("Corp code size: " + stockCodes.size());
		
		Map<String, String> map = InitiateDatasets.getStockCodes();
		for(String code: stockCodes) {
			StockCode sc = new StockCode(code, map.get(code.substring(2)));
			if(map.get(code.substring(2)) == null)
					System.out.println(sc);
			mongodb.save(sc, "stockCode");
		}
	}

}

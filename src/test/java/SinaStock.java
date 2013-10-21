

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Don.W.Lee
 * @version 1.0
 * @since 2012-03-29
 *
 */
public class SinaStock {
	private static String db  = "d:\\sina-stock-codes.txt" ;
	private static final int COLUMNS = 32;
	private static List<String> codes;
	/*
	static{
		File in = new File(db) ;
		if(! in.exists()){
			// 从网络获取
			if(codes.size() < 1 )
				try {
					codes = getAllStackCodes() ;
				} catch (IOException e) {
					e.printStackTrace();
				}
		}else{
			// 从本地获取
			if(codes.size() < 1)
				try {
					codes = getAllStockCodesFromLocal() ;
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	*/
	// 解析一组股票代码字符串   把code中包括的所有股票代码放入List中
	private static List<String> handleStockCode(String code){
		List<String> codes = null ;
		int end = code.indexOf(";") ;
		    code = code.substring(0,end) ;
		int start = code.lastIndexOf("=") ;
		   code = code.substring(start) ;
		   code = code.substring(code.indexOf("\"")+1,code.lastIndexOf("\"")) ;
		   codes = Arrays.asList(code.split(",")) ;
		return codes ;
	}
	
	//   返回的值是一个js代码段  包括指定url页面包含的所有股票代码
	private static String getBatchStackCodes(URL url) throws IOException{
	 	 URLConnection connection = url.openConnection() ;
		 connection.setConnectTimeout(30000) ;
		 BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream())) ;
		 String line = null ;
	   	 StringBuffer sb = new StringBuffer() ;
		boolean flag =false ;
		 while((line = br.readLine()) != null ){
			 if(line.contains("<script type=\"text/javascript\">") || flag){
				 sb.append(line) ;
				 flag = true ;
			 }
			 if(line.contains("</script>")){
				 flag =false ;
				 if(sb.length() > 0 ){
					 if(sb.toString().contains("code_list") && sb.toString().contains("element_list")){
						 break ;
					 }else{
						 sb.setLength(0) ;
					 }
				 }
			 }
		 }
		 if(br != null ){
			 br.close() ;
			 br= null ;
		 }
		return sb.toString() ;
	}
	
	// 获取新浪38也的所有股票代码
	private static List<String> getAllStackCodes() throws IOException{
		List<String> codes = new ArrayList<String>() ;
		int i =1 ;
		URL url = null ;
		// 新浪 股票 好像目前为止就 38页
		for(; i < 65 ; i ++ ){
			try{
				System.out.println("handling the " + i);
				 url = new URL("http://vip.stock.finance.sina.com.cn/q/go.php/vIR_CustomSearch/index.phtml?p="+i + "&sr_p=-1") ;
				 String code = getBatchStackCodes(url) ;
				 codes.addAll(handleStockCode(code)) ;
			} catch(Exception e) {
				System.err.println("error on handling: " + url + ", info: " + e.getMessage());
			}
		}
		if(! ( new File(db) ).exists() )
			saveStockCodes(codes) ;
		return codes ;
	}
	
	//把新浪38页的所有股票代码存入本地文件
	private static void saveStockCodes(List<String> codes ) throws IOException{
		//将所有股票代码存入文件中
		File out = new File(db) ;
		if(! out.exists())
			out.createNewFile() ;
		BufferedWriter bw = new BufferedWriter(new FileWriter(out)) ;
		for(String code : codes ){
			bw.write(code) ;
			bw.newLine() ;
		}
		if(bw != null ){
			bw.close() ;
			bw = null ;
		}
	}
	
	private static List<String> getAllStockCodesFromLocal() throws IOException{
		List<String> codes = new ArrayList<String>() ;
		File in = new File(db) ;
		if(! in.exists())
			throw new IOException("指定数据文件不存在!");
		BufferedReader br = new BufferedReader(new FileReader(in)) ;
		String line = null ;
		while( ( line = br.readLine() ) != null ){
			codes.add(line) ;
		}
		// 删除最后一个空行
		codes.remove(codes.size()-1) ;
		if(br != null ){
			br.close() ;
			br = null ;
		}
		return codes ;
	}
	
	public static String[]  getStockInfoByCode(String stockCode) throws IOException{
		// 仅仅打印
		 String[] stockInfo = new String[COLUMNS] ; 
		 URL url = new URL("http://hq.sinajs.cn/?list="+stockCode) ;
		 URLConnection connection = url.openConnection() ;
		 connection.setConnectTimeout(16000) ;
		 BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream())) ;
		 String line = null ;
		 StringBuffer sb = new StringBuffer() ;
		 while(( line = br.readLine()) != null ){
			 sb.append(line) ;
		 }
		 if(sb.length() > 0 ){
			 String rs = sb.toString() ;
			 rs = rs.substring(rs.indexOf("\"")+1,rs.lastIndexOf("\"")) ;
			 String[] rss = rs.split(",") ;
			 for(int i = 0 ;  i< rss.length ; i ++ ){
				 System.out.print(rss[i]+"\t|");
				 stockInfo[i] = rss[i];
			 }
			 System.out.println("\n------------------------------------"); 
		 }
		 return stockInfo ;
	}
	
	public static void getAllStockInfo() throws IOException{
		String[] header = getHeaders() ;
		System.out.println(header.length);
		for(int i = 0 ; i < header.length ;  i++ ){
			System.out.print(header[i]+"\t|");
		}
		for(String code : codes ){
			getStockInfoByCode(code) ;
		}
	}
	
	/**
	 * 
	 * @param first 从0开始
	 * @param last  不包括 last
	 * @return
	 */
	public static List<String[]> getStockInfo(int first , int last , int recoeds)throws Exception{
		List<String[]> stockInfo = new ArrayList<String[]>() ;
		first = first < 0 ? 0 : first ;
		if(first > last )
			throw new Exception("参数不合法!") ;
		int i = 0 ;
		while(last > codes.size()  ){
			if(first + recoeds < codes.size()+1 ){
				last = first +  recoeds ;
				break ;
			}else{
				last = first + recoeds +(--i) ;
			}
		}
		for( i = first ; i <= last ; i ++ ){
			stockInfo.add(getStockInfoByCode(codes.get(i))) ;
		}
		return stockInfo ;
	}
	
	public static String[] getHeaders(){
		String[] header = {"股票名字","今日开盘价	","昨日收盘价","当前价格","今日最高价","今日最低价","竟买价","竞卖价","成交的股票数","成交金额(元)","买一","买一","买二","买二","买三","买三","买四","买四","买五","买五","卖一","卖一","卖二","卖二","卖三","卖三","卖四","卖四","卖五","卖五","日期","时间"} ;
		return header ;
	}	
	
	public static List<String> getStockCodes(){
		return codes ;
	}
	public static void main(String[] args) {
		try {
			codes = getAllStackCodes();
			getAllStockInfo() ;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

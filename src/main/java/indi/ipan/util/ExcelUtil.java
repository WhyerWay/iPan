package indi.ipan.util;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


import indi.ipan.model.User;

@Component
public class ExcelUtil {
	private final static String EXCEL_2003L =".xls";    
	private final static String EXCEL_2007U =".xlsx";   
	
	public Boolean exportUser(ServletOutputStream out,List<User> users) {
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();
	        HSSFSheet hssfSheet = workbook.createSheet("sheet1");
	        HSSFRow row = hssfSheet.createRow(0);
	        row.createCell(0).setCellValue("username");
	        row.createCell(1).setCellValue("password");
	        int i = 1;
	        for (User user : users) {
	        	row = hssfSheet.createRow(i);
	        	row.createCell(0).setCellValue(user.getUsername());
	        	row.createCell(1).setCellValue(user.getPassword());
	        	i++;
			}
			try {
				workbook.write(out);
	            out.flush();
	            out.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public List<User> importUser(InputStream in,String fileName) throws IOException {
		String fileType = fileName.substring(fileName.lastIndexOf("."));
		List<User> users = new ArrayList<>();
		Workbook wb = null;
		if(EXCEL_2003L.equals(fileType)){			
			wb = new HSSFWorkbook(in);  //2003-	 	
		}else if(EXCEL_2007U.equals(fileType)){			
			wb = new XSSFWorkbook(in);  //2007+		
		}else{			
			return null;	
		}
		Sheet sheet = wb.getSheetAt(0);
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if(row==null||row.getFirstCellNum()==i){continue;}//TODO what does this line do
			if (row.getLastCellNum() > row.getFirstCellNum() + 1) {//make sure this row has two element
				User user = new User();
				Cell usernameCell = row.getCell(row.getFirstCellNum());
				Cell passwordCell = row.getCell(row.getFirstCellNum()+1);
				user.setUsername(cell2String(usernameCell));
				user.setPassword(cell2String(passwordCell));
//				System.out.println("elements: " + user.getUsername() + " " + user.getPassword());
				users.add(user);
			}

		}
		return users;
	}
	
	private String cell2String(Cell cell) {
		DecimalFormat df = new DecimalFormat("0");
//		DecimalFormat df2 = new DecimalFormat("0.00");
		if (cell.getCellType() == CellType.STRING) {
			return cell.getStringCellValue();
		}else if (cell.getCellType() == CellType.NUMERIC) {
//			if ("General".equals(cell.getCellStyle().getDataFormatString())) {
//				System.out.println("df");
//				return df.format(cell.getNumericCellValue());
//			}else {
//				System.out.println("df2");
//				return df2.format(cell.getNumericCellValue());
//			}
			return df.format(cell.getNumericCellValue());
		}else {
			return null;
		}
	}
}

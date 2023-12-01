package com.hilan.vistr.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hilan.vistr.model.OriginDbMovieDetails;
import com.hilan.vistr.model.VideoDetails;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VideoDataService {

	private static final String MOVIES = "movies";
	private static final Integer ROW_LENGTH = 6;
	private static final String DEFAULT_IMG_PATH = "cinema.jpg";

	@Value("${origindb.filename}")
	private String origindb;

	@Autowired
	private OriginDbService originDbService;
//	public static void main(String[] args) {

	public void createReplicaDBFile() {
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a blank sheet

		XSSFSheet sheet = workbook.createSheet("VideoData");

		// Prepare data to be written as an Object[]

		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		data.put("1", new Object[] { "TITLE", "OVERVIEW", "POPULARITY", "LANGUAGE", "IMGPATH", "VIDEOPATH" });
		data.put("2", new Object[] { "Guru", "Based on the life of Dhirubai Ambani", "70", "TA",
				"J:\\originserverdb\\images\\GURU_2007.jpg", "J:\\originserverdb\\VID2.mp4" });
//		data.put("3", new Object[] { 2, "Lokesh", "Gupta" });
//		data.put("4", new Object[] { 3, "John", "Adwards" });
//		data.put("5", new Object[] { 4, "Brian", "Schultz" });

		// Iterate over data and write to sheet

		Set<String> keyset = data.keySet();
		int rownum = 0;
		for (String key : keyset) {

			Row row = sheet.createRow(rownum++);
			Object[] objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof String)
					cell.setCellValue((String) obj);
				else if (obj instanceof Integer)
					cell.setCellValue((Integer) obj);
			}
		}

		try {
			FileOutputStream out = new FileOutputStream(new File(origindb));
			workbook.write(out);
			out.close();
			System.out.println(MOVIES + " written successfully on disk.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

////	public static void main(String[] args) {
//	public List<VideoDetails> getMoviesList(){
//		try {
////			FileInputStream in = new FileInputStream(new File(origindb));
//			FileInputStream in = new FileInputStream(new File(origindb));
//			Workbook workbook = new XSSFWorkbook(in);
//
//			Sheet sheet = workbook.getSheetAt(0);
//
//			List<OriginDbMovieDetails> dataList = new ArrayList<>();
//			OriginDbMovieDetails data;
//			int i = 0;
//
//			for (Row row : sheet) {
//				data = new OriginDbMovieDetails();
//				// "TITLE", "OVERVIEW", "POPULARITY", "LANGUAGE", "IMGPATH", "VIDEOPATH"
//
//				if (i != 0) {
//					System.out.println(row.getCell(0).getStringCellValue());
//					data.setTitle(row.getCell(0).getStringCellValue());
//					data.setOverview(row.getCell(1).getStringCellValue());
//					data.setPopularity(row.getCell(2).getStringCellValue());
//					data.setLanguage(row.getCell(3).getStringCellValue());
//					data.setImgPath(row.getCell(4).getStringCellValue());
//					data.setVideoPath(row.getCell(5).getStringCellValue());
//				}
////			    for (Cell cell : row) {
////			    	System.out.println(cell.getColumnIndex());
////			        switch (cell.getCellType()) {
////			            case STRING:
////			            	System.out.println(cell.getRichStringCellValue().getString());
////			            	break;
////			        }
////			    }
//				i++;
//			}
//			System.out.println("movies.xlsx written successfully on disk.");
//			workbook.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	public List<VideoDetails> getMovieList() {
		List<VideoDetails> movieList = new ArrayList<>();
		VideoDetails videoDetails;
		List<OriginDbMovieDetails> list = originDbService.getOriginDbList();
		int i = 1;
		for (OriginDbMovieDetails e : list) {
			videoDetails = new VideoDetails();
			videoDetails.setId(String.valueOf(i));
			videoDetails.setTitle(e.getTitle());
			videoDetails.setOverview(e.getOverview());
			videoDetails.setPopularity(e.getPopularity());
			videoDetails.setLanguage(e.getLanguage());
			videoDetails.setImgPath(e.getImgPath());
			i++;
			movieList.add(videoDetails);
		}
		log.info(String.format("Returning Movie List with %d item(s)", movieList.size()));
		return movieList;
	}

	public byte[] getMoviePoster(String path) throws IOException {
		Path p = Paths.get(path);
		if (Files.exists(p)) {
			log.info("sending poster");
			File f1 = new File(path);
			FileInputStream in = new FileInputStream(f1);
			return IOUtils.toByteArray(in);
		} else {
			log.warn("Sending Default poster. No File at path: " + p.toString());
			File f1 = new File(DEFAULT_IMG_PATH);
			FileInputStream in = new FileInputStream(f1);
			return IOUtils.toByteArray(in);
		}
	}

}

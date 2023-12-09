package com.hilan.vistr.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.hilan.vistr.model.OriginDbMovieDetails;
import com.hilan.vistr.model.VideoDetails;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VideoDataService {

	@Value("${instance.id}")
	private String instanceId;

	private List<String> list = new LinkedList<>();
	private static final Integer REPLICASERVER_SIZE = 3;

	@Value("${origindb.filename}")
	private String origindb;

	@Autowired
	private OriginDbService originDbService;
//	public static void main(String[] args) {

	private static final String MOVIES = "movies";
	private static final Integer ROW_LENGTH = 6;
	private static final String DEFAULT_IMG_PATH = "cinema.jpg";
	private static final String IMG_RESOURCE_DIR = "J:\\originserverdb\\images\\";
	private static final String VID_RESOURCE_DIR = "J:\\originserverdb\\";
	private String REPLICA_SERVER_1_DIR = "J:\\replicaserver1db\\";
	private String REPLICA_SERVER_2_DIR = "J:\\replicaserver2db\\";

	public void createReplicaDBFile() {
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a blank sheet

		XSSFSheet sheet = workbook.createSheet("VideoData");

		// Prepare data to be written as an Object[]

		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		data.put("1", new Object[] { "TITLE", "OVERVIEW", "POPULARITY", "LANGUAGE", "IMGPATH", "VIDEOPATH" });
		data.put("2", new Object[] { "Guru", "Based on the life of Dhirubai Ambani", "70", "TA",
				IMG_RESOURCE_DIR + "GURU_2007.jpg", "J:\\originserverdb\\VID2.mp4" });
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
			videoDetails.setVideoname(e.getVideopath());
			videoDetails.setLanguage(e.getLanguage());
			videoDetails.setImgpath(e.getImgpath());
			i++;
			movieList.add(videoDetails);
		}
		log.info(String.format("Returning Movie List with %d item(s)", movieList.size()));
		return movieList;
	}

	public byte[] getMoviePoster(String imgName) throws IOException {
		Path p = Paths.get(IMG_RESOURCE_DIR + imgName);
		if (Files.exists(p)) {
			log.info("sending poster");
			File f1 = new File(p.toString());
			FileInputStream in = new FileInputStream(f1);
			return IOUtils.toByteArray(in);
		} else {
			log.warn("Sending Default poster. No File at path: " + p.toString());
			File f1 = new File(DEFAULT_IMG_PATH);
			FileInputStream in = new FileInputStream(f1);
			return IOUtils.toByteArray(in);
		}
	}

	public InputStreamResource getMovieVideo(String vid) throws Exception {
		System.out.println(instanceId);
		String dirPath;
		switch (instanceId) {
		case "1":
			dirPath = REPLICA_SERVER_1_DIR;
			break;
		case "2":
			dirPath = REPLICA_SERVER_2_DIR;
			break;
		default:
			dirPath = REPLICA_SERVER_1_DIR;

		}
		Path p = Paths.get(dirPath + vid);
		if (Files.exists(p)) {
			System.out.println(p.toString());
			log.info("Video Exists in Replica Server");
			File f1 = new File(p.toString());
			FileInputStream in = new FileInputStream(f1);
			// return IOUtils.toByteArray(in);
			InputStreamResource resource = new InputStreamResource(in);
			return resource;
		} else {
			log.info("Video Unavailable in Replica Server");
			p = Paths.get(VID_RESOURCE_DIR + vid);
			System.out.println(p.toString());

			if (Files.exists(p)) {
				log.info("Video Exists in Origin Server");
				if (list.size() == REPLICASERVER_SIZE)
					list.remove(0);
				list.add(vid);
				// API call
				log.info("Video Added to Replica Server. ");
				log.info(list.size() + "");
				return null;
			}

			else {
				log.info("Invalid video path");
				throw new Exception("Invalid video path");
			}
		}

	}

	public ResponseEntity<StreamingResponseBody> getMovieVideov2(String vid, String rangeHeader) throws Exception {
		String dirPath;
		switch (instanceId) {
		case "1":
			dirPath = REPLICA_SERVER_1_DIR;
			break;
		case "2":
			dirPath = REPLICA_SERVER_2_DIR;
			break;
		default:
			dirPath = REPLICA_SERVER_1_DIR;

		}
		Path p = Paths.get(dirPath + vid);
		if (Files.exists(p)) {
			System.out.println(p.toString());
			log.info("Video Exists in Replica Server");
			File f1 = new File(p.toString());
			FileInputStream in = new FileInputStream(f1);
//            InputStreamResource resource = new InputStreamResource(in);
//            return resource;
			try {
				StreamingResponseBody responseStream;
				String filePathString = p.toString();
				Path filePath = p;
				Long fileSize = Files.size(filePath);
				byte[] buffer = new byte[1024];
				final HttpHeaders responseHeaders = new HttpHeaders();

				if (rangeHeader == null) {
					responseHeaders.add("Content-Type", "video/mp4");
					responseHeaders.add("Content-Length", fileSize.toString());
					responseStream = os -> {
						RandomAccessFile file = new RandomAccessFile(filePathString, "r");
						try (file) {
							long pos = 0;
							file.seek(pos);
							while (pos < fileSize - 1) {
								file.read(buffer);
								os.write(buffer);
								pos += buffer.length;
							}
							os.flush();
						} catch (Exception e) {
						}
					};

					return new ResponseEntity<StreamingResponseBody>(responseStream, responseHeaders, HttpStatus.OK);
				}

				String[] ranges = rangeHeader.split("-");
				Long rangeStart = Long.parseLong(ranges[0].substring(6));
				Long rangeEnd;
				if (ranges.length > 1) {
					rangeEnd = Long.parseLong(ranges[1]);
				} else {
					rangeEnd = fileSize - 1;
				}

				if (fileSize < rangeEnd) {
					rangeEnd = fileSize - 1;
				}

				String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
				responseHeaders.add("Content-Type", "video/mp4");
				responseHeaders.add("Content-Length", contentLength);
				responseHeaders.add("Accept-Ranges", "bytes");
				responseHeaders.add("Content-Range", "bytes" + " " + rangeStart + "-" + rangeEnd + "/" + fileSize);
				final Long _rangeEnd = rangeEnd;
				responseStream = os -> {
					RandomAccessFile file = new RandomAccessFile(filePathString, "r");
					try (file) {
						long pos = rangeStart;
						file.seek(pos);
						while (pos < _rangeEnd) {
							file.read(buffer);
							os.write(buffer);
							pos += buffer.length;
						}
						os.flush();
					} catch (Exception e) {
					}
				};

				return new ResponseEntity<StreamingResponseBody>(responseStream, responseHeaders,
						HttpStatus.PARTIAL_CONTENT);
			} catch (FileNotFoundException e) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			} catch (IOException e) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			log.info("Video Unavailable in Replica Server");
			p = Paths.get(VID_RESOURCE_DIR + vid);
			System.out.println(p.toString());

			if (Files.exists(p)) {
				log.info("Video Exists in Origin Server");
				if (list.size() == REPLICASERVER_SIZE)
					list.remove(0);
				list.add(vid);
				// API call
				log.info("Video Added to Replica Server. ");
				log.info(list.size() + "");
				return null;
			}

			else {
				log.info("Invalid video path");
				throw new Exception("Invalid video path");
			}
		}

	}
}
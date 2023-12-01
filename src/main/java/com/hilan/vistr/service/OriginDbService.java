package com.hilan.vistr.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hilan.vistr.model.OriginDbMovieDetails;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OriginDbService {

	private static final String MOVIES = "movies";
	private static final Integer ROW_LENGTH = 6;

	@Value("${origindb.filename}")
	private String origindb;

	public List<OriginDbMovieDetails> getOriginDbList() {
		List<OriginDbMovieDetails> dataList = new ArrayList<>();

		try {
			File f = new File(origindb);
			if (!f.exists()) {
				log.warn("No such File Exists: " + origindb);
				return new ArrayList<>();
			}
			FileInputStream in = new FileInputStream(f);
			Workbook workbook = new XSSFWorkbook(in);
			Sheet sheet = workbook.getSheetAt(0);
			OriginDbMovieDetails data;
			int i = 0;
			for (Row row : sheet) {
				data = new OriginDbMovieDetails();
				// "TITLE", "OVERVIEW", "POPULARITY", "LANGUAGE", "IMGPATH", "VIDEOPATH"

				if (i != 0) {
					data.setTitle(row.getCell(0).getStringCellValue());
					data.setOverview(row.getCell(1).getStringCellValue());
					data.setPopularity(row.getCell(2).getStringCellValue());
					data.setLanguage(row.getCell(3).getStringCellValue());
					data.setImgPath(row.getCell(4).getStringCellValue());
					data.setVideoPath(row.getCell(5).getStringCellValue());
					dataList.add(data);
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;

	}

}

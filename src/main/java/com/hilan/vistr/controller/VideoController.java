package com.hilan.vistr.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hilan.vistr.model.MovieFilePath;
import com.hilan.vistr.model.VideoDetails;
import com.hilan.vistr.service.VideoDataService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class VideoController {

	@Value("${instance.id}")
	private String instanceId;

	@Autowired
	private VideoDataService videoDataService;

	public List<VideoDetails> getVideoDetails() {

		return new ArrayList<>();
	}

	@GetMapping(path = "/getlist")
	public List<String> getList() {
		System.out.println("------------------ooooooooo----------");
		log.info("New Request to getList() of Video Controller");
		String temp = "Temp response from video controler of server " + instanceId;
		return List.of(temp);
	}

	@GetMapping(path = "/movie/list")
	public List<VideoDetails> getMovieList() {
		log.info("New Request to get all movie list");
		return videoDataService.getMovieList();
	}

	// , produces = { MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG}
	@PostMapping(path = "/movie/img", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
	public @ResponseBody byte[] getMoviePoster(@RequestBody MovieFilePath filePath) throws IOException {
		log.info("New Request to get movie poster from Path: " + filePath.getImgpath());
		return videoDataService.getMoviePoster(filePath.getImgpath());

	}

}

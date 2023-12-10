package com.hilan.vistr.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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

	@GetMapping(path = "/hello")
	public String getGreeting() {
		log.info("New Request ");
		String temp = "Temp response from Video Controller";
		return temp;
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
	@GetMapping(path = "/movie/img", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
	public @ResponseBody byte[] getMoviePoster(@RequestParam String  name) throws IOException {
		log.info("New Request to get movie poster from Path: " + name);
		return videoDataService.getMoviePoster(name);
	}

	@GetMapping(path = "/movie/video", produces = "video/mp4")
	public InputStreamResource getMovieVideo(@RequestParam String  name) throws Exception {
		log.info("New Request to get movie video from Path: " + name);
		return videoDataService.getMovieVideo(name);

	}

	@GetMapping(path = "/movie/video/v2", produces = "video/mp4")
	public ResponseEntity<StreamingResponseBody> getMovieVideov2(@RequestParam String  name, @RequestHeader(value = "Range", required = false)
	   String rangeHeader) throws Exception {
		log.info("New Request to get movie video from Path: " + name);
		return videoDataService.getMovieVideov2(name, rangeHeader);

	}

}

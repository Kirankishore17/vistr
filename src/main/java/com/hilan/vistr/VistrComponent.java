package com.hilan.vistr;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@EnableWebMvc
public class VistrComponent {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
			}
		};
	}

//	@Bean
//	JettyServerCustomizer disableSniHostCheck() {
//		return (server) -> {
//			for (Connector connector : server.getConnectors()) {
//				if (connector instanceof ServerConnector serverConnector) {
//					HttpConnectionFactory connectionFactory = serverConnector
//							.getConnectionFactory(HttpConnectionFactory.class);
//					if (connectionFactory != null) {
//						SecureRequestCustomizer secureRequestCustomizer = connectionFactory.getHttpConfiguration()
//								.getCustomizer(SecureRequestCustomizer.class);
//						if (secureRequestCustomizer != null) {
//							secureRequestCustomizer.setSniHostCheck(false);
//						}
//					}
//				}
//			}
//		};
//	}

}

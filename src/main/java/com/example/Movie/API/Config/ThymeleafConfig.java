package com.example.Movie.API.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;


public class ThymeleafConfig {

//  @Bean
//  @Description("Thymeleaf Template Resolver")
//  public ITemplateResolver templateResolver() {
//    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
//    templateResolver.setPrefix("templates/email/");
//    templateResolver.setSuffix(".html");
//    templateResolver.setTemplateMode(TemplateMode.HTML);
//    templateResolver.setCharacterEncoding("UTF-8");
//    templateResolver.setCacheable(false);
//    return templateResolver;
//  }
//
//  @Bean
//  @Description("Thymeleaf Template Engine")
//  public TemplateEngine templateEngine() {
//    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
//    templateEngine.setTemplateResolver(templateResolver());
//    templateEngine.setEnableSpringELCompiler(true);
//    return templateEngine;
//  }
}
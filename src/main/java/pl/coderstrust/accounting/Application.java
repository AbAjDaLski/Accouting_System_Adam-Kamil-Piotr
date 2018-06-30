package pl.coderstrust.accounting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;


@SpringBootApplication
public class Application {

 private static Logger logger = LoggerFactory.getLogger(Application.class);



  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);



//    try {
//      throw new NullPointerException();
//    }catch (NullPointerException ex){
//      logger.warn("test", ex);
//      throw new RuntimeException(ex);
//    }


  }

}
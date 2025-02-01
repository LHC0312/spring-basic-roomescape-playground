package roomescape.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.Auth.LoginMemberArgumentResolver;
import roomescape.Auth.Interceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Autowired
  private LoginMemberArgumentResolver loginMemberArgumentResolver;

  @Autowired
  private Interceptor interceptor;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(loginMemberArgumentResolver);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(interceptor).addPathPatterns("/admin/**");
  }


}
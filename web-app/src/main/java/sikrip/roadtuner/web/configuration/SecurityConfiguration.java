package sikrip.roadtuner.web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain sessionTokenFilterChain(HttpSecurity http) throws Exception {
		return http.securityMatcher("/**")
			.authorizeHttpRequests(
				(authz) -> authz
					.requestMatchers("/**")
					.permitAll()
			).build();
	}

}

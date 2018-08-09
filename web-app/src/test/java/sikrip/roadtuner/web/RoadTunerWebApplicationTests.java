package sikrip.roadtuner.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RoadTunerWebApplication.class)
@WebAppConfiguration
@TestPropertySource(properties = {
		"spring.config.name = road.tuner.application"})
public class RoadTunerWebApplicationTests {

	@Test
	public void contextLoads() {
	}

}

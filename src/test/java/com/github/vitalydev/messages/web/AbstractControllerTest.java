package com.github.vitalydev.messages.web;

import com.github.vitalydev.messages.model.User;
import com.github.vitalydev.messages.repository.UserRepository;
import com.github.vitalydev.messages.to.AuthenticationRequest;
import com.github.vitalydev.messages.to.AuthenticationResponse;
import com.github.vitalydev.messages.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static com.github.vitalydev.messages.config.WebSecurity.AUTH_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing-spring-boot-applications
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
//https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing-spring-boot-applications-testing-with-mock-environment
public abstract class AbstractControllerTest {
    private static final String TOKEN_TYPE = "Bearer ";

    @Autowired
    public UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    protected ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }

    protected AuthenticationResponse login(AuthenticationRequest authRequest) throws Exception {
        ResultActions actions = mockMvc.perform(post(AUTH_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValue(authRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        return JsonUtil.readValue(actions.andReturn().getResponse().getContentAsString(), AuthenticationResponse.class);
    }

    protected String token(User user) throws Exception {
        AuthenticationResponse authResponse = login(new AuthenticationRequest(user.getEmail(), user.getPassword()));
        return TOKEN_TYPE + " " + authResponse.getToken();
    }
}

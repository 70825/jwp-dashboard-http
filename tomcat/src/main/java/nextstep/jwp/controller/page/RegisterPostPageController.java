package nextstep.jwp.controller.page;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseLine;

public class RegisterPostPageController implements Controller {

    private static final String STATIC = "static";

    private RegisterPostPageController() {
    }

    public static Controller create() {
        return new RegisterPostPageController();
    }

    @Override
    public HttpResponse process(final HttpRequest request) throws IOException {
        final URL url = HttpResponse.class.getClassLoader()
                .getResource(STATIC + "/index" + ".html");
        final Path path = new File(url.getPath()).toPath();

        final String[] splitUserInfo = request.getRequestBody().split("&");
        if (splitUserInfo.length != 3) {
            throw new IllegalArgumentException("아이디, 이메일, 비밀번호가 전부 들어와야 합니다.");
        }

        final String account = splitUserInfo[0].split("=")[1];
        final String password = splitUserInfo[1].split("=")[1];
        final String email = splitUserInfo[2].split("=")[1];
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        final User user = new User(account, email, password);
        InMemoryUserRepository.save(user);

        final byte[] content = Files.readAllBytes(path);

        final HttpHeaders headers = HttpHeaders.createResponse(path);
        headers.setHeader("Location", "/index.html");
        final String responseBody = new String(content);

        return new HttpResponse(ResponseLine.create(HttpStatus.FOUND), headers, responseBody);
    }
}

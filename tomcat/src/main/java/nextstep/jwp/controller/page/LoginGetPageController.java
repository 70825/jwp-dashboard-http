package nextstep.jwp.controller.page;

import static nextstep.jwp.controller.FileContent.HTML;
import static nextstep.jwp.controller.FileContent.INDEX_URI;
import static nextstep.jwp.controller.FileContent.STATIC;
import static org.apache.coyote.http11.common.HttpHeaders.COOKIE_NAME;
import static org.apache.coyote.http11.common.HttpHeaders.LOCATION;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.FileContent;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseLine;

public class LoginGetPageController implements Controller {

    private static final String COMMA_REGEX = "\\.";
    private static final int FILENAME_INDEX = 0;

    private LoginGetPageController() {
    }

    public static Controller create() {
        return new LoginGetPageController();
    }

    @Override
    public HttpResponse process(final HttpRequest request) throws IOException {
        final String uri = request.getUri();
        final String splitUri = uri.split(COMMA_REGEX)[FILENAME_INDEX];
        final String s = STATIC + FileContent.findPage(splitUri) + HTML;
        final URL url = HttpResponse.class.getClassLoader()
                .getResource(STATIC + FileContent.findPage(splitUri) + HTML);

        final Path path = new File(url.getPath()).toPath();

        final byte[] content = Files.readAllBytes(path);

        final HttpHeaders headers = HttpHeaders.createResponse(path);
        final String responseBody = new String(content);

        final Session session = SessionManager.findSession(request.getHeaders().getCookie(COOKIE_NAME));
        if (Objects.isNull(session)) {
            return new HttpResponse(ResponseLine.create(HttpStatus.OK), headers, responseBody);
        }

        final URL indexUrl = HttpResponse.class.getClassLoader()
                .getResource(STATIC + INDEX_URI + HTML);
        final Path loginPath = new File(indexUrl.getPath()).toPath();

        final byte[] loginContent = Files.readAllBytes(loginPath);
        final String loginResponseBody = new String(loginContent);

        headers.setHeader(LOCATION, INDEX_URI + HTML);

        return new HttpResponse(ResponseLine.create(HttpStatus.FOUND), headers, loginResponseBody);
    }
}
package space.swordfish.restore.service.api.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SilverstripeGitFetchImpl implements SilverstripeGitFetch {

    @Value("${silverstripe.dashHost}")
    private static String HOST;

    private final RestTemplate restTemplate;

    @Autowired
    public SilverstripeGitFetchImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<JsonNode> create(String projectId) {
        return restTemplate.exchange(HOST + "/{projectId}/git/fetches", HttpMethod.POST, null, JsonNode.class, projectId);
    }

    @Override
    public ResponseEntity<JsonNode> view(String projectId, String fetchId) {
        return restTemplate.exchange(HOST + "/{projectId}/git/fetches/{fetchId}", HttpMethod.GET, null, JsonNode.class, projectId, fetchId);
    }
}
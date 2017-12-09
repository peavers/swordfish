package space.swordfish.edge.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import space.swordfish.edge.service.domain.Instance;
import space.swordfish.edge.service.service.AuthenticationService;
import space.swordfish.edge.service.service.JsonTransformService;

import java.io.IOException;

@Api(tags = "Instances Command")
@Slf4j
@RestController
public class InstanceCommandGatewayRestController {

    @Value("${queues.instanceEvents}")
    private String queue;

    private final QueueMessagingTemplate queueMessagingTemplate;
    private final JsonTransformService jsonTransformService;
    private final AuthenticationService authenticationService;

    @Autowired
    public InstanceCommandGatewayRestController(QueueMessagingTemplate queueMessagingTemplate, JsonTransformService jsonTransformService, AuthenticationService authenticationService) {
        this.queueMessagingTemplate = queueMessagingTemplate;
        this.jsonTransformService = jsonTransformService;
        this.authenticationService = authenticationService;
    }

    @ApiOperation(value = "Issue a command against a instance.")
    @RequestMapping(value = "/instances", method = RequestMethod.POST)
    public ResponseEntity<String> post(@RequestBody String payload) {
        try {
            String result = java.net.URLDecoder.decode(payload, "UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
            Instance instance = objectMapper.readValue(result, Instance.class);

            instance.setUserToken(authenticationService.getCurrentToken());
            payload = jsonTransformService.write(instance);

            this.queueMessagingTemplate.send(queue, MessageBuilder.withPayload(payload).build());
        } catch (DocumentSerializationException | IOException e) {
            log.error(e.getLocalizedMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Issue a command against a instance.")
    @RequestMapping(value = "/instances/{id}", method = {RequestMethod.PATCH, RequestMethod.DELETE})
    public ResponseEntity<String> patch(@RequestBody String payload, @PathVariable("id") String id) {
        this.queueMessagingTemplate.send(queue, MessageBuilder.withPayload(payload).build());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
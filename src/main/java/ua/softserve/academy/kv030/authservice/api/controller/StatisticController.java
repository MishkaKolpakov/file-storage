package ua.softserve.academy.kv030.authservice.api.controller;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.softserve.academy.kv030.authservice.services.httpclient.HttpClient;


/**
 * Created by Miha on 24.12.2017.
 */
@RestController
public class StatisticController implements StatisticsApi {

    private HttpClient httpClient;
    private Logger logger;

    @Autowired
    public StatisticController(HttpClient httpClient, Logger logger) {
        this.httpClient = httpClient;
        this.logger = logger;
    }

    @Override
    public ResponseEntity<String> statSize(@ApiParam(value = "ID of user to get statistics for", required = true) @PathVariable("userId") String userId) {

        logger.info("Entering statSize endpoint");

        ResponseEntity<String> responseEntity = httpClient.getUploadSizeStatistics(Long.valueOf(userId));

        if (responseEntity.getStatusCode().is2xxSuccessful())
            logger.info("Successfully requested Size of Uploaded Files Statistics");
        else logger.info("Error occurred in Statistics when requesting all Uploaded Files size");

        return responseEntity;
    }

    @RequestMapping(value = "/updownrate/{userId}",
            produces = { "text/plain" },
            method = RequestMethod.GET)
    public ResponseEntity<String> uploadDownloadRate(@ApiParam(value = "ID of user to get statistics for", required = true) @PathVariable("userId") String userId) {

        logger.info("Entering uploadDownloadRate endpoint");

        ResponseEntity<String> responseEntity = httpClient.getUploadDownloadRate(Long.valueOf(userId));

        if (responseEntity.getStatusCode().is2xxSuccessful())
            logger.info("Successfully requested Rate of Upload/Download Statistics");
        else logger.info("Error occurred in Statistics when requesting Upload/Download rate");

        return responseEntity;
    }
}
